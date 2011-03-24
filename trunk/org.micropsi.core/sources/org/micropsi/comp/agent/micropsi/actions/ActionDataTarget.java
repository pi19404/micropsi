/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/actions/ActionDataTarget.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $ 
 */
package org.micropsi.comp.agent.micropsi.actions;

import org.micropsi.nodenet.ActorDataTargetIF;
import org.micropsi.nodenet.CycleObserverIF;


public class ActionDataTarget implements ActorDataTargetIF,CycleObserverIF {
		
	private final String dataType;
	private double signalStrength = 0;
	private double success = 0;
	private boolean hasBeenRead = true;
	private Object lock = new Object();
		
	private long currentnetstep;
	private long lastsetnetstep;
		
	public int PARALYSIS_STEPS = 5;
		
	public ActionDataTarget(String actionName) {
		dataType = actionName;
	}

	public String getDataType() {
		return dataType;
	}
	
	public void quiet() {

		synchronized(lock) {
			if(hasBeenRead) success = 0;
		}
				
		signalStrength = 0;
	}

	public void addSignalStrength(double value) {		
		signalStrength += value;
	}
		
	public double getExecutionSuccess() {
		synchronized(lock) {
			hasBeenRead = true;
			return success;
		}
	}
		
	public void setSuccess(double success) {
		synchronized(lock) {
			this.success = success;
			hasBeenRead = false;
		}
	}
		
	public double getSignalStrength() {
		double toReturn = 0;

		if(signalStrength > 0) {
						
			if(currentnetstep < lastsetnetstep + PARALYSIS_STEPS) {
				toReturn = 0;
			} else { 
				toReturn = signalStrength;
			}
			
		}
		
		return toReturn;
	}
	
	public void confirmActionExecution() {
		lastsetnetstep = currentnetstep;
	}
		
	public void startCycle(long netStep) {
	}

	public void endCycle(long netStep) {
		currentnetstep = netStep;
	}

}

