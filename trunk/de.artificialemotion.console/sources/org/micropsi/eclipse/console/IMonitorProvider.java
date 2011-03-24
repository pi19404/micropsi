/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.console/sources/org/micropsi/eclipse/console/IMonitorProvider.java,v 1.2 2004/08/10 14:35:06 fuessel Exp $ 
 */
package org.micropsi.eclipse.console;

public interface IMonitorProvider {

	public void initialize(IParameterMonitorRegistry registry);
	
	public IParameterMonitor createMonitor(String classname);
	

}
