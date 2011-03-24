package org.micropsi.comp.agent.omni;

import org.micropsi.comp.agent.aaa.UrgeCreatorIF;
import org.micropsi.nodenet.SensorDataSourceIF;

public class LightUrge implements UrgeCreatorIF,SensorDataSourceIF {

	private String TYPE;
	private double signal = 0;
	
	public LightUrge(String type) {
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
