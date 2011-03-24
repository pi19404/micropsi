/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/dialogs/SuspendMessageDialog.java,v 1.1 2005/01/02 20:55:19 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole.dialogs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;


public class SuspendMessageDialog extends MessageDialog {

	private boolean suspend = false;

	public SuspendMessageDialog(Shell arg0, String arg1, Image arg2, String arg3, int arg4, String[] arg5, int arg6) {
		super(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
	}

	public Control createButtonBar(Composite parent) {
		Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		topLevel.setFont(parent.getFont());
		final Button s = new Button(topLevel,SWT.CHECK);
		s.setText("Suspend net after this dialog has been closed");
		s.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				suspend = s.getSelection();					
			}				
		});
		super.createButtonBar(topLevel);
		return topLevel;
	}
	
	public boolean isSuspend() {
		return suspend;
	}
	
}
