/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 16.04.2003
 *
 */
package org.micropsi.eclipse.worldconsole.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.micropsi.eclipse.worldconsole.QuestionErrorHandler;
import org.micropsi.eclipse.worldconsole.WorldConsole;
import org.micropsi.eclipse.worldconsole.WorldPlugin;

/**
 * @author matthias
 *
 */
public class SaveWorldDialog extends Dialog implements ModifyListener {
	
	private Text nameText;


	/**
	 * @param parentShell
	 */
	public SaveWorldDialog(Shell parentShell) {
		super(parentShell);
	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		new Label(composite, SWT.NONE).setText("Filename");
		nameText = new Text(composite, SWT.BORDER);
		nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nameText.addModifyListener(this);
		nameText.setText(WorldConsole.getInstance().getGlobalData().getLocalWorld().getWorldFileName());
		nameText.selectAll();

		return composite;
	}
	
	private String getFileName() {
		return nameText.getText();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText(ModifyEvent e) {
		updateOKButton();
		
	}

	/**
	 * 
	 */
	private void updateOKButton() {
		Button okButton = getButton(IDialogConstants.OK_ID);
		if (okButton != null) {
			okButton.setEnabled(!getFileName().equals(""));
		}
	}



	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	protected void okPressed() {
		WorldPlugin.getDefault().getConsole().getInformation(
			0,
			100,
			"world",
			"saveworld",
			getFileName(),
			QuestionErrorHandler.getInstance().getAnswerQueue(),
			null,
			true);
		
		super.okPressed();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		updateOKButton();
	}
}
