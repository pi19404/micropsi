/*
 * Created on Dec 22, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.micropsi.comp.agent.amouse.percept;

import org.micropsi.comp.agent.aaa.UrgeCreatorIF;
import org.micropsi.nodenet.SensorDataSourceIF;

/**
 * @author Daniel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LightUrges implements UrgeCreatorIF, SensorDataSourceIF {

	private String TYPE;
	private double signal = 0;
	
	public LightUrges(String type) {
		TYPE = type;
	}
	
	public void notifyOfPerception() {
	}

	public void notifyOfBodyPropertyChanges() {
	}

	public void shutdown() {
	}

	public String getDataType() {
		return TYPE;
	}

	public double getSignalStrength() {
		return signal;
	}
	
	public void setSignalStrength(double value) {
		signal = value;
	}
}
