/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.console/sources/org/micropsi/eclipse/console/internal/ParameterMonitorRegistry.java,v 1.4 2005/07/12 12:52:34 vuine Exp $ 
 */
package org.micropsi.eclipse.console.internal;

import java.util.ArrayList;

import org.micropsi.eclipse.console.IParameterMonitor;


public class ParameterMonitorRegistry {

	private static ParameterMonitorRegistry instance;
	
	public static ParameterMonitorRegistry getInstance() {
		if(instance == null) {
			instance = new ParameterMonitorRegistry();
		} 
		return instance;
	}
	
	private ArrayList<ParameterMonitorWrapper> parameterMonitors = new ArrayList<ParameterMonitorWrapper>(10);
	
	/**
	 * You can safely register the monitor even if underlying data is not
	 * yet there or useful - the getCurrentValue() and getIntervalMillis()
	 * methods won't be called until the data is really to be displayed.
	 * @param mon
	 */
	public void registerParameterMonitor(ParameterMonitorWrapper mon) {
		for(int i=0;i<parameterMonitors.size();i++) {
			if(((IParameterMonitor)parameterMonitors.get(i)).getID().equals(mon.getID())) return;
		}
		parameterMonitors.add(mon);
	}
	
	/**
	 * returns the actual data structure, so be careful
	 * @return
	 */
	public ArrayList getParameterMonitors() {
		return parameterMonitors;
	}

	/**
	 * unregisters all parameter monitors 
	 */
	public void unregisterAllParameterMonitors() {
		parameterMonitors.clear();
	}	
}
