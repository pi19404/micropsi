/*
 * Created on 09.04.2005
 *
 */
package org.micropsi.comp.agent.urges;

import org.apache.log4j.Logger;
import org.micropsi.comp.ConstantValues;
import org.micropsi.comp.agent.MouseMicroPsiAgent;
import org.micropsi.comp.agent.aaa.UrgeCreatorIF;
import org.micropsi.nodenet.CycleObserverIF;
import org.micropsi.nodenet.LocalNetFacade;

/**
 * @author Markus
 *
 */
public class MouseBodySimulator implements CycleObserverIF {
    
//    private Logger logger;
    private MouseMicroPsiAgent agent;
    private double foodLevel;
    private double waterLevel;
    private double hitpoints;
    private double affiliation;
    private double lifetime;
    
//    private double maxAge;
    private double maxFood;
    private double maxWater;

    private boolean groundTypeIsFood = false;
    private boolean groundTypeIsWater = false;
    private boolean groundTypeIsPoison = false;
    private boolean groundTypeIsHealing = false;
    private boolean smiled = false;
    
    public MouseBodySimulator(MouseMicroPsiAgent agent, Logger logger) {
        this.agent = agent;
//        this.logger = logger;
        this.hitpoints = 100;
        this.affiliation = 100;
        this.foodLevel = ConstantValues.MAX_FOOD;
        this.waterLevel = ConstantValues.MAX_WATER;
        this.lifetime = 0;
        
        this.maxFood = ConstantValues.MAX_FOOD;
        this.maxWater = ConstantValues.MAX_WATER;
    }
    
    /**
	 * Damages the agent
	 * @param damagePoints the damage taken
	 */
	public void damage (double damagePoints) {
		hitpoints -= damagePoints;
		if (hitpoints <= 0) {
			agent.die(this.toString());
		}
	}
	
	/**
	 * new startCycle with automatic actions
	 */
	public void startCycle(long netStep) {
        if ((foodLevel -= ConstantValues.FOOD_DECAY) < 0) {
            foodLevel = 0;
            //damage(hitpoints + 1);
        }
        
        if ((waterLevel -= ConstantValues.FOOD_DECAY) < 0) {
            waterLevel = 0;
            //damage(hitpoints + 1);
        }
        
        if ((affiliation -= 0.025) < 25) {
            affiliation = 25;
        }
        
        /*
        if ((hitpoints += ConstantValues.HEALING_RATE) > 100) {
            hitpoints = 100;
        }
        */
        
        if(groundTypeIsFood)
        	foodLevel = maxFood;
        else if(groundTypeIsWater)
        	waterLevel = maxWater;
        else if(groundTypeIsPoison)
        	damage(0.01);
        else if(groundTypeIsHealing)
        	hitpoints = 100;
        
        if(smiled) {
        	affiliation += ConstantValues.AFFILIATION_GAIN;
        	if(affiliation > 100)
        		affiliation = 100;
        	smiled = false;
        }
        
        lifetime++;
    }

    /* (non-Javadoc)
     * @see org.micropsi.nodenet.CycleObserverIF#endCycle(long)
     */
    public void endCycle(long netStep) {
    }

    public double getFoodUrge() {
        return (maxFood - foodLevel) / maxFood;
    }
    
    public double getWaterUrge() {
        return (maxWater - waterLevel) / maxWater;
    }
    
    public double getIntegrityUrge() {
        return (100.0 - hitpoints) / 100.0;
    }
    
    public double getAffiliationUrge() {
        return (100.0 - affiliation) / 100.0;
    }
    
    public UrgeCreatorIF[] createUrges(LocalNetFacade net) {
		UrgeCreatorIF[] urges = new UrgeCreatorIF[4];
		
		urges[0] = new FoodUrge(this, net);
		urges[1] = new WaterUrge(this, net);
		urges[2] = new IntegrityUrge(this, net);
		urges[3] = new AffiliationUrge(this, net);
		return urges;
	}
   
    public void setMaxFood(double maxFood) {
        this.maxFood = maxFood;
    }
    
    public void setMaxWater(double maxWater) {
    	this.maxWater = maxWater;
    }
    
    public double getFoodLevel() {
        return foodLevel;
    }
    
    public double getWaterLevel() {
    	return waterLevel;
    }
    
    public double getAffiliation() {
        return affiliation;
    }
    
    /**
     * @return age
     */
    public double getLifetime() {
        return lifetime;
    }
    
    public void setGroundTypeIsFood(boolean groundTypeIsFood) {
        this.groundTypeIsFood = groundTypeIsFood;
    }
    
    public void setGroundTypeIsHealing(boolean groundTypeIsHealing) {
        this.groundTypeIsHealing = groundTypeIsHealing;
    }
    
    public void setGroundTypeIsPoison(boolean groundTypeIsPoison) {
        this.groundTypeIsPoison = groundTypeIsPoison;
    }
    
    public void setGroundTypeIsWater(boolean groundTypeIsWater) {
        this.groundTypeIsWater = groundTypeIsWater;
    }
    
    
    public void setSmiled(boolean smiled) {
    	this.smiled = smiled;
    }
}

