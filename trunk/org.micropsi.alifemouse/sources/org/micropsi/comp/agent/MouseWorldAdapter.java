/*
 * Created on 03.05.2005
 *
 */
package org.micropsi.comp.agent;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.agent.aaa.AgentControllerIF;
import org.micropsi.comp.agent.aaa.AgentWorldAdapterIF;
import org.micropsi.comp.agent.aaa.PerceptTranslatorIF;
import org.micropsi.comp.agent.aaa.UrgeCreatorIF;
import org.micropsi.comp.agent.actions.*;
import org.micropsi.comp.agent.percepts.*;
import org.micropsi.comp.agent.urges.MouseBodySimulator;
import org.micropsi.comp.messages.MPerceptionValue;
import org.micropsi.nodenet.LocalNetFacade;

/**
 * @author Markus
 *
 */
public class MouseWorldAdapter implements AgentWorldAdapterIF, AgentControllerIF {
    
    private Logger logger;
    private MouseMicroPsiAgent agent;
    private MouseBodySimulator bodySimulator;
    
    private PerceptXCoordinate xPercept;
    private PerceptYCoordinate yPercept;
    private PerceptOrientation orientationPercept;
    private PerceptObstacle obstaclePercept;
    private PerceptPoison poisonPercept;
    private PerceptSmiling smilePercept;
    private PerceptBiting bitePercept;
    private PerceptFood foodPercept;
    private PerceptWater waterPercept;
    private PerceptHealing healingPercept;
    private PerceptCollision collisionPercept;
    
    public void initialize(AgentIF agent, Logger logger) throws MicropsiException {
        if(agent.getAgentType() != AgentTypesIF.AGENT_MICROPSI_STANDARD)
			throw new MicropsiException(500,Integer.toString(agent.getAgentType()));
        
        this.agent = (MouseMicroPsiAgent)agent;
        this.logger = logger;
        this.bodySimulator = new MouseBodySimulator(this.agent, logger);
        this.agent.getNet().getCycle().registerCycleObserver(this.bodySimulator);

        xPercept = new PerceptXCoordinate(this.agent);
	    ((LocalNetFacade)this.agent.getNet()).getSensorRegistry().registerSensorDataProvider(xPercept);
	    yPercept = new PerceptYCoordinate(this.agent);
	    ((LocalNetFacade)this.agent.getNet()).getSensorRegistry().registerSensorDataProvider(yPercept);
	    orientationPercept = new PerceptOrientation(this.agent);
	    ((LocalNetFacade)this.agent.getNet()).getSensorRegistry().registerSensorDataProvider(orientationPercept);
	    obstaclePercept = new PerceptObstacle(this.agent);
	    ((LocalNetFacade)this.agent.getNet()).getSensorRegistry().registerSensorDataProvider(obstaclePercept);
	    poisonPercept = new PerceptPoison(this.agent);
	    ((LocalNetFacade)this.agent.getNet()).getSensorRegistry().registerSensorDataProvider(poisonPercept);
	    foodPercept = new PerceptFood(this.agent);
	    ((LocalNetFacade)this.agent.getNet()).getSensorRegistry().registerSensorDataProvider(foodPercept);
	    waterPercept = new PerceptWater(this.agent);
	    ((LocalNetFacade)this.agent.getNet()).getSensorRegistry().registerSensorDataProvider(waterPercept);
	    healingPercept = new PerceptHealing(this.agent);
	    ((LocalNetFacade)this.agent.getNet()).getSensorRegistry().registerSensorDataProvider(healingPercept);
	    collisionPercept = new PerceptCollision(this.agent);
	    ((LocalNetFacade)this.agent.getNet()).getSensorRegistry().registerSensorDataProvider(collisionPercept);
	    
	    smilePercept = new PerceptSmiling(this.agent);
	    bitePercept = new PerceptBiting(this.agent);
    }
    
    public AgentControllerIF createController() {
        return this;
    }
    
