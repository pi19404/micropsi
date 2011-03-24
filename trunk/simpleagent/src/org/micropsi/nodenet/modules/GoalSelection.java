package org.micropsi.nodenet.modules;

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

public class GoalSelection extends AbstractNativeModuleImpl {

	// slots
	private static final int TRIGGER			= 18000;
	
	// gates
	private static final int LNK_GOALLIST	= 18000;
	private static final int LNK_SELECTED	= 18001;
	private static final int NO_GOAL		= 18002;

	// technical
	private boolean firsttime = true;
	private NetEntity goalListNode;
	private NetEntity selectedMotiveNode;
	private GateManipulator goalListNodeManipulator;
	private Slot trigger;
	

	private final int[] gateTypes = { 
		LNK_GOALLIST,
		LNK_SELECTED,
		NO_GOAL
	};

	private final int[] slotTypes = {
		TRIGGER
	};
	
	public GoalSelection() {
		
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "goalselection";

			public String getExtensionID() {
				return id;
			}

			public String gateType(int type) {
				switch(type) {
					case LNK_GOALLIST:	return "GoalList";
					case LNK_SELECTED:	return "SelMotive";
					case NO_GOAL:		return "NoGoal";
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

	public void calculate(Slot[] slots, GateManipulator gates, long netstep) throws NetIntegrityException {
		if(firsttime) {
			catchSlots(slots);
			
			Link l = gates.getGate(LNK_GOALLIST).getLinkAt(0);
			if(l == null)
					logger.warn("GoalSelection has no link to GoalList node. (Expect NullPointerExceptions)");
			goalListNode = l.getLinkedEntity();
			
			l = gates.getGate(LNK_SELECTED).getLinkAt(0);
			if(l == null)
					logger.warn("GoalSelection has no link to SelectedMotive node. (Expect NullPointerExceptions)");
			selectedMotiveNode = l.getLinkedEntity();

			goalListNodeManipulator = structure.getGateManipulator(goalListNode.getID());
		}
		
		if(goalListNode.getGate(GateTypesIF.GT_GEN).getNumberOfLinks() == 0)
			gates.setGateActivation(NO_GOAL,1.0);
		
		if(trigger.getIncomingActivation() > 0) {
			
			logger.debug("dropping goals!");
			
			goalListNodeManipulator.unlinkGate(GateTypesIF.GT_GEN);
			
			Link activeMotiveLink = selectedMotiveNode.getFirstLinkAt(GateTypesIF.GT_GEN);
			if(activeMotiveLink == null) return;
			NetEntity activeMotive = activeMotiveLink.getLinkedEntity();
			
			Iterator goalLinks = activeMotive.getGate(GateTypesIF.GT_POR).getLinks();
			while(goalLinks.hasNext()) {
								
				Link l = (Link)goalLinks.next();
				String goalID = l.getLinkedEntityID();
								
				logger.debug("Adding new goal: "+l.getLinkedEntityID());
				
				// ---- debug code ----
				
					NetEntity linked = l.getLinkedEntity();
					Link sublink = linked.getGate(GateTypesIF.GT_SUB).getLinkAt(0);
					logger.debug("that corresponds to: "+sublink.getLinkedEntity().getEntityName());
				
				
				//---------------------
				structure.createLink(
					goalListNode.getID(),
					GateTypesIF.GT_GEN,
					goalID,
					SlotTypesIF.ST_GEN,
					1.0,
					1.0
				);		
			}	
		}	
	}

}
