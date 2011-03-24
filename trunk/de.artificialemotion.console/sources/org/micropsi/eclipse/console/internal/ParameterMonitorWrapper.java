/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.console/sources/org/micropsi/eclipse/console/internal/ParameterMonitorWrapper.java,v 1.2 2004/08/10 14:35:07 fuessel Exp $ 
 */
package org.micropsi.eclipse.console.internal;

import org.eclipse.ui.IMemento;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.console.IParameterMonitor;


public class ParameterMonitorWrapper implements IParameterMonitor {

	private IParameterMonitor monitor;
	private String provider;
	
	public ParameterMonitorWrapper(IParameterMonitor monitor, String provider) {
		this.monitor = monitor;
		this.provider = provider;
	}

	public long getIntervalMillis() {
		return monitor.getIntervalMillis();
	}

	public double getCurrentValue() throws MicropsiException {
		return monitor.getCurrentValue();
	}

	public String getID() {
		return monitor.getID();
	}

	public void saveToMemento(IMemento memento) {
		monitor.saveToMemento(memento);
	}

	public void restoreFromMemento(IMemento memento) {
		monitor.restoreFromMemento(memento);
	}
	
	public String getProvider() {
		return provider;
	}
	
	public IParameterMonitor getWrapped() {
		return monitor;
	}

}
