package org.micropsi.eclipse.emotion3d;

import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class Emotion3dPreferences extends PreferencePage implements IWorkbenchPreferencePage {

	 IPreferencePageContainer preferencePageContainer;
	 IWorkbench workbench;
	 IPreferenceStore store;
	 
	 private RadioGroupFieldEditor resolution;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		
		Font font = parent.getFont();
		
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		composite.setLayout(layout);
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);
		composite.setFont(font);
				
		Group external = new Group(composite,SWT.NONE);
		external.setText("Standalone viewer options");
		external.setLayoutData(new GridData(GridData.FILL_BOTH));
		    
		resolution = new RadioGroupFieldEditor(
			IEmotion3dPrefKeys.RESOLUTION,
			"Resolution",
			4,
			new String[][] {
				{"1024 x 768", "-res 1024x768"},
				{"800 x 600", "-res 800x600"},
				{"640 x 480", "-res 640x480"},
				{"320 x 240", "-res 320x240"},
				{"160 x 120", "-res 160x120"},
				{"80 x 60", "-res 80x60"}
			},  
			external);
		resolution.setPreferenceStore(store);
		resolution.load();
		
		return composite;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	  this.workbench = workbench;
	  this.store = PlatformUI.getPreferenceStore();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		resolution.store();
		return true;
	}
}
