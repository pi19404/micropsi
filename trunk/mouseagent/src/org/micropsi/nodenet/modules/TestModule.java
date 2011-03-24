/*
 * Created on 06.06.2005
 *
 */
package org.micropsi.nodenet.modules;

import java.util.ArrayList;

import org.micropsi.common.coordinates.Position;
import org.micropsi.common.coordinates.WorldVector;
import org.micropsi.comp.NodeFunctions;
import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.ConceptNode;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class TestModule extends AbstractNativeModuleImpl {
    // slots
    private static final int XCOORDINATE     = 16500;
	private static final int YCOORDINATE     = 16501;
	private static final int XGOAL = 16502;
	private static final int YGOAL = 16503;
	
	//gates
	private static final int REACHABLE = 16500;
	private static final int OBSTACLECONCEPT = 16501;
	private static final int DAMAGECONCEPT = 16502;
	
	private ConceptNode damageConcept = null;
	private ConceptNode obstacleConcept = null;
	private boolean firsttime = true;
	
	private Slot x;
	private Slot y;
	private Slot goalx;
	private Slot goaly;
	
	private final int[] slotTypes = {
	        XCOORDINATE,
	        YCOORDINATE,
			XGOAL,
			YGOAL
		};
	
	private final int[] gateTypes = {
	        REACHABLE,
			DAMAGECONCEPT,
			OBSTACLECONCEPT,
	};

	public TestModule() {	
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "testing";

			public String getExtensionID() {
				return id;
			}

			public String gateType(int type) {
			    switch(type) {
			    	case REACHABLE:	  return "reachable";
			    	case DAMAGECONCEPT: return "damage-concept";
			    	case OBSTACLECONCEPT: return "obstacle-concept";
			    	default:    return null;
			    }
			}

			public String slotType(int type) {
				switch(type) {			
					case XCOORDINATE: return "x-position";
					case YCOORDINATE: return "y-position";
					case XGOAL: return "xgoal";
					case YGOAL: return "ygoal";
					default:			return null;
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
				case XCOORDINATE :
					x = slots[i];
					break;
				case YCOORDINATE : 
					y = slots[i];
					break;
				case XGOAL :
					goalx = slots[i];
					break;
				case YGOAL : 
					goaly = slots[i];
					break;
			}
		}
	}
	
	public void calculate(Slot[] slots, GateManipulator manipulator, long netstep) throws NetIntegrityException {
        if (firsttime) {
			catchSlots(slots);
			firsttime = false;
			
			
			Link l = manipulator.getGate(DAMAGECONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to damageconcept");
			else
				damageConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(OBSTACLECONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to obstacleconcept");
			else
				obstacleConcept = (ConceptNode)l.getLinkedEntity();
		}
        Position currentPosition = NodeFunctions.getPosition(x.getIncomingActivation(), y.getIncomingActivation());
        Position goalPosition = NodeFunctions.getPosition(goalx.getIncomingActivation(), goaly.getIncomingActivation());
        
        manipulator.setGateActivation(REACHABLE, isReachable(currentPosition, goalPosition) ? 1.0 : -1.0);
	}
	
	private boolean isReachable(Position currentPos, Position goalPos) {
    	ArrayList potentialObstacles = new ArrayList();
    	int numberOfNodes;
    	int counter = 0;
    	int i;
    	if(damageConcept != null) {
	    	numberOfNodes = damageConcept.getGate(GateTypesIF.GT_SUB).getNumberOfLinks();
	    	for(i = 0; i < numberOfNodes; i++) {
	    		potentialObstacles.add(damageConcept.getLink(GateTypesIF.GT_SUB, i).getLinkedEntityID());
	    		counter++;
	    	}
    	}
    	if(obstacleConcept != null) {
    		numberOfNodes = obstacleConcept.getGate(GateTypesIF.GT_SUB).getNumberOfLinks();
    		for(i = 0; i < numberOfNodes; i++) {
	    		potentialObstacles.add(obstacleConcept.getLink(GateTypesIF.GT_SUB, i).getLinkedEntityID());
	    		counter++;
	    	}
    	}
    	
    	//System.out.println("counter: " + counter + "; real: " + potentialObstacles.size());
    	
    	if(counter > 0) {
    		WorldVector goalVector = new WorldVector(goalPos.getX() - currentPos.getX(), goalPos.getY() - currentPos.getY(), 0.0);
    		WorldVector testVector;
    		Position testPosition;
    		double angle;
    		for(i = 0; i < potentialObstacles.size(); i++) {
    			if(potentialObstacles.get(i) != null) {
    				testPosition = NodeFunctions.getPosition(structure.findEntity((String)potentialObstacles.get(i)));
    				// obstacle belongs to way
    				if(testPosition.distance2D(goalPos) <= 0.25)
    					continue;
    				testVector = new WorldVector(testPosition.getX() - currentPos.getX(), testPosition.getY() - currentPos.getY(), 0.0);
    				angle = Math.abs(goalVector.getAngle() - testVector.getAngle());
    		    	if(angle > 180.0)
    		    		angle -= 180.0;
    		    	if(angle > 90.0)
    		    		continue;
    		    	WorldVector normGoalVector = new WorldVector(goalVector);
    		    	normGoalVector.setLength(1.0);
    				if(scalarProduct(normGoalVector, testVector) > goalVector.getLength())
    					continue;
    				else {
    					//WorldVector normalVector = new WorldVector(goalVector.getY(), goalVector.getX(), 0.0);
    					WorldVector normalVector = new WorldVector(goalVector.getX(), goalVector.getY(), 0.0);
    					normalVector.rotate(90.0);
    					normalVector.setLength(1.0);
    					if(Math.abs(scalarProduct(normalVector, testVector)) <= 0.5) {
    						return false;
    					}
    					normalVector.scaleBy(-1.0);
    					if(Math.abs(scalarProduct(normalVector, testVector)) <= 0.5) {
    						return false;
    					}
    				}
    			}
    		}
    	}
    	return true;
    }
    
    private double scalarProduct(WorldVector vec1, WorldVector vec2) {
    	/*
    	double angle = Math.abs(vec1.getAngle() - vec2.getAngle());
    	if(angle > 180.0)
    		angle -= 180.0;
		return Math.cos(Math.toRadians(angle)) * vec1.getLength() * vec2.getLength();
		*/
    	return vec1.getX() * vec2.getX() + vec1.getY() * vec2.getY();
    }
}
