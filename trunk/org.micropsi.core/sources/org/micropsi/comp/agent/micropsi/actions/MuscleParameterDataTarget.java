/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/actions/MuscleParameterDataTarget.java,v 1.1 2005/11/21 20:12:22 vuine Exp $ 
 */
package org.micropsi.comp.agent.micropsi.actions;

import org.micropsi.comp.agent.micropsi.ActorValueCache;
import org.micropsi.nodenet.ActorDataTargetIF;


public class MuscleParameterDataTarget implements ActorDataTargetIF {
		
	private final String dataType;
	private double signalStrength = 0;
	private ActorValueCache actorValueCache;
			
	public MuscleParameterDataTarget(String emotionParameterName, ActorValueCache actorValueCache) {
		dataType = emotionParameterName;
		this.actorValueCache = actorValueCache;
		actorValueCache.reportValue(dataType,0);
	}

	public String getDataType() {
		return dataType;
	}
	
	public void addSignalStrength(double value) {		
		signalStrength = value;
		actorValueCache.reportValue(dataType,signalStrength);
	}
		
	public double getExecutionSuccess() {
		return 1;
	}
				
	public double getSignalStrength() {
		return signalStrength;
	}
}

