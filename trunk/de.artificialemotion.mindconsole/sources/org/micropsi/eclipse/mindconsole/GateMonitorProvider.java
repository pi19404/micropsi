/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/GateMonitorProvider.java,v 1.2 2004/08/10 14:35:49 fuessel Exp $ 
 */
package org.micropsi.eclipse.mindconsole;

import org.eclipse.swt.widgets.Shell;

import org.micropsi.eclipse.console.IMonitorProvider;
import org.micropsi.eclipse.console.IParameterMonitor;
import org.micropsi.eclipse.console.IParameterMonitorRegistry;


public class GateMonitorProvider implements IMonitorProvider {

	private static GateMonitorProvider instance;

	public static GateMonitorProvider getInstance() {
		return instance;
	}

	public GateMonitorProvider() {
		instance = this;
	}

	private IParameterMonitorRegistry registry;

	public void initialize(IParameterMonitorRegistry registry) {
		this.registry = registry;
	}

	public IParameterMonitor createMonitor(String classname) {
		try {
			Object o = Class.forName(classname).newInstance();
			return (IParameterMonitor) o;
		} catch (Exception e) {
			MindPlugin.getDefault().handleException(e);
			return null;
		}
	}

	public void registerParameterMonitor(IParameterMonitor mon, Shell shell) {
		registry.registerParameterMonitor(mon,shell);
	}

}
