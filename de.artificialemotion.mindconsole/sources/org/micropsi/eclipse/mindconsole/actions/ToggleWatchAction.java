package org.micropsi.eclipse.mindconsole.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import org.micropsi.eclipse.mindconsole.MindEditController;

public class ToggleWatchAction implements IWorkbenchWindowActionDelegate {

	public void run(IAction action) {
		action.setChecked(MindEditController.getInstance().toggleWatch());
  	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
     

}
