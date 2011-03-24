package org.micropsi.comp.agent.minipsi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.agent.AgentIF;
import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.agent.aaa.AgentControllerIF;
import org.micropsi.comp.agent.aaa.AgentWorldAdapterIF;
import org.micropsi.comp.agent.aaa.PerceptTranslatorIF;
import org.micropsi.comp.agent.aaa.UrgeCreatorIF;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.nodenet.LocalNetFacade;


public class MinipsiWorldAdapter implements AgentWorldAdapterIF, AgentControllerIF {

	private MicroPsiAgent micropsi;
	private MovementAction movement;
	private HashMap<String,UrgeCreatorIF> urges;
	
	public void initialize(AgentIF agent, Logger logger) throws MicropsiException {
		logger.debug("Initializing MinipsiWorldAdapter");
		
		micropsi = (MicroPsiAgent)agent;
		movement = new MovementAction((LocalNetFacade)micropsi.getNet(),logger);
	
		urges = new HashMap<String,UrgeCreatorIF>();		
	}

	public AgentControllerIF createController() {
		return this;
	}

	public ActionTranslatorIF[] createActionTranslators() {
		return new ActionTranslatorIF[] {movement};
	}

	public PerceptTranslatorIF[] createPerceptTranslators() {	
		return new PerceptTranslatorIF[] {};
	}

	public UrgeCreatorIF[] createUrgeCreators() {
		UrgeCreatorIF[] toReturn = new UrgeCreatorIF[urges.size()];
		Iterator urgeIterator = urges.values().iterator();
		
		for(int i=0;i<urges.size();i++) 
			toReturn[i] = (UrgeCreatorIF)urgeIterator.next();
		
		return toReturn;		
	}

	public boolean wantsPerception() {
		return true;
	}

	public void receiveBodyPropertyChanges(ArrayList propertyChanges) {
//		for(int i=0;i<propertyChanges.size();i++) {
//			MPerceptionValue tmp = (MPerceptionValue)propertyChanges.get(i);
//			LightUrge urge = (LightUrge)urges.get(tmp.getKey());
//			urge.setSignalStrength(Double.parseDouble(tmp.getValue()));
//		}
	}

	public void notifyOfPerception() {
	}

	public void notifyOfAction() {
	}

	public void notifyOfActionResult(String actionName, double result) {
	}
	
	public void shutdown() {
	}
}
