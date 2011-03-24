/*
 * Created on 28.04.2005
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
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;
import org.micropsi.nodenet.ext.StandardDecays;

/**
 * @author Markus
 *
 */
public class ProtocolModule extends AbstractNativeModuleImpl {
	
    //	slots
	private static final int XCOORDINATE  = 19500;
	private static final int YCOORDINATE  = 19501;
	private static final int FOODURGE	  = 19502;
	private static final int WATERURGE    = 19503;
	private static final int HEALINGURGE  = 19504;
	private static final int HURTBYGROUND = 19505;
	private static final int OBSTACLE     = 19506;
	private static final int RESET		  = 19507;
	private static final int ONTHEWAY	  = 19508;
	
	// gates
	private static final int FOODCONCEPT     = 19500;
	private static final int WATERCONCEPT    = 19501;
	private static final int HEALINGCONCEPT  = 19502;
	private static final int HURTCONCEPT     = 19503;
	private static final int OBSTACLECONCEPT = 19504;
	private static final int CURRENTGOAL     = 19505;
	private static final int ALLNODES		 = 19506;
	
	private Slot x;
	private Slot y;
	private Slot food;
	private Slot water;
	private Slot healing;
	private Slot hurt;
	private Slot obstacle;
	private Slot reset;
	private Slot onTheWay;
	
	private String lastNode = null;
	private String foodConcept = null;
	private String waterConcept = null;
	private String healingConcept = null;
	private String damageConcept = null;
	private String obstacleConcept = null;
	private String currentGoal = null;
	private String allNodes = null;
	
	private boolean firsttime = true;
	private Position lastInsertedDamage = null;
	private Position lastInsertedObstacle = null;
	private boolean insertedInProtocol = false;
	private int counter = 0;
	
	private ArrayList<String> toDelete = new ArrayList<String>();
	//debug
	int cycle = 0;
	boolean debug = false;
	
	private final int[] slotTypes = {
	        XCOORDINATE,
	        YCOORDINATE,
	        FOODURGE,
	        WATERURGE,
	        HEALINGURGE,
	        HURTBYGROUND,
			OBSTACLE,
			RESET,
			ONTHEWAY
		};
	
	private final int[] gateTypes = {
			FOODCONCEPT,
			WATERCONCEPT,
			HEALINGCONCEPT,
			HURTCONCEPT,
			OBSTACLECONCEPT,
			CURRENTGOAL,
			ALLNODES
	};
	
