/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/NetParametersIF.java,v 1.4 2005/09/23 09:35:11 vuine Exp $
 */
package org.micropsi.nodenet;

/**
 * This interface contains parameter types applicable in the
 * changeParameter(...) methods of NetEntities and NetFacadeIF implementations.
 */
public interface NetParametersIF {
	
	/**
	 * The NetEntity's name
	 */
	public static final int PARM_ENTITY_NAME = 2;
	
	/**
	 * The activation of one of the NetEntity's slots
	 */
	public static final int PARM_ENTITY_SLOT_ACTIVATION = 3;
	
	/**
	 * The activation of one of the NetEntity's gates
	 */
	public static final int PARM_ENTITY_GATE_ACTIVATION = 4;
	
	/**
	 * The gate factor of one of the NetEntity's gates. (The gate factor will be
	 * multiplied with the gate's activation just before comparision with the
	 * gate's threshold. Gates set their gate factors to a default value after
	 * they propagated the activation. This is normally 1.)
	 */
	public static final int PARM_ENTITY_GATE_FACTOR = 5;
	
	/**
	 * The amplification factor of one of the NetEntity's gates. (The amp factor
	 * will be multiplied with the activation of the gate after the comparision
	 * with the threshold and therefore directly causes higher activation in all
	 * linked gates (if the activation times the amp does not exceed the
	 * maximum of the gate.)
	 */
	public static final int PARM_ENTITY_GATE_AMP = 6;
	
	/**
	 * A parameter of an entity gate output function.
	 * (Use PARAM=VALUE as value). 
	 */
	public static final int PARM_ENTITY_GATE_OUTPUTFUNCTION_PARAMETER = 7;
	
	/**
	 * The maximum output of one of the NetEntity's gates.
	 */
	public static final int PARM_ENTITY_GATE_MAX = 8;
	
	/**
	 * The minimum output of one of the NetEntity's gates.
	 */
	public static final int PARM_ENTITY_GATE_MIN = 9;
	
	/**
	 * The type of decay to be applied to the gate's links
	 */
	public static final int PARM_ENTITY_GATE_DECAYTYPE = 10;

	/**
	 * The dissociation constant of a NodeSpaceModule
	 */
	public static final int PARM_NODESPACE_DISSO = 11;

	/**
	 * The net's learning constant
	 */
	public static final int PARM_NODESPACE_ASSO = 12;
	
	/**
	 * The net's constant for strengthening links that were used and caused
	 * activation.
	 */
	public static final int PARM_NODESPACE_STRENGTHENING = 13;

	/**
	 * Boolean parameter: Should gates of nodes directly contained in a nodespace
	 * undergo decay? (Does the decay type at gates have any effect?)
	 */
	public static final int PARM_NODESPACE_DECAYALLOWED = 15;
	
	/**
	 * The output function to be used in a gate
	 */
	public static final int PARM_ENTITY_GATE_OUTPUTFUNCTION = 14;

}
