/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/aaa/ActionTranslatorIF.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $ 
 */
package org.micropsi.comp.agent.aaa;

import org.micropsi.comp.messages.MAction;

/**
 * 
 * Action translators decide what (if any) action will be sent to the server and
 * construct the MAction objects when needed. 
 * 
 */
public interface ActionTranslatorIF {

	/**
	 * Returns the ID of the action that this translator will construct.  
	 * @return the action's id
	 */
	public String getActionID();
	
	/**
	 * Returns the action's current priority. Every step the framework calls this
	 * method on all action translators. The action with the highest priority will
	 * "win".
	 * @return the action's priority for this step.
	 */
	public double getCurrentActionPriority();
	
	/**
	 * This method will be called on the action translator with the highest priority.
	 * It returns the MAction object that will be sent to the world. If, for some
	 * reason, the action would be invalid or should not be sent, it is okay to return
	 * null (no action will be sent in that case).
	 * @return the action to be sent to the world.
	 */
	public MAction calculateAction();
	
	/**
	 * This method is called on the actions with lower priorities. (Those that did
	 * not "win") Typically used for cleaning up.  
	 */
	public void dontCalculateAction();
		
	/**
	 * Called every cycle to report the success of the action,
	 * without regard of previous cycle's priorities. If the action was not sent
	 * to the server (if it didn't win, if calculateAction returned null) the
	 * value will simply be zero. Otherwise, the value will be the success value
	 * received from the server.
	 * @param value the success of this action's execution
	 */
	public void receiveActionResult(double value);
	
	/**
	 * This is called directly before destruction and should be used to
	 * unregister listeners etc.
	 */
	public void shutdown();

}
