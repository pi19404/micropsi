/*
 * Created on 29.04.2005
 *
 */
package org.micropsi.nodenet.modules;

import java.util.ArrayList;

import org.micropsi.common.coordinates.Position;
import org.micropsi.common.coordinates.WorldVector;
import org.micropsi.comp.ConstantValues;
import org.micropsi.comp.NodeFunctions;
import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.ConceptNode;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

/**
 * @author Markus
 *
 */
public class PlanningModule extends AbstractNativeModuleImpl {
    // slots
    private static final int XCOORDINATE     = 16500;
	private static final int YCOORDINATE     = 16501;
	private static final int FOODURGE	     = 16502;
	private static final int WATERURGE       = 16503;
	private static final int HEALINGURGE     = 16504;
	private static final int AFFILIATIONURGE = 16505;
	private static final int EXPLORATIONURGE = 16506; // not really a slot
	private static final int RESET			 = 16507;
    
    // gates
    private static final int XGOAL 			   = 16500;
    private static final int YGOAL             = 16501;
    private static final int RANDOM 		   = 16502; // if there is no goal, randommovement
	private static final int FOODCONCEPT 	   = 16503;
	private static final int WATERCONCEPT 	   = 16504;
	private static final int HEALINGCONCEPT    = 16505;
	private static final int HURTCONCEPT 	   = 16506;
	private static final int OBSTACLECONCEPT   = 16507;
	private static final int PROTOCOL          = 16508;
	private static final int NOPROTOCOL 	   = 16509;
	private static final int CURRENTGOAL   	   = 16510;
	private static final int NEARAGENTSCONCEPT = 16511;
	private static final int ONTHEWAY 		   = 16512;
    
    private Slot x;
	private Slot y;
	private Slot food;
	private Slot water;
	private Slot healing;
	private Slot hurt;
	private Slot affiliation;
	private Slot reset;
	
	private ConceptNode foodConcept = null;
	private ConceptNode waterConcept = null;
	private ConceptNode healingConcept = null;
	private ConceptNode damageConcept = null;
	private ConceptNode obstacleConcept = null;
	private ConceptNode currentGoal = null;
	
	private boolean firsttime = true;
	private int currentMotive = 0;
	private String goalID = null;
	
	private boolean onTheWay = false;
	private Position lastPosition;
	
	private double explorationStrength = ConstantValues.EXPLORATIONSTRENGTH;
	
	//debug
	int cycle = 0;
	
