/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.runtime/sources/org/micropsi/eclipse/runtime/dialogs/FatalDialog.java,v 1.4 2005/03/30 17:42:09 vuine Exp $ 
 */
package org.micropsi.eclipse.runtime.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;

public class FatalDialog extends Dialog {

	private String message;
	private IWorkbench workbench;

	public FatalDialog(Shell parentShell, String message, IWorkbench workbench) {
		super(parentShell);
		this.workbench = workbench;
		this.message = message;
		this.setBlockOnOpen(true);
	}
	
	public FatalDialog(Shell parentShell, String message, Throwable e, IWorkbench workbench) {
		super(parentShell);
		this.workbench = workbench;
		this.message = message;
		this.setBlockOnOpen(true);
	}

	protected Control createDialogArea(Composite parent) {
		Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		topLevel.setFont(parent.getFont());

		Label label = new Label(topLevel,SWT.NONE);
		label.setForeground(new Color(parent.getDisplay(),255,0,0));
		label.setText("A fatal error occured. Message:");
				
		Text text = new Text(topLevel,SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 600;
		data.heightHint = 100;
		text.setLayoutData(data);
		text.setText(message);
		text.setEditable(false);
									
		text = new Text(topLevel,SWT.V_SCROLL | SWT.WRAP);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 600;
		data.heightHint = 300;
		text.setLayoutData(data);
		text.setText(
			"A fatal error occured. This means that the Micropsi plugins won't work. \n"+
			"You should now close the workbench, or, if the problem is in the preferences, fix it and restart. \n"+
			"Normally this is due to a communications problem, and the above message or previous exception dialogs "+
			"might have given you a hint about what is going on.\n"+
			"A checklist with typical problems:\n"+
			"- You have lost physical connection\n"+
			"- The server is dead or malconfigured\n"+
			"- You have a bad local aepconfig file with bad client entries\n"+
			"- Your server entry in the Micropsi preferences is bad\n\n"+
			"If you didn't set up the system you want to use, it's perhaps time to ask the one who did.\n"+
			"If you are trying to set up a system and cannot figure out why you got these connection problems, "+
			"feel free to contact us."
		);
		text.setEditable(false);	
		
		return topLevel;
	}
	
	public Control createButtonBar(Composite parent) {
		
		Composite topLevel = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2; 
		topLevel.setLayout(layout);
		topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		topLevel.setFont(parent.getFont());
		
/*		Button closeWorkbench = new Button(topLevel,SWT.PUSH);
		closeWorkbench.setText("Close workbench");
		closeWorkbench.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				Platform.endSplash();
				close();
				workbench.close();
			}			
		});
*/

		Button editPreferences = new Button(topLevel,SWT.PUSH);
		editPreferences.setText("Edit preferences");
		editPreferences.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				close();
				PreferencesDialog dlg = new PreferencesDialog(getShell(), workbench);
				dlg.open();
			}			
		});
				
		Button doNothing = new Button(topLevel,SWT.PUSH);
		doNothing.setText("Ignore the problem");			
		doNothing.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				close();
			}			
		});
				
		return topLevel;
	}
}
