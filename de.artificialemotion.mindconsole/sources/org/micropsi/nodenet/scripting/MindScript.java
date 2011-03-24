/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/nodenet/scripting/MindScript.java,v 1.6 2005/05/22 23:57:20 vuine Exp $ 
 */
package org.micropsi.nodenet.scripting;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.eclipse.console.adminperspective.ParameterController;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.mindconsole.actions.LoadAgentAction;
import org.micropsi.eclipse.mindconsole.actions.RunNetAction;
import org.micropsi.eclipse.mindconsole.actions.SaveAgentAction;
import org.micropsi.eclipse.mindconsole.actions.StepSuspendedAction;
import org.micropsi.eclipse.mindconsole.actions.SuspendNetAction;
import org.micropsi.eclipse.mindconsole.dialogs.SuspendMessageDialog;
import org.micropsi.eclipse.model.net.AgentNetModelManager;
import org.micropsi.eclipse.model.net.EntityModel;
import org.micropsi.eclipse.runtime.RuntimePlugin;
import org.micropsi.nodenet.NetFacadeIF;



public abstract class MindScript extends Script {
	
	private NetFacadeIF net;
	private Display display;
	
	void setup(NetFacadeIF net) {
		this.net = net;
		this.display = MindPlugin.getDefault().getWorkbench().getDisplay();
	}

	/**
	 * Returns the current net.
	 * @return the current net
	 */
	public NetFacadeIF getNet() {
		return net;
	}

	/**
	 * Suspends the net.
	 */
	public void suspendNet() {
		SuspendNetAction action = new SuspendNetAction();
		action.run(true);
	}
	
	/**
	 * Steps a suspended net forward.
	 * @param steps the number of steps to proceed
	 */
	public void stepNet(long steps) {
		StepSuspendedAction action = new StepSuspendedAction();
		action.run(steps,true);
	}
	
	/**
	 * Resumes a suspended net.
	 */
	public void resumeNet() {
		RunNetAction action = new RunNetAction();
		action.run(true);
	}
	
	/**
	 * Loads an existing agent state. If you pass null or "null",
	 * an empty state will be created. 
	 * 
	 * @param stateName
	 */
	public void loadNet(String stateName) {
	    if(stateName == null) {
	        stateName = "null";
	    }
	    LoadAgentAction action = new LoadAgentAction();
	    action.run(stateName,true);
		
	}
	
	/**
	 * Saves the current state.
	 * 
	 * @param stateName the state name, must not be null
	 * @throws IllegalArgumentException if the stateName is null
	 */
	public void saveNet(String stateName) {
		SaveAgentAction action = new SaveAgentAction();
		action.run(stateName,true);
	}
	
	/**
	 * Resets the parameter view.
	 */
	public void clearParameterView() {
		display.syncExec(new Runnable() {
			public void run() {
				ParameterController.getInstance().clear();
			}
		});
	}
	
	/**
	 * Sens a "command" to a component.
	 * (In terms of the MRS question/answer system, a command is a question
	 * that is not expected to be answered, just understood and executed).
	 * 
	 * @param component The component to receive the question
	 * @param command the question to pose
	 * @param parameters additional parameters
	 */
	public void sendCommand(String component, String command, String parameters) {
		MindPlugin.getDefault().getConsole().sendCommand(
				ConsoleFacadeIF.ZERO_TOLERANCE,
				component, 
				command, 
				parameters, 
				true
		);
	}

	/**
	 * Sets the position of the given node.
	 * 
	 * @param nodeId the id of the node whose position is to be changed
	 * @param x 
	 * @param y
	 * @throws MicropsiException if the node can't be found
	 */
	public void setNodePosition(String nodeId, int x, int y) throws MicropsiException {
		EntityModel model = AgentNetModelManager.getInstance().getNetModel().getModel(nodeId);
		model.setX(x);
		model.setY(y);
	}

	
	/**
	 * Displays a message.
	 * 
	 * @param message
	 */
	public void message(final String message) {
		
		getLogger().debug("Script message: "+message);
						
		display.syncExec(new Runnable() {
			public void run() {
				SuspendMessageDialog mdl = new SuspendMessageDialog(
					RuntimePlugin.getDefault().getShell(),
					"Script message",
					null,
					message,
					MessageDialog.INFORMATION,
					new String[] {"OK"},
					0);

				mdl.setBlockOnOpen(true);
				mdl.open();
				
				if(mdl.isSuspend())
					suspendNet();

			}
		});
				
	}
	
	
}
