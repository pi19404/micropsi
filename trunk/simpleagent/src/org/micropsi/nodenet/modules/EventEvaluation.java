package org.micropsi.nodenet.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;


public class EventEvaluation extends AbstractNativeModuleImpl {

	// slots 
	private static final int MOTIVES			= 9000;
	private static final int TRIGGER			= 9001;

	// gates
	private static final int LNK_USTM			= 9000;
	private static final int CORRELATION		= 9002;
	
	// technical
	private boolean firsttime = true;

	private NetEntity ustmEntity;
	private NetEntity selectionRegister;
	private Slot motives;
	private Slot trigger;

	private final int[] gateTypes = {
		LNK_USTM,
		CORRELATION
	};

	private final int[] slotTypes = {
		TRIGGER,
		MOTIVES
	}; 

	protected int[] getGateTypes() {
		return gateTypes;
	}

	protected int[] getSlotTypes() {
		return slotTypes;
	}
	
	public EventEvaluation() {
		
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "eventevaluation";

			public String getExtensionID() {
				return id;
			}

			public String gateType(int type) {
				switch(type) {
					case LNK_USTM:		return "USTM";
					case CORRELATION:	return "Correl"; 
					default: 			return null;
				}
			}

			public String slotType(int type) {
				switch(type) {
					case TRIGGER:		return "Trigger";
					case MOTIVES:		return "Motives";
					default: 	return null;
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
				case MOTIVES:
					motives = slots[i];
					break;
			}
		}
	}
	
	HashMap mot = new HashMap();

	public void calculate(Slot[] slots, GateManipulator gates, long netstep) throws NetIntegrityException {
		if(firsttime) {
			catchSlots(slots);
			
			Link l = gates.getGate(LNK_USTM).getLinkAt(0);
			if(l == null)
				logger.warn("EventEvaluation has no USTM link. (Expect NullPointerExceptions)");
			ustmEntity = l.getLinkedEntity();
			
			firsttime = false;
		}
		
		
//		if(trigger.getIncomingActivation() < 1) {
//			return;
//		} 
		
		ArrayList risenMotives = new ArrayList(1);
		ArrayList droppedMotives = new ArrayList(1);
		
		Iterator motivelinks = motives.getIncomingLinks();
		while(motivelinks.hasNext()) {
			Node motive = (Node)((Link)motivelinks.next()).getLinkingEntity();
						
			double currentActivation = motive.getGenActivation();
			
			if(!mot.containsKey(motive.getID()))
				mot.put(motive.getID(),new Double(currentActivation));
			
			double previousActivation = ((Double)mot.get(motive.getID())).doubleValue();
			
			mot.put(motive.getID(),new Double(currentActivation));
					
			if(currentActivation > (previousActivation * 2)) { // && timesincelastsuccess <= CORRELATION_CUTOFF) {
				// a motive rose significantly - bad!
				
				//logger.debug("Something bad happened, motive "+motive+" rose.");
				//risenMotives.add(motive);
				
			} else if(currentActivation < (previousActivation * 0.80)) {//&& currentActivation > 0.1) { // && timesincelastsuccess <= CORRELATION_CUTOFF) {
				// a motive dropped signifcantly - good!
				
				logger.debug("Motive drop: "+motive+", cur: "+currentActivation+" previous: "+previousActivation);

				gates.setGateActivation(CORRELATION,1.0);
				droppedMotives.add(motive);			 
			}
			
		}

		for(int i=0;i<droppedMotives.size();i++) {
			Node motive = (Node)droppedMotives.get(i);

			Link currentInterNodeLink = ustmEntity.getFirstLinkAt(GateTypesIF.GT_SUB);
			if(currentInterNodeLink == null) return;
			
			NetEntity currentInterNode = currentInterNodeLink.getLinkedEntity();

			/*Link protocolledNodeLink = currentInterNode.getFirstLinkAt(GateTypesIF.GT_SUB);
			if(protocolledNodeLink == null) return;
			
			NetEntity protocolledNode = protocolledNodeLink.getLinkedEntity();*/

			// currently, the success is associated with the protocol chain element, not the macro itself
			// As these associated nodes become goals later, they must not be active by default, but the
			// macro nodes are (for biasing) - otherwise the memory search wouldn't work. (It uses activation
			// for tracing the path from situation to node)
			NetEntity protocolledNode = currentInterNode;

			//userinteraction.displayInformation("Found correlation - "+protocolledNode.getID());

			if(motive.getGate(GateTypesIF.GT_POR).getLinkTo(protocolledNode.getID(),SlotTypesIF.ST_GEN) == null) {
				
				try {
					if(protocolledNode.getFirstLinkAt(GateTypesIF.GT_SUB).getLinkedEntity().getEntityName().startsWith("cat")) logger.warn("Protocolling a situation/motivedrop combination - bad!");
				} catch(Exception e) {
					return;
				}
				
				logger.info("Correlation: "+protocolledNode.getEntityName()+" --> "+motive.getEntityName());
				
				structure.createLink(
					motive.getID(),
					GateTypesIF.GT_POR,
					protocolledNode.getID(),
					SlotTypesIF.ST_GEN,
					1.0,
					0
				);
				structure.createLink(
					protocolledNode.getID(),
					GateTypesIF.GT_RET,
					motive.getID(),
					SlotTypesIF.ST_GEN,
					1.0,
					0
				);

				
			}
		}
		
	}


}
