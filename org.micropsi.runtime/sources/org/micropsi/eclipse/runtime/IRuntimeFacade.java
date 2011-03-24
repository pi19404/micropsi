/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.runtime/sources/org/micropsi/eclipse/runtime/IRuntimeFacade.java,v 1.4 2005/01/20 23:26:38 vuine Exp $ 
 */
package org.micropsi.eclipse.runtime;

import org.micropsi.comp.common.AbstractComponent;
import org.micropsi.comp.common.ComponentRunnerException;
import org.micropsi.comp.console.ConsoleFacadeIF;


public interface IRuntimeFacade {
	
	public IBasicServices getBasicServices();
	
	public ConsoleFacadeIF getConsole();
	
	public EclipseConsoleFunctionality getSelf();
	
	public AbstractComponent getComponent(String componentId) throws ComponentRunnerException;
	
	public String getServerID();

}
