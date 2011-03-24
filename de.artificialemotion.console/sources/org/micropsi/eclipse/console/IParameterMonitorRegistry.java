/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.console/sources/org/micropsi/eclipse/console/IParameterMonitorRegistry.java,v 1.2 2004/08/10 14:35:06 fuessel Exp $ 
 */
package org.micropsi.eclipse.console;

import org.eclipse.swt.widgets.Shell;

public interface IParameterMonitorRegistry {
	
	/**
	 * You can safely register the monitor even if underlying data is not
	 * yet there or useful - the getCurrentValue() and getIntervalMillis()
	 * methods won't be called until the data is really to be displayed.
	 * @param mon
	 */
	public abstract void registerParameterMonitor(IParameterMonitor mon, Shell shell);
	
}