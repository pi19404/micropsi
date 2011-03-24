/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/actions/EatAction.java,v 1.4 2005/01/31 00:29:26 vuine Exp $ 
 */
package org.micropsi.comp.agent.micropsi.actions;

import org.apache.log4j.Logger;
import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.comp.messages.MAction;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetFacadeIF;
import org.micropsi.nodenet.agent.SituationElement;


public class EatAction implements ActionTranslatorIF {

	private static final String TYPE = "EAT";

	private	NetFacadeIF net;
	private ActionDataTarget eat;
	private MAction eatAction;
	private MicroPsiAgent agent;
	private Logger logger;

	public EatAction(NetFacadeIF net, MicroPsiAgent agent, Logger logger) {
		this.net = net;
		eat = new ActionDataTarget("eat");
		((LocalNetFacade)net).getSensorRegistry().registerActuatorDataTarget(eat);
		((LocalNetFacade)net).getCycle().registerCycleObserver(eat);
		
		this.agent = agent;
		this.logger = logger;
		
		eatAction = new MAction(TYPE,"");
	}

	public String getActionID() {
		return TYPE;
	}

	public MAction calculateAction() {
		SituationElement attention = agent.getSituation().getElementInFovea();
		if(attention != null) {
			eatAction.setTargetObject(attention.getWorldID());				
		} else {
			eatAction.setTargetObject(-1);
		}
		
		eat.quiet();
		eat.confirmActionExecution();
		
		return eatAction;
	}

	public void receiveActionResult(double value) {
		logger.info("eat success: "+value);
		eat.setSuccess(value);
	}

	public double getCurrentActionPriority() {
		return eat.getSignalStrength();
	}

	public void dontCalculateAction() {
		eat.quiet();
	}

	public void shutdown() {
		((LocalNetFacade)net).getSensorRegistry().registerActuatorDataTarget(eat);
	}

}
