package org.micropsi.eclipse.mindconsole.actions;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.eclipse.common.model.AgentManager;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.mindconsole.dialogs.StateSelectDialog;
import org.micropsi.eclipse.model.net.StateRepositoryModel;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

/**
 * LoadAgentAction
 *
 * @author rv
 */
public class LoadAgentAction implements IViewActionDelegate {

	private Shell shell;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
		this.shell = view.getSite().getShell();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
			
		String currentAgent = AgentManager.getInstance().getCurrentAgent();
		if(currentAgent == null || currentAgent.equals("null")) {
			
			MessageDialog mdlg = new MessageDialog(
				shell,
				"No agent selected",
				null,
				"You have not selected an agent.\n Select an agent via the Agents menu.",				MessageDialog.INFORMATION,
				new String[] {"OK"},
				0
			);
			
			mdlg.open();
			return;			 
		}
			
	
		String statename = null;
		
		StateSelectDialog d = null;
		try {
			StateRepositoryModel model = new StateRepositoryModel(AgentManager.getInstance().getCurrentAgent());
			
			d = new StateSelectDialog(shell, "Select a state to load:", model);
			d.open();
			statename = d.getSelectedState();
			
			if(statename == null) {
				statename = "null";
			}
				
		
		} catch (MicropsiException e) {
			MindPlugin.getDefault().handleException(e);
		}
		
		if(d.getReturnCode() == Dialog.CANCEL) return;
				
		run(statename,false);
	}

	/**
	 * Actually runs the action, loading the given agent state
	 * 
	 * @param agentState the agent state to load
	 */
	public void run(String agentState, boolean blocking) {
		MindPlugin.getDefault().getConsole().sendCommand(
			ConsoleFacadeIF.ZERO_TOLERANCE, 
			1000,
			AgentManager.getInstance().getCurrentAgent(), 
			"loadstate", 
			agentState,
			null,
			blocking
		);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}