package org.micropsi.nodenet.modules;

import java.util.Iterator;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.NodeFunctionalTypesIF;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class MostActiveDetector extends AbstractNativeModuleImpl {

	// gate types
	private static final int MACTIVEREG		= 17000;
	
	// slot types
	// none

	private boolean firsttime = true;
	
	private final int[] gateTypes = {
		MACTIVEREG 
	};

	private final int[] slotTypes = {
	};


	protected int[] getGateTypes() {
		return gateTypes;		
	}

	protected int[] getSlotTypes() {
		return slotTypes;
	}

	private void catchSlots(Slot[] slots) {	
/*		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
				case TRIGGER:
					trigger = slots[i];
					break;
			}
		}*/
	}
	
	public MostActiveDetector() {
				
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			
			private static final String id = "mostactivedetector";

			public String getExtensionID() {
				return id;
			}

			public String gateType(int type) {
				switch(type) {
					case MACTIVEREG:	return "MostActive";
					default:			return null;
				}
			}

			public String slotType(int type) {
				return null;
			}
		});
		
	}
	
	private String lastCycleMostActive = "";
	
	public void calculate(Slot[] slots, GateManipulator gates, long netstep) throws NetIntegrityException {
		if(firsttime) {
			catchSlots(slots);
			firsttime = false;			
		}
				
		Link mActiveRegLink = gates.getGate(MACTIVEREG).getLinkAt(0);		
		if(mActiveRegLink == null) return;
		NetEntity mActiveReg = mActiveRegLink.getLinkedEntity();
	
		Iterator iter = structure.getSpace().getAllLevelOneEntities();
		String mostActiveID = null;
		
		
		// tweak here the lower limit of activation that counts "active" -- this could be
		// done by a slot later
		double mostActiveActivation = 50;
		while(iter.hasNext()) {
			NetEntity e = (NetEntity)iter.next();
			if(e.getEntityType() != NetEntityTypesIF.ET_NODE) continue;
			Node n = (Node)e;
			if(n.getType() != NodeFunctionalTypesIF.NT_CONCEPT) continue;
			if(n.getGenActivation() > mostActiveActivation) {
				mostActiveActivation = n.getGenActivation();
				mostActiveID = n.getID();
			}
		}
		
		if(mostActiveID == null) return;
		if(mostActiveID.equals(lastCycleMostActive)) return;
	
		structure.getGateManipulator(mActiveReg.getID()).unlinkGate(GateTypesIF.GT_GEN);
		
		structure.createLink(
			mActiveReg.getID(),
			GateTypesIF.GT_GEN,
			mostActiveID,
			GateTypesIF.GT_GEN,
			1.0,
			1.0
		);
	}
}
