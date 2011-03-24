package org.micropsi.eclipse.mindconsole.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.eclipse.common.model.AgentManager;
import org.micropsi.eclipse.mindconsole.MindPlugin;

/**
 * 
 * 
 * 
 */
public class ChangeDelayAction implements IViewActionDelegate {

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

		InputDialog inputDlg = new InputDialog(
			view.getSite().getShell(),
			"New cycle delay",
			"Please enter a new delay value: ",
			"5",
			new IInputValidator() {
				public String isValid(String newText) {
					try {
						int i = Integer.parseInt(newText);
						if(i < 0) return "Value too small";
					} catch (Exception e) {
						return newText+" not valid: "+e.getMessage();
					}
					return null;
				}
			}
		);
		
		inputDlg.open();
		if(inputDlg.getReturnCode() != InputDialog.OK) return;
				
		MindPlugin.getDefault().getConsole().sendCommand(
			ConsoleFacadeIF.ZERO_TOLERANCE,
			AgentManager.getInstance().getCurrentAgent(),
			"setcyclelength",
			inputDlg.getValue(),
			false
		);
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
