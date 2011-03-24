package org.micropsi.eclipse.whiskerbotcontrol;

import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * 
 * 
 */
public class RobotPreferences implements IWorkbenchPreferencePage {

	public static final String CFG_KEY_ROBOTNAME = "org.micropsi.whiskerbotcontrol.robotname";
	public static final String CFG_KEY_AGENTNAME = "org.micropsi.whiskerbotcontrol.agentname";
	
	IPreferencePageContainer preferencePageContainer;
	IWorkbench workbench;
	IPreferenceStore store;
	Composite topLevel;
	
	StringFieldEditor robotfield;
	StringFieldEditor agentfield;
	
	private String error;
	private String message;
	private String title = "Khepera Robot";
	
	public void init(IWorkbench workbench) {
		this.workbench = workbench;
		this.store = PlatformUI.getPreferenceStore();
	}

	public Point computeSize() {
		if(topLevel != null)
			return topLevel.getSize();
		else return new Point(100,100);
	}

	public boolean isValid() {
		return true;
	}

	public boolean okToLeave() {
		return true;
	}

	public boolean performCancel() {		
		return true;
	}

	public boolean performOk() {	
		robotfield.store();
		agentfield.store();
		return true;
	}

	public void setContainer(IPreferencePageContainer preferencePageContainer) {
		this.preferencePageContainer = preferencePageContainer;
	}

	public void setSize(Point size) {
		topLevel.setSize(size);
	}

	public void createControl(Composite parent) {
		topLevel = new Composite(parent,SWT.NONE);
		topLevel.setFont(parent.getFont());
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		topLevel.setLayout(layout);
		
		GridData d = new GridData();
		d.widthHint = 200;
		topLevel.setLayoutData(d);

		robotfield = new StringFieldEditor(CFG_KEY_ROBOTNAME,"Robot name",topLevel);
		robotfield.setPreferenceStore(store);
		robotfield.setPreferenceName(RobotPreferences.CFG_KEY_ROBOTNAME);
		robotfield.load();

		agentfield = new StringFieldEditor(CFG_KEY_AGENTNAME,"Agent name",topLevel);
		agentfield.setPreferenceStore(store);
		agentfield.setPreferenceName(RobotPreferences.CFG_KEY_AGENTNAME);
		agentfield.load();
		
		topLevel.setSize(400, 400);
		
	}

	public void dispose() {
		robotfield.dispose();
		agentfield.dispose();
		topLevel.dispose();
	}

	public Control getControl() {
		return topLevel;
	}

	public String getDescription() {
		return "Micropsi runtime system preferences";
	}

	public String getErrorMessage() {
		return error;
	}

	public Image getImage() {
		return null;
	}

	public String getMessage() {
		return message;
	}

	public String getTitle() {
		return title;
	}

	public void performHelp() {
	}

	public void setDescription(String description) {
	}

	public void setImageDescriptor(ImageDescriptor image) {
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setVisible(boolean visible) {
	}

	public Control getContents(Composite parent) {
		createControl(parent);
		return topLevel;
	}
	
	public void loadEverything() {
		robotfield.load();	
		agentfield.load();
	}

}
