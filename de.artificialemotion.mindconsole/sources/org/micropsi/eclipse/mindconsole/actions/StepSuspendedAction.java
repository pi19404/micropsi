package org.micropsi.eclipse.mindconsole.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.eclipse.common.model.AgentManager;
import org.micropsi.eclipse.mindconsole.MindPlugin;

/**
 * @author daniel
 *
 */
public class StepSuspendedAction implements IWorkbenchWindowActionDelegate {

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run(IAction action) {
		run(1,false);
  	}
	
	public void run(long steps, boolean blocking) {
		if(AgentManager.getInstance().getCurrentAgent() == null) return;
		
		MindPlugin.getDefault().getConsole().sendCommand(
			ConsoleFacadeIF.ZERO_TOLERANCE,
			AgentManager.getInstance().getCurrentAgent(),
			"cyclesuspendednet",
			Long.toString(steps),
			blocking
		);		
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}
}
