/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/urges/BodySimulator.java,v 1.7 2005/11/15 23:13:42 vuine Exp $
 */
package org.micropsi.comp.agent.micropsi.urges;

import org.micropsi.comp.agent.aaa.UrgeCreatorIF;
import org.micropsi.nodenet.CycleObserverIF;
import org.micropsi.nodenet.LocalNetFacade;

public class BodySimulator implements CycleObserverIF {
	
	public static final double litersToPercent = 10;
	public static final double kilojouleToPercent = 0.5;
		
	public static final double FOOD_DECAY = 0.9999;
	public static final double WATER_DECAY = 0.9999;
	
	private double foodLevel;

	private double waterLevel;
	
	private double integrityLevel;
		
	private PhysicalStateListenerIF physicalstate;
	
	/**
	 * Constructor for BodySimulator.
	 */
	public BodySimulator (PhysicalStateListenerIF physicalstate) {
		this.physicalstate = physicalstate;
		foodLevel = 100;
		waterLevel = 100;
		integrityLevel = 100;
	}

	/**
	 * Returns the foodUrge.
	 * @return double
	 */
	public double getFoodUrge() {
		return (100 - foodLevel) / 100;
	}

	/**
	 * Returns the integrityUrge.
	 * @return double
	 */
	public double getIntegrityUrge() {
		return (100 - integrityLevel) / 100;
	}

	/**
	 * Returns the waterUrge.
	 * @return double
	 */
	public double getWaterUrge() {
		return (100 - waterLevel) / 100;
	}

	/**
	 * Consumes food
	 * @param kiloJoule food value that was eaten
	 */
	public void eat (double kiloJoule) {
		foodLevel += (kiloJoule * kilojouleToPercent);
		if(foodLevel < 0) {
			physicalstate.die("[starved] "+this.toString());
		}

	}

	/**
	 * Consumes water
	 * @param liters water value that was drunken
	 */
	public void drink (double liters) {
		waterLevel += (liters * litersToPercent);
		if(waterLevel < 0) {
			physicalstate.die("[died with thirst] "+this.toString());
		}
	}

	/**
	 * Damages the agent
	 * @param damagePoints the damage taken
	 */
	public void damage (double damagePoints) {
		integrityLevel -= damagePoints;
		if (integrityLevel < 0) {
			physicalstate.die("[killed] "+this.toString());
		}
	}
	
	public String toString () {
		return "foodLevel: " + foodLevel +
				" waterLevel: " + waterLevel +
				" integrityLevel: " + integrityLevel;
	}
	
	/**
	 * @see org.micropsi.nodenet.CycleObserverIF#startCycle(long)
	 */
	public void startCycle(long netStep) {
		adjustIntegrity ();
		adjustFoodLevel ();
		adjustWaterLevel ();
	}
	
	/**
	 * Method adjustLiquidityLevel.
	 */
	private void adjustWaterLevel() {
		waterLevel *= WATER_DECAY;	
	}
	
	/**
	 * Method adjustFoodLevel.
	 */
	private void adjustFoodLevel() {
		foodLevel *= FOOD_DECAY;
	}
	
	/**
	 * Method adjustIntegrity.
	 */
	private void adjustIntegrity() {
	}
	
	/**
	 * @see org.micropsi.nodenet.CycleObserverIF#endCycle(long)
	 */
	public void endCycle(long netStep) {
	}

	/**
	 * @return double
	 */
	public double getFoodLevel() {
		return foodLevel;
	}

	/**
	 * @return double
	 */
	public double getIntegrityLevel() {
		return integrityLevel;
	}

	/**
	 * @return double
	 */
	public double getWaterLevel() {
		return waterLevel;
	}
	
	public UrgeCreatorIF[] createUrges(LocalNetFacade net) {
		UrgeCreatorIF[] urges = new UrgeCreatorIF[3];
		
		urges[0] = new BodyDataProvider(BodyDataProvider.UFOOD,this,net);
		urges[1] = new BodyDataProvider(BodyDataProvider.UINTEGRITY,this,net);
		urges[2] = new BodyDataProvider(BodyDataProvider.UWATER,this,net);
		
		return urges;
	}

	public void reset() {
		foodLevel = 100;
		waterLevel = 100;
		integrityLevel = 100;
	}	

}
