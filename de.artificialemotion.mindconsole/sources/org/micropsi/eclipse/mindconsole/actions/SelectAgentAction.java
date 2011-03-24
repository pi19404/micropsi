/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/actions/SelectAgentAction.java,v 1.2 2004/08/10 14:35:49 fuessel Exp $ 
 */
package org.micropsi.eclipse.mindconsole.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import org.micropsi.eclipse.agentmanager.SelectAgentDialog;
import org.micropsi.eclipse.common.model.AgentManager;

public class SelectAgentAction implements IWorkbenchWindowActionDelegate {

	IWorkbenchWindow window;

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		SelectAgentDialog d = new SelectAgentDialog(window.getShell(),"Select the agent to edit:");
		d.setBlockOnOpen(true);
		d.open();
		if(d.getReturnCode() != SelectAgentDialog.OK) return;
		String res = d.getSelected();
		if(res != null) {
			AgentManager.getInstance().changeCurrentAgent(res);
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
