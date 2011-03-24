/*
 * Created on 29.04.2005
 *
 */
package org.micropsi.nodenet.modules;

import java.util.ArrayList;
import java.util.Iterator;

import org.micropsi.common.coordinates.Position;
import org.micropsi.common.coordinates.WorldVector;
import org.micropsi.comp.ConstantValues;
import org.micropsi.comp.NodeFunctions;
import org.micropsi.comp.agent.voronoi.Region;
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

/**
 * @author Markus
 *
 * decides the next action of the agent based
 * on sensordata, regions and waynodes
 */
public class AdvancedPlanningModule extends AbstractNativeModuleImpl {
    // slots
    private static final int XCOORDINATE     = 16500;
	private static final int YCOORDINATE     = 16501;
	private static final int FOODURGE	     = 16502;
	private static final int WATERURGE       = 16503;
	private static final int HEALINGURGE     = 16504;
	private static final int AFFILIATIONURGE = 16505;
	private static final int EXPLORATIONURGE = 16506; // not really a slot
	private static final int RESET			 = 16507;
	private static final int OBSTACLE        = 16508;
	private static final int PROTOCOLREGIONS = 16509;
    
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
	private static final int SMILE			   = 16513;
	private static final int BITE			   = 16514;
	private static final int TARGETRED		   = 16515;
	private static final int TARGETGREEN	   = 16516;
	private static final int TARGETBLUE		   = 16517;
	private static final int REGIONNODE		   = 16518;
	private static final int UNKNOWNREGIONNODE = 16519;
	
	// competence
	private static final int FOODCOMPETENCE    		= 16520;
	private static final int WATERCOMPETENCE   		= 16521;
	private static final int HEALINGCOMPETENCE 		= 16522;
	private static final int EXPLORATIONCOMPETENCE  = 16523;
	private static final int SOCIALCOMPETENCE		= 16524;
	
    private Slot x;
	private Slot y;
	private Slot food;
	private Slot water;
	private Slot healing;
	private Slot hurt;
	private Slot affiliation;
	private Slot reset;
	private Slot obstacle;
	private Slot foodCompetenceSlot;
	private Slot waterCompetenceSlot;
	private Slot healingCompetenceSlot;
	private Slot explorationCompetenceSlot;
	private Slot protocolRegions;
	
	private ConceptNode foodConcept = null;
	private ConceptNode waterConcept = null;
	private ConceptNode healingConcept = null;
	private ConceptNode damageConcept = null;
	private ConceptNode obstacleConcept = null;
	private ConceptNode currentGoal = null;
	private ConceptNode nearAgentsConcept = null; 
	private ConceptNode regionsConcept = null;
	private ConceptNode unknownRegionsConcept = null;
	
	private boolean firsttime = true;
	private int currentMotive = 0;
	private String goalID = null;
	private String oldGoalID = null;
	
	private int[][] knownMaze;
	
	private String lastSmiledAgentID = null;
	private long lastSmileStep = 0;
	
	// inner states
	private double foodCompetence = 1.0;
	private double waterCompetence = 1.0;
	private double healingCompetence = 1.0;
	private double affiliationCompetence = 0.75;
	
	private String foodGoal = null;
	private String waterGoal = null;
	private String healingGoal = null;
	private String explorationGoal = null;
	
	private boolean onTheWay = false;
	private boolean newData = true; 
	//private Position lastPosition;
	private boolean regionsProtocolled;
	private int numberOfObstacles = 0;
	
	private boolean debug = false;
	
	private Position lastPlanPositionFood = null;
	private Position lastPlanPositionWater = null;
	private Position lastPlanPositionHealing = null;
	private Position lastPlanPositionExploration = null;
	
	private double explorationStrength = ConstantValues.EXPLORATIONSTRENGTH;
	
	private Position startPosition = null;
	private boolean settedStartPosition = false;
	
	// navigate
	boolean navigating = false;
	int navigationIndex = 0;
	Position[] way = null;
	
	//debug
	int cycle = 0;
	
