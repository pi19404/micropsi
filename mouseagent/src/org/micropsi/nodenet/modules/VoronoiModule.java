package org.micropsi.nodenet.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.micropsi.common.coordinates.Position;
import org.micropsi.common.coordinates.WorldVector;
import org.micropsi.comp.ConstantValues;
import org.micropsi.comp.NodeFunctions;
import org.micropsi.comp.agent.voronoi.*;
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

public class VoronoiModule extends AbstractNativeModuleImpl {
	// slots
	private static final int XCOORDINATE 	 = 11700;
	private static final int YCOORDINATE 	 = 11701;
	private static final int FOOD	         = 11702;
	private static final int WATER           = 11703;
	private static final int HEALING		 = 11704;
	private static final int HURTBYGROUND 	 = 11705;
	private static final int OBSTACLE 		 = 11706;
	private static final int PROTOCOL 		 = 11707;
	private static final int ORIENTATION     = 11708;
	private static final int PROTOCOLREGIONS = 11709;
	
	// gates
	private static final int ROOT 			 = 11700;
	private static final int UNKNOWNROOT	 = 11701;
	private static final int FOODCONCEPT	 = 11702;
	private static final int WATERCONCEPT 	 = 11703;
	private static final int HEALINGCONCEPT  = 11704;
	
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
	
	private String rootNode = null;
	private String unknownNode = null;
	private List<Region> regions = null;
	private List<Region> unknownRegions = null;
	private HashMap<Pnt, Region> points = null;
	
	private boolean firsttime = true;
	private Position lastPosition = null;
	
	private boolean debug = true;
	
	private DelaunayTriangulation dt = null;
	private List<Region> toCleanUpList;
	HashMap<Pnt, ArrayList<Pnt>> pointHash;
	private int counter = 0;
	private int nodecounter = 0;
	private int maxCounter = 0;
	
	private final int[] slotTypes = {
	        XCOORDINATE,
	        YCOORDINATE,
	        FOOD,
	        WATER,
	        HEALING,
	        OBSTACLE,
	        HURTBYGROUND,
	        PROTOCOL, 
	        ORIENTATION,
	        PROTOCOLREGIONS
		};
	
	private final int[] gateTypes = {
			ROOT,
			UNKNOWNROOT,
			FOODCONCEPT,
			WATERCONCEPT,
			HEALINGCONCEPT
	};
	
