package org.micropsi.eclipse.media;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

public class VideoServerPreferences extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String CFG_KEY_SERVERLIST = "org.micropsi.media.videoservers";
	
	private IWorkbench workbench;
	private VideoServerListEditor serverList;
	
	public void init(IWorkbench workbench) {
		this.workbench = workbench;
	}

	protected Control createContents(Composite parent) {
		Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setFont(parent.getFont());
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		topLevel.setLayout(layout);
		
		GridData d = new GridData();
		d.widthHint = 200;
		topLevel.setLayoutData(d);

		serverList = new VideoServerListEditor(workbench,CFG_KEY_SERVERLIST,"Video Servers",topLevel);
		serverList.fillIntoGrid(topLevel, 2);
		serverList.setPreferenceStore(PlatformUI.getPreferenceStore());
		serverList.load();
		
		topLevel.setSize(400, 400);
		return topLevel;
	}
	
	public boolean performOk() {
		serverList.store();
		return super.performOk();
	}

}
