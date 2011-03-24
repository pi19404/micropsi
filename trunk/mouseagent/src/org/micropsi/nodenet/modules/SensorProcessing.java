/*
 * Created on 06.05.2005
 *
 */
package org.micropsi.nodenet.modules;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

/**
 * @author Markus
 *
 */
public class SensorProcessing extends AbstractNativeModuleImpl {
    private static final int XCOORDINATE   = 15500;
	private static final int YCOORDINATE   = 15501;
    private static final int FOODURGE	   = 15502;
	private static final int WATERURGE     = 15503;
	private static final int INTEGRITYURGE = 15504;
	
	private static final int FOODVALUE	  = 15500;
	private static final int WATERVALUE   = 15501;
	private static final int HEALINGVALUE = 15502;
	private static final int HURTBYGROUND = 15503;
	private static final int OBSTACLE	  = 15504;
	
	private Slot x;
	private Slot y;
	private Slot food;
	private Slot water;
	private Slot healing;
	
	private double lastFood = 0.0;
	private double lastWater = 0.0;
	private double lastHealing = 0.0; 
	
	private boolean firsttime = true;
	
	private final int[] slotTypes = {
	        XCOORDINATE,
	        YCOORDINATE,
	        FOODURGE,
	        WATERURGE,
	        INTEGRITYURGE
	};
	
	private final int[] gateTypes = {
	        FOODVALUE,
	        WATERVALUE,
	        HEALINGVALUE,
	        HURTBYGROUND,
	        OBSTACLE
	};
	
	public SensorProcessing() {	
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "sensor";

			public String getExtensionID() {
				return id;
			}

			public String slotType(int type) {
				switch(type) {
					case XCOORDINATE:	return "x-position";
					case YCOORDINATE:	return "y-position";
					case FOODURGE:		return "foodurge";
					case WATERURGE:		return "waterurge";
					case INTEGRITYURGE:	return "integrityurge";
					default:			return null;
				}
			}
			
			public String gateType(int type) {
			    switch(type) {
			    	case FOODVALUE: return "food-found";
			    	case WATERVALUE: return "water-found";
			    	case HEALINGVALUE: return "healing-found";
			    	case HURTBYGROUND: return "got-hurt";
			    	case OBSTACLE: return "obstacle";
			    	default: return null;
			    }
			}
		});		
	}
	
	/* (non-Javadoc)
     * @see org.micropsi.nodenet.AbstractNativeModuleImpl#getGateTypes()
     */
    protected int[] getGateTypes() {
        return gateTypes;
    }

    /* (non-Javadoc)
     * @see org.micropsi.nodenet.AbstractNativeModuleImpl#getSlotTypes()
     */
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
			    case FOODURGE :
			        food = slots[i];
			        break;
			    case WATERURGE :
			        water = slots[i];
			        break;
			    case INTEGRITYURGE :
			        healing = slots[i];
			        break;
			}
		}
	}
    
    public void calculate(Slot[] slots, GateManipulator manipulator, long netstep) throws NetIntegrityException {
        if (firsttime) {
			catchSlots(slots);
			firsttime = false;
			lastHealing = healing.getIncomingActivation();
		}
        
        if(lastFood > food.getIncomingActivation()) {
            manipulator.setGateActivation(FOODVALUE, lastFood - food.getIncomingActivation());
        } else {
            manipulator.setGateActivation(FOODVALUE, 0.0);
        }
        lastFood = food.getIncomingActivation();
        if(lastWater > water.getIncomingActivation()) {
            manipulator.setGateActivation(WATERVALUE, lastWater - water.getIncomingActivation());
        } else {
            manipulator.setGateActivation(WATERVALUE, 0.0);
        }
        lastWater = water.getIncomingActivation();
        if(lastHealing > healing.getIncomingActivation()) {
            manipulator.setGateActivation(HEALINGVALUE, lastHealing - healing.getIncomingActivation());
        } else {
            manipulator.setGateActivation(HEALINGVALUE, 0.0);
        }
        if(lastHealing < healing.getIncomingActivation()) {
            manipulator.setGateActivation(HURTBYGROUND, 1.0);
        } else {
            manipulator.setGateActivation(HURTBYGROUND, 0.0);
        }
        lastHealing = healing.getIncomingActivation();
    }
}
