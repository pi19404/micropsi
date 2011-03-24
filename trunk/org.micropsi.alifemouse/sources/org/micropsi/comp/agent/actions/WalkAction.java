/*
 * Created on 16.08.2004
 *
 */
package org.micropsi.comp.agent.actions;

import org.apache.log4j.Logger;
import org.micropsi.comp.agent.MouseMicroPsiAgent;
import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.agent.micropsi.actions.ActionDataTarget;
import org.micropsi.comp.messages.MAction;
import org.micropsi.nodenet.LocalNetFacade;

/**
 * @author Markus
 *
 */
public class WalkAction implements ActionTranslatorIF {

    private static final String TYPE = "WALK";
    
    private ActionDataTarget walkPriority;
    private MouseActionDataTarget rotate;
    private MouseActionDataTarget forward;
    private MAction walkAction;
    
	public WalkAction(MouseMicroPsiAgent agent, LocalNetFacade net, Logger logger) {
	    walkAction = new MAction(TYPE, "");
	    walkPriority = new ActionDataTarget("walkPriority");
	    rotate = new MouseActionDataTarget("rotate");
	    forward = new MouseActionDataTarget("forward");
	    net.getSensorRegistry().registerActuatorDataTarget(walkPriority);
		net.getCycle().registerCycleObserver(walkPriority);
	    net.getSensorRegistry().registerActuatorDataTarget(rotate);
		net.getCycle().registerCycleObserver(rotate);
		net.getSensorRegistry().registerActuatorDataTarget(forward);
		net.getCycle().registerCycleObserver(forward);
	}
	
    public String getActionID() {
        return TYPE;
    }

    public double getCurrentActionPriority() {
        /*
        if(walkPriority.getSignalStrength() > 0)
            System.out.print("w");
        */
        return walkPriority.getSignalStrength();
    }

    public MAction calculateAction() {
        walkAction.clearParameters();
        
        walkAction.setTicket(-1);
        walkAction.addParameter((new Double(rotate.getSignalStrength())).toString());
        walkAction.addParameter((new Double(forward.getSignalStrength())).toString());
        
        walkPriority.quiet();
        rotate.quiet();
        forward.quiet();
        walkPriority.confirmActionExecution();
        rotate.confirmActionExecution();
        forward.confirmActionExecution();
        
		return walkAction;
    }

    public void dontCalculateAction() {
        walkPriority.quiet();
        rotate.quiet();
        forward.quiet();
    }

    public void receiveActionResult(double value) {
        walkPriority.setSuccess(value);
    }

    public void shutdown() {
    }

}
