/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/percepts/PerceptObjectDataSource.java,v 1.5 2004/11/15 01:35:15 vuine Exp $
 */
package org.micropsi.comp.agent.micropsi.percepts;

import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.nodenet.SensorDataSourceIF;
import org.micropsi.nodenet.agent.SituationElement;

public class PerceptObjectDataSource implements SensorDataSourceIF {
	
	private final String dataType;
	private final String percept;
	private MicroPsiAgent agent;
		
	public PerceptObjectDataSource(String percept, MicroPsiAgent agent) {
		this.dataType = "percept_"+percept;
		this.percept = percept;
		this.agent = agent;
	}
		
	public String getDataType() {
		return dataType;
	}
		
	public double getSignalStrength() {
		SituationElement cur = agent.getSituation().getElementInFovea();
		if(cur == null) return -1;
		double toReturn = (cur.getType().equals(percept) ? 1 : -1);						
		return toReturn;
	}				

}
