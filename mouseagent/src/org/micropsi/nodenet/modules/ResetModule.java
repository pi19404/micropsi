package org.micropsi.nodenet.modules;

import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.NodeFunctions;
import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class ResetModule extends AbstractNativeModuleImpl {
    // slots
    private static final int XCOORDINATE     = 16900;
	private static final int YCOORDINATE     = 16901;
	
	//gates
	private static final int RESET = 16900;
	
	private boolean firsttime = true;
	
	private Position oldPosition;
	
	private Slot x;
	private Slot y;
	
	public ResetModule() {	
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "reset";

			public String getExtensionID() {
				return id;
			}

			public String gateType(int type) {
			    switch(type) {
			    	case RESET:	  return "reset";
			    	default:    return null;
			    }
			}

			public String slotType(int type) {
				switch(type) {			
					case XCOORDINATE: return "x-position";
					case YCOORDINATE: return "y-position";
					default:			return null;
				}
			}
		});		
	}
	
	private final int[] slotTypes = {
	        XCOORDINATE,
	        YCOORDINATE,
		};
	
	private final int[] gateTypes = {
	        RESET
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
				case XCOORDINATE :
					x = slots[i];
					break;
				case YCOORDINATE : 
					y = slots[i];
					break;
			}
		}
	}

	public void calculate(Slot[] slots, GateManipulator manipulator, long netStep) throws NetIntegrityException {
		if (firsttime) {
			catchSlots(slots);
			firsttime = false;
			oldPosition = NodeFunctions.getPosition(x.getIncomingActivation(), y.getIncomingActivation());  
		}
		
		double reset = 0.0;
		Position currentPosition = NodeFunctions.getPosition(x.getIncomingActivation(), y.getIncomingActivation());
		
		if(currentPosition.distance2D(oldPosition) > 1.0) {
			reset = 1.0;
		}
		
		manipulator.setGateActivation(RESET, reset);
		oldPosition = currentPosition;
	}
}
