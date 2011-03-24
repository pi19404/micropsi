/*
 * Created on 26.04.2005
 *
 */
package org.micropsi.comp.agent.percepts;

import org.micropsi.comp.ConstantValues;
import org.micropsi.comp.agent.MouseMicroPsiAgent;
import org.micropsi.nodenet.SensorDataSourceIF;

/**
 * @author Markus
 *
 */
public class PerceptXCoordinate implements SensorDataSourceIF {

	private String dataType = new String("x-position");
	private double signalStrength;

	public PerceptXCoordinate(MouseMicroPsiAgent micropsi) {
	}
	
	public String getDataType() {
		return dataType;
	}

//	TODO relative to worldsize
	public double getSignalStrength() {
	    return signalStrength;
	}
	
	public void setSignalStrength(double signal) {
	    signalStrength = signal / ConstantValues.WORLDMAXX;
	}
}
