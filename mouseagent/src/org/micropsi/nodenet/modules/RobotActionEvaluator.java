/*
 * Created on 21.05.2005
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
public class RobotActionEvaluator extends AbstractNativeModuleImpl {

    // slots
	private static final int XCOORDINATE  = 17500;
	private static final int YCOORDINATE  = 17501;
	private static final int XGOAL		  = 17502;
	private static final int YGOAL		  = 17503;
	private static final int ORIENTATION  = 17504;
	private static final int HURTBYGROUND = 17505;
	private static final int RANDOM 	  = 17506;
	private static final int OBSTACLE	  = 17507;
	private static final int ONTHEWAY     = 17508;
	
	// gates
	private static final int LEFT 	      = 17500;
	private static final int RIGHT 		  = 17501;
	
	private static final int STEPS 		  = 50;
	
	private Slot x;
	private Slot y;
	private Slot goalX;
	private Slot goalY;
	private Slot orientation;
	private Slot hurt;
	private Slot random;
	private Slot obstacle;
	private Slot onTheWay;
	
	private boolean firsttime = true;
	private WorldVector oldDirection = new WorldVector(0.0, 1.0, 0.0);
	private double goalAngle;
	private int counter = 0;
	private boolean relocating = false;
	
	private final int[] slotTypes = {
	        XCOORDINATE,
	        YCOORDINATE,
	        XGOAL,
	        YGOAL,
			ORIENTATION,
	        HURTBYGROUND,
	        RANDOM,
	        OBSTACLE,
			ONTHEWAY
		};
	
	private final int[] gateTypes = {
	        LEFT,
	        RIGHT
	};
	
	public RobotActionEvaluator() {	
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "acting";

			public String getExtensionID() {
				return id;
			}

			public String slotType(int type) {
			    switch(type) {
			    	case XCOORDINATE:  return "x-position";
			    	case YCOORDINATE:  return "y-position";
			    	case XGOAL:		   return "x-goal";
			    	case YGOAL:        return "y-goal";
			    	case ORIENTATION:  return "orientation";
			    	case HURTBYGROUND: return "got-hurt";
			    	case RANDOM : 	   return "random-movement";
			    	case OBSTACLE:     return "obstacle";
			    	case ONTHEWAY: 	   return "ontheway";
			    	default:           return null;
			    }
			}

			public String gateType(int type) {
				switch(type) {
					case LEFT: return "left-wheel";
					case RIGHT: return "right-wheel";
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
			    case ORIENTATION :
			    	orientation = slots[i];
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
			    case ONTHEWAY :
			    	onTheWay = slots[i];
			    	break;
			}
		}
	}
    
    /* (non-Javadoc)
     * @see org.micropsi.nodenet.AbstractNativeModuleImpl#calculate(org.micropsi.nodenet.Slot[], org.micropsi.nodenet.GateManipulator, long)
     */
    public void calculate(Slot[] slots, GateManipulator manipulator, long netstep) throws NetIntegrityException {
        if (firsttime) {
			catchSlots(slots);
			firsttime = false;
			goalAngle = RandomGenerator.generator.nextDouble() * 360.0;
		}
        
        double currentOrientation = orientation.getIncomingActivation() * 360.0;
        double movestep = ConstantValues.STEPLENGTH;
        double leftActivation = 0.01;
        double rightActivation = 0.01;
        
        if(!relocating) {
        	counter = 0;
	        if(obstacle.getIncomingActivation() > 0.5) {
	        	goalAngle = RandomGenerator.generator.nextDouble() * 360.0;
	        	//goalAngle = (int)(goalAngle + (RandomGenerator.generator.nextInt() % 20) + 180.0) % 360;
	        	relocating = true;
	        } else if (hurt.getIncomingActivation() > 0.5) {
		        goalAngle = (int)(goalAngle + (RandomGenerator.generator.nextInt() % 20) + 180.0) % 360;
		        relocating = true;
	        } else if(random.getIncomingActivation() > 0.5) {
	        	counter = 0;
	        } else {
	        	counter = 0;
	        	if(x.getIncomingActivation() != 0.0 || y.getIncomingActivation() != 0.0) {
		            Position currentPosition = NodeFunctions.getPosition(x.getIncomingActivation(), y.getIncomingActivation());
		            Position goalPosition = NodeFunctions.getPosition(goalX.getIncomingActivation(), goalY.getIncomingActivation());
		            WorldVector newVector = new WorldVector(goalPosition.getX() - currentPosition.getX(), goalPosition.getY() - currentPosition.getY());
		            movestep = newVector.getLength();
		            newVector.setLength(1.0);
		            oldDirection = newVector;
		            goalAngle = oldDirection.getAngle();
	        	}
	        }
        } else {
        	counter = (counter + 1) % STEPS;
        	if(counter == 0)
        		relocating = false;
        }
        
        double angleDifference = currentOrientation - goalAngle;
        if (angleDifference > 180.0)
        	angleDifference -= 360.0;
        if (angleDifference < -180.0)
        	angleDifference += 360.0;
        if(Math.abs(angleDifference) > 45.0) {
        	leftActivation = 0.1;
        	rightActivation = 0.1;
        }
        
        if(Math.abs(angleDifference) < 2.0) {
        	if(movestep > ConstantValues.STEPLENGTH)
        		movestep = ConstantValues.STEPLENGTH;
        	manipulator.setGateActivation(LEFT, movestep);
        	manipulator.setGateActivation(RIGHT, movestep);
        } else if(angleDifference < 0.0) {
        	manipulator.setGateActivation(LEFT, leftActivation);
        	manipulator.setGateActivation(RIGHT, -rightActivation);
        	counter = 1;
        } else {
        	manipulator.setGateActivation(LEFT, -leftActivation);
        	manipulator.setGateActivation(RIGHT, rightActivation);
        	counter = 1;
        }
    }
}

