/*
 * Created on 21.04.2005
 */

package org.micropsi.comp.agent;

/**
 * @author Markus
 */
public class AgentRepresentation {
    private long ID;
    private double negative;
    private double positive;
    
    public AgentRepresentation(long ID) {
        this.ID = ID;
        this.negative = 0.0;
        this.positive = 0.0;
    }
    
    /**
     * @param adjustment is substracted from the negative variable
     */
    public void adjustNegative(double adjustment) {
        negative -= adjustment;
    }
    
    /**
     * @param adjustment is added to the positive variable
     */
    public void adjustPositive(double adjustment) {
        positive += adjustment;
    }
    
    /**
     * @return the sum of positive and negative experiences with the agent
     */
    public double getOpinion() {
        return positive + negative;
    }

	public long getID() {
		return ID;
	}  
}
