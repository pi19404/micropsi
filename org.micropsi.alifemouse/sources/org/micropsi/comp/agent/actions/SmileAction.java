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
public class SmileAction implements ActionTranslatorIF {

    private static final String actionID = "SMILE";
    private	LocalNetFacade net;
    private ActionDataTarget smile;
    private MouseActionDataTarget red;
    private MouseActionDataTarget green;
    private MouseActionDataTarget blue;
    private MAction smileAction;
    
    public SmileAction(LocalNetFacade net, Logger logger) {
        this.net = net;
        smile = new ActionDataTarget("smile");
        red = new MouseActionDataTarget("smile-red");
        green = new MouseActionDataTarget("smile-green");
        blue = new MouseActionDataTarget("smile-blue");
        net.getSensorRegistry().registerActuatorDataTarget(smile);
		net.getCycle().registerCycleObserver(smile);
		net.getSensorRegistry().registerActuatorDataTarget(red);
		net.getCycle().registerCycleObserver(red);
		net.getSensorRegistry().registerActuatorDataTarget(green);
		net.getCycle().registerCycleObserver(green);
		net.getSensorRegistry().registerActuatorDataTarget(blue);
		net.getCycle().registerCycleObserver(blue);
        smileAction = new MAction(actionID, "");
    }

    public String getActionID() {
        return actionID;
    }

    public double getCurrentActionPriority() {
        return smile.getSignalStrength();
    }

    public MAction calculateAction() {
        int[] RGB = new int[3];
        RGB[0] = (int)(red.getSignalStrength() * 255);
        RGB[1] = (int)(green.getSignalStrength() * 255);
        RGB[2] = (int)(blue.getSignalStrength() * 255);
        
        long ID = Functions.getID(RGB);
        
		smileAction.setTargetObject(ID);
		
		smile.quiet();
		smile.confirmActionExecution();
		
		return smileAction;
    }
/*
    // sets the attention on agent in range
    private void getPartner() {
        
    }
*/
    
    public void dontCalculateAction() {
        smile.quiet();
    }

    public void receiveActionResult(double value) {
        smile.setSuccess(value);
    }

    public void shutdown() {
    	net.getCycle().unregisterCycleObserver(smile);
        net.getCycle().unregisterCycleObserver(red);
        net.getCycle().unregisterCycleObserver(green);
        net.getCycle().unregisterCycleObserver(blue);
    }
}
