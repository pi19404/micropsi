/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/NodeFunctionalTypesIF.java,v 1.4 2006/08/03 15:40:37 rvuine Exp $
 */
package org.micropsi.nodenet;

/**
 * Possible return values of Node.getType()
 * 
 * @see org.micropsi.nodenet.Node
 */
public interface NodeFunctionalTypesIF {

	/**
	 * Concept node (9 gates)
	 */
	public static final int NT_CONCEPT = 0;
	
	/**
	 * Chunk node
	 */
	public static final int NT_CHUNK = 1;
	
	/**
	 * Topo node (like CONCEPT, but mith MAX instead of SUM at the Slot)
	 */
	public static final int NT_TOPO = 2;
	
	/**
	 * Directional activator for POR gates  
	 */
	public static final int NT_ACT_POR = 10;
	
	/**
	 * Directional activator for RET gates
	 */
	public static final int NT_ACT_RET = 11;
	
	/**
	 * Directional activator for SUR gates
	 */
	public static final int NT_ACT_SUR = 12;
	
	/**
	 * Directional activator for SUB gates
	 */	
	public static final int NT_ACT_SUB = 13;
	
	/**
	 * Directional activator for CAT gates
	 */
	public static final int NT_ACT_CAT = 14;
	
	/**
	 * Directional activator for EXP gates
	 */
	public static final int NT_ACT_EXP = 15;
	
	/**
	 * Directional activator for SYM gates
	 */
	public static final int NT_ACT_SYM = 16;
	
	/**
	 * Directional activator for REF gates
	 */
	public static final int NT_ACT_REF = 17;	
	
	/**
	 * Associator node
	 */
	public static final int NT_ASSOCIATOR = 20;
	
	/**
	 * Dissociator node
	 */
	public static final int NT_DISSOCIATOR = 21;
	
	
	/**
	 * Register node. 1 gate, no extra use()-functionality
	 */
	public static final int NT_REGISTER = 30;
	
	
	/**
	 * General activator node
	 */
	public static final int NT_ACTIVATOR = 40;
	
	/**
	 * Deneral deactivator node
	 */
	public static final int NT_DEACTIVATOR = 41;
	
	
	/**
	 * Sensor node. 1 gate, activated by the registered DataSource
	 */
	public static final int NT_SENSOR = 50;
	
	/**
	 * Actor node.
	 */
	public static final int NT_ACTOR = 51;

}