	public VoronoiModule() {	
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "regions";

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
			    	case PROTOCOLREGIONS: return "stop-protocol-regions";
			    	default:           return null;
			    }
			}

			public String gateType(int type) {
				switch(type) {
					case ROOT: return "rootnode";
					case UNKNOWNROOT: return "unknownnode";
					case FOODCONCEPT: return "food-concept";
					case WATERCONCEPT: return "water-concept";
					case HEALINGCONCEPT: return "healing-concept";
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
			
			Link l = manipulator.getGate(ROOT).getLinkAt(0);
			if(l == null)
				logger.warn("regions has no link to rootNodeConcept");
			else
				rootNode = l.getLinkedEntityID();
			
			l = manipulator.getGate(UNKNOWNROOT).getLinkAt(0);
			if(l == null)
				logger.warn("regions has no link to unknownRootNodeConcept");
			else
				unknownNode = l.getLinkedEntityID();
			
			regions = new ArrayList<Region>();
			unknownRegions = new ArrayList<Region>();
			toCleanUpList = new ArrayList<Region>();
			pointHash = new HashMap<Pnt, ArrayList<Pnt>>();
			points = new HashMap<Pnt, Region>();
			
			resetCleanUp();
			
			collectRegions();
			
			if(structure.findEntity(rootNode).getGate(GateTypesIF.GT_CAT).getNumberOfLinks() == 0) {
				System.out.println("creating bordernodes");
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
			
			/*
			test();
			System.out.println("test");
			*/
		}
        
        if(protocolRegions.getIncomingActivation() > 0.5)
        	return;
        
        // TODO testing
        long time = System.currentTimeMillis();
        cleanUpRegions();
        long timediff = System.currentTimeMillis() - time;
        if(timediff > 20)
        	System.out.println("cleaning took " + timediff);
        
        /*
        if(nodecounter == 0)
        	System.out.println("nodes from regions: " + (structure.findEntity(rootNode).getGate(GateTypesIF.GT_SUB).getNumberOfLinks() * 4));
        nodecounter = (nodecounter + 1) % 400;
        */
        
        double xposition = x.getIncomingActivation();
        double yposition = y.getIncomingActivation();
        Position currentPosition = NodeFunctions.getPosition(x.getIncomingActivation(), y.getIncomingActivation());
        
        if(xposition == 0.0 && yposition == 0.0)
        	return;

        /*
        if(obstacle.getIncomingActivation() > 0.5) {
        	boolean insert = true;
        	for(int i = 0; i < regions.size(); i++) {
        		if(regions.get(i).isType(Region.IMPASSABLE) && regions.get(i).position.distance2D(currentPosition) < ConstantValues.MINDISTANCE) {
        			insert = false;
        			break;
        		}
        	}
        	if(insert) {
        		String newRegion = createRegionNodeAndUnknownSurrounding(currentPosition, Region.IMPASSABLE);
        		if(newRegion != null)
        			connectRegionNode(newRegion);    		
        	}
        } else if(damage.getIncomingActivation() > 0.5) {
        	boolean insert = true;
        	for(int i = 0; i < regions.size(); i++) {
        		if(regions.get(i).isType(Region.DAMAGE) && regions.get(i).position.distance2D(currentPosition) < ConstantValues.MINDISTANCE) {
        			insert = false;
        			break;
        		}
        	}
        	if(insert) {
        		String newRegion = createRegionNodeAndUnknownSurrounding(currentPosition, Region.DAMAGE);
        		if(newRegion != null)
        			connectRegionNode(newRegion);    		
        	}
        } else if(food.getIncomingActivation() > 0.5) {
        	boolean insert = true;
        	for(int i = 0; i < regions.size(); i++) {
        		if(regions.get(i).isType(Region.FOOD) && regions.get(i).position.distance2D(currentPosition) < ConstantValues.MINDISTANCE) {
        			insert = false;
        			break;
        		}
        	}
        	if(insert) {
        		String newRegion = createRegionNodeAndUnknownSurrounding(currentPosition, Region.FOOD);
        		if(newRegion != null)
        			connectRegionNode(newRegion);    		
        	}
        } else if(water.getIncomingActivation() > 0.5) {
        	boolean insert = true;
        	for(int i = 0; i < regions.size(); i++) {
        		if(regions.get(i).isType(Region.WATER) && regions.get(i).position.distance2D(currentPosition) < ConstantValues.MINDISTANCE) {
        			insert = false;
        			break;
        		}
        	}
        	if(insert) {
        		String newRegion = createRegionNodeAndUnknownSurrounding(currentPosition, Region.WATER);
        		if(newRegion != null)
        			connectRegionNode(newRegion);    		
        	}
        } else if(healing.getIncomingActivation() > 0.5) {
        	boolean insert = true;
        	for(int i = 0; i < regions.size(); i++) {
        		if(regions.get(i).isType(Region.HEALING) && regions.get(i).position.distance2D(currentPosition) < ConstantValues.MINDISTANCE) {
        			insert = false;
        			break;
        		}
        	}
        	if(insert) {
        		String newRegion = createRegionNodeAndUnknownSurrounding(currentPosition, Region.HEALING);
        		if(newRegion != null)
        			connectRegionNode(newRegion);    		
        	}
        } else */if(lastPosition != null && lastPosition.distance2D(currentPosition) > ConstantValues.REGIONMINDISTANCE / 2) {
        	String newRegion = createRegionNodeAndUnknownSurrounding(currentPosition);
        	if(newRegion != null)
        		connectRegionNode(newRegion); 
        } else if (lastPosition == null) {
        	String newRegion = createRegionNodeAndUnknownSurrounding(currentPosition);
        	if(newRegion != null)
        		connectRegionNode(newRegion); 
        }
    }
    
    /**
     * creates a neutral region node for the position
     * @param position
     * @return the ID of the created region node
     */
    private String createRegionNode(Position position) throws NetIntegrityException {
    	String newNode = structure.createConceptNode("region " + position.getX() + ":" + position.getY());
        String newX = structure.createConceptNode("x");
        String newY = structure.createConceptNode("y");
        String exploration = structure.createConceptNode("exploration");
        /*
        String food = structure.createConceptNode("food");
        String water = structure.createConceptNode("water");
        String healing = structure.createConceptNode("healing");
        String damage = structure.createConceptNode("damage");
        String impassable = structure.createConceptNode("impassable");
        */
        linkEntities(newNode, newX, GateTypesIF.GT_SUB, position.getX() / ConstantValues.WORLDMAXX, position.getX() / ConstantValues.WORLDMAXX);
        linkEntities(newNode, newY, GateTypesIF.GT_SUB, position.getY() / ConstantValues.WORLDMAXY, position.getY() / ConstantValues.WORLDMAXY);
        linkEntities(newNode, exploration, GateTypesIF.GT_SUB, 1.0, 1.0);
        /*
        linkEntities(newNode, food, GateTypesIF.GT_SUB, 0.0, 1.0);
        linkEntities(newNode, water, GateTypesIF.GT_SUB, 0.0, 1.0);
        linkEntities(newNode, healing, GateTypesIF.GT_SUB, 0.0, 1.0);
        linkEntities(newNode, damage, GateTypesIF.GT_SUB, 0.0, 1.0);
        linkEntities(newNode, impassable, GateTypesIF.GT_SUB, 0.0, 1.0);
        */
        
        return newNode;
    }
    
    /**
     * creates a region node that is tagged as unkown territory
     * @param position
     * @return the ID of the created region node
     * @throws NetIntegrityException
     */
    private String createUnknownRegionNode(Position position) throws NetIntegrityException {
    	String newNode = structure.createConceptNode("region " + position.getX() + ":" + position.getY());
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
        
        return newNode;
    }
    
    private String createRegionNode(Position position, int type) throws NetIntegrityException {
    	String toReturn = createRegionNode(position);
    	structure.changeLinkParameters(structure.findEntity(toReturn).getLink(GateTypesIF.GT_SUB, type + 3), 1.0, 1.0);
    	
    	return toReturn;
    }
    
    private String createRegionNodeAndUnknownSurrounding(Position position, int type) throws NetIntegrityException {
    	lastPosition = position;
    	
    	Position testPosition = null;
    	Position centerPosition = position;
    	Region nearest;
    	String toReturn = null;
    	
    	Region region = null;
    	nearest = getNearestRegion(position, null);
    	
    	if(nearest.isUnknown()) {
    		if(nearest.position.distance2D(position) < ConstantValues.REGIONMINDISTANCE) {
    			structure.changeLinkParameters(nearest.getNode().getLink(GateTypesIF.GT_SUB, 2), 1.0, 1.0);
    			structure.changeLinkParameters(nearest.getNode().getLink(GateTypesIF.GT_SUB, type + 3), 1.0, 1.0);
    			removeFromUnknown(nearest);
    			region = nearest;
    			centerPosition = nearest.position;
    		} else {
    			toReturn = createRegionNode(position);
    		}
    	} else {
    		structure.changeLinkParameters(nearest.getNode().getLink(GateTypesIF.GT_SUB, type + 3), 1.0, 1.0);
    	}
    	
    	
    	if(nearest.position.distance2D(position) <= ConstantValues.REGIONMINDISTANCE) {
    		if(unknownRegions.contains(nearest)) {
    			structure.changeLinkParameters(nearest.getNode().getLink(GateTypesIF.GT_SUB, 2), 1.0, 1.0);
    			structure.changeLinkParameters(nearest.getNode().getLink(GateTypesIF.GT_SUB, type + 3), 1.0, 1.0);
    		} else {
    			structure.changeLinkParameters(nearest.getNode().getLink(GateTypesIF.GT_SUB, type + 3), 1.0, 1.0);
    		}
    		removeFromUnknown(nearest);
   			region = nearest;
   			centerPosition = nearest.position;
    	} else {
    		toReturn = createRegionNode(position, type);
    	}
    	

    	WorldVector normal = new WorldVector(ConstantValues.REGIONMINDISTANCE, 0.0, 0.0);
    	normal.rotate(orientation.getIncomingActivation() * 360.0);
    	
    	for(int i = 0; i <= 7; i++) {
    		switch(i) {
    			case 0: break;
    			default: normal.rotate(90.0);
				 	     break;
    		}
    		testPosition = new Position(centerPosition.getX() + normal.getX(), centerPosition.getY() + normal.getY());
    		if(testPosition != null) {
    			nearest = getNearestRegion(testPosition, region);
    			if(nearest.position.distance2D(testPosition) >= ConstantValues.REGIONMINDISTANCE) {
    				if(unknownRegions.contains(nearest) || nearest.equals(region)) {
    					String nodeID = createUnknownRegionNode(testPosition);
    					connectRegionNode(nodeID);
    				}
    			}
    		}
    	}
    	
    	return toReturn;
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
    			if(nearest.position.distance2D(testPosition) >= ConstantValues.REGIONMINDISTANCE - (ConstantValues.REGIONMINDISTANCE / 5)) {
    				if(unknownRegions.contains(nearest) || nearest.equals(region)) {
    					String nodeID = createUnknownRegionNode(testPosition);
    					connectRegionNode(nodeID);
    				}
    			}
    		}
    	}
    	
    	return toReturn;
    }
    
    /**
     * adds a region node with the ID node by linking it to the
     * rootNode-concept
     * @param node
     * @throws NetIntegrityException
     */
    private void connectRegionNode(String node) throws NetIntegrityException {
    	linkEntities(rootNode, node, GateTypesIF.GT_CAT, 1.0, 1.0);
    	Region toAdd = getRegionFromNode(node);
    	if(!regions.contains(toAdd))
    		regions.add(toAdd);
    	if(toAdd.isUnknown()) {
    		unknownRegions.add(toAdd);
    		linkEntities(unknownNode, node, GateTypesIF.GT_CAT, 1.0, 1.0);
    	}
    }
    
    /*
    private void deleteRegionNode(String nodeID) throws NetIntegrityException {
    	for(int i = 0; i < structure.findEntity(nodeID).getGate(GateTypesIF.GT_SUB).getNumberOfLinks(); i++) {
    		structure.deleteEntity(structure.findEntity(nodeID).getLink(GateTypesIF.GT_SUB, i).getLinkedEntityID());
    	}
    	structure.deleteEntity(nodeID);
    }
    */
    
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
    
    private void mergeNodes(Region region1, Region region2, Position position) throws NetIntegrityException {
    	structure.changeLinkParameters(region1.getNode().getLink(GateTypesIF.GT_SUB, 0), position.getX() / ConstantValues.WORLDMAXX, 1.0);
    	structure.changeLinkParameters(region1.getNode().getLink(GateTypesIF.GT_SUB, 1), position.getY() / ConstantValues.WORLDMAXY, 1.0);
    	structure.changeLinkParameters(region1.getNode().getLink(GateTypesIF.GT_SUB, 2), max(region1.getNode().getLink(GateTypesIF.GT_SUB, 2).getWeight(), region2.getNode().getLink(GateTypesIF.GT_SUB, 2).getWeight()), 1.0);
    	structure.changeLinkParameters(region1.getNode().getLink(GateTypesIF.GT_SUB, 3), max(region1.getNode().getLink(GateTypesIF.GT_SUB, 3).getWeight(), region2.getNode().getLink(GateTypesIF.GT_SUB, 3).getWeight()), 1.0);
    	structure.changeLinkParameters(region1.getNode().getLink(GateTypesIF.GT_SUB, 4), max(region1.getNode().getLink(GateTypesIF.GT_SUB, 4).getWeight(), region2.getNode().getLink(GateTypesIF.GT_SUB, 4).getWeight()), 1.0);
    	structure.changeLinkParameters(region1.getNode().getLink(GateTypesIF.GT_SUB, 5), max(region1.getNode().getLink(GateTypesIF.GT_SUB, 5).getWeight(), region2.getNode().getLink(GateTypesIF.GT_SUB, 5).getWeight()), 1.0);
    	structure.changeLinkParameters(region1.getNode().getLink(GateTypesIF.GT_SUB, 6), max(region1.getNode().getLink(GateTypesIF.GT_SUB, 6).getWeight(), region2.getNode().getLink(GateTypesIF.GT_SUB, 6).getWeight()), 1.0);
    	structure.changeLinkParameters(region1.getNode().getLink(GateTypesIF.GT_SUB, 7), max(region1.getNode().getLink(GateTypesIF.GT_SUB, 7).getWeight(), region2.getNode().getLink(GateTypesIF.GT_SUB, 7).getWeight()), 1.0);
    	
    	deleteRegionNode(region2.getNode().getID());
    	if(regions.contains(region2)) {
    		regions.remove(region2);
    	}
    }
    
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
    	}  else if(type == GateTypesIF.GT_CAT) {
    		structure.createLink(from, GateTypesIF.GT_CAT, to, SlotTypesIF.ST_GEN, weight, 1.0);
    		structure.createLink(to, GateTypesIF.GT_EXP, from, SlotTypesIF.ST_GEN, backWeight, 1.0);
    	} else if(type == GateTypesIF.GT_SYM) {
    		structure.createLink(from, GateTypesIF.GT_SYM, to, SlotTypesIF.ST_GEN, weight, 1.0);
    		structure.createLink(to, GateTypesIF.GT_REF, from, SlotTypesIF.ST_GEN, backWeight, 1.0);
    	}
    }
    
    private double max(double value1, double value2) {
    	return (value1 > value2) ? value1 : value2;
    }
    
    /*
    private Region getRegionFromNode(NetEntity region) {
    	int type = 0;
        if(region.getLink(GateTypesIF.GT_SUB, 3).getWeight() > 0.5)
        	type += Region.FOOD;
        if(region.getLink(GateTypesIF.GT_SUB, 4).getWeight() > 0.5)
        	type += Region.WATER;
        if(region.getLink(GateTypesIF.GT_SUB, 5).getWeight() > 0.5)
        	type += Region.HEALING;
        if(region.getLink(GateTypesIF.GT_SUB, 6).getWeight() > 0.5)
        	type += Region.DAMAGE;
        if(region.getLink(GateTypesIF.GT_SUB, 7).getWeight() > 0.5)
        	type += Region.IMPASSABLE;
        
        Region toReturn = new Region(NodeFunctions.getPosition(region), type);
        toReturn.setNode((ConceptNode)region);
        return toReturn;
    }
    */
    
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
    	toReturn.setNode((ConceptNode)structure.findEntity(nodeID));
    	
    	return toReturn;
    }
    
    /**
     * fills global variables with the region informations 
     * saved in the node net
     */
    private void collectRegions() throws NetIntegrityException {
    	regions.clear();
    	unknownRegions.clear();
    	if(rootNode != null)
			for(int i = 0; i < structure.findEntity(rootNode).getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
				Region temp = getRegionFromNode(structure.findEntity(rootNode).getLink(GateTypesIF.GT_CAT, i).getLinkedEntityID());
				regions.add(temp);
				if(temp.isUnknown())
					unknownRegions.add(temp);
			}
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
    	
    	/*
    	double diff = System.currentTimeMillis() - time;
    	if(diff > 0)
    		System.out.println(System.currentTimeMillis() - time);
    	*/
    	
    	/*
    	time = System.currentTimeMillis();
    	for(Iterator<Link> links = structure.findEntity(rootNode).getGate(GateTypesIF.GT_SUB).getLinks(); links.hasNext();) {
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
    	if(diff > 0)
    		System.out.println(System.currentTimeMillis() - time);
    	*/
    	
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
    
    private void cleanUpRegions() throws NetIntegrityException {
    	if(counter == 0) {
    		resetCleanUp();
    		maxCounter = regions.size();
    		for(Region region : regions) {
    			toCleanUpList.add(region);
    		}
    		Simplex<Pnt> tri = new Simplex<Pnt>(new Pnt(-10000.0, -10000.0), new Pnt(10000.0, -10000.0), new Pnt(0.0, 10000.0));
			dt = new DelaunayTriangulation(tri);
			
			Pnt pnt = new Pnt(toCleanUpList.get(counter).position);
			points.put(pnt, toCleanUpList.get(counter));
			
			dt.delaunayPlace(pnt);
			
			counter++;
    	} else if(counter < maxCounter) {
    		Pnt pnt = new Pnt(toCleanUpList.get(counter).position);
			points.put(pnt, toCleanUpList.get(counter));
			
			dt.delaunayPlace(pnt);
			
			counter++;
    	} else if (counter == maxCounter){
    		pointHash.clear();
    		ArrayList<Pnt> pointArray = new ArrayList<Pnt>();
    		for(Simplex<Pnt> simplex : dt) {
    			pointArray.clear();
    			for(Pnt pnt : simplex) {
    				pointArray.add(pnt);
    			}
    			for(int i = 0; i < pointArray.size(); i++) {
    				Pnt localPnt = pointArray.get(i);
    				if(!pointHash.containsKey(localPnt)) {
    					pointHash.put(localPnt, new ArrayList<Pnt>());
    				}
    				for(int j = 0; j < pointArray.size(); j++) {
    					if(j != i && !pointHash.get(localPnt).contains(pointArray.get(j))) {
    						pointHash.get(localPnt).add(pointArray.get(j));
    					}
    				}
    			}
    		}
    		counter++;
    	} else {
    		for(Pnt pnt : pointHash.keySet()) {
    			Region tempRegion = points.get(pnt);
    			if(tempRegion != null) {
    				ArrayList tempPnts = pointHash.get(pnt);
    				for(int i = 0; i < tempPnts.size(); i++) {
    					if(!tempRegion.equalsType(points.get(tempPnts.get(i)))) {
    						boolean cleared = toCleanUpList.remove(tempRegion);
    						break;
    					}
    				}
    			}
    		}
    		for(Region region : toCleanUpList) {
    			boolean toDelete = true;
    			for(Region testRegion : regions) {
    				if(!region.equalsType(testRegion)) {
    					if(region.position.distance2D(testRegion.position) < Math.sqrt(2*ConstantValues.REGIONMINDISTANCE*ConstantValues.REGIONMINDISTANCE)) {
    						toDelete = false;
    						break;
    					}
    				}
    			}
    			if(toDelete) {
    				boolean cleared = regions.remove(region);
    				deleteRegionNode(region.getNode().getID());
    			}
    		}
    		
    		counter = 0;
    	}
    }
    
    private void resetCleanUp() {
    	toCleanUpList.clear();
    	pointHash.clear();
    	counter = 0;
    }
    
    private void test() throws NetIntegrityException {
    	String testRegion;
    	testRegion = createRegionNodeAndUnknownSurrounding(new Position(40, 40));
    	if(testRegion != null)
    		connectRegionNode(testRegion);
    	testRegion = createRegionNodeAndUnknownSurrounding(new Position(40 + ConstantValues.REGIONMINDISTANCE, 40));
    	if(testRegion != null)
    		connectRegionNode(testRegion);
    	testRegion = createRegionNodeAndUnknownSurrounding(new Position(40 + 2 * ConstantValues.REGIONMINDISTANCE, 40));
    	if(testRegion != null)
    		connectRegionNode(testRegion);
    	testRegion = createRegionNodeAndUnknownSurrounding(new Position(40 - 2 * ConstantValues.REGIONMINDISTANCE, 40));
    	if(testRegion != null)
    		connectRegionNode(testRegion);
    	testRegion = createRegionNodeAndUnknownSurrounding(new Position(40, 40 + 2 * ConstantValues.REGIONMINDISTANCE));
    	if(testRegion != null)
    		connectRegionNode(testRegion);
    	testRegion = createRegionNodeAndUnknownSurrounding(new Position(40, 40 - 2 * ConstantValues.REGIONMINDISTANCE));
    	if(testRegion != null)
    		connectRegionNode(testRegion);
    	testRegion = createRegionNodeAndUnknownSurrounding(new Position(40 - ConstantValues.REGIONMINDISTANCE, 40));
    	if(testRegion != null)
    		connectRegionNode(testRegion);
    	testRegion = createRegionNodeAndUnknownSurrounding(new Position(40 + ConstantValues.REGIONMINDISTANCE, 40 + ConstantValues.REGIONMINDISTANCE));
    	if(testRegion != null)
    		connectRegionNode(testRegion);
    	testRegion = createRegionNodeAndUnknownSurrounding(new Position(40 - ConstantValues.REGIONMINDISTANCE, 40 + ConstantValues.REGIONMINDISTANCE));
    	if(testRegion != null)
    		connectRegionNode(testRegion);
    	testRegion = createRegionNodeAndUnknownSurrounding(new Position(40 + ConstantValues.REGIONMINDISTANCE, 40 - ConstantValues.REGIONMINDISTANCE));
    	if(testRegion != null)
    		connectRegionNode(testRegion);
    	testRegion = createRegionNodeAndUnknownSurrounding(new Position(40 - ConstantValues.REGIONMINDISTANCE, 40 - ConstantValues.REGIONMINDISTANCE));
    	if(testRegion != null)
    		connectRegionNode(testRegion);
    	testRegion = createRegionNodeAndUnknownSurrounding(new Position(40, 40 - ConstantValues.REGIONMINDISTANCE));
    	if(testRegion != null)
    		connectRegionNode(testRegion);
    	testRegion = createRegionNodeAndUnknownSurrounding(new Position(40, 40 + ConstantValues.REGIONMINDISTANCE));
    	if(testRegion != null)
    		connectRegionNode(testRegion);
    }      
    
    private Position[] navigate(Position startPosition, Position goalPosition) throws NetIntegrityException {
    	long time1 = System.currentTimeMillis(); 
    	
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
    	
    	while(!startReached || nodes.size() == 0) {
    		ArrayList<Position> newNodes = new ArrayList<Position>();
    		
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
    				
        			testPosition = new Position(x,y);        			
        			
        			Region nearest = getNearestRegion(testPosition, null);
        			if(nearest.isUnknown())
        				continue;
        			
        			
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
    	Position[] posArray = new Position[way.size()];
    	for(int i = 0; i < way.size(); i++)
    		posArray[i] = way.get(i);
    	System.out.println("2: " + (System.currentTimeMillis() - time1));
    	
    	return posArray;
    }
}
