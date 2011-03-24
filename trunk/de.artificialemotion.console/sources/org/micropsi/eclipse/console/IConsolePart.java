/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.console/sources/org/micropsi/eclipse/console/IConsolePart.java,v 1.3 2005/01/20 23:25:41 vuine Exp $ 
 */
package org.micropsi.eclipse.console;

import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.eclipse.runtime.IBasicServices;


public interface IConsolePart {

	public void initialize(ConsoleFacadeIF console, IBasicServices bserv);
	
	
}
