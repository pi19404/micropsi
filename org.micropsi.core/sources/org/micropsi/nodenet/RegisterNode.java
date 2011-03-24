/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/RegisterNode.java,v 1.3 2004/11/24 16:34:54 vuine Exp $
 */
package org.micropsi.nodenet;

/**
 * Register nodes are the most simple nodes - just the minimum number of gates
 * and slots (1 each), no use()-Functionality. 
 */
public class RegisterNode extends Node {

	/**
	 * @see org.micropsi.nodenet.NetEntity#NetEntity(String, NetEntityManager)
	 */
	protected RegisterNode(String nodeID, NetEntityManager manager) {
		super(NodeFunctionalTypesIF.NT_REGISTER,nodeID,manager,DecayIF.NO_DECAY);
	}
	
	/**
	 * @see org.micropsi.nodenet.Node#use()
	 */
	protected void use() {
	}	

}
