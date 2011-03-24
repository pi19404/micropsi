package org.micropsi.nodenet.modules;

import java.util.ArrayList;
import java.util.Iterator;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class RetroReinforcer extends AbstractNativeModuleImpl {

	// gate types
	private static final int REINFORCEREG		= 19000;
	private static final int CHAINREG			= 19001;
	
	// slot types
	private static final int INTENSITY		= 19000;
	
	private boolean firsttime = true;
//	private Slot intensity;
	
	private final int[] gateTypes = { 
		REINFORCEREG,
		CHAINREG
	};

	private final int[] slotTypes = {
		INTENSITY
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
				case INTENSITY:
//					intensity = slots[i];
					break;
			}
		}
	}
	
	public RetroReinforcer() {
				
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			
			private static final String id = "retroreinforcer";

			public String getExtensionID() {
				return id;
			}

			public String gateType(int type) {
				switch(type) {
					case REINFORCEREG:	return "ReinforceReg";
					case CHAINREG:		return "ChainReg";
					default:			return null;
				}
			}

			public String slotType(int type) {
				switch(type) {
					case INTENSITY:		return "Intensity";
					default:			return null;
				}				
			}
		});
		
	}
	
	public void calculate(Slot[] slots, GateManipulator gates, long netstep) throws NetIntegrityException {
		if(firsttime) {
			catchSlots(slots);
			firsttime = false;			
		}
	
		//if(intensity.getIncomingActivation() <= 0) return;
		
		Link l = gates.getGate(REINFORCEREG).getLinkAt(0);
		if(l == null) {
			logger.warn("RetroReinforcer has no reinforcereg link!");
			return;
		}

		l = gates.getGate(CHAINREG).getLinkAt(0);
		if(l == null) {
			logger.warn("RetroReinforcer has no chainreg link!");
			return;
		}
		
		Node chainRegister = (Node)l.getLinkedEntity();
		
		Node startRegister = (Node)l.getLinkedEntity(); 
		Link rl = startRegister.getGate(GateTypesIF.GT_GEN).getLinkAt(0);
		if(rl == null && chainRegister.getFirstLinkAt(GateTypesIF.GT_GEN) == null) return;
		
		Node startNode = (Node)rl.getLinkedEntity();
		
		ArrayList chain = new ArrayList();
		Iterator iter = chainRegister.getGate(GateTypesIF.GT_GEN).getLinks();
		while(iter.hasNext()) {
			Link nextLink = (Link)iter.next();
			//logger.debug("in chain: "+nextLink.getLinkedEntityID());
			chain.add(nextLink.getLinkedEntity());
		}
		
		logger.debug("Reinforcing links between "+chain.size()+" elements, backwards from: "+startNode);
		
		double factor = 1.5;
		
		boolean stop = false;
		Node currentNode = startNode;
		while(!stop) {
			Iterator retlinks = currentNode.getGate(GateTypesIF.GT_RET).getLinks();
			while(retlinks.hasNext()) {
				Link retlink = (Link)retlinks.next();
				Node linked = (Node)retlink.getLinkedEntity();
				if(chain.contains(linked)) {
					// change ret link
					structure.changeLinkParameters(retlink,retlink.getWeight() * factor, retlink.getConfidence());
					
					//get and change also por link
					Link porlink = linked.getGate(GateTypesIF.GT_POR).getLinkTo(currentNode.getID(),SlotTypesIF.ST_GEN);
					structure.changeLinkParameters(porlink,porlink.getWeight() * factor, porlink.getConfidence());
					
					currentNode = linked;
					break;			
				} else {
					stop = true;
				}
			}
		}
	
		structure.unlinkGate(startRegister.getID(),GateTypesIF.GT_GEN);
		structure.unlinkGate(chainRegister.getID(),GateTypesIF.GT_GEN);
		
	}
}
