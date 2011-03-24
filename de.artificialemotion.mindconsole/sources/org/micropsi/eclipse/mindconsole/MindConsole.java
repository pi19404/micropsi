/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/MindConsole.java,v 1.3 2005/01/20 23:25:30 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole;

import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.eclipse.console.IConsolePart;
import org.micropsi.eclipse.runtime.IBasicServices;


public class MindConsole implements IConsolePart {

	private static MindConsole instance;
	
	public static MindConsole getInstance() {
		return instance;
	}
	
	private ConsoleFacadeIF console;
	private IBasicServices bserv;

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

}
