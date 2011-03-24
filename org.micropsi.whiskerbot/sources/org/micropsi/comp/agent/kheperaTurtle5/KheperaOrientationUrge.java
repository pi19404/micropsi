/*
 * Created on Nov 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.micropsi.comp.agent.kheperaTurtle5;

import org.apache.log4j.Logger;
import org.micropsi.comp.agent.aaa.UrgeCreatorIF;
//import org.micropsi.comp.robot.khepera8.khepera.Khepera;
import org.micropsi.nodenet.SensorDataSourceIF;

/**
 * @author Leo
 *
 */
public class KheperaOrientationUrge implements UrgeCreatorIF,
		SensorDataSourceIF {

	private String TYPE;

	private double signal = 0;

	private Logger logger = null;

	private boolean debug;

	public KheperaOrientationUrge(String type, Logger logger, boolean debug) {
		TYPE = type;
		this.logger = logger;
		this.debug = debug;
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
		// this method is read by sensor nodes connected to this datasource.
		
		return signal;
	}

	public void setSignalStrength(double value) {
		/* the orientation sensors of the khepera provide a range of [0,360].
		 * here the intervall is mapped to [0,1]. 
		 * (this method is called by KheperaWorldAdapter.receiveBodyPropertyChanges().)
		 */
		signal = value/360.0;
//		signal = (360 - (value + 90)) / 360.0;
//		signal=value;
	}

}
