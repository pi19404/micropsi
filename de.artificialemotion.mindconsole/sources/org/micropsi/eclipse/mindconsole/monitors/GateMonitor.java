/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/monitors/GateMonitor.java,v 1.4 2005/11/10 00:24:13 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole.monitors;

import org.eclipse.ui.IMemento;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.console.IParameterMonitor;
import org.micropsi.eclipse.model.net.AgentNetModelManager;


public class GateMonitor implements IParameterMonitor {

	private String id;
	
	private String entityID;
	private int gate;

	public GateMonitor() {
	}

	public GateMonitor(String entityID, int gate)  {
								
		id = "mon_"+gate+"_"+entityID;
		
		this.entityID = entityID;
		this.gate = gate;		
	}

	public long getIntervalMillis() {
		return 1;
	}

	public double getCurrentValue() throws MicropsiException {
		try {
			if(AgentNetModelManager.getInstance().getNetModel().getNet().entityExists(entityID)) {
				return AgentNetModelManager.getInstance().getNetModel().getModel(entityID).getEntity().getGate(gate).getConfirmedActivation();
			} else {
				return 0;
			}
		} catch (Exception e) {
			return 0;
		}
	}

	public String getID() {
		return id;
	}

	public void saveToMemento(IMemento memento) {
		memento.putString("entityID", entityID);
		memento.putInteger("gate", gate);
	}

	public void restoreFromMemento(IMemento memento) {

		this.entityID = memento.getString("entityID");
		this.gate = memento.getInteger("gate").intValue();	

		id = "mon_"+gate+"_"+entityID;
	}

}
