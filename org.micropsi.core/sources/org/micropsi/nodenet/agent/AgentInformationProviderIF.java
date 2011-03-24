package org.micropsi.nodenet.agent;

/**
 * MicroPSINetProperties are able to tell native modules information on the current agent.
 */
public interface AgentInformationProviderIF {

	/**
	 * Returns the agent's current name.
	 * @return the agent name
	 */
	public String getAgentName();
	
}
