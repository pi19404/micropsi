package org.micropsi.comp.agent.actions;

import org.apache.log4j.Logger;
import org.micropsi.comp.agent.MouseMicroPsiAgent;
import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.agent.micropsi.actions.ActionDataTarget;
import org.micropsi.comp.messages.MAction;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetFacadeIF;

public class NoAction implements ActionTranslatorIF {

	private static final String actionID = "NOOP";
	private ActionDataTarget noop;
	private	NetFacadeIF net;
	
	public NoAction(LocalNetFacade net, MouseMicroPsiAgent agent, Logger logger) {
		this.net = net;
		noop = new ActionDataTarget("noop");
		net.getSensorRegistry().registerActuatorDataTarget(noop);
		net.getCycle().registerCycleObserver(noop);
	}
	
	public String getActionID() {
		return actionID;
	}

	public double getCurrentActionPriority() {
		return noop.getSignalStrength();
	}

	public MAction calculateAction() {
		return null;
	}

	public void dontCalculateAction() {
		noop.quiet();
	}

	public void receiveActionResult(double value) {
		noop.setSuccess(value);
	}

	public void shutdown() {
		((LocalNetFacade)net).getSensorRegistry().registerActuatorDataTarget(noop);
	}

}
