package org.micropsi.eclipse.runtime.internal;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.micropsi.common.utils.InteractiveClassLoaderIF;


/**
 * 
 * 
 * 
 */
public class InteractiveClassLoader extends URLClassLoader implements InteractiveClassLoaderIF {
		
	private class SelectJarDialog extends TitleAreaDialog {
		
		FileFieldEditor location;
		String selection;
		String classname;
		
		public SelectJarDialog(Shell parentShell, String classname) {
			super(parentShell);
			this.setTitle("Class not found");
			this.classname = classname;
		}
		
		protected Control createDialogArea(Composite parent) {
			Composite topLevel = new Composite(parent,SWT.NONE);
			topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			GridLayout layout = new GridLayout();
			layout.marginWidth = 20;
			layout.marginHeight = 20;
			topLevel.setLayout(layout);
			topLevel.setFont(parent.getFont());
			
			Label label = new Label(topLevel,SWT.NONE);
			label.setText("Select: "+classname);
			
			final Composite filler = new Composite(topLevel, SWT.NONE);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			filler.setLayoutData(data);

			layout = new GridLayout();
			layout.numColumns = 3;
			filler.setLayout(layout);

						
			location = new FileFieldEditor("---","Select jar or class",filler);
			location.setFileExtensions(new String[] {"*.jar","*.class"});
			location.getTextControl(filler).addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					selection = location.getTextControl(filler).getText();
				}
			});
			
			setMessage("Class not found. Select the class or a jar containing the class.");
			
			return topLevel; 
		}
		
		public String getSelection() {
			return selection;
		}
	}
	
	private Shell parentShell;
	private IPreferenceStore store;

	public InteractiveClassLoader(ClassLoader parent, Shell parentShell, IPreferenceStore store) {
		super(new URL[] {}, parent);
		String cpextension = store.getString(IRuntimePref.CFG_KEY_CPEXTENSION);
		StringTokenizer tokener = new StringTokenizer(cpextension,",");
		while(tokener.hasMoreTokens()) {
			String next = tokener.nextToken();
			if(next.equals("")) continue;
			try {
				super.addURL(new File(next).toURL());
			} catch (Exception e) {
				// nix
			}
		}
		
		this.parentShell = parentShell;
		this.store = store;
	}
	
	public String findLibrary(String name) {
		String sup = super.findLibrary(name);
		if(sup != null) {
			return null;
		}
		
		String sep = System.getProperty("path.separator");
		String filesep = System.getProperty("file.separator");
		String[] pathElements = System.getProperty("java.library.path").split(sep);
		if(pathElements == null) {
			pathElements = new String[0];
		}
		
		for(int i=0;i<pathElements.length;i++) {
			File test = new File(pathElements[i] + filesep + System.mapLibraryName(name));	
			if(test.exists()) return test.getAbsolutePath();
		}
		
		System.err.println("Warning: no "+System.mapLibraryName(name)+" in "+System.getProperty("java.library.path"));
		
		return null;
	}

	public URL findResource(String name) {		
		return super.findResource(name);
	}
	
	public Class<?> findClass(String classname) throws ClassNotFoundException {			
		while(true) {
			try {				
				return super.findClass(classname);
			} catch (ClassNotFoundException e) {
				String resName = getResFromDialog(classname);				
				if(resName == null) throw new ClassNotFoundException("You did not select a file containing the class");
				try {
					addURL(new File(resName).toURL());
				} catch (Exception nested) {
					throw new ClassNotFoundException("Unable to load class, could not add url. Reason: "+nested.getMessage(),nested);
				}	
			}
		}			
	}
	
	private String getResFromDialog(final String classname) {
		
		try {
			
			final SelectJarDialog dialog = new SelectJarDialog(parentShell,classname);
			
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					dialog.setBlockOnOpen(true);
					dialog.open();
				}
			});
			
			String ret = dialog.getSelection();
			if(dialog.getReturnCode() == Dialog.CANCEL) return null;
		
			String ext = store.getString(IRuntimePref.CFG_KEY_CPEXTENSION);
			ext += dialog.getSelection()+",";
			store.setValue(IRuntimePref.CFG_KEY_CPEXTENSION, ext);										
		
			return ret;
		} catch (Exception e) {
			return null;
		}
	}

}
