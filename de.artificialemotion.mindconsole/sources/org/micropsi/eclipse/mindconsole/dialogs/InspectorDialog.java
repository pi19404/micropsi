package org.micropsi.eclipse.mindconsole.dialogs;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.mindconsole.groups.InspectorGroup;
import org.micropsi.eclipse.model.net.EntityModel;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * 
 * 
 */
public class InspectorDialog extends Dialog {
	
	private EntityModel model;
	
	public InspectorDialog(Shell shell, EntityModel model) throws MicropsiException {
		super(shell);
		this.model = model;
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, OK, "OK", true);
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		topLevel.setFont(parent.getFont());
		
		GridData data = new GridData();
		new InspectorGroup(topLevel, data, model);
		
		return topLevel;
	}
	
	protected void okPressed() {
		super.okPressed();	
	}

}
