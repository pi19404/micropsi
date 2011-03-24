package org.micropsi.eclipse.mindconsole.actions;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.common.model.AgentManager;
import org.micropsi.eclipse.mindconsole.MindEditView;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.mindconsole.wizards.NewNetEntityWizard;
import org.micropsi.eclipse.model.net.AgentNetModelManager;
import org.micropsi.nodenet.NetEntityTypesIF;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * 
 * 
 * 
 */
public class CreateNewEntityAction implements IViewActionDelegate {

	private IViewPart view;

	/**
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		this.view = view;
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		String currentAgent = AgentManager.getInstance().getCurrentAgent();
		if(currentAgent == null || currentAgent.equals("null")) {
			
			MessageDialog mdlg = new MessageDialog(
				view.getSite().getShell(),
				"No agent selected",
				null,
				"You have not selected an agent.\n Select an agent via the Agents menu.",				MessageDialog.INFORMATION,
				new String[] {"OK"},
				0
			);
			
			mdlg.open();
			return;			 
		}

		
		
		try {
			NewNetEntityWizard wiz =
				new NewNetEntityWizard(
					AgentNetModelManager.getInstance().getNetModel(),
					((MindEditView) view).getCurrentNodeSpaceID(),
					NetEntityTypesIF.ET_MODULE_NATIVE);
			WizardDialog wizDial = new WizardDialog(view.getSite().getShell(),wiz);
			wiz.setContainer(wizDial);
			wizDial.open();

		} catch (MicropsiException e) {
			MindPlugin.getDefault().handleException(e);
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
