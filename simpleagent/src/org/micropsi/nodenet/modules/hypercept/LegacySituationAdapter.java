package org.micropsi.nodenet.modules.hypercept;

import java.util.ArrayList;
import java.util.Iterator;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;


public class LegacySituationAdapter extends AbstractNativeModuleImpl {

	// slot types
	
	// gate types
	private static final int THECASE	= 31001;
	private static final int SITREG		= 31002;
							
	// technical stuff
	private boolean firsttime = true;
	
	private final int[] gateTypes = {
		THECASE,
		SITREG
	};

	private final int[] slotTypes = {
	};
	
	public LegacySituationAdapter() {
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			public String getExtensionID() {
				return "legacysituationadapter";
			}
			public String gateType(int type) {
				switch(type) {
					case THECASE:			return "TheCase";
					case SITREG:			return "SitReg";
					default:				return null;
				}				
			}
			public String slotType(int type) {
				switch(type) {
					default: 				return null;
				}
			}
		});
		
		innerstate.ensureStateExistence("lastsit","");
	}

	private void catchSlots(Slot[] slots) {	 
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
/*				case TRIGGER:
					trigger = slots[i];
					break;*/
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
			catchSlots(slots);
			firsttime = false;
		}
				
		Link theCaseLink = gates.getGate(THECASE).getLinkAt(0);
		if(theCaseLink == null) logger.error("LegacySituationAdapter has no theCaseReg. Expect exceptions.");
		NetEntity theCaseReg = theCaseLink.getLinkedEntity();

		Link sitLink = gates.getGate(SITREG).getLinkAt(0);
		if(sitLink == null) logger.error("LegacySituationAdapter has no sitReg. Expect exceptions.");
		NetEntity sitReg = sitLink.getLinkedEntity();
		
		ArrayList theCaseList = new ArrayList();
		Iterator theCaseLinks = theCaseReg.getGate(GateTypesIF.GT_GEN).getLinks();
		while(theCaseLinks.hasNext()) {
			NetEntity theCase = ((Link)theCaseLinks.next()).getLinkedEntity(); 
			if(theCase.getEntityName().indexOf("sit") > 0) {
				if(!innerstate.getInnerState("lastsit").equals(theCase.getEntityName())) {
					innerstate.setState("lastsit",theCase.getEntityName());
					
					structure.unlinkGate(sitReg.getID(),GateTypesIF.GT_GEN);
					structure.createLink(
						sitReg.getID(),
						GateTypesIF.GT_GEN,
						theCase.getID(),
						SlotTypesIF.ST_GEN,
						1.0,
						1.0
					);
				}
				break;
			}
		}
		
		
	}
}
