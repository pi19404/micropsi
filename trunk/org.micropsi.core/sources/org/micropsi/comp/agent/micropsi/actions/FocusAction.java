/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/actions/FocusAction.java,v 1.2 2005/01/31 01:12:06 vuine Exp $ 
 */
package org.micropsi.comp.agent.micropsi.actions;

import org.apache.log4j.Logger;
import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.comp.messages.MAction;
import org.micropsi.nodenet.agent.SituationElement;


public class FocusAction implements ActionTranslatorIF {

	public static final String TYPE = "FOCUS";

	private MicroPsiAgent agent;

	public FocusAction(MicroPsiAgent agent, Logger logger) {
		this.agent = agent;
	}

	public String getActionID() {
		return TYPE;
	}

	public MAction calculateAction() {
		MAction focusAction = new MAction(TYPE,"");
		SituationElement attention = agent.getSituation().getElementInFovea();
		if(attention != null) {
			focusAction.setTargetObject(attention.getWorldID());				
		} else {
			focusAction.setTargetObject(-1);
		}
		agent.resetZoomFlags();
		
		return focusAction;
	}

	public void receiveActionResult(double value) {
	}

	public double getCurrentActionPriority() {
		return (agent.isAnyZoomFlagSet() ? 0.9 : 0);
	}

	public void dontCalculateAction() {
	}

	public void shutdown() {
	}

}
