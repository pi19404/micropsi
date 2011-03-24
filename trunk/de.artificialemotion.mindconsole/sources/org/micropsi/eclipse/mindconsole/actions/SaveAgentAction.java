package org.micropsi.eclipse.mindconsole.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.eclipse.common.model.AgentManager;
import org.micropsi.eclipse.console.adminperspective.ParameterController;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.mindconsole.dialogs.StateInputDialog;
import org.micropsi.eclipse.mindconsole.dialogs.StateSelectDialog;
import org.micropsi.eclipse.model.net.AgentNetModelManager;
import org.micropsi.eclipse.model.net.StateRepositoryModel;

/**
 * SaveAgentAction
 *
 * @author rv
 */
public class SaveAgentAction implements IViewActionDelegate {

	private IViewPart view;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		this.view = view;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		
		String currentAgent = AgentManager.getInstance().getCurrentAgent();
		if(currentAgent == null || currentAgent.equals("null")) {
			
			MessageDialog mdlg = new MessageDialog(
				view.getSite().getShell(),
				"No agent selected",
				null,
				"You have not selected an agent.\n Select an agent via the Agents menu.",
				MessageDialog.INFORMATION,
				new String[] {"OK"},
				0
			);
			
			mdlg.open();
			return;			 
		}

		
		String statename = null;
		
		StateSelectDialog d;
		try {
			StateRepositoryModel model = new StateRepositoryModel(AgentManager.getInstance().getCurrentAgent());
			
			d = new StateSelectDialog(view.getSite().getShell(), "Select a state for saving:", model);
			d.open();
			if(d.getReturnCode() == StateSelectDialog.CANCEL) return;
			statename = d.getSelectedState();
			
			if(statename == null) {
				StateInputDialog i = new StateInputDialog(
					view.getSite().getShell(),
					"noname",
					model.getList()
				);
				i.open();
				statename = i.getValue();
			}
			
			run(statename,false);
			
		} catch (Exception exc) {
			MindPlugin.getDefault().handleException(exc);
		}
	}
	
	/**
	 * Actually runs the action, saving the agent state with
	 * the given name.
	 * 
	 * @param stateName tha agent state's name, must not be null
	 */
	public void run(String stateName, boolean blocking) {
		
	    if(stateName == null) throw new IllegalArgumentException("stateName must not be null");
	    
	    try {
	        AgentNetModelManager.getInstance().getNetModel().saveModels(stateName);
		
	        MindPlugin.getDefault().getConsole().sendCommand(
		        ConsoleFacadeIF.ZERO_TOLERANCE,
		        AgentManager.getInstance().getCurrentAgent(),
		        "savestate",
		        stateName,
				blocking);
			
			ParameterController.getInstance().saveState(stateName);
	    
		} catch (Exception exc) {
		    MindPlugin.getDefault().handleException(exc);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	    
	}

}