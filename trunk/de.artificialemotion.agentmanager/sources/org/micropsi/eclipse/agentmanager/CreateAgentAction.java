/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.agentmanager/sources/org/micropsi/eclipse/agentmanager/CreateAgentAction.java,v 1.2 2004/08/10 14:35:15 fuessel Exp $ 
 */
package org.micropsi.eclipse.agentmanager;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;


public class CreateAgentAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		CreateAgentWizard cawiz = new CreateAgentWizard();
		WizardDialog wizDial = new WizardDialog(window.getShell(),cawiz);
		cawiz.setContainer(wizDial);
		wizDial.open();			
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
