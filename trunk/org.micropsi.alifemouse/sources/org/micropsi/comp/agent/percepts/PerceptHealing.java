/*
 * Created on 10.05.2005
 *
 */
package org.micropsi.comp.agent.percepts;

import org.micropsi.comp.agent.MouseMicroPsiAgent;
import org.micropsi.nodenet.SensorDataSourceIF;

/**
 * @author Markus
 *
 */
public class PerceptHealing implements SensorDataSourceIF {

	private String dataType = new String("healing");
	private double signalStrength;

	public PerceptHealing(MouseMicroPsiAgent micropsi) {
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


