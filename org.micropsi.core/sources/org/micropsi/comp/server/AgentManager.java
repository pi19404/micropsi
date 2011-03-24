/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/server/AgentManager.java,v 1.3 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.messages.MActionResponse;

public class AgentManager {
		
	class AgentDataElement {
	
		public boolean actionsent;
		public String agentId;
		public long lastTouchedTime;
		public MActionResponse actionResponse;

	}
	
	private HashMap<String,AgentDataElement> agents = new HashMap<String,AgentDataElement>();
	private ArrayList<String> newAgents = new ArrayList<String>();
	private ArrayList<String> deletedAgents = new ArrayList<String>();	
	private static int auxCounter = -1;
	private Logger logger;
	
	private static synchronized String getNextAux() {
		auxCounter++;
		return Integer.toString(auxCounter);
	}
	
	public AgentManager(Logger logger) {
		this.logger = logger;
	}
	
	public synchronized String addAgent(String name, String classname) {
		AgentDataElement newElement = new AgentDataElement();
		newElement.agentId = name;
		if(newElement.agentId == null) newElement.agentId = "agent";
		String origName = newElement.agentId;
		String tmpName = new String(origName);
		while(agents.containsKey(tmpName)) {
          tmpName = new String(origName+getNextAux());
		}
		if(!tmpName.equals(origName)) newElement.agentId = tmpName;

		newElement.lastTouchedTime = System.currentTimeMillis();
		newElement.actionsent = false;
		agents.put(newElement.agentId,newElement);
		newAgents.add(newElement.agentId+","+classname);
		logger.info("Agent registered: "+newElement.agentId);
		return newElement.agentId;
	}
	
	public boolean isAgentKnown(String agentId) {
		Iterator it = agents.keySet().iterator();
		while(it.hasNext()) {
			if(it.next().equals(agentId)) return true;
		}
		return false;
	}
	
	public void removeAgent(String agentId) {
		if(agents.containsKey(agentId)) {
			synchronized(agents) {
				agents.remove(agentId);
				logger.info("Agent removed: "+agentId);
				deletedAgents.add(agentId);
			}
		}
	}
	
	public String[] getNewAgentIDs() {
		synchronized(newAgents) {
			if(newAgents.size() == 0) return null;
			String[] toReturn = newAgents.toArray(new String[0]);
			newAgents.clear();
			return toReturn;
		}
	}
	
	public String[] getDeletedAgentIDs() {
		synchronized(deletedAgents) {
			if(deletedAgents.size() == 0) return null;
			String[] toReturn = deletedAgents.toArray(new String[0]);
			deletedAgents.clear();
			return toReturn;
		}
	}
	
	public Iterator getAllAgentIDs() {
		return agents.keySet().iterator();
	}

	public void touchAgent(String agentId) throws MicropsiException {
		if(!agents.containsKey(agentId)) throw new MicropsiException(1000,agentId);
		agents.get(agentId).lastTouchedTime = System.currentTimeMillis();
	}
	
	// this is NOT synchronized! Synchronization has to be done from outside: Ensure that clearAllFlags
	// and setActionSentFlag don't get called concurrently. Synchronization from outside is necessary
	// because clearAllFlags leads to a common state that has also to be reached by the actionGatherer!
	public void clearAllFlags() {
		Iterator it = agents.values().iterator();
		while(it.hasNext())
			((AgentDataElement)it.next()).actionsent = false;
	}
	
	public void setActionSentFlag(String agentId) throws MicropsiException {
		if(!agents.containsKey(agentId)) throw new MicropsiException(1000,agentId);	
		agents.get(agentId).actionsent = true;
	}
	
	public boolean isActionSentFlag(String agentId) throws MicropsiException {
		if(!agents.containsKey(agentId)) throw new MicropsiException(1000,agentId);
		return agents.get(agentId).actionsent;
	}
	
	public long getTimeSinceLastTouched(String agentId) throws MicropsiException {
		if(!agents.containsKey(agentId)) throw new MicropsiException(1000,agentId);
		return agents.get(agentId).lastTouchedTime;
	}
	
	public void setActionResponse(String agentId, MActionResponse resp) throws MicropsiException {
		if(!agents.containsKey(agentId)) throw new MicropsiException(1000,agentId);
		agents.get(agentId).actionResponse = resp;
	}

	public MActionResponse retrieveActionResponse(String agentId) throws MicropsiException {
		if(!agents.containsKey(agentId)) throw new MicropsiException(1000,agentId);
		AgentDataElement agent = agents.get(agentId);
		MActionResponse toReturn = agent.actionResponse;		
		agent.actionResponse = null;
		return toReturn;
	} 

}
