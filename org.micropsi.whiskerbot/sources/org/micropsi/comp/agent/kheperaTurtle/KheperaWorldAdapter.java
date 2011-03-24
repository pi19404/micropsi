/*
 * Created on 24.03.2005
 *
 */
package org.micropsi.comp.agent.kheperaTurtle;

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
//import org.micropsi.comp.agent.turtle.MovementAction;
import org.micropsi.comp.messages.MPerceptionValue;
import org.micropsi.nodenet.LocalNetFacade;

/**
 * @author Uni WLAN
 *
 * created first in project org.micropsi.whiskerbot
 */
public class KheperaWorldAdapter implements AgentWorldAdapterIF,
		AgentControllerIF {

	private MicroPsiAgent micropsi;
	//private KheperaMovementActionTL movement;
	//private 
	private HashMap urges;
	private Logger logger;
	
	public void initialize(AgentIF agent, Logger logger) throws MicropsiException {
	/*	if(agent.getAgentType() != AgentTypesIF.AGENT_MICROPSI_TURTLE)
			throw new MicropsiException(500,Integer.toString(agent.getAgentType()));*/
		this.logger = logger;
		this.logger.debug("Initializing KheperaWorldAdapter...");
		
		micropsi = (MicroPsiAgent)agent;
		//movement = new KheperaMovementActionTL((LocalNetFacade)micropsi.getNet(),logger);
	
		urges = new HashMap();
		
		KheperaProximityUrge tmp = new KheperaProximityUrge("prox_phalanx_left");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(tmp);
		urges.put("PROX_PHALANX_LEFT",tmp);

		tmp = new KheperaProximityUrge("prox_phalanx_right");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(tmp);
		urges.put("PROX_PHALANX_RIGHT",tmp);		
	}


	public AgentControllerIF createController() {
		return this;
	}

	/*public ActionTranslatorIF[] createActionTranslators() {
		return new ActionTranslatorIF[] {movement};
	}*/
	public ActionTranslatorIF[] createActionTranslators() {
		ActionTranslatorIF[] desc = new ActionTranslatorIF[1];
		
		desc[0] = new KheperaMovementActionTL((LocalNetFacade)micropsi.getNet(),logger);
	
		//desc[0] = new EatAction(micropsi.getNet(),micropsi,logger);
		//desc[1] = new DrinkAction(micropsi.getNet(),micropsi,logger); 
		//desc[2] = new NoopAction(micropsi.getNet(),logger);
		//desc[3] = new MoveAction(micropsi.getNet(),logger);
		//desc[4] = new FocusAction(micropsi,logger);
		
		return desc;
	}

	public PerceptTranslatorIF[] createPerceptTranslators() {
		PerceptTranslatorIF[] percepts = new PerceptTranslatorIF[1];
		percepts[0] = new KheperaProximityPerceptTL((LocalNetFacade)micropsi.getNet(),micropsi,logger);
		//percepts[1] = new KheperaSpeedPerceptTL((LocalNetFacade)micropsi.getNet(),micropsi,logger);
		//percepts[3] = new KheperaPositionPerceptTL((LocalNetFacade)micropsi.getNet(),micropsi,logger);
		
		return percepts;
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
	// leo: wird ueber AgentControlerIF von agentenobjekt aufgerufen
	public void receiveBodyPropertyChanges(ArrayList propertyChanges) {
		for(int i=0;i<propertyChanges.size();i++) {
			MPerceptionValue tmp = (MPerceptionValue)propertyChanges.get(i);
			KheperaProximityUrge urge = (KheperaProximityUrge)urges.get(tmp.getKey());
			urge.setSignalStrength(Double.parseDouble(tmp.getValue()));
		}
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.AgentControllerIF#notifyOfPerception()
	 */
	public void notifyOfPerception() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.AgentControllerIF#notifyOfAction()
	 */
	public void notifyOfAction() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.AgentControllerIF#notifyOfActionResult(java.lang.String, double)
	 */
	public void notifyOfActionResult(String arg0, double arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.AgentControllerIF#shutdown()
	 */
	public void shutdown() {
		// TODO Auto-generated method stub

	}

}
