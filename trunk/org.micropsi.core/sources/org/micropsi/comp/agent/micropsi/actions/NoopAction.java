/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/actions/NoopAction.java,v 1.3 2004/11/24 16:19:32 vuine Exp $ 
 */
package org.micropsi.comp.agent.micropsi.actions;

import org.apache.log4j.Logger;
import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.messages.MAction;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetFacadeIF;


public class NoopAction implements ActionTranslatorIF {

	private static final String TYPE = MAction.NOOP;

	private ActionDataTarget noop;
	private MAction noopAction;

	public NoopAction(NetFacadeIF net, Logger logger) {
		noop = new ActionDataTarget("noop");
		((LocalNetFacade)net).getSensorRegistry().registerActuatorDataTarget(noop);
		
		noopAction = new MAction(TYPE,"");
	}

	public String getActionID() {
		return TYPE;
	}

	public MAction calculateAction() {
		
		noop.quiet();
		noop.confirmActionExecution();
		
		return noopAction;
	}

	public void receiveActionResult(double value) {
		noop.setSuccess(value);
	}

	public double getCurrentActionPriority() {
		return noop.getSignalStrength();
	}

	public void dontCalculateAction() {
		noop.quiet();
	}

	public void shutdown() {
	}

}
