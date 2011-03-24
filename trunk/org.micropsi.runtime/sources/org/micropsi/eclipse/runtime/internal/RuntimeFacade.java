/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.runtime/sources/org/micropsi/eclipse/runtime/internal/RuntimeFacade.java,v 1.4 2005/01/20 23:26:38 vuine Exp $ 
 */
package org.micropsi.eclipse.runtime.internal;



import org.micropsi.comp.common.AbstractComponent;
import org.micropsi.comp.common.ComponentRunner;
import org.micropsi.comp.common.ComponentRunnerException;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.eclipse.runtime.IBasicServices;
import org.micropsi.eclipse.runtime.IRuntimeFacade;
import org.micropsi.eclipse.runtime.RuntimePlugin;
import org.micropsi.eclipse.runtime.EclipseConsoleFunctionality;


public class RuntimeFacade implements IRuntimeFacade {

	
	public AbstractComponent getComponent(String componentId) throws ComponentRunnerException {
		RuntimePlugin.getDefault();
		return ComponentRunner.getInstance().getComponent(componentId);
	}

	public IBasicServices getBasicServices() {
		return RuntimePlugin.getDefault().getBasicServices();
	}

	public EclipseConsoleFunctionality getSelf() {
		return RuntimePlugin.getDefault().getComponent();
	}

	public ConsoleFacadeIF getConsole() {
		return RuntimePlugin.getDefault().getConsole();
	}

	public String getServerID() {
		return RuntimePlugin.getDefault().getServerID();
	}

}
