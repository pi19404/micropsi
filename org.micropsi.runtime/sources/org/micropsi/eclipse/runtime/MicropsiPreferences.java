package org.micropsi.eclipse.runtime;

import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * 
 * 
 */
public class MicropsiPreferences extends PreferencePage implements IWorkbenchPreferencePage {

	IPreferencePageContainer preferencePageContainer;
	IWorkbench workbench;
	IPreferenceStore store;
	Composite topLevel;
		
	public void init(IWorkbench workbench) {
		this.workbench = workbench;
		this.store = PlatformUI.getPreferenceStore();
	}

	public Control createContents(Composite parent) {		
		topLevel = new Composite(parent,parent.getStyle());
		topLevel.setFont(parent.getFont());
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		topLevel.setLayout(layout);

		Composite editorLevel = new Composite(topLevel,SWT.NONE);
		GridData d = new GridData(GridData.FILL_HORIZONTAL);
		d.horizontalSpan = 2;
		editorLevel.setLayoutData(d);
				
		Label label = new Label(topLevel,SWT.NONE);
		label.setText("Micropsi Preferences");

		topLevel.layout();
		return topLevel;
	}

	public void dispose() {
		topLevel.dispose();
	}
	
	public void loadEverything() {
	}

}
