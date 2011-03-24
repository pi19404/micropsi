/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/actions/DrinkAction.java,v 1.5 2005/07/12 12:55:16 vuine Exp $ 
 */
package org.micropsi.comp.agent.micropsi.actions;

import org.apache.log4j.Logger;
import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.comp.messages.MAction;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetFacadeIF;
import org.micropsi.nodenet.agent.SituationElement;


public class DrinkAction implements ActionTranslatorIF {

	private static final String TYPE = "DRINK";

	private LocalNetFacade net;
	private ActionDataTarget drink;
	private MAction drinkAction;
	private MicroPsiAgent agent;
	private Logger logger;

	public DrinkAction(NetFacadeIF net, MicroPsiAgent agent, Logger logger) {
		this.net = (LocalNetFacade)net;	
		drink = new ActionDataTarget("drink");
		((LocalNetFacade)net).getSensorRegistry().registerActuatorDataTarget(drink);
		((LocalNetFacade)net).getCycle().registerCycleObserver(drink);
		
		this.logger = logger;
		this.agent = agent;
		
		drinkAction = new MAction(TYPE,"");
	}

	public String getActionID() {
		return TYPE;
	}

	public MAction calculateAction() {
		SituationElement attention = agent.getSituation().getElementInFovea();
		if(attention != null) {
			drinkAction.setTargetObject(attention.getWorldID());				
		} else {
			drinkAction.setTargetObject(-1);
		}
		
		drink.quiet();
		drink.confirmActionExecution();

		return drinkAction;
	}

	public void receiveActionResult(double value) {
		logger.info("drink success: "+value);
		drink.setSuccess(value);
	}

	public double getCurrentActionPriority() {
		return drink.getSignalStrength();
	}

	public void dontCalculateAction() {
		drink.quiet();
	}

	public void shutdown() {
		net.getCycle().unregisterCycleObserver(drink);
	}

}
