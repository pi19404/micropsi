/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/ActLinkTypeNode.java,v 1.5 2006/08/03 15:40:37 rvuine Exp $
 */
package org.micropsi.nodenet;

import java.util.Iterator;

/**
 * ActLinkTypeNodes activate one specific gate of all nodes within their
 * NodeSpace by setting the gate factor of the gates to a value > 0. (Normally,
 * all gates of Nodes except the GEN gate have the gate factor zero and hence do
 * not get active. But if the corresponding ActLinkTypeNode is active, its
 * activation will be the gate factor for the gates in this cycle. Note that
 * gate factors are automatically reset to zero at the end of the cycle by the
 * Nodes.)
 */
public class ActLinkTypeNode extends Node {

	private int linkTypeToActivate;
	private NodeSpaceModule space;
	private NetEntityManager manager;
	
	/**
	 * Constructor, setting the type of Gate that the ActLinkTypeNode will
	 * enable when active.
	 * @param nodeType	the type of the Node (one of the ACT_* types)
	 * @param typeToActivate the type of the Gate to activate
	 * @param nodeID the ID of this node
	 * @param manager the manager
	 */
	protected ActLinkTypeNode(int nodeType, int typeToActivate, String nodeID, NetEntityManager manager) {
		super(nodeType,nodeID,manager,DecayIF.NO_DECAY);
		this.linkTypeToActivate = typeToActivate;
		this.manager = manager;
	}
	
	/**
	 * @see org.micropsi.nodenet.NetEntity#calculateGates()
	 */
	protected void calculateGates() throws NetIntegrityException {
		super.calculateGates();
		
		if(!isActive()) return;
		
		Iterator iter = space.getAllEntities();
		while(iter.hasNext()) {
			NetEntity entity = (NetEntity)iter.next();
			if(entity.getEntityType() != NetEntityTypesIF.ET_NODE) continue;
			Node node = (Node)entity;
			if(	node.getType() != NodeFunctionalTypesIF.NT_CONCEPT && node.getType() != NodeFunctionalTypesIF.NT_TOPO) continue;
								
			Gate g = node.getGate(linkTypeToActivate);
			if(g != null) {
				g.setGateFactor(getGenActivation());
				manager.reportActiveEntity(node);
			} 
		}
	}
	
	/**
	 * Sets the space to act upon
	 * @param space the NodeSpace to set
	 */
	protected void setSpace(NodeSpaceModule space) {
		this.space = space;
	}

	/**
	 * @see org.micropsi.nodenet.Node#use()
	 */
	protected void use() throws NetIntegrityException {
	}
		
}
