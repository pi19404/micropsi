package org.micropsi.nodenet.modules;

import java.util.ArrayList;
import java.util.Iterator;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.RegisterNode;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class ImmediateTrash extends AbstractNativeModuleImpl {

	// gate types
	private static final int TRASH_REG	= 12000; 

	// slot types
	private static final int TRIGGER		= 12000;
	
	private boolean firsttime = true;
	private Slot trigger;

	private final int[] gateTypes = {
		TRASH_REG 
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
	 
	public ImmediateTrash() {
		
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

				private static final String id = "immediatetrash";

				public String getExtensionID() {
					return id;
				}

				public String gateType(int type) {
					switch(type) {
						case TRASH_REG:	return "TrashReg";
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
		
		if(trigger.getIncomingActivation() <= 1) return;
		
		Link rnl = gates.getGate(TRASH_REG).getLinkAt(0);
		if(rnl == null) return;
		
		RegisterNode register = (RegisterNode)rnl.getLinkedEntity();
		
		ArrayList deleteIDs = new ArrayList(); 
		Iterator iter = register.getGate(GateTypesIF.GT_GEN).getLinks();
		while(iter.hasNext())
			deleteIDs.add(((Link)iter.next()).getLinkedEntity().getID());
		
/*		if(deleteIDs.size() > 0) {
			for(int i=0;i<deleteIDs.size();i++)
				logger.debug("Trashing: "+deleteIDs.get(i));
		}
*/
		
		for(int i=0;i<deleteIDs.size();i++)
			structure.deleteEntity((String)deleteIDs.get(i));

	}

}
