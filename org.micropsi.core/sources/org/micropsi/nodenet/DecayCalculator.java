/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/DecayCalculator.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.nodenet;

/**
 * Central service for decay calculations.
 */
public class DecayCalculator {
		
	private static DecayIF decayInstance;
	
	/**
	 * Calculate the new weight of a link.
	 * @param decayType the decay type to be applied
	 * @param oldweight the old weight of the link
	 * @param steps the steps that passed by since the last calculation
	 * @param entity the NetEntity where the link originates
	 * @return double the new weight of the link
	 */
	public static double calculateWeigth(int decayType, double oldweight, long steps, NetEntity entity) {
		if(decayInstance == null || decayType == DecayIF.NO_DECAY) return oldweight;
		return decayInstance.calculateWeight(decayType,oldweight, steps,entity);
	}
	
	/**
	 * Sets the implementation if DecayIF to be used for the calculation of
	 * decays.
	 * @param newDecayCalculator the decay implementation
	 */
	public static void setDecay(DecayIF newDecayCalculator) {
		decayInstance = newDecayCalculator;
	}


}
