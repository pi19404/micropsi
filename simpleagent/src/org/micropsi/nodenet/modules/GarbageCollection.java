package org.micropsi.nodenet.modules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.Gate;
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
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class GarbageCollection extends AbstractNativeModuleImpl {

	// slots
	public static final int TRIGGER	= 15000;
	
	// gates
	public static final int DONE	= 15000;
	public static final int PROTECT	= 15001;
	
	// technical stuff
	private boolean firsttime = true;
	private Random random = new Random();
	private String selfID;
	
	private Slot trigger;

	private final int[] gateTypes = {
		PROTECT,
		DONE
	};

	private final int[] slotTypes = {
		TRIGGER
	};
	
	public GarbageCollection() {
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			public String getExtensionID() {
				return "garbagecollection";
			}
			public String gateType(int type) {
				switch(type) {
					case PROTECT:			return "Protect";
					case DONE:				return "Done";
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

	protected int[] getGateTypes() {
		return gateTypes;
	}

	protected int[] getSlotTypes() {
		return slotTypes;
	}
	
	private void catchSlots(Slot[] slots) {	
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
				case TRIGGER :
					trigger = slots[i];
					break;
			}
		}
		
		selfID = trigger.getNetEntity().getID();
	}
	
	private long lastcollection = 0;
	private int COLLECTION_INTERVAL = 10;

	public void calculate(Slot[] slots, GateManipulator gates, long netstep) throws NetIntegrityException {
		if(firsttime) {
			catchSlots(slots);
			firsttime = false;
		}
		
		//if(trigger.getIncomingActivation() <= 0) return;
		if(netstep < lastcollection + COLLECTION_INTERVAL) return;
		
		lastcollection = netstep;
		
		ArrayList garbage = new ArrayList();
		
		NodeSpaceModule space = structure.getSpace();
		Iterator entities = space.getAllLevelOneEntities();
		while(entities.hasNext()) {
			NetEntity e = (NetEntity)entities.next();
			
			if(e.getEntityType() != NetEntityTypesIF.ET_NODE) continue;
			if(((Node)e).getType() != NodeFunctionalTypesIF.NT_CONCEPT) continue;
			
			e.updateDecayState();
		
			boolean deleteit = true;
			Gate por = e.getGate(GateTypesIF.GT_POR);
			for(int i=0;i<por.getNumberOfLinks();i++) {
				if(por.getLinkAt(i).getLinkedEntity().getParentID().equals(space.getID())) {
//					logger.info("Node has por link within the same space: "+e.getID()+" weight: "+por.getLinkAt(i).getWeight());
					deleteit = false;
					break;
				}
			}

			Gate ret = e.getGate(GateTypesIF.GT_RET);
			for(int i=0;i<ret.getNumberOfLinks();i++) {
				if(ret.getLinkAt(i).getLinkedEntity().getParentID().equals(space.getID())) {
//					logger.info("Node has ret link within the same space: "+e.getID());
					deleteit = false;
					break;
				}
			}
			
			if(deleteit) {
				Iterator iter = gates.getGate(PROTECT).getLinks();
				while(iter.hasNext()) {
					Link l = (Link)iter.next();
					if(l.getLinkedEntityID().equals(e.getID())) {
						deleteit = false;
						break;
					}
				}
				
			}
			
			if(deleteit) garbage.add(e.getID());

		}
		
		for(int i=0;i<garbage.size();i++) {
			String id = (String)garbage.get(i);
			structure.deleteEntity(id);
			
			logger.debug("Garbage collected node: "+id);
		}
		
		gates.setGateActivation(DONE,1.0);
		
	}

}
