/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/DeactivatorNode.java,v 1.4 2006/08/03 15:40:37 rvuine Exp $
 */
package org.micropsi.nodenet;

import java.util.Iterator;

/**
 * Complementary to ActivatorNodes, DeactivatorNodes lower the activation on all
 * slots in the NodeSpace they are attached to. When activated, DeactivatorNodes
 * take the amount of their own activation out of the slots of the Nodes in the
 * NodeSpace.
 */
public class DeactivatorNode extends Node {

	private NodeSpaceModule space;

	/**
	 * @see org.micropsi.nodenet.NetEntity#NetEntity(String, NetEntityManager)
	 */
	protected DeactivatorNode(String nodeID, NetEntityManager manager) {
		super(NodeFunctionalTypesIF.NT_DEACTIVATOR,nodeID,manager,DecayIF.NO_DECAY);
	}

	/**
	 * @see org.micropsi.nodenet.Node#use()
	 */
	protected void use() throws NetIntegrityException {
		
		//TODO: Ronnie: Rethink this. What about chunks?
		
		Iterator iter = space.getAllEntities();
		while(iter.hasNext()) {
			NetEntity entity = (NetEntity)iter.next();
			if(entity.getEntityType() != NetEntityTypesIF.ET_NODE) continue;
			Node node = (Node)entity; 
			if(	node.getType() != NodeFunctionalTypesIF.NT_CONCEPT && node.getType() != NodeFunctionalTypesIF.NT_TOPO) continue;
			
			node.getSlot(SlotTypesIF.ST_GEN).takeActivationGreaterThanZero(this.getGenActivation());
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
