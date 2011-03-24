/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.worldconsole/sources/org/micropsi/eclipse/worldconsole/WorldConsole.java,v 1.4 2005/04/25 20:05:18 fuessel Exp $ 
 */
package org.micropsi.eclipse.worldconsole;

import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.eclipse.console.IConsolePart;
import org.micropsi.eclipse.runtime.IBasicServices;


public class WorldConsole implements IConsolePart {

	private static WorldConsole instance;

	public static WorldConsole getInstance() {
		return instance;
	}
	
	private ConsoleFacadeIF console;
	private IBasicServices bserv;
	
	private GlobalData globalData = null;
	
	/* (non-Javadoc)
	 * @see org.micropsi.eclipse.console.IConsolePart#initialize(org.micropsi.eclipse.console.IConsoleFacade, org.micropsi.eclipse.runtime.IBasicServices)
	 */
	public void initialize(ConsoleFacadeIF console, IBasicServices bserv) {	
		this.console = console;
		this.bserv = bserv;
		instance = this;

	}
	
	/**
	 * @return
	 */
	public IBasicServices getBserv() {
		return bserv;
	}

	/**
	 * @return
	 */
	public ConsoleFacadeIF getConsole() {
		return console;
	}
	
	public GlobalData getGlobalData() {
		if (globalData == null) {
			globalData = new GlobalData(getConsole());
		}
		return globalData;
	}
	
}
