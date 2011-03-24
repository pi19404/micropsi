package org.micropsi.nodenet.modules.hypercept;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;


public class RebootRemover extends AbstractNativeModuleImpl {

	// slot types
	private static final int TRIGGER 	= 30000;
	
	// gate types
	private static final int HYPOTHESES = 30000;
	private static final int THECASE	= 30003;
	private static final int NOTTHECASE	= 30004;
							
	// technical stuff
	private boolean firsttime = true;
	private Slot trigger;
	
	private final int[] gateTypes = {
		HYPOTHESES,
		THECASE,
		NOTTHECASE
	};

	private final int[] slotTypes = {
		TRIGGER
	};
	
	public RebootRemover() {
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			public String getExtensionID() {
				return "rebootremover";
			}
			public String gateType(int type) {
				switch(type) {
					case HYPOTHESES:		return "Hypotheses";
					case THECASE:			return "TheCase";
					case NOTTHECASE:		return "!TheCase";
					default:				return null;
				}				
			}
			public String slotType(int type) {
				switch(type) {
					case TRIGGER:			return "Trigger";
					default: 				return null;
				}
			}
		});
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
		
		if(trigger.getIncomingActivation() <= 0) return;
		
		logger.debug("RebootRemover removed everything");
		
		Link hypothesesLink = gates.getGate(HYPOTHESES).getLinkAt(0);
		if(hypothesesLink == null) logger.error("SurGenerator has no hypothesesReg. Expect exceptions.");
		NetEntity hypothesesReg = hypothesesLink.getLinkedEntity();
		
		Link theCaseLink = gates.getGate(THECASE).getLinkAt(0);
		if(theCaseLink == null) logger.error("SurGenerator has no theCaseReg. Expect exceptions.");
		NetEntity theCaseReg = theCaseLink.getLinkedEntity();
		
		Link notTheCaseLink = gates.getGate(NOTTHECASE).getLinkAt(0);
		if(notTheCaseLink == null) logger.error("SurGenerator has no notTheCaseReg. Expect exceptions.");
		NetEntity notTheCaseReg = notTheCaseLink.getLinkedEntity();
	
		structure.unlinkGate(hypothesesReg.getID(),GateTypesIF.GT_GEN);
		structure.unlinkGate(theCaseReg.getID(),GateTypesIF.GT_GEN);
		structure.unlinkGate(notTheCaseReg.getID(),GateTypesIF.GT_GEN);
		
	}
	
}