	public ProtocolModule() {	
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "protocol";

			public String getExtensionID() {
				return id;
			}

			public String gateType(int type) {
			    switch(type) {
			    	case FOODCONCEPT: return "food-concept";
			    	case WATERCONCEPT: return "water-concept";
			    	case HEALINGCONCEPT: return "healing-concept";
			    	case HURTCONCEPT: return "damage-concept";
			    	case OBSTACLECONCEPT: return "obstacle-concept";
			    	case CURRENTGOAL: return "currentgoal";
			    	case ALLNODES: return "allnodes";
			    	default: return null;
			    }
			}

			public String slotType(int type) {
				switch(type) {
					case XCOORDINATE:	return "x-position";
					case YCOORDINATE:	return "y-position";
					case FOODURGE:		return "food-found";
					case WATERURGE:		return "water-found";
					case HEALINGURGE:	return "healing-found";
					case HURTBYGROUND:	return "got-hurt";
					case OBSTACLE:		return "obstacle";
					case RESET:			return "reset";
					case ONTHEWAY:		return "ontheway";
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
			    case FOODURGE :
			        food = slots[i];
			        break;
			    case WATERURGE :
			        water = slots[i];
			        break;
			    case HEALINGURGE :
			        healing = slots[i];
			        break;
			    case HURTBYGROUND :
			        hurt = slots[i];
			        break;
			    case OBSTACLE :
			    	obstacle =slots[i];
			    	break;
			    case RESET :
			    	reset = slots[i];
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
			
			Link l = manipulator.getGate(FOODCONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to foodconcept");
			else
				foodConcept = l.getLinkedEntityID();
			
			l = manipulator.getGate(WATERCONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to waterconcept");
			else
				waterConcept = l.getLinkedEntityID();
			
			l = manipulator.getGate(HEALINGCONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to healingconcept");
			else
				healingConcept = l.getLinkedEntityID();
			
			l = manipulator.getGate(HURTCONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to damageconcept");
			else
				damageConcept = l.getLinkedEntityID();
			
			l = manipulator.getGate(OBSTACLECONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to obstacleconcept");
			else
				obstacleConcept = l.getLinkedEntityID();
			
			l = manipulator.getGate(CURRENTGOAL).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to currentgoal");
			else
				currentGoal = l.getLinkedEntityID();
			
			l = manipulator.getGate(ALLNODES).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to allnodes");
			else
				allNodes = l.getLinkedEntityID();
		}
        
        if(reset.getIncomingActivation() > 0.5) {
        	resetModule();
        	return;
        }
        
        checkIntegrity();
        
        double xposition = x.getIncomingActivation();
        double yposition = y.getIncomingActivation();
        Position currentPosition = NodeFunctions.getPosition(x.getIncomingActivation(), y.getIncomingActivation());
        
        String newNode;
        String newX;
        String newY;

        decayProtocol();
        
        //TODO test optimization
        //long time = System.currentTimeMillis();
        optimateWaypoints(currentPosition);
        optimateWaypoints();
        //System.out.println("timeForOptimization: " + (System.currentTimeMillis() - time));

        if(onTheWay.getIncomingActivation() > 0.5 && !insertedInProtocol) {
        	if(structure.findEntity(currentGoal) != null && structure.findEntity(currentGoal).getFirstLinkAt(GateTypesIF.GT_SUB) != null) {
        		String currentGoalNode = structure.findEntity(currentGoal).getFirstLinkAt(GateTypesIF.GT_SUB).getLinkedEntityID();
        		structure.getGateManipulator(currentGoalNode).setGateDecayType(GateTypesIF.GT_RET, StandardDecays.NO_DECAY);
        		/*
        		if(structure.findEntity(currentGoalNode).getGate(GateTypesIF.GT_RET).getNumberOfLinks() == 0) {
        			System.out.println("connected to last");
        		}
        		*/
        		if(lastNode != null) {
        			double weight = 1.0;
	        		if(structure.findEntity(lastNode).getLastLinkAt(GateTypesIF.GT_RET) != null) {
		        		String beforeLastNode = structure.findEntity(lastNode).getLastLinkAt(GateTypesIF.GT_RET).getLinkedEntityID();
		        		
		        		//double weight = 1.0;//(1.0 + structure.findEntity(lastNode).getLastLinkAt(GateTypesIF.GT_RET).getWeight()) / 2.0;
/*
		        		if(structure.findEntity(currentGoalNode).getFirstLinkAt(GateTypesIF.GT_POR) != null) {
		        			String nextNode = structure.findEntity(currentGoalNode).getFirstLinkAt(GateTypesIF.GT_POR).getLinkedEntityID();
		        			for(int i = 0; i < structure.findEntity(nextNode).getGate(GateTypesIF.GT_RET).getNumberOfLinks(); i++) {
		        				if(structure.findEntity(nextNode).getLink(GateTypesIF.GT_RET, i).getLinkedEntityID().equals(currentGoalNode)) {
		        					weight = structure.findEntity(nextNode).getLink(GateTypesIF.GT_RET, i).getWeight() - 0.1; //TODO modify this by distance between nodes
		        					break;
		        				}
		        			}
		        		}
*/		        		
		        		linkEntities(beforeLastNode, currentGoalNode, GateTypesIF.GT_POR, 1.0, weight);
		        		deleteNode(lastNode);
	        		} else {
	        			linkEntities(lastNode, currentGoalNode, GateTypesIF.GT_POR, 1.0, weight);
	        		}
        		} else {
        			System.out.println("lastNode was null");
        		}
                lastNode = currentGoalNode;
                insertedInProtocol = true;
                structure.deleteLink(currentGoal, GateTypesIF.GT_SUB, structure.findEntity(currentGoal).getLastLinkAt(GateTypesIF.GT_SUB).getLinkedEntityID(), SlotTypesIF.ST_GEN);
                /*
                if(structure.findEntity(lastNode).getFirstLinkAt(GateTypesIF.GT_SUR) == null) {
                	System.out.println("error connecting");
                }
                */
                if(structure.findEntity(lastNode).getGate(GateTypesIF.GT_EXP).getNumberOfLinks() == 1) {
                	System.out.println("error connecting");
                }
        	}
        }
        
        if(onTheWay.getIncomingActivation() < 0.5 && !insertedInProtocol) {
	        if(lastNode != null && structure.findEntity(lastNode).getFirstLinkAt(GateTypesIF.GT_POR) == null) {
	            Link xLink = structure.findEntity(lastNode).getLink(GateTypesIF.GT_SUB, 0);
	            Link yLink = structure.findEntity(lastNode).getLink(GateTypesIF.GT_SUB, 1);
	            if(xLink != null && yLink != null) {
	                if(xLink.getWeight() == xposition && yLink.getWeight() == yposition) {
	                	// no movement
	                } else {
	                	double weight = 1.0;
		                newNode = structure.createConceptNode("pos " + xposition + ":" + yposition);
		                newX = structure.createConceptNode("x");
		                newY = structure.createConceptNode("y");
		                linkEntities(newNode, newX, GateTypesIF.GT_SUB, xposition, xposition);
		                linkEntities(newNode, newY, GateTypesIF.GT_SUB, yposition, yposition);
		                linkEntities(allNodes, newNode, GateTypesIF.GT_CAT, 1.0, 1.0);
		                
		                // test if node deletion is necessary
		                if(structure.findEntity(lastNode).getFirstLinkAt(GateTypesIF.GT_RET) != null) {
		                    String beforeLastNode = structure.findEntity(lastNode).getFirstLinkAt(GateTypesIF.GT_RET).getLinkedEntity().getID();
		                    Position beforeLastPos = NodeFunctions.getPosition(structure.findEntity(beforeLastNode));
		                    Position lastPos = NodeFunctions.getPosition(structure.findEntity(lastNode));
		                    Position newPos = NodeFunctions.getPosition(structure.findEntity(newNode));
		                    // test if node is not in static protocol
		                    //if(structure.findEntity(lastNode).getFirstLinkAt(GateTypesIF.GT_SUR) == null) {
		                    if(structure.findEntity(lastNode).getGate(GateTypesIF.GT_EXP).getNumberOfLinks() == 1) {
			                    // node-distance is less than threshold
			                    if(beforeLastPos.distance2D(newPos) < 3.0) {
			                        WorldVector vec1 = new WorldVector(lastPos.getX() - beforeLastPos.getX(), lastPos.getY() - beforeLastPos.getY());
			                        WorldVector vec2 = new WorldVector(newPos.getX() - lastPos.getX(), newPos.getY() - lastPos.getY());
			                        double angleDifference = Math.abs((vec1.getAngle() - vec2.getAngle()) % 360.0);
			                        if (angleDifference < 10.0) {
			                        	weight = (weight + structure.findEntity(lastNode).getLastLinkAt(GateTypesIF.GT_RET).getWeight()) / 2.0;
			                            stupidDeleteNode(lastNode);
			                            lastNode = beforeLastNode;
			                        }
			                    }
		                    }
		                }
		                if(checkIntegrity())
			            	System.out.println("point 3.1.2");
		                
		                linkEntities(lastNode, newNode, GateTypesIF.GT_POR, 1.0, weight);
		                //TODO decay evolvable?
		                structure.getGateManipulator(newNode).setGateDecayType(GateTypesIF.GT_RET, StandardDecays.DT_FAST_LINEAR);

		                lastNode = newNode;
		                
		                if(checkIntegrity())
			            	System.out.println("point 3.1.3");
	                }
	            }
	        } else {
	            newNode = this.structure.createConceptNode("pos " + xposition + ":" + yposition);
	            newX = this.structure.createConceptNode("x");
	            newY = this.structure.createConceptNode("y");
	            
	            linkEntities(newNode, newX, GateTypesIF.GT_SUB, xposition, xposition);
                linkEntities(newNode, newY, GateTypesIF.GT_SUB, yposition, yposition);
                linkEntities(allNodes, newNode, GateTypesIF.GT_CAT, 1.0, 1.0);
                
	            lastNode = newNode;
	        }
        }

        // protocol events
        if(hurt.getIncomingActivation() > 0.0) {
        	if(lastNode != null && currentPosition.distance2D(NodeFunctions.getPosition(structure.findEntity(lastNode))) < 0.5) {
        		addDamageNode(lastNode);
        	} else {
        		newNode = structure.createConceptNode("pos " + xposition + ":" + yposition);
                newX = structure.createConceptNode("x");
                newY = structure.createConceptNode("y");
                
                linkEntities(newNode, newX, GateTypesIF.GT_SUB, xposition, xposition);
                linkEntities(newNode, newY, GateTypesIF.GT_SUB, yposition, yposition);
                linkEntities(allNodes, newNode, GateTypesIF.GT_CAT, 1.0, 1.0);
                
                addDamageNode(newNode);
        	}
        }
        
        if(obstacle.getIncomingActivation() > 0.0) {
        	if(lastNode != null && currentPosition.distance2D(NodeFunctions.getPosition(structure.findEntity(lastNode))) < 0.5) {
        		addObstacleNode(lastNode);
        	} else {
        		newNode = structure.createConceptNode("pos " + xposition + ":" + yposition);
                newX = structure.createConceptNode("x");
                newY = structure.createConceptNode("y");
                
                linkEntities(newNode, newX, GateTypesIF.GT_SUB, xposition, xposition);
                linkEntities(newNode, newY, GateTypesIF.GT_SUB, yposition, yposition);
                linkEntities(allNodes, newNode, GateTypesIF.GT_CAT, 1.0, 1.0);
                
                addObstacleNode(newNode);
        	}
        }
        
        boolean eventCalled = false; 
        if(food.getIncomingActivation() > 0.0) {
        	notifyEvent(foodConcept, food.getIncomingActivation());
        	eventCalled = true;
        }
        
        if(water.getIncomingActivation() > 0.0) {
        	notifyEvent(waterConcept, water.getIncomingActivation());
        	eventCalled = true;
        }
        
        if(healing.getIncomingActivation() > 0.0) {
        	notifyEvent(healingConcept, healing.getIncomingActivation());
        	eventCalled = true;
        }
        
        /*
        if(eventCalled || counter == 0) {
        	repairNet();
        	System.out.println("nodes from protocol: " + structure.findEntity(allNodes).getGate(GateTypesIF.GT_SUB).getNumberOfLinks() * 3);
        }
        counter = (counter + 1) % 400;
        */
        
        if(eventCalled) {
        	repairNet();
        }
        clearNodes(); 
    }
    
    /**
     * deletes all nodes referenced in the list toDelete
     */
    private void clearNodes() {
    	int pensum = toDelete.size();
    	int counter = 0;
    	for(int i = 0; i < toDelete.size(); i++) {
    		counter++;
    		try {
    			if(toDelete.get(i) != null)
    				deleteNode(toDelete.get(i));
    		} catch (NetIntegrityException e) {
    			e.printStackTrace();
            }
        }
    	toDelete.clear();
    	if(pensum != counter)
    		System.out.println("deleted Nodes: " + counter + " but " + pensum + " should have been deleted.");
    }
    
    /**
     * deletes all nodes that have RET-links with weight lower
     * than a given treshold
     */
    private void decayProtocol() {
        if(lastNode == null) {
        	return;
        }
        
        String currentNode = lastNode;
        
        while(structure.findEntity(currentNode).getLastLinkAt(GateTypesIF.GT_RET) != null) {
        	try {
				structure.findEntity(currentNode).updateDecayState();
			} catch (NetIntegrityException e) {
				e.printStackTrace();
			}
        	
            if(structure.findEntity(currentNode).getLastLinkAt(GateTypesIF.GT_RET) != null && structure.findEntity(currentNode).getLastLinkAt(GateTypesIF.GT_RET).getWeight() < ConstantValues.LINKTHRESHOLD) {
                String linkedNode = structure.findEntity(currentNode).getLastLinkAt(GateTypesIF.GT_RET).getLinkedEntityID();
                //if(structure.findEntity(linkedNode).getLastLinkAt(GateTypesIF.GT_SUR) == null) {
                if(structure.findEntity(linkedNode).getGate(GateTypesIF.GT_EXP).getNumberOfLinks() == 1) {              
                	if(!toDelete.contains(linkedNode))
                		toDelete.add(linkedNode);
                }
            }
            if(structure.findEntity(currentNode).getLastLinkAt(GateTypesIF.GT_RET) != null) {
            	currentNode = structure.findEntity(currentNode).getLastLinkAt(GateTypesIF.GT_RET).getLinkedEntityID();
            	if(currentNode == lastNode) {
            		System.out.println("LOOP");
            		int counter = 0;
            		System.out.println("" + ++counter + ": (" + NodeFunctions.getPosition(structure.findEntity(currentNode)).getX() + "," + NodeFunctions.getPosition(structure.findEntity(currentNode)).getY() + ")");
            		while(structure.findEntity(currentNode).getLastLinkAt(GateTypesIF.GT_RET) != null) {
            			currentNode = structure.findEntity(currentNode).getLastLinkAt(GateTypesIF.GT_RET).getLinkedEntityID();
            			System.out.println("" + ++counter + ": (" + NodeFunctions.getPosition(structure.findEntity(currentNode)).getX() + "," + NodeFunctions.getPosition(structure.findEntity(currentNode)).getY() + ")");
            			if(currentNode == lastNode)
            				break;
            		}
            		return;
            	}
            } else {
            	break;
            }
        }
    }
    
    /**
     * protocols the way that led to the event, eventually merging
     * the way with older ones that are similar
     * 
     * @param conceptNode describes the concept of the event
     * @param incomingActivation
     */
    private void notifyEvent(String conceptNode, double incomingActivation) throws NetIntegrityException {
    	insertedInProtocol = false;
    	if(lastNode == null) {
    		System.out.println("null detected");
    		return;
    	}
    		
    	double threshold = 0.0;
        String oldNode;
    	
        boolean debug = false;
        /*
        if(structure.findEntity(lastNode).getFirstLinkAt(GateTypesIF.GT_SUR) != null) {
        	debug = true;
        	System.out.println("debug start");
        }
        */
        threshold = 1.0 - incomingActivation;
        
        // TODO test this
        if(threshold > 0.9) {
        	//System.out.println(threshold);
        	return;
        }
        
        boolean merged = false;
        
        //start new code
        //if((structure.findEntity(lastNode).getFirstLinkAt(GateTypesIF.GT_POR) == null) && (structure.findEntity(lastNode).getFirstLinkAt(GateTypesIF.GT_SUR) == null)) {
        if((structure.findEntity(lastNode).getFirstLinkAt(GateTypesIF.GT_POR) == null) && (structure.findEntity(lastNode).getGate(GateTypesIF.GT_EXP).getNumberOfLinks() == 1)) {
        	//if(debug)
        	//	System.out.println("error");
	        String mergeNode = null;
	        for(int i = 0; i < structure.findEntity(conceptNode).getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
	        	if(structure.findEntity(conceptNode).getGate(GateTypesIF.GT_CAT).getLinkAt(i).getLinkedEntity().getFirstLinkAt(GateTypesIF.GT_POR) == null) {
	        		NetEntity toCompare = structure.findEntity(conceptNode).getGate(GateTypesIF.GT_CAT).getLinkAt(i).getLinkedEntity();
	        		if(NodeFunctions.getPosition(structure.findEntity(lastNode)).distance2D(NodeFunctions.getPosition(toCompare)) <= ConstantValues.MINOBJECTSIZE) {        			
	        			mergeNode = toCompare.getID();
	        			// this should never occur, just for safety
	        			if(structure.findEntity(lastNode).getLastLinkAt(GateTypesIF.GT_RET) == null) {
	        				if(!toDelete.contains(lastNode))
		                		toDelete.add(lastNode);
	        				lastNode = null;
	        				merged = true;
	        				break;
	        			}
	        			lastNode = structure.findEntity(lastNode).getLastLinkAt(GateTypesIF.GT_RET).getLinkedEntityID();
	        			
	        			boolean stop = false;
	        			while(!stop) {
	        				stop = true;
	        				for(int j = 0; j < structure.findEntity(mergeNode).getGate(GateTypesIF.GT_RET).getNumberOfLinks(); j++) {
	        					toCompare = structure.findEntity(mergeNode).getGate(GateTypesIF.GT_RET).getLinkAt(j).getLinkedEntity();
	        					if((NodeFunctions.getPosition(structure.findEntity(lastNode)).distance2D(NodeFunctions.getPosition(toCompare)) <= ConstantValues.MINOBJECTSIZE)) {					
	        						mergeNode = toCompare.getID();
	        						if(!(structure.findEntity(lastNode).getLastLinkAt(GateTypesIF.GT_RET) == null)) {
	        							lastNode = structure.findEntity(lastNode).getLastLinkAt(GateTypesIF.GT_RET).getLinkedEntityID();
	        							stop = false;
	        							break;
	        						}
	        					}
	        				}
	        			}
	        			if(structure.findEntity(lastNode).getLastLinkAt(GateTypesIF.GT_RET) != null) {
	        				String deleteNode = lastNode;
	        				while(structure.findEntity(deleteNode).getFirstLinkAt(GateTypesIF.GT_POR) != null) {
	        					deleteNode = structure.findEntity(deleteNode).getFirstLinkAt(GateTypesIF.GT_POR).getLinkedEntityID();
	        					if(!toDelete.contains(deleteNode))
	    	                		toDelete.add(deleteNode);
	        				}
	        				//hack
	        				if(NodeFunctions.getPosition(structure.findEntity(lastNode)).distance2D(NodeFunctions.getPosition(structure.findEntity(mergeNode))) > 7.0) {
	        					System.out.println("error detected, distance: " + NodeFunctions.getPosition(structure.findEntity(lastNode)).distance2D(NodeFunctions.getPosition(structure.findEntity(mergeNode))));
	        					System.out.println(NodeFunctions.positionToString(NodeFunctions.getPosition(structure.findEntity(lastNode))) + " -> " + NodeFunctions.positionToString(NodeFunctions.getPosition(structure.findEntity(mergeNode))));
	        					System.out.println("lastNode info: " + NodeFunctions.positionToString(NodeFunctions.getPosition(structure.findEntity(lastNode))) + " <- " + NodeFunctions.positionToString(NodeFunctions.getPosition(structure.findEntity(lastNode).getFirstLinkAt(GateTypesIF.GT_POR).getLinkedEntity())));
	        				} else {
		        				linkEntities(lastNode, mergeNode, GateTypesIF.GT_POR, 1.0, 1.0);
	        				}
	        			} else {
	        				merged = true;
	        				String deleteNode = lastNode;
	        				if(!toDelete.contains(deleteNode))
		                		toDelete.add(deleteNode);
	        				while(structure.findEntity(deleteNode).getFirstLinkAt(GateTypesIF.GT_POR) != null) {
	        					deleteNode = structure.findEntity(deleteNode).getFirstLinkAt(GateTypesIF.GT_POR).getLinkedEntityID();
	        					if(!toDelete.contains(deleteNode))
	    	                		toDelete.add(deleteNode);
	        				}
	        				lastNode = null;
	        			}
	        			break;
	        		}
	        	}
	        }
        }
        // end newCode
        
        if(!merged) {
        	//if(debug)
        	//	System.out.println("d-start");
	    	boolean linkExists = false;
	    	for(int i = 0; i < structure.findEntity(lastNode).getGate(GateTypesIF.GT_EXP).getNumberOfLinks(); i++) {
	    		if(structure.findEntity(lastNode).getLink(GateTypesIF.GT_EXP, i).getLinkedEntityID().equals(conceptNode))
	    			linkExists = true;
	    	}
	    	if(!linkExists) {
	    		linkEntities(conceptNode, lastNode, GateTypesIF.GT_CAT, 1.0, 1.0);
	    	}
	              
	        if (structure.findEntity(lastNode).getLastLinkAt(GateTypesIF.GT_RET) != null) {
	            String currentNode = structure.findEntity(lastNode).getLastLinkAt(GateTypesIF.GT_RET).getLinkedEntityID();
	            oldNode = lastNode;
	            
	            while (structure.findEntity(oldNode).getLastLinkAt(GateTypesIF.GT_RET) != null) {
	            	if(structure.findEntity(oldNode).getLastLinkAt(GateTypesIF.GT_RET).getWeight() >= threshold) {
	            		//if(debug)
	                	//	System.out.println("d-linked");
	            		linkEntities(conceptNode, currentNode, GateTypesIF.GT_CAT, 1.0, 1.0);           	      
	                	structure.getGateManipulator(oldNode).setGateDecayType(GateTypesIF.GT_RET, StandardDecays.NO_DECAY);
	                	
	                	if(structure.findEntity(currentNode).getLastLinkAt(GateTypesIF.GT_RET) == null)
		                    break;
	                	oldNode = currentNode;
		                currentNode = structure.findEntity(currentNode).getLastLinkAt(GateTypesIF.GT_RET).getLinkedEntityID();         
	            	} else {
		                //if(structure.findEntity(oldNode).getFirstLinkAt(GateTypesIF.GT_SUR) == null) {
	            		if(structure.findEntity(oldNode).getGate(GateTypesIF.GT_EXP).getNumberOfLinks() == 1) {
		                	if(!toDelete.contains(oldNode))
		                		toDelete.add(oldNode);
		                }
		                if(structure.findEntity(currentNode).getLastLinkAt(GateTypesIF.GT_RET) == null)
		                    break;
	                	oldNode = currentNode;
		                currentNode = structure.findEntity(currentNode).getLastLinkAt(GateTypesIF.GT_RET).getLinkedEntityID();
	            	}          	                
	            }
	        } else {
	        	System.out.println("nothing to link");
	        }
	        lastNode = null;
	    }
    }
    /*
    private void deleteNode(String ID) throws NetIntegrityException {
    	//if(structure.findEntity(ID) != null && structure.findEntity(ID).getFirstLinkAt(GateTypesIF.GT_SUR) == null) {
    	if(structure.findEntity(ID) != null && structure.findEntity(ID).getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 1) {
    		for(int i = 0; i < structure.findEntity(ID).getGate(GateTypesIF.GT_SUB).getNumberOfLinks(); i++) {
    			structure.deleteEntity(structure.findEntity(ID).getLink(GateTypesIF.GT_SUB, i).getLinkedEntityID());
    		}
    		structure.deleteEntity(ID);
    	} else {
    		System.out.println("node not deleted");
    	}
    }
    */
    
    private void deleteNode(String ID) throws NetIntegrityException {
    	if(structure.findEntity(ID) != null && structure.findEntity(ID).getGate(GateTypesIF.GT_EXP).getNumberOfLinks() == 1) {
    		while(structure.findEntity(ID) != null && structure.findEntity(ID).getFirstLinkAt(GateTypesIF.GT_SUB) != null) {
    			structure.deleteEntity(structure.findEntity(ID).getFirstLinkAt(GateTypesIF.GT_SUB).getLinkedEntityID());
    		}
    		structure.deleteEntity(ID);
    	} else {
    		System.out.println("node not deleted");
    	}
    }
    
    /*
    private void stupidDeleteNode(String ID) throws NetIntegrityException {
    	for(int i = 0; i < structure.findEntity(ID).getGate(GateTypesIF.GT_SUB).getNumberOfLinks(); i++) {
    		structure.deleteEntity(structure.findEntity(ID).getLink(GateTypesIF.GT_SUB, i).getLinkedEntityID());
    	}
    	structure.deleteEntity(ID);
    }
    */
    
    /**
     * deletes the node with ID without checking 
     */
    private void stupidDeleteNode(String ID) throws NetIntegrityException {
    	while(structure.findEntity(ID) != null && structure.findEntity(ID).getFirstLinkAt(GateTypesIF.GT_SUB) != null) {
			structure.deleteEntity(structure.findEntity(ID).getFirstLinkAt(GateTypesIF.GT_SUB).getLinkedEntityID());
		}
		structure.deleteEntity(ID);
    }
    
    /**
     * creates a new damage node by linking the damage concept and
     * the node described by ID
     * @param ID of the node to protocol as a damage node
     */
    private void addDamageNode(String ID) throws NetIntegrityException {
    	if(lastInsertedDamage != null) {
    		if(NodeFunctions.getPosition(structure.findEntity(ID)).distance2D(lastInsertedDamage) < ConstantValues.NODEMINDISTANCE)
    			return;
    	}
    	ConceptNode damage = (ConceptNode)structure.findEntity(damageConcept);
    	int numberOfNodes = damage.getGate(GateTypesIF.GT_CAT).getNumberOfLinks();
    	Position testPosition = NodeFunctions.getPosition(structure.findEntity(ID));
    	for(int i = 0; i < numberOfNodes; i++) {
    		if(testPosition.distance2D(NodeFunctions.getPosition(damage.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity())) < ConstantValues.NODEMINDISTANCE)
	    			return;
    	}
    	linkEntities(damageConcept, ID, GateTypesIF.GT_CAT, 1.0, 1.0);
        lastInsertedDamage = testPosition;
    }
    
    /**
     * creates a new obstacle node by linking the obstacle concept and
     * the node described by ID
     * @param ID of the node to protocol as an obstacle node
     */
    private void addObstacleNode(String ID) throws NetIntegrityException {
    	if(lastInsertedObstacle != null) {
    		if(NodeFunctions.getPosition(structure.findEntity(ID)).distance2D(lastInsertedObstacle) < ConstantValues.NODEMINDISTANCE)
    			return;
    	}
    	ConceptNode obstacle = (ConceptNode)structure.findEntity(obstacleConcept);
    	int numberOfNodes = obstacle.getGate(GateTypesIF.GT_CAT).getNumberOfLinks();
    	Position testPosition = NodeFunctions.getPosition(structure.findEntity(ID));
    	for(int i = 0; i < numberOfNodes; i++) {
    		if(testPosition.distance2D(NodeFunctions.getPosition(obstacle.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity())) < ConstantValues.NODEMINDISTANCE)
	    			return;
    	}
    	linkEntities(obstacleConcept, ID, GateTypesIF.GT_CAT, 1.0, 1.0);
        lastInsertedObstacle = testPosition;
    }
    
    /**
     * links two nodes with the POR/RET or SUB/SUR 
     * @param from ID of first node
     * @param to ID of second node
     * @param type of the connection
     * 	GateTypesIF.GT_POR for POR/RET
     * 	GateTypesIF.GT_SUB for SUB/SUR
     * @param weight
     * @param backWeight
     */
    private void linkEntities(String from, String to, int type, double weight, double backWeight) throws NetIntegrityException {
    	if(from.equals(to)) {
    		logger.info("link not created to avoid loops");
    		return;
    	}
    	
    	if(type == GateTypesIF.GT_POR) {
    		structure.createLink(from, GateTypesIF.GT_POR, to, SlotTypesIF.ST_GEN, weight, 1.0);
    		structure.createLink(to, GateTypesIF.GT_RET, from, SlotTypesIF.ST_GEN, backWeight, 1.0);
    	} else if(type == GateTypesIF.GT_SUB) {
    		structure.createLink(from, GateTypesIF.GT_SUB, to, SlotTypesIF.ST_GEN, weight, 1.0);
    		structure.createLink(to, GateTypesIF.GT_SUR, from, SlotTypesIF.ST_GEN, backWeight, 1.0);
    	} else if(type == GateTypesIF.GT_CAT) {
    		structure.createLink(from, GateTypesIF.GT_CAT, to, SlotTypesIF.ST_GEN, weight, 1.0);
    		structure.createLink(to, GateTypesIF.GT_EXP, from, SlotTypesIF.ST_GEN, backWeight, 1.0);
    	} else if(type == GateTypesIF.GT_SYM) {
    		structure.createLink(from, GateTypesIF.GT_SYM, to, SlotTypesIF.ST_GEN, weight, 1.0);
    		structure.createLink(to, GateTypesIF.GT_REF, from, SlotTypesIF.ST_GEN, backWeight, 1.0);
    	}
    	
    	/*
    	if(!debug) {
    		System.out.println("" + cycle++ + ":" + structure.findEntity(from).getFirstLinkAt(type).getLinkedEntityID().equals(structure.findEntity(from).getLastLinkAt(type).getLinkedEntityID()));
    	}
    	*/
    }
    
    private void resetModule() {
    	String oldNode = lastNode;
    	while(oldNode != null && structure.findEntity(oldNode).getLastLinkAt(GateTypesIF.GT_RET) != null) {
    		if(!toDelete.contains(oldNode))
    			toDelete.add(oldNode);
    		oldNode = structure.findEntity(oldNode).getLastLinkAt(GateTypesIF.GT_RET).getLinkedEntityID();
    	}
    	if(!toDelete.contains(oldNode))
			toDelete.add(oldNode);
    	
    	clearNodes();
    	lastNode = null;
    	insertedInProtocol = false;
    }
    
    /**
     * optimate ways with the given position by trying to shorten
     * them
     * @param position
     */
    private void optimateWaypoints(Position position) throws NetIntegrityException {
    	if(onTheWay.getIncomingActivation() < 0.5) {
    		optimateWaypoints(foodConcept, position);
    		optimateWaypoints(waterConcept, position);
    		optimateWaypoints(healingConcept, position);
    	}
    }
    
    
    /**
     * if the given @param position can optimate a way of @param concept it is corrected 
     */
    private void optimateWaypoints(String concept, Position position) throws NetIntegrityException {
    	Position currentPosition = new Position(position);
    	
    	for(int i = 0; i < structure.findEntity(concept).getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
    		NetEntity toCompare = structure.findEntity(concept).getGate(GateTypesIF.GT_CAT).getLinkAt(i).getLinkedEntity();
    		//if(!toCompare.getID().equals(lastNode)) {
	    		if(toCompare.getFirstLinkAt(GateTypesIF.GT_POR) != null && toCompare.getFirstLinkAt(GateTypesIF.GT_RET) != null) {
	    			if(NodeFunctions.getPosition(toCompare).distance2D(currentPosition) <= ConstantValues.MINOBJECTSIZE) {
	    				double distanceCompareToNext = NodeFunctions.getPosition(toCompare.getFirstLinkAt(GateTypesIF.GT_POR).getLinkedEntity()).distance2D(NodeFunctions.getPosition(toCompare));
	    				double distanceCurrentToNext = NodeFunctions.getPosition(toCompare.getFirstLinkAt(GateTypesIF.GT_POR).getLinkedEntity()).distance2D(currentPosition);
	    				boolean optimationPossible = true;
	    				for(int j = 0; j < toCompare.getGate(GateTypesIF.GT_RET).getNumberOfLinks(); j++) {
	    					double distanceCompareToPrevious = NodeFunctions.getPosition(toCompare.getGate(GateTypesIF.GT_RET).getLinkAt(j).getLinkedEntity()).distance2D(NodeFunctions.getPosition(toCompare));
	    					double distanceCurrentToPrevious = NodeFunctions.getPosition(toCompare.getGate(GateTypesIF.GT_RET).getLinkAt(j).getLinkedEntity()).distance2D(currentPosition);
	    					if((distanceCurrentToNext + distanceCurrentToPrevious) >= (distanceCompareToNext + distanceCompareToPrevious)) {
	    						optimationPossible = false;
	    						break;
	    					}
	    				}
	    				if(optimationPossible) {
	    					structure.changeLinkParameters(toCompare.getLink(GateTypesIF.GT_SUB, 0), x.getIncomingActivation(), 1.0);
	    					structure.changeLinkParameters(toCompare.getLink(GateTypesIF.GT_SUB, 1), y.getIncomingActivation(), 1.0);
	    				}
	    			}
	    		}
    		//}
    	}
    }
    
    /**
     * combines nodes of a way, which are very close to one another
     * @throws NetIntegrityException 
     */
    private void optimateWaypoints() throws NetIntegrityException {
    	optimateWaypoints(foodConcept);
    	optimateWaypoints(waterConcept);
    	optimateWaypoints(healingConcept);
    }
    
    /**
     * combines nodes of a way of @param cocept, which are very close to one another
     * @throws NetIntegrityException 
     */
    private void optimateWaypoints(String concept) throws NetIntegrityException {
    	for(int i = 0; i < structure.findEntity(concept).getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
    		if(structure.findEntity(concept).getGate(GateTypesIF.GT_CAT).getLinkAt(i) != null) {
	    		NetEntity currentNode = structure.findEntity(concept).getGate(GateTypesIF.GT_CAT).getLinkAt(i).getLinkedEntity();
	    		for(int j = 0; j < currentNode.getGate(GateTypesIF.GT_RET).getNumberOfLinks(); j++) {
	    			NetEntity beforeNode = currentNode.getGate(GateTypesIF.GT_RET).getLinkAt(j).getLinkedEntity();
	    			if(!beforeNode.getID().equals(lastNode)) {
		    			if(NodeFunctions.getPosition(currentNode).distance2D(NodeFunctions.getPosition(beforeNode)) < ConstantValues.MAXMERGEDISTANCE) {
		    				for(int k = 0; k < beforeNode.getGate(GateTypesIF.GT_RET).getNumberOfLinks(); k++) {
		    					linkEntities(beforeNode.getGate(GateTypesIF.GT_RET).getLinkAt(k).getLinkedEntityID(), currentNode.getID(), GateTypesIF.GT_POR, beforeNode.getFirstLinkAt(GateTypesIF.GT_POR).getWeight(), beforeNode.getFirstLinkAt(GateTypesIF.GT_POR).getConfidence());
		    				}
		    				stupidDeleteNode(beforeNode.getID());
		    				break;
		    			}
	    			}
	    		}
    		}
    	}
    }
    
    /**
     * @return false if net is ok, true if net structure is corrupt
     * @throws NetIntegrityException
     */
    private boolean checkIntegrity() throws NetIntegrityException {
    	boolean toReturn1 = checkIntegrity(foodConcept);
    	boolean toReturn2 = checkIntegrity(waterConcept);
    	boolean toReturn3 = checkIntegrity(healingConcept);
    	
    	return toReturn1 || toReturn2 || toReturn3;
    }
    
    private boolean checkIntegrity(String concept) throws NetIntegrityException {
    	boolean toReturn = false;
    	for(int i = 0; i < structure.findEntity(concept).getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
    		NetEntity current = structure.findEntity(concept).getGate(GateTypesIF.GT_CAT).getLinkAt(i).getLinkedEntity();
    		int counter = 0;
    		for(int j = 0; j < current.getGate(GateTypesIF.GT_POR).getNumberOfLinks(); j++) {
    			if(toDelete.contains(current.getGate(GateTypesIF.GT_POR).getLinkAt(j).getLinkedEntityID()))
    				counter++;
    		}
    			
    		if(current.getGate(GateTypesIF.GT_POR).getNumberOfLinks() - counter > 1) {
    			System.out.println("net corrupted at " + NodeFunctions.positionToString(NodeFunctions.getPosition(current)));
    			toReturn = true;
    		}
    	}
    	return toReturn;
    }
    
    private void repairNet() throws NetIntegrityException {
    	repairNet(foodConcept);
    	repairNet(waterConcept);
    	repairNet(healingConcept);
    	checkNodes();
    }
    
    private void repairNet(String concept) throws NetIntegrityException {
    	NetEntity currentNode;
    	for(int i = 0; i < structure.findEntity(concept).getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
    		currentNode = structure.findEntity(concept).getGate(GateTypesIF.GT_CAT).getLinkAt(i).getLinkedEntity();
    		for(int j = 0; j < currentNode.getGate(GateTypesIF.GT_RET).getNumberOfLinks(); j++) {
    			NetEntity tempNode = currentNode.getGate(GateTypesIF.GT_RET).getLinkAt(j).getLinkedEntity();
    			//if(tempNode.getFirstLinkAt(GateTypesIF.GT_SUR) == null) {
    			if(tempNode.getGate(GateTypesIF.GT_EXP).getNumberOfLinks() == 1) {
    				boolean stop = false;
    				while(!stop) {
    					toDelete.add(tempNode.getID());
    					if(tempNode.getLastLinkAt(GateTypesIF.GT_RET) != null)
    						tempNode = tempNode.getLastLinkAt(GateTypesIF.GT_RET).getLinkedEntity();
    					else
    						stop = true;
    				}
    			}
    		}
    	}
    }
    
    private void checkNodes() throws NetIntegrityException {
    	if(allNodes == null)
    		return;
    	for(int i = 0; i < structure.findEntity(allNodes).getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
    		ConceptNode node = (ConceptNode)structure.findEntity(allNodes).getLink(GateTypesIF.GT_CAT, i).getLinkedEntity();
    		node.updateDecayState();
    		if(node.getGate(GateTypesIF.GT_EXP).getNumberOfLinks() > 0)
    			continue;
    		
    		if(node.getLastLinkAt(GateTypesIF.GT_RET) != null && node.getLastLinkAt(GateTypesIF.GT_RET).getWeight() < ConstantValues.LINKTHRESHOLD) {
                String linkedNode = node.getLastLinkAt(GateTypesIF.GT_RET).getLinkedEntityID();
                if(structure.findEntity(linkedNode).getGate(GateTypesIF.GT_EXP).getNumberOfLinks() == 1) {
                	if(!toDelete.contains(linkedNode))
                		toDelete.add(linkedNode);
                }
                continue;
            }
    	}
    }
}
