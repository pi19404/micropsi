/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/aaa/AgentWorldAdapterIF.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $ 
 */
package org.micropsi.comp.agent.aaa;

import org.apache.log4j.Logger;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.agent.AgentIF;

/**
 * An AgentWorldAdapter describes the interface between one specific type of agent
 * (eg "micropsi") and one specific type of world implementation (eg "island world").
 * You can add any number of world adapters to an agent as long their sets of percept 
 * and action names are disjunct.
 * 
 * WorldAdapters are a flexible way to make some type of agent work within different
 * environments. Embedding agents in environments requires the definition of things
 * that can be received from the environment (percepts) and the definition of things
 * that can be done to the environment (actions).
 * 
 * WorldAdapters are also the means of choice when using some type of agent within
 * other contexts than simulated environments. Possible "real" environments could be
 * the WWW, a text document or, of course, a real robot's environment.
 * 
 * Depending on the architecture of your agent, you will probably also have to do some
 * adaption within the agent itself to make it use the actions and percepts you define
 * in the WorldAdapter. The same holds true for the world - the WorldAdapter doesn't
 * do any sophisticated magic. A WorldAdapter is simply the place where you translate
 * the "language" of the world into that of the agent. So there will need to be an adapter
 * for each pair of world/agent types.
 * 
 */
public interface AgentWorldAdapterIF {

	/**
	 * Initializes the adapter and provides some basic things the other methods
	 * will probably need. The agent object itself - cast it to your agent's 
	 * implementation of the interface and the agent's logger.<br><br>
	 * This method is called once, directly after instantiating the WorldAdapter and
	 * before the create-methods are called.
	 * @param agent the agent
	 * @param logger the agent's logger
	 * @throws MicropsiException if the world adapter can't be initialized for some reason
	 */
	public void initialize(AgentIF agent, Logger logger) throws MicropsiException;

	/**
	 * Creates (or simply returns) the agent controller object.<br><br>
	 * This method is called once, directly after the initialize method was called.
	 * @return the AgentController object of the adapter
	 */
	public AgentControllerIF createController();
	
	/**
	 * Creates (or returns) the action translator objects of this adapter.<br><br>
	 * This method is called once, directly after the createController method.
	 * @return the action translators of the world adapter.
	 */
	public ActionTranslatorIF[] createActionTranslators();
	
	/**
	 * Creates (or returns) the percept translator objects of this adapter.<br><br>
	 * This method is called once, directly after the createTranslators method.
	 * @return the percept translators of the world adapter.
	 */
	public PerceptTranslatorIF[] createPerceptTranslators();
	
	/**
	 * Creates (or returns) the urge creators of this adapter. 
	 * @return the urge creator
	 */
	public UrgeCreatorIF[] createUrgeCreators();
}
