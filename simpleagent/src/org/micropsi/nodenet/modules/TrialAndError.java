package org.micropsi.nodenet.modules;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.RegisterNode;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class TrialAndError extends AbstractNativeModuleImpl {

	// slots
	public static final int TRIGGER	= 6000;
	public static final int DEBUG 	= 6001;
	
	// gates
	public static final int ACTIONS		= 6000;
	public static final int DONE		= 6001;
	public static final int FAIL		= 6004;
	public static final int PRGREG		= 6002;
	public static final int STARTREG	= 6003;
	
	// technical stuff
	private boolean firsttime = true;
	private Random random = new Random();
	
	private Slot trigger;
	private Slot debug;

	private final int[] gateTypes = {
		PRGREG,
		STARTREG,
		ACTIONS,
		DONE,
		FAIL
	};

	private final int[] slotTypes = {
		TRIGGER,
		DEBUG
	};
	
	public TrialAndError() {
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			public String getExtensionID() {
				return "trialanderror";
			}
			public String gateType(int type) {
				switch(type) {
					case ACTIONS:			return "Actions";
					case DONE:				return "Done";
					case FAIL:				return "Fail";
					case PRGREG:			return "PrgReg";
					case STARTREG:			return "StartReg";
					default:				return null;
				}				
			}
			public String slotType(int type) {
				switch(type) {
					case TRIGGER:			return "Trigger";
					case DEBUG:				return "Debug";
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
				case DEBUG :
					debug = slots[i];
					break;
			}
		}
	}

	public void calculate(Slot[] slots, GateManipulator gates, long netstep) throws NetIntegrityException {
		if(firsttime) {
			catchSlots(slots);
			firsttime = false;
		}
			
		if(trigger.getIncomingActivation() <= 0) return;

		Link selRegLink = gates.getGate(PRGREG).getLinkAt(0);
		if(selRegLink == null) return;
		
		RegisterNode selReg = (RegisterNode)selRegLink.getLinkedEntity();
		structure.unlinkGate(selReg.getID(),GateTypesIF.GT_GEN);
		
		Link startRegLink = gates.getGate(STARTREG).getLinkAt(0);
		if(startRegLink == null) return;
		
		RegisterNode startReg = (RegisterNode)startRegLink.getLinkedEntity();
		structure.unlinkGate(startReg.getID(),GateTypesIF.GT_GEN);

		double rating[] = new double[gates.getGate(ACTIONS).getNumberOfLinks()];
		int i = 0;
		Iterator iter = gates.getGate(ACTIONS).getLinks();	
		while(iter.hasNext()) {
			Link l = (Link)iter.next();
			double bias = ((Node)l.getLinkedEntity()).getGenActivation();
//			System.err.println("Bias: "+bias+" for "+l.getLinkedEntity());
			
			rating[i] = (1 + bias) * (1 + (random.nextDouble() / 2));
			
//			System.err.println("Rating: "+rating[i]);
			
			i++;
		}
		
		double highest = 0;
		int highestAt = -1;
		for(i=0;i<rating.length;i++) {			
			if(rating[i] > highest) {
				highest = rating[i];
				highestAt = i;
			}
		}
		
		NetEntity selectedAction = gates.getGate(ACTIONS).getLinkAt(highestAt).getLinkedEntity();

		if(debug.getIncomingActivation() > 0) {
			selectedAction = askUser(gates.getGate(ACTIONS).getLinks());
		}

		if(selectedAction == null) {
			gates.setGateActivation(FAIL,1.0);
			return;
		}

		logger.debug("tae selected: "+selectedAction.getEntityName());
		
		structure.createLink(
			selReg.getID(),
			GateTypesIF.GT_GEN,
			selectedAction.getID(),
			SlotTypesIF.ST_GEN,
			1.0,
			1.0
		);
		
		structure.createLink(
			startReg.getID(),
			GateTypesIF.GT_GEN,
			selectedAction.getID(),
			SlotTypesIF.ST_GEN,
			1.0,
			1.0
		);
		
		gates.setGateActivation(DONE,1.0);
	}
	
	private NetEntity askUser(Iterator iter) {
		
		Vector listData = new Vector();
		Vector entities = new Vector();		

		try {
			while(iter.hasNext()) {
				Link l = (Link)iter.next();
				listData.add(l.getLinkedEntity().getEntityName());
				entities.add(l.getLinkedEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		String[] sel = new String[listData.size()];
		for(int i=0;i<sel.length;i++)
			sel[i] = (String)listData.elementAt(i);
		
		String[] res = userinteraction.selectFromAlternatives(sel);
		if(res == null) return null;
		if(res.length == 0) return null;
		
		int index = listData.indexOf(res[0]);
		
		return (NetEntity)entities.get(index);
	}

}
