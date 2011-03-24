package org.micropsi.eclipse.console.adminperspective;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class ToggleWatchAction implements IViewActionDelegate {

	public void run(IAction action) {
		action.setChecked(LogController.getInstance().toggleWatch());
  	}

	public void dispose() {
	}


	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void init(IViewPart view) {
	}
     

}