    public ActionTranslatorIF[] createActionTranslators() {
        ActionTranslatorIF[] desc = new ActionTranslatorIF[4];
        
        desc[0] = new WalkAction(agent, (LocalNetFacade)agent.getNet(), logger);
        desc[1] = new MovementAction((LocalNetFacade)agent.getNet(), logger);
        desc[2] = new SmileAction((LocalNetFacade)agent.getNet(), logger);
        desc[3] = new BiteAction((LocalNetFacade)agent.getNet(), logger);
        
        return desc;
    }

    public PerceptTranslatorIF[] createPerceptTranslators() {
        PerceptTranslatorIF[] percepts = new PerceptTranslatorIF[1];
        percepts[0] = new MouseWorldContentPercept(agent, (LocalNetFacade)agent.getNet(), logger);
		return percepts;
    }
    
    public boolean wantsPerception() {
		return true;
	}
    
    public UrgeCreatorIF[] createUrgeCreators() {
        return bodySimulator.createUrges((LocalNetFacade)agent.getNet());
    }
    
    public void receiveBodyPropertyChanges(ArrayList propertyChanges) {
    	int smiled = 0;
        int smiledAt = 0;
        int[] smilerRGB = new int[3];
        int gotBitten = 0;
        int[] biterRGB = new int[3];
		for(int i = 0; i < propertyChanges.size(); i++) {
			MPerceptionValue tmp = (MPerceptionValue)propertyChanges.get(i);
			switch(i) {
				case 0: xPercept.setSignalStrength(Double.parseDouble(tmp.getValue()));
						break;
				case 1: yPercept.setSignalStrength(Double.parseDouble(tmp.getValue()));
						break;
				case 2: orientationPercept.setSignalStrength(Double.parseDouble(tmp.getValue()));
						break;
				case 3: obstaclePercept.setSignalStrength(Integer.parseInt(tmp.getValue()));
						break;
				case 4: poisonPercept.setSignalStrength(Integer.parseInt(tmp.getValue()));
						bodySimulator.setGroundTypeIsPoison(Integer.parseInt(tmp.getValue()) == 1);
						break;
				case 5: bodySimulator.setGroundTypeIsFood(Integer.parseInt(tmp.getValue()) == 1);
						foodPercept.setSignalStrength(Integer.parseInt(tmp.getValue()));
						break;
				case 6: bodySimulator.setGroundTypeIsWater(Integer.parseInt(tmp.getValue()) == 1);
						waterPercept.setSignalStrength(Integer.parseInt(tmp.getValue()));
						break;
				case 7: bodySimulator.setGroundTypeIsHealing(Integer.parseInt(tmp.getValue()) == 1);
						healingPercept.setSignalStrength(Integer.parseInt(tmp.getValue()));
						break;
				case 8: collisionPercept.setSignalStrength(Integer.parseInt(tmp.getValue()));
						break;
				case 9: smiledAt = Integer.parseInt(tmp.getValue());
					    break;
			    case 10: smilerRGB[0] = Integer.parseInt(tmp.getValue());
			    		break;
			    case 11: smilerRGB[1] = Integer.parseInt(tmp.getValue());
	    				 break;
			    case 12: smilerRGB[2] = Integer.parseInt(tmp.getValue());
	    				 break;
			    case 13: gotBitten = Integer.parseInt(tmp.getValue());
			    		 break;
			    case 14: biterRGB[0] = Integer.parseInt(tmp.getValue());
	    				 break;
			    case 15: biterRGB[1] = Integer.parseInt(tmp.getValue());
				 		 break;
			    case 16: biterRGB[2] = Integer.parseInt(tmp.getValue());
			    		 break;
			    case 17: smiled = Integer.parseInt(tmp.getValue());
			    		 break;
			}
		}
		
		bodySimulator.setSmiled(smiled == 1 || smiledAt == 1);
		
		smilePercept.setSignalStrength(smiledAt, smilerRGB);
		bitePercept.setSignalStrength(gotBitten, biterRGB);
	}
    
    public void notifyOfPerception() {
		agent.getSituation().clear();
	}

	public void notifyOfAction() {
	}

	public void notifyOfActionResult(String actionName, double result) {
	}
	
	public void shutdown() {
	}

}
