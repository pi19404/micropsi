/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/simpleagent/src/org/micropsi/nodenet/modules/ProtocolGeneration.java,v 1.1 2004/05/07 21:48:06 vuine Exp $
 */
package org.micropsi.nodenet.modules;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.RegisterNode;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;
import org.micropsi.nodenet.ext.StandardDecays;

/**
 * 
 * protgen
 * 
 */
public class ProtocolGeneration extends AbstractNativeModuleImpl implements GateTypesIF {

	// gates

	public static final int LNK_PROTOCOL_ACT	=	3000;
	public static final int LNK_PROTOCOL_SIT	=	3001;
	public static final int LNK_USTM			=	3002;

	// slots
	
	public static final int BLOCK_SITPROT		=	3000;

	private final int[] gateTypes = {
		LNK_PROTOCOL_ACT,
		LNK_PROTOCOL_SIT,
		LNK_USTM
	};	

	private final int[] slotTypes = {
		BLOCK_SITPROT
	};

	protected int[] getGateTypes() {
		return gateTypes;
	}

	protected int[] getSlotTypes() {
		return slotTypes;
	}
	
	private boolean firsttime = true;
	private Slot blockSit;
	
	public ProtocolGeneration() {

		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			public String getExtensionID() {
				return "protocolgeneration";
			}
			public String gateType(int type) {
				switch(type) {
					case LNK_PROTOCOL_ACT:		return "ProtAct";
					case LNK_PROTOCOL_SIT:		return "ProtSit";
					case LNK_USTM:				return "USTM";
					default:					return null;
				}				
			}
			public String slotType(int type) {
				switch(type) {
					case BLOCK_SITPROT:			return "BlockSitProt";
					default:					return null;
				}				
			}
		});

	}
	
	private void catchSlots(Slot[] slots) {	
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
				case BLOCK_SITPROT:
					blockSit = slots[i];
					break;
			}
		}
	}
				
	public void calculate(Slot[] slots, GateManipulator manipulator, long netstep) throws NetIntegrityException {
		if(firsttime) {
			catchSlots(slots);
			firsttime = false;
		}

		// get the link to the protocollandum
		Link actProtocollandumRegLink = manipulator.getGate(LNK_PROTOCOL_ACT).getLinkAt(0);
		Link sitProtocollandumRegLink = manipulator.getGate(LNK_PROTOCOL_SIT).getLinkAt(0);
	
		// if any of the both register nodes is missing - do nothing
		if(actProtocollandumRegLink == null || sitProtocollandumRegLink == null) return;
	
		RegisterNode actProtocollandumReg = (RegisterNode)actProtocollandumRegLink.getLinkedEntity();
		RegisterNode sitProtocollandumReg = (RegisterNode)sitProtocollandumRegLink.getLinkedEntity();

		Link actProtocollandumLink = actProtocollandumReg.getFirstLinkAt(GateTypesIF.GT_GEN);
		Link sitProtocollandumLink = sitProtocollandumReg.getFirstLinkAt(GateTypesIF.GT_GEN);
		
		// if there's no protocollandum link, there is simply nothing to add 
		// to the protocol. That's ok, but there's nothing to do, so: return.
		if(actProtocollandumLink == null && sitProtocollandumLink == null) return;
	
		String protocollandumID = null;
		// get the protocollandum entity, prefer actions
		if(actProtocollandumLink != null) {
			protocollandumID = actProtocollandumLink.getLinkedEntity().getID();
			
			// unlink the register to avoid protocolling the same over and over again
			structure.unlinkGate(actProtocollandumReg.getID(),GateTypesIF.GT_GEN);			
			
		} else {
			
			NetEntity situationNode = sitProtocollandumLink.getLinkedEntity(); 

			protocollandumID = situationNode.getID();
			
			// unlink the register to avoid protocolling the same over and over again
			structure.unlinkGate(sitProtocollandumReg.getID(),GateTypesIF.GT_GEN);

			if(blockSit.getIncomingActivation() > 0) {
				//logger.error("blockSit blocked!");
				return;
			}
		}
		
		
//		System.err.println("------------- protocol!");
//		System.err.println("protocollandum is: "+protocollandumID);
	
	
		// get the link to the USTM entity
		Link ustmLink = manipulator.getGate(LNK_USTM).getLinkAt(0);
		
		// if there is no ustm, the situation is really bad.
		if(ustmLink == null) {
		 	logger.warn("ProtocolGeneration: No ustm link! - can't do anything!");
			return;
		}
	
		// get the ustm entity
		NetEntity ustm = ustmLink.getLinkedEntity();
//		System.err.println("ustm is: "+ustm+" "+ustm.getID());
		 
		// create a new "inter" node 
		String interNodeID = structure.createConceptNode("internode");
		
		// set the decay for the por/ret path
		structure.getGateManipulator(interNodeID).setGateDecayType(GateTypesIF.GT_POR, StandardDecays.DT_SLOW_LINEAR);
		structure.getGateManipulator(interNodeID).setGateDecayType(GateTypesIF.GT_RET, StandardDecays.DT_SLOW_LINEAR);
		
//		System.err.println("new internode is: "+interNodeID);
					
		// create a new SUB link from the interNode to the protocollandum
		structure.createLink(
			interNodeID, 
			GateTypesIF.GT_SUB, 
			protocollandumID, 
			SlotTypesIF.ST_GEN, 
			1.0, 
			1.0);
		
//		System.err.println("created sub: internode --> protocollandum");
			
		// create a new SUR link from the protocollandum to the interNode
		structure.createLink(
			protocollandumID,
			GateTypesIF.GT_SUR,
			interNodeID,
			SlotTypesIF.ST_GEN,
			1.0,
			1.0);
			
//		System.err.println("created sur: protocollandum --> internode");
		
		// get the link to the current protocol head and the previous protocol head
		Link protocolHeadLink = ustm.getFirstLinkAt(GT_SUB);
	
		Link prevProtocolHeadLink = ustm.getFirstLinkAt(GT_RET);
		
		// if the protocolHead is not linked: don't bother, start a new
		// chain. This is perfectly ok, in the next cycle everything will be normal  		
	
		if(protocolHeadLink != null) {
			
			// if there is a protocol head, shift it to be the new prevProtocol head
			
			NetEntity protocolHead = protocolHeadLink.getLinkedEntity();
//			System.err.println("protocolhead is: "+protocolHead+" "+protocolHead.getID());
			
			// create links between the new inter node and the (now "old") protocol head
			structure.createLink(
				protocolHead.getID(),
				GateTypesIF.GT_POR,
				interNodeID,
				SlotTypesIF.ST_GEN,
				1.0,
				1.0);
				
//			System.err.println("created por: protocolhead --> internode");
				
			structure.createLink(
				interNodeID,
				GateTypesIF.GT_RET,
				protocolHead.getID(),
				SlotTypesIF.ST_GEN,
				1.0,
				1.0);
			
//			System.err.println("created ret: internode --> protocolhead");
			
			// delete the old ustm RET link (if there)
			if(prevProtocolHeadLink != null) {
				String prevProtocolHeadID = prevProtocolHeadLink.getLinkedEntityID();
//				System.err.println("prevprotocolhead ist: "+prevProtocolHeadID);
				structure.deleteLink(
					ustm.getID(), 
					GateTypesIF.GT_RET,
					prevProtocolHeadID, 
					SlotTypesIF.ST_GEN);
					
//				System.err.println("deleted ret: ustm --> prevprotocolhead");
			}

			// create the new ustm RET link (making the protocolhead the prevProtocolHead 
			// in the next step)
			structure.createLink(
				ustm.getID(),  
				GateTypesIF.GT_RET,
				protocolHead.getID(), 
				SlotTypesIF.ST_GEN, 
				1.0, 
				1.0);
			
//			System.err.println("created ret: ustm --> protocolhead");
			
			// delete old ustm SUB link
			structure.deleteLink(
				ustm.getID(),
				GateTypesIF.GT_SUB,
				protocolHead.getID(),
				SlotTypesIF.ST_GEN);
				
//			System.err.println("deleted SUB: ustm --> protocolhead");
				
		} //else System.err.println("No protocol head");
	
		// link the ustm with the brand new protocol head, our "inter" entity!
		
		structure.createLink(
			ustm.getID(),
			GateTypesIF.GT_SUB,
			interNodeID,
			SlotTypesIF.ST_GEN,
			1.0,
			1.0);
			
//		System.err.println("created sub: ustm --> internode");
			
				
	}

}
