/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/ActivatorNode.java,v 1.4 2006/08/03 15:40:37 rvuine Exp $
 */
package org.micropsi.nodenet;

import java.util.Iterator;

/**
 * An ActivatorNode can be used to increase the activation of all Nodes within
 * one NodeSpace. As NodeSpaces are hierarchical, this will also affect all
 * Nodes in the nested NodeSpaces. ActivatorNodes put, when activated, their
 * activation into the slots of all Nodes of their NodeSpace.
 */
public class ActivatorNode extends Node {

	private NodeSpaceModule space;

	/**
	 * @see org.micropsi.nodenet.NetEntity#NetEntity(String, NetEntityManager)
	 */
	protected ActivatorNode(String nodeID, NetEntityManager manager) {
		super(NodeFunctionalTypesIF.NT_ACTIVATOR,nodeID,manager,DecayIF.NO_DECAY);
	}
	
	/**
	 * @see org.micropsi.nodenet.Node#use()
	 */
	protected void use() throws NetIntegrityException {
		Iterator iter = space.getAllEntities();
		while(iter.hasNext()) {
			NetEntity entity = (NetEntity)iter.next();
			if(entity.getEntityType() != NetEntityTypesIF.ET_NODE) continue; 
			Node node = (Node)entity;
			if(	node.getType() != NodeFunctionalTypesIF.NT_CONCEPT && node.getType() != NodeFunctionalTypesIF.NT_TOPO) continue;

			node.getSlot(SlotTypesIF.ST_GEN).putActivation(this.getGenActivation());
		}
	}
	
	/**
	 * Sets the space.
	 * @param space The space to set
	 */
	public void setSpace(NodeSpaceModule space) {
		this.space = space;
	}

}
