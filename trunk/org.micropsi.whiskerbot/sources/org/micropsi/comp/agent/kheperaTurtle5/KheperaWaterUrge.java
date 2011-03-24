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
public class KheperaWaterUrge implements UrgeCreatorIF, SensorDataSourceIF {
	private String TYPE;
	private double signal = 0;
	private Logger logger = null;
	private boolean debug;
	private double posX = 0;
	private double posY = 0;
	
	private final double WATER_POSITION_X = 0.84;
	private final double WATER_POSITION_Y = 0.85;
	
    //leo: calibrate X,Y positions by hand!
	private final double MAX_DISTANT_X_TO_WATER = 0.23;
	private final double MAX_DISTANT_Y_TO_WATER = 0.1;
	
	// leo: calibrate by hand!
	private final double MAX_DISTANCE_TO_WATER = 
		Math.sqrt(Math.pow(WATER_POSITION_X - MAX_DISTANT_X_TO_WATER, 2) 
				+ Math.pow(WATER_POSITION_Y - MAX_DISTANT_Y_TO_WATER, 2));
	
	public KheperaWaterUrge(String type, Logger logger, boolean debug) {
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
		// euclidian distance to water, scaled to nearly [0,1] = [big distance to water, hit water]:
		signal = 1 - (Math.sqrt(Math.pow(WATER_POSITION_X - posX, 2) 
				+ Math.pow(WATER_POSITION_Y - posY, 2))
				/ MAX_DISTANCE_TO_WATER);
		
		logger.debug("(WATER) signal = "+signal+", pos = "+posX+"|"+posY);
		return signal;
	}
	public void setXSignalStrength(double value) {
		
		posX = value;
	}
	
	public void setYSignalStrength(double value) {
		
		posY = value;
	}
		
	
}
