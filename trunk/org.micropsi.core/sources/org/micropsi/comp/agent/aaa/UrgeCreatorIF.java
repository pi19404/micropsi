/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/aaa/UrgeCreatorIF.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $ 
 */
package org.micropsi.comp.agent.aaa;

/**
 * UrgeCreators provide urges to the agent. How this is done, of course, very much
 * depends on the type of the agent. Implementations of this interface can leave
 * both methods blank if the urge does not depend on perception or body properties,
 * but on some other parameter, say the agent's internal timer. 
 */
public interface UrgeCreatorIF {

	/**
	 * Called by the framework when new perception arrived. You <i>may</i> want
	 * to recalc urges then. Note: The method, unlike the one in the AgentControllerIF,
	 * is called AFTER all perception data has been posted to the percept translators.
	 */
	public void notifyOfPerception();
	
	/**
	 * Called by the framework when some body property change occurred. You <i>may</i>
	 * want to recalc urges then.
	 */
	public void notifyOfBodyPropertyChanges();

	/**
	 * This is called directly before destruction and should be used to
	 * unregister listeners etc.
	 */
	public void shutdown();

}
