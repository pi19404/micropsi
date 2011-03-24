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
public class KheperaFoodUrge implements UrgeCreatorIF, SensorDataSourceIF {
	private String TYPE;
	private double signal = 0;
	private Logger logger = null;
	private boolean debug;
	private double posX = 0;
	private double posY = 0;
	
	//leo: set food position: (to do calibration run main() of Tracker class)
	private final double FOOD_POSITION_X = 0.93;
	private final double FOOD_POSITION_Y = 0.5;
	
	//leo: calibrate X,Y positions by hand!
	private final double MAX_DISTANT_X_TO_FOOD = 0.1;
	private final double MAX_DISTANT_Y_TO_FOOD = 0.5;
	
	private final double MAX_DISTANCE_TO_FOOD = 
		Math.sqrt(Math.pow(FOOD_POSITION_X - MAX_DISTANT_X_TO_FOOD, 2) 
				+ Math.pow(FOOD_POSITION_Y - MAX_DISTANT_Y_TO_FOOD, 2));
	
	
	public KheperaFoodUrge(String type, Logger logger, boolean debug) {
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
		// euclidian distance to food, scaled to nearly [0,1] = [big distance to food, hit food]:
		signal = ((Math.sqrt(Math.pow(FOOD_POSITION_X - posX, 2) 
				+ Math.pow(FOOD_POSITION_Y - posY, 2))
				/ MAX_DISTANCE_TO_FOOD)
				- 0.5)
				* (-1) + 0.5;
		
		logger.debug("(FOOD) signal = "+signal+", pos = "+posX+"|"+posY);
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
