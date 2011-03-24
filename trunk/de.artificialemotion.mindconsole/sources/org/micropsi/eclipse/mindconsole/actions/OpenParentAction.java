package org.micropsi.eclipse.mindconsole.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import org.micropsi.eclipse.mindconsole.MindEditController;

public class OpenParentAction implements IWorkbenchWindowActionDelegate,IViewActionDelegate {

	public void run(IAction action) {
		MindEditController.getInstance().openParentSpace();
  	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {	
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void init(IViewPart view) {
	}
     

}
