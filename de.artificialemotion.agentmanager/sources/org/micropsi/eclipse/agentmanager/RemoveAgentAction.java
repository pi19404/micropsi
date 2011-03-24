/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.agentmanager/sources/org/micropsi/eclipse/agentmanager/RemoveAgentAction.java,v 1.2 2004/08/10 14:35:15 fuessel Exp $ 
 */
package org.micropsi.eclipse.agentmanager;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import org.micropsi.comp.common.ComponentRunner;
import org.micropsi.comp.common.ComponentRunnerException;
import org.micropsi.eclipse.common.model.AgentManager;


public class RemoveAgentAction implements IWorkbenchWindowActionDelegate {

	IWorkbenchWindow window;

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		SelectAgentDialog d = new SelectAgentDialog(window.getShell(),"Select the agent to delete:");
		d.setBlockOnOpen(true);
		d.open();
		if(d.getReturnCode() != SelectAgentDialog.OK) return;
		String res = d.getSelected();
		if(res != null) {
			
			AgentManager.getInstance().reportAgentDeletion(res);
			
			try {
				ComponentRunner.getInstance().destroyComponent(res);
			} catch (ComponentRunnerException e) {
				AgentManagerPlugin.getDefault().handleException(e);
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
