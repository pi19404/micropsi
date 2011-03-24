package org.micropsi.nodenet.modules.hypercept;

import java.util.Iterator;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.NodeFunctionalTypesIF;
import org.micropsi.nodenet.SensorNode;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.Situation;
import org.micropsi.nodenet.agent.SituationElement;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;


public class SituationImporter extends AbstractNativeModuleImpl {

	// slots
	private static final int TRIGGER		= 27000;
	private static final int SENSORS		= 27001;
	private static final int FOVEA_H		= 27002;
	private static final int FOVEA_V		= 27003;
	private static final int FOVEA_RESET	= 27004;

	// gates
	private static final int DONE		= 27000;
	
	
	// technical stuff
	private boolean firsttime = true;
	
	private Slot sensors;
	private Slot trigger;
	private Slot fovea_h;
	private Slot fovea_v;

	private final int[] gateTypes = {
		DONE,
	};

	private final int[] slotTypes = {
		TRIGGER,
		SENSORS,
		FOVEA_RESET,
		FOVEA_H,
		FOVEA_V

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
				case TRIGGER:
					trigger = slots[i];
					break;
				case SENSORS:
					sensors = slots[i];
					break;
				case FOVEA_H:
					fovea_h = slots[i];
					break;
				case FOVEA_V:
					fovea_v = slots[i];
					break;
			}
		}
			
		innerstate.ensureStateExistence("situation","micropsi");
		innerstate.ensureStateExistence("counter","0");
	}	
	
	public SituationImporter() {
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			
			private static final String id = "situationimporter"; 
	
			public String getExtensionID() {
				return id;
			}

			public String gateType(int type) {
				switch(type) {
					case DONE:			return "Done";
					default:			return null;
				}
			}

			public String slotType(int type) {
				switch(type) {
					case TRIGGER:		return "Trigger";
					case SENSORS:		return "Sensors";
					case FOVEA_RESET:	return "Reset";
					case FOVEA_H:		return "H Link";
					case FOVEA_V:		return "V Link";

					default:			return null;
				}
			}
		});
	}
	
	public Node findSensor(String datatype) {
		Iterator iter = sensors.getIncomingLinks();
		while(iter.hasNext()) {
			Node potentialSensor = (Node)((Link)iter.next()).getLinkingEntity();
			if(potentialSensor.getType() != NodeFunctionalTypesIF.NT_SENSOR) continue;
			SensorNode s = (SensorNode)potentialSensor;
			if(!s.isConnected()) continue;
			if(s.getDataType().equals("percept_"+datatype)) return s;
		}
		return null;
	}

	public void calculate(Slot[] slots, GateManipulator gates, long netstep) throws NetIntegrityException {
		if(firsttime) {
			catchSlots(slots);
			firsttime = false;
		}
	
		if(trigger.getIncomingActivation() > 0) {
			
			logger.debug("importing situation");
			
			Situation situation = Situation.getInstance(innerstate.getInnerState("situation"));
			Iterator elements = situation.getWholeSituation(); 
			
			int counter = innerstate.getStateInt("counter");
			String situationID = structure.createChunkNode(counter+".sit");
			innerstate.setState("counter",counter++);
			
			String previousID = null;
			
			while(elements.hasNext()) {
				SituationElement element = (SituationElement)elements.next();
				Node sensor = findSensor(element.getType());
				if(sensor == null) {
					logger.warn("SituationImporter has no sensor for type "+element.getType()+", type will be omitted.");
					continue;
				}
				
				String conceptID = structure.createChunkNode(element.getType());
				String foveaID = structure.createChunkNode("look");
				String hID = structure.createChunkNode("fovea_h");
				String vID = structure.createChunkNode("fovea_v");
				String senseID = structure.createChunkNode("sense");
				
				// SUB/SUR situation -> concept
				structure.createLink(
					situationID,
					GateTypesIF.GT_SUB,
					conceptID,
					SlotTypesIF.ST_SUB,
					1.0,
					1.0
				);
				structure.createLink(
					conceptID,
					GateTypesIF.GT_SUR,
					situationID,
					SlotTypesIF.ST_SUR,
					1.0,
					1.0
				);
				
				// POR/RET previous
				if(previousID != null) {
					structure.createLink(
						conceptID,
						GateTypesIF.GT_POR,
						previousID,
						SlotTypesIF.ST_POR,
						1.0,
						1.0
					);
					structure.createLink(
						previousID,
						GateTypesIF.GT_RET,
						conceptID,
						SlotTypesIF.ST_RET,
						1.0,
						1.0
					);
				}
				previousID = conceptID;
								
				// SUB/SUR -> fovea
				structure.createLink(
					conceptID,
					GateTypesIF.GT_SUB,
					foveaID,
					SlotTypesIF.ST_SUB,
					1.0,
					1.0
				);
				structure.createLink(
					foveaID,
					GateTypesIF.GT_SUR,
					conceptID,
					SlotTypesIF.ST_SUR,
					1.0,
					1.0
				);

				// SUB/SUR fovea -> h
				structure.createLink(
					foveaID,
					GateTypesIF.GT_SUB,
					hID,
					SlotTypesIF.ST_SUB,
					1.0,
					1.0
				);
				structure.createLink(
					hID,
					GateTypesIF.GT_SUR,
					foveaID,
					SlotTypesIF.ST_SUR,
					1.0,
					1.0
				);

				// SUB/SUR fovea -> v
				structure.createLink(
					foveaID,
					GateTypesIF.GT_SUB,
					vID,
					SlotTypesIF.ST_SUB,
					1.0,
					1.0
				);
				structure.createLink(
					vID,
					GateTypesIF.GT_SUR,
					foveaID,
					SlotTypesIF.ST_SUR,
					1.0,
					1.0
				);

				
				// SUB/SUR -> sense
				structure.createLink(
					conceptID,
					GateTypesIF.GT_SUB,
					senseID,
					SlotTypesIF.ST_SUB,
					1.0,
					1.0
				);
				structure.createLink(
					senseID,
					GateTypesIF.GT_SUR,
					conceptID,
					SlotTypesIF.ST_SUR,
					1.0,
					1.0
				);

				// POR/RET fovea -> sense
				structure.createLink(
					foveaID,
					GateTypesIF.GT_POR,
					senseID,
					SlotTypesIF.ST_POR,
					1.0,
					1.0
				);
				structure.createLink(
					senseID,
					GateTypesIF.GT_RET,
					foveaID,
					SlotTypesIF.ST_RET,
					1.0,
					1.0
				);
				
				// SUB/SUR h -> actor (h)
				structure.createLink(
					hID,
					GateTypesIF.GT_SUB,
					fovea_h.getIncomingLinkAt(0).getLinkingEntity().getID(),
					SlotTypesIF.ST_GEN,
					1.0,
					1.0,
					element.getX(),
					element.getY(),
					0.0,
					0
				);
				structure.createLink(
					fovea_h.getIncomingLinkAt(0).getLinkingEntity().getID(),
					GateTypesIF.GT_GEN,
					hID,
					SlotTypesIF.ST_SUR,
					1.0,
					1.0
				);

				// SUB/SUR v -> actor (v)
				structure.createLink(
					vID,
					GateTypesIF.GT_SUB,
					fovea_v.getIncomingLinkAt(0).getLinkingEntity().getID(),
					SlotTypesIF.ST_GEN,
					1.0,
					1.0,
					element.getX(),
					element.getY(),
					0.0,
					0
				);
				
				structure.createLink(
					fovea_v.getIncomingLinkAt(0).getLinkingEntity().getID(),
					GateTypesIF.GT_GEN,
					vID,
					SlotTypesIF.ST_SUR,
					1.0,
					1.0
				);

				// SUB/SUR sense -> sensor
				structure.createLink(
					senseID,
					GateTypesIF.GT_SUB,
					sensor.getID(),
					SlotTypesIF.ST_GEN,
					1.0,
					1.0
				);
				structure.createLink(
					sensor.getID(),
					GateTypesIF.GT_GEN,
					senseID,
					SlotTypesIF.ST_SUR,
					1.0,
					1.0
				);
			}
			
			gates.setGateActivation(DONE,1.0);
			
		}
		
	}
}
