package org.micropsi.eclipse.mindconsole.jdt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;

/**
 * 
 * 
 * 
 */
public class JDTClassLoader extends ClassLoader {
		
	private JDTClassLoader() {
	}
	
	public JDTClassLoader(ClassLoader parent) {
		super(parent);				
	}
			 
	public Class<?> findClass(String classname) throws ClassNotFoundException {
		
		System.err.println("jdt findClass "+classname);
		
		try {

			IJavaProject project = null;
			
			IWorkspace ws = ResourcesPlugin.getWorkspace();
			IProject[] projects = ws.getRoot().getProjects();
			if(projects == null) throw new ClassNotFoundException("No such class or source file.");
			
			ArrayList<IJavaProject> javaProjects = new ArrayList<IJavaProject>();
			for(int i=0;i<projects.length;i++) {
				if( projects[i].isOpen() &&
					projects[i].hasNature("org.eclipse.jdt.core.javanature")) {
					
					IJavaProject p = JavaCore.create(projects[i]);
					
					String primaryType = classname;
					if(classname.indexOf("$") > 0)
						primaryType = classname.substring(0, classname.indexOf("$"));
										
					IType type = p.findType(primaryType);
					if(type != null) javaProjects.add(p); 
				}
			}
			if(javaProjects.size() == 0) throw new ClassNotFoundException("No such class or source file.");
	
			project = javaProjects.get(0);
	
			if(javaProjects.size() > 1) {
				String warning = "Reference to primary type of "+classname+" was ambiguous in JDT workspace. " +
					"This primary type is contained in the following projects: [";
				
				for(int i=0;i<javaProjects.size();i++) {
				 	IJavaProject p = javaProjects.get(i);
				 	warning += " " + p.getProject().getName();
				 	
				 	long stamp = p.getProject().getModificationStamp(); 
				 	if(stamp > project.getProject().getModificationStamp()) {
				 		project = p;
				 	}
				}
				
				warning += " ] The version from the project with the latest modifications was chosen: "+
				project.getProject().getName();
				
				Logger.getRootLogger().warn(warning);
			} 
					
			String primaryType = classname;
			if(classname.indexOf("$") > 0)
				primaryType = classname.substring(0, classname.indexOf("$"));

			IType type = project.findType(primaryType);		

			IClassFile file = type.getClassFile();
			ICompilationUnit unit = type.getCompilationUnit();
			if(file != null) {			
				IPackageFragmentRoot root = (IPackageFragmentRoot) file.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
				if (root != null) {
					if (root instanceof JarPackageFragmentRoot) {
						try {
							IPackageFragmentRoot[] pfrs = project.getPackageFragmentRoots();
							if(pfrs == null) {
								pfrs = new IPackageFragmentRoot[0];
							}
							ArrayList<URL> urls = new ArrayList<URL>();
							for(int i=0;i<pfrs.length;i++) {
								if(pfrs[i] instanceof JarPackageFragmentRoot) {
									if(pfrs[i].getPath().getDevice() == null) {
										File jarFile = new File(pfrs[i].getJavaProject().getProject().getWorkspace().getRoot().getLocation().toFile(),pfrs[i].getPath().toOSString());
										urls.add(jarFile.toURL());
									} else {
										File jarFile = new File(pfrs[i].getPath().toOSString());
										urls.add(jarFile.toURL());
									}
								}
							}
							URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[0]),getParent());
							return loader.loadClass(classname);
						} catch (Exception e) {
							throw new ClassNotFoundException("Jar file loading error",e);
						}
					} else {
						return fileLoad(classname,file.getPath().toOSString());
					}
				} else { 
					throw new ClassNotFoundException("No PackageFragmentRoot");
				}
			} else if(unit != null) {
						
				IPath path = project.getOutputLocation();
				path = path.removeFirstSegments(1);

				String filepath = path.toString();
				filepath = "/" + filepath + "/" + classname.replace('.', '/') + ".class";
				
				filepath = 	project.getProject().getLocation().removeTrailingSeparator().toOSString()+
							filepath;
				  			
				return fileLoad(classname,filepath);
			} else {
				// didn't find anywhere
				throw new ClassNotFoundException("No such class or source file.");
			} 
		} catch (CoreException e) {
			throw new ClassNotFoundException(e.getMessage());
		}
		
	}
	
	private Class fileLoad(String classname, String filepath) throws ClassNotFoundException {		
		try {
			File osfile = new File(filepath);				
			FileInputStream finp = new FileInputStream(osfile);				
			byte[] b = new byte[(int)osfile.length()];				
			finp.read(b);				
			return defineClass(classname, b, 0, b.length);
		} catch (FileNotFoundException e) {
			throw new ClassNotFoundException("Classfile not found: "+e.getMessage());		
		} catch (ClassFormatError e) {
			throw new ClassNotFoundException("Illegal classfile: "+e.getMessage());
		} catch (IOException e) {
			throw new ClassNotFoundException("IO error when loading class: "+e.getMessage());
		}
	}
}
