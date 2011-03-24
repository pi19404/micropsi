/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/ext/StandardDecays.java,v 1.2 2004/08/10 14:38:18 fuessel Exp $ 
 */
package org.micropsi.nodenet.ext;

import org.micropsi.nodenet.DecayIF;
import org.micropsi.nodenet.NetEntity;


public class StandardDecays implements DecayIF {

	/**
	 * Linear decay means that the link's weight will be reduced by a fixed 
	 * amount each cycle until it reaches zero.
	 * Fast linear decay reduces the weight by 0.05 each cycle.
	 *  
	 */
	public static final int DT_FASTEST_LINEAR = 7;
	
	public static final int DT_FASTER_LINEAR = 1;
	
	public static final int DT_FAST_LINEAR = 2;
	
	public static final int DT_AVERAGE_LINEAR = 3;
	
	public static final int DT_SLOW_LINEAR = 4;
	
	public static final int DT_SLOWER_LINEAR = 5;
	
	public static final int DT_SLOWEST_LINEAR = 6;


	/**
	 * @see org.micropsi.nodenet.DecayIF#calculateWeight(int, double, long, NetEntity)
	 */
	public double calculateWeight(int decayType, double oldweight, long steps, NetEntity entity) {		
		double spd = 1;
			
/*		if(entity.getEntityType() == NetEntityTypesIF.ET_NODE) {
			Node n = (Node)entity;
			if(n.getEntityType() == NodeFunctionalTypesIF.NT_CONCEPT) {
				if(n.getGate(GateTypesIF.GT_SUB).hasLinks())
					decayType = NO_DECAY;
			}
		}*/
		
		switch(decayType) {
			case NO_DECAY:
				return oldweight;
			case DT_FASTEST_LINEAR:
				spd = 0.05;
				break;
			case DT_FASTER_LINEAR:
				spd = 0.01;
				break;
			case DT_FAST_LINEAR:
				spd = 0.005;
				break;
			case DT_AVERAGE_LINEAR:
				spd = 0.001;
				break;
			case DT_SLOW_LINEAR:
				spd = 0.0001;
				break;								
			case DT_SLOWER_LINEAR:
				spd = 0.00001;
				break;
			case DT_SLOWEST_LINEAR:
				spd = 0.00001;
				break;
		}
				
		double tmp = oldweight;
		tmp -= (steps * spd);
		return (tmp > 0 ? tmp : 0);
	}

}
