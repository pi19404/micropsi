/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/ChunkNode.java,v 1.9 2004/10/28 20:13:55 vuine Exp $
 */
package org.micropsi.nodenet;

/**
 * Chunk Node - the node type containing the magic for executable semantic
 * networks.
 * 
 */
public class ChunkNode extends Node {
	
	//Logger //logger;

	private static final int CS_INACTIVE = 0;
	private static final int CS_REQUESTED = 1;
	private static final int CS_REQUESTING = 2;
	private static final int CS_CONFIRMED = 4;
	private static final int CS_DISCONFIRMED = 5;
	
	protected Slot genSlot;
	protected Slot porSlot;
	protected Slot retSlot;
	protected Slot subSlot;
	protected Slot surSlot;
	
	protected Gate genGate;
	protected Gate porGate;
	protected Gate retGate;
	protected Gate subGate;
	protected Gate surGate;

	
	/**
	 * Constructor, setting the decay type to be applied to attached links.
	 * @param nodeID the ID of this Node.
	 * @param manager the entity manager.
	 * @param decayType the decay type.
	 * @see DecayIF
	 */
	protected ChunkNode(String nodeID, NetEntityManager manager, int decayType) {
		super(NodeFunctionalTypesIF.NT_CHUNK,nodeID,manager,decayType);
		
		//logger = manager.getLogger();
		
		try {
			
			genGate = super.genericLinks;
			porGate = new Gate(GT_POR,this,decayType);
			retGate = new Gate(GT_RET,this,decayType);
			subGate = new Gate(GT_SUB,this,decayType);
			surGate = new Gate(GT_SUR,this,decayType);
	
			addGate(porGate);
			addGate(retGate);
			addGate(subGate);
			addGate(surGate);
			addGate(new Gate(GT_CAT,this,decayType));
			addGate(new Gate(GT_EXP,this,decayType));
			addGate(new Gate(GT_SYM,this,decayType));
			addGate(new Gate(GT_REF,this,decayType));
			
			genSlot = super.genSlot;
			porSlot = new Slot(SlotTypesIF.ST_POR,this,manager);
			retSlot = new Slot(SlotTypesIF.ST_RET,this,manager);
			subSlot = new Slot(SlotTypesIF.ST_SUB,this,manager); 
			surSlot = new Slot(SlotTypesIF.ST_SUR,this,manager);
			
			addSlot(porSlot);
			addSlot(retSlot);
			addSlot(subSlot);
			addSlot(surSlot);
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
	

	int state = CS_INACTIVE;
	
	/*
	 * (non-Javadoc)
	 * @see org.micropsi.nodenet.NetEntity#calculateGates()
	 */
	protected void calculateGates() throws NetIntegrityException {

		genGate.setGateFactor(1.0);
		porGate.setGateFactor(1.0);
		retGate.setGateFactor(1.0);
		subGate.setGateFactor(1.0);
		surGate.setGateFactor(1.0);
		
		//String id = getEntityName();
		
		// override the default behavior of gen gates.
		//getGate(GateTypesIF.GT_GEN).setActivation(0);
		
		double keepAliveActivation = subSlot.getIncomingActivation();
		//if(keepAliveActivation == 0) keepAliveActivation = genSlot.getIncomingActivation();
		

		
		if(state != CS_INACTIVE) {
			if(keepAliveActivation <= 0) {
				state = CS_INACTIVE;
				//logger.debug("going to INACTIVE: "+id);
				return;
			}			
		}
		
		switch(state) {
			case CS_INACTIVE:
				if(keepAliveActivation > 0) {
					retGate.setActivation(keepAliveActivation);
					porGate.setActivation(keepAliveActivation);
					state = CS_REQUESTED;
					//logger.debug("going to REQUESTED: "+id);
				}
				break;
			case CS_REQUESTED:
		
				porGate.setActivation(keepAliveActivation);
				retGate.setActivation(keepAliveActivation);
		
				if(porSlot.getIncomingActivation() == 0) {
					state = CS_REQUESTING;
					subGate.setActivation(keepAliveActivation);
					//logger.debug("going to REQUESTING: "+id+" inPOR == 0");
					break;
				} else if(porSlot.getIncomingActivation() < 0) {
					state = CS_DISCONFIRMED;
					//logger.debug("going to DISCONFIRMED: "+id+" inPOR < 0");
					break;
				}
								
				break;
			case CS_REQUESTING:

				porGate.setActivation(keepAliveActivation);
				retGate.setActivation(keepAliveActivation);
				
				if(genSlot.hasIncomingLinks()) {
					if(genSlot.getIncomingActivation() > 0) {
						state = CS_CONFIRMED;
						//logger.debug("going to CONFIRMED: "+id+" (GEN > 0)");
						break;
					} else {
						state = CS_DISCONFIRMED;
						//logger.debug("going to DISCONFIRMED: "+id+" (GEN <= 0)");
						break;							
					}
				}
				
				if(surSlot.getIncomingActivation() > 0) {
					state = CS_CONFIRMED;
					//logger.debug("going to CONFIRMED: "+id+" (surSlot > 0: "+surSlot.getIncomingActivation()+")");
					break;
				} else if(surSlot.getIncomingActivation() < 0) {
					state = CS_DISCONFIRMED;
					//logger.debug("going to DISCONFIRMED: "+id+" (surSlot < 0)");
					break;
				}
				
				subGate.setActivation(keepAliveActivation);
				
				// in case we're "bottom" and have no further macros: check for
				// the presence of sensor input, if there should be sensor input on gen
				// report if it confirms the node or not.
				// If not, confirm. (Macros without sensors always succeed)

				if(!subGate.hasLinks()) {
					state = CS_CONFIRMED;
					//logger.debug("going to CONFIRMED: "+id+" (no SUB)");
					break;
				}						
				
				break;
			case CS_CONFIRMED:
		
				genGate.setActivation(keepAliveActivation);
				//subGate.setActivation(keepAliveActivation);
				retGate.setActivation(keepAliveActivation);
				surGate.setActivation(keepAliveActivation - Math.abs(retSlot.getIncomingActivation()));
				
				break;
			case CS_DISCONFIRMED:
		
				genGate.setActivation(-keepAliveActivation);
				//subGate.setActivation(keepAliveActivation);
				surGate.setActivation((-keepAliveActivation + retSlot.getIncomingActivation()) * 0.6);
				porGate.setActivation(-keepAliveActivation);
				retGate.setActivation(-keepAliveActivation);
				
				break;
		}

	}	
	
	/**
	 * Returns the state of this chunk node. This should only be called for
	 * reasons of persistency.
	 * @return
	 */
	protected int getState() {
		return state;
	}
	
	/**
	 * Sets this chunk node's state. This should only be called for
	 * reasons of persistency.
	 * @param state
	 */
	protected void setState(int state) {
		this.state = state;
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.nodenet.Node#use()
	 */
	protected void use() throws NetIntegrityException {
	}
}
