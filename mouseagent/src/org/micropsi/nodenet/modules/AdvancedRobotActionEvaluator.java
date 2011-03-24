/*
 * Created on 21.05.2005
 */
package org.micropsi.nodenet.modules;

import java.util.ArrayList;
import java.util.Iterator;

import org.micropsi.common.coordinates.Position;
import org.micropsi.common.coordinates.WorldVector;
import org.micropsi.comp.ConstantValues;
import org.micropsi.comp.NodeFunctions;
import org.micropsi.comp.agent.RandomGenerator;
import org.micropsi.comp.agent.voronoi.Region;
import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.ConceptNode;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

/**
 * @author Markus
 *
 */
public class AdvancedRobotActionEvaluator extends AbstractNativeModuleImpl {

    // slots
	private static final int XCOORDINATE  = 17500;
	private static final int YCOORDINATE  = 17501;
	// gates as well
	private static final int SMILE		  = 17502;
	private static final int BITE		  = 17503;
	private static final int TARGETRED    = 17504;
	private static final int TARGETGREEN  = 17505;
	private static final int TARGETBLUE   = 17506;
	// slots
	private static final int XGOAL		  = 17507;
	private static final int YGOAL		  = 17508;
	private static final int ORIENTATION  = 17509;
	private static final int HURTBYGROUND = 17510;
	private static final int RANDOM 	  = 17511;
	private static final int OBSTACLE	  = 17512;
	private static final int ONTHEWAY     = 17513;
	private static final int PROTOCOLREGIONS = 17514;
	private static final int COLLISION    = 17515;
	
	// gates
	private static final int LEFT 	      = 17500;
	private static final int RIGHT 		  = 17501;
	private static final int OBSTACLECONCEPT = 17507;
	private static final int UNKNOWNREGIONS  = 17508;
	private static final int REGIONS		 = 17509;
	
	private static final int STEPS 		  = 150;
	
	private Slot x;
	private Slot y;
	private Slot goalX;
	private Slot goalY;
	private Slot orientation;
	private Slot hurt;
	private Slot random;
	private Slot obstacle;
	private Slot collision;
	private Slot onTheWay;
	private Slot smile;
	private Slot bite;
	private Slot targetRed;
	private Slot targetGreen;
	private Slot targetBlue;
	private Slot protocolRegions;
	
	private boolean firsttime = true;
	private WorldVector oldDirection = new WorldVector(0.0, 1.0, 0.0);
	private Position oldPosition = null;
	private boolean direction;
	private double goalAngle;
	private int counter = 0;
	private boolean relocating = false;
	
	private boolean regionsProtocolled;
	
	private ConceptNode obstacleConcept;
	private ConceptNode unknownRegionsConcept;
	private ConceptNode regionsConcept;
	
	private final int[] slotTypes = {
	        XCOORDINATE,
	        YCOORDINATE,
	        SMILE,
			BITE,
			TARGETRED,
			TARGETGREEN,
			TARGETBLUE,
	        XGOAL,
	        YGOAL,
			ORIENTATION,
	        HURTBYGROUND,
	        RANDOM,
	        OBSTACLE,
			ONTHEWAY,
	        PROTOCOLREGIONS,
	        COLLISION
		};
	
	private final int[] gateTypes = {
	        LEFT,
	        RIGHT,
	        SMILE,
			BITE,
			TARGETRED,
			TARGETGREEN,
			TARGETBLUE,
			OBSTACLECONCEPT,
			UNKNOWNREGIONS,
			REGIONS
	};
	
