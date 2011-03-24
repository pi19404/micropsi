/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.whiskerbotcontrol/sources/org/micropsi/eclipse/whiskerbotcontrol/RobotConsole.java,v 1.2 2006/04/26 18:22:47 dweiller Exp $ 
 */
package org.micropsi.eclipse.whiskerbotcontrol;

import org.micropsi.eclipse.console.IConsolePart;
import org.micropsi.eclipse.runtime.IBasicServices;
import org.micropsi.comp.console.ConsoleFacadeIF;

public class RobotConsole implements IConsolePart {

	private static RobotConsole instance; 

	private IBasicServices bserv;
	private ConsoleFacadeIF console;

	public static RobotConsole getInstance() {
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
