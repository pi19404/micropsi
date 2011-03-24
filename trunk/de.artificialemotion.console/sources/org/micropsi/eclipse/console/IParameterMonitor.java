/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.console/sources/org/micropsi/eclipse/console/IParameterMonitor.java,v 1.2 2004/08/10 14:35:06 fuessel Exp $ 
 */
package org.micropsi.eclipse.console;

import org.eclipse.ui.IMemento;

import org.micropsi.common.exception.MicropsiException;


public interface IParameterMonitor {

	public long getIntervalMillis();

	public double getCurrentValue() throws MicropsiException;
	
	public String getID();
		
	public void saveToMemento(IMemento memento);
	
	public void restoreFromMemento(IMemento memento);
	
}
