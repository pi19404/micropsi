/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/AssociatorNode.java,v 1.6 2006/06/27 19:35:44 rvuine Exp $
 */
package org.micropsi.nodenet;

import java.util.Iterator;


/**
 * Associates the currently active gates of nodes within the current nodespace
 * with the slots of the nodes that are linked to the ASSO gate of the associator.
 * The link strength is calculated as
 * w_source_target = sqrt(w_source_target + act_source * act_associator 
 *                   * w_associator_target * act_gen_target * assoConst)^2
 * Warning: the gen gates of associators themselves do not transmit activation.
 */
public class AssociatorNode extends Node {

	private NodeSpaceModule space;
	private NetEntityManager manager; 

	/**
	 * @see org.micropsi.nodenet.NetEntity#NetEntity(String, NetEntityManager)
	 */
	protected AssociatorNode(String nodeID, NetEntityManager manager) {
		super(NodeFunctionalTypesIF.NT_ASSOCIATOR,nodeID,manager,DecayIF.NO_DECAY);
		try {
			addGate(new Gate(GT_ASSOCIATION,this,DecayIF.NO_DECAY));			
		} catch (NetIntegrityException e) {
			// won't happen
			manager.getLogger().error("Strange error.", e);
		}
		
		getGate(GT_ASSOCIATION).setGateFactor(0);
		this.manager = manager;
	}
	
	/**
	 * @see org.micropsi.nodenet.Node#use()
	 */
	protected void use() throws NetIntegrityException {
		Iterator iter = this.getGate(GateTypesIF.GT_ASSOCIATION).getLinks();
		while(iter.hasNext()) {
		    Link toAssociate = (Link)iter.next();
			NetEntity to = manager.getEntity(toAssociate.getLinkedEntityID()); 
			int toSlot = toAssociate.getLinkedSlot().getType();
			// if(to.getEntityType() != NetEntityTypesIF.ET_NODE) continue;
			Iterator iter2 = space.getActiveEntityIDs();
			while(iter2.hasNext()) {
				NetEntity from = manager.getEntity((String)iter2.next());
				// if(from.getEntityType() != NetEntityTypesIF.ET_NODE) continue;
				if(from == to) continue;
				if(from == this || to == this) continue;
				// make sure that source is not one of the targets and no direct successor
				Iterator iter3 = from.getSlots();
				while(iter3.hasNext()) {
				    Slot tempSlot = (Slot)iter3.next();
				    Link existing = this.getGate(GateTypesIF.GT_ASSOCIATION).getLinkTo(from.getID(),tempSlot.getType());
				    if (existing == null) {
				        existing = this.getGate(GateTypesIF.GT_GEN).getLinkTo(from.getID(),tempSlot.getType());
				    }
				    if (existing == null) { 
				        associateActiveGates(from,to,toSlot,this.getGenActivation(),1.0,1);//space.getLearningConstant());
				    }
				}
			}
		}
	}
	
	/**
	 * Associates all active Gates of the node with a slot of a second node.
	 * "Associate" means: If there already is a link, it will be strengthened,
	 * if there is no link, a new link will be created. 
	 * @param source the node to link from
	 * @param target the node to be linked
	 * @param slotType the slot to be linked
	 * @param assoActivation the activation of the associator node 
	 * @param assoWeight additional associator weight
	 * @throws NetIntegrityException if one of the existing links is bad or the
	 * target node does not exist.
	 */
	protected void associateActiveGates(NetEntity source, NetEntity target, 
	        int targetSlotType, double assoActivation, double assoWeight, double assoConst) 
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
			double newLinkStrength = Math.sqrt(Math.abs(existingStrength 
	            + sourceGate.getConfirmedActivation()*targetActivation
	              *assoWeight*assoActivation*assoConst));
			newLinkStrength *= newLinkStrength;
			if (existing != null) {
			    if (newLinkStrength == 0) {
			        source.deleteLink(sourceGateType, target.getID(),targetSlotType);
			    } else {
			        existing.setWeight(newLinkStrength);
			    }
			} else if (newLinkStrength!=0) {
			    source.createLinkTo(target.getID(),targetSlotType, sourceGate, 
				   newLinkStrength, 1.0);
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