/*
 * Created on 20.05.2005
 *
 */
package org.micropsi.comp.agent.actions;

import org.apache.log4j.Logger;
import org.micropsi.comp.Functions;
import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.agent.micropsi.actions.ActionDataTarget;
import org.micropsi.comp.messages.MAction;
import org.micropsi.nodenet.LocalNetFacade;

/**
 * @author Markus
 *
 */
public class BiteAction implements ActionTranslatorIF {

    private static final String actionID = "BITE";
    private	LocalNetFacade net;
    private ActionDataTarget bite;
    private ActionDataTarget red;
    private ActionDataTarget green;
    private ActionDataTarget blue;
    private MAction biteAction;

    public BiteAction(LocalNetFacade net, Logger logger) {
        this.net = net;
        bite = new ActionDataTarget("bite");
        red = new ActionDataTarget("bite-red");
        green = new ActionDataTarget("bite-green");
        blue = new ActionDataTarget("bite-blue");
        net.getSensorRegistry().registerActuatorDataTarget(bite);
		net.getCycle().registerCycleObserver(bite);
		net.getSensorRegistry().registerActuatorDataTarget(red);
		net.getCycle().registerCycleObserver(red);
		net.getSensorRegistry().registerActuatorDataTarget(green);
		net.getCycle().registerCycleObserver(green);
		net.getSensorRegistry().registerActuatorDataTarget(blue);
		net.getCycle().registerCycleObserver(blue);
        biteAction = new MAction(actionID, "");
    }

    public String getActionID() {
        return actionID;
    }

    public double getCurrentActionPriority() {
        return bite.getSignalStrength();
    }

    public MAction calculateAction() {
        int[] RGB = new int[3];
        RGB[0] = (int)(red.getSignalStrength() * 255);
        RGB[1] = (int)(green.getSignalStrength() * 255);
        RGB[2] = (int)(blue.getSignalStrength() * 255);
        
        long ID = Functions.getID(RGB);
        
		biteAction.setTargetObject(ID);
		
		bite.quiet();
		bite.confirmActionExecution();
		
		return biteAction;
    }
    
    public void dontCalculateAction() {
        bite.quiet();
    }

    public void receiveActionResult(double value) {
        bite.setSuccess(value);
    }

    public void shutdown() {
        net.getCycle().unregisterCycleObserver(bite);
        net.getCycle().unregisterCycleObserver(red);
        net.getCycle().unregisterCycleObserver(green);
        net.getCycle().unregisterCycleObserver(blue);
    }
}