	public PlanningModule() {	
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "planning";

			public String getExtensionID() {
				return id;
			}

			public String gateType(int type) {
			    switch(type) {
			    	case XGOAL: return "x-goal";
			    	case YGOAL: return "y-goal";
			    	case RANDOM: return "random-movement";
			    	case FOODCONCEPT: return "food-concept";
			    	case WATERCONCEPT: return "water-concept";
			    	case HEALINGCONCEPT: return "healing-concept";
			    	case HURTCONCEPT: return "damage-concept";
			    	case OBSTACLECONCEPT: return "obstacle-concept";
			    	case PROTOCOL: return "protocol-module";
			    	case NOPROTOCOL: return "no-protocolling";
			    	case CURRENTGOAL: return "current-goalnode";
			    	case NEARAGENTSCONCEPT: return "near-agents";
			    	case ONTHEWAY: return "ontheway";
			    	default:    return null;
			    }
			}

			public String slotType(int type) {
				switch(type) {
					case XCOORDINATE: return "x-position";
					case YCOORDINATE: return "y-position";
					case FOODURGE: return "food-urge";
					case WATERURGE:	return "water-urge";
					case HEALINGURGE: return "healing-urge";
					case AFFILIATIONURGE: return "affiliation-urge";
					case RESET: return "reset";
					default:			return null;
				}
			}
		});		
	}
	
	private final int[] slotTypes = {
	        XCOORDINATE,
	        YCOORDINATE,
	        FOODURGE,
	        WATERURGE,
	        HEALINGURGE,
	        AFFILIATIONURGE,
	        RESET
		};
	
	private final int[] gateTypes = {
	        XGOAL,
	        YGOAL,
	        RANDOM,
			FOODCONCEPT,
			WATERCONCEPT,
			HEALINGCONCEPT,
			HURTCONCEPT,
			OBSTACLECONCEPT,
			PROTOCOL,
			NOPROTOCOL,
			CURRENTGOAL,
			NEARAGENTSCONCEPT,
			ONTHEWAY
	};
    
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
			    case HEALINGURGE :
			        healing = slots[i];
			        break;
			    case AFFILIATIONURGE:
			        affiliation = slots[i];
			        break;
			    case RESET:
			    	reset = slots[i];
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
			
			Link l = manipulator.getGate(FOODCONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to foodconcept");
			else
				foodConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(WATERCONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to waterconcept");
			else
				waterConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(HEALINGCONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to healingconcept");
			else
				healingConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(HURTCONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to damageconcept");
			else
				damageConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(OBSTACLECONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to obstacleconcept");
			else
				obstacleConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(CURRENTGOAL).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to goalconcept");
			else
				currentGoal = (ConceptNode)l.getLinkedEntity();
			
			lastPosition = NodeFunctions.getPosition(x.getIncomingActivation(), y.getIncomingActivation()); 
		}
        
        if(reset.getIncomingActivation() > 0.5) {
        	resetModule();
        	return;
        }
        
        Position currentPosition = NodeFunctions.getPosition(x.getIncomingActivation(), y.getIncomingActivation());
        manipulator.setGateActivation(RANDOM, 0.0);
        
        // calculate motive
        //TODO consider competence
        /*
        int newMotive = 0;
        double newMotiveStrength = 0.0;
        */
        int newMotive = EXPLORATIONURGE;
        double newMotiveStrength = explorationStrength;
        double oldMotiveStrength = 0.0;
        for(int i = 2; i < slots.length; i++) {
            if(slots[i].getIncomingActivation() > newMotiveStrength) {
                newMotive = slots[i].getType();
                newMotiveStrength = slots[i].getIncomingActivation();
            }
            if(slots[i].getType() == currentMotive) {
                oldMotiveStrength = slots[i].getIncomingActivation();
            }
        }
        if(currentMotive == 0) {         
            currentMotive = newMotive;
            clear();
        } else {
            if(oldMotiveStrength + ConstantValues.STUBBORNESS < newMotiveStrength) {
                currentMotive = newMotive;
                clear();
            }
        }
        
        // already have a plan
        if(goalID != null) {
            ConceptNode goal = (ConceptNode)structure.findEntity(goalID);
            if(currentPosition.distance2D(NodeFunctions.getPosition(goal)) < ConstantValues.STEPLENGTH) {
            	manipulator.setGateActivation(NOPROTOCOL, 1.0);
            	
            	// goal reached
                if(goal.getFirstLinkAt(GateTypesIF.GT_POR) == null) {
                	clear();
                } else {
	                ConceptNode newGoal = (ConceptNode)goal.getFirstLinkAt(GateTypesIF.GT_POR).getLinkedEntity();
	                
	                manipulator.setGateActivation(XGOAL, newGoal.getLink(GateTypesIF.GT_SUB, 0).getWeight());
	                manipulator.setGateActivation(YGOAL, newGoal.getLink(GateTypesIF.GT_SUB, 1).getWeight());
	                goalID = newGoal.getID();
	                onTheWay = true;
                }
            } else if (!onTheWay) {
            	if(isReachable(currentPosition, NodeFunctions.getPosition(goal))) {
            		manipulator.setGateActivation(XGOAL, structure.findEntity(goalID).getLink(GateTypesIF.GT_SUB, 0).getWeight());
            		manipulator.setGateActivation(YGOAL, structure.findEntity(goalID).getLink(GateTypesIF.GT_SUB, 1).getWeight());
            		manipulator.setGateActivation(NOPROTOCOL, 1.0);
            	} else {
            		logger.info("target not reachable");
            		clear();
            		deletePlanGoal();
            		manipulator.setGateActivation(RANDOM, 1.0);
            		manipulator.setGateActivation(NOPROTOCOL, 0.0);
            	}
            } else {
            	manipulator.setGateActivation(XGOAL, structure.findEntity(goalID).getLink(GateTypesIF.GT_SUB, 0).getWeight());
        		manipulator.setGateActivation(YGOAL, structure.findEntity(goalID).getLink(GateTypesIF.GT_SUB, 1).getWeight());
        		manipulator.setGateActivation(NOPROTOCOL, 1.0);
            }
        } else {
            manipulator.setGateActivation(NOPROTOCOL, 0.0);
        	if(getMotiveSlot(currentMotive) != null) {
	            if(getMotiveSlot(currentMotive).getIncomingActivation() < 0.05) {
	                manipulator.setGateActivation(RANDOM, 1.0);
	                return;
	            }
        	}
            if(currentMotive == AFFILIATIONURGE) {
                //TODO search agent and smile
                return;
            }
            
            if(currentMotive == EXPLORATIONURGE) {
            	manipulator.setGateActivation(RANDOM, 1.0);
            	return;
            }
            
            ConceptNode currentMotiveConcept = getConcept(currentMotive);
            if(currentMotiveConcept == null || currentMotiveConcept.getFirstLinkAt(GateTypesIF.GT_SUB) == null) {
                // no planning possible due to lack of information
                manipulator.setGateActivation(RANDOM, 1.0);
                return;
            }
            
            // significant change in position to justify new planning
            if(currentPosition.distance2D(lastPosition) > ConstantValues.STEPLENGTH) {
	            int numberOfNodes = currentMotiveConcept.getGate(GateTypesIF.GT_SUB).getNumberOfLinks();
	            double minDistance = 1000.0;
	            for(int i = 0; i < numberOfNodes; i++) {
	                Link testLink = currentMotiveConcept.getLink(GateTypesIF.GT_SUB, i);
	                if(testLink != null) { 
	                    Position testPosition = NodeFunctions.getPosition(testLink.getLinkedEntity());
	                    /*
	                    if(!isReachable(currentPosition, testPosition)) {
	                    	System.out.println("obstacle between (" + currentPosition.getX() + "," + currentPosition.getY() + ") and (" + testPosition.getX() + "," + testPosition.getY() + ")");
	                    }
	                    */
	                    if(currentPosition.distance2D(testPosition) < minDistance && isReachable(currentPosition, testPosition)) {
	                        minDistance = currentPosition.distance2D(testPosition);
	                        goalID = ((ConceptNode)testLink.getLinkedEntity()).getID();
	                    }
	                }
	            }
	            lastPosition = currentPosition;
            }
            if(goalID == null) {
            	logger.info("no reachable waypoint found");
                manipulator.setGateActivation(RANDOM, 1.0);
                clear();
                return;
            } else {
            	//TODO make this more accurate (noprotocol)
            	deletePlanGoal();
            	structure.createLink(currentGoal.getID(), GateTypesIF.GT_SUB, goalID, SlotTypesIF.ST_GEN, 1.0, 1.0);
                manipulator.setGateActivation(XGOAL, structure.findEntity(goalID).getLink(GateTypesIF.GT_SUB, 0).getWeight());
                manipulator.setGateActivation(YGOAL, structure.findEntity(goalID).getLink(GateTypesIF.GT_SUB, 1).getWeight());
            }
        }
        manipulator.setGateActivation(ONTHEWAY, onTheWay ? 1.0 : 0.0);
    }
    
    private ConceptNode getConcept(int motive) {
        switch(motive) {
        	case FOODURGE: return foodConcept;
        	case WATERURGE: return waterConcept;
        	case HEALINGURGE: return healingConcept;
        	default: return null;
        }
    }
    
    private Slot getMotiveSlot(int motive) {
        switch(motive) {
        	case FOODURGE: return food;
        	case WATERURGE: return water;
        	case HEALINGURGE: return healing;
        	case AFFILIATIONURGE: return affiliation;
        	default: return null;
        }
    }
    
    private boolean isReachable(Position currentPos, Position goalPos) {
    	ArrayList<String> potentialObstacles = new ArrayList<String>();
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
    	
    	if(counter > 0) {
    		WorldVector goalVector = new WorldVector(goalPos.getX() - currentPos.getX(), goalPos.getY() - currentPos.getY(), 0.0);
    		WorldVector testVector;
    		Position testPosition;
    		double angle;
    		for(i = 0; i < potentialObstacles.size(); i++) {
    			if(potentialObstacles.get(i) != null) {
    				testPosition = NodeFunctions.getPosition(structure.findEntity((String)potentialObstacles.get(i)));
    				// obstacle belongs to way

    				if(testPosition.distance2D(goalPos) <= ConstantValues.NODEMINDISTANCE / 4.0) //TODO test this
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
    					WorldVector normalVector = new WorldVector(goalVector.getX(), goalVector.getY(), 0.0);
    					normalVector.rotate(90.0);
    					normalVector.setLength(1.0);
    					if(Math.abs(scalarProduct(normalVector, testVector)) <= ConstantValues.NODEMINDISTANCE * 2) {
    						//System.out.println("obstacle is (" + testPosition.getX() + "," + testPosition.getY() + ")");
    						return false;
    					}
    					normalVector.scaleBy(-1.0);
    					if(Math.abs(scalarProduct(normalVector, testVector)) <= ConstantValues.NODEMINDISTANCE * 2) {
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
    
    private void clear() {
    	goalID = null;
        onTheWay = false;
    }
    
    private void deletePlanGoal() throws NetIntegrityException {
    	while(currentGoal.getFirstLinkAt(GateTypesIF.GT_SUB) != null) {
    		structure.deleteLink(currentGoal.getID(), GateTypesIF.GT_SUB, currentGoal.getFirstLinkAt(GateTypesIF.GT_SUB).getLinkedEntityID(), SlotTypesIF.ST_GEN);
    	}
    }
    
    private void resetModule() {
    	onTheWay = false;
    	goalID = null;
    }
}
