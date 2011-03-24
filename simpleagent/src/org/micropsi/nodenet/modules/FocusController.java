package org.micropsi.nodenet.modules;

import java.util.ArrayList;
import java.util.Iterator;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.LinkST;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.RegisterNode;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.Situation;
import org.micropsi.nodenet.agent.SituationElement;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class FocusController extends AbstractNativeModuleImpl {

	private static final int ATTNEXT	= 14000;
	private static final int ATTPREV	= 14001;
	private static final int RESET		= 14002;

	private static final int ATTREG	= 14000;
	private static final int SITREG	= 14001;
	private static final int SUCCESS	= 14002;

	private final int[] gateTypes = {
		ATTREG,
		SITREG,
		SUCCESS
	};

	private final int[] slotTypes = {
		ATTNEXT,
		ATTPREV,
		RESET
	};
	
	private boolean firsttime = true;
	
	private Slot attnext;
	private Slot attprev;
	private Slot reset;
	
	protected int[] getGateTypes() {
		return gateTypes;
	}

	protected int[] getSlotTypes() {
		return slotTypes;
	}

	private void catchSlots(Slot[] slots) {	
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
				case ATTNEXT:
					attnext = slots[i];
					break;
				case ATTPREV:
					attprev = slots[i];
					break;
				case RESET:
					reset = slots[i];
					break;
			}
		}
	}
	
	public FocusController() {
		
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

				private static final String id = "focuscontroller";

				public String getExtensionID() {
					return id;
				}

				public String gateType(int type) {
					switch(type) {
						case ATTREG:		return "AttReg";
						case SITREG:		return "SitReg";
						case SUCCESS:		return "Success";
						default:			return null;
					}
				}

				public String slotType(int type) {
					switch(type) {
						case ATTNEXT:		return "AttNext";
						case ATTPREV:		return "AttPrev";
						case RESET:			return "AttReset";
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
		
		// hack hack, was ist hier mit dem innerstate objekt los?
		
		//innerstate.ensureStateExistence("situation","micropsi");
		Situation situation = //Situation.getInstance(innerstate.getStateString("situation"));
			Situation.getInstance("micropsi");
		
		double highest = 0;
		Slot highestActivated = null;
		if(attnext.getIncomingActivation() > highest) {
			highestActivated = attnext;
			highest = attnext.getIncomingActivation();
		}
		if(attprev.getIncomingActivation() > highest) {
			highestActivated = attprev;
			highest = attprev.getIncomingActivation();
		}
		if(reset.getIncomingActivation() > highest) {
			highestActivated = reset;
			highest = reset.getIncomingActivation();
		}
		if(highestActivated == null) return;
		

		Link attentionRegLink = gates.getGate(ATTREG).getLinkAt(0);
		if(attentionRegLink == null) return;
		
		RegisterNode attentionReg = (RegisterNode)attentionRegLink.getLinkedEntity();


		Link situationRegLink = gates.getGate(SITREG).getLinkAt(0);
		if(situationRegLink == null) return;
		
		RegisterNode situationReg = (RegisterNode)situationRegLink.getLinkedEntity();
		
		Link currentSituationLink = situationReg.getFirstLinkAt(GateTypesIF.GT_GEN);
		if(currentSituationLink == null) return;

		Node currentSituation = (Node)currentSituationLink.getLinkedEntity();		
		
		Node currentAttention = null;
		Link currentAttentionLink = attentionReg.getFirstLinkAt(GateTypesIF.GT_GEN);
		if(currentAttentionLink != null) currentAttention = (Node)currentAttentionLink.getLinkedEntity();
				
		// retrieve the subField of the current situation, the things that are the case and
		// can be focussed
		ArrayList subField = new ArrayList();
		Iterator subLinks = currentSituation.getGate(GateTypesIF.GT_SUB).getLinks();
		while(subLinks.hasNext())
			subField.add(((Link)subLinks.next()).getLinkedEntity());
				
		switch(highestActivated.getType()) {
			case ATTNEXT:
				if(currentAttention != null && subField.contains(currentAttention)) {
					// find the next node		
					
					for(int i=0;i<subField.size();i++) {
						if(subField.get(i) == currentAttention) {
							if(i<(subField.size()-1)) {
								NetEntity tmp = (NetEntity) subField.get(i+1);
								attentionAtElement(tmp,situation);
								success(tmp, attentionReg, gates,situation);
								break;
							}
						}
					}
				} else {
					// find the first node
					for(int i=0;i<subField.size();i++) {
						Node tmp = (Node)subField.get(i);
						
						// tmp is the first node if it has no ret-linked nodes in the subField
						
						Iterator iter = tmp.getGate(GateTypesIF.GT_RET).getLinks();
						boolean found = false;
						while(iter.hasNext()) {
							NetEntity linked = ((Link)iter.next()).getLinkedEntity();
							if(subField.contains(linked)) found = true;
						}
						if(!found) {
							// TODO fixme situation.attentionAtHomePosition();
							success(tmp,attentionReg,gates,situation);
							break;
						}
					}
				}
				break;
			case ATTPREV:
				if(currentAttention != null && subField.contains(currentAttention)) {					
					// find the previous node		

					for(int i=0;i<subField.size();i++) {
						if(subField.get(i) == currentAttention) {
							if(i>-1) {
								NetEntity tmp = (NetEntity) subField.get(i-1);
								attentionAtElement(tmp,situation);
								success(tmp, attentionReg, gates,situation);
								break;
							} else {
								// return focus to the situation, that is currently, just somwhere
								// TODO fixme situation.attentionAtHomePosition();
								structure.unlinkGate(attentionReg.getID(),GateTypesIF.GT_GEN);
								logger.info("attention undefined now (no previous element)");								
							}
						}
					}
				} 
				break;
			case RESET:
				// TODO fixme situation.attentionAtHomePosition();
				structure.unlinkGate(attentionReg.getID(),GateTypesIF.GT_GEN);
				// TODO fixme SituationElement e = situation.getAttentionElement();
				// TODO fixme logger.info("attention undefined now (reset request): "+e);				
				break;
		}
		

	}
	
	private void attentionAtElement(NetEntity newAttention, Situation situation) throws NetIntegrityException {
		
		// hack hack hack
		logger.info("move attention to "+newAttention.getEntityName());
		
		NetEntity look = newAttention.getGate(GateTypesIF.GT_SUB).getLinkAt(0).getLinkedEntity();
		NetEntity fovea_h = look.getGate(GateTypesIF.GT_SUB).getLinkAt(0).getLinkedEntity();
		NetEntity fovea_v = look.getGate(GateTypesIF.GT_SUB).getLinkAt(1).getLinkedEntity();
		
		LinkST hlink = (LinkST)fovea_h.getGate(GateTypesIF.GT_SUB).getLinkAt(0);
		LinkST vlink = (LinkST)fovea_v.getGate(GateTypesIF.GT_SUB).getLinkAt(0);
		
		// TODO fixme situation.attentionAtHomePosition();
		// TODO fixme situation.attentionRight(hlink.getX());
		// TODO fixme situation.attentionDown(vlink.getY());
		
	}

	private void success(NetEntity newAttention, RegisterNode attentionReg, GateManipulator gates, Situation situation) throws NetIntegrityException {
		
		// TODO fixme logger.info("attention is now on: "+newAttention.getEntityName()+" ("+situation.getAttentionElement().getType()+")");
		
		structure.unlinkGate(attentionReg.getID(),GateTypesIF.GT_GEN);
		
		structure.createLink(
			attentionReg.getID(),
			GateTypesIF.GT_GEN,
			newAttention.getID(),
			SlotTypesIF.ST_GEN,
			1.0,
			1.0
		);
		
		gates.setGateActivation(SUCCESS,1.0);		
		
	}

}
