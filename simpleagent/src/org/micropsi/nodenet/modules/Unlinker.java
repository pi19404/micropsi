package org.micropsi.nodenet.modules;

import java.util.Iterator;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class Unlinker extends AbstractNativeModuleImpl {

	// gate types
	private static final int UNLINK_REG	= 20000; 

	// slot types
	private static final int TRIGGER	= 20000;
	
	private boolean firsttime = true;
	private Slot trigger;

	private final int[] gateTypes = {
		UNLINK_REG 
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
	 
	public Unlinker() {
		
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

				private static final String id = "unlinker";

				public String getExtensionID() {
					return id;
				}

				public String gateType(int type) {
					switch(type) {
						case UNLINK_REG:	return "UnlinkReg";
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
		
		Iterator iter = gates.getGate(UNLINK_REG).getLinks();
		while(iter.hasNext()) {
			Link rnl = (Link)iter.next();
			structure.unlinkGate(rnl.getLinkedEntityID(),GateTypesIF.GT_GEN);	
		}
	}

}
