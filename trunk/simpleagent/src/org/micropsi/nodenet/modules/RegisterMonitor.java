package org.micropsi.nodenet.modules;

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

public class RegisterMonitor extends AbstractNativeModuleImpl {
	
	private static final int MONITOR	= 5004;
	private static final int ONCHANGE	= 5000;
	private static final int ONEMPTY	= 5003;
	private static final int LINKTO		= 5001;
	private static final int DETECT		= 5002;
	
	private static final int TRIGGER	= 5000;

	private final int[] gateTypes = {
		MONITOR,
		ONCHANGE,
		ONEMPTY,
		LINKTO,
		DETECT
	};

	private final int[] slotTypes = {
		TRIGGER
	};
	
	private boolean firsttime = true;
	private Slot force;
	
	protected int[] getGateTypes() {
		return gateTypes;
	}

	protected int[] getSlotTypes() {
		return slotTypes;
	}
	
	public RegisterMonitor() {
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "registermonitor";

			public String getExtensionID() {
				return id;
			}

			public String gateType(int type) {
				switch(type) {
					case MONITOR:		return "Monitor";
					case ONCHANGE:		return "OnChange";
					case ONEMPTY:		return "OnEmpty";
					case LINKTO:		return "LinkTo";
					case DETECT:		return "Detect";
					default: 			return null;
				}
			}

			public String slotType(int type) {
				switch(type) {
					case TRIGGER:		return "Trigger";
					default: 			return null;
				}
			}
		});
		
	}
	
	private void catchSlots(Slot[] slots) {	
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
				case TRIGGER:
					force = slots[i];
					break;
			}
		}
	}

	ArrayList previous;
	
	public void calculate(Slot[] slots, GateManipulator gates, long netstep) throws NetIntegrityException {
		if(firsttime) {
			catchSlots(slots);
			previous = new ArrayList();
			firsttime = false;
		}

		Link onChangeLink = gates.getGate(ONCHANGE).getLinkAt(0);
		if(onChangeLink != null) {
			NetEntity onChange = onChangeLink.getLinkedEntity();

			ArrayList currentlyLinked = new ArrayList();
			Iterator currLinks = onChange.getGate(GateTypesIF.GT_GEN).getLinks();
			while(currLinks.hasNext()) {
				Link l = (Link)currLinks.next();
				currentlyLinked.add(l.getLinkedEntityID());
			}
						
			if(!previous.containsAll(currentlyLinked) || force.getIncomingActivation() > 0) {
				// new links occurred, make copy at copyReg
							
				Iterator toIter = gates.getGate(LINKTO).getLinks();
				while(toIter.hasNext()) {
					Link l = (Link)toIter.next();				
					structure.unlinkGate(l.getLinkedEntityID(),GateTypesIF.GT_GEN);
					for(int i=0;i<currentlyLinked.size();i++) {	
						structure.createLink(
							l.getLinkedEntityID(),
							GateTypesIF.GT_GEN,
							(String)currentlyLinked.get(i),
							SlotTypesIF.ST_GEN,
							1.0,
							1.0	
						);	
					}
				}
			}

			if(!currentlyLinked.containsAll(previous) ||
			   !previous.containsAll(currentlyLinked) || 
			   force.getIncomingActivation() > 0) {
	
				gates.setGateActivation(DETECT,1.0);
				previous = currentlyLinked;	
			}
		}
		
		Link monitorLink = gates.getGate(MONITOR).getLinkAt(0);
		if(force.getIncomingActivation() > 0 && monitorLink != null) {

			NetEntity monitor = monitorLink.getLinkedEntity();
			Iterator toDuplicateLinks = monitor.getGate(GateTypesIF.GT_GEN).getLinks();
			
			while(toDuplicateLinks.hasNext()) {
				Link toDuplicate = (Link)toDuplicateLinks.next();
				//logger.debug("duplicating link to "+toDuplicate.getLinkedEntityID());
				
				Iterator toIter = gates.getGate(LINKTO).getLinks();
				while(toIter.hasNext()) {
					Link l = (Link)toIter.next();				
					structure.createLink(
						l.getLinkedEntityID(),
						GateTypesIF.GT_GEN,
						toDuplicate.getLinkedEntityID(),
						SlotTypesIF.ST_GEN,
						1.0,
						1.0	
					);	
				}			
			}
			
			structure.unlinkGate(monitor.getID(),GateTypesIF.GT_GEN);
		}
				
		Link onEmptyLink = gates.getGate(ONEMPTY).getLinkAt(0);
		if(onEmptyLink == null) return;
		
		NetEntity onEmpty = onChangeLink.getLinkedEntity();
		if(onEmpty == null) return;

		if(onEmpty.getGate(GateTypesIF.GT_GEN).getNumberOfLinks() == 0) {				
			gates.setGateActivation(DETECT,1.0);	
		}				

	}

}
