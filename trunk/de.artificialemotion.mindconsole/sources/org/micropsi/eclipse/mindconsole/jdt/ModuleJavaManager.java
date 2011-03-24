package org.micropsi.eclipse.mindconsole.jdt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.micropsi.common.progress.ProgressMonitorIF;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.mindconsole.dialogs.InjectDialog;
import org.micropsi.eclipse.mindconsole.wizards.NewMicroPsiProjectWizard;
import org.micropsi.nodenet.NativeModule;


/**
 * 
 * 
 * 
 */
public class ModuleJavaManager extends JavaManager {

	private static ModuleJavaManager instance;
	
	public static void ensureInitialization(ClassLoader parent, Shell shell) {
		if(instance == null)
			instance = new ModuleJavaManager(parent,shell);
	}
	
	public static ModuleJavaManager getInstance() {
		if(instance == null) throw new RuntimeException("instance is null");
		return instance;
	}
	
	private AutoUpdateListener autoUpdates;
	
	public ModuleJavaManager(ClassLoader parent,Shell shell) {
		super(parent,shell);
	}
	
	public void reinitialize() {
		autoUpdates = null;
		super.reinitialize();
	}
		
	public List<IType> getModuleImplementationsInProject(String projectname, IProgressMonitor monitor) {
		ArrayList<IType> toReturn = new ArrayList<IType>();
			
		if(projectname == null) return toReturn;
		if(projectname.equals("")) return toReturn;
						
		try {
			IJavaProject project = javaModel.getJavaProject(projectname);
			
			IPackageFragmentRoot[] fragRoots = project.getAllPackageFragmentRoots();
			
			if(monitor != null)
				monitor.beginTask("Searching - ", fragRoots.length);
				
			IType moduleImplementationType =
				project.findType("org.micropsi.nodenet.AbstractNativeModuleImpl");
			
			if(moduleImplementationType == null) {
				monitor.setTaskName("AbstractNativeModuleImpl not found - check your classpaths!");
				monitor.setCanceled(true);
				return toReturn;			
			}
			
			for(int i=0;i<fragRoots.length;i++) {
								
				IJavaElement[] elements = fragRoots[i].getChildren();
				for(int j=0;j<elements.length;j++) {
					IPackageFragment fragment = (IPackageFragment)elements[j];
					
					if(!fragment.containsJavaResources()) continue;			
					if(!fragment.getElementName().startsWith("org.micropsi")) continue;
					
					ICompilationUnit cunits[] = fragment.getCompilationUnits();
					for(int k=0;k<cunits.length;k++) {
						monitor.subTask("Checking "+cunits[k].getElementName());
						IType type = cunits[k].findPrimaryType();
						ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
						if(hierarchy.contains(moduleImplementationType)) toReturn.add(cunits[k].getTypes()[0]);						
					}
					
					IClassFile files[] = fragment.getClassFiles();
					for(int k=0;k<files.length;k++) {
						if(!files[k].isClass()) continue;
						if(files[k].getType().isAnonymous()) continue;
						if(files[k].getType().isLocal()) continue;
						if(files[k].getType().isMember()) continue;
						if(Flags.isAbstract(files[k].getType().getFlags())) continue;
						monitor.subTask("Checking "+files[k].getElementName());
						ITypeHierarchy hierarchy = files[k].getType().newSupertypeHierarchy(null);
						if(hierarchy.contains(moduleImplementationType)) toReturn.add(files[k].getType());						
					}
					
				}
				if(monitor != null) monitor.worked(1);
			}
			
			if(monitor != null) {
				monitor.subTask("");
				monitor.setTaskName("");
				monitor.done();
			}
			 
			
		} catch (JavaModelException e) {
			return toReturn;
		}
		
		
		return toReturn;
	}

	private void ensureRegistration() {
		if(autoUpdates == null) {
			autoUpdates = new AutoUpdateListener(shell);
			ResourcesPlugin.getWorkspace().addResourceChangeListener(
				autoUpdates,
				IResourceChangeEvent.POST_BUILD				
			);
		}
	}
	
	public void enableAutoReplacement(NativeModule m) {
		ensureRegistration();
		
		if(!autoUpdates.isObserved(m.getID()) && m.getImplementationClassName() != null) {
			IType type = findType(m.getImplementationClassName());
			if(type != null) {
				autoUpdates.addObserved(
					m.getID(), 
					m.getImplementationClassName(), 
					type.getPath()
				);
			}
		}
	}
	
