/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/jdt/JavaManager.java,v 1.6 2005/07/12 12:53:54 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole.jdt;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.nodenet.LocalNetFacade;


public abstract class JavaManager {
	
	protected ClassLoader parentLoder;
	protected IWorkspaceRoot workspaceRoot;
	protected IJavaModel javaModel;
	protected JDTClassLoader loader;
	protected Shell shell;
	
	protected JavaManager(ClassLoader parent, Shell shell) {
		workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		javaModel = JavaCore.create(workspaceRoot);
		parentLoder = parent;
		reinitialize();
	}
	
	public void reinitialize() {	
		loader = new JDTClassLoader(parentLoder);
		JDTClassLoader initialLoader = new JDTClassLoader(parentLoder);
		LocalNetFacade.setInitialClassLoader(initialLoader);
	}

	public IProject[] getJavaProjects() {
		try {
			IProject[] projects = workspaceRoot.getProjects();
			int javaprojects = 0;
			for(int i=0;i<projects.length;i++)
				if(	projects[i].isOpen() &&
					projects[i].hasNature("org.eclipse.jdt.core.javanature")) javaprojects++; 
			
			IProject[] toReturn = new IProject[javaprojects];
			
			int c = 0;
			for(int i=0;i<projects.length;i++)
				if(	projects[i].isOpen() &&
					projects[i].hasNature("org.eclipse.jdt.core.javanature")) {
					
					toReturn[c] = projects[i];
					c++;					 
				}
			
			return toReturn;
		} catch (CoreException e) {
			MindPlugin.getDefault().handleException(e);
			return null;
		}
	}

	public IType findType(String classname) {
		
		if(classname == null) return null;
		
		try {

			IJavaProject project = null;
			
			IWorkspace ws = ResourcesPlugin.getWorkspace();
			IProject[] projects = ws.getRoot().getProjects();
			if(projects == null) {
				return null;
			}
			
			ArrayList<IJavaProject> javaProjects = new ArrayList<IJavaProject>();
			for(int i=0;i<projects.length;i++) {
				if(	projects[i].isOpen() &&
					projects[i].hasNature("org.eclipse.jdt.core.javanature")) {
					
					IJavaProject p = JavaCore.create(projects[i]);
					IType type = p.findType(classname);
					if(type != null) javaProjects.add(p);
				}
			}
			if(javaProjects.size() == 0) {
				return null;
			}
	
			project = javaProjects.get(0);
	
			if(javaProjects.size() > 1) {
				String warning = "Reference to "+classname+" was ambiguous in JDT workspace. " +
					"This class is contained in the following projects: [";
				
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
				
				warnDialog(warning);
			} 
			
			IType type = project.findType(classname);
			
			return type;
			
		} catch (CoreException e) {
			MindPlugin.getDefault().handleException(e);
			return null;			 
		}

	}	
	
	private void warnDialog(String warning) {
		MessageDialog dlg = new MessageDialog(
			shell,
			"Warning",
			null,
			warning,
			MessageDialog.WARNING,
			new String[] {"OK"},
			0
		);
		dlg.setBlockOnOpen(true);
		dlg.open();
	}
	
	public void openInEditor(String className) {
			
		IType type = findType(className);
		if(type != null) try {
			
			JavaUI.openInEditor(type);
			
		} catch (Exception e) {
			MindPlugin.getDefault().handleException(e);
		}		
	}
	
	public void delete(String className) {
		IType type = findType(className);
		try {
			type.getResource().delete(true, null);
		} catch (CoreException e) {
			MindPlugin.getDefault().handleException(e);
		}

	}
	
	public ClassLoader getJDTClassLoader() {
		return loader;
	}
	
	public ClassLoader getNewJDTClassLoader() {
		return new JDTClassLoader(loader.getParent());
	}

}
