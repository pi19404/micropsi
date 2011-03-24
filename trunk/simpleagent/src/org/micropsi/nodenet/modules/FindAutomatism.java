package org.micropsi.nodenet.modules;

import java.util.ArrayList;
import java.util.Iterator;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.NodeSpaceModule;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class FindAutomatism extends AbstractNativeModuleImpl {
		
	private static final int RESREG		= 10000;
	private static final int PLANSPACE	= 10001;
	private static final int GOALS		= 10002;
	private static final int SITUATION	= 10003;
	private static final int MEMORY		= 10004;
	private static final int FOUND		= 10005;
	private static final int FAILURE	= 10006;
	 
	private static final int TRIGGER	= 10000;
		
	private boolean firsttime = true;
	private Slot trigger;
	
	private final int[] gateTypes = { 
		RESREG,
		GOALS,
		SITUATION,
		MEMORY,
		PLANSPACE,
		FOUND,
		FAILURE
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
	
	public FindAutomatism() {
		
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "findautomatism";

			public String getExtensionID() {
				return id;
			}

			public String gateType(int type) {
				switch(type) {
					case GOALS:			return "Goals";
					case RESREG:		return "ResReg";
					case PLANSPACE:		return "PlanSpace";
					case MEMORY:		return "Memory";
					case SITUATION:		return "Situation";
					case FOUND:			return "Found";
					case FAILURE:		return "Failure";
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
	
	private void catchSlots(Slot[] slots) {	
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
				case TRIGGER:
					trigger = slots[i];
					break;
			}
		}
	}
	
	private NetEntity goalListRegister = null;
	private NetEntity situationRegister = null;
	private NodeSpaceModule memory = null;
	private NodeSpaceModule planSpace = null;

	public void calculate(Slot[] slots, GateManipulator gates, long netstep) throws NetIntegrityException {
		if(firsttime) {
			catchSlots(slots);

			// goal list register
			Link goalListRegisterLink = gates.getGate(GOALS).getLinkAt(0);
			if(goalListRegisterLink == null)
				logger.warn("FindAutomatism has no link to GoalList node. (Expect NullPointerExceptions)");			
			goalListRegister = goalListRegisterLink.getLinkedEntity();
			
			// current situation register
			Link situationRegisterLink = gates.getGate(SITUATION).getLinkAt(0);
			if(situationRegisterLink == null)
				logger.warn("FindAutomatism has no link to the situation node. (Expect NullPointerExceptions)");
			situationRegister = situationRegisterLink.getLinkedEntity();

			// memory space
			Link memorySpaceLink = gates.getGate(MEMORY).getLinkAt(0);
			if(memorySpaceLink == null)
				logger.warn("FindAutomatism has no link to a memory space. (Expect NullPointerExceptions)");		 
			try {
				memory = (NodeSpaceModule)memorySpaceLink.getLinkedEntity();
			} catch (ClassCastException e) {
				logger.warn("FindAutomatism's memory space link doesn't link a NodeSpaceModule. (searching was aborted)");
				return;
			}

			// plan space
			Link planSpaceLink = gates.getGate(PLANSPACE).getLinkAt(0);
			if(planSpaceLink == null)
				logger.warn("FindAutomatism has no link to a plan space. (Expect NullPointerExceptions)");		 
			try {
				planSpace = (NodeSpaceModule)planSpaceLink.getLinkedEntity();
			} catch (ClassCastException e) {
				logger.warn("FindAutomatism's plan space link doesn't link a NodeSpaceModule. (searching was aborted)");
				return;
			}
			
			firsttime = false;			
		}

		if(trigger.getIncomingActivation() <= 0) return;
				 
		if(goalListRegister.getGate(GateTypesIF.GT_GEN).getNumberOfLinks() <= 0) {
			logger.debug("FindAutomatism failed: no goals");
			gates.setGateActivation(FAILURE,1);
			return;			 
		}
		
		ArrayList situationOccurences = new ArrayList();
		try {
			NetEntity situation = situationRegister.getFirstLinkAt(GateTypesIF.GT_GEN).getLinkedEntity();
			Iterator iter = situation.getGate(GateTypesIF.GT_SUR).getLinks();
			while(iter.hasNext()) situationOccurences.add(((Link)iter.next()).getLinkedEntity());
		} catch (Exception e) {
			logger.error("FindAutomatism failed: no situation",e);
			e.printStackTrace();
			gates.setGateActivation(FAILURE,1);
			return;			 			
		}

		Iterator iter = goalListRegister.getGate(GateTypesIF.GT_GEN).getLinks();
		while(iter.hasNext()) {
			NetEntity goalElement = ((Link)iter.next()).getLinkedEntity();
			
			// check if goal has a SUB link to the plan space (references a plan)
			String referencedElement = goalElement.getFirstLinkAt(GateTypesIF.GT_SUB).getLinkedEntityID();
			if(planSpace.containsEntityDirectly(referencedElement)) {
				// this is a candidate - it is the one we're looking for if
				// its protocol precessor references the current situation 
				
				for(int i=0;i<situationOccurences.size();i++) {
					NetEntity situationOccurrence = (NetEntity) situationOccurences.get(i);
					Link l = situationOccurrence.getGate(GateTypesIF.GT_POR).getLinkTo(goalElement.getID(),SlotTypesIF.ST_GEN);
					if(l != null) {
						linkResult(gates,referencedElement);
						logger.info("FindAutomatism found plan to reuse: "+referencedElement);
						gates.setGateActivation(FOUND,1);
						return;
					}
				}				
			}
		}
		
		logger.debug("FindAutomatism failed");
		gates.setGateActivation(FAILURE,1);
	}

	private void linkResult(GateManipulator gates, String referencedElement) throws NetIntegrityException {

		Iterator iter = gates.getGate(RESREG).getLinks();
		
		while(iter.hasNext()) {
			String resreg = ((Link)iter.next()).getLinkedEntityID();
			structure.unlinkGate(resreg,GateTypesIF.GT_GEN);
			structure.createLink(
				resreg,
				GateTypesIF.GT_GEN,
				referencedElement,
				SlotTypesIF.ST_GEN,
				1.0,
				1.0
			);			
		}
	}
}
