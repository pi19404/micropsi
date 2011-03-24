package org.micropsi.eclipse.runtime.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.micropsi.eclipse.runtime.SystemPreferences;

public class PreferencesDialog extends Dialog {

	SystemPreferences pref;
	
	PreferencesDialog(Shell shell, IWorkbench workbench) {
		super(shell);
//		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.A );
		pref = new SystemPreferences();
		pref.init(workbench);
	}
	
	protected void okPressed() {
		pref.performOk();
		super.okPressed();
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite)super.createDialogArea(parent);
		Composite topLevel = new Composite(composite,SWT.NONE);
		topLevel.setFont(parent.getFont());
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		pref.createContents(topLevel);
		return topLevel;
	}
	
	public void loadEverything() {
		pref.loadEverything();
	}

}
