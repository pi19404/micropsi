/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/dialogs/StateInputDialog.java,v 1.2 2004/08/10 14:35:49 fuessel Exp $ 
 */
package org.micropsi.eclipse.mindconsole.dialogs;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;


public class StateInputDialog extends InputDialog {

	public StateInputDialog(Shell parentShell, String initialValue, ArrayList existingStates) {
		super(
			parentShell, 
			"Agent state", 
			"Enter a new state name", 
			initialValue, 
			new StateNameValidator(existingStates)
		);

	}

}
