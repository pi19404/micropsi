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


public class CategoryLinker extends AbstractNativeModuleImpl {

	// gate types
	private static final int ORIGREG		= 16000;
	private static final int COPYREG		= 16001;
	
	// slot types
	private static final int TRIGGER		= 16000;

	private boolean firsttime = true;
	private Slot trigger;
	
	private final int[] gateTypes = { 
		ORIGREG,
		COPYREG
	};

	private final int[] slotTypes = {
		TRIGGER
	};


	protected int[] getGateTypes() {
		return gateTypes;		
	}

	protected int[] getSlotTypes() {
		return slotTypes;
	}

	private void catchSlots(Slot[] slots) {	
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
				case TRIGGER:
					trigger = slots[i];
					break;
			}
		}
	}
	
	public CategoryLinker() {
				
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			
			private static final String id = "categorylinker";

			public String getExtensionID() {
				return id;
			}

			public String gateType(int type) {
				switch(type) {
					case ORIGREG:		return "OrigReg";
					case COPYREG:		return "CopyReg";
					default:			return null;
				}
			}

			public String slotType(int type) {
				switch(type) {
					case TRIGGER:		return "Trigger";
					default:			return null;
				}				
			}
		});
		
	}
	
	public void calculate(Slot[] slots, GateManipulator gates, long netstep) throws NetIntegrityException {
		if(firsttime) {
			catchSlots(slots);
			firsttime = false;			
		}
	
		if(trigger.getIncomingActivation() <= 0) return;
				
		Link origRegLink = gates.getGate(ORIGREG).getLinkAt(0);
		if(origRegLink == null) return;
		NetEntity origReg = origRegLink.getLinkedEntity();
		
		Link copyRegLink = gates.getGate(COPYREG).getLinkAt(0);
		if(copyRegLink == null) return;
		NetEntity copyReg = copyRegLink.getLinkedEntity();
				
		Iterator iter = origReg.getGate(GateTypesIF.GT_GEN).getLinks();
		while(iter.hasNext()) {
			// create a category node for every linked entity
		
			Node origNode = (Node)((Link)iter.next()).getLinkedEntity();
			
			String catNodeID = structure.createConceptNode("cat-"+origNode.getEntityName());
			GateManipulator catNodeMan = structure.getGateManipulator(catNodeID);
			
			//@todo: Check if this is safe (are there other uses of CategoryLinker where this is undesirable?)
			catNodeMan.setGateMaximum(GateTypesIF.GT_GEN,100);
						
			String proxyID = structure.createRegisterNode("proxy-"+origNode.getEntityName());
			
			structure.createLink(
				copyReg.getID(),
				GateTypesIF.GT_GEN,
				catNodeID,
				SlotTypesIF.ST_GEN,
				1.0,
				1.0
			);
			
			structure.createLink(
				origNode.getID(),
				GateTypesIF.GT_GEN,
				proxyID,
				SlotTypesIF.ST_GEN,
				1.0,
				1.0
			);
			
			structure.createLink(
				proxyID,
				GateTypesIF.GT_GEN,
				catNodeID,
				SlotTypesIF.ST_GEN,
				100.0,
				1.0
			);

			
		}
		
		
		structure.getGateManipulator(origReg.getID()).unlinkGate(GateTypesIF.GT_GEN);
		

		
	}
}
