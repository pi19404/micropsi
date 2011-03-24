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


public class Prover extends AbstractNativeModuleImpl {

	// slot types
		
	// gate types
	private static final int HYPOTHESES = 24000;
	private static final int PROVING	= 24001;
	private static final int THECASE	= 24002;
	private static final int NOTTHECASE = 24003;
	private static final int BOOT		= 24004;
	private static final int START		= 24005;
							
	// technical stuff
	private boolean firsttime = true;
	
	private final int[] gateTypes = {
		HYPOTHESES,
		PROVING,
		START,
		THECASE,
		NOTTHECASE,
		BOOT
	};

	private final int[] slotTypes = {
	};
	
	public Prover() {
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			public String getExtensionID() {
				return "prover";
			}
			public String gateType(int type) {
				switch(type) {
					case HYPOTHESES:		return "Hypotheses";
					case PROVING:			return "Proving";
					case START:				return "ProveStart";
					case THECASE:			return "TheCase";
					case NOTTHECASE:		return "!TheCase";
					case BOOT:				return "Boot";
					default:				return null;
				}			
			}
			public String slotType(int type) {
				switch(type) {
//					case SUCCESS:			return "Success";
					default: 				return null;
				}
			}
		});
	}

	private void catchSlots(Slot[] slots) {	
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
/*				case SUCCESS:
					success = slots[i];
					break;
*/
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
		
		gates.setGateActivation(THECASE,1);
		
		Link hypothesesLink = gates.getGate(HYPOTHESES).getLinkAt(0);
		if(hypothesesLink == null) logger.error("Prover has no hypothesesReg. Expect exceptions.");
		NetEntity hypothesesReg = hypothesesLink.getLinkedEntity();

		Link theCaseLink = gates.getGate(THECASE).getLinkAt(0);
		if(theCaseLink == null) logger.error("Prover has no theCaseReg. Expect exceptions.");
		NetEntity theCaseReg = theCaseLink.getLinkedEntity();
		
		Link notTheCaseLink = gates.getGate(NOTTHECASE).getLinkAt(0);
		if(notTheCaseLink == null) logger.error("Prover has no notTheCaseReg. Expect exceptions.");
		NetEntity notTheCaseReg = notTheCaseLink.getLinkedEntity();

		Link provingLink = gates.getGate(PROVING).getLinkAt(0);
		if(provingLink == null) logger.error("Prover has no provingReg. Expect exceptions.");
		NetEntity provingReg = provingLink.getLinkedEntity();
		
		if(provingReg.getGate(GateTypesIF.GT_GEN).hasLinks()) {

			double success = provingReg.getGate(GateTypesIF.GT_GEN).getLinkAt(0).getLinkedEntity().getGate(GateTypesIF.GT_GEN).getConfirmedActivation();
			
			NetEntity proving = provingReg.getGate(GateTypesIF.GT_GEN).getLinkAt(0).getLinkedEntity(); 
			if(success == -1) {
				logger.debug("Proving failed, "+proving+" is not the case.");
				structure.unlinkGate(provingReg.getID(),GateTypesIF.GT_GEN);
				structure.createLink(
					notTheCaseReg.getID(),
					GateTypesIF.GT_GEN,
					proving.getID(),
					SlotTypesIF.ST_GEN,
					1.0,
					1.0
				);
			} else if(success == 1) {
				logger.debug("Proving succeeded, "+proving+" is the case.");
				structure.unlinkGate(provingReg.getID(),GateTypesIF.GT_GEN);
				structure.createLink(
					theCaseReg.getID(),
					GateTypesIF.GT_GEN,
					proving.getID(),
					SlotTypesIF.ST_GEN,
					1.0,
					1.0
				);			
			} else {
				//logger.debug("Proving in progress: "+proving);
				return;
			}
		}
		
		if(	!hypothesesReg.getGate(GateTypesIF.GT_GEN).hasLinks() &&
			!theCaseReg.getGate(GateTypesIF.GT_GEN).hasLinks()) {
				
			gates.setGateActivation(BOOT,1.0);
			return;
		}
		
		ArrayList instantTrue = new ArrayList();
		ArrayList toProve = new ArrayList();
		
		Iterator hypotheses = hypothesesReg.getGate(GateTypesIF.GT_GEN).getLinks();
		while(hypotheses.hasNext()) {
			NetEntity hypo = ((Link)hypotheses.next()).getLinkedEntity();
			if(hypo.isActive()) 
				instantTrue.add(hypo);
			else
				toProve.add(hypo);
		}
		
		for(int i=0;i<instantTrue.size();i++) {
			NetEntity hypo = (NetEntity)instantTrue.get(i);
			structure.deleteLink(
				hypothesesReg.getID(),
				GateTypesIF.GT_GEN,
				hypo.getID(),
				SlotTypesIF.ST_GEN);
				
			structure.createLink(
				theCaseReg.getID(),
				GateTypesIF.GT_GEN,
				hypo.getID(),
				SlotTypesIF.ST_GEN,
				1.0,
				1.0
			);
		}
		
		if(toProve.size() < 1) return;
		NetEntity proveNow = (NetEntity)toProve.get(0);
		logger.debug("Prover now trying to prove: "+proveNow);
		gates.setGateActivation(START,1.0);
		
		structure.deleteLink(
			hypothesesReg.getID(),
			GateTypesIF.GT_GEN,
			proveNow.getID(),
			SlotTypesIF.ST_GEN);
					
		structure.createLink(
			provingReg.getID(),
			GateTypesIF.GT_GEN,
			proveNow.getID(),
			SlotTypesIF.ST_GEN,
			1.0,
			1.0
		);
	}

}
