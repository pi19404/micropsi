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


public class HypoMaintenance extends AbstractNativeModuleImpl {

	// slot types

	// gate types
	private static final int HYPOTHESES = 26000;
	private static final int NOTTHECASE	= 26001;
							
	// technical stuff
	private boolean firsttime = true;
	private Slot reboot;
	
	private final int[] gateTypes = {
		HYPOTHESES,
		NOTTHECASE
	};

	private final int[] slotTypes = {
	};
	
	public HypoMaintenance() {
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			public String getExtensionID() {
				return "hypomaintenance";
			}
			public String gateType(int type) {
				switch(type) {
					case HYPOTHESES:		return "Hypotheses";
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
		
		Link notTheCaseLink = gates.getGate(NOTTHECASE).getLinkAt(0);
		if(notTheCaseLink == null) logger.error("SurGenerator has no notTheCaseReg. Expect exceptions.");
		NetEntity notTheCaseReg = notTheCaseLink.getLinkedEntity();

		ArrayList notTheCase = new ArrayList();
		Iterator notTheCaseLinks = notTheCaseReg.getGate(GateTypesIF.GT_GEN).getLinks();
		while(notTheCaseLinks.hasNext()) {
			notTheCase.add(((Link)notTheCaseLinks.next()).getLinkedEntity());
		}
		
		for(int i=0;i<notTheCase.size();i++) {
			NetEntity nce = (NetEntity)notTheCase.get(i);
			if(!nce.getGate(GateTypesIF.GT_SUR).hasLinks()) continue;
			
			Iterator surs = nce.getGate(GateTypesIF.GT_SUR).getLinks();
			while(surs.hasNext()) {
				NetEntity sur = ((Link)surs.next()).getLinkedEntity();
				if(hypotheses.contains(sur)) {
					hypotheses.remove(sur);
					structure.deleteLink(hypothesesReg.getID(),GateTypesIF.GT_GEN,sur.getID(),SlotTypesIF.ST_GEN);					
				}
			}
		}

		//@todo: Sort hypotheses to make the ones with more indicators be checked first
		
	}

}
