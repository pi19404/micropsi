package org.micropsi.nodenet.modules.hypercept;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.LinkST;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;


public class STActor extends AbstractNativeModuleImpl {

	// slot types
	
	// gate types
	private static final int OUT = 28000;
							
	// technical stuff
	private boolean firsttime = true;
	private Slot gen = null;
	int count = 0; 
	
	private final int[] gateTypes = {
		GateTypesIF.GT_GEN,
		OUT		
	};

	private final int[] slotTypes = {
		SlotTypesIF.ST_GEN,
	};
	
	public STActor() {
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			public String getExtensionID() {
				return "stactor";
			}
			public String gateType(int type) {
				switch(type) {
					case OUT:			return "toRealActor";
					default:			return null;
				}				
			}
			public String slotType(int type) {
				switch(type) {
					default: 				return null;
				}
			}
		});
		
		innerstate.ensureStateExistence("attribute","");
	}

	private void catchSlots(Slot[] slots) {	 
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
				case SlotTypesIF.ST_GEN:
					gen = slots[i];
					break;
			}
		}			
	}
	
	protected int[] getGateTypes() {
		return gateTypes;
	}

	protected int[] getSlotTypes() {
		return slotTypes;
	}
	
	public void calculate(Slot[] slots, GateManipulator gates, long netstep) throws NetIntegrityException {
		if(firsttime) {
			
			gates.setGateMaximum(OUT,100);
			gates.setGateMinimum(OUT,-100);
			
			catchSlots(slots);
			firsttime = false;
		}

		double out = 0;
		gates.setGateActivation(GateTypesIF.GT_GEN,1.0);
		String attribute = innerstate.getInnerState("attribute");
		
		if(gen.getIncomingActivation() > 0) {
			for(int i=0;i<gen.getNumberOfIncomingLinks();i++) {
				if(!(gen.getIncomingLinkAt(i) instanceof LinkST)) continue;
				if(!gen.getIncomingLinkAt(i).getLinkingGate().isActive()) continue; 				
				
				LinkST link = (LinkST)gen.getIncomingLinkAt(i);
								
				if(attribute.equalsIgnoreCase("x")) {
					out += link.getX();
				} else if(attribute.equalsIgnoreCase("y")) {
					out += link.getY();
				} else if(attribute.equalsIgnoreCase("z")) {
					out += link.getZ();
				} else if(attribute.equalsIgnoreCase("t")) {
					out += link.getT();
				}
				
				// to make "0" moves report success
				if(out == 0.0) out += 0.0001; 
			}
		}
		
		if(out != 0.0 && gates.getGate(OUT).hasLinks()) {
			//logger.debug("at "+gates.getGate(0).getNetEntity().getEntityName()+" out is: "+out+" the "+count+". time");
			gates.setGateActivation(OUT,out);
			count ++;
		} else {
			count = 0;
		}
	}
}
