package org.micropsi.comp.agent.kheperaTurtle5;

import org.apache.log4j.Logger;
import org.micropsi.comp.agent.aaa.UrgeCreatorIF;
import org.micropsi.nodenet.SensorDataSourceIF;

public class KheperaColorUrge implements UrgeCreatorIF, SensorDataSourceIF {
	
	private String TYPE; 
	private double signal = 0;
	private Logger logger = null;
	private boolean debug;
	
	public KheperaColorUrge(String type, Logger logger, boolean debug) {
		TYPE = type;
		this.logger = logger;
		this.debug = debug;
	}
	
	

	public void notifyOfPerception() {
		// TODO Auto-generated method stub

	}

	public void notifyOfBodyPropertyChanges() {
		// TODO Auto-generated method stub

	}

	public void shutdown() {
		// TODO Auto-generated method stub

	}

	public String getDataType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	public double getSignalStrength() {
// this method is read by sensor nodes connected to this datasource.
		
		if(debug) logger.debug("(COLORURGE) signal = "+signal);
		return signal;
	}
	
	public void setSignalStrength(double value) {
		/* the position sensors of the khepera provide a range of [0,1]. 
		 * this method is called by KheperaWorldAdapter.receiveBodyPropertyChanges().
		 */
		signal =  value;
		
	}	

}
