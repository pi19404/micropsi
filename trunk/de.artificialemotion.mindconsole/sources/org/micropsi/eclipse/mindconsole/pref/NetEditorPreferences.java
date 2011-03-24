package org.micropsi.eclipse.mindconsole.pref;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.micropsi.eclipse.mindconsole.MindEditController;

/**
 * 
 * 
 * 
 */
public class NetEditorPreferences extends PreferencePage implements IWorkbenchPreferencePage {

	IPreferencePageContainer preferencePageContainer;
	IWorkbench workbench;
	IPreferenceStore store;
	Composite topLevel;
	
	IntegerFieldEditor scalefield;
	FontFieldEditor fontfield;
	BooleanFieldEditor showplusfield;
	BooleanFieldEditor showcompactfield;
	BooleanFieldEditor showarrowfield;
	BooleanFieldEditor showannofield;
	BooleanFieldEditor shownodeactfield;
	BooleanFieldEditor showlinkactfield;
	
	
	public void init(IWorkbench workbench) {
		this.workbench = workbench;
		this.store = PlatformUI.getPreferenceStore();
	}

	public boolean performOk() {
		performApply();
		return true;
	}
	
	public void performApply() {
		scalefield.store();
		fontfield.store();
		showplusfield.store();
		showcompactfield.store();
		showarrowfield.store();	
		shownodeactfield.store();
		showlinkactfield.store();		
		MindEditController.getInstance().triggerRedraw();				
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

		scalefield = new IntegerFieldEditor(INetEditorPrefKeys.SCALE,"Default editor scaling",editorLevel);
		scalefield.setPreferenceStore(store);
		scalefield.setPreferenceName(INetEditorPrefKeys.SCALE);
		scalefield.load();

		fontfield = new FontFieldEditor(INetEditorPrefKeys.FONT, "Font for net entities",editorLevel);
		fontfield.setPreferenceStore(store);
		fontfield.setPreferenceName(INetEditorPrefKeys.FONT);
		fontfield.load();
		
		showplusfield = new BooleanFieldEditor(INetEditorPrefKeys.SHOWPLUSVALUE,"Show numbers of incoming and outgoing links",editorLevel);
		showplusfield.setPreferenceStore(store);
		showplusfield.setPreferenceName(INetEditorPrefKeys.SHOWPLUSVALUE);
		showplusfield.load();

		showcompactfield = new BooleanFieldEditor(INetEditorPrefKeys.SHOWCOMPACTNODES,"Display nodes with compact style",editorLevel);
		showcompactfield.setPreferenceStore(store);
		showcompactfield.setPreferenceName(INetEditorPrefKeys.SHOWCOMPACTNODES);
		showcompactfield.load();

		showarrowfield = new BooleanFieldEditor(INetEditorPrefKeys.SHOWARROWHEADS,"Display arrow heads on links",editorLevel);
		showarrowfield.setPreferenceStore(store);
		showarrowfield.setPreferenceName(INetEditorPrefKeys.SHOWARROWHEADS);
		showarrowfield.load();

		showannofield = new BooleanFieldEditor(INetEditorPrefKeys.SHOWANNOTATIONS,"Display annotations on links, if different from default",editorLevel);
		showannofield.setPreferenceStore(store);
		showannofield.setPreferenceName(INetEditorPrefKeys.SHOWANNOTATIONS);
		showannofield.load();

		shownodeactfield = new BooleanFieldEditor(INetEditorPrefKeys.SHOWENTITYACTIVATION,"Show activation of entities using color",editorLevel);
		shownodeactfield.setPreferenceStore(store);
		shownodeactfield.setPreferenceName(INetEditorPrefKeys.SHOWENTITYACTIVATION);
		shownodeactfield.load();

		showlinkactfield = new BooleanFieldEditor(INetEditorPrefKeys.SHOWLINKACTIVATION,"Show activation of links using color",editorLevel);
		showlinkactfield.setPreferenceStore(store);
		showlinkactfield.setPreferenceName(INetEditorPrefKeys.SHOWLINKACTIVATION);
		showlinkactfield.load();
				
		topLevel.layout();
		return topLevel;
	}

	public void dispose() {
		topLevel.dispose();
	}

	public void loadEverything() {
		scalefield.load();
		showplusfield.load();		
		fontfield.load();
		showcompactfield.load();
		showarrowfield.load();
		showannofield.load();
		shownodeactfield.load();
		showlinkactfield.load();
	}
}
