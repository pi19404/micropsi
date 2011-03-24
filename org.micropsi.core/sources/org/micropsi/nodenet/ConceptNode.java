/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/ConceptNode.java,v 1.3 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.nodenet;

import java.util.Iterator;

/**
 * The most common node inside MicroPSI nets. Universally usable for scripts,
 * plans, and memory. And so much more. Hopefully.
 */
public class ConceptNode extends Node {

	/**
	 * Constructor, setting the decay type to be applied to attached links.
	 * @param nodeID the ID of this Node.
	 * @param manager the entity manager.
	 * @param decayType the decay type.
	 * @see DecayIF
	 */
	protected ConceptNode(String nodeID, NetEntityManager manager, int decayType) {
		super(NodeFunctionalTypesIF.NT_CONCEPT,nodeID,manager,decayType);
		try {
			addGate(new Gate(GT_POR,this,decayType));
			addGate(new Gate(GT_RET,this,decayType));
			addGate(new Gate(GT_SUR,this,decayType));
			addGate(new Gate(GT_SUB,this,decayType));
			addGate(new Gate(GT_CAT,this,decayType));
			addGate(new Gate(GT_EXP,this,decayType));
			addGate(new Gate(GT_SYM,this,decayType));
			addGate(new Gate(GT_REF,this,decayType));			
		} catch (NetIntegrityException e) {
			// won't happen
			manager.getLogger().error("Strange error.", e);
		}
		
		getGate(GT_POR).setGateFactor(0);
		getGate(GT_RET).setGateFactor(0);
		getGate(GT_SUB).setGateFactor(0);
		getGate(GT_SUR).setGateFactor(0);
		getGate(GT_CAT).setGateFactor(0);
		getGate(GT_EXP).setGateFactor(0);
		getGate(GT_SYM).setGateFactor(0);
		getGate(GT_REF).setGateFactor(0);
	}
	
	/**
	 * ConceptNodes strengthen their links when these have been used and caused
	 * activation in the previous cycle.
	 * 
	 * @see org.micropsi.nodenet.NetEntity#calculateGates()
	 */
	protected void calculateGates() throws NetIntegrityException {
		super.calculateGates();
		
		NodeSpaceModule parent = (NodeSpaceModule)entityManager.getEntity(getParentID()); 
		if(parent == null) return;
		
		if(parent.getStrengtheningConstant() == 1) return;
				
		// confirm strengthening of links that were used and caused this node to be active
		Iterator iter = genSlot.getIncomingLinks();
		while(iter.hasNext()) {
			Link l = (Link)iter.next();
			if(l.wasUsed()) {				
				double linkweight = l.getWeight();

				boolean isNegative = false;
				if(linkweight < 0) {
					isNegative = true;
					linkweight = Math.abs(linkweight);
				}
	
				linkweight = Math.sqrt(linkweight) + this.getGenActivation() * parent.getStrengtheningConstant();
				linkweight *= linkweight;

				if(isNegative) linkweight *= -1;

				l.setWeight(linkweight);
				l.setUsed(false);
			}
		}
			
	}
	
	/**
	 * Empty!
	 * @see org.micropsi.nodenet.Node#use()
	 */
	public void use() {
	}
								
}