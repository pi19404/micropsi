/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.agentmanager/sources/org/micropsi/eclipse/agentmanager/AgentManagerConsole.java,v 1.3 2005/01/20 23:25:50 vuine Exp $ 
 */
package org.micropsi.eclipse.agentmanager;

import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.eclipse.console.IConsolePart;
import org.micropsi.eclipse.runtime.IBasicServices;


public class AgentManagerConsole implements IConsolePart {

	private static AgentManagerConsole instance; 

	private IBasicServices bserv;
	private ConsoleFacadeIF console;

	public static AgentManagerConsole getInstance() {
		return instance;
	}

	public void initialize(ConsoleFacadeIF console, IBasicServices bserv) {
		this.bserv = bserv;
		this.console = console;
		instance = this;
	}
	
	public IBasicServices getBserv() {
		return bserv;
	}

	public ConsoleFacadeIF getConsole() {
		return console;
	}	

}
