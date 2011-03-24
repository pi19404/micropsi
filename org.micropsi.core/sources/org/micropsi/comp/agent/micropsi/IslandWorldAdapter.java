/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/IslandWorldAdapter.java,v 1.8 2005/11/21 20:12:23 vuine Exp $ 
 */
package org.micropsi.comp.agent.micropsi;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.agent.AgentIF;
import org.micropsi.comp.agent.AgentTypesIF;
import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.agent.aaa.AgentControllerIF;
import org.micropsi.comp.agent.aaa.AgentWorldAdapterIF;
import org.micropsi.comp.agent.aaa.PerceptTranslatorIF;
import org.micropsi.comp.agent.aaa.UrgeCreatorIF;
import org.micropsi.comp.agent.micropsi.actions.DrinkAction;
import org.micropsi.comp.agent.micropsi.actions.EatAction;
import org.micropsi.comp.agent.micropsi.actions.FocusAction;
import org.micropsi.comp.agent.micropsi.actions.MoveAction;
import org.micropsi.comp.agent.micropsi.actions.NoopAction;
import org.micropsi.comp.agent.micropsi.conserv.QTypeChangeBodyData;
import org.micropsi.comp.agent.micropsi.conserv.QTypeGetBodyData;
import org.micropsi.comp.agent.micropsi.percepts.WorldContentPercept;
import org.micropsi.comp.agent.micropsi.urges.BodySimulator;
import org.micropsi.comp.messages.MPerceptionValue;
import org.micropsi.nodenet.LocalNetFacade;


public class IslandWorldAdapter implements AgentWorldAdapterIF, AgentControllerIF {

	private MicroPsiAgent micropsi;
	private BodySimulator bodySimulator;
	private Logger logger;
	private boolean perceptionInterest = true;

	public void initialize(AgentIF agent, Logger logger) throws MicropsiException {
		if(agent.getAgentType() != AgentTypesIF.AGENT_MICROPSI_STANDARD)
			throw new MicropsiException(500,Integer.toString(agent.getAgentType()));
				
		logger.debug("Initializing island WorldAdapter");
		
		this.logger = logger;
		micropsi = (MicroPsiAgent)agent;
		
		bodySimulator = new BodySimulator(micropsi);
		micropsi.getNet().getCycle().registerCycleObserver(bodySimulator);
		
		micropsi.registerAdditionalQuestionType(new QTypeGetBodyData(bodySimulator));
		micropsi.registerAdditionalQuestionType(new QTypeChangeBodyData(bodySimulator));
	}
	
	public void shutdown() {
		micropsi.getNet().getCycle().unregisterCycleObserver(bodySimulator);
	}


	public ActionTranslatorIF[] createActionTranslators() {
		ActionTranslatorIF[] desc = new ActionTranslatorIF[5];
		
		desc[0] = new EatAction(micropsi.getNet(),micropsi,logger);
		desc[1] = new DrinkAction(micropsi.getNet(),micropsi,logger); 
		desc[2] = new NoopAction(micropsi.getNet(),logger);
		desc[3] = new MoveAction(micropsi.getNet(),logger);
		desc[4] = new FocusAction(micropsi,logger);
		
		return desc;
	}

	public PerceptTranslatorIF[] createPerceptTranslators() {
		PerceptTranslatorIF[] percepts = new PerceptTranslatorIF[1];
		percepts[0] = new WorldContentPercept((LocalNetFacade)micropsi.getNet(),micropsi,logger);
		
		//percepts[1] = new PatchPercept((LocalNetFacade)micropsi.getNet(),micropsi,logger);
		
		return percepts;
	}

	public AgentControllerIF createController() {
		return this;
	}

	public boolean wantsPerception() {
		if(!micropsi.isAlive()) return false;
		return perceptionInterest;
	}

	public void receiveBodyPropertyChanges(ArrayList propertyChanges) {
		for(int i=0;i<propertyChanges.size();i++) {
			MPerceptionValue tmp = (MPerceptionValue)propertyChanges.get(i);
			if(tmp.getKey().equals("ABSORBED_FOOD")) {
				bodySimulator.eat(Double.parseDouble(tmp.getValue()));		
			} else if(tmp.getKey().equals("ABSORBED_WATER")) {
				bodySimulator.drink(Double.parseDouble(tmp.getValue()));			
			} else if(tmp.getKey().equals("SUFFERED_DAMAGE")) {
				bodySimulator.damage(Double.parseDouble(tmp.getValue()));
			}
		}
	}

	public void notifyOfPerception() {
		perceptionInterest = false;
		micropsi.getSituation().clear();
	}

	public void notifyOfAction() {
	}
	
	public void notifyOfActionResult(String actionName, double result) {	
		if(!actionName.equals(FocusAction.TYPE)) {
			perceptionInterest = true;
		}
	}


	public UrgeCreatorIF[] createUrgeCreators() {
		return bodySimulator.createUrges((LocalNetFacade)micropsi.getNet());
	}

}
