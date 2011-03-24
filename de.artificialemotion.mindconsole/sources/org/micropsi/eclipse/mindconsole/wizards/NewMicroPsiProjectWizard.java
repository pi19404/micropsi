/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/wizards/NewMicroPsiProjectWizard.java,v 1.7 2005/08/11 19:27:18 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole.wizards;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.runtime.RuntimePlugin;


public class NewMicroPsiProjectWizard extends JavaProjectWizard {
	
	public NewMicroPsiProjectWizard() {
	}
	
	public boolean performFinish() {
		boolean res=super.performFinish();		
		customize();
		return res;
	}
		    
	private void customize() {
		
		try {			
			JavaCapabilityConfigurationPage page=(JavaCapabilityConfigurationPage)getPage("JavaCapabilityConfigurationPage");
			if (page!=null) {			
				IJavaProject prj=page.getJavaProject();				
				IClasspathEntry[] ocp=prj.getRawClasspath();
				IClasspathEntry[] ncp=new IClasspathEntry[ocp.length+4];
				for(int i=0; i<ocp.length; i++)
					ncp[i]=ocp[i];
							
				Path libdir = new Path("lib"); 
				String runtimeRoot = Platform.asLocalURL(RuntimePlugin.getDefault().find(libdir)).getFile();
				String mindRoot = Platform.asLocalURL(MindPlugin.getDefault().find(libdir)).getFile();
				
				File fp=new File(runtimeRoot,"org.micropsi.common.jar");
				Path cp=new Path(fp.getCanonicalPath());
				
				File fpsrc=new File(runtimeRoot,"org.micropsi.common.src.zip");
				Path cpsrc=new Path(fpsrc.getCanonicalPath());

				IClasspathEntry icp=JavaCore.newLibraryEntry(cp,null,null);
				ncp[ocp.length]=icp;

				fp=new File(mindRoot,"org.micropsi.net.jar");
				cp=new Path(fp.getCanonicalPath());
				fpsrc=new File(mindRoot,"org.micropsi.net.src.zip");
				cpsrc=new Path(fpsrc.getCanonicalPath());

				icp=JavaCore.newLibraryEntry(cp,cpsrc,null);
				ncp[ocp.length+1]=icp;

				fp=new File(mindRoot,"org.micropsi.mindscripting.jar");
				cp=new Path(fp.getAbsolutePath());
				fpsrc=new File(mindRoot,"org.micropsi.mindscripting.src.zip");
				cpsrc=new Path(fpsrc.getCanonicalPath());
				icp=JavaCore.newLibraryEntry(cp,cpsrc,null);
				ncp[ocp.length+2]=icp;
				
				fp=new File(runtimeRoot,"log4j-1.2.8.jar");
				cp=new Path(fp.getAbsolutePath());
				icp=JavaCore.newLibraryEntry(cp,null,null);
				ncp[ocp.length+3]=icp;				
				
				prj.open(null);			

				IFolder folder = prj.getProject().getFolder("bin");
				try {
					folder.create(false,true,null);
				} catch (CoreException e) {}
				prj.setOutputLocation(folder.getFullPath(), null);

				folder = prj.getProject().getFolder("src");
				try {	
					folder.create(false,true,null);
				} catch (CoreException e) {}
				
				IClasspathEntry source = JavaCore.newSourceEntry(folder.getFullPath());				
				ncp[0] = source;

				prj.setRawClasspath(ncp,null);

				String name = prj.getProject().getName();
//				Path path = new Path("org/micropsi/nodenet/modules/"+name);
//				folder = folder.getFolder(path);
//				try {
//					CoreUtility.createFolder(folder, false, true, null);
//				} catch (CoreException e) {}			

				Path path = new Path("org/micropsi/nodenet/scripts/"+name);
				folder = folder.getFolder(path);
				try {
					CoreUtility.createFolder(folder, false, true, null);
				} catch (CoreException e) {}			

			}	
		} catch(Exception e) {
			MindPlugin.getDefault().handleException(e);
		}	
	}
}
