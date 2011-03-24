/*
 * Created on 29.04.2005
 *
 */
package org.micropsi.nodenet.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.micropsi.common.coordinates.Position;
import org.micropsi.common.coordinates.WorldVector;
import org.micropsi.comp.ConstantValues;
import org.micropsi.comp.NodeFunctions;
import org.micropsi.comp.agent.voronoi.PlanningTask;
import org.micropsi.comp.agent.voronoi.Region;
import org.micropsi.comp.agent.voronoi.Tile;
import org.micropsi.comp.agent.voronoi.Waypoint;
import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.ConceptNode;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Node;
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
public class HierarchicalRegionPlanningModule extends AbstractNativeModuleImpl {
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
	private static final int HIERARCHYNODES    = 16520;
	
	// competence
	private static final int FOODCOMPETENCE    		= 16521;
	private static final int WATERCOMPETENCE   		= 16522;
	private static final int HEALINGCOMPETENCE 		= 16523;
	private static final int EXPLORATIONCOMPETENCE  = 16524;
	private static final int SOCIALCOMPETENCE		= 16525;
	
	// actuator
	private static final int ACT_SUB = 16526;
	private static final int ACT_SUR = 16527;
	private static final int ACT_POR = 16528;
	
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
	//private ConceptNode currentGoal = null;
	private ConceptNode nearAgentsConcept = null; 
	private ConceptNode regionsConcept = null;
	private ConceptNode hierarchyRegionsConcept = null;
	private ConceptNode unknownRegionsConcept = null;
	
	private boolean firsttime = true;
	private int currentMotive = 0;
	private String goalID = null;
	private String oldGoalID = null;
	
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
	private int oldState = 0;
	
	private Position lastPlanPositionFood = null;
	private Position lastPlanPositionWater = null;
	private Position lastPlanPositionHealing = null;
	private Position lastPlanPositionExploration = null;
	private int planCounter = 0;
	
	private double explorationStrength = ConstantValues.EXPLORATIONSTRENGTH;
	
	private Position startPosition = null;
	private boolean settedStartPosition = false;
	private int numberOfRegions = 0;
	
	// navigate
	private boolean navigating = false;
	private Waypoint way = null;
	private List<Region> foodRegions;
	private List<Region> waterRegions;
	private List<Region> healingRegions;
	private int pc = 0;
	private final int max_steps = 8;
	private final int delay = 2;
	private Region start = null;
	private Region goal = null;
	
	private int state = 0;
	private static final int firing = 1;
	private static final int searching = 2;
	private static final int planning = 3;
	
	// planning with regions
	private PlanningTask currentTask = null;
	private int planPC = 0;
	private Stack<PlanningTask> tasks = null;
	private ArrayList<NetEntity> startPoints = null;
	private ArrayList<NetEntity> endPoints = null;
	private HashMap<String, NetEntity> startChildren = null;
	
	//debug
	protected int cycle = 0;
	
