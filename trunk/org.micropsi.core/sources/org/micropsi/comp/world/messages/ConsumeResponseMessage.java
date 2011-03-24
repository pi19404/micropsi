package org.micropsi.comp.world.messages;

import org.micropsi.comp.world.objects.AbstractObjectPart;

/**
 * @author Gregor
 *
 */

public class ConsumeResponseMessage extends WorldMessage {
	private double eatenKiloJoules;
	private double drunkLiters; 
	private int receivedDamage;

	/**
	 * Constructor ConsumeResponseMessage.
	 * @param i
	 * @param i1
	 */
	public ConsumeResponseMessage(double eatenKiloJoules, double drunkLiters, int receivedDamage, AbstractObjectPart sender) {
		super("AGENTACTION_RESPONSE", "CONSUMERESPONSE", sender);
		this.eatenKiloJoules = eatenKiloJoules;
		this.drunkLiters = drunkLiters;
		this.receivedDamage = receivedDamage;
	}
	
	public String toString() {
		return	super.toString()
				+ "drunkLiters: "
				+ drunkLiters
				+ "\n"
				+ "eatenKiloJoules: "
				+ eatenKiloJoules
				+ "\n"
				+ "receivedDamage: "
				+ receivedDamage
				+ "\n";
	}

	/**
	 * Returns the drunkLiters.
	 * @return int
	 */
	public double getDrunkLiters() {
		return drunkLiters;
	}

	/**
	 * Returns the eatenKiloJoules.
	 * @return int
	 */
	public double getEatenKiloJoules() {
		return eatenKiloJoules;
	}

	/**
	 * Returns the receivedDamage.
	 * @return int
	 */
	public int getReceivedDamage() {
		return receivedDamage;
	}

}
