/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/DebugDataSource.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $ 
 */
package org.micropsi.comp.agent.micropsi;

import org.micropsi.nodenet.SensorDataSourceIF;


public class DebugDataSource implements SensorDataSourceIF {

	private final String type;
	private double strength;

	public DebugDataSource(double strength) {
		this.strength = strength;
		type = "debugSource";
	}

	public String getDataType() {
		return type;
	}

	public double getSignalStrength() {
		return strength;
	}
	
	public void setStrength(double strength) {
		this.strength = strength;
	}
}	