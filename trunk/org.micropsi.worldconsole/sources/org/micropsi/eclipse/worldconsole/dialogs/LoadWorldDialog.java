/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 16.04.2003
 *
 */
package org.micropsi.eclipse.worldconsole.dialogs;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.micropsi.comp.console.worldconsole.IViewControllerListener;
import org.micropsi.comp.console.worldconsole.WorldMetaDataController;
import org.micropsi.eclipse.worldconsole.QuestionErrorHandler;
import org.micropsi.eclipse.worldconsole.WorldConsole;
import org.micropsi.eclipse.worldconsole.WorldPlugin;

/**
 * @author matthias
 *
 */
public class LoadWorldDialog extends Dialog implements IViewControllerListener, SelectionListener {
	
	private static int REFRESHBUTTON_ID = IDialogConstants.CLIENT_ID;

	private List fileList = null;

	private Button okButton = null;


	/**
	 * @param parentShell
	 */
	public LoadWorldDialog(Shell parentShell) {
		super(parentShell);
	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		WorldConsole.getInstance().getGlobalData().getWorldMetaData().registerView(this);
		
		Composite composite = (Composite) super.createDialogArea(parent);
		new Label(composite, SWT.NONE).setText("Select world file to load");
		fileList = new List(composite, SWT.SINGLE | SWT.V_SCROLL);
		fileList.setLayoutData(new GridData(GridData.FILL_BOTH));
		fileList.addSelectionListener(this);
		
		updateFileList(WorldConsole.getInstance().getGlobalData().getWorldMetaData().getWorldFileNames());
		queryWorldFileNames();
		return composite;
	}
	
	/**
	 * 
	 */
	protected void queryWorldFileNames() {
		WorldConsole.getInstance().getGlobalData().getWorldMetaData().refreshWorldFileNames();
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
		updateFileList(((WorldMetaDataController) o).getWorldFileNames());
	}

	/**
	 * @param set
	 */
	private void updateFileList(Collection fileNames) {
		if (fileNames != null && fileList != null && !fileList.isDisposed()) {
			fileList.removeAll();
			Iterator it = fileNames.iterator();
			while (it.hasNext()) {
				fileList.add((String) it.next());
			}
			
		}
	}



	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		Button refreshButton = createButton(parent, REFRESHBUTTON_ID, "Refresh filenames", false);
		refreshButton.setToolTipText("asks world for existing world files");
		
		super.createButtonsForButtonBar(parent);

		okButton = getButton(IDialogConstants.OK_ID);
		okButton.setEnabled(false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == REFRESHBUTTON_ID) {
			queryWorldFileNames();
		} else {
			super.buttonPressed(buttonId);
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
			"loadworld",
			getSelectedFileName(),
			QuestionErrorHandler.getInstance().getAnswerQueue(),
			null,
			true);
		
		super.okPressed();
	}



	/**
	 * @return
	 */
	private String getSelectedFileName() {
		return fileList.getSelection()[0];
	}



	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
		okButton.setEnabled(true);
	}



	/* (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
		okButton.setEnabled(true);
		okPressed();
	}

}
