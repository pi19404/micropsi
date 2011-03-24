/*
 * Created on 14.06.2005
 *
 */
package org.micropsi.comp.agent.percepts;

import org.micropsi.comp.agent.MouseMicroPsiAgent;
import org.micropsi.nodenet.SensorDataSourceIF;

/**
 * @author Markus
 *
 */
public class PerceptProperty implements SensorDataSourceIF {

	private String dataType;
	private double signalStrength;

	public PerceptProperty(MouseMicroPsiAgent micropsi, String dataType) {
		this.dataType = dataType;
	}
	
	public String getDataType() {
		return dataType;
	}

	public double getSignalStrength() {
	    return signalStrength;
	}
	
	public void setSignalStrength(double signal) {
	    signalStrength = signal;
	}
}
