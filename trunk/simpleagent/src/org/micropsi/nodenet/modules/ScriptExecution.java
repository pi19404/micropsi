package org.micropsi.nodenet.modules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.LinkST;
import org.micropsi.nodenet.LinkTypesIF;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.NodeSpaceModule;
import org.micropsi.nodenet.RegisterNode;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

/**
 * 
 * 
 * 
 */
public class ScriptExecution extends AbstractNativeModuleImpl implements GateTypesIF {

	class MakroStackElement {
		
		public String makroName = "";
		public Node branchPoint = null;
		public boolean dontEnter = false;
		public ArrayList chosenAlternatives = new ArrayList();
		
		public String toString() {
			String toReturn = "";
			
			toReturn += (makroName.equals("")) ? "(no name)" : makroName;
			toReturn += " ";
			toReturn += (branchPoint == null) ? "(no branches)" : branchPoint.getID();
			toReturn += " ";
			toReturn += " checked("+chosenAlternatives.size()+")";
			
			return toReturn;
		}
		
	}

	// slots

	public static final int FACTSIG_SCRIPT_ACT		=	4000;
	public static final int SIG_ABORT				=	4001;
	public static final int DEBUG					=	4002;

	// gates	
	
	public static final int EXE_IDLE		=	4000;
	public static final int EXE_PRGREGISTER	=	4002;
	public static final int EXE_SUCCESS		=	4003;
	public static final int EXE_FAILURE		=	4004;
	public static final int EXE_ABORT		=	4005;
	public static final int EXE_MACRO		=	4006;
	public static final int CURRENT_REG		=	4007;	

	// states
	
	private final int STATE_IDLE = 0;
	private final int STATE_NORMAL_EXE = 1;

	private int state = STATE_IDLE;
	private int waitcounter = 0;

	private boolean firsttime = true;
	private Stack makroStack = new Stack();

	private Slot abortSignal;
	private Slot scrActSignal;
	private Slot debug;
	private GateManipulator modgates;
	
	private final int[] gateTypes = {
		CURRENT_REG,
		EXE_PRGREGISTER,
		EXE_MACRO,		
		EXE_IDLE,
		EXE_SUCCESS,
		EXE_FAILURE,
		EXE_ABORT
		 
	};

	private final int[] slotTypes =	{ 
		SIG_ABORT,
		FACTSIG_SCRIPT_ACT,
		DEBUG
	};

	protected int[] getGateTypes() {
		return gateTypes;
	}

	protected int[] getSlotTypes() {
		return slotTypes;
	}

