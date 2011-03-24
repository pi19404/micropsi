package org.micropsi.nodenet.modules;

import java.util.ArrayList;
import java.util.Iterator;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class GoalChecker extends AbstractNativeModuleImpl {

	// slots
	private static final int TRIGGER			= 22000;
	
	// gates
	private static final int LNK_GOALLIST	= 22000;
	private static final int LNK_CURSIT		= 22001;
	private static final int DONE			= 22003;

	// technical
	private boolean firsttime = true;
	private NetEntity goalListNode;
	private Node currentSituationReg;
	private Node primaryGoal;
	private int depth = 0;
	private Slot trigger;
	
	private final int[] gateTypes = { 
		LNK_GOALLIST,
		LNK_CURSIT,
		DONE
	};

	private final int[] slotTypes = {
		TRIGGER
	};
	
	public GoalChecker() {
		
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "goalchecker";

			public String getExtensionID() {
				return id;
			}

			public String gateType(int type) {
				switch(type) {
					case LNK_GOALLIST:	return "GoalList";
					case LNK_CURSIT:	return "CurSituation";
					case DONE:			return "Done";
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
					logger.warn("GoalChecker has no link to GoalList node. (Expect NullPointerExceptions)");
			goalListNode = l.getLinkedEntity();
									
			l = gates.getGate(LNK_CURSIT).getLinkAt(0);
			if(l == null)
					logger.warn("GoalChecker has no link to the current situation register. (Expect NullPointerExceptions)");
			currentSituationReg = (Node)l.getLinkedEntity();
			
			firsttime = false;
		}
		
		if(trigger.getIncomingActivation() < 1) return;
		
		
		ArrayList occurrences = new ArrayList();
		Link currentLink = currentSituationReg.getFirstLinkAt(GateTypesIF.GT_GEN);
		
		if(currentLink == null) {
			logger.warn("GoalChecker: no current situation found");
			done(gates);
			return;
		}
		
		Node currentSituation = (Node)currentLink.getLinkedEntity();
		Iterator iter = currentSituation.getGate(GateTypesIF.GT_SUR).getLinks();
		while(iter.hasNext()) occurrences.add(((Link)iter.next()).getLinkedEntity());
		
		// check if some of the situation's occurrences are goals, and if so, remove
		ArrayList toRemove = new ArrayList();
		iter = goalListNode.getGate(GateTypesIF.GT_GEN).getLinks();
		while(iter.hasNext()) {
			Node goal = (Node)((Link)iter.next()).getLinkedEntity();
			if(occurrences.contains(goal)) {
				toRemove.add(goal);		
				logger.info("GoalChecker: (Sub)Goal "+goal.getID()+" has been reached and was removed.");
			}
		}
		
		for(int i=0;i<toRemove.size();i++) {
			Node exgoal = (Node) toRemove.get(i);
			Link toDelete = goalListNode.getGate(GateTypesIF.GT_GEN).getLinkTo(exgoal.getID(),GateTypesIF.GT_GEN);
			structure.deleteLink(
				toDelete.getLinkingEntity().getID(),
				toDelete.getLinkingGate().getType(),
				toDelete.getLinkedEntityID(),
				toDelete.getLinkedSlot().getType()
			);
		}
		done(gates);
	}

	private void done(GateManipulator gates) {
		gates.setGateActivation(DONE,1.0);
	}
	
}
