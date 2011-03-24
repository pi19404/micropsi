/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/simpleagent/src/org/micropsi/nodenet/modules/Motivation.java,v 1.1 2004/05/07 21:48:06 vuine Exp $
 */
package org.micropsi.nodenet.modules;

import java.util.Iterator;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

/**
 * 
 *
 */
public class Motivation extends AbstractNativeModuleImpl {

	// slots
	private static final int FS_THRESHOLD		=	2000;
	private static final int FS_COMPETENCE		= 	2001;
	private static final int MOTIVES				=	2002;	
		
	// gates
	private static final int SELECTED			=	2000;
	private static final int CHANGE				=	2003;
	
	
	private final int[] slotTypes = {
		FS_COMPETENCE,
		FS_THRESHOLD,
		MOTIVES
	};

	private final int[] gateTypes = {
		SELECTED,
		CHANGE
	};

	private boolean firsttime = true;
	
	private NetEntity selectionNode;
	private GateManipulator selectionNodeGates;
	private Slot motives;
	private Slot threshold;
	private Slot competence;

	protected int[] getGateTypes() {
		return gateTypes;
	}

	protected int[] getSlotTypes() {
		return slotTypes;
	}
	
	private void catchSlots(Slot[] slots) {
		for(int i=0;i<slots.length;i++) {
			switch(slots[i].getType()) {
				case FS_COMPETENCE:
					competence = slots[i];
					break;
				case FS_THRESHOLD:
					threshold = slots[i];
					break;
				case MOTIVES:
					motives = slots[i];
					break;
			}
		}		
	}
	
	public Motivation() {

		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			public String getExtensionID() {
				return "motivation";
			}
			public String gateType(int type) {
				switch(type) {
					case SELECTED:			return "Selected";
					case CHANGE:			return "Change";
					default:				return null;
				}				
			}
			public String slotType(int type) {
				switch(type) {
					case FS_COMPETENCE:	return "FSCompete";
					case FS_THRESHOLD:		return "FSThresho";
					case MOTIVES:			return "Motives";
					default: 				return null;
				}
			}
		});

	}
	
	public void calculate(Slot[] slots, GateManipulator gates, long netstep) throws NetIntegrityException {
		if(firsttime) { 
			catchSlots(slots);
			
			Link l = gates.getGate(SELECTED).getLinkAt(0);
			if(l == null) logger.warn("Motivation: No selectionNode linked. This is bad, expect NullPointerExceptions");
			
			selectionNode = l.getLinkedEntity();
			selectionNodeGates = structure.getGateManipulator(selectionNode.getID());
			
			firsttime = false;
		}
		
		double currentSelectedNodeActivation = -100000;
		String currentSelectedNodeID = null; 
		
		Link selectionLink = selectionNode.getGate(GateTypesIF.GT_GEN).getLinkAt(0);
		if(selectionLink != null) {
			currentSelectedNodeActivation = selectionLink.getLinkedEntity().getGate(GateTypesIF.GT_GEN).getConfirmedActivation();
			currentSelectedNodeID = selectionLink.getLinkedEntityID();
		}
		
		double highestActivation = currentSelectedNodeActivation;
		String highestIndicatorID = null;

		Iterator iter = motives.getIncomingLinks();
		while(iter.hasNext()) {
			Link l = (Link)iter.next();	
			Node motive = (Node)l.getLinkingEntity();
			
			double newValue = motive.getGenActivation();

			if(		(!motive.getID().equals(currentSelectedNodeID)) &&
					((newValue - threshold.getIncomingActivation())  > highestActivation)) {
									
				highestActivation = newValue;
				highestIndicatorID = motive.getID();
			}
		}
		
		// if we detected a new highest activation, change the links
		if(highestIndicatorID != null) {
			selectionNodeGates.unlinkGate(GateTypesIF.GT_GEN);
			
			structure.createLink(
				selectionNode.getID(),
				GateTypesIF.GT_GEN,
				highestIndicatorID,
				SlotTypesIF.ST_GEN,
				1.0,
				1.0
			);
			
			gates.setGateActivation(CHANGE,1.0);
			logger.info("Active motive is now: "+structure.findEntity(highestIndicatorID).getEntityName());		
		}
		
	}
}
