/*
 * Created on Nov 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.micropsi.comp.agent.kheperaTurtle5;

import org.apache.log4j.Logger;
import org.micropsi.comp.agent.aaa.UrgeCreatorIF;
import org.micropsi.nodenet.SensorDataSourceIF;

/**
 * @author Leo
 *
 */
public class KheperaObstacleUrge implements UrgeCreatorIF, SensorDataSourceIF {
	private String TYPE;
	private double signal = 0;
	private Logger logger = null;
	private boolean debug;
	private double posX = 0;
	private double posY = 0;
	
	
	public KheperaObstacleUrge(String type, Logger logger, boolean debug) {
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
		if(debug) logger.debug("(OBSTACLE) signal = "+signal);
		return signal;
	}
	
	public void setXSignalStrength(double value) {
		/* the light sensors of the khepera provide a range of [0,512] equals [bright, dark]. 
		 * this method is called by KheperaWorldAdapter.receiveBodyPropertyChanges().
		 */
		posX = value;
	}
	
	public void setYSignalStrength(double value) {
		/* the light sensors of the khepera provide a range of [0,512] equals [bright, dark]. 
		 * this method is called by KheperaWorldAdapter.receiveBodyPropertyChanges().
		 */
		posY = value;
	}
	
	
}
