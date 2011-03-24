/*
 * Created on Nov 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.micropsi.comp.agent.kheperaTurtle2;

import org.micropsi.comp.agent.aaa.UrgeCreatorIF;
import org.micropsi.nodenet.SensorDataSourceIF;

/**
 * @author Leo
 *
 */
public class KheperaLightUrge implements UrgeCreatorIF, SensorDataSourceIF {
	private String TYPE;
	private double signal = 0;
	
	public KheperaLightUrge(String type) {
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
		/* the light sensors of the khepera provide a range of roughly [80,500] 
           with high values meaning darkness. here the range is scaled to [0,1]
		   with low values meaning darkness.
		   this method is called by KheperaWorldAdapter.receiveBodyPropertyChanges().
		*/
		signal = (-1.0 * ((value - 80)/420.0)) + 1.0;
		//signal = value;
	}	
		
}
