/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.console/sources/org/micropsi/eclipse/console/internal/BasicServicesProxy.java,v 1.2 2004/08/10 14:35:07 fuessel Exp $ 
 */
package org.micropsi.eclipse.console.internal;

import org.apache.log4j.Logger;

import org.micropsi.eclipse.console.ConsoleRuntimeUser;
import org.micropsi.eclipse.runtime.IBasicServices;


public class BasicServicesProxy implements IBasicServices {

	public Logger getLogger() {
		return ConsoleRuntimeUser.getInstance().getLogger();
	}

	public String handleException(Throwable e) {
		return ConsoleRuntimeUser.getInstance().handleException(e);
	}

	public String getServerID() {
		return ConsoleRuntimeUser.getInstance().getServerID();
	}

}