	public void disableAutoReplacement(String moduleID) {
		ensureRegistration();
		
		autoUpdates.removeObserved(moduleID);
	}
	
	public void disableAutoReplacementForAll() {
		ensureRegistration();
		
		autoUpdates.removeAllObserved();
	}

		
	public void injectSourceFiles(Shell shell, HashMap files, ProgressMonitorIF monitor) {
		
		ArrayList<String> toOverwrite = new ArrayList<String>();
		ArrayList<String> toInject = new ArrayList<String>();
		
		Iterator iter = files.keySet().iterator();
		while(iter.hasNext()) {
			String name = (String)iter.next();
			IType type = findType(name);
			if(type != null) 
				toOverwrite.add(name);
			else
				toInject.add(name);
		}
		
		if(toOverwrite.size() > 0) {
			ListSelectionDialog dlg = new ListSelectionDialog(
				shell,
				toOverwrite,
				new IStructuredContentProvider() {
					public Object[] getElements(Object alternatives) {		
						return ((ArrayList)alternatives).toArray();
					}
					public void dispose() {
					}
					public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
					}		
				},
				new ILabelProvider() {

					public Image getImage(Object element) {
						return null;
					}

					public String getText(Object element) {
						return (String)element;
					}

					public void addListener(ILabelProviderListener listener) {
					}

					public void dispose() {
					}

					public boolean isLabelProperty(Object element, String property) {
						return false;
					}

					public void removeListener(ILabelProviderListener listener) {
					}
					
				},
				"These modules' sources conflict with sources in your workspace\n"+
				"If slots or gates differ, you're about to lose links. \n"+
				"Please select the workspace resources to be replaced!"
			);
			
			dlg.setTitle("Possible conflict");
			dlg.open();

			if(monitor != null) monitor.beginTask("Replacing...");
			
			Object[] result = dlg.getResult();
			if(result == null) result = new Object[0];
			for(int i=0;i<result.length;i++) {
				if(monitor != null) monitor.reportProgress(i,result.length,"Replacing files...");

				IType type = findType((String)result[i]);
				File f = type.getResource().getLocation().toFile();
				try {
					FileWriter fw = new FileWriter(f);
					fw.write((String)files.get(result[i]));
					fw.close();
					type.getResource().refreshLocal(-1, null);
				} catch (Exception e) {
					toInject.remove(result[i]);
					MindPlugin.getDefault().handleException(e);
				}
			}
			
			if(monitor != null) monitor.endTask();			
		}
		
		if(toInject.size() > 0) {
			InjectDialog dlg = new InjectDialog(shell);
			dlg.open();	
			String projectName = dlg.getSelected();
			
			while(projectName == null) {
				NewMicroPsiProjectWizard wiz = new NewMicroPsiProjectWizard();
				WizardDialog wizdial = new WizardDialog(shell,wiz);
				wiz.init(MindPlugin.getDefault().getWorkbench(), null);
				wizdial.open();

				dlg = new InjectDialog(shell);
				dlg.open();	
				projectName = dlg.getSelected();
				
			//	NewElementWizard sepp;
				
			}
			
			try {
				if(monitor != null) monitor.beginTask("Creating...");
				IJavaProject project = javaModel.getJavaProject(projectName);
				IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
				IPackageFragmentRoot sourceFragmentRoot = null;
				for(int i=0;i<roots.length;i++) {
					if(roots[i].getKind() == IPackageFragmentRoot.K_SOURCE) {
						sourceFragmentRoot = roots[i];
						break;
					}
				}
				if(sourceFragmentRoot == null) {
					//@todo: Ronnie: check if this happens, if it does, handle
					System.err.println("no package fragment root");
				}
				
				for(int i=0;i<toInject.size();i++) {
					if(monitor != null) monitor.reportProgress(i,toInject.size(),"Creating files...");
					
					String classname = toInject.get(i);
					String packagename = classname.substring(0,classname.lastIndexOf("."));
					String cuname = classname.substring(classname.lastIndexOf(".")+1)+".java";				
					IPackageFragment p = sourceFragmentRoot.createPackageFragment(packagename, true, null);
					ICompilationUnit c = p.createCompilationUnit(cuname, (String)files.get(classname), true, null);
					c.save(null, false);				
				}
			} catch (Exception e) {
				MindPlugin.getDefault().handleException(e);
			}
			
			if(monitor != null) monitor.endTask();
		}

	}
	
}
