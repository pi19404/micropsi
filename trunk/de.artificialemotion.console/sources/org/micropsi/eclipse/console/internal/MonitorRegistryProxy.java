/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.console/sources/org/micropsi/eclipse/console/internal/MonitorRegistryProxy.java,v 1.2 2004/08/10 14:35:07 fuessel Exp $ 
 */
package org.micropsi.eclipse.console.internal;

import org.eclipse.swt.widgets.Shell;

import org.micropsi.eclipse.console.IParameterMonitor;
import org.micropsi.eclipse.console.IParameterMonitorRegistry;
import org.micropsi.eclipse.console.adminperspective.ParameterController;


public class MonitorRegistryProxy implements IParameterMonitorRegistry {

	private String providername;

	public MonitorRegistryProxy(String providername) {
		this.providername = providername;
	}
	
	public void registerParameterMonitor(IParameterMonitor mon, Shell shell) {
		ParameterMonitorWrapper w = new ParameterMonitorWrapper(mon,providername);
		ParameterMonitorRegistry.getInstance().registerParameterMonitor(w);
		ParameterController.getInstance().addParameterMonitor(w.getID(), shell);
	}

}
