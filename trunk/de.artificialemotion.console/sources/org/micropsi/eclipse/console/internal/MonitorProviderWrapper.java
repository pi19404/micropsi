/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.console/sources/org/micropsi/eclipse/console/internal/MonitorProviderWrapper.java,v 1.2 2004/08/10 14:35:07 fuessel Exp $ 
 */
package org.micropsi.eclipse.console.internal;

import org.micropsi.eclipse.console.IMonitorProvider;
import org.micropsi.eclipse.console.IParameterMonitor;
import org.micropsi.eclipse.console.IParameterMonitorRegistry;


public class MonitorProviderWrapper implements IMonitorProvider {

	private IMonitorProvider provider;
	private String id;

	public MonitorProviderWrapper(IMonitorProvider provider, String id) {
		this.provider = provider;
		this.id = id;
	}
	
	public String getID() {
		return id;
	}

	public IParameterMonitor createMonitor(String classname) {
		return provider.createMonitor(classname);
	}

	public void initialize(IParameterMonitorRegistry registry) {
		provider.initialize(registry);
	}

}
