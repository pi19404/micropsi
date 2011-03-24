/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/aaa/PerceptTranslatorIF.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $ 
 */
package org.micropsi.comp.agent.aaa;

import org.micropsi.comp.messages.MPercept;

/**
 *
 * Percept translators transfer percept data into the agent.
 *  
 */
public interface PerceptTranslatorIF {
	
	/**
	 * The percept ID that this translator is registered for. Any perception with
	 * the same ID as the return value of this method will be passed to receivePercept
	 * @return the ID of the percept
	 */
	public String getPerceptID();

	/**
	 * Called by the framework if it receives a percept object with this translator's
	 * ID.
	 * @param percept the percept object
	 */
	public void receivePercept(MPercept percept);
	
	/**
	 * This is called directly before destruction and should be used to
	 * unregister listeners etc.
	 */
	public void shutdown();
	
}
