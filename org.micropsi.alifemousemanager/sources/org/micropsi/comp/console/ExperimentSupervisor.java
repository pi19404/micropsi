/*
 * Created on 14.09.2004
 *
 */
package org.micropsi.comp.console;

import org.micropsi.comp.ConstantValues;
import org.micropsi.comp.agent.MouseAgentManager;
import org.micropsi.comp.console.worldconsole.LocalWorldObjectInfo;


/**
 * @author Markus
 *
 */
public class ExperimentSupervisor extends Thread {
    private boolean maxAgentsReached = false;
    boolean stayAlive = true;
    
    public ExperimentSupervisor() {
    	super("ExperimentSupervisor");
    	LocalWorldObjectInfo.getInstance();
    }
    
    public void stopController() {
		stayAlive = false;
	}
    
    public void run() {
        while(stayAlive) {
        	MouseAgentManager.getInstance().runAgents();
        	if(LocalWorldObjectInfo.getInstance().getAgentCount() < ConstantValues.MAX_AGENT_COUNT && !maxAgentsReached) {
        		MouseAgentManager.getInstance().createAgent();
        	} else {
        	    //maxAgentsReached = true;
        	}
        	
            try {
				sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }
 
    public void setStayAlive(boolean stayAlive) {
        this.stayAlive = stayAlive;
    }
}
