/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/aaa/AgentControllerIF.java,v 1.3 2005/01/31 00:29:26 vuine Exp $
 */
package org.micropsi.comp.agent.aaa;

import java.util.ArrayList;

/**
 * 
 * The AgentController manages the properties of an adapter, which are not specific
 * to any particular action or percept. 
 * 
 */
public interface AgentControllerIF {

	/**
	 * Checks if the agent is ready to receive perception data. If this method returns
	 * false, no perception data will be requested in a cycle, keeping the load on
	 * transportation and/or perception infrastructure low. You can of course always
	 * return true, but typically agents are not able to handle perception each step
	 * or even make good guesses on when it will make sense to have a look at the world
	 * again.<br><br>
	 * This method is called periodically every step.
	 * @return true if the agent does want to request perception data this step.
	 */
	public boolean wantsPerception();
	
	/**
	 * Body property changes are not ordinary perceptions, but immediate results of
	 * some action. The ArrayList passed to this method contains object of type
	 * MPerceptionValue. What exactly the world returns depends on the world 
	 * implementation.<br><br>
	 * This method is called whenever an action response was received from the world,
	 * typically some steps after an action was sent to the world.
	 * @param parameterChanges a List of MPerceptionValue with body parameter changes.
	 */
	public void receiveBodyPropertyChanges(ArrayList parameterChanges);
	
	/**
	 * This method is called directly after the framework received a new perception
	 * packet. The packet contents are the (after this method returned) dispatched to
	 * the perception translators.
	 */
	public void notifyOfPerception();
	
	/**
	 * This method is called directly after an action translator constructed an action
	 * and before it is sent to the world. (Typically implementations of this method
	 * will do some cleanup)
	 */
	public void notifyOfAction();
	
	/**
	 * This method is called when action result data is received from the world.
	 * @param actionName the name of the action
	 * @param result the success code or value
	 */
	public void notifyOfActionResult(String actionName, double result);
	
	/**
	 * This is called directly before destruction and should be used to
	 * unregister listeners etc.
	 */
	public void shutdown();
	
}
