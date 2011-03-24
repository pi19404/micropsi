package org.micropsi.nodenet.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.NetWeaver;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.NodeFunctionalTypesIF;
import org.micropsi.nodenet.NodeSpaceModule;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class PlanCreation extends AbstractNativeModuleImpl {

	private class ReachableDistance {
		public Node situation;
		public int distance;
	}

	//@todo revise this and how it is used
	private static final int MAX_DEPTH = 150;
	
	private static final double REINFORCE_FACTOR	= 1.5;
	
	private static final int STATE_IDLE				= 0;
	private static final int STATE_SEARCH_REACHABLE	= 1;
	private static final int STATE_WAIT_REACHABLE	= 2;
	private static final int STATE_SEARCH_FRAGMENT	= 3;
	private static final int STATE_WAIT_FRAGMENT	= 4;

	// slots
	private static final int TRIGGER			= 21000;
	
	// gates
	private static final int LNK_GOALLIST	= 21000;
	private static final int LNK_MEMORY		= 21001;
	private static final int LNK_SITUATIONS	= 21002;
	private static final int LNK_CURSIT		= 21003;
	private static final int PLANREG		= 21004;
	private static final int PLANENDREG		= 21005;
	private static final int SUCCESS		= 21006;
	private static final int FAILURE		= 21007;

	// technical
	private boolean firsttime = true;
	private NetEntity goalListNode;
	private NetEntity planReg;
	private NetEntity planEndReg;
	private NodeSpaceModule memory;
	private NodeSpaceModule situations;
	private NodeSpaceModule goalNodeSpace;
	
	private Node goalSpaceActRet;
	private Node currentSituation;
	private Node memoryActRet;
	private Node memoryActPor;
	private Node memoryDeactivator;
	
	private Node primaryGoal;
	
	private int depth = 0;
	private int reorgcounter = 0;
	private Slot trigger;
	
	private int state = STATE_IDLE;

	private final int[] gateTypes = { 
		LNK_GOALLIST,
		LNK_MEMORY,
		LNK_SITUATIONS,
		LNK_CURSIT,
		PLANREG,
		PLANENDREG,
		SUCCESS,
		FAILURE
	};

	private final int[] slotTypes = {
		TRIGGER
	};
	
	public PlanCreation() {
		
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "plancreation";

			public String getExtensionID() {
				return id;
			}

			public String gateType(int type) {
				switch(type) {
					case LNK_GOALLIST:	return "GoalList";
					case LNK_MEMORY:	return "Memory";
					case LNK_SITUATIONS:return "Situations";
					case LNK_CURSIT:	return "CurSituation";
					case PLANREG:		return "Plan";
					case PLANENDREG:	return "PlanEnd";
					case SUCCESS:		return "Success";
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
					logger.warn("PlanCreation has no link to GoalList node. (Expect NullPointerExceptions)");
			goalListNode = l.getLinkedEntity();
			
			l = gates.getGate(LNK_MEMORY).getLinkAt(0);
			if(l == null)
					logger.warn("PlanCreation has no link to protocol memory. (Expect NullPointerExceptions)");
			memory = (NodeSpaceModule)l.getLinkedEntity();
			
			memoryActRet = (Node)memory.getActRet();
			if(memoryActRet == null)
					logger.warn("PlanCreation: Memory node space has no RET activator (Expect NullPointerExceptions");

			memoryActPor = (Node)memory.getActPor();
			if(memoryActPor == null)
					logger.warn("PlanCreation: Memory node space has no POR activator (Expect NullPointerExceptions");

			memoryDeactivator = (Node)memory.getDeactivator();
			if(memoryDeactivator == null)
					logger.warn("PlanCreation: Memory node space has no deactivator (Expect NullPointerExceptions");
			
			l = gates.getGate(LNK_SITUATIONS).getLinkAt(0);
			if(l == null)
					logger.warn("PlanCreation has no link to situation memory. (Expect NullPointerExceptions)");
			situations = (NodeSpaceModule)l.getLinkedEntity();

			l = gates.getGate(LNK_CURSIT).getLinkAt(0);
			if(l == null)
					logger.warn("PlanCreation has no link to the current situation register. (Expect NullPointerExceptions)");
			currentSituation = (Node)l.getLinkedEntity();

			l = gates.getGate(PLANREG).getLinkAt(0);
			if(l == null)
					logger.warn("PlanCreation has no link to a plan register. (Expect NullPointerExceptions)");
			planReg = (Node)l.getLinkedEntity();

			l = gates.getGate(PLANENDREG).getLinkAt(0);
			if(l == null)
					logger.warn("PlanCreation has no link to a planEnd register. (Expect NullPointerExceptions)");
			planEndReg = (Node)l.getLinkedEntity();
			
			firsttime = false;
		}
		
		switch(state) {
			case STATE_IDLE:
				checkForNewTask(gates);
				break;
			case STATE_SEARCH_REACHABLE:
				searchReachable(gates);
				break;
			case STATE_SEARCH_FRAGMENT:
				searchFragment(gates);
				break;
			case STATE_WAIT_FRAGMENT:
				reorgcounter++;
				if(reorgcounter == 3) {
					state = STATE_SEARCH_FRAGMENT;
					logger.info("SEARCH_FRAGMENT");
					reorgcounter = 0;
				}
				break;
			case STATE_WAIT_REACHABLE:
				reorgcounter++;
				if(reorgcounter == 3) {
					state = STATE_SEARCH_FRAGMENT;
					logger.info("SEARCH_FRAGMENT");
					reorgcounter = 0;
				}				
		}			
	}

	private void done(GateManipulator gates) {
		logger.info("PlanCreation: done");
		gates.setGateActivation(SUCCESS,1.0);
	}
	
	private void fail(GateManipulator gates) {
		logger.info("PlanCreation: failed");
		gates.setGateActivation(FAILURE,1.0);
	}	
	
	private ArrayList allSituations;
	private ArrayList targetOccurrences;
	private ArrayList planElements;
	private ArrayList reachableSituations;
	private ArrayList currentSituationOccurrences;
	private ArrayList toReinforce;
	
	private String currentSituationID;
	
	//@todo: this should obviously be made a question of resolution level 
	private int MAX_REACHABLE_SEARCHDEPTH = 10;

	private void checkForNewTask(GateManipulator gates) throws NetIntegrityException {
		if(trigger.getIncomingActivation() <= 0) return;
		
		Link l = goalListNode.getFirstLinkAt(GateTypesIF.GT_GEN);
		if(l == null) {
			fail(gates);
			logger.debug("PlanCreation could not generate a plan: No goals.");
			return;
		}

		// get the primary goal
		primaryGoal = (Node)l.getLinkedEntity();
		goalNodeSpace = (NodeSpaceModule)structure.findEntity(primaryGoal.getParentID());
		goalSpaceActRet = (Node)goalNodeSpace.getActRet();
		
		if(goalSpaceActRet == null) {
			fail(gates);
			logger.warn("PlanCreation: The primary goal's node space has no RET activator!");
			return;			
		}
		
		// new empty list of links to reinforce
		toReinforce = new ArrayList();
				
		// new empty list of reachable situations -- will be filled during STATE_SEARCH_REACHABALE
		reachableSituations = new ArrayList();		
		
		// new empty list of plan elements
		planElements = new ArrayList();
		
		// the first target, the goal, occurs only once, as itself
		targetOccurrences = new ArrayList();
		targetOccurrences.add(primaryGoal);

		// gather all nodes that represent situations within the protocol memory into an array list
		allSituations = new ArrayList();
		Iterator iter = situations.getAllLevelOneEntities();
		while(iter.hasNext()) {
			NetEntity e = (NetEntity)iter.next();
			if(e.getEntityType() != NetEntityTypesIF.ET_NODE) continue;
			if(((Node)e).getType() != NodeFunctionalTypesIF.NT_CONCEPT) continue;
			
			Iterator surLinks = e.getGate(GateTypesIF.GT_SUR).getLinks();
			while(surLinks.hasNext()) {
				Link surLink = (Link)surLinks.next();
				if(surLink.getLinkedEntity().getParentID().equals(memory.getID())) {
					// this linked entity is really a situations representant in memory
					allSituations.add(surLink.getLinkedEntity());	 
				}
			}
		}
		
		if(allSituations.isEmpty()) {
			fail(gates);
			logger.debug("PlanCreation: No situations found in protocol memory.");
			return;					
		}
		
		// find out the ID of the current "real" situation
		currentSituationID = currentSituation.getFirstLinkAt(GateTypesIF.GT_GEN).getLinkedEntityID();
		
		// and add all occurrences to a list
		currentSituationOccurrences = new ArrayList();
		for(int i=0;i<allSituations.size();i++) {
			Node n = (Node) allSituations.get(i);
			if(n.getFirstLinkAt(GateTypesIF.GT_SUB).getLinkedEntityID().equals(currentSituationID)) {
				currentSituationOccurrences.add(n);				
			}
		}
		
		depth = 0;
		state = STATE_SEARCH_REACHABLE;
		logger.info("SEARCH_REACHABLE");
	}
	
	private void searchReachable(GateManipulator gates) throws NetIntegrityException {
		if(depth >= MAX_REACHABLE_SEARCHDEPTH) {
			depth = 0;
			structure.activateNode(memoryDeactivator.getID(),1.0);
			state = STATE_WAIT_REACHABLE;
			logger.info("WAIT_REACHABLE");
			return;
		}

		for(int i=0;i<allSituations.size();i++) {
			Node e = (Node)allSituations.get(i);
			if(e.isActive()) {
				
				Node reachableSituation = (Node)e.getFirstLinkAt(GateTypesIF.GT_SUB).getLinkedEntity();
				
				boolean found = false;
				for(int j=0;j<reachableSituations.size();j++) {
					if(((ReachableDistance)reachableSituations.get(j)).situation == reachableSituation) {
//						logger.debug("candidate already there: "+((ReachableDistance)reachableSituations.get(j)).situation.getEntityName());
						found = true;
						break;
					}
				}
				if(found) continue;
				
				ReachableDistance rd = new ReachableDistance();
				rd.situation = reachableSituation;
				rd.distance = depth;
				
				reachableSituations.add(rd);
				//logger.debug("reachable: "+e.getEntityName()+" at distance "+depth);		
			}
		}

		// activate POR links
		structure.activateEntity(memoryActPor.getID(),SlotTypesIF.ST_GEN,1.0);
		
		// activate the target occurrences
		for(int i=0;i<currentSituationOccurrences.size();i++) {
			Node n = (Node)currentSituationOccurrences.get(i);
			structure.activateEntity(n.getID(),SlotTypesIF.ST_GEN,1.0);
		}

		depth++;		
		
	}
	

	private void searchFragment(GateManipulator gates) throws NetIntegrityException {
		if(depth >= MAX_DEPTH) {
			fail(gates);
			state = STATE_IDLE;
			logger.info("planning aborted");
			logger.info("SEARCH_IDLE");
			return;
		}
		
		ArrayList candidates = new ArrayList();
		// check if the activation has reached a situation
		for(int i=0;i<allSituations.size();i++) {
			Node e = (Node)allSituations.get(i);
			if(e.isActive() && !targetOccurrences.contains(e) && !planElements.contains(e)) {
				candidates.add(e);
			}
		}
		
		// now check which one of the reached situations is reachable (and if so, in minimal steps)
		Node winner = null;
		int shortestDistance = 100000000;
		for(int i=0;i<candidates.size();i++) {
			Node candidate = (Node) candidates.get(i);
			Node candidatesSituation = (Node) candidate.getFirstLinkAt(GateTypesIF.GT_SUB).getLinkedEntity();
			for(int j=0;j<reachableSituations.size();j++) {
				ReachableDistance rd = (ReachableDistance) reachableSituations.get(j);
				if(rd.situation == candidatesSituation && rd.distance < shortestDistance) {
					shortestDistance = rd.distance;
					winner = candidate;
				}
			}
		}
		
		if(winner != null) {
			String realSituationID = winner.getFirstLinkAt(GateTypesIF.GT_SUB).getLinkedEntityID();
				
			try {					
					
				// the activation from one of the targetOccurrences reached a situation via RET
				// this means that the macro from that situation to the targetOccurrcens should
				// be extracted and added to the plan
				extractPath(gates,winner);
					
				// the target changes now, we're no searching for all occurrences of the situation
				// that e stands for -- recreathe the targetOccurrences list.
				targetOccurrences.clear();
				for(int j=0;j<allSituations.size();j++) {
					Node situationRepresentant = (Node) allSituations.get(j);
					if(situationRepresentant.getFirstLinkAt(GateTypesIF.GT_SUB).getLinkedEntityID().equals(realSituationID)) {
						targetOccurrences.add(situationRepresentant);
							
						// in the future, we don't want no plan elements that lead to that situations, so exclude them
						// from future planning
						planElements.add(situationRepresentant);
					}
				}				
			} catch (MicropsiException exception) {
				logger.error(exception);
			}
				
			if(realSituationID.equals(currentSituationID)) {
				// we're done. Create a main macro node.
					
				String mainMacroNodeID = structure.createConceptNode("plan");	
				structure.createLink(
					planReg.getID(),
					GateTypesIF.GT_GEN,
					mainMacroNodeID,
					SlotTypesIF.ST_GEN,
					1.0,
					1.0
				);
			
				// link the new macro node sub/sur to plan head
				String planHead = planEndReg.getFirstLinkAt(GateTypesIF.GT_GEN).getLinkedEntityID();
				
				structure.createLink(
					mainMacroNodeID,
					GateTypesIF.GT_SUB,
					planHead,
					SlotTypesIF.ST_GEN,
					1.0,
					1.0
				);
		
				structure.createLink(
					planHead,
					GateTypesIF.GT_SUR,
					mainMacroNodeID,
					SlotTypesIF.ST_GEN,
					1.0,
					1.0
				);
						
				// make the mainMacroNode the new planEndNode
				structure.unlinkGate(planEndReg.getID(),GateTypesIF.GT_GEN);
				structure.createLink(
					planEndReg.getID(),
					GateTypesIF.GT_GEN,
					mainMacroNodeID,
					SlotTypesIF.ST_GEN,
					1.0,
					1.0
				);
					
				// and reinforce
				reinforce();					
				done(gates);
				state = STATE_IDLE;
				logger.info("SEARCH_IDLE");
				return;						
			} else {
				state = STATE_WAIT_FRAGMENT;
				structure.activateNode(memoryDeactivator.getID(),1.0);
				logger.info("WAIT_FRAGMENT");
				return;
			}
		}
				
		//@todo: add emotional regulation that stops the process if too many planElements are present

		
		//if we didn't return for some reason: search on.
		
		// activate RET links
		structure.activateEntity(goalSpaceActRet.getID(),SlotTypesIF.ST_GEN,1.0);
		structure.activateEntity(memoryActRet.getID(),SlotTypesIF.ST_GEN,1.0);
		
		// activate the target occurrences
		for(int i=0;i<targetOccurrences.size();i++) {
			Node targetOccurrence = (Node)targetOccurrences.get(i);
			structure.activateEntity(targetOccurrence.getID(),SlotTypesIF.ST_GEN,1.0);
		}
		
		depth++;		
		
	}

	private void reinforce() {
		logger.debug("PlanCreation: Reinforcing "+toReinforce.size()+" links in memory");
		for(int i=0;i<toReinforce.size();i++) {
			Link l = (Link)toReinforce.get(i);
			structure.changeLinkParameters(l,l.getWeight() * REINFORCE_FACTOR,l.getConfidence());		
		}
	}

	protected void extractPath(GateManipulator gates, NetEntity from) throws NetIntegrityException, MicropsiException {
		
		//logger.debug("extracting path from "+from.getID()+" to "+to.getID());
		
		ArrayList path = new ArrayList();
		NetEntity tmp = from;
				
		int counter = 0;
		int EMERGENCY_EXIT = 100;
		
		while(!targetOccurrences.contains(tmp)) {
			path.add(tmp);
			
			//logger.debug("planning checking retwards from: "+tmp.getID());
			
			counter++;
			if(counter == EMERGENCY_EXIT) throw new RuntimeException("cannot find path. fix this.");
				
			Node winner = null;
			double highestActivation = 0;
			
			Iterator iter = tmp.getGate(GateTypesIF.GT_POR).getLinks();
			while(iter.hasNext()) {
				Node candidate = (Node)((Link)iter.next()).getLinkedEntity();
			
				if(	candidate.getGenActivation() > highestActivation &&
					memory.containsEntity(candidate.getID())) {
					winner = candidate;
					highestActivation = winner.getGenActivation();					
				}	
			}
			
			// reinforce links
			
			Link por = tmp.getGate(GateTypesIF.GT_POR).getLinkTo(winner.getID(),SlotTypesIF.ST_GEN);
			Link ret = winner.getGate(GateTypesIF.GT_RET).getLinkTo(tmp.getID(),SlotTypesIF.ST_GEN);

			toReinforce.add(por);
			toReinforce.add(ret);
		
			tmp = winner;			
		}
		
		// and add the "to" node
		path.add(tmp);
		Node to = (Node)tmp;

		// now clone the part of the plan 
		HashMap cloneMappings = (HashMap) structure.cloneEntities(path,structure.getSpace().getID(),NetWeaver.PM_PRESERVE_ALL);
		
		// macro elements are only allowed to have por/rets with other elements of the same macro (level)
		ArrayList toDeleteLinks = new ArrayList();
		Iterator iter = cloneMappings.values().iterator();
		while(iter.hasNext()) {
			String nodeID = (String)iter.next();
			Node macroNode = (Node)structure.findEntity(nodeID);
			Iterator porlinks = macroNode.getGate(GateTypesIF.GT_POR).getLinks();
			while(porlinks.hasNext()) {
				Link porlink = (Link)porlinks.next();
				if(!cloneMappings.containsValue(porlink.getLinkedEntityID())) toDeleteLinks.add(porlink);
			}
			Iterator retlinks = macroNode.getGate(GateTypesIF.GT_RET).getLinks();
			while(retlinks.hasNext()) {
				Link retlink = (Link)retlinks.next();
				if(!cloneMappings.containsValue(retlink.getLinkedEntityID())) toDeleteLinks.add(retlink);
			}
		}
		
		// macro nodes need to be pretty "clean", there's no use for incoming links that
		// have nothing todo with the macro's structure
		iter = cloneMappings.values().iterator();
		while(iter.hasNext()) {
			String nodeID = (String)iter.next();
			Node macroNode = (Node)structure.findEntity(nodeID);
			Iterator links = macroNode.getSlot(SlotTypesIF.ST_GEN).getIncomingLinks();
			while(links.hasNext()) {
				Link l = (Link)links.next();
				if( l.getLinkingGate().getType() == GateTypesIF.GT_SUB ||
					l.getLinkingGate().getType() == GateTypesIF.GT_SUR) continue;
					
				if(cloneMappings.containsValue(l.getLinkingEntity().getID())) continue;
				
				if(!toDeleteLinks.contains(l)) toDeleteLinks.add(l);
			}
		}
		
		
		for(int i=0;i<toDeleteLinks.size();i++) {
			Link l = (Link)toDeleteLinks.get(i);
			structure.deleteLink(
				l.getLinkingEntity().getID(),
				l.getLinkingGate().getType(),
				l.getLinkedEntityID(),
				l.getLinkedSlot().getType()
			);
		}		
		
		// create a macro node for the plan part
		String newMacroNodeID = structure.createConceptNode("plan-element");
		
		// mark it as part of the plan
		structure.createLink(
			planReg.getID(),
			GateTypesIF.GT_GEN,
			newMacroNodeID,
			SlotTypesIF.ST_GEN,
			1.0,
			1.0
		);
		
		// and mark all parts as parts of the plan
		for(int i=0;i<path.size();i++) {
			String elementID = (String)cloneMappings.get(((Node)path.get(i)).getID());
			structure.createLink(
				planReg.getID(),
				GateTypesIF.GT_GEN,
				elementID,
				SlotTypesIF.ST_GEN,
				1.0,
				1.0
			);					
		}
										
		// link the new macro node por/ret to existing plan
		Link planEndNodeLink = planEndReg.getFirstLinkAt(GateTypesIF.GT_GEN);
		if(planEndNodeLink != null) { // if this is null, the plan was just started
			
			String planEndNodeID = planEndNodeLink.getLinkedEntityID(); 
		
			structure.createLink(
				newMacroNodeID,
				GateTypesIF.GT_POR,
				planEndNodeID,
				SlotTypesIF.ST_GEN,
				1.0,
				1.0
			);
		
			structure.createLink(
				planEndNodeID,
				GateTypesIF.GT_RET,
				newMacroNodeID,
				SlotTypesIF.ST_GEN,
				1.0,
				1.0
			);
		}
		
		// make the newMacroNode the new planEndNode
		structure.unlinkGate(planEndReg.getID(),GateTypesIF.GT_GEN);
		structure.createLink(
			planEndReg.getID(),
			GateTypesIF.GT_GEN,
			newMacroNodeID,
			SlotTypesIF.ST_GEN,
			1.0,
			1.0
		);
		
		// get the clone of the first and the last element
		String beginElementID = (String)cloneMappings.get(from.getID());
		String endElementID = (String)cloneMappings.get(to.getID());		
				
		// link the first sur/sub to its macro node

		structure.createLink(
			newMacroNodeID,
			GateTypesIF.GT_SUB,
			beginElementID,
			SlotTypesIF.ST_GEN,
			1.0,
			1.0
		);
		
		structure.createLink(
			beginElementID,
			GateTypesIF.GT_SUR,
			newMacroNodeID,
			SlotTypesIF.ST_GEN,
			1.0,
			1.0
		);
	}
}
