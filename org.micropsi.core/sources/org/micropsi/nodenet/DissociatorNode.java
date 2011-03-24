/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/DissociatorNode.java,v 1.6 2006/06/27 19:35:44 rvuine Exp $
 */
package org.micropsi.nodenet;

import java.util.Iterator;

/**
 * Complementary to the AssociatorNodes, DissociatorNodes weaken or delete Links
 * between all Nodes within one NodeSpace that have active Gates and the slots that
 * are connected to the dissociator.
 */
public class DissociatorNode extends Node {

	private NodeSpaceModule space;
	private NetEntityManager manager;

	/**
	 * @see org.micropsi.nodenet.NetEntity#NetEntity(String, NetEntityManager)
	 */
	protected DissociatorNode(String nodeID, NetEntityManager manager) {
		super(NodeFunctionalTypesIF.NT_DISSOCIATOR,nodeID,manager,DecayIF.NO_DECAY);
		try {
			addGate(new Gate(GT_DISSOCIATION,this,DecayIF.NO_DECAY));			
		} catch (NetIntegrityException e) {
			// won't happen
			manager.getLogger().error("Strange error.", e);
		}
		
		getGate(GT_DISSOCIATION).setGateFactor(0);
		this.manager = manager;
	}

	/**
	 * @see org.micropsi.nodenet.Node#use()
	 */
	protected void use() throws NetIntegrityException {
		Iterator iter = this.getGate(GateTypesIF.GT_DISSOCIATION).getLinks();
		while(iter.hasNext()) {
		    Link toDissociate = (Link)iter.next();
			NetEntity to = manager.getEntity(toDissociate.getLinkedEntityID()); 
			int toSlot = toDissociate.getLinkedSlot().getType();
			Iterator iter2 = space.getActiveEntityIDs();
			while(iter2.hasNext()) {
				NetEntity from = manager.getEntity((String)iter2.next());
				if(from == to) continue;
				if(from == this || to == this) continue;
				// make sure that source is not one of the targets and no direct successor
				Iterator iter3 = from.getSlots();
				while(iter3.hasNext()) {
				    Slot tempSlot = (Slot)iter3.next();
				    Link existing = this.getGate(GateTypesIF.GT_DISSOCIATION).getLinkTo(from.getID(),tempSlot.getType());
				    if (existing == null) {
				        existing = this.getGate(GateTypesIF.GT_GEN).getLinkTo(from.getID(),tempSlot.getType());
				    }
				    if (existing == null) { 
				        dissociateActiveGates(from,to,toSlot,this.getGenActivation(),1.0,space.getDissociationConstant());
				    }
				}
			}
		}
	}
	
	/**
	 * Dissociates all active Gates of the node with a slot of a second node.
	 * "Dissociate" means: existing links are weakened or removed.
	 * @param source the node to link from
	 * @param target the node to be linked
	 * @param slotType the slot to be linked
	 * @param dissoActivation the activation of the dissociator node 
	 * @param dissoWeight additional dissociator weight
	 * @throws NetIntegrityException if one of the existing links is bad or the
	 * target node does not exist.
	 */
	protected void dissociateActiveGates(NetEntity source, NetEntity target, 
	        int targetSlotType, double dissoActivation, double dissoWeight, double dissoConst) 
			throws NetIntegrityException {

	    double targetActivation = 1.0;
	    if (target.getGate(GateTypesIF.GT_GEN)!=null) {
	        targetActivation = target.getGate(GateTypesIF.GT_GEN).getConfirmedActivation();
	    }
	    
	    Iterator iter = source.getGates();
	    while (iter.hasNext()) {
	        Gate sourceGate = (Gate)iter.next();
	        if (!sourceGate.isActive()) continue;
	        int sourceGateType = sourceGate.getType();
	        
	        //check if source and target are linked already		
	        double existingStrength=0;
			Link existing = sourceGate.getLinkTo(target.getID(),targetSlotType);
			if (existing != null) {
				existingStrength=existing.getWeight();
			}
			double newLinkStrength = Math.sqrt(Math.max(0,existingStrength 
	            - sourceGate.getConfirmedActivation()*targetActivation
	              *dissoWeight*dissoActivation*dissoConst));
			newLinkStrength *= newLinkStrength;
			if (existing != null) {
			    if (newLinkStrength == 0) {
			        source.deleteLink(sourceGateType, target.getID(),targetSlotType);
			    } else {
			        existing.setWeight(newLinkStrength);
			    }
			} 
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
