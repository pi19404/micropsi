package org.micropsi.eclipse.runtime;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.micropsi.eclipse.common.ClassPathEditor;
import org.micropsi.eclipse.runtime.internal.IRuntimePref;

/**
 * 
 * 
 * 
 */
public class SystemPreferences extends PreferencePage implements IWorkbenchPreferencePage {

	IPreferencePageContainer preferencePageContainer;
	IWorkbench workbench;
	IPreferenceStore store;
	Composite topLevel;
	Composite editorLevel;
	Composite serverLevel;
	
	FileFieldEditor aepconfig;
	ClassPathEditor pathextension;
	StringFieldEditor serverfield;
	StringFieldEditor userfield;
	BooleanFieldEditor exposeserver;
	IntegerFieldEditor serverport;
	
	Button browseButton1, browseButton2;
	String[] MICROPSI_CONF_EXTENSIONS = {"*.xml"};
	
	public void init(IWorkbench workbench) {
		this.workbench = workbench;
		this.store = PlatformUI.getPreferenceStore();
	}

	public boolean isValid() {
		try {
			serverport.getIntValue();
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

	public boolean performOk() {
		performApply();
		return true;
	}
	
	public void performApply() {
		aepconfig.store();
		pathextension.store();
		serverfield.store();
		userfield.store();
		exposeserver.store();
		serverport.store();		
	}

	public Control createContents(Composite parent) {
		topLevel = new Composite(parent,SWT.NONE);
		topLevel.setFont(parent.getFont());
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		topLevel.setLayout(layout);
		
		GridData d = new GridData();
		d.widthHint = 200;
		topLevel.setLayoutData(d);

		editorLevel = new Composite(topLevel,SWT.NONE);
		d = new GridData(GridData.FILL_HORIZONTAL);
		d.horizontalSpan = 2;
		editorLevel.setLayoutData(d);
				
		aepconfig = new FileFieldEditor(IRuntimePref.CFG_KEY_MICROPSICONFIG, "Micropsi runtime config", editorLevel);
		aepconfig.setPreferenceStore(store);
		aepconfig.setPreferenceName(IRuntimePref.CFG_KEY_MICROPSICONFIG);
		aepconfig.load();
		aepconfig.setFileExtensions(MICROPSI_CONF_EXTENSIONS);
		
		serverLevel = new Composite(topLevel,SWT.NONE);
		d = new GridData();
		d.horizontalSpan = 2;
		serverLevel.setLayoutData(d);

		serverfield = new StringFieldEditor(IRuntimePref.CFG_KEY_SERVER,"Server to use",serverLevel);
		serverfield.setPreferenceStore(store);
		serverfield.setPreferenceName(IRuntimePref.CFG_KEY_SERVER);
		serverfield.load();

		userfield = new StringFieldEditor(IRuntimePref.CFG_KEY_USERNAME,"User tag",serverLevel);
		userfield.setPreferenceStore(store);
		userfield.setPreferenceName(IRuntimePref.CFG_KEY_USERNAME);
		userfield.load();

		Composite pathLevel = new Composite(topLevel,SWT.NONE);
		d = new GridData();
		d.horizontalSpan = 2;
		d.widthHint = 400;
		pathLevel.setLayoutData(d);		
		
		pathextension = new ClassPathEditor(IRuntimePref.CFG_KEY_CPEXTENSION,pathLevel);
		pathextension.fillIntoGrid(pathLevel, 2);
		pathextension.setPreferenceStore(store);
		pathextension.load();
		
		exposeserver = new BooleanFieldEditor(IRuntimePref.CFG_KEY_EXPOSESERVER,"Make server available via HTTP",topLevel);
		exposeserver.fillIntoGrid(topLevel, 2);
		exposeserver.setPreferenceStore(store);
		exposeserver.load();

		Composite portLevel = new Composite(topLevel,SWT.NONE);
		d = new GridData();
		d.horizontalSpan = 2;
		portLevel.setLayoutData(d);
		serverport = new IntegerFieldEditor(IRuntimePref.CFG_KEY_SERVERPORT,"Port",portLevel);
		d = new GridData();
		d.widthHint = 100; 
		serverport.getLabelControl(portLevel).setLayoutData(d);
		serverport.setPreferenceStore(store);
		serverport.load();

		Label label = new Label(topLevel,SWT.NONE);
		label.setText(
			"Note: Any change will require a workbench restart to take effect.\n\n\n"+
			"Not all settings make sense with all config files:\n"+
			"- The Server must be a valid server component withing the system\n"+
			"- A server can only be made available via HTTP if it is local and\n" +
			"has type-1 channelservers."
		);

		topLevel.setSize(400, 400);
		return topLevel;
	}

	public void dispose() {
		aepconfig.dispose();
		pathextension.dispose();
		serverfield.dispose();
		userfield.dispose();
		exposeserver.dispose();
		serverport.dispose();
		serverLevel.dispose();
		editorLevel.dispose();
		topLevel.dispose();
	}
	
	public void loadEverything() {
		aepconfig.load();
		pathextension.load();
		serverfield.load();
		userfield.load();
		exposeserver.load();
		serverport.load();	
	}

}
