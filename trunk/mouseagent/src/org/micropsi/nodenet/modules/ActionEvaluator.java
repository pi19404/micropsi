/*
 * Created on 01.05.2005
 *
 */
package org.micropsi.nodenet.modules;

import org.micropsi.common.coordinates.Position;
import org.micropsi.common.coordinates.WorldVector;
import org.micropsi.comp.ConstantValues;
import org.micropsi.comp.NodeFunctions;
import org.micropsi.comp.agent.RandomGenerator;
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
public class ActionEvaluator extends AbstractNativeModuleImpl {

    // slots
	private static final int XCOORDINATE  = 17500;
	private static final int YCOORDINATE  = 17501;
	private static final int XGOAL		  = 17502;
	private static final int YGOAL		  = 17503;
	private static final int HURTBYGROUND = 17504;
	private static final int RANDOM 	  = 17505;
	private static final int OBSTACLE	  = 17506;
	
	// gates
	private static final int MOVE 	   = 17500;
	private static final int DIRECTION = 17501;
	private static final int DISTANCE  = 17502;
	
	private Slot x;
	private Slot y;
	private Slot goalX;
	private Slot goalY;
	private Slot hurt;
	private Slot random;
	private Slot obstacle;
	
	private boolean firsttime = true;
	private WorldVector oldDirection = new WorldVector(0.0, 1.0, 0.0);
	private int counter = 0;
	
	private final int[] slotTypes = {
	        XCOORDINATE,
	        YCOORDINATE,
	        XGOAL,
	        YGOAL,
	        HURTBYGROUND,
	        RANDOM,
	        OBSTACLE
		};
	
	private final int[] gateTypes = {
	        MOVE,
	        DIRECTION,
	        DISTANCE
	};
	
	public ActionEvaluator() {	
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "acting";

			public String getExtensionID() {
				return id;
			}

			public String slotType(int type) {
			    switch(type) {
			    	case XCOORDINATE: return "x-position";
			    	case YCOORDINATE: return "y-position";
			    	case XGOAL: return "x-goal";
			    	case YGOAL: return "y-goal";
			    	case HURTBYGROUND: return "got-hurt";
			    	case RANDOM : return "random-movement";
			    	case OBSTACLE: return "obstacle";
			    	default: return null;
			    }
			}

			public String gateType(int type) {
				switch(type) {
					case MOVE: return "movepriority";
					case DIRECTION: return "direction";
					case DISTANCE: return "move-strengt";
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
			    case XGOAL :
			        goalX = slots[i];
			        break;
			    case YGOAL :
			        goalY = slots[i];
			        break;
			    case HURTBYGROUND :
			        hurt = slots[i];
			        break;
			    case RANDOM :
			        random = slots[i];
			        break;
			    case OBSTACLE :
			        obstacle = slots[i];
			        break;
			}
		}
	}
    
    /* (non-Javadoc)
     * @see org.micropsi.nodenet.AbstractNativeModuleImpl#calculate(org.micropsi.nodenet.Slot[], org.micropsi.nodenet.GateManipulator, long)
     */
    public void calculate(Slot[] slots, GateManipulator manipulator, long netstep) throws NetIntegrityException {
        double angle;
    	double movestep = ConstantValues.STEPLENGTH;
        
        if (firsttime) {
			catchSlots(slots);
			firsttime = false;
			angle = RandomGenerator.generator.nextDouble() * 360.0;
            oldDirection.rotate(angle);
		}
        
        if(obstacle.getIncomingActivation() >= 0.5) {
        	if(counter == 0) {
	            angle = (RandomGenerator.generator.nextDouble() * 180.0) + 90.0;
	        	//angle = RandomGenerator.generator.nextDouble() * 360.0;
	            oldDirection.rotate(angle);           
        	}
        	counter = (counter + 1) % 20;
        } else if(random.getIncomingActivation() > 0.5) {
        	counter = 0;
            //angle = RandomGenerator.generator.nextDouble() * 360.0;
            //oldDirection.rotate(angle);
        } else {
        	counter = 0;
        	if(x.getIncomingActivation() != 0.0 || y.getIncomingActivation() != 0.0) {
	            Position currentPosition = NodeFunctions.getPosition(x.getIncomingActivation(), y.getIncomingActivation());
	            Position goalPosition = NodeFunctions.getPosition(goalX.getIncomingActivation(), goalY.getIncomingActivation());
	            WorldVector newVector = new WorldVector(goalPosition.getX() - currentPosition.getX(), goalPosition.getY() - currentPosition.getY());
	            movestep = newVector.getLength();
	            newVector.setLength(1.0);
	            oldDirection = newVector;
        	}
        }
        angle = oldDirection.getAngle();
        
        manipulator.setGateActivation(MOVE, 1.0);
        manipulator.setGateActivation(DIRECTION, angle / 360.0);
        //TODO consider inner values
        if(movestep > ConstantValues.STEPLENGTH)
        	movestep = ConstantValues.STEPLENGTH;
        manipulator.setGateActivation(DISTANCE, movestep);
    }
}
