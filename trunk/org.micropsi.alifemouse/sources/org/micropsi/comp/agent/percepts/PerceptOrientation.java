/*
 * Created on 21.05.2005
 *
 */
package org.micropsi.comp.agent.percepts;

import org.micropsi.comp.agent.MouseMicroPsiAgent;
import org.micropsi.nodenet.SensorDataSourceIF;

/**
 * @author Markus
 *
 */
public class PerceptOrientation implements SensorDataSourceIF {

	private String dataType = new String("orientation");
	private double signalStrength;

	public PerceptOrientation(MouseMicroPsiAgent micropsi) {
	}
	
	public String getDataType() {
		return dataType;
	}

//	TODO relative to worldsize
	public double getSignalStrength() {
	    return signalStrength;
	}
	
	public void setSignalStrength(double signal) {
	    signalStrength = signal;
	}
}