	public HierarchicalRegionPlanningModule() {	
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
			    	case HIERARCHYNODES: return "hierarchyRegions";
			    	case FOODCOMPETENCE: return "food-competence";
			    	case WATERCOMPETENCE: return "water-competence";
			    	case HEALINGCOMPETENCE: return "healing-competence";
			    	case EXPLORATIONCOMPETENCE: return "exploration-competence";
			    	case ACT_SUB: return "act_sub";
			    	case ACT_SUR: return "act_sur";
			    	case ACT_POR: return "act_por";
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
			HIERARCHYNODES,
			FOODCOMPETENCE,
	        WATERCOMPETENCE,
	        HEALINGCOMPETENCE,
	        EXPLORATIONCOMPETENCE,
	        ACT_SUB,
	        ACT_SUR,
	        ACT_POR
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
				logger.warn("planning has no link to foodconcept");
			else
				foodConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(WATERCONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("planning has no link to waterconcept");
			else
				waterConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(HEALINGCONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("planning has no link to healingconcept");
			else
				healingConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(HURTCONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("planning has no link to damageconcept");
			else
				damageConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(OBSTACLECONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("planning has no link to obstacleconcept");
			else
				obstacleConcept = (ConceptNode)l.getLinkedEntity();
			
			/*
			l = manipulator.getGate(CURRENTGOAL).getLinkAt(0);
			if(l == null)
				logger.warn("planning has no link to goalconcept");
			else
				currentGoal = (ConceptNode)l.getLinkedEntity();
			*/
			l = manipulator.getGate(NEARAGENTSCONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("planning has no link to nearagentsconcept");
			else
				nearAgentsConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(REGIONNODE).getLinkAt(0);
			if(l == null)
				logger.warn("planning has no link to regionsconcept");
			else
				regionsConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(UNKNOWNREGIONNODE).getLinkAt(0);
			if(l == null)
				logger.warn("planning has no link to unknownregionsconcept");
			else
				unknownRegionsConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(HIERARCHYNODES).getLinkAt(0);
			if(l == null)
				logger.warn("planning has no link to hierachyregionsconcept");
			else
				hierarchyRegionsConcept = (ConceptNode)l.getLinkedEntity();
			
			Position lastPosition = NodeFunctions.getPosition(x.getIncomingActivation(), y.getIncomingActivation());
			setLastPosition(FOODURGE, lastPosition);
			setLastPosition(WATERURGE, lastPosition);
			setLastPosition(HEALINGURGE, lastPosition);
			setLastPosition(EXPLORATIONURGE, lastPosition);
			
			foodRegions = new ArrayList<Region>();
			waterRegions = new ArrayList<Region>();
			healingRegions = new ArrayList<Region>();
			
			startPoints = new ArrayList<NetEntity>();
			endPoints = new ArrayList<NetEntity>();
			startChildren = new HashMap<String, NetEntity>();
			
			tasks = new Stack<PlanningTask>();
			
			if(regionsConcept != null && regionsConcept.getFirstLinkAt(GateTypesIF.GT_CAT) != null) {
				for(int i = 0; i < regionsConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
					Region temp = getRegionFromNode(regionsConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntityID());
					if(temp.isType(Region.FOOD))
						foodRegions.add(temp);
					if(temp.isType(Region.WATER))
						waterRegions.add(temp);
					if(temp.isType(Region.HEALING))
						healingRegions.add(temp);
				}
				numberOfRegions = regionsConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks();
			}
		}
        
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
        
        if(oldState != state) {
        	System.out.println("state: " + state);
        	oldState = state;
        }
        
        if(regionsConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks() > numberOfRegions) {
        	for(int i = numberOfRegions - 1; i < regionsConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
				Region temp = getRegionFromNode(regionsConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntityID());
				if(temp.isType(Region.FOOD))
					foodRegions.add(temp);
				if(temp.isType(Region.WATER))
					waterRegions.add(temp);
				if(temp.isType(Region.HEALING))
					healingRegions.add(temp);
			}
			numberOfRegions = regionsConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks();
        }
        
        Position currentPosition = NodeFunctions.getPosition(x.getIncomingActivation(), y.getIncomingActivation());
        manipulator.setGateActivation(RANDOM, 0.0);
        
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
            resetPlanning();
            start = getNearestRegion(currentPosition);
        } else {
            if(oldMotiveStrength + ConstantValues.STUBBORNESS < newMotiveStrength && newData) {
            	//System.out.println("" + getNetProperties().getProperty("agentName") +  " new Motive: " + newMotive + " with strength " +newMotiveStrength);
                currentMotive = newMotive;              
                clear();
                resetPlanning();
                start = getNearestRegion(currentPosition);
                newData = false;
                //TODO if new Motive cannot be fulfilled return to old one without deleting goal
            } else if(newData) {
            	clear();
            }
        }
        
        if(way != null) {
        	navigating = true;
        } else {
        	navigating = false;
        }
        
        //TODO seperate navigating and planning, only test for way?
        if(navigating && way != null) {
        	if(currentPosition.distance2D(way.getPosition()) < 0.5) {
        		if(way.next != null) {
        			way = way.delete();
        		} else {
        			//TODO end of way
        			if(currentPosition.distance2D(goal.position) < 0.25) {
        				navigating = false;
        				if(way != null) {
        		        	way.deleteAll();
        		        	way = null;
        		        	resetPlanning();
        		        }
        			}
        		}
        	}else {
        		manipulator.setGateActivation(XGOAL, way.getPosition().getX() / ConstantValues.WORLDMAXX);
	            manipulator.setGateActivation(YGOAL, way.getPosition().getY() / ConstantValues.WORLDMAXY);
        	}
        } 
        if(true) {
        	if(currentMotive == EXPLORATIONURGE) {
        		if(goalID != null) {
                	decreaseCompetence(currentMotive);
                    NetEntity goal = structure.findEntity(goalID);
                    if(goal == null) {
                    	System.err.println("goalID has no node");
                    	clear();
                    	return;
                    }
                    if(currentPosition.distance2D(NodeFunctions.getPosition(goal)) < ConstantValues.STEPLENGTH) {
                    	manipulator.setGateActivation(NOPROTOCOL, 1.0);
                    	increaseCompetence(currentMotive);
                        clear();
                    } else if (!onTheWay) {
                    	if(!newData || isReachable(currentPosition, NodeFunctions.getPosition(goal))) {
                    		manipulator.setGateActivation(XGOAL, structure.findEntity(goalID).getLink(GateTypesIF.GT_SUB, 0).getWeight());
                    		manipulator.setGateActivation(YGOAL, structure.findEntity(goalID).getLink(GateTypesIF.GT_SUB, 1).getWeight());
                    		manipulator.setGateActivation(NOPROTOCOL, 1.0);
                    		newData = false;
                    	} else {
                    		clear();
                    		//deletePlanGoal();
                    		System.out.println("4, 527 random");
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
                    double value = -1;
                    
                    value = createPlan(currentPosition, currentMotive);
                    
                    goalID = getGoalID(currentMotive);
                    if(goalID == null) {
                    	//logger.info("no reachable waypoint found");
                    	//System.out.println("5, 589 random");
                        manipulator.setGateActivation(RANDOM, 1.0);
                        clear();
                        //resetModule();
                        return;
                    } else {
                    	//deletePlanGoal();
                    	if(structure.findEntity(goalID) != null) {
        	            	//structure.createLink(currentGoal.getID(), GateTypesIF.GT_SUB, goalID, SlotTypesIF.ST_GEN, 1.0, 1.0);
        	                manipulator.setGateActivation(XGOAL, structure.findEntity(goalID).getLink(GateTypesIF.GT_SUB, 0).getWeight());
        	                manipulator.setGateActivation(YGOAL, structure.findEntity(goalID).getLink(GateTypesIF.GT_SUB, 1).getWeight());
                    	} else {
                    		System.err.println("error with goal");
                    	}
                    }
                }
                manipulator.setGateActivation(ONTHEWAY, onTheWay ? 1.0 : 0.0);                
        	} else if(way == null || state != 0) {
        		if(start == null) {
    				start = getNearestRegion(currentPosition);
    			}
        		
        		if(state == 0 && tasks.empty()) {
        			start = getNearestRegion(currentPosition);
        			state = firing;
        		}
	        	int type = identifyMotiveWithType(currentMotive);
	        	
	        	if(state == firing) {
	        		List<Region> regions = getRegionList(type);
	        		if(regions != null) {
	        			for(int i = 0; i < hierarchyRegionsConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
	        				NetEntity tempRegion = hierarchyRegionsConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity();
	        				double genActivation = tempRegion.getGate(GateTypesIF.GT_GEN).getConfirmedActivation();
	        				if(genActivation == 0.5) {
	        					NetEntity goalPath = null;
	        					boolean pathFound = false;
	        					for(int j = 0; j < tempRegion.getGate(GateTypesIF.GT_SUB).getNumberOfLinks(); j++) {
	        						goalPath = tempRegion.getLink(GateTypesIF.GT_SUB, j).getLinkedEntity();
	        						if(goalPath.getGate(GateTypesIF.GT_GEN).getConfirmedActivation() > 0.0 &&
	        						   goalPath.getGate(GateTypesIF.GT_GEN).getConfirmedActivation() < 0.5) {
	        							System.out.println("path found");
	        							pathFound = true;
	        							break;
	        						}
	        					}
	        					if(pathFound) {
		        					boolean stop = false;
		        					while(!stop) {					
	        							for(Region region : regions) {
	        								if(region.getNode().getID().equals(goalPath.getID())) {
	        									stop = true;
	        									goal = region;				        							
			        							state = searching;
			    	        					pc = -delay;
			    	        					manipulator.setGateActivation(ACT_SUR, 0.0);
			    	        					System.out.println("goal found, end firing, start searching");
			    	    	        			System.out.println(NodeFunctions.positionToString(NodeFunctions.getPosition(start.getNode())) + " -> " + NodeFunctions.positionToString(NodeFunctions.getPosition(goal.getNode())));
	        									break;
	        								}
	        							}
		        						if(!stop) {
			        						for(int j = 0; j < goalPath.getGate(GateTypesIF.GT_SUB).getNumberOfLinks(); j++) {
			        							NetEntity temp = goalPath.getLink(GateTypesIF.GT_SUB, j).getLinkedEntity();
			        							if(temp.getGate(GateTypesIF.GT_GEN).getConfirmedActivation() > 0.0) {
			        								goalPath = temp;
			        								break;
			        							}
			        						}	
		        						}
		        					}
	        					} else {
	        						// no target found
	        						System.err.println("no goal found");
	        					}
	        				}
	        			}
	        			/*
	        			for(Region region : regions) {
	        				if(region.getNode().getGate(GateTypesIF.GT_GEN).getConfirmedActivation() > 0.0) {
	        					goal = region;
	        					state = searching;
	        					pc = -delay;
	        					manipulator.setGateActivation(ACT_SUB, 0.0);
	    	        			manipulator.setGateActivation(ACT_SUR, 0.0);
	    	        			System.out.println("goal found, end firing, start searching");
	    	        			System.out.println(NodeFunctions.positionToString(NodeFunctions.getPosition(start.getNode())) + " -> " + NodeFunctions.positionToString(NodeFunctions.getPosition(goal.getNode())));
	        					break;
	        				}
	        			}
	        			*/
	        		} else {
	        			state = 0;
	        			pc = -delay;
	        		}/*
	        		if(state == firing) {        			
	        			manipulator.setGateActivation(ACT_SUB, 1.0);
	        			manipulator.setGateActivation(ACT_SUR, 1.0);
	        			structure.activateNode(start.getNode().getID(), 1.0);
	        		}
	        		*/
	        		if(state == firing) {        			
	        			manipulator.setGateActivation(ACT_SUR, 1.0);
	        			structure.activateNode(start.getNode().getID(), 0.5);
	        			for(Region region : regions) {
	        				structure.activateNode(region.getNode().getID(), 0.5 / regions.size());
	        			}
	        		}
	        	} else if(state == searching) {
	        		System.out.println("searching ... " + pc);
	        		manipulator.setGateActivation(ACT_SUR, 0.95);
	        		structure.activateNode(start.getNode().getID(), 1.0);
	        		structure.activateNode(goal.getNode().getID(), 1.0);
	        		if(pc == max_steps + delay) {
	        			pc = -delay;
	        			System.out.println("calculating ...");
	        			double activation = 0;
	        			NetEntity fork = null;
	        			for(int i = 0; i < hierarchyRegionsConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
	        				NetEntity tempRegion = hierarchyRegionsConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity();
	        				double genActivation = 0.0;
	        				if(tempRegion.getGate(GateTypesIF.GT_GEN).getConfirmedActivation() > 0.0) {
	        					for(int j = 0; j < tempRegion.getGate(GateTypesIF.GT_SUB).getNumberOfLinks(); j++) {
	        						NetEntity tempChild = tempRegion.getLink(GateTypesIF.GT_SUB, j).getLinkedEntity();
	        						if(tempChild.getGate(GateTypesIF.GT_GEN).getConfirmedActivation() > 0.0) {
	        							genActivation += tempChild.getGate(GateTypesIF.GT_GEN).getConfirmedActivation();
	        						}
	        						if(genActivation > 1.0)
	        							break;
	        					}
	        				}
	        				if(genActivation > 1.0 && genActivation > activation) {
	        					fork = tempRegion;
	        					activation = genActivation;
	        				}
	        			}
	        			if(fork != null) {
	        				ArrayList<NetEntity> nodeArray = new ArrayList<NetEntity>();
	        				NetEntity next = start.getNode();
	        				startPoints.clear();
	        				endPoints.clear();
	        				startChildren.clear();
	        				for(int i = 0; i <= max_steps; i++) {
	        					nodeArray.add(next);
	        					next = next.getFirstLinkAt(GateTypesIF.GT_SUR).getLinkedEntity();
	        					if(!next.isActive() || next.getID().equals(fork.getID()))
	        						break;
	        				}
	        				for(int i = nodeArray.size() - 1; i >= 0; i--) {
	        					startPoints.add(nodeArray.get(i));
	        				}
	        				
	        				next = goal.getNode();
	        				nodeArray.clear();
	        				for(int i = 0; i <= max_steps; i++) {
	        					nodeArray.add(next);
	        					next = next.getFirstLinkAt(GateTypesIF.GT_SUR).getLinkedEntity();
	        					if(!next.isActive() || next.getID().equals(fork.getID()))
	        						break;
	        				}
	        				for(int i = nodeArray.size() - 1; i >= 0; i--) {
	        					endPoints.add(nodeArray.get(i));
	        				}
	        				if(endPoints.size() != startPoints.size())
	        					System.err.println("strange tree");
	        				
	        				for(int i = 0; i < startPoints.size(); i++)
	        					System.out.println("layer " + i + " : " + startPoints.get(i).getID() + " -> " + endPoints.get(i).getID());
	        				
	        				//PlanningTask firstTask = new PlanningTask(startPoints.get(0), endPoints.get(0), 0, startPoints.get(1));
	        				PlanningTask firstTask = new PlanningTask(fork, fork, -1);
	        				
	        				if(!tasks.empty()) {
	        					while(!tasks.empty()) {
	        						tasks.peek().clear();
	        						tasks.pop();
	        					}	        						
		        				tasks.clear();
		        				tasks = new Stack<PlanningTask>();
	        				}
	        				
	        				tasks.push(firstTask);
	        				state = planning;
	        				pc = -delay;
	        			} else {
	        				state = 0;
	        				if(!tasks.empty()) {
	        					while(!tasks.empty()) {
	        						tasks.peek().clear();
	        						tasks.pop();
	        					}	        						
		        				tasks.clear();
		        				tasks = new Stack<PlanningTask>();
	        				}
	        				pc = 0;
	        				System.out.println("no way found");
	        			}
	        		}
	        	} else if (state == planning) {
	        		if(!tasks.empty()) {
	        			regionPlanning(tasks.peek(), manipulator);
	        		} else if(way != null) {	        			
	        			start = null;
		        		state = 0;
		        		pc = 0;
	        		} else {
		        		System.out.println("reset search");
		        		resetPlanning();		        		
		        	}
	        	}
	        		        	
	        	//TODO organize pc	        	
	        	if(state != planning && pc > max_steps + delay) {
	        		pc = 0;
	        	}        	        	
	        	pc++;
        	}
        }
        
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
	    	numberOfNodes = damageConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks();
	    	for(i = 0; i < numberOfNodes; i++) {
	    		potentialObstacles.add(damageConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntityID());
	    		counter++;
	    	}
    	}
    	if(obstacleConcept != null) {
    		numberOfNodes = obstacleConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks();
    		for(i = 0; i < numberOfNodes; i++) {
	    		potentialObstacles.add(obstacleConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntityID());
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
    	foodGoal = null;
    	waterGoal = null;
    	healingGoal = null;
    	explorationGoal = null;
        onTheWay = false;
        newData = true;
    }
    
    /**
     * deletes the link between the current-goal-concept and the
     * first connected node
     * there should only one linked node to the concept at all times
     */
    /*
    private void deletePlanGoal() throws NetIntegrityException {
    	while(currentGoal.getFirstLinkAt(GateTypesIF.GT_SUB) != null) {
    		structure.deleteLink(currentGoal.getID(), GateTypesIF.GT_SUB, currentGoal.getFirstLinkAt(GateTypesIF.GT_SUB).getLinkedEntityID(), SlotTypesIF.ST_GEN);
    	}
    }
    */
    
    /**
     * resets the module to starting values
     */
    private void resetModule() throws NetIntegrityException {
    	clear();
    	resetPlanning();
    }
    
    /**
     * creates a plan for @param type and saves the goal in the corresponding Variable
     * @return a value to assess the plan
     * @throws NetIntegrityException 
     */
    private double createPlan(Position currentPosition, int type) throws NetIntegrityException {
    	double value = -2;
    	String ID = null;
    	
    	ConceptNode currentMotiveConcept = getConcept(type);
        if(currentMotiveConcept == null || currentMotiveConcept.getFirstLinkAt(GateTypesIF.GT_CAT) == null) {
            // no planning possible due to lack of information
        	// System.out.println("no planning");
            return -1;
        }
        
        if(getGoalID(type) != null) {
        	System.out.println("goalID not null");
        	Position temp = NodeFunctions.getPosition(structure.findEntity(getGoalID(type)));
        	if(isReachable(currentPosition, temp)) {
        		return 1.0 - (currentPosition.distance2D(temp) / Math.sqrt(2 * ConstantValues.WORLDMAXX * ConstantValues.WORLDMAXX));
        	} else {
        		System.out.println("not reachable");
        	}
        }
        
        // significant change in position to justify new planning
        if(currentPosition.distance2D(getLastPosition(type)) > ConstantValues.STEPLENGTH / 2 || planCounter > 40) {
            int numberOfNodes = currentMotiveConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks();
            double minDistance = 10000.0;
            double currentDistance = 10000.0;
            //System.out.println("number of possible targets: " + numberOfNodes);
            for(int i = 0; i < numberOfNodes; i++) {
                Link testLink = currentMotiveConcept.getLink(GateTypesIF.GT_CAT, i);
                if(testLink != null) { 
                    Position testPosition = NodeFunctions.getPosition(testLink.getLinkedEntity());
                    currentDistance = currentPosition.distance2D(testPosition);
                    if(type == EXPLORATIONURGE && settedStartPosition) {
                    	currentDistance += startPosition.distance2D(testPosition);
                    }
                    if(currentDistance < minDistance && isReachable(currentPosition, testPosition)) {
                        minDistance = currentPosition.distance2D(testPosition);
                        ID = testLink.getLinkedEntity().getID();
                        value = 1.0 - (minDistance / Math.sqrt(2 * ConstantValues.WORLDMAXX * ConstantValues.WORLDMAXX));
                        //System.out.println("new value " + value);
                    }
                }
            }
            setLastPosition(type, currentPosition);
            planCounter = 0;
        } else {
        	planCounter++;
        }
        
        if(ID == null) {
        	/*
        	if(currentPosition.distance2D(getLastPosition(type)) > 0)
        		System.out.println("distance: " + currentPosition.distance2D(getLastPosition(type)));
        	*/
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
    	if(!regionsProtocolled || regionsConcept.getFirstLinkAt(GateTypesIF.GT_CAT) == null)
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
    				/*
    				if(knownMaze[x][y] != 1)
    					continue;
    				*/
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
    	 	
    	for(Iterator<Link> links = regionsConcept.getGate(GateTypesIF.GT_CAT).getLinks(); links.hasNext();) {
    		Link link = links.next();
    		Position pos;
    		try {
    			pos = NodeFunctions.getPosition(link.getLinkedEntity());
    		} catch(NetIntegrityException e) {
    			return null;
    		}
    		if(pos == null) {
    			System.err.println("debug: pos in getNearestRegion is null");
    			continue;
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
    
    private Region getNearestRegion(Position position) {
    	return getNearestRegion(position, null);
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
    	toReturn.setNode(structure.findEntity(nodeID));
    	
    	return toReturn;
    }
    
    private List<Region> getRegionList(int type) {
    	switch(type) {
    		case Region.FOOD: return foodRegions;
    		case Region.WATER: return waterRegions;
    		case Region.HEALING: return healingRegions;
    		default: return null;
    	}
    }
    
    private int identifyTypeWithMotive(int type) {
    	switch(type) {
    		case Region.FOOD: return FOODURGE;
    		case Region.WATER: return WATERURGE;
    		case Region.HEALING: return HEALINGURGE;
    		default: return 0;
    	}
    }
    
    private int identifyMotiveWithType(int motive) {
    	switch(motive) {
    		case FOODURGE: return Region.FOOD;
    		case WATERURGE: return Region.WATER;
    		case HEALINGURGE: return Region.HEALING;
    		default: return 0;
    	}
    }
    
    private void regionPlanning(PlanningTask task, GateManipulator manipulator) throws NetIntegrityException {
    	if(task == null)
    		return;
    	
    	if(task == currentTask) {
    		if(task.isFinalLayer()) {
    			if(way == null) {
    				way = new Waypoint(NodeFunctions.getPosition(task.getGoal()));
    			} else {
    				way.add(new Waypoint(NodeFunctions.getPosition(task.getGoal())));
    			}
    			System.out.println("waypoint: " + NodeFunctions.positionToString(NodeFunctions.getPosition(task.getGoal())) + " remaining tasks: " + tasks.size());
    			if(task.getGoal().getLink(GateTypesIF.GT_SUB, 7).getWeight() > 0.5)
    				System.err.println(NodeFunctions.positionToString(NodeFunctions.getPosition(task.getGoal())) + " is impassable");
    			task.clear();
    			tasks.pop();
    			planPC = 0;
    			return;
    		}
    		
    		planPC++;
    		boolean complete = false;
    		if(task.getStartChild() == null) {
    			if(startChildren.containsKey(task.getStart().getID())) {
    				task.setStartChild(startChildren.get(task.getStart().getID()));
    			} else {
    				task.setStartChild(startPoints.get(task.getLayer() + 1));
    			}
    		}
    		if(task.getNextTask() == null && task.getGoal().getGate(GateTypesIF.GT_SUB).getLinkTo(endPoints.get(task.getLayer() + 1).getID(), SlotTypesIF.ST_GEN) != null) {
    			task.setWatchedNode(endPoints.get(task.getLayer() + 1));
    		}
			for(NetEntity watched : task.getWatchedNodes()) {
				if(watched.getGate(GateTypesIF.GT_POR).getConfirmedActivation() > 0.0) {
					String startID = task.getStartChild().getID();
					NetEntity waypoint = watched;
					System.out.println("from " + startID + " to " + waypoint.getID());
					ArrayList<NetEntity> way = new ArrayList<NetEntity>();
					way.add(waypoint);
					while(!waypoint.getID().equals(startID)) {
						double activation = 0.0;
						NetEntity nextWaypoint = null;
						for(int i = 0; i < waypoint.getGate(GateTypesIF.GT_RET).getNumberOfLinks(); i++) {
							NetEntity neighbour = waypoint.getLink(GateTypesIF.GT_RET, i).getLinkedEntity();
							if((neighbour.getGate(GateTypesIF.GT_POR).getConfirmedActivation() > activation) 
							   && (waypoint.getGate(GateTypesIF.GT_POR).getConfirmedActivation() < neighbour.getGate(GateTypesIF.GT_POR).getConfirmedActivation())) {
								
								if((neighbour.getFirstLinkAt(GateTypesIF.GT_SUB).getWeight() < 0.99) 
									&& (neighbour.getGate(GateTypesIF.GT_SUB).getNumberOfLinks() >= 8) 
									&& (neighbour.getLink(GateTypesIF.GT_SUB, 7).getWeight() > 0.5)) {
									continue;
							    }
								if((neighbour.getFirstLinkAt(GateTypesIF.GT_SUB).getWeight() < 0.99) 
									&& (neighbour.getGate(GateTypesIF.GT_SUB).getNumberOfLinks() >= 8) 
									&& (neighbour.getLink(GateTypesIF.GT_SUB, 2).getWeight() < 0.5)) {
									continue;
								}
								
								activation = neighbour.getGate(GateTypesIF.GT_POR).getConfirmedActivation();
								nextWaypoint = neighbour;
							}
						}
						
						if(nextWaypoint == null) {
							System.err.println("way could not be retrieved");
							break;
						} else {
							System.err.println("way retrieved " + nextWaypoint.getID());
						}
						waypoint = nextWaypoint;
						way.add(waypoint);
					}
					complete = true;
					task.setComplete(true);
					task.addWay(way);
					if(task.getNextTask() != null) {
						task.getNextTask().setStartChild(way.get(0));
					} else {
						startChildren.put(task.getGoal().getID(), way.get(0));
					}
					PlanningTask oldTask = null;
					for(int j = 0; j < way.size() - 1; j++) {
						PlanningTask newTask = new PlanningTask(way.get(j + 1), way.get(j), task.getLayer() + 1);
						newTask.setParentTask(task);
						task.addSubTask(newTask);
						tasks.push(newTask);
						if(oldTask != null) {
							newTask.setNextTask(oldTask);
						}
						oldTask = newTask;
					}
					break;
				}
			}
			
			if(!complete) {
				manipulator.setGateActivation(ACT_POR, 0.95);
				structure.activateNode(task.getStartChild().getID(), 1.0);
    		}	
    	} else {
        	System.out.println("planning ...");
    		planPC = 0;
    		currentTask = task;
    		manipulator.setGateActivation(ACT_POR, 0.0);
    		manipulator.setGateActivation(ACT_SUB, 0.0);
    		manipulator.setGateActivation(ACT_SUR, 0.0);
    		if(task.isComplete()) {
    			task.clear();
    			tasks.pop();
    		}
    	}
    }
    
    private void resetPlanning() {
    	state = 0;
        pc = 0;
    	goal = null;
        start = null;
        
        if(way != null) {
        	way.deleteAll();
        	way = null;
        }
        if(!tasks.empty()) {
			while(!tasks.empty()) {
				tasks.peek().clear();
				tasks.pop();
			}	        						
			tasks.clear();
			tasks = new Stack<PlanningTask>();
		}
        navigating = false;
        startPoints.clear();
		endPoints.clear();
		startChildren.clear();
    }
    
    private ArrayList<Position> searchWay(Position startPosition, int motive) throws NetIntegrityException {
    	int type = identifyMotiveWithType(motive);
    	ConceptNode concept = getConcept(motive);
    	Position goalPosition = null;
    	ArrayList<Tile> toDo = null;
    	ArrayList<String> checkedIDs = null;
    	
    	if(startPosition == null)
    		return null;
    	
    	toDo = new ArrayList<Tile>();
    	checkedIDs = new ArrayList<String>();
    	Region nearest = getNearestRegion(startPosition);
    	
    	toDo.add(new Tile(nearest.getNode()));
    	boolean goalFound = false;
    	Tile goal = null;
    	
    	while(!goalFound && toDo.size() != 0) {
    		ArrayList<Tile> nextToDo = new ArrayList<Tile>();
    		Tile currentTile;
    		for(int i = 0; i< toDo.size(); i++) {
    			currentTile = toDo.get(i);
    			
    			if(isType(currentTile.voronoiNode, type)) {
    				goalFound = true;
    				goal = currentTile;
    				break;
    			}
    			
    			NetEntity node = currentTile.voronoiNode;
    			checkedIDs.add(node.getID());
    			
    			for(int j = 0; j < node.getGate(GateTypesIF.GT_POR).getNumberOfLinks(); j++) {
    				NetEntity neighbour = node.getLink(GateTypesIF.GT_POR, j).getLinkedEntity();
    				if(checkedIDs.contains(neighbour.getID()))
    					continue;
    				
    				checkedIDs.add(neighbour.getID());
    				Tile neighbourTile = new Tile(neighbour);
    				neighbourTile.setCost(currentTile.getCost() + 1);
    				currentTile.addNeighbour(neighbourTile);
    				neighbourTile.addNeighbour(currentTile);
    				
    				nextToDo.add(neighbourTile);
    			}
    		}
    		
    		toDo.clear();
    		toDo = nextToDo;
    	}
    	
    	if(!goalFound || goal == null)
    		return null;
    	
    	ArrayList<Position> toReturn = null;
    	
    	boolean reached = false;
    	Tile currentTile = goal;
    	ArrayList<Position> newWay = new ArrayList<Position>();
    	int nextValue;
    	Tile nextTile = null;
    	int wayCounter = 0;
    	while(!reached) {
    		nextValue = (int)(ConstantValues.WORLDMAXX * ConstantValues.WORLDMAXY);
    		for(int j = 0; j < currentTile.getNeighbours().size(); j++) {
    			Tile neighbour = currentTile.getNeighbours().get(j);
				if(neighbour.getCost() >= 0 && neighbour.getCost() < nextValue) {
					nextValue = neighbour.getCost();
					nextTile = neighbour;
				}
    		}
    		currentTile = nextTile;
    		newWay.add(wayCounter++, NodeFunctions.getPosition(currentTile.voronoiNode));
    		
    		if(currentTile.voronoiNode.getID().equals(nearest.getNode().getID()))
    			reached = true;
    	}
    	
    	toReturn = new ArrayList<Position>();
    	for(int i = newWay.size() - 1; i >= 0; i--) {
    		toReturn.add(newWay.get(i));
    		
    		// TODO testing
    		if(way == null) {
				way = new Waypoint(newWay.get(i));
			} else {
				way.add(new Waypoint(newWay.get(i)));
			}
    	}   	
    	return toReturn;
    }
    
    private boolean isType(NetEntity node, int type) {
    	if(node.getGate(GateTypesIF.GT_SUB).getNumberOfLinks() >= type + 3) {
    		if(node.getLink(GateTypesIF.GT_SUB, type + 3).getWeight() > 0.05)
    			return true;
    		else
    			return false;
    	} else 
    		return false;
    }
}
