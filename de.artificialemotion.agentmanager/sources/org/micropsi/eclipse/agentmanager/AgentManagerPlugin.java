/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.agentmanager/sources/org/micropsi/eclipse/agentmanager/AgentManagerPlugin.java,v 1.4 2005/01/20 23:25:50 vuine Exp $ 
 */
package org.micropsi.eclipse.agentmanager;

import org.apache.log4j.Logger;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import org.micropsi.comp.console.ConsoleFacadeIF;


public class AgentManagerPlugin extends AbstractUIPlugin {

	private static AgentManagerPlugin instance;
	
	// for lazy initialization
	private static boolean initialized = false;

	public AgentManagerPlugin() {
		instance = this;
	}

	public static AgentManagerPlugin getDefault() {
		if(!initialized) {
			initialized = true;	
		}
		return instance;
	}
	
	public ConsoleFacadeIF getConsole() {
		return AgentManagerConsole.getInstance().getConsole();
	}
	
	public Logger getLogger() {
		return AgentManagerConsole.getInstance().getBserv().getLogger();
	}
	
	public String handleException(Throwable e) {
		return AgentManagerConsole.getInstance().getBserv().handleException(e);
	}

	public String getServerID() {
		return AgentManagerConsole.getInstance().getBserv().getServerID();
	}
	
}
