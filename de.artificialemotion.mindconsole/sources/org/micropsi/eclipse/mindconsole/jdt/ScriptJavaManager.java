/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/jdt/ScriptJavaManager.java,v 1.3 2005/07/12 12:53:54 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole.jdt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.widgets.Shell;
import org.micropsi.eclipse.mindconsole.MindPlugin;


public class ScriptJavaManager extends JavaManager {

	private static ScriptJavaManager instance;
	
	public static void ensureInitialization(ClassLoader parent, Shell shell) {
		if(instance == null)
			instance = new ScriptJavaManager(parent,shell);
	}
	
	public static ScriptJavaManager getInstance() {
		if(instance == null) throw new RuntimeException("instance is null");
		return instance;
	}
	
	protected ScriptJavaManager(ClassLoader parent, Shell shell) {
		super(parent,shell);
	}
			
	public List<IType> getScriptImplementations() {
		ArrayList<IType> toReturn = new ArrayList<IType>();
			
		IProject[] projects = getJavaProjects();
		
		try {
			for(int l=0;l<projects.length;l++) {
			
				IJavaProject project = javaModel.getJavaProject(projects[l].getName());
			
				IPackageFragmentRoot[] fragRoots = project.getAllPackageFragmentRoots();
			
				IType scriptImplementationType =
					project.findType("org.micropsi.nodenet.scripting.Script");
				
				if(scriptImplementationType == null) {
					MindPlugin.getDefault().getLogger().error("Check you classpath, org.micropsi.nodenet.scripting.Script not found");
					continue;	
				}
			
				for(int i=0;i<fragRoots.length;i++) {
								
					IJavaElement[] elements = fragRoots[i].getChildren();
					
					for(int j=0;j<elements.length;j++) {
						
						IPackageFragment fragment = (IPackageFragment)elements[j];
					
						if(!fragment.containsJavaResources()) continue;			
						if(!fragment.getElementName().startsWith("org.micropsi")) continue;
					
						ICompilationUnit cunits[] = fragment.getCompilationUnits();
						for(int k=0;k<cunits.length;k++) {
							IType type = cunits[k].findPrimaryType();
							ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
							if(hierarchy.contains(scriptImplementationType)) toReturn.add(cunits[k].getTypes()[0]);						
						}
					
						IClassFile files[] = fragment.getClassFiles();
						for(int k=0;k<files.length;k++) {
							if(!files[k].isClass()) continue;
							if(files[k].getType().isAnonymous()) continue;
							if(files[k].getType().isLocal()) continue;
							if(files[k].getType().isMember()) continue;							
							if(Flags.isAbstract(files[k].getType().getFlags())) continue;
							ITypeHierarchy hierarchy = files[k].getType().newSupertypeHierarchy(null);
							if(hierarchy.contains(scriptImplementationType)) toReturn.add(files[k].getType());						
						}	
					}
				}
			}			
		} catch (JavaModelException e) {
			MindPlugin.getDefault().getLogger().error("Model exception",e);
			return toReturn;
		}
		
		
		return toReturn;
	}

}