	public AdvancedPlanningModule() {	
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
			    	case SMILE: return "smile";
			    	case BITE: return "bite";
			    	case TARGETRED: return "target-red";
			    	case TARGETGREEN: return "target-green";
			    	case TARGETBLUE: return "target-blue";
			    	case REGIONNODE: return "regions";
			    	case UNKNOWNREGIONNODE: return "unknownRegions";
			    	case FOODCOMPETENCE: return "food-competence";
			    	case WATERCOMPETENCE: return "water-competence";
			    	case HEALINGCOMPETENCE: return "healing-competence";
			    	case EXPLORATIONCOMPETENCE: return "exploration-competence";			    	
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
					case OBSTACLE: return "obstacle"; 
					case FOODCOMPETENCE: return "food-competence";
			    	case WATERCOMPETENCE: return "water-competence";
			    	case HEALINGCOMPETENCE: return "healing-competence";
			    	case EXPLORATIONCOMPETENCE: return "exploration-competence";
			    	case PROTOCOLREGIONS: return "stop-protocol-regions";
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
	        RESET,
	        OBSTACLE,
	        FOODCOMPETENCE,
	        WATERCOMPETENCE,
	        HEALINGCOMPETENCE,
	        EXPLORATIONCOMPETENCE,
	        PROTOCOLREGIONS
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
			ONTHEWAY,
			SMILE,
			BITE,
			TARGETRED,
			TARGETGREEN,
			TARGETBLUE,
			REGIONNODE,
			UNKNOWNREGIONNODE,
			FOODCOMPETENCE,
	        WATERCOMPETENCE,
	        HEALINGCOMPETENCE,
	        EXPLORATIONCOMPETENCE
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
			    case OBSTACLE:
			    	obstacle = slots[i];
			    	break;
			    case FOODCOMPETENCE:
			    	foodCompetenceSlot = slots[i];
			    	break;
			    case WATERCOMPETENCE:
			    	waterCompetenceSlot = slots[i];
			    	break;
			    case HEALINGCOMPETENCE:
			    	healingCompetenceSlot = slots[i];
			    	break;
			    case EXPLORATIONCOMPETENCE:
			    	explorationCompetenceSlot = slots[i];
			    	break;
			    case PROTOCOLREGIONS:
			    	protocolRegions = slots[i];
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
			
			l = manipulator.getGate(NEARAGENTSCONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to nearagentsconcept");
			else
				nearAgentsConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(REGIONNODE).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to regionsconcept");
			else
				regionsConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(UNKNOWNREGIONNODE).getLinkAt(0);
			if(l == null)
				logger.warn("protocol has no link to unknownregionsconcept");
			else
				unknownRegionsConcept = (ConceptNode)l.getLinkedEntity();
			
			Position lastPosition = NodeFunctions.getPosition(x.getIncomingActivation(), y.getIncomingActivation());
			setLastPosition(FOODURGE, lastPosition);
			setLastPosition(WATERURGE, lastPosition);
			setLastPosition(HEALINGURGE, lastPosition);
			setLastPosition(EXPLORATIONURGE, lastPosition);
			
			knownMaze = new int[(int)ConstantValues.WORLDMAXX][(int)ConstantValues.WORLDMAXY];
			for(int  i = 0; i < (int)ConstantValues.WORLDMAXX; i++)
				for(int j = 0; j < (int)ConstantValues.WORLDMAXY; j++)
					knownMaze[i][j] = -1;
			
			createMaze();
		}
        
        if(protocolRegions.getIncomingActivation() < 0.5) {
        	regionsProtocolled = true;
        } else {
        	regionsProtocolled = false;
        }
        
        /*
        if(debug) {
        	long time = System.currentTimeMillis();
        	debug = false;
        	Position[] way = navigate(new Position(45, 45), new Position(10,60));
        	System.out.println(System.currentTimeMillis() - time);
        	if(way == null) {
        		System.out.println("no way");
        	} else {
	        	for(int i = 0; i < way.length; i++) {
	        		System.out.println(NodeFunctions.positionToString(way[i]));
	        	}
        	}
        }
        */
        
        if(!settedStartPosition) {
        	double xposition = x.getIncomingActivation();
            double yposition = y.getIncomingActivation();
            
            if(!(xposition == 0.0 && yposition == 0.0)) {
            	startPosition = NodeFunctions.getPosition(xposition, yposition);
            	settedStartPosition = true;
            }

        }
        
        if(reset.getIncomingActivation() > 0.5) {
        	resetModule();
        	return;
        }
        
        Position currentPosition = NodeFunctions.getPosition(x.getIncomingActivation(), y.getIncomingActivation());
        manipulator.setGateActivation(RANDOM, 0.0);
        
        if(regionsProtocolled) {
        	createMaze(currentPosition);
        }
        
        // calculate motive
        /*
        int newMotive = 0;
        double newMotiveStrength = 0.0;
        */
        
        if(goalID != null && currentMotive == EXPLORATIONURGE) {
        	if(isReachable(currentPosition, NodeFunctions.getPosition(structure.findEntity(goalID)))) {
        		oldGoalID = goalID;
        	} else {
        		oldGoalID = null;
        		goalID = null;
        	}
        } else {
        	oldGoalID = null;
        }
        
        int newMotive = EXPLORATIONURGE;
        double newMotiveStrength = explorationStrength;
        double oldMotiveStrength = explorationStrength;
        for(int i = 2; i <= 6; i++) {
            if((slots[i].getIncomingActivation()/* + getCompetence(slots[i].getType()) / 2.0*/) > newMotiveStrength) {
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
            if(oldMotiveStrength + ConstantValues.STUBBORNESS < newMotiveStrength && newData) {
            	//System.out.println("" + getNetProperties().getProperty("agentName") +  " new Motive: " + newMotive + " with strength " +newMotiveStrength);
                currentMotive = newMotive;
                clear();
                newData = false;
                //TODO if new motive cannot be fulfilled return to old one without deleting goal
            }
        }
        
        // already have a plan
        //System.out.println("" + netstep + ": goal : " + (goalID != null));
        if(goalID != null) {
        	decreaseCompetence(currentMotive);
            ConceptNode goal = (ConceptNode)structure.findEntity(goalID);
            if(goal == null) {
            	clear();
            	return;
            }
            if(currentPosition.distance2D(NodeFunctions.getPosition(goal)) < ConstantValues.STEPLENGTH) {
            	manipulator.setGateActivation(NOPROTOCOL, 1.0);
            	
            	/*
            	// goal reached
                if(goal.getFirstLinkAt(GateTypesIF.GT_POR) == null) {
                */
                	increaseCompetence(currentMotive);
                	clear();
                	/*
                } else {
	                ConceptNode newGoal = (ConceptNode)goal.getFirstLinkAt(GateTypesIF.GT_POR).getLinkedEntity();
	                
	                manipulator.setGateActivation(XGOAL, newGoal.getLink(GateTypesIF.GT_SUB, 0).getWeight());
	                manipulator.setGateActivation(YGOAL, newGoal.getLink(GateTypesIF.GT_SUB, 1).getWeight());
	                goalID = newGoal.getID();
	                onTheWay = true;
                }
                */
            } else if (!onTheWay) {
            	if(!newData || isReachable(currentPosition, NodeFunctions.getPosition(goal))) {
            		manipulator.setGateActivation(XGOAL, structure.findEntity(goalID).getLink(GateTypesIF.GT_SUB, 0).getWeight());
            		manipulator.setGateActivation(YGOAL, structure.findEntity(goalID).getLink(GateTypesIF.GT_SUB, 1).getWeight());
            		manipulator.setGateActivation(NOPROTOCOL, 1.0);
            		newData = false;
            	} else {
            		//logger.info("target not reachable");
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
	                if(obstacle.getIncomingActivation() > 0.5) {
	                	newData = true;
	                }
	                return;
	            }
        	}
        	
        	//TODO competence, urgency, random choice between candidates
            if(currentMotive == AFFILIATIONURGE) {
                if(nearAgentsConcept != null) {
                	if(nearAgentsConcept.getGate(GateTypesIF.GT_SUB).getNumberOfLinks() == 0) {
                		System.out.println("3, 436 random");
                		manipulator.setGateActivation(RANDOM, 1.0);
                	} else {
                		double experience = 0;
                	    ConceptNode partner = null;
                		for(int i = 0; i < nearAgentsConcept.getGate(GateTypesIF.GT_SUB).getNumberOfLinks(); i++) {
                			experience = nearAgentsConcept.getLink(GateTypesIF.GT_SUB, i).getLinkedEntity().getLink(GateTypesIF.GT_SUB, 3).getWeight();
                			if(experience >= 0.0) {
                				partner = (ConceptNode)nearAgentsConcept.getLink(GateTypesIF.GT_SUB, i).getLinkedEntity();
                				break;
                			}
                		}
                		if(partner == null) {
                			System.out.println("4, 449 random");
                			manipulator.setGateActivation(RANDOM, 1.0);
                		} else {
                			//if((lastSmiledAgentID != partner.getID()) || (lastSmileStep + 40 < netstep)) {
	                			manipulator.setGateActivation(SMILE, 1.0);
	                			manipulator.setGateActivation(TARGETRED, partner.getLink(GateTypesIF.GT_SUB, 0).getWeight());
	                			manipulator.setGateActivation(TARGETGREEN, partner.getLink(GateTypesIF.GT_SUB, 1).getWeight());
	                			manipulator.setGateActivation(TARGETBLUE, partner.getLink(GateTypesIF.GT_SUB, 2).getWeight());
	                			double newExperience = partner.getLink(GateTypesIF.GT_SUB, 3).getWeight() + 0.1;
	                			if(newExperience > 1.0)
	                				newExperience = 1.0;
	                			structure.changeLinkParameters(partner.getLink(GateTypesIF.GT_SUB, 3), newExperience, partner.getLink(GateTypesIF.GT_SUB, 3).getConfidence());
	                			lastSmiledAgentID = partner.getID();
	                			lastSmileStep = netstep;
                			//} else
	                		//	manipulator.setGateActivation(SMILE, 0.0);
                		}
                	}
                }
                if(obstacle.getIncomingActivation() > 0.5) {
                	newData = true;
                }
                return;
            }
            
            /*
            if(currentMotive == EXPLORATIONURGE) {
            	manipulator.setGateActivation(RANDOM, 1.0);
            	return;
            }
            */
            
            /*
            ConceptNode currentMotiveConcept = getConcept(currentMotive);
            if(currentMotiveConcept == null || currentMotiveConcept.getFirstLinkAt(GateTypesIF.GT_SUB) == null) {
                // no planning possible due to lack of information
                manipulator.setGateActivation(RANDOM, 1.0);
                return;
            }
            */
            double value = -1;
            
            long time3 = System.currentTimeMillis();
           	value = createPlan(currentPosition, currentMotive);
            if(value == -1) {
            	//TODO find appropiate goal
            	//way = navigate(currentPosition, NodeFunctions.getPosition(getConcept(currentMotive).getFirstLinkAt(GateTypesIF.GT_SUB).getLinkedEntity()));
            	//if(way != null) {
            	//	navigating = true;
            	//	navigationIndex = 0;
            	//} else {
            		currentMotive = EXPLORATIONURGE;
            		value = createPlan(currentPosition, currentMotive);
            	//}
            }
            if((System.currentTimeMillis() - time3) > 50)
            	System.out.println("planning took " + (System.currentTimeMillis() - time3) + "ms");
            
            goalID = getGoalID(currentMotive);
            if(goalID == null) {
            	//logger.info("no reachable waypoint found");
            	//System.out.println("5, 520 random");
                manipulator.setGateActivation(RANDOM, 1.0);
                clear();
                return;
            } else {
            	deletePlanGoal();
            	if(structure.findEntity(goalID) != null) {
	            	structure.createLink(currentGoal.getID(), GateTypesIF.GT_SUB, goalID, SlotTypesIF.ST_GEN, 1.0, 1.0);
	                manipulator.setGateActivation(XGOAL, structure.findEntity(goalID).getLink(GateTypesIF.GT_SUB, 0).getWeight());
	                manipulator.setGateActivation(YGOAL, structure.findEntity(goalID).getLink(GateTypesIF.GT_SUB, 1).getWeight());
            	} else {
            		System.err.println("error with goal");
            	}
            }
        }
        manipulator.setGateActivation(ONTHEWAY, onTheWay ? 1.0 : 0.0);
        if(obstacle.getIncomingActivation() > 0.5) {
        	newData = true;
        }
    }

    private ConceptNode getConcept(int motive) {
        switch(motive) {
        	case FOODURGE: return foodConcept;
        	case WATERURGE: return waterConcept;
        	case HEALINGURGE: return healingConcept;
        	case EXPLORATIONURGE: return unknownRegionsConcept;
        	default: return null;
        }
    }
    
    private String getGoalID(int motive) {
        switch(motive) {
        	case FOODURGE: return foodGoal;
        	case WATERURGE: return waterGoal;
        	case HEALINGURGE: return healingGoal;
        	case EXPLORATIONURGE: if(oldGoalID == null)
        							return explorationGoal;
        						  else
        							return oldGoalID;
        	default: return null;
        }
    }
    
    private double getCompetence(int motive) {
        switch(motive) {
        	case FOODURGE: return foodCompetence;
        	case WATERURGE: return waterCompetence;
        	case HEALINGURGE: return healingCompetence;
        	case AFFILIATIONURGE: return affiliationCompetence;
        	default: return 0.5;
        }
    }
    
    private void decreaseCompetence(int motive) {
        switch(motive) {
        	case FOODURGE: if(foodCompetence > 0.0)
        				   	   foodCompetence -= 0.01;
        				   break;	
        	case WATERURGE: if(waterCompetence > 0.0)
        						waterCompetence -= 0.01;
        	 				break;
        	case HEALINGURGE: if(healingCompetence > 0.0)
        					      healingCompetence -= 0.1;
        					  break;
        	case AFFILIATIONURGE: if(affiliationCompetence > 0.0)
        							affiliationCompetence -= 0.1;
        						  break;
        }
    }
    
    private void increaseCompetence(int motive) {
        switch(motive) {
        	case FOODURGE: foodCompetence = 1.0;
        				   break;	
        	case WATERURGE: waterCompetence = 1.0;
        	 				break;
        	case HEALINGURGE: healingCompetence = 1.0;
        					  break;
        	case AFFILIATIONURGE: affiliationCompetence = 0.75;
        						  break;
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
    
    /**
     * calculats if the position is reachable based on known obstacles and
     * damaging regions
     * 
     * @param currentPos describes the current position
     * @param goalPos desribes the goal position
     * @return true, if goal position is reachable from the current position
     */
    private boolean isReachable(Position currentPos, Position goalPos) {
    	long time = System.currentTimeMillis();
    	if(currentPos == null || goalPos == null)
    		return false;
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
    				
    				/*
    				if(testPosition.distance2D(goalPos) <= ConstantValues.NODEMINDISTANCE / 4.0) {//TODO test this
    					if(System.currentTimeMillis() - time > 20) {
    			    		System.out.println("reach test took " + (System.currentTimeMillis() - time) + "ms");
    			    	}
    					return false;				
    				}
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
    						if(System.currentTimeMillis() - time > 20) {
    				    		System.out.println("reach test took " + (System.currentTimeMillis() - time) + "ms");
    				    	}
    						return false;
    					}
    					normalVector.scaleBy(-1.0);
    					if(Math.abs(scalarProduct(normalVector, testVector)) <= ConstantValues.NODEMINDISTANCE * 3) {
    						if(System.currentTimeMillis() - time > 20) {
    				    		System.out.println("reach test took " + (System.currentTimeMillis() - time) + "ms");
    				    	}
    						return false;
    					}
    				}
    				*/
    				if(isObstacle(currentPos, goalPos, testPosition)) {
    					return false;
    				}
    			}
    		}
    	} else {
    		//System.out.println("no obstacles");
    	}
    	if(System.currentTimeMillis() - time > 20) {
    		System.out.println("reach test took " + (System.currentTimeMillis() - time) + "ms");
    	}
    	return true;
    }
    
    
    private boolean isObstacle(Position start, Position goal, Position potentialObstacle, WorldVector goalVector) {
    	if(goalVector == null)
    		goalVector =  new WorldVector(goal.getX() - start.getX(), goal.getY() - start.getY(), 0.0);
    	
		// obstacle belongs to way
		if(potentialObstacle.distance2D(goal) <= ConstantValues.NODEMINDISTANCE / 2.0) //TODO test this
			return false;
		WorldVector testVector = new WorldVector(potentialObstacle.getX() - start.getX(), potentialObstacle.getY() - start.getY(), 0.0);
		double angle = Math.abs(goalVector.getAngle() - testVector.getAngle());
    	if(angle > 180.0)
    		angle -= 180.0;
    	if(angle > 90.0)
    		return false;
    	WorldVector normGoalVector = new WorldVector(goalVector);
    	normGoalVector.setLength(1.0);
		if(scalarProduct(normGoalVector, testVector) > goalVector.getLength())
			return false;
		else {
			WorldVector normalVector = new WorldVector(goalVector.getX(), goalVector.getY(), 0.0);
			normalVector.rotate(90.0);
			normalVector.setLength(1.0);
			if(Math.abs(scalarProduct(normalVector, testVector)) <= ConstantValues.NODEMINDISTANCE * 2) {
				//System.out.println("obstacle is (" + testPosition.getX() + "," + testPosition.getY() + ")");
				return true;
			}
			normalVector.scaleBy(-1.0);
			if(Math.abs(scalarProduct(normalVector, testVector)) <= ConstantValues.NODEMINDISTANCE * 2) {
				return true;
			}
		}
		return false;
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
    
    /**
     * clears global variables
     */
    private void clear() {
    	goalID = null;
    	oldGoalID = null; // TODO new
    	foodGoal = null;
    	waterGoal = null;
    	healingGoal = null;
    	explorationGoal = null;
        onTheWay = false;
        newData = true;
        navigating = false;
        way = null;
        navigationIndex = 0;
    }
    
    /**
     * deletes the link between the current-goal-concept and the
     * first connected node
     * there should only one linked node to the concept at all times
     */
    private void deletePlanGoal() throws NetIntegrityException {
    	while(currentGoal.getFirstLinkAt(GateTypesIF.GT_SUB) != null) {
    		structure.deleteLink(currentGoal.getID(), GateTypesIF.GT_SUB, currentGoal.getFirstLinkAt(GateTypesIF.GT_SUB).getLinkedEntityID(), SlotTypesIF.ST_GEN);
    	}
    }
    
    /**
     * resets the module to starting values
     */
    private void resetModule() throws NetIntegrityException {
    	clear();
    	createMaze();
    }
    
    /**
     * creates a plan for @param type and saves the goal in the corresponding Variable
     * @return a value to assess the plan
     * @throws NetIntegrityException 
     */
    private double createPlan(Position currentPosition, int type) throws NetIntegrityException {
    	
    	if(type == EXPLORATIONURGE && !regionsProtocolled) {
    		explorationGoal = null;
    		return -1;
    	}
    	double value = -2;
    	String ID = null;
    	
    	ConceptNode currentMotiveConcept = getConcept(type);
        if(currentMotiveConcept == null || currentMotiveConcept.getFirstLinkAt(GateTypesIF.GT_SUB) == null) {
            // no planning possible due to lack of information
        	//System.out.println("no planning");
            return -1;
        }
        
        if(getGoalID(type) != null) {
        	Position temp = NodeFunctions.getPosition(structure.findEntity(getGoalID(type)));
        	if(isReachable(currentPosition, temp)) {
        		return 1.0 - (currentPosition.distance2D(temp) / Math.sqrt(2 * ConstantValues.WORLDMAXX * ConstantValues.WORLDMAXX));
        	}
        }
        
        // significant change in position to justify new planning
        if(currentPosition.distance2D(getLastPosition(type)) > ConstantValues.STEPLENGTH) {
            int numberOfNodes = currentMotiveConcept.getGate(GateTypesIF.GT_SUB).getNumberOfLinks();
            double minDistance = 10000.0;
            double currentDistance = 10000.0;
            //System.out.println("number of possible targets: " + numberOfNodes);
            for(int i = 0; i < numberOfNodes; i++) {
                Link testLink = currentMotiveConcept.getLink(GateTypesIF.GT_SUB, i);
                if(testLink != null) { 
                    Position testPosition = NodeFunctions.getPosition(testLink.getLinkedEntity());
                    currentDistance = currentPosition.distance2D(testPosition);
                    if(type == EXPLORATIONURGE && settedStartPosition) {
                    	currentDistance += startPosition.distance2D(testPosition);
                    }
                    if(currentDistance < minDistance && isReachable(currentPosition, testPosition)) {
                        minDistance = currentPosition.distance2D(testPosition);
                        ID = ((ConceptNode)testLink.getLinkedEntity()).getID();
                        value = 1.0 - (minDistance / Math.sqrt(2 * ConstantValues.WORLDMAXX * ConstantValues.WORLDMAXX));
                        //System.out.println("new value " + value);
                    }
                }
            }
            setLastPosition(type, currentPosition);
        }
        
        if(ID == null) {
        	//logger.info("no reachable waypoint found");
            return -1;
        } else {
        	switch(type) {
        		case FOODURGE: foodGoal = ID;
        					   break;
        		case WATERURGE: waterGoal = ID;
        						break;
        		case HEALINGURGE: healingGoal = ID;
        					      break;
        		case EXPLORATIONURGE: explorationGoal = ID;
        							  oldGoalID = null; //TODO new
        							 break;
        		default: return -1;
        	}
        }
        /*
        if(type != EXPLORATIONURGE) {
	        int numberOfNodes = unknownRegionsConcept.getGate(GateTypesIF.GT_SUB).getNumberOfLinks();
	        Position goal = NodeFunctions.getPosition(structure.findEntity(ID));
	        WorldVector goalVector = new WorldVector(goal.getX() - currentPosition.getX(), goal.getY() - currentPosition.getY());
	        
	        for(int i = 0; i < numberOfNodes; i++) {
	            Link testLink = unknownRegionsConcept.getLink(GateTypesIF.GT_SUB, i);
	            if(testLink != null) { 
	                Position testPosition = NodeFunctions.getPosition(testLink.getLinkedEntity());
	                if(isObstacle(currentPosition, goal, testPosition, goalVector)) {
	                    value -= 0.1;
	                }
	            }
	        }
        }
        */
        
        return value;
        
        //TODO remember setting lastPosition after planning
    }
    
    private Position getLastPosition(int type) {
    	switch(type) {
    		case FOODURGE: return lastPlanPositionFood;
    		case WATERURGE: return lastPlanPositionWater;
    		case HEALINGURGE: return lastPlanPositionHealing;
    		case EXPLORATIONURGE: return lastPlanPositionExploration;
    		default: return null;
    	}
    }
    
    private void setLastPosition(int type, Position position) {
    	switch(type) {
			case FOODURGE: lastPlanPositionFood = position;
					   	   break;
			case WATERURGE: lastPlanPositionWater = position;
							break;
			case HEALINGURGE: lastPlanPositionHealing = position;
						  	  break;
			case EXPLORATIONURGE: lastPlanPositionExploration = position;
								  break;
    	}
    }
    
    /**
     * creates an array of positions that leads from starting position
     * to goal position by using the map that is derived from region informations
     * @param startPosition
     * @param goalPosition
     * @return array of position that describes an optimal way from start to goal
     */
    private Position[] navigate(Position startPosition, Position goalPosition) throws NetIntegrityException {
    	long time1 = System.currentTimeMillis(); 
    	if(!regionsProtocolled || regionsConcept.getFirstLinkAt(GateTypesIF.GT_SUB) == null)
    		return null;
    	
    	int[][] maze = new int[(int)ConstantValues.WORLDMAXX][(int)ConstantValues.WORLDMAXY];
    	for(int i = 0; i < ConstantValues.WORLDMAXX; i++) {
    		for(int j = 0; j < ConstantValues.WORLDMAXY; j++) {
    			maze[i][j] = -1;
    		}
    	}
    	
    	ArrayList<Position> nodes = new ArrayList<Position>();
    	
    	int goalX = (int)goalPosition.getX();
    	int goalY = (int)goalPosition.getY();
    	
    	int startX = (int)startPosition.getX();
    	int startY = (int)startPosition.getY();
    	
    	maze[goalX][goalY] = 0;
    	nodes.add(new Position(goalX, goalY));
    	
    	boolean startReached = false;
    	
    	while(!startReached && nodes.size() != 0) {
    		ArrayList<Position> newNodes = new ArrayList<Position>();
    		newNodes.clear();
    		
    		Position currentNode;
    		Position testPosition;
    		int mazeValue;
    		int x = 0;
    		int y = 0;
    		for(int i = 0; i < nodes.size(); i++) {
    			currentNode = nodes.get(i);
    			mazeValue = maze[(int)currentNode.getX()][(int)currentNode.getY()];
    			
    			for(int j = 0; j < 4; j++) {
    				switch(j) {
    					case 0: 
    						x = (int)currentNode.getX();
    		    			y = (int)currentNode.getY() - 1;
    		    			break;
    					case 1:
    						x = (int)currentNode.getX();
    		    			y = (int)currentNode.getY() + 1;
    		    			break;
    					case 2:
    						x = (int)currentNode.getX() - 1;
    		    			y = (int)currentNode.getY();
    		    			break;
    					case 3:
    						x = (int)currentNode.getX() + 1;
    		    			y = (int)currentNode.getY();
    		    			break;
    				}
    				if(x < 0 || x >= 100 || y < 0 || y >= 100)
        				continue;
    				
    				if(maze[x][y] != - 1)
    					continue;
    				
    				if(knownMaze[x][y] != 1)
    					continue;
    				
        			testPosition = new Position(x,y);
        			
    				/*      			       			
        			boolean wall = false;
        			for(int k = 0; k < obstacleConcept.getGate(GateTypesIF.GT_SUB).getNumberOfLinks(); k++) {
        				Position obstacle = NodeFunctions.getPosition(obstacleConcept.getLink(GateTypesIF.GT_SUB, k).getLinkedEntity());
        				if(testPosition.distance2D(obstacle) < 1.0) {
        					wall = true;
        					break;
        				}
        			}
        			if(wall)
        				continue;
        			
        			
        			Region nearest = getNearestRegion(testPosition, null);
        			if(nearest.isUnknown())
        				continue;
        			*/
        			
        			maze[x][y] = mazeValue + 1;
        			if(x == startX && y == startY) {
        				startReached = true;
        			}
        			newNodes.add(testPosition);
    			}
    		}
    		nodes.clear();
    		nodes = newNodes;
    	}
    	
    	if(!startReached)
    		return null;
    	System.out.println("1: " + (System.currentTimeMillis() - time1));
    	
    	time1 = System.currentTimeMillis();
    	
    	// Weg suchen
    	boolean reached = false;
    	Position currentNode = new Position(startX, startY);
    	ArrayList<Position> way = new ArrayList<Position>();
    	int x = 0;
    	int y = 0;
    	int nextX = 0;
    	int nextY = 0;
    	int nextValue;
    	int wayCounter = 0;
    	while(!reached) {
    		nextValue = (int)(ConstantValues.WORLDMAXX * ConstantValues.WORLDMAXY);
    		for(int j = 0; j < 4; j++) {
				switch(j) {
					case 0: 
						x = (int)currentNode.getX();
		    			y = (int)currentNode.getY() - 1;
		    			break;
					case 1:
						x = (int)currentNode.getX();
		    			y = (int)currentNode.getY() + 1;
		    			break;
					case 2:
						x = (int)currentNode.getX() - 1;
		    			y = (int)currentNode.getY();
		    			break;
					case 3:
						x = (int)currentNode.getX() + 1;
		    			y = (int)currentNode.getY();
		    			break;
				}
				if(x < 0 || x >= 100 || y < 0 || y >= 100)
    				continue;
				
				if(maze[x][y] >= 0 && maze[x][y] < nextValue) {
					nextValue = maze[x][y];
					nextX = x;
					nextY = y;
				}
    		}
    		currentNode = new Position(nextX, nextY);
    		way.add(wayCounter++, currentNode);
    		
    		if(currentNode.getX() == goalX && currentNode.getY() == goalY)
    			reached = true;
    	}
    	
    	int arrayStartCounter = 0;
    	int arrayEndCounter = 2;
    	ArrayList<Position> optimalWay = new ArrayList<Position>();
    	while(arrayStartCounter < way.size() && arrayEndCounter < way.size()) {
    		if(pointOnLine(way.get(arrayStartCounter), way.get(arrayEndCounter), way.get(arrayEndCounter - 1))) {
    			arrayEndCounter = arrayEndCounter + 1;
    			//TODO continue this
    		} else {
    			optimalWay.add(way.get(arrayStartCounter));
    			arrayStartCounter = arrayEndCounter - 1;
    			arrayEndCounter = arrayEndCounter + 1;
    		}
    	}
    	optimalWay.add(way.get(arrayStartCounter));
    	optimalWay.add(way.get(way.size() - 1));
    	
    	Position[] posArray = new Position[optimalWay.size()];
    	for(int i = 0; i < optimalWay.size(); i++)
    		posArray[i] = optimalWay.get(i);
    	System.out.println("2: " + (System.currentTimeMillis() - time1));
    	
    	return posArray;
    }
    
    /**
     * calculates if a given point is on a line between start and end
     * @param startLine
     * @param endLine
     * @param point
     * @return
     */
    private boolean pointOnLine(Position startLine, Position endLine, Position point) {
    	double right = (point.getX() - startLine.getX()) * (endLine.getY() - startLine.getY()); 
    	double left = (point.getY() - startLine.getY()) * (endLine.getX() - startLine.getX());
    	
    	if(left == right)
    		return true;
    	else
    		return false;
    }
    
    /**
     * searches the region information for the nearest region to a given
     * position
     * @param position
     * @param ignore describes a region that is ignored when searching for
     * the nearest region, set to null if no region should be ignored
     * @return the nearest region to position
     */
    private Region getNearestRegion(Position position, Region ignore) {
    	Region toReturn = null;
    	double distance = 1000.0;
    	
    	
    	for(Iterator<Link> links = regionsConcept.getGate(GateTypesIF.GT_SUB).getLinks(); links.hasNext();) {
    		Link link = links.next();
    		Position pos;
    		try {
    			pos = NodeFunctions.getPosition(link.getLinkedEntity());
    		} catch(NetIntegrityException e) {
    			return null;
    		}
    		if(ignore == null || !(pos.distance2D(ignore.position) < 0.01)) {
    			double compareDistance = position.distance2D(pos);
	    		if(compareDistance < distance) {
	    			toReturn = getRegionFromNode(link.getLinkedEntityID());
	    			distance = compareDistance;
	    		}
    		}
    	}
    	
    	return toReturn;
    }
    
    /**
     * creates an instance of Region filled from region-node information
     * saved in nodeID
     * @param nodeID ID of the region node
     * @return instance of Region
     */
    private Region getRegionFromNode(String nodeID) {
    	NetEntity regionNode = structure.findEntity(nodeID);
    	Region toReturn = new Region(NodeFunctions.getPosition(regionNode));
    	if(regionNode.getLink(GateTypesIF.GT_SUB, 2).getWeight() > 0.5)
    		toReturn.known();
    	if(regionNode.getGate(GateTypesIF.GT_SUB).getNumberOfLinks() >= 8) {
	    	for(int i = 3; i < 8; i++) {
	    		if(regionNode.getLink(GateTypesIF.GT_SUB, i).getWeight() > 0.5) {
	    			toReturn.addType(i - 3);
	    		}
	    	}
    	}
    	toReturn.setNode((ConceptNode)structure.findEntity(nodeID));
    	
    	return toReturn;
    }
    
    /**
     * fills the map array around given position
     * with informations from regions and obstacles
     * @param current
     */
    private void createMaze(Position current) throws NetIntegrityException {
    	if(current == null)
    		return;
    	for(int  i = ((int)current.getX() - 2); i < ((int)current.getX() + 2); i++) {
			for(int j = ((int)current.getY() - 2); j < ((int)current.getY() + 2); j++) {
				if(i < 0 || i >= 100 || j < 0 || j >= 100)
    				continue;
				if(knownMaze[i][j] != -1)
					continue;
				
				Position testPosition = new Position(i, j);
       			
				if(numberOfObstacles < obstacleConcept.getGate(GateTypesIF.GT_SUB).getNumberOfLinks()) {
	    			numberOfObstacles = obstacleConcept.getGate(GateTypesIF.GT_SUB).getNumberOfLinks();
	    			Position obstacle = NodeFunctions.getPosition(obstacleConcept.getLastLinkAt(GateTypesIF.GT_SUB).getLinkedEntity());
	    			knownMaze[(int)obstacle.getX()][(int)obstacle.getY()] = 0;
				}
    			
    			Region nearest = getNearestRegion(testPosition, null);
    			if(nearest != null && !nearest.isUnknown()) {
    				knownMaze[i][j] = 1; // allowed
    				continue;				
    			}
			}
    	}
    }
    
    /**
     * initialize the map array knownMaze with informations from regions
     * and obstacles
     */
    private void createMaze() throws NetIntegrityException {
    	for(int  i = 0; i < (int)ConstantValues.WORLDMAXX; i++) {
			for(int j = 0; j < (int)ConstantValues.WORLDMAXY; j++) {
				if(knownMaze[i][j] != -1)
					continue;
				
				Position testPosition = new Position(i, j);
       			
    			boolean wall = false;
    			for(int k = 0; k < obstacleConcept.getGate(GateTypesIF.GT_SUB).getNumberOfLinks(); k++) {
    				Position obstacle = NodeFunctions.getPosition(obstacleConcept.getLink(GateTypesIF.GT_SUB, k).getLinkedEntity());
    				if(testPosition.distance2D(obstacle) < 1.0) {
    					wall = true;
    					break;
    				}
    			}
    			if(wall) {
    				knownMaze[i][j] = 0; // forbidden
    				continue;
    			}
    			
    			Region nearest = getNearestRegion(testPosition, null);
    			if(nearest != null && !nearest.isUnknown()) {
    				knownMaze[i][j] = 1; // allowed
    				continue;				
    			}
			}
    	}
    }
    
    private boolean isObstacle(Position start, Position goal, Position potentialObstacle) {
    	WorldVector goalVector = new WorldVector(goal.getX() - start.getX(), goal.getY() - start.getY(), goal.getZ() - start.getZ());
    	
    	if(potentialObstacle.distance2D(goal) <= 0.5) //TODO test this
			return false;
		WorldVector testVector = new WorldVector(potentialObstacle.getX() - start.getX(), potentialObstacle.getY() - start.getY(), potentialObstacle.getZ() - start.getZ());
		double angle = Math.abs(goalVector.getAngle() - testVector.getAngle());
    	if(angle > 180.0)
    		angle -= 180.0;
    	if(angle > 90.0)
    		return false;
    	
    	if(goalVector.getLength() > 0.0)
    		goalVector.setLength(1.0);
    	else
    		return false;
    	
    	WorldVector vector = new WorldVector(potentialObstacle.getX() - start.getX(), potentialObstacle.getY() - start.getY());
    	WorldVector cross = cross(vector, goalVector);
    	
    	double distance = cross.getLength();
    	
    	if(distance <= ConstantValues.NODEMINDISTANCE) {
    		return true;
    	} else
    		return false;
    }
    
    private WorldVector cross(WorldVector a, WorldVector b) {
    	return new WorldVector(
    				a.getY()*b.getZ() - a.getZ()*b.getY(),
    				a.getZ()*b.getX() - a.getX()*b.getZ(),
    				a.getX()*b.getY() - a.getY()*b.getX()
    						   );
    }
}