	private void catchSlots(Slot[] slots) {
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
				case SIG_ABORT :
					abortSignal = slots[i];
					break;
				case FACTSIG_SCRIPT_ACT :
					scrActSignal = slots[i];
					break;
				case DEBUG:
					debug = slots[i];
					break;
			}
		}
	}
	
	protected void abortScriptExecution() throws NetIntegrityException {
		state = STATE_IDLE;
		modgates.setGateActivation(EXE_ABORT, 1.0);

		Link l = modgates.getGate(CURRENT_REG).getLinkAt(0);
		if(l != null) structure.unlinkGate(l.getLinkedEntityID(),GateTypesIF.GT_GEN);

		modgates.unlinkGate(EXE_IDLE);
		
		makroStack.clear();
	}

	public Node backtrackFail(Node currentNode) throws NetIntegrityException {
		
//		logger.debug("backtrack-fail from "+currentNode.getID());
		
		modgates.setGateActivation(EXE_FAILURE, 0.5);

		makroStack.pop();

		if(!makroStack.empty()) {
			return ((MakroStackElement)makroStack.peek()).branchPoint;
		} else {
			return null;
		}
		
	}
	
	public Node backtrackSuccess(Node currentNode) throws NetIntegrityException {
		
//		logger.debug("backtrack-success from "+currentNode.getID());
		
		modgates.setGateActivation(EXE_SUCCESS, 0.5);
		
		makroStack.pop();
			
		if(!makroStack.empty()) {
			((MakroStackElement)makroStack.peek()).dontEnter = true;
			return ((MakroStackElement)makroStack.peek()).branchPoint;
		} else {
			return null;
		}

	}
	
	public Node decidePath(Node currentNode, GateManipulator currGates) throws NetIntegrityException {
		Node nextNode = null;
		
/*		logger.debug("---------------------------------------> DecidePath");
		logger.debug("Makrostack: ");
		Enumeration e = makroStack.elements();
		while(e.hasMoreElements()) logger.debug(" - "+e.nextElement());
		
		logger.debug("CurrentNode: "+currentNode.getID());
		logger.debug("WaitCounter: "+waitcounter);		
*/		
		// there's no use in backtracking, as the last backtracking reached
		// the highest level and there are no more nodes 
		//if(makroStack.isEmpty()) return null;  
		
		MakroStackElement stackElement = (MakroStackElement)makroStack.peek();
		ArrayList failed = stackElement.chosenAlternatives;
			
		/*
		 * Check if there are SUB-linked nodes. If there are, choose the most
		 * active one, return it as next node and activate the SUB gate
		 */
		 double highestActivation = 0;
		 
		 
		 // only enter makros if the dontEnter flag is not set (the flag is set
		 // by backtrack-success) - so execution will follow the por-path after the
		 // successfull execution of a makro
		 
		 Link nextNodeLink = null;
		 if(!stackElement.dontEnter) {
		 	Iterator subGate = currentNode.getGate(GateTypesIF.GT_SUB).getLinks();
			while(subGate.hasNext()) {
				nextNodeLink = (Link)subGate.next();
		 		Node node = (Node)nextNodeLink.getLinkedEntity();
		 		 	
		 		double act = node.getGenActivation();
		 		act = act * nextNodeLink.getWeight() * nextNodeLink.getConfidence();
			
				if(highestActivation <= act && !failed.contains(node.getID())) {
	 				nextNode = node;
	 				highestActivation = act;
		 		}
		 	} 
		 
		 	if(nextNode != null) {
		 			 	
//		 		logger.debug("Next node is sub-linked: "+nextNode);
				
				// strengthen link to next node
				double factor = ((NodeSpaceModule)structure.findEntity(nextNode.getParentID())).getStrengtheningConstant(); 
				structure.changeLinkParameters(nextNodeLink,nextNodeLink.getWeight() * factor, nextNodeLink.getConfidence());
				
				// strengthen lonk from the next node
				Link surLink = nextNode.getGate(GateTypesIF.GT_SUR).getLinkTo(currentNode.getID(),SlotTypesIF.ST_GEN);
				structure.changeLinkParameters(surLink,surLink.getWeight() * factor, surLink.getConfidence());
		 			 	
			 	stackElement.branchPoint = currentNode;
			 	stackElement.chosenAlternatives.add(nextNode.getID());
		 	
			 	MakroStackElement newElement = new MakroStackElement();
			 	newElement.makroName = "makro-"+nextNode.getEntityName();
			 	makroStack.push(newElement);
		 	
//				currGates.setGateFactor(GateTypesIF.GT_SUB, 1.0);
				modgates.setGateActivation(EXE_MACRO, 1.0);
				waitcounter = 0;
				return nextNode;		 	
			 } else if(currentNode.getGate(GateTypesIF.GT_SUB).hasLinks()) {
			 	// if there are links and there is no nextNode, all links have
			 	// failed already and it's time to fail-backtrack.
			 	
			 	waitcounter = 0;
			 	return backtrackFail(currentNode);
			 }
		 }
		 	
	 		 
		/*
		 * if there are no POR links, the makro is at it's end and was
		 * successfully executed
		 */
		 if(!currentNode.getGate(GateTypesIF.GT_POR).hasLinks()) {
		 	
//		 	logger.debug("No por links, success!");
		 	
		 	waitcounter = 0;
			return backtrackSuccess(currentNode);
		 }
			
		/*
		 * As there are no makros, continue on the POR-path. If there are POR-
		 * linked nodes, choose the most active one, return it and activate the
		 * POR gate.
		 */

		highestActivation = 0;
		
		nextNodeLink = null; 
		Iterator porLinks = currentNode.getGate(GateTypesIF.GT_POR).getLinks();
		while(porLinks.hasNext()) { 
			Link l = (Link)porLinks.next();
			Node node = (Node)l.getLinkedEntity();	   	   
			double act = node.getGenActivation();		   	   		   
			act = act * l.getWeight() * l.getConfidence();		   	   		   
			if(act > 0) {
			   if(highestActivation < act) {
				   nextNode = node;
				   nextNodeLink = l;
				   highestActivation = act;
				}
			
			} else if(act < 0) {
				
				// activations below zero immediately fail the macro
				
				waitcounter = 0;
				return backtrackFail(currentNode);
			}
		}
		 
		if(nextNode != null) {
			
			// strengthen link to next node
			double factor = ((NodeSpaceModule)structure.findEntity(nextNode.getParentID())).getStrengtheningConstant();
			structure.changeLinkParameters(nextNodeLink,nextNodeLink.getWeight() * factor, nextNodeLink.getConfidence());
				
			// strengthen link from the next node
			Link retLink = nextNode.getGate(GateTypesIF.GT_RET).getLinkTo(currentNode.getID(),SlotTypesIF.ST_GEN);
			structure.changeLinkParameters(retLink,retLink.getWeight() * factor, retLink.getConfidence());
			
//			logger.debug("Next node is por-linked: "+nextNode);
		   
			//currGates.setGateFactor(GateTypesIF.GT_POR, 1.0);
			waitcounter = 0;
		   
			// reset the list of checked alternatives
			((MakroStackElement)makroStack.peek()).chosenAlternatives.clear();
			((MakroStackElement)makroStack.peek()).dontEnter = false;
		   
			return nextNode;		 	
		} else {
			// if there is no active node: check if it's ok to wait any longer or no wait annotations are given
			
			int maxwait = 0;
			
			Link firstPorLink = null;
			porLinks = currentNode.getGate(GateTypesIF.GT_POR).getLinks();
			while(porLinks.hasNext()) {
				Link l = (Link)porLinks.next();
				if(firstPorLink == null) firstPorLink = l;
				if(l.getType() != LinkTypesIF.LINKTYPE_SPACIOTEMPORAL) continue;
				LinkST stl = (LinkST)l;
			 	if(stl.getT() > maxwait)
					maxwait = stl.getT();
			}
			
			// if there are no wait annotations, just go on.
			if(maxwait == 0) {
				waitcounter = 0;
				// reset the list of checked alternatives
				((MakroStackElement)makroStack.peek()).chosenAlternatives.clear();
				((MakroStackElement)makroStack.peek()).dontEnter = false;
				return (Node)firstPorLink.getLinkedEntity();
			} 
						
			if(debug.getIncomingActivation() > 0) logger.debug("script waiting... ("+waitcounter+" out of "+maxwait+")");
//			logger.debug("Waiting: "+waitcounter+" of "+maxwait);
			
			if(maxwait > waitcounter) return currentNode;	   
		}
				 
		/*
		 * no more alternatives - fail!
		 */
		 
//		 logger.debug("No more alternatives, fail.");
		 		
		waitcounter = 0;
		return backtrackFail(currentNode);
		 
	}
	
	public ScriptExecution() {

		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			public String getExtensionID() {
				return "scriptexecution";
			}
			public String gateType(int type) {
				switch(type) {
					case CURRENT_REG:		return "CurrentReg";
					case EXE_PRGREGISTER:	return "PrgReg";
					case EXE_MACRO:			return "Macro";
					case EXE_IDLE:			return "Idle";
					case EXE_FAILURE:		return "Failure";
					case EXE_SUCCESS:		return "Success";			
					case EXE_ABORT:			return "FailAbort";			
					default:				return null;
				}				
			}
			public String slotType(int type) {
				switch(type) {
					case FACTSIG_SCRIPT_ACT:	return "ScriptAct";
					case SIG_ABORT:				return "Abort";
					case DEBUG:					return "Debug";
					default: 					return null;
				}
			}
		});
		
	}	 



	public void calculate(Slot[] slots, GateManipulator gates, long netstep) throws NetIntegrityException {
		
		try {
		
			if (firsttime) {
				catchSlots(slots);
				modgates = gates;
				firsttime = false;
			}
			
			if(abortSignal.getIncomingActivation() > 0) abortScriptExecution();
		
			double currentWill = scrActSignal.getIncomingActivation();
			
			// check for a current node
			Node currentNode = null;
			RegisterNode currentReg = null;
			Link currentRegLink = gates.getGate(CURRENT_REG).getLinkAt(0);
			
			if(currentRegLink == null) {
				state = STATE_IDLE;
			} else {
				currentReg = (RegisterNode)currentRegLink.getLinkedEntity(); 
			
				Link currentNodeLink = currentReg.getFirstLinkAt(GateTypesIF.GT_GEN);

				// perhaps we're idling and someone connected a node to the idle gate
				if(currentNodeLink == null)
					currentRegLink = gates.getGate(EXE_IDLE).getLinkAt(0);
							
				// if there is no current node, there's nothing to do
				if(currentNodeLink == null) {
					state = STATE_IDLE;
				} else {				
					currentNode = (Node)currentNodeLink.getLinkedEntity();
				}
			}
							
			switch(state) {
				case STATE_IDLE:
					if(currentNode != null) {
						state = STATE_NORMAL_EXE;
						structure.activateNode(currentNode.getID(), currentWill);
						modgates.setGateActivation(EXE_PRGREGISTER, currentWill);
						Link regLnk = modgates.getGate(EXE_PRGREGISTER).getLinkAt(0);
						if(regLnk != null) 
							structure.activateNode(regLnk.getLinkedEntity().getID(), currentWill);
						
						MakroStackElement rootMakro = new MakroStackElement();
						rootMakro.makroName = "main";
						makroStack.empty();
						makroStack.push(rootMakro);
						
						modgates.setGateActivation(EXE_MACRO, 1.0);
					} else {
						gates.setGateActivation(EXE_IDLE, 1.0);
					}
					break;
				case STATE_NORMAL_EXE:
												
					structure.unlinkGate(currentReg.getID(),GateTypesIF.GT_GEN);
					
					modgates.unlinkGate(EXE_IDLE);
					modgates.setGateActivation(EXE_PRGREGISTER, currentWill);
					
					GateManipulator currentNodeGates = structure.getGateManipulator(currentNode.getID());
					
					Node nextNode = decidePath(currentNode,currentNodeGates);
					
					if(nextNode == null) {
						
						if(makroStack.size() != 0) {
							// script failed
							gates.setGateActivation(EXE_ABORT, 1.0);		
							gates.setGateActivation(EXE_FAILURE, 1.0);
							
//							logger.debug("Script failed at "+currentNode+". Last Macrostack entry: "+makroStack.get(makroStack.size()-1));
							
						} else {					
							// script successfull
							gates.setGateActivation(EXE_IDLE, 1.0);
							gates.setGateActivation(EXE_SUCCESS, 1.0);
							
//							logger.debug("Script success at "+currentNode);
							
						}
						
						makroStack.clear();
						state = STATE_IDLE;
							
					} else if(nextNode == currentNode) {
						waitcounter++; 

						// re-link the same Node
						structure.createLink(
							currentReg.getID(),
							GateTypesIF.GT_GEN,
							nextNode.getID(),
							SlotTypesIF.ST_GEN,
							1.0,
							1.0
						);
	
					} else	{
	
						// link the new currentNode
						structure.createLink(
							currentReg.getID(),
							GateTypesIF.GT_GEN,
							nextNode.getID(),
							SlotTypesIF.ST_GEN,
							1.0,
							1.0
						);
						
						structure.activateEntity(nextNode.getID(),SlotTypesIF.ST_GEN,currentWill);
						if(debug.getIncomingActivation() > 0) logger.debug("CurrentNode: "+nextNode.getEntityName());					
					}
																	
					break;
			}
	
		} catch (ClassCastException e) {
			
			abortScriptExecution();
			
			logger.warn("ClassCastException. Probably an attempt to execute a script" +
				"that contained non-node entities - fix it! Script execution module "+
				gates.getGate(EXE_IDLE).getNetEntity().getID()
				+" reset.");
		}

	}

}
