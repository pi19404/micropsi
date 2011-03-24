/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 16.04.2003
 *
 */
package org.micropsi.eclipse.worldconsole.dialogs;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.micropsi.comp.console.worldconsole.IViewControllerListener;
import org.micropsi.comp.console.worldconsole.WorldMetaDataController;
import org.micropsi.eclipse.worldconsole.WorldConsole;

/**
 * @author matthias
 *
 */
public class CreateObjectDialog extends Dialog implements IViewControllerListener, ModifyListener {
	
	private static int REFRESHBUTTON_ID = IDialogConstants.CLIENT_ID;

	private Text nameText;
	private Combo typeCombo;
	private Text positionText;


	/**
	 * @param parentShell
	 */
	public CreateObjectDialog(Shell parentShell) {
		super(parentShell);
	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		WorldConsole.getInstance().getGlobalData().getWorldMetaData().registerView(this);
		
		Composite composite = (Composite)super.createDialogArea(parent);
		new Label(composite, SWT.NONE).setText("Name");
		nameText = new Text(composite, SWT.BORDER);
		nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nameText.addModifyListener(this);
		new Label(composite, SWT.NONE).setText("Type");
		typeCombo = new Combo(composite, SWT.BORDER | SWT.DROP_DOWN | SWT.READ_ONLY);
		typeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		typeCombo.addModifyListener(this);

		new Label(composite, SWT.NONE).setText("Position");
		positionText = new Text(composite, SWT.BORDER);
		positionText.setText(WorldConsole.getInstance().getGlobalData().getEditSession().getObjectCreatePosition().toString());
		positionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		positionText.addModifyListener(this);
		
		updateTypeCombo(WorldConsole.getInstance().getGlobalData().getWorldMetaData().getObjectTypes());
		return composite;
	}
	
	/**
	 * 
	 */
	protected void queryObjectTypes() {
		WorldConsole.getInstance().getGlobalData().getWorldMetaData().refreshTypes();
	}



	protected void updateTypeCombo(Set objectTypes) {
		if (typeCombo != null && !typeCombo.isDisposed()) {
			typeCombo.removeAll();
			Iterator it = objectTypes.iterator();
			while (it.hasNext()) {
				typeCombo.add((String) it.next());
			}
		}
	}



	/* (non-Javadoc)
	 * @see de.artificialemotion.comp.consoleapp.controller.ViewControllerListenerIF#setDataBase(java.lang.Object)
	 */
	public void setDataBase(Object o) {
	}



	/* (non-Javadoc)
	 * @see de.artificialemotion.comp.consoleapp.controller.ViewControllerListenerIF#setData(java.lang.Object)
	 */
	public void setData(Object o) {
		updateTypeCombo(((WorldMetaDataController) o).getObjectTypes());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		Button refreshButton = createButton(parent, REFRESHBUTTON_ID, "Refresh types", false);
		refreshButton.setToolTipText("asks world for supported object types");
		
		super.createButtonsForButtonBar(parent);

		Button okButton = getButton(IDialogConstants.OK_ID);
		okButton.setEnabled(false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == REFRESHBUTTON_ID) {
			queryObjectTypes();
		} else {
			super.buttonPressed(buttonId);
		}
	}
	
	private String getObjectName() {
		return nameText.getText();
	}

	private String getObjectType() {
		return typeCombo.getText();
	}

	private String getObjectPosition() {
		return positionText.getText();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText(ModifyEvent e) {
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (okButton != null) {
			okButton.setEnabled(!getObjectType().equals("") && !getObjectPosition().equals(""));
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		WorldConsole.getInstance().getGlobalData().getRemoteWorld().createObject(getObjectType(), getObjectPosition(), getObjectName());
		super.okPressed();
	}

}
