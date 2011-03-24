/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/Node.java,v 1.5 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.nodenet;

/**
 * Nodes are entities with one slot and up to 9 gates. Nodes, coming in
 * different flavors, are used to program so called "node-scripts", which
 * provide a means of "neuronal programming". Unlike modules, every node has at
 * least (and exaclty) one slot and one gate (or more). How many gates there are
 * actually depends on the implementation of the abstract class "Node".
 */
public abstract class Node extends NetEntity implements GateTypesIF {

	private int type;
	
	/**
	 * The first, always-present gate of the node, "genericLinks", of type
	 * GT_GEN
	 * @see GateTypesIF
	 */
	protected Gate genericLinks;
	
	/**
	 * The first and only slot of the node, of type ST_GEN
	 */
	protected Slot genSlot;
	
	/**
	 * Constructor, should only be called by the NetEntityFactory! For a list of
	 * types see NodeFunctionalTypesIF.
	 * @param type type of the node
	 * @param nodeID ID of the node/entity	
	 * @param manager the EntityManager to be used
	 * @param decayType the decaytype for the node's gates
	 * @see NodeFunctionalTypesIF 
	 */
	protected Node(int type, String nodeID, NetEntityManager manager, int decayType) {
		super(nodeID,manager);
		this.type = type;
	
		try {
			genericLinks = new Gate(GateTypesIF.GT_GEN,this,decayType);
			genericLinks.setGateFactor(1);
			addGate(genericLinks);
	
			genSlot = new Slot(SlotTypesIF.ST_GEN,this,manager);
			addSlot(genSlot);
		} catch (NetIntegrityException e) {
			// won't happen
			manager.getLogger().error("Strange error",e);
		}
	}
	
	/**
	 * Returns the current activation of the GEN gate. 
	 * @return double the current activation
	 */
	public double getGenActivation() {
		return genericLinks.getConfirmedActivation();
	}
			
	/**
	 * @see org.micropsi.nodenet.NetEntity#calculateGates()
	 */
	protected void calculateGates() throws NetIntegrityException {		
		double slotvalue = genSlot.getIncomingActivation();

		for(int i=0;i<gates.size();i++) {
			Gate g = gates.get(i);
			g.setActivation(slotvalue);
		}
	}
	
	/**
	 * @see org.micropsi.nodenet.NetEntity#propagateActivation()
	 */
	protected void propagateActivation() throws NetIntegrityException {
		if(this.isActive()) use();
		super.propagateActivation();		
		
		/* 
		 * (Gate factors can only be set by ActLinkTypeNodes. As these 
		 * set the factors during gate calculation,
		 * gates are in defined state during activation propagation. After that
		 * gates must be reset. This can't be done during gate calculation as it
		 * would interfere with the activity of the ActLinkTypeNodes.)
		 */
		 
		 for(int i=0;i<gates.size();i++) {
		 	Gate g = gates.get(i);
		 	g.setGateFactor(g.getType() == GateTypesIF.GT_GEN ? 1 : 0);
		 }
	}

	/**
	 * Returns the node's type - <b> This is NOT the entity type, but the NODE
	 * type </b>, one of the values in NodeFunctionalyTypesIF.
	 * @return int the type of the node
	 * @see NodeFunctionalTypesIF
	 */
	public int getType() {
		return type;
	}

	/**
	 * Some nodes perform low-level java operations when activated.
	 * (Dissociators, activators and the like.) This method is <b> not </b>
	 * meant to get java functionality into the net - use NativeModules for this
	 * purpose!
	 * @throws NetIntegrityException if something is wrong with the net
	 */
	protected abstract void use() throws NetIntegrityException;

	/**
	 * @see org.micropsi.nodenet.NetEntity#getEntityType()
	 */
	public int getEntityType() {
		return NetEntityTypesIF.ET_NODE;
	}
}
