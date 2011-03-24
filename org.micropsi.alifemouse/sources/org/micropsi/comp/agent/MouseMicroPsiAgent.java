/*
 * Created on 09.04.2005
 *
 */
package org.micropsi.comp.agent;

import java.util.HashMap;

import org.micropsi.common.config.ConfigurationReaderIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.ConstantValues;
import org.micropsi.comp.agent.AgentFrameworkComponent;
import org.micropsi.comp.agent.conserv.*;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.comp.agent.urges.MouseBodySimulator;

/**
 * @author Markus
 *
 */
public class MouseMicroPsiAgent extends MicroPsiAgent implements MouseAgentIF {
	protected String type = null;
	private MouseBodySimulator bodySimulator;
	private Position position;
	private long ID;
	private HashMap<Long, AgentRepresentation> knownAgents;
	private boolean hasDied = false;
	private boolean phenotypeCreated = false;
	
	public ConsoleQuestionTypeIF[] initialize(AgentFrameworkComponent teclayer, String configroot, ConfigurationReaderIF reader) throws MicropsiException {
        ConsoleQuestionTypeIF[] toReturn = super.initialize(teclayer, configroot, reader);
		type = new String("MouseAgent");
		knownAgents = new HashMap<Long, AgentRepresentation>(ConstantValues.MAX_AGENT_COUNT);
		MouseAgentManager.getInstance().addAgent(this);
		registerAdditionalQuestionType(new QTypeGetWaypoints(this));
		registerAdditionalQuestionType(new QTypeGetRegions(this));
		registerAdditionalQuestionType(new QTypeGetHierarchicalRegions(this));
		return toReturn;
	}
	
	 public AgentFrameworkComponent getTecLayer() {	
		return tecLayer;		
	}
	 
	public void createMind() {
		if(!phenotypeCreated) {
			try {
				setCurrentAgentState("robotmouse5");
				phenotypeCreated = true;
			} catch (MicropsiException e) {
				e.printStackTrace();
			}
		}
	}
	 
	public void die(String reason) {
	    if (!hasDied) {
	    	MouseAgentManager.getInstance().deleteAgent(this);
	        hasDied = true;
        }
        super.die(reason);
	}
	 
	/**
	 * @return Returns the bodySimulator.
	 */
	public MouseBodySimulator getBodySimulator() {
		return bodySimulator;
	}
	
	/**
	 * @return Returns the position.
	 */
	public Position getPosition() {
		return position;
	}
	
	/**
	 * @param position The position to set.
	 */
	public void setPosition(Position position) {
		this.position = position;
	}
	
	public void createNet() {
	    try {
            this.setCurrentAgentState("robotmouse5");
        } catch (MicropsiException e) {
            e.printStackTrace();
        }
	}
	
	public void runNet() {
		setCycleDelay(ConstantValues.CYCLE_LENGTH);
		micropsi.resume();
	}
	
	public boolean isKnown(long ID) {
	    return knownAgents.containsKey(new Long(ID));
	}
	
	public int getOpinion(long ID) {
	    if(isKnown(ID))
	        return (((AgentRepresentation)knownAgents.get(new Long(ID))).getOpinion() > 0.0) ? 1 : -1;
	    else return 0;
	}
	
	public void metAgent(long ID, double opinion) {
	    if(knownAgents.containsKey(new Long(ID))) {
	        if(opinion < 0.0) {
	            ((AgentRepresentation)knownAgents.get(new Long(ID))).adjustNegative(-opinion);
	        } else {
	            ((AgentRepresentation)knownAgents.get(new Long(ID))).adjustPositive(opinion);
	        }
	    } else {
	        AgentRepresentation newAgent = new AgentRepresentation(ID);
	        if(opinion < 0.0) {
	            newAgent.adjustNegative(-opinion);
	        } else {
	            newAgent.adjustPositive(opinion);
	        }
	        knownAgents.put(new Long(ID), newAgent);
	    }
	}
	
	public void deleteAgentFromKnownAgents(long ID) {
	    if(isKnown(ID))
	        knownAgents.remove(new Long(ID));
	}
	
    /**
     * @return Returns the iD.
     */
    public long getID() {
        return ID;
    }
    
    /**
     * @param id The iD to set.
     */
    public void setID(long id) {
        ID = id;
    }
}
