package org.micropsi.nodenet.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class HierarchicalRegionsModule extends AbstractNativeModuleImpl {

	// slots
	private static final int XCOORDINATE 	 = 18700;
	private static final int YCOORDINATE 	 = 18701;
	private static final int FOOD	         = 18702;
	private static final int WATER           = 18703;
	private static final int HEALING		 = 18704;
	private static final int HURTBYGROUND 	 = 18705;
	private static final int OBSTACLE 		 = 18706;
	private static final int PROTOCOL 		 = 18707;
	private static final int ORIENTATION     = 18708;
	
	// gates
	private static final int ROOT 			 = 18700;
	private static final int REGION          = 18701;
	private static final int UNKNOWNROOT	 = 18702;
	private static final int FOODCONCEPT	 = 18703;
	private static final int WATERCONCEPT 	 = 18704;
	private static final int HEALINGCONCEPT  = 18705;
	private static final int DAMAGECONCEPT   = 18706;
	private static final int OBSTACLECONCEPT = 18707;
	
	private Slot x;
	private Slot y;
	private Slot food;
	private Slot water;
	private Slot healing;
	private Slot obstacle;
	private Slot damage;
	private Slot protocolChanged;
	private Slot orientation;
	private Slot protocolRegions;
	
	boolean firsttime = true;
	private Position lastPosition = null;
	
	private ConceptNode foodConcept;
	private ConceptNode waterConcept;
	private ConceptNode healingConcept;
	private ConceptNode damageConcept;
	private ConceptNode obstacleConcept;
	private ConceptNode rootNode;
	private ConceptNode allRegionsConcept;
	private String unknownNode;
	
	private int oldObstacleCount = 0;
	private int oldDamageCount = 0;
	private int oldFoodCount = 0;
	private int oldWaterCount = 0;
	private int oldHealCount = 0;
	
	int pc = 0;
	
	private List<Region> regions;
	private List<Region> unknownRegions;
	private List<Position> obstacles;
	private List<Position> poisonSpots;
	private List<Region> foodRegions;
	private List<Region> waterRegions;
	private List<Region> healingRegions;
	private List<Region> notInsertedNodes;
	private HashMap<NetEntity, Integer> notInsertedRegions;
	
	//advanced mapping
	private Region oldRegion = null;
	private Position oldPosition = null;
	private int oldType = -1;
	
	private final int[] slotTypes = {
	        XCOORDINATE,
	        YCOORDINATE,
	        FOOD,
	        WATER,
	        HEALING,
	        OBSTACLE,
	        HURTBYGROUND,
	        PROTOCOL, 
	        ORIENTATION
		};
	
	private final int[] gateTypes = {
			ROOT,
			REGION,
			UNKNOWNROOT,
			FOODCONCEPT,
			WATERCONCEPT,
			HEALINGCONCEPT,
			DAMAGECONCEPT,
			OBSTACLECONCEPT
	};
	private Position lastInsertedFood;
	private Position lastInsertedWater;
	private Position lastInsertedHealing;
	private Position lastInsertedDamage;
	private Position lastInsertedObstacle;
	
	public HierarchicalRegionsModule() {	
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "hierarchical-regions";

			public String getExtensionID() {
				return id;
			}

			public String slotType(int type) {
			    switch(type) {
			    	case XCOORDINATE:  return "x-position";
			    	case YCOORDINATE:  return "y-position";
			    	case FOOD:		   return "food";
			    	case WATER:        return "water";
			    	case HEALING:      return "healing";
			    	case OBSTACLE:     return "obstacle";
			    	case HURTBYGROUND: return "got-hurt";
			    	case PROTOCOL:	   return "protocol";
			    	case ORIENTATION:  return "orientation";
			    	default:           return null;
			    }
			}

			public String gateType(int type) {
				switch(type) {
					case ROOT: return "rootnode";
					case REGION: return "regions";
					case UNKNOWNROOT: return "unknownnode";
					case FOODCONCEPT: return "food-concept";
					case WATERCONCEPT: return "water-concept";
					case HEALINGCONCEPT: return "healing-concept";
					case DAMAGECONCEPT: return "damage-concept";
			    	case OBSTACLECONCEPT: return "obstacle-concept";
					default: return null;
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
				case XCOORDINATE:
					x = slots[i];
					break;
				case YCOORDINATE: 
					y = slots[i];
					break;
				case FOOD:
					food = slots[i];
					break;
				case WATER:
					water = slots[i];
					break;
				case HEALING:
					healing = slots[i];
					break;
			    case OBSTACLE:
			        obstacle = slots[i];
			        break;
			    case HURTBYGROUND:
			        damage = slots[i];
			        break;
			    case PROTOCOL:
			    	protocolChanged = slots[i];
			    	break;
			    case ORIENTATION:
			    	orientation = slots[i];
			    	break;
			}
		}
	}

	public void calculate(Slot[] slots, GateManipulator manipulator, long netstep)	throws NetIntegrityException {
		if (firsttime) {
			catchSlots(slots);
			firsttime = false;
		
			Link l = manipulator.getGate(FOODCONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("regions has no link to foodconcept");
			else
				foodConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(WATERCONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("regions has no link to waterconcept");
			else
				waterConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(HEALINGCONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("regions has no link to healingconcept");
			else
				healingConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(DAMAGECONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("regions has no link to damageconcept");
			else
				damageConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(OBSTACLECONCEPT).getLinkAt(0);
			if(l == null)
				logger.warn("regions has no link to obstacleconcept");
			else
				obstacleConcept = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(ROOT).getLinkAt(0);
			if(l == null)
				logger.warn("regions has no link to region root");
			else
				rootNode = (ConceptNode)l.getLinkedEntity();
			
			l = manipulator.getGate(UNKNOWNROOT).getLinkAt(0);
			if(l == null)
				logger.warn("regions has no link to unknownRootNodeConcept");
			else
				unknownNode = l.getLinkedEntityID();
			
			l = manipulator.getGate(REGION).getLinkAt(0);
			if(l == null)
				logger.warn("regions has no link to regions");
			else
				allRegionsConcept = (ConceptNode)l.getLinkedEntity();
			
			regions = new ArrayList<Region>();
			unknownRegions = new ArrayList<Region>();
			obstacles = new ArrayList<Position>();
			poisonSpots = new ArrayList<Position>();
			foodRegions = new ArrayList<Region>();
			waterRegions = new ArrayList<Region>();
			healingRegions = new ArrayList<Region>();
			notInsertedNodes = new ArrayList<Region>();
			notInsertedRegions = new HashMap<NetEntity, Integer>();
			
			if(rootNode.getGate(GateTypesIF.GT_CAT).getNumberOfLinks() == 0) {
				String nodeID;
				nodeID = createUnknownRegionNode(new Position(0.0, 0.0));
				connectRegionNode(nodeID);
				nodeID = createUnknownRegionNode(new Position(ConstantValues.WORLDMAXX, 0.0));
				connectRegionNode(nodeID);
				nodeID = createUnknownRegionNode(new Position(0.0, ConstantValues.WORLDMAXY));
				connectRegionNode(nodeID);
				nodeID = createUnknownRegionNode(new Position(ConstantValues.WORLDMAXX, ConstantValues.WORLDMAXY));
				connectRegionNode(nodeID);
			}
			
			for(int i = 0; i < obstacleConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
				obstacles.add(NodeFunctions.getPosition(obstacleConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity()));
			}
			oldObstacleCount = obstacleConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks();
				
			for(int i = 0; i < damageConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
				poisonSpots.add(NodeFunctions.getPosition(damageConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity()));
			}
			oldDamageCount = damageConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks();

			collectRegions();	
		}
		
		double xposition = x.getIncomingActivation();
        double yposition = y.getIncomingActivation();
        Position currentPosition = NodeFunctions.getPosition(xposition, yposition);
		
        if(xposition == 0.0 && yposition == 0.0)
        	return;   
        
		if(obstacleConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks() > oldObstacleCount) {
			Position obstaclePos = NodeFunctions.getPosition(obstacleConcept.getLink(GateTypesIF.GT_CAT, oldObstacleCount).getLinkedEntity());
			obstacles.add(obstaclePos);
			oldObstacleCount++;
		}
		
		if(damageConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks() > oldDamageCount) {
			Position damagePos = NodeFunctions.getPosition(damageConcept.getLink(GateTypesIF.GT_CAT, oldDamageCount).getLinkedEntity());
			poisonSpots.add(damagePos);
			oldDamageCount++;
		}
		
		if(lastPosition != null && lastPosition.distance2D(currentPosition) > ConstantValues.REGIONMINDISTANCE / 2) {
        	String newRegion = createRegionNodeAndUnknownSurrounding(currentPosition);
        	if(newRegion != null)
        		connectRegionNode(newRegion); 
        } else if (lastPosition == null) {
        	String newRegion = createRegionNodeAndUnknownSurrounding(currentPosition);
        	if(newRegion != null)
        		connectRegionNode(newRegion);
        }
	
		Region nearest = getNearestRegion(currentPosition);
		
		int type = -1; 
		if(damage.getIncomingActivation() > 0.5) {
        	type = Region.DAMAGE;
        } else if(obstacle.getIncomingActivation() > 0.5) {
        	type = Region.IMPASSABLE;
        } else if(food.getIncomingActivation() > 0.5) {
        	type = Region.FOOD;
        } else if(water.getIncomingActivation() > 0.5) {
        	type = Region.WATER;
        } else if(healing.getIncomingActivation() > 0.5) {
        	type = Region.HEALING;
        }

        if(type != -1) {
        	addEventNode(type, currentPosition);
        	if(!nearest.isType(type)) {
        		NetEntity node = nearest.getNode();
        		if(!ConstantValues.advancedMapping) {
	        		structure.changeLinkParameters(node.getLink(GateTypesIF.GT_SUB, type + 3), 1.0, 1.0);
	        		structure.changeLinkParameters(node.getLink(GateTypesIF.GT_SUB, 2), 1.0, 1.0);
	        		if(nearest.isUnknown())
	        			removeFromUnknown(nearest);
	        		nearest.addType(type);
        		}
        		// regions with obstacles not suitable for planning
        		if(type == Region.IMPASSABLE) {
        			structure.getGateManipulator(node.getID()).setGateAmpFactor(GateTypesIF.GT_POR, 0.0);
        		}
        	}
        	List<Region> regionList = getRegionList(type);
        	if(regionList != null && !regionList.contains(nearest))
        		getRegionList(type).add(nearest);
        }
        
        //start advanced mapping
        if(ConstantValues.advancedMapping) {
	        if(oldRegion != null) {
	        	String ID;
	        	if((oldType != type) && (type != Region.IMPASSABLE) && (oldType != Region.IMPASSABLE)) {
	        		if(oldRegion.getNode().getID().equals(nearest.getNode().getID())) {
	        			double distance = currentPosition.distance2D(oldRegion.position);
	        			if(distance < ConstantValues.REGIONMINDISTANCE / 2 && distance > 0.0) {
	        				moveRegion(oldRegion, oldPosition);
	        			} else {
	        				ID = createRegionNode(currentPosition, oldType);
	        				connectRegionNode(ID);
	        			}
	        			ID = createRegionNode(currentPosition, type);
	        			connectRegionNode(ID);
	        			// TODO correct por/ret-values
	        		} 
	        	} 
	        }
	               
	        oldType = type;
	        oldRegion = nearest;
	        oldPosition = currentPosition;
	        
	        nearest = getNearestRegion(currentPosition);
	        
	        if(type != -1 && !nearest.isType(type)) {
				structure.changeLinkParameters(nearest.getNode().getLink(GateTypesIF.GT_SUB, type + 3), 1.0, 1.0);
	    		structure.changeLinkParameters(nearest.getNode().getLink(GateTypesIF.GT_SUB, 2), 1.0, 1.0);
	    		if(nearest.isUnknown())
	    			removeFromUnknown(nearest);
	    		nearest.addType(type);
			}
        }
        //end advanced mapping
        nearest = null;
        
		pc = (pc + 1) % 3000;
		if(!ConstantValues.advancedMapping) { // temporary while advanced mapping is not neighbour-correct
	        if(pc == 0) {
	        	correctNeighbours();
				createHierarchy();
	        }
		}
	}
	
	private void deleteRegionNode(String nodeID) throws NetIntegrityException {
		Region toDelete = null;
		for(Region region : regions) {
			if(region.getNode().getID() == nodeID) {
				toDelete = region;
				break;
			}
		}
		if(toDelete != null) {
			regions.remove(toDelete);
			if(unknownRegions.contains(toDelete))
				unknownRegions.remove(toDelete);
		}
		
    	while(structure.findEntity(nodeID).getFirstLinkAt(GateTypesIF.GT_SUB) != null) {
    		structure.deleteEntity(structure.findEntity(nodeID).getFirstLinkAt(GateTypesIF.GT_SUB).getLinkedEntityID());
    	}
    	structure.deleteEntity(nodeID);
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
    	if(to == null || from == null)
    		return;
    	
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
    }
    
    private Region getNearestRegion(Position position) {
    	return getNearestRegion(position, null); 
    }
    
    private Region getNearestRegion(Position position, Region ignore) {
    	long time = System.currentTimeMillis();
    	
    	Region toReturn = null;
    	double distance = 1000.0;
    	
    	for(Region region : regions) {
    		if(!region.equals(ignore)) {
	    		double compareDistance = position.distance2D(region.position);
	    		if(compareDistance < distance) {
	    			toReturn = region;
	    			distance = compareDistance;
	    		}
    		}
    	}
    	
    	double diff = System.currentTimeMillis() - time;
    	if(diff > 20)
    		System.out.println("search for nearest region took " + (System.currentTimeMillis() - time) + "ms");
    	
    	return toReturn;
    }
  
    /**
     * creates a neutral region node for the position
     * @param position
     * @return the ID of the created region node
     */
    private String createRegionNode(Position position) throws NetIntegrityException {
    	String newNode = structure.createTopoNode("region " + position.getX() + ":" + position.getY());
        String newX = structure.createConceptNode("x");
        String newY = structure.createConceptNode("y");
        String exploration = structure.createConceptNode("exploration");
        String food = structure.createConceptNode("food");
        String water = structure.createConceptNode("water");
        String healing = structure.createConceptNode("healing");
        String damage = structure.createConceptNode("damage");
        String impassable = structure.createConceptNode("impassable");
        linkEntities(newNode, newX, GateTypesIF.GT_SUB, position.getX() / ConstantValues.WORLDMAXX, position.getX() / ConstantValues.WORLDMAXX);
        linkEntities(newNode, newY, GateTypesIF.GT_SUB, position.getY() / ConstantValues.WORLDMAXY, position.getY() / ConstantValues.WORLDMAXY);
        linkEntities(newNode, exploration, GateTypesIF.GT_SUB, 1.0, 1.0);
        linkEntities(newNode, food, GateTypesIF.GT_SUB, 0.0, 1.0);
        linkEntities(newNode, water, GateTypesIF.GT_SUB, 0.0, 1.0);
        linkEntities(newNode, healing, GateTypesIF.GT_SUB, 0.0, 1.0);
        linkEntities(newNode, damage, GateTypesIF.GT_SUB, 0.0, 1.0);
        linkEntities(newNode, impassable, GateTypesIF.GT_SUB, 0.0, 1.0);
        
        structure.getGateManipulator(newNode).setGateAmpFactor(GateTypesIF.GT_SUB, 0.0);
        
        return newNode;
    }
    
    private String createRegionNode(Position position, int type) throws NetIntegrityException {
    	String toReturn = createRegionNode(position);
    	if(type >= 0) {
    		structure.changeLinkParameters(structure.findEntity(toReturn).getLink(GateTypesIF.GT_SUB, type + 3), 1.0, 1.0);
    	}
    	return toReturn;
    }
    
    /**
     * creates a region node that is tagged as unkown territory
     * @param position
     * @return the ID of the created region node
     * @throws NetIntegrityException
     */
    private String createUnknownRegionNode(Position position) throws NetIntegrityException {
    	String newNode = structure.createTopoNode("region " + position.getX() + ":" + position.getY());
        String newX = structure.createConceptNode("x");
        String newY = structure.createConceptNode("y");
        String exploration = structure.createConceptNode("exploration");
        String food = structure.createConceptNode("food");
        String water = structure.createConceptNode("water");
        String healing = structure.createConceptNode("healing");
        String damage = structure.createConceptNode("damage");
        String impassable = structure.createConceptNode("impassable");

        linkEntities(newNode, newX, GateTypesIF.GT_SUB, position.getX() / ConstantValues.WORLDMAXX, position.getX() / ConstantValues.WORLDMAXX);
        linkEntities(newNode, newY, GateTypesIF.GT_SUB, position.getY() / ConstantValues.WORLDMAXY, position.getY() / ConstantValues.WORLDMAXY);
        linkEntities(newNode, exploration, GateTypesIF.GT_SUB, 0.0, 1.0);
        linkEntities(newNode, food, GateTypesIF.GT_SUB, 0.0, 1.0);
        linkEntities(newNode, water, GateTypesIF.GT_SUB, 0.0, 1.0);
        linkEntities(newNode, healing, GateTypesIF.GT_SUB, 0.0, 1.0);
        linkEntities(newNode, damage, GateTypesIF.GT_SUB, 0.0, 1.0);
        linkEntities(newNode, impassable, GateTypesIF.GT_SUB, 0.0, 1.0);

        structure.getGateManipulator(newNode).setGateAmpFactor(GateTypesIF.GT_SUB, 0.0);
        structure.getGateManipulator(newNode).setGateAmpFactor(GateTypesIF.GT_POR, 0.0);
        
        return newNode;
    }
    
    /**
     * creates a known region node and surrounds it with unknown
     * nodes if the region around it is unknown
     * @param position
     * @return the ID of the created region node
     * @throws NetIntegrityException
     */
    private String createRegionNodeAndUnknownSurrounding(Position position) throws NetIntegrityException {
    	lastPosition = position;
    	
    	Position testPosition = null;
    	Position centerPosition = position;
    	Region nearest = null;
    	String toReturn = null;
    	
    	Region region = null;
    	nearest = getNearestRegion(position, null);
    	
    	if(nearest.isUnknown()) {
    		if(nearest.position.distance2D(position) < ConstantValues.REGIONMINDISTANCE) {    			
    			removeFromUnknown(nearest);
    			region = nearest;
    			centerPosition = nearest.position;
    		} else {
    			toReturn = createRegionNode(position);
    		}
    	}
    	
    	WorldVector normal = new WorldVector(ConstantValues.REGIONMINDISTANCE, 0.0, 0.0);
    	normal.rotate(orientation.getIncomingActivation() * 360.0);

    	for(int i = 0; i <= 15; i++) {
    		normal.rotate(45.0);  		
    		testPosition = new Position(centerPosition.getX() + normal.getX(), centerPosition.getY() + normal.getY());
    		if(testPosition != null) {
    			nearest = getNearestRegion(testPosition, region);
    			if(nearest.position.distance2D(testPosition) >= ConstantValues.REGIONMINDISTANCE - (ConstantValues.REGIONMINDISTANCE / 4)) {
    				//if(unknownRegions.contains(nearest) || nearest.equals(region)) {
    					String nodeID = createUnknownRegionNode(testPosition);
    					connectRegionNode(nodeID);
    					if(toReturn != null) {
    						linkEntities(toReturn, nodeID, GateTypesIF.GT_POR, 1.0, 1.0);
    						linkEntities(nodeID, toReturn, GateTypesIF.GT_POR, 1.0, 1.0);
    					} else if (region != null){
    						linkEntities(region.getNode().getID(), nodeID, GateTypesIF.GT_POR, 1.0, 1.0);
    						linkEntities(nodeID, region.getNode().getID(), GateTypesIF.GT_POR, 1.0, 1.0);
    					}
    				//}
    			} else {
    				if(!nearest.equals(region)) {
    					if(toReturn != null && (structure.findEntity(toReturn).getGate(GateTypesIF.GT_POR).getLinkTo(nearest.getNode().getID(), SlotTypesIF.ST_GEN) == null)) {						
    						linkEntities(toReturn, nearest.getNode().getID(), GateTypesIF.GT_POR, 1.0, 1.0);
    						linkEntities(nearest.getNode().getID(), toReturn, GateTypesIF.GT_POR, 1.0, 1.0);
    					} else if (region != null && (region.getNode().getGate(GateTypesIF.GT_POR).getLinkTo(nearest.getNode().getID(), SlotTypesIF.ST_GEN) == null)){
    						linkEntities(region.getNode().getID(), nearest.getNode().getID(), GateTypesIF.GT_POR, 1.0, 1.0);
    						linkEntities(nearest.getNode().getID(), region.getNode().getID(), GateTypesIF.GT_POR, 1.0, 1.0);
    					}
    				}
    			}
    		}
    	}
    	
    	return toReturn;
    }
    
    private void removeFromUnknown(Region region) throws NetIntegrityException {
    	NetEntity regionNode = region.getNode();
    	structure.changeLinkParameters(regionNode.getLink(GateTypesIF.GT_SUB, 2), 1.0, 1.0);
    	structure.getGateManipulator(regionNode.getID()).setGateAmpFactor(GateTypesIF.GT_POR, 1.0);
    	try {
    		structure.deleteLink(unknownNode, GateTypesIF.GT_CAT, regionNode.getID(), SlotTypesIF.ST_GEN);
    		structure.deleteLink(regionNode.getID(), GateTypesIF.GT_EXP, unknownNode, SlotTypesIF.ST_GEN);
    	} catch(NetIntegrityException e) {
    		System.err.println("no link to unknown");
    	}
    	
    	region.known();
    	unknownRegions.remove(region);
    }
    
    /**
     * adds a region node with the ID node by linking it to the
     * rootNode-concept
     * @param node
     * @throws NetIntegrityException
     */
    private void connectRegionNode(String node) throws NetIntegrityException {
    	linkEntities(rootNode.getID(), node, GateTypesIF.GT_CAT, 1.0, 1.0);
    	Region toAdd = getRegionFromNode(node);
    	if(!regions.contains(toAdd))
    		regions.add(toAdd);
    	if(toAdd.isUnknown()) {
    		unknownRegions.add(toAdd);
    		linkEntities(unknownNode, node, GateTypesIF.GT_CAT, 1.0, 1.0);
    	}
    }
    
    /**
     * returns an instance of Region filled with the information
     * in the region node with the ID nodeID
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

    private void addEventNode(int eventType, Position position) throws NetIntegrityException {
    	if(lastInserted(eventType) != null) {
    		if(position.distance2D(lastInserted(eventType)) < ConstantValues.NODEMINDISTANCE)
    			return;
    	}
    	ConceptNode eventNode = getConcept(eventType);
    	int numberOfNodes = eventNode.getGate(GateTypesIF.GT_CAT).getNumberOfLinks();
    	Position testPosition = new Position(position);
    	for(int i = 0; i < numberOfNodes; i++) {
    		if(testPosition.distance2D(NodeFunctions.getPosition(eventNode.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity())) < ConstantValues.NODEMINDISTANCE)
	    			return;
    	}
    	String ID = createPositionNode(testPosition);
    	linkEntities(eventNode.getID(), ID, GateTypesIF.GT_CAT, 1.0, 1.0);
        setLastInserted(eventType, testPosition);
    }
    
    private Position lastInserted(int nodeType) {
    	switch(nodeType) {
    		case Region.FOOD: return lastInsertedFood;
    		case Region.WATER: return lastInsertedWater;
    		case Region.HEALING: return lastInsertedHealing;
    		case Region.DAMAGE: return lastInsertedDamage;
    		case Region.IMPASSABLE: return lastInsertedObstacle;
    		default: return null;
    	}
    }
    
    private ConceptNode getConcept(int nodeType) {
    	switch(nodeType) {
    		case Region.FOOD: return foodConcept;
    		case Region.WATER: return waterConcept;
    		case Region.HEALING: return healingConcept;
    		case Region.DAMAGE: return damageConcept;
    		case Region.IMPASSABLE: return obstacleConcept;
    		default: return null;
    	}
    }
    
    private void setLastInserted(int nodeType, Position position) {
    	switch(nodeType) {
    		case Region.FOOD: lastInsertedFood = position;
    						  break;
    		case Region.WATER: lastInsertedWater = position;
    						   break;
    		case Region.HEALING: lastInsertedHealing = position;
    							 break;
    		case Region.DAMAGE: lastInsertedDamage = position;
    							break;
    		case Region.IMPASSABLE: lastInsertedObstacle = position;
    							    break;
    	}
    }
    
    private String createPositionNode(Position position) throws NetIntegrityException {
        String newNode = structure.createConceptNode("pos " + position.getX() + ":" + position.getY());
        String newX = structure.createConceptNode("x");
        String newY = structure.createConceptNode("y");
        linkEntities(newNode, newX, GateTypesIF.GT_SUB, position.getX() / ConstantValues.WORLDMAXX, position.getX() / ConstantValues.WORLDMAXX);
        linkEntities(newNode, newY, GateTypesIF.GT_SUB, position.getY() / ConstantValues.WORLDMAXY, position.getY() / ConstantValues.WORLDMAXY);
        
        return newNode;
    }
    
    private void createHierarchy() throws NetIntegrityException {
    	for(Region region : regions) {
    		if(region.isUnknown())
    			continue;
    		
    		NetEntity concept = region.getNode();
    		Position regionPos = NodeFunctions.getPosition(concept);
    		
    		boolean isUnknown = false;
    		//TODO with the new CAT/EXP linking this is not necessary anymore
    		for(int j = 0; j < concept.getGate(GateTypesIF.GT_EXP).getNumberOfLinks(); j++) {
    			String linked = concept.getLink(GateTypesIF.GT_EXP, j).getLinkedEntityID();
    			if(linked.equals(unknownNode)) {
    				if(region.getNode().getLink(GateTypesIF.GT_SUB, 2).getWeight() < 0.5) {
    					region.unknown();
    					structure.getGateManipulator(region.getNode().getID()).setGateAmpFactor(GateTypesIF.GT_POR, 0.0);
    					isUnknown = true;
    					break;
    				} else {
    					removeFromUnknown(region);
    				}
    			}
    		}
    		
    		if(isUnknown)
    			continue;

    		NetEntity regionNode = region.getNode();
    		if(regionNode.getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 0) {
    			ArrayList<NetEntity> neighbours = new ArrayList<NetEntity>();
    			ArrayList<NetEntity> neighbourRegions = new ArrayList<NetEntity>();;
    			HashMap<NetEntity, Integer> neighboursInRegion = new HashMap<NetEntity, Integer>();
    			for(int i = 0; i < regionNode.getGate(GateTypesIF.GT_POR).getNumberOfLinks(); i++) {
    				NetEntity neighbour = regionNode.getLink(GateTypesIF.GT_POR, i).getLinkedEntity();
    				if(neighbour.getLink(GateTypesIF.GT_SUB, 2).getWeight() > 0.5) {
    					neighbours.add(neighbour);
    				}
    			}
    			for(NetEntity neighbour : neighbours) {
    				if(neighbour.getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 1) {
    					NetEntity upperRegion = neighbour.getLastLinkAt(GateTypesIF.GT_SUR).getLinkedEntity();
    					if(!neighbourRegions.contains(upperRegion)) {
    						neighbourRegions.add(upperRegion);
    						neighboursInRegion.put(upperRegion, new Integer(1));
    					} else {
    						int count = neighboursInRegion.get(upperRegion).intValue() + 1;
    						neighboursInRegion.remove(upperRegion);
    						neighboursInRegion.put(upperRegion, new Integer(count));
    					}
    				}
    			}
    			
    			if(neighbourRegions.isEmpty()) {
    				String newRegionNode = structure.createTopoNode("region");
    				linkEntities(allRegionsConcept.getID(), newRegionNode, GateTypesIF.GT_CAT, 1.0, 1.0);
    				linkEntities(newRegionNode, regionNode.getID(), GateTypesIF.GT_SUB, 1.0, 1.0);
    			} else {
    				boolean inserted = false;
    				String insertRegion = null;
    				
    				/*
    				ArrayList<ConceptNode> potentialRegions = new ArrayList<ConceptNode>();
    				for(ConceptNode neighbourRegion : neighbourRegions) {
    					Region type = getRegionFromNode(neighbourRegion.getFirstLinkAt(GateTypesIF.GT_SUB).getLinkedEntityID());
    					if(region.equalsType(type)) {
    						potentialRegions.add(neighbourRegion);
    					}
    				}

    				int oldNumber = 0;
    				for(ConceptNode neighbourRegion : potentialRegions) {
    					if(neighbourRegion.getGate(GateTypesIF.GT_SUB).getNumberOfLinks() >= ConstantValues.MAX_CLUSTER_SIZE)
    						continue;
    					if(neighboursInRegion.get(neighbourRegion).intValue() > oldNumber) {
    						oldNumber = neighboursInRegion.get(neighbourRegion).intValue();
    						insertRegion = neighbourRegion.getID();
    						inserted = true;
    					}
    				}
    				*/
    				
//    				alternative
    				double oldMaxDistance = 100;
    				
    				for(NetEntity neighbourRegion : neighbourRegions) {
    					if(neighbourRegion.getGate(GateTypesIF.GT_SUB).getNumberOfLinks() >= ConstantValues.MAX_CLUSTER_SIZE)
    						continue;
    					Position[] posArray = new Position[neighbourRegion.getGate(GateTypesIF.GT_SUB).getNumberOfLinks()];
    					double maxDistance = 0;
    					for(int j = 0; j < neighbourRegion.getGate(GateTypesIF.GT_SUB).getNumberOfLinks(); j++) {
    						Position current = NodeFunctions.getPosition(neighbourRegion.getLink(GateTypesIF.GT_SUB, j).getLinkedEntity());
    						double distance = current.distance2D(regionPos);
    						posArray[j] = current;
    						if(distance > maxDistance) 
    							maxDistance = distance;
    						for(int k = 0; k < j; k++) {
    							distance = posArray[k].distance(current);
    							if(distance > maxDistance) 
        							maxDistance = distance;
    						}
    					}
    					if(maxDistance < oldMaxDistance) {
    						oldMaxDistance = maxDistance;
    						if(maxDistance < 2.0 * ConstantValues.REGIONMINDISTANCE) {
    							insertRegion = neighbourRegion.getID();
    							inserted = true;
    						}
    					}
    				}
// 					end alt
    				
    				if(!inserted) {
        				insertRegion = structure.createTopoNode("region"); 
        				linkEntities(allRegionsConcept.getID(), insertRegion, GateTypesIF.GT_CAT, 1.0, 1.0);
    				}
    				if(insertRegion != null) {				
        				linkEntities(insertRegion, regionNode.getID(), GateTypesIF.GT_SUB, 1.0, 1.0);
	    				for(NetEntity neighbourRegion : neighbourRegions) {
	    					if((!neighbourRegion.getID().equals(insertRegion))
	    					   && (neighbourRegion.getGate(GateTypesIF.GT_POR).getLinkTo(insertRegion, SlotTypesIF.ST_GEN) == null)) {
		    					linkEntities(neighbourRegion.getID(), insertRegion, GateTypesIF.GT_POR, 1.0, 1.0);
		    					linkEntities(insertRegion, neighbourRegion.getID(), GateTypesIF.GT_POR, 1.0, 1.0);
	    					}
	    				}
	    				if(notInsertedNodes.contains(region))
	    	    			notInsertedNodes.remove(region);
    				}
    			}
    		}
    	}
    	
    	//delete unused nodes
    	
    	ArrayList<NetEntity> toDelete = new ArrayList<NetEntity>();
    	for(int i = 0; i < allRegionsConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
    		NetEntity temp = allRegionsConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity();
    		if(temp.getGate(GateTypesIF.GT_SUB).getNumberOfLinks() <= 1)
    			toDelete.add(temp);
    	}
    	for(int j = 0; j < toDelete.size(); j++) {
    		try {
    			structure.deleteEntity(toDelete.get(j).getID());
    		} catch (NetIntegrityException e) {
    			e.printStackTrace();
            }
    	}
    	toDelete.clear();
    	
    	// insert nodes not yet inserted
    	for(Region region : regions) {
    		if(region.isUnknown())
    			continue;
    		
    		NetEntity node = region.getNode();
    		Position nodePos = NodeFunctions.getPosition(node);
    		ArrayList<NetEntity> neighbours = new ArrayList<NetEntity>(); 
    		if(node.getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 0) {
    			if(notInsertedNodes.contains(region)) {
    				double neighbourDist = 1000;
    				NetEntity insertNeighbour = null; 
    				for(int i = 0; i < node.getGate(GateTypesIF.GT_POR).getNumberOfLinks(); i++) {
    					NetEntity neighbour = node.getLink(GateTypesIF.GT_POR, i).getLinkedEntity();
    					if(neighbour.getLink(GateTypesIF.GT_SUB, 2).getWeight() > 0.5 && neighbour.getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 1) { //not unknown and inserted
    						NetEntity neighbourRegion = neighbour.getLastLinkAt(GateTypesIF.GT_SUR).getLinkedEntity();
        					if(!neighbours.contains(neighbourRegion))
        						neighbours.add(neighbourRegion);
        					
        					double maxDist = 0;
        					for(int j = 0; j < neighbourRegion.getGate(GateTypesIF.GT_SUB).getNumberOfLinks(); j++){
        						NetEntity child = neighbourRegion.getLink(GateTypesIF.GT_SUB, j).getLinkedEntity();
        						Position childPos = NodeFunctions.getPosition(child);
        						if(childPos == null) {
        							System.out.println("childpos null");
        							if(child == null) {
        								System.out.println("child null");
        							}
        						}
        						double dist = childPos.distance2D(nodePos);
        						if(dist > maxDist)
        							maxDist = dist;
        					}
        					if(maxDist < neighbourDist) {
        						insertNeighbour = neighbourRegion;
        						neighbourDist = maxDist;
        					}
        				}
    				}
    				if(insertNeighbour != null) {
	    				linkEntities(insertNeighbour.getID(), node.getID(), GateTypesIF.GT_SUB, 1.0, 1.0);
	    				for(NetEntity neighbourRegion : neighbours) {
	    					if((!neighbourRegion.getID().equals(insertNeighbour.getID()))
	    					   && (neighbourRegion.getGate(GateTypesIF.GT_POR).getLinkTo(insertNeighbour.getID(), SlotTypesIF.ST_GEN) == null)) {
		    					linkEntities(neighbourRegion.getID(), insertNeighbour.getID(), GateTypesIF.GT_POR, 1.0, 1.0);
		    					linkEntities(insertNeighbour.getID(), neighbourRegion.getID(), GateTypesIF.GT_POR, 1.0, 1.0);
	    					}
	    				}
	    				if(notInsertedNodes.contains(region))
	    					notInsertedNodes.remove(region);
    				} else {
    					System.out.println("error - node still not inserted; neighbours: " + region.getNode().getGate(GateTypesIF.GT_POR).getNumberOfLinks() + " at " + NodeFunctions.positionToString(NodeFunctions.getPosition(region.getNode())));
    					if(region.getNode().getGate(GateTypesIF.GT_POR).getNumberOfLinks() == 0) {
    						WorldVector normal = new WorldVector(ConstantValues.REGIONMINDISTANCE, 0.0, 0.0);
    				    	normal.rotate(orientation.getIncomingActivation() * 360.0);
    				    	Position testPosition;
    				    	Position centerPosition = NodeFunctions.getPosition(region.getNode());
    				    	Region nearest = null;
    				    	
    				    	for(int i = 0; i <= 15; i++) {
    				    		switch(i) {
    				    			default: normal.rotate(45.0);
    				    					 break;
    				    		}
    				    		testPosition = new Position(centerPosition.getX() + normal.getX(), centerPosition.getY() + normal.getY());
    				    		if(testPosition != null) {
    				    			nearest = getNearestRegion(testPosition, region);
    				    			if(nearest.position.distance2D(testPosition) >= ConstantValues.REGIONMINDISTANCE - (ConstantValues.REGIONMINDISTANCE / 4)) {
    				    				System.out.println("rnode not inserted");
    				    				//if(unknownRegions.contains(nearest) || nearest.equals(region)) {
    				    				//	String nodeID = createUnknownRegionNode(testPosition);
    				    				//	connectRegionNode(nodeID);
    				    				//	if (region != null){
    				    				//		linkEntities(region.getNode().getID(), nodeID, GateTypesIF.GT_POR, 1.0, 1.0);
    				    				//		linkEntities(nodeID, region.getNode().getID(), GateTypesIF.GT_POR, 1.0, 1.0);
    				    				//	}
    				    				//}
    				    			} else {
    				    				if(!nearest.equals(region)) {
    				    					if (region != null && (region.getNode().getGate(GateTypesIF.GT_POR).getLinkTo(nearest.getNode().getID(), SlotTypesIF.ST_GEN) == null)){
    				    						linkEntities(region.getNode().getID(), nearest.getNode().getID(), GateTypesIF.GT_POR, 1.0, 1.0);
    				    						linkEntities(nearest.getNode().getID(), region.getNode().getID(), GateTypesIF.GT_POR, 1.0, 1.0);
    				    					}
    				    				}
    				    			}
    				    		}
    				    	}
    					}
    						
    				}  				
    			} else {
    				if(!notInsertedNodes.contains(region))
    					notInsertedNodes.add(region);
    			}
    		} else if(notInsertedNodes.contains(region)) {
    			notInsertedNodes.remove(region);
    		}
    	}
    	// end inserting
    	
    	ArrayList<NetEntity> regionNodes = new ArrayList<NetEntity>();
    	for(Region region : regions) {
    		if(!region.isUnknown() && region.getNode().getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 1) {
    			NetEntity temp = region.getNode().getLastLinkAt(GateTypesIF.GT_SUR).getLinkedEntity();
    			if(!regionNodes.contains(temp))
    				regionNodes.add(temp);
    		} 		
    	}

    	ArrayList<NetEntity> nextLayer = regionNodes;
    	
    	boolean stop = false;
    	while(!nextLayer.isEmpty() || nextLayer.size() > 1) {
    	//while(!stop) {
    		System.out.println("layersize " + nextLayer.size());
    		ArrayList<NetEntity> potentialNextLayer = createHierarchyForLayer(nextLayer);
    		
// do the same for the hierarchies.
    		/*
    		ArrayList<NetEntity> deleteNodes = new ArrayList<NetEntity>();
    		for(NetEntity regionNode : nextLayer) {
        		if(regionNode.getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 0) {
        			ArrayList<NetEntity> neighbours = new ArrayList<NetEntity>();
        			ArrayList<NetEntity> neighbourRegions = new ArrayList<NetEntity>();;
        			HashMap<NetEntity, Integer> neighboursInRegion = new HashMap<NetEntity, Integer>();
        			for(int i = 0; i < regionNode.getGate(GateTypesIF.GT_POR).getNumberOfLinks(); i++) {
        				NetEntity neighbour = regionNode.getLink(GateTypesIF.GT_POR, i).getLinkedEntity();
        				if(!neighbours.contains(neighbour))
        					neighbours.add(neighbour);
        				else {
        					System.out.println("error: too many neighbours");
        				}
        			}
        			for(NetEntity neighbour : neighbours) {
        				if(neighbour.getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 1) {
        					NetEntity upperRegion = neighbour.getLastLinkAt(GateTypesIF.GT_SUR).getLinkedEntity();
        					if(!neighbourRegions.contains(upperRegion)) {
        						neighbourRegions.add(upperRegion);
        						neighboursInRegion.put(upperRegion, new Integer(1));
        					} else {
        						int count = neighboursInRegion.get(upperRegion).intValue() + 1;
        						neighboursInRegion.remove(upperRegion);
        						neighboursInRegion.put(upperRegion, new Integer(count));
        					}
        				}
        			}
        			
        			if(neighbourRegions.isEmpty()) {
        				if(regionNode.getGate(GateTypesIF.GT_POR).getNumberOfLinks() > 0) { // TODO check this
        					String newRegionNode = structure.createConceptNode("region");
        					linkEntities(allRegionsConcept.getID(), newRegionNode, GateTypesIF.GT_CAT, 1.0, 1.0);
        					linkEntities(newRegionNode, regionNode.getID(), GateTypesIF.GT_SUB, 1.0, 1.0);
        				} else if(nextLayer.size() > 1) {
        					System.out.println("region without neighbours!");
        					ArrayList<NetEntity> realNeighbours = new ArrayList<NetEntity>();
        					for(int i = 0; i < regionNode.getGate(GateTypesIF.GT_SUB).getNumberOfLinks(); i++) {
        						NetEntity child = regionNode.getLink(GateTypesIF.GT_SUB, i).getLinkedEntity();
        						for(int j = 0; j < child.getGate(GateTypesIF.GT_POR).getNumberOfLinks(); j++) {
        							NetEntity childNeighbour = child.getLink(GateTypesIF.GT_POR, j).getLinkedEntity();
        							if(childNeighbour.getGate(GateTypesIF.GT_SUB).getNumberOfLinks() == 8 && childNeighbour.getLink(GateTypesIF.GT_SUB, 2).getWeight() <= 0.5)
        								continue;
        							else if(childNeighbour.getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 1) {
        								NetEntity childNeighbourRegion = childNeighbour.getLastLinkAt(GateTypesIF.GT_SUR).getLinkedEntity();
        								if(!childNeighbourRegion.getID().equals(regionNode.getID()) && !realNeighbours.contains(childNeighbourRegion)) {
        									if(!realNeighbours.contains(childNeighbourRegion))
        										realNeighbours.add(childNeighbourRegion);
        								}
        							}
        						}
        					}
        					if(realNeighbours.isEmpty()) {
        						System.out.println("should be deleted");
        						deleteNodes.add(regionNode);
        					} else {
        						for(NetEntity realNeighbour : realNeighbours) {
        							if((!realNeighbour.getID().equals(regionNode.getID()))
        	    	    			   && (regionNode.getGate(GateTypesIF.GT_POR).getLinkTo(realNeighbour.getID(), SlotTypesIF.ST_GEN) == null)) {
        	    		    			linkEntities(regionNode.getID(), realNeighbour.getID(), GateTypesIF.GT_POR, 1.0, 1.0);
        	    		    			linkEntities(realNeighbour.getID(), regionNode.getID(), GateTypesIF.GT_POR, 1.0, 1.0);
        	    	    			}
        						}
        						if(!(regionNode.getGate(GateTypesIF.GT_POR).getNumberOfLinks() >= 1))
        							System.out.println("error: still no neighbours");
        					}
        				}
        			} else {
        				boolean inserted = false;
        				String insertRegion = null;
        				int oldNumber = 0;
        				for(NetEntity neighbourRegion : neighbourRegions) {
        					if(neighbourRegion.getGate(GateTypesIF.GT_SUB).getNumberOfLinks() >= ConstantValues.MAX_CLUSTER_SIZE)
        						continue;
        					if(neighboursInRegion.get(neighbourRegion).intValue() > oldNumber) {
        						oldNumber = neighboursInRegion.get(neighbourRegion).intValue();
        						insertRegion = neighbourRegion.getID();
        						inserted = true;
        					}
        				}
        				        				
        				if(!inserted && regionNode.getGate(GateTypesIF.GT_POR).getNumberOfLinks() >= 1) {
            				insertRegion = structure.createTopoNode("region"); 
            				linkEntities(allRegionsConcept.getID(), insertRegion, GateTypesIF.GT_CAT, 1.0, 1.0);
        				}
        				if(insertRegion != null) {				
            				linkEntities(insertRegion, regionNode.getID(), GateTypesIF.GT_SUB, 1.0, 1.0);
    	    				for(NetEntity neighbourRegion : neighbourRegions) {
    	    					if((!neighbourRegion.getID().equals(insertRegion))
    	    					   && (neighbourRegion.getGate(GateTypesIF.GT_POR).getLinkTo(insertRegion, SlotTypesIF.ST_GEN) == null)) {
    		    					linkEntities(neighbourRegion.getID(), insertRegion, GateTypesIF.GT_POR, 1.0, 1.0);
    		    					linkEntities(insertRegion, neighbourRegion.getID(), GateTypesIF.GT_POR, 1.0, 1.0);
    	    					}
    	    				}
        				} else {
        					System.out.println("error: region without neighbours");
        				}
        			}
        		}
        	}
    		*/
    		/*
    		for(int i = 0; i < allRegionsConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
        		NetEntity temp = allRegionsConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity();
        		if(temp.getGate(GateTypesIF.GT_SUB).getNumberOfLinks() <= 1)
        			if(!deleteNodes.contains(temp))
        				deleteNodes.add(temp);
        	}
        	*/
    		
    		/*
    		for(int i = 0; i < deleteNodes.size(); i++) {
    			structure.deleteEntity(deleteNodes.get(i).getID());
    		}
    		    		
    		//TODO connect regions not yet inserted
    		for(NetEntity region : nextLayer) {
        		ArrayList<NetEntity> neighbours = new ArrayList<NetEntity>(); 
        		if(region.getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 0) {
        			if(notInsertedRegions.containsKey(region)) {
        				if(notInsertedRegions.get(region).intValue() >= 3) {
        					NetEntity insertNeighbour = null;
	        				int neighbourCount = 1000;
	        				for(int i = 0; i < region.getGate(GateTypesIF.GT_POR).getNumberOfLinks(); i++) {
	        					NetEntity neighbour = region.getLink(GateTypesIF.GT_POR, i).getLinkedEntity();
	        					if(neighbour.getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 1) {
	        						NetEntity neighbourRegion = neighbour.getLastLinkAt(GateTypesIF.GT_SUR).getLinkedEntity();
	            					if(!neighbours.contains(neighbourRegion)) {
	            						neighbours.add(neighbourRegion);
		            					int tempCount = neighbourRegion.getGate(GateTypesIF.GT_SUB).getNumberOfLinks();
		            					
		            					if(tempCount < neighbourCount) {
		            						insertNeighbour = neighbourRegion;
		            						neighbourCount = tempCount;
		            					}
	            					}
	            				}
	        				}
	        				if(insertNeighbour != null) {
	    	    				linkEntities(insertNeighbour.getID(), region.getID(), GateTypesIF.GT_SUB, 1.0, 1.0);
	    	    				for(NetEntity neighbourRegion : neighbours) {
	    	    					if((!neighbourRegion.getID().equals(insertNeighbour.getID()))
	    	    					   && (neighbourRegion.getGate(GateTypesIF.GT_POR).getLinkTo(insertNeighbour.getID(), SlotTypesIF.ST_GEN) == null)) {
	    		    					linkEntities(neighbourRegion.getID(), insertNeighbour.getID(), GateTypesIF.GT_POR, 1.0, 1.0);
	    		    					linkEntities(insertNeighbour.getID(), neighbourRegion.getID(), GateTypesIF.GT_POR, 1.0, 1.0);
	    	    					}
	    	    				}
	    	    				if(notInsertedRegions.containsKey(region))
	    	    					notInsertedRegions.remove(region);	
	        				} else {
	        					System.out.println("hregion not inserted");
	        				}
        				} else {
        					int temp = notInsertedRegions.get(region).intValue() + 1;
        					notInsertedRegions.remove(region);
        					notInsertedRegions.put(region, new Integer(temp));
        				}
        			} else {
        				notInsertedRegions.put(region, new Integer(0));
        			}
        		} else if(notInsertedRegions.containsKey(region)) {
        			notInsertedRegions.remove(region);
        		}
        	}
        	// end inserting
    	   	
        	toDelete = new ArrayList<NetEntity>();
        	for(int i = 0; i < allRegionsConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
        		NetEntity temp = allRegionsConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity();
        		if(temp.getGate(GateTypesIF.GT_SUB).getNumberOfLinks() <= 1)
        			toDelete.add(temp);
        	}
        	for(int j = 0; j < toDelete.size(); j++) {
        		try {
        			structure.deleteEntity(toDelete.get(j).getID());
        		} catch (NetIntegrityException e) {
        			e.printStackTrace();
                }
        	}
        	toDelete.clear();
    		
    		ArrayList<NetEntity> tempList = new ArrayList<NetEntity>();
    		for(NetEntity region : nextLayer) {
    			if(region.getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 1) {
    				NetEntity temp = region.getLastLinkAt(GateTypesIF.GT_SUR).getLinkedEntity();
    				if(!tempList.contains(temp))
    					tempList.add(temp);
    			}
    		}
    		if(tempList.isEmpty()) {
    			stop = true;
    		} else {
    			nextLayer.clear();
    			nextLayer = tempList;
    		}
    		*/
    		
    		nextLayer = potentialNextLayer;
    	}
    	
    	for(int j = 0; j < allRegionsConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); j++) {
    		if(allRegionsConcept.getLink(GateTypesIF.GT_CAT, j).getLinkedEntity().getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 0) {
    			NetEntity fail = allRegionsConcept.getLink(GateTypesIF.GT_CAT, j).getLinkedEntity();
    			if(fail.getGate(GateTypesIF.GT_POR).getNumberOfLinks() > 0) {
    				ArrayList<NetEntity> neighbourRegions = new ArrayList<NetEntity>();     				
            		if(notInsertedRegions.containsKey(fail)) {
            			if(notInsertedRegions.get(fail).intValue() > 2) {
            				NetEntity insertNeighbour = null;
    	        			int neighbourCount = 1000;
    	        			for(int i = 0; i < fail.getGate(GateTypesIF.GT_POR).getNumberOfLinks(); i++) {
    	        				NetEntity neighbour = fail.getLink(GateTypesIF.GT_POR, i).getLinkedEntity();
    	        				if(neighbour.getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 1) {
    	        					NetEntity neighbourRegion = neighbour.getLastLinkAt(GateTypesIF.GT_SUR).getLinkedEntity();
    	            				if(!neighbourRegions.contains(neighbourRegion)) {
    	            					neighbourRegions.add(neighbourRegion);
    		            				int tempCount = neighbourRegion.getGate(GateTypesIF.GT_SUB).getNumberOfLinks();
    		            				
    		            				if(tempCount < neighbourCount) {
    		            					insertNeighbour = neighbourRegion;
    		            					neighbourCount = tempCount;
    		            				}
    	            				}
    	            			}
    	        			}
    	        			if(insertNeighbour != null) {
    	    	    			linkEntities(insertNeighbour.getID(), fail.getID(), GateTypesIF.GT_SUB, 1.0, 1.0);
    	    	    			for(NetEntity neighbourRegion : neighbourRegions) {
    	    	    				if((!neighbourRegion.getID().equals(insertNeighbour.getID()))
    	    	    				   && (neighbourRegion.getGate(GateTypesIF.GT_POR).getLinkTo(insertNeighbour.getID(), SlotTypesIF.ST_GEN) == null)) {
    	    		   					linkEntities(neighbourRegion.getID(), insertNeighbour.getID(), GateTypesIF.GT_POR, 1.0, 1.0);
    	    		   					linkEntities(insertNeighbour.getID(), neighbourRegion.getID(), GateTypesIF.GT_POR, 1.0, 1.0);
    	    	    				}
    	    	    			}
    	    	   				if(notInsertedRegions.containsKey(fail))
    	    	    				notInsertedRegions.remove(fail);	
    	        			} else {
    	        				System.err.println("fail-region not inserted");
    	        			}
            			} else {
            				int temp = notInsertedRegions.get(fail).intValue() + 1;
            				notInsertedRegions.remove(fail);
            				notInsertedRegions.put(fail, new Integer(temp));
            			}
            		} else {
            			notInsertedRegions.put(fail, new Integer(0));
            		}
            	} else if(notInsertedRegions.containsKey(fail)) {
            		notInsertedRegions.remove(fail);
            	}
            }   		
    	}
    	
    	
    	System.out.println("number of regions " + allRegionsConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks());
    	int roots = 0;
    	for(int i = 0; i < allRegionsConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
    		if(allRegionsConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity().getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 0)
    			roots++;
    	}
    	System.out.println("number of rootNodes " + roots);
    }
    
    private ArrayList<NetEntity> createHierarchyForLayer(ArrayList<NetEntity> layer) throws NetIntegrityException {
    	for(NetEntity hnode : layer) {
    		if(hnode.getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 1)
    			continue;
    		
    		ArrayList<NetEntity> neighbours = new ArrayList<NetEntity>();
    		HashMap<NetEntity, Integer> neighbourRegions = new HashMap<NetEntity, Integer>();
    		for(int i = 0; i < hnode.getGate(GateTypesIF.GT_POR).getNumberOfLinks(); i++) {
    			NetEntity neighbour = hnode.getLink(GateTypesIF.GT_POR, i).getLinkedEntity();   			
    			neighbours.add(neighbour);
    			
    			if(neighbour.getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 1) {
    				NetEntity neighbourRegion = neighbour.getFirstLinkAt(GateTypesIF.GT_SUR).getLinkedEntity();
					if(!neighbourRegions.containsKey(neighbourRegions)) {
						neighbourRegions.put(neighbourRegion, new Integer(1));
					} else {
						Integer inc = new Integer(neighbourRegions.get(neighbourRegion).intValue() + 1);
						neighbourRegions.remove(neighbourRegion);
						neighbourRegions.put(neighbourRegion, inc);
    				}
    			}
    		}
    		int highestChildCount = 0;
    		NetEntity insertRegion = null;
    		for(NetEntity region : neighbourRegions.keySet()) {
    			if(region.getGate(GateTypesIF.GT_SUB).getNumberOfLinks() < ConstantValues.MAX_CLUSTER_SIZE) {
	    			if(neighbourRegions.get(region).intValue() > highestChildCount) {
	    				highestChildCount = neighbourRegions.get(region).intValue();
	    				insertRegion = region;
	    			} else if(neighbourRegions.get(region).intValue() == highestChildCount && RandomGenerator.generator.nextBoolean()) {
	    				insertRegion = region;
	    			}
    			}
    		}
    		if(insertRegion == null) {
    			String newRegionNode = structure.createTopoNode("region");
				linkEntities(allRegionsConcept.getID(), newRegionNode, GateTypesIF.GT_CAT, 1.0, 1.0);
				linkEntities(newRegionNode, hnode.getID(), GateTypesIF.GT_SUB, 1.0, 1.0);
				
				if(!neighbourRegions.isEmpty()) {
					for(NetEntity region : neighbourRegions.keySet()) {
						NetEntity neighbour = structure.findEntity(newRegionNode);
						if(neighbour.getGate(GateTypesIF.GT_POR).getLinkTo(region.getID(), SlotTypesIF.ST_GEN) == null) {
		    				linkEntities(neighbour.getID(), region.getID(), GateTypesIF.GT_POR, 1.0, 1.0);
		    			}
		    			if(region.getGate(GateTypesIF.GT_POR).getLinkTo(neighbour.getID(), SlotTypesIF.ST_GEN) == null) {
							linkEntities(region.getID(), neighbour.getID(), GateTypesIF.GT_POR, 1.0, 1.0);
		    			}
					}
				}
    		} else {
				linkEntities(insertRegion.getID(), hnode.getID(), GateTypesIF.GT_SUB, 1.0, 1.0);
				
				if(!neighbourRegions.isEmpty()) {
					for(NetEntity region : neighbourRegions.keySet()) {
						if(!insertRegion.getID().equals(region.getID())) {
							if(insertRegion.getGate(GateTypesIF.GT_POR).getLinkTo(region.getID(), SlotTypesIF.ST_GEN) == null) {
			    				linkEntities(insertRegion.getID(), region.getID(), GateTypesIF.GT_POR, 1.0, 1.0);
			    			}
			    			if(region.getGate(GateTypesIF.GT_POR).getLinkTo(insertRegion.getID(), SlotTypesIF.ST_GEN) == null) {
								linkEntities(region.getID(), insertRegion.getID(), GateTypesIF.GT_POR, 1.0, 1.0);
			    			}
						}
					}
				}
    		}
    	}  	
    	
    	// delete nodes and create next layer
    	ArrayList<NetEntity> nextLayer = new ArrayList<NetEntity>();
    	for(NetEntity region : layer) {
			if(region.getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 1) {
				NetEntity temp = region.getFirstLinkAt(GateTypesIF.GT_SUR).getLinkedEntity();
				if(temp.getGate(GateTypesIF.GT_SUB).getNumberOfLinks() == 1) {
					try {
		    			structure.deleteEntity(temp.getID());
		    		} catch (NetIntegrityException e) {
		    			e.printStackTrace();
		            }
				} else if(!nextLayer.contains(temp))
					nextLayer.add(temp);
			}
		}
    	return nextLayer;
    }
    
    private void correctNeighbours() throws NetIntegrityException {
    	for(Region region : regions) {
    		NetEntity node = region.getNode();
    		Position regionPos = NodeFunctions.getPosition(node);
    		
			WorldVector normal = new WorldVector(ConstantValues.REGIONMINDISTANCE, 0.0, 0.0);
	    	normal.rotate(orientation.getIncomingActivation() * 360.0);
	
	    	for(int i = 0; i <= 15; i++) {
	    		switch(i) {
	    			default: normal.rotate(45.0);
	    					 break;
	    		}
	    		Position testPosition = new Position(regionPos.getX() + normal.getX(), regionPos.getY() + normal.getY());
	    		Region nearest = null;
	    		if(testPosition != null) {
	    			nearest = getNearestRegion(testPosition, region);
	    			if(!(nearest.position.distance2D(testPosition) >= ConstantValues.REGIONMINDISTANCE - (ConstantValues.REGIONMINDISTANCE / 4))) {
	    				if(!nearest.equals(region)) {
	    					if(region != null) {
	    						if(region.getNode().getGate(GateTypesIF.GT_POR).getLinkTo(nearest.getNode().getID(), SlotTypesIF.ST_GEN) == null) {
	    							linkEntities(region.getNode().getID(), nearest.getNode().getID(), GateTypesIF.GT_POR, 1.0, 1.0);
	    						}
	    						if(nearest.getNode().getGate(GateTypesIF.GT_POR).getLinkTo(region.getNode().getID(), SlotTypesIF.ST_GEN) == null) {
	    							linkEntities(nearest.getNode().getID(), region.getNode().getID(), GateTypesIF.GT_POR, 1.0, 1.0);
	    						}
	    					}
	    				}
	    			}
	    		}
	    	}
    	}
    	
    	for(int i = 0; i < allRegionsConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
    		NetEntity hierarchy = allRegionsConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity();

    		ArrayList<NetEntity> neighbourRegions = new ArrayList<NetEntity>();
    		ArrayList<NetEntity> neighbours = new ArrayList<NetEntity>(); 
    		
    		for(int j = 0; j < hierarchy.getGate(GateTypesIF.GT_SUB).getNumberOfLinks(); j++) {
    			NetEntity child = hierarchy.getLink(GateTypesIF.GT_SUB, j).getLinkedEntity();
    			for(int k = 0; k < child.getGate(GateTypesIF.GT_POR).getNumberOfLinks(); k++) {
    				NetEntity neighbour = child.getLink(GateTypesIF.GT_POR, k).getLinkedEntity();
    				if(!neighbours.contains(neighbour))
    					neighbours.add(neighbour);
    			}
    		}
    		
    		for(NetEntity neighbour : neighbours) {
    			if(neighbour.getGate(GateTypesIF.GT_SUR).getNumberOfLinks() == 1) {
    				NetEntity neighbourParent = neighbour.getFirstLinkAt(GateTypesIF.GT_SUR).getLinkedEntity();
    				if(!neighbourParent.getID().equals(hierarchy.getID()) && !neighbourRegions.contains(neighbourParent))
    					neighbourRegions.add(neighbourParent);   				
    			}
    		}
    		
    		for(NetEntity neighbour : neighbourRegions) {
    			if(neighbour.getGate(GateTypesIF.GT_POR).getLinkTo(hierarchy.getID(), SlotTypesIF.ST_GEN) == null) {
    				linkEntities(neighbour.getID(), hierarchy.getID(), GateTypesIF.GT_POR, 1.0, 1.0);
    			}
    			if(hierarchy.getGate(GateTypesIF.GT_POR).getLinkTo(neighbour.getID(), SlotTypesIF.ST_GEN) == null) {
					linkEntities(hierarchy.getID(), neighbour.getID(), GateTypesIF.GT_POR, 1.0, 1.0);
    			}
    		}
    	}
    }
    
    private void collectRegions() {
    	if(rootNode != null) {
	    	for(int i = 0; i < rootNode.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
				Region temp = getRegionFromNode(rootNode.getLink(GateTypesIF.GT_CAT, i).getLinkedEntityID());
				regions.add(temp);
				if(temp.isUnknown())
					unknownRegions.add(temp);
			}
    	}
    }
    
    private List<Region> getRegionList(int type) {
    	switch(type) {
    		case Region.FOOD: return foodRegions;
    		case Region.WATER: return waterRegions;
    		case Region.HEALING: return healingRegions;
    		default: return null;
    	}
    }
    
    private void splitHierarchyRegion(NetEntity hierarchy) {
    	
    }
    
    private void moveRegion(Region region, Position to) {
    	moveRegion(region.getNode(), to);
    	region.position = to;
    }
    
    private void moveRegion(NetEntity region, Position to) {
    	if(region == null || to == null)
    		return;
    	
    	structure.changeLinkParameters(region.getLink(GateTypesIF.GT_SUB, 0), to.getX() / ConstantValues.WORLDMAXX, 1.0);
    	structure.changeLinkParameters(region.getLink(GateTypesIF.GT_SUB, 1), to.getY() / ConstantValues.WORLDMAXY, 1.0);
    }
}
