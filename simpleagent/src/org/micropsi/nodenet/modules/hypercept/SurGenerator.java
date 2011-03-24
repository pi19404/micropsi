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


public class SurGenerator extends AbstractNativeModuleImpl {

	// slot types
	
	// gate types
	private static final int HYPOTHESES = 29000;
	private static final int MEMORY 	= 29001;
	private static final int CHECKING 	= 29002;
	private static final int THECASE	= 29003;
	private static final int NOTTHECASE	= 29004;
							
	// technical stuff
	private boolean firsttime = true;
	
	private final int[] gateTypes = {
		HYPOTHESES,
		CHECKING,
		MEMORY,
		THECASE,
		NOTTHECASE
	};

	private final int[] slotTypes = {
	};
	
	public SurGenerator() {
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			public String getExtensionID() {
				return "surgenerator";
			}
			public String gateType(int type) {
				switch(type) {
					case HYPOTHESES:		return "Hypotheses";
					case MEMORY:			return "Memory";
					case CHECKING: 			return "Checking";
					case THECASE:			return "TheCase";
					case NOTTHECASE:		return "!TheCase";
					default:				return null;
				}				
			}
			public String slotType(int type) {
				switch(type) {
					default: 				return null;
				}
			}
		});
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
		
		Link hypothesesLink = gates.getGate(HYPOTHESES).getLinkAt(0);
		if(hypothesesLink == null) logger.error("SurGenerator has no hypothesesReg. Expect exceptions.");
		NetEntity hypothesesReg = hypothesesLink.getLinkedEntity();
		
		ArrayList hypotheses = new ArrayList();
		Iterator hypLinks = hypothesesReg.getGate(GateTypesIF.GT_GEN).getLinks();
		while(hypLinks.hasNext()) {
			hypotheses.add(((Link)hypLinks.next()).getLinkedEntity());
		}

		Link checkingLink = gates.getGate(CHECKING).getLinkAt(0);
		if(checkingLink == null) logger.error("SurGenerator has no checkingReg. Expect exceptions.");
		NetEntity checkingReg = checkingLink.getLinkedEntity();
		
		Link checkingNodeLink = checkingReg.getGate(GateTypesIF.GT_GEN).getLinkAt(0);
		NetEntity checking = checkingNodeLink != null ? checkingNodeLink.getLinkedEntity() : null;
		
		Link theCaseLink = gates.getGate(THECASE).getLinkAt(0);
		if(theCaseLink == null) logger.error("SurGenerator has no theCaseReg. Expect exceptions.");
		NetEntity theCaseReg = theCaseLink.getLinkedEntity();

		ArrayList theCaseList = new ArrayList();
		Iterator theCaseLinks = theCaseReg.getGate(GateTypesIF.GT_GEN).getLinks();
		while(theCaseLinks.hasNext()) {
			theCaseList.add(((Link)theCaseLinks.next()).getLinkedEntity());
		}
		
		Link notTheCaseLink = gates.getGate(NOTTHECASE).getLinkAt(0);
		if(notTheCaseLink == null) logger.error("SurGenerator has no notTheCaseReg. Expect exceptions.");
		NetEntity notTheCaseReg = notTheCaseLink.getLinkedEntity();

		ArrayList notTheCase = new ArrayList();
		Iterator notTheCaseLinks = notTheCaseReg.getGate(GateTypesIF.GT_GEN).getLinks();
		while(notTheCaseLinks.hasNext()) {
			notTheCase.add(((Link)notTheCaseLinks.next()).getLinkedEntity());
		}
		
		ArrayList newHypotheses = new ArrayList();
		theCaseLinks = theCaseReg.getGate(GateTypesIF.GT_GEN).getLinks();
		while(theCaseLinks.hasNext()) {
			NetEntity theCase = ((Link)theCaseLinks.next()).getLinkedEntity();
			if(!theCase.getGate(GateTypesIF.GT_SUR).hasLinks()) continue;
			
			Iterator supraList = theCase.getGate(GateTypesIF.GT_SUR).getLinks();
			while(supraList.hasNext()) {
				NetEntity candidate = ((Link)supraList.next()).getLinkedEntity();				
				if(	!hypotheses.contains(candidate) &&
					!notTheCase.contains(candidate) &&
					!theCaseList.contains(candidate) &&
					(checking == null || checking != candidate)) {
					
					hypotheses.add(candidate);
					newHypotheses.add(candidate);
				}
			}
		}
		
		for(int i=0;i<newHypotheses.size();i++) {
			NetEntity newHyp = (NetEntity)newHypotheses.get(i);
			logger.debug("surGenerator generating hypothesis: "+newHyp.getEntityName());
			linkNewHypothesis(newHyp.getID(),hypothesesReg.getID());
		}
	}
	
	private void linkNewHypothesis(String id, String register) throws NetIntegrityException {
		structure.createLink(
			register,
			GateTypesIF.GT_GEN,
			id,
			SlotTypesIF.ST_GEN,
			1.0,
			0
		);
	}
}
