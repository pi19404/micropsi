package org.micropsi.nodenet.modules.hypercept;

import java.util.ArrayList;
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
import org.micropsi.nodenet.NodeSpaceModule;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.Situation;
import org.micropsi.nodenet.agent.SituationElement;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;


public class BootstrapGenerator extends AbstractNativeModuleImpl {

	// slot types
	private static final int TRIGGER 	= 25000;
	
	// gate types
	private static final int HYPOTHESES = 25000;
	private static final int MEMORY 	= 25001;
	private static final int FAIL		= 25002;
	//private static final int THECASE	= 25003;
							
	// technical stuff
	private boolean firsttime = true;
	private Slot trigger = null;
	
	private final int[] gateTypes = {
		HYPOTHESES,
		MEMORY,
		FAIL
	};

	private final int[] slotTypes = {
		TRIGGER
	};
	
	public BootstrapGenerator() {
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			public String getExtensionID() {
				return "bootstrapgenerator";
			}
			public String gateType(int type) {
				switch(type) {
					case HYPOTHESES:		return "Hypotheses";
					case MEMORY:			return "Memory";
					case FAIL:				return "Fail";
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
		
		innerstate.ensureStateExistence("situation","micropsi");			
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
		if(hypothesesLink == null) logger.error("BootstrapGenerator has no hypothesesReg. Expect exceptions.");
		NetEntity hypothesesReg = hypothesesLink.getLinkedEntity();
		
		if(hypothesesReg.getGate(GateTypesIF.GT_GEN).getNumberOfLinks() > 0) {
			return;
		}
		
		if(trigger.getIncomingActivation() < 1)
			return;
		
		logger.debug("BootstrapGenerator: No hypotheses! Bootstrapping.");
		
		ArrayList basicFeatures = new ArrayList();
		Situation situation = Situation.getInstance(innerstate.getInnerState("situation"));
		Iterator elements = situation.getWholeSituation();
		while(elements.hasNext()) {
			SituationElement e = (SituationElement)elements.next();
			basicFeatures.add(e.getType());	
		}
		
		boolean couldBootstrap = false;
		
		Link memoryLink = gates.getGate(MEMORY).getLinkAt(0);
		if(memoryLink == null) logger.error("BootstrapGenerator has no memory link. Expect exceptions.");
		NodeSpaceModule memory = (NodeSpaceModule)memoryLink.getLinkedEntity();
		Iterator entities = memory.getAllLevelOneEntities();
		while(entities.hasNext()) {
			NetEntity entity = (NetEntity)entities.next();
			if(entity.getEntityType() != NetEntityTypesIF.ET_NODE) continue;
			Node node = (Node)entity;
			if(node.getType() != NodeFunctionalTypesIF.NT_CHUNK) continue;
			if(!node.hasName()) continue;	
				
			if(basicFeatures.contains(node.getEntityName())) {
				logger.debug("new bootstrap hypothesis: "+node);
				linkNewHypothesis(node.getID(),hypothesesReg.getID());
				couldBootstrap = true;
			}	
		}
		
		if(!couldBootstrap) {
			logger.warn("bootstrapping failed!");
			gates.setGateActivation(FAIL,1.0);
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