	public AdvancedRobotActionEvaluator() {	
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "acting";

			public String getExtensionID() {
				return id;
			}

			public String slotType(int type) {
			    switch(type) {
			    	case XCOORDINATE:  return "x-position";
			    	case YCOORDINATE:  return "y-position";
			    	case SMILE:		   return "smile";	 
			    	case BITE:		   return "bite";
			    	case TARGETRED:    return "target-red";
			    	case TARGETGREEN:  return "target-green";
			    	case TARGETBLUE:   return "target-blue";
			    	case XGOAL:		   return "x-goal";
			    	case YGOAL:        return "y-goal";
			    	case ORIENTATION:  return "orientation";
			    	case HURTBYGROUND: return "got-hurt";
			    	case RANDOM : 	   return "random-movement";
			    	case OBSTACLE:     return "obstacle";
			    	case ONTHEWAY: 	   return "ontheway";
			    	case PROTOCOLREGIONS: return "stop-protocol-regions";
			    	case COLLISION:    return "collision";
			    	default:           return null;
			    }
			}

			public String gateType(int type) {
				switch(type) {
					case LEFT: return "left-wheel";
					case RIGHT: return "right-wheel";
					case SMILE:		   return "smile";	 
			    	case BITE:		   return "bite";
			    	case TARGETRED:    return "target-red";
			    	case TARGETGREEN:  return "target-green";
			    	case TARGETBLUE:   return "target-blue";
			    	case OBSTACLECONCEPT: return "obstacle-concept";
			    	case UNKNOWNREGIONS: return "unknown-regions";
			    	case REGIONS: return "regions";
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
				case XCOORDINATE:
					x = slots[i];
					break;
				case YCOORDINATE: 
					y = slots[i];
					break;
				case SMILE:
					smile = slots[i];
					break;
				case BITE:
					bite = slots[i];
					break;
				case TARGETRED:
					targetRed = slots[i];
					break;
				case TARGETGREEN:
					targetGreen = slots[i];
					break;
				case TARGETBLUE:
					targetBlue = slots[i];
					break;
			    case XGOAL:
			        goalX = slots[i];
			        break;
			    case YGOAL:
			        goalY = slots[i];
			        break;
			    case ORIENTATION:
			    	orientation = slots[i];
			    	break;
			    case HURTBYGROUND:
			        hurt = slots[i];
			        break;
			    case RANDOM:
			        random = slots[i];
			        break;
			    case OBSTACLE:
			        obstacle = slots[i];
			        break;
			    case ONTHEWAY:
			    	onTheWay = slots[i];
			    	break;
			    case PROTOCOLREGIONS:
			    	protocolRegions = slots[i];
			    	break;
			    case COLLISION:
			    	collision = slots[i];
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
			
			Link l = manipulator.getGate(OBSTACLECONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("action has no link to obstacleconcept");
			else
				obstacleConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(UNKNOWNREGIONS).getLinkAt(0);
			if(l == null)
				logger.warn("action has no link to unknownregionconcept");
			else
				unknownRegionsConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(REGIONS).getLinkAt(0);
			if(l == null)
				logger.warn("action has no link to regionconcept");
			else
				regionsConcept = (ConceptNode)l.getLinkedEntity();
		}
        
        long time = System.currentTimeMillis();
        
        if(protocolRegions.getIncomingActivation() < 0.5) {
        	regionsProtocolled = true;
        } else {
        	regionsProtocolled = false;
        }
        
        Position currentPosition = NodeFunctions.getPosition(x.getIncomingActivation(), y.getIncomingActivation());
        double currentOrientation = orientation.getIncomingActivation() * 360.0;
        double movestep = ConstantValues.STEPLENGTH;
        double leftActivation = 0.01;
        double rightActivation = 0.01;
        
        if(!relocating) {
        	counter = 0;
	        if(obstacle.getIncomingActivation() > 0.5) {
	        	oldPosition = currentPosition;
	        	direction = RandomGenerator.generator.nextBoolean();
	        	//TODO random orientation, go curve until moving, save old position, compare
	        	double currentAngle = (currentOrientation + (Math.abs(RandomGenerator.generator.nextInt()))) % 360;
	        	goalAngle = currentAngle;
//	        	goalAngle = RandomGenerator.generator.nextDouble() * 360.0;
	        	//goalAngle = (currentOrientation + 180) % 360;
	        	/*
	        	int counter = 0;
	        	do {
	        		currentAngle = (currentAngle + 10) % 360;
	        		WorldVector tempVector = new WorldVector(ConstantValues.NODEMINDISTANCE - (ConstantValues.NODEMINDISTANCE / 4), 0, 0);
	        		tempVector.rotate(currentAngle);
	        		Position testPosition = new Position(currentPosition.getX() + tempVector.getX(), currentPosition.getY() + tempVector.getY());
	        		
	        		if(isReachable(currentPosition, testPosition)) {
	        			goalAngle = tempVector.getAngle();
	        			break;
	        		}
	        		
//	        		if(isKnown(testPosition)) {
//	        			goalAngle = tempVector.getAngle();
//	        			System.out.println("new angle " + goalAngle);
//	        			break;
//	        		}


	        		if(counter++ > 36) {
	        			//System.out.println("no new goal-angle found");
	        			break;	        			
	        		}
	        	} while (true);
	        	*/
	        	relocating = true;
	        } else if(collision.getIncomingActivation() > 0.5) {        	
	        	goalAngle = (int)(goalAngle + (RandomGenerator.generator.nextInt() % 20) + 180.0) % 360;
		        relocating = true;
		        
		        /*
	        	double currentAngle = (currentOrientation + (Math.abs(RandomGenerator.generator.nextInt()))) % 360;
	        	goalAngle = RandomGenerator.generator.nextDouble() * 360.0;
	        	//goalAngle = (currentOrientation + 180) % 360;
	        	int counter = 0;
	        	do {
	        		currentAngle = (currentAngle + 10) % 360;
	        		WorldVector tempVector = new WorldVector(ConstantValues.NODEMINDISTANCE - (ConstantValues.NODEMINDISTANCE / 4), 0, 0);
	        		tempVector.rotate(currentAngle);
	        		Position testPosition = new Position(currentPosition.getX() + tempVector.getX(), currentPosition.getY() + tempVector.getY());
	        		
	        		if(isReachable(currentPosition, testPosition)) {
	        			goalAngle = tempVector.getAngle();
	        			System.out.println("new angle " + goalAngle);
	        			break;
	        		}
	        		
	        		if(counter++ > 36) {
	        			//System.out.println("no new goal-angle found");
	        			break;	        			
	        		}
	        		
	        	} while (true);
	        	relocating = true;
	        	*/
	        /*} else if (hurt.getIncomingActivation() > 0.5) {
		        goalAngle = (int)(goalAngle + (RandomGenerator.generator.nextInt() % 20) + 180.0) % 360;
		        relocating = true;
		        */
	        } else if(random.getIncomingActivation() > 0.5) {
	        	counter = 0;
	        } else if(smile.getIncomingActivation() > 0.5) {
	        	manipulator.setGateActivation(SMILE, 1.0);
	        	manipulator.setGateActivation(TARGETRED, targetRed.getIncomingActivation());
	        	manipulator.setGateActivation(TARGETGREEN, targetGreen.getIncomingActivation());
	        	manipulator.setGateActivation(TARGETBLUE, targetBlue.getIncomingActivation());
	        } else if(bite.getIncomingActivation() > 0.5) {
	        	manipulator.setGateActivation(BITE, 1.0);
	        	manipulator.setGateActivation(TARGETRED, targetRed.getIncomingActivation());
	        	manipulator.setGateActivation(TARGETGREEN, targetGreen.getIncomingActivation());
	        	manipulator.setGateActivation(TARGETBLUE, targetBlue.getIncomingActivation());
	        } else {
	        	counter = 0;
	        	if(goalX.getIncomingActivation() != 0.0 || goalY.getIncomingActivation() != 0.0) {
		            Position goalPosition = NodeFunctions.getPosition(goalX.getIncomingActivation(), goalY.getIncomingActivation());
		            //if(goalPosition.getX() != 0.0 || goalPosition.getY() != 0.0) {
			            WorldVector newVector = new WorldVector(goalPosition.getX() - currentPosition.getX(), goalPosition.getY() - currentPosition.getY());
			            movestep = newVector.getLength();
			            newVector.setLength(1.0);
			            oldDirection = newVector;
			            goalAngle = oldDirection.getAngle();
		            //}
	        	} else {
	        		//manipulator.setGateActivation(LEFT, 0.0);
	            	//manipulator.setGateActivation(RIGHT, 0.0);	        		
	        		return;
	        	}
	        }
        } else {
        	counter = (counter + 1) % STEPS;
        	if(counter == 0)
        		relocating = false;
        }
        
        if((System.currentTimeMillis() - time) > 40)
        	System.out.println("calculate action took " + (System.currentTimeMillis() - time) + "ms");
        
        if(oldPosition != null) {
        	if(oldPosition.distance2D(currentPosition) < 5.0) {
        		if(direction) {
	        		leftActivation = 0.2;
	        		rightActivation = 0.25;
        		} else {
        			leftActivation = 0.2;
	        		rightActivation = 0.15;
        		}
        		manipulator.setGateActivation(LEFT, leftActivation);
            	manipulator.setGateActivation(RIGHT, rightActivation);
        		return;
        	} else {
        		oldPosition = null;
        		relocating = false;
        		counter = 0;
        	}
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
        
        if((x.getIncomingActivation() == 0.0 && y.getIncomingActivation() == 0.0)) {
        	//|| (goalX.getIncomingActivation() == 0.0 && goalY.getIncomingActivation() == 0.0)) {
        	//manipulator.setGateActivation(LEFT, 0.0);
        	//manipulator.setGateActivation(RIGHT, 0.0);
        	return;
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
    
    private boolean isKnown(Position testPos) {
    	boolean type = false;
    	for(Iterator<Link> links = regionsConcept.getGate(GateTypesIF.GT_CAT).getLinks(); links.hasNext();) {
    		Link link = links.next();
    		Position pos;
    		try {
    			pos = NodeFunctions.getPosition(link.getLinkedEntity());
    		} catch(NetIntegrityException e) {
    			return false;
    		}
    		double distance = 1000.0;
    		double compareDistance = testPos.distance2D(pos);
    		
	    	if(compareDistance < distance) {
	    		type = getRegionKnownTypeFromNode(link.getLinkedEntityID());
	    		distance = compareDistance;
	    	}
    	}
    	return type;
    }
    
    private boolean getRegionKnownTypeFromNode(String nodeID) {
    	NetEntity regionNode = structure.findEntity(nodeID);
    	Region toReturn = new Region(NodeFunctions.getPosition(regionNode));
    	for(int i = 3; i < 8; i++) {
    		if(regionNode.getLink(GateTypesIF.GT_SUB, i).getWeight() > 0.5) {
    			toReturn.addType(i - 3);
    		}
    	}
    	toReturn.setNode((ConceptNode)structure.findEntity(nodeID));
    	
    	return !toReturn.isUnknown();
    }
    
    private boolean isReachable(Position currentPos, Position goalPos) {
    	ArrayList<String> potentialObstacles = new ArrayList<String>();
    	int numberOfNodes;
    	int counter = 0;
    	int i;
    	
    	double mindistance;
    	if(ConstantValues.NODEMINDISTANCE < ConstantValues.REGIONMINDISTANCE)
    		mindistance = ConstantValues.REGIONMINDISTANCE;
    	else
    		mindistance = ConstantValues.NODEMINDISTANCE;
    	
    	if(obstacleConcept != null) {
    		numberOfNodes = obstacleConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks();
    		for(i = 0; i < numberOfNodes; i++) {
	    		potentialObstacles.add(obstacleConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntityID());
	    		counter++;
	    	}
    	}
    	
    	if(regionsProtocolled) {
	    	if(unknownRegionsConcept != null) {
	    		numberOfNodes = unknownRegionsConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks();
	    		for(i = 0; i < numberOfNodes; i++) {
		    		potentialObstacles.add(unknownRegionsConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntityID());
		    		counter++;
		    	}
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
    				
    				if(testPosition.distance2D(currentPos) < mindistance) //TODO test this
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
    					if(Math.abs(scalarProduct(normalVector, testVector)) <= mindistance * 2) {
    						//System.out.println("obstacle is (" + testPosition.getX() + "," + testPosition.getY() + ")");
    						return false;
    					}
    					normalVector.scaleBy(-1.0);
    					if(Math.abs(scalarProduct(normalVector, testVector)) <= mindistance * 2) {
    						return false;
    					}
    				}
    			}
    		}
    	}
    	
    	return true;
    }
    
    private double scalarProduct(WorldVector vec1, WorldVector vec2) {
    	return vec1.getX() * vec2.getX() + vec1.getY() * vec2.getY();
    }
}

