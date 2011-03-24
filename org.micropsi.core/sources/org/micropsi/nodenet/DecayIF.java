/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/DecayIF.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.nodenet;

/**
 * Implementations of this interface calculate the decay of some link's weights.
 */
public interface DecayIF {
	
	/**
	 * No decay: the new weight will be the old one.
	 */
	public static final int NO_DECAY = -1;
		
	/**
	 * Calculates and returns the new weight for a link, deciding on the
	 * decayType on the formula to be applied. The calculation can be based on
	 * the old weight of the link and the steps that went by since the last
	 * calculation of that weight.
	 * @param decayType the type of decay to be applied to the link's weight.
	 * @param oldweight the old weight of the link
	 * @param steps steps since last calculation
	 * @param entity the NetEntity the link belongs to
	 * @return double the new weight for the link
	 */
	public double calculateWeight(int decayType, double oldweight, long steps, NetEntity entity);

}
