package org.micropsi.eclipse.mindconsole.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.mindconsole.library.LibraryManager;

/**
 * 
 * 
 * 
 */
public class LibEntryAboutDialog extends Dialog {
	
	private String entryName;
	private LibraryManager lib;
	private Text text;
	
	public LibEntryAboutDialog(Shell shell, LibraryManager lib, String entryName) {
		super(shell);
		this.entryName = entryName;
		this.lib = lib;
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		topLevel.setFont(parent.getFont());

		try {

			Label label = new Label(topLevel,SWT.NONE);
			label.setText("Library entry: "+entryName);
	
			text = new Text(topLevel,SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			GridData data = new GridData(GridData.FILL_BOTH);
			data.heightHint = 400;
			data.widthHint = 300;
			text.setLayoutData(data);
		
			text.setText(lib.getElementText(entryName));
		} catch(MicropsiException e) {
			MindPlugin.getDefault().handleException(e);
		}
		
		return topLevel;
	}
	
	protected void okPressed() {

		try {
			lib.setElementText(entryName, text.getText());
		} catch(Exception e) {
			MindPlugin.getDefault().handleException(e);
		}		
			
		super.okPressed();	
	}

}
