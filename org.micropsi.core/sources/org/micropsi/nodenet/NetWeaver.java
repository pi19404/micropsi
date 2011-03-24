/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/NetWeaver.java,v 1.9 2006/06/27 19:37:02 rvuine Exp $ 
 */
package org.micropsi.nodenet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.progress.ProgressMonitorIF;


public class NetWeaver {
	
	/**
	 * PM_PRESERVE_ALL tries to preserve all links. If a linked entity is not
	 * present, a warning will be issued and a link will not be created. If a
	 * linked entity does not have the slot the link would point to, no link
	 * will be created and a warning will be issued.
	 */
	public static final int PM_PRESERVE_ALL = 0;
	
	/**
	 * PM_PRESERVE_INTER preserves only the links between the given set of entities.
	 */
	public static final int PM_PRESERVE_INTER = 1;
	
	/**
	 * PM_PRESERVE_NONE does not preserve any links
	 */
	public static final int PM_PRESERVE_NONE = 2;
	
	/**
	 * Inserts an entity from any net into the passed net. The method will create
	 * a new ID for the entity. Hence you can use this method to "clone" an entity
	 * within the same net.
	 * @param net The net where the entity is to be inserted.
	 * @param entity The entity to be inserted.
	 * @param space The space where the entity is to be inserted
	 * @param preserveMode Link preservation mode. One of the values PM_PRESERVE_ALL\nPM_PRESERVE_NONE
	 * @param cloneMappings Pass a hashmap here to be filled with the id mappings. Can be null
	 * @param monitor a progress monitor, can be null 
	 * @return A List of Strings with warnings. Will not be null. 
	 * @throws NetIntegrityException if the target space does not exist.
	 * @throws MicropsiException if your net is messed up in some unexpected way
	 */
	public static List<String> insertEntity(NetFacadeIF net, NetEntity entity, String space, int preserveMode, HashMap<String, String> cloneMappings, ProgressMonitorIF monitor) throws NetIntegrityException,MicropsiException {
		ArrayList<NetEntity> l = new ArrayList<NetEntity>();
		l.add(entity);
		return insertEntities(net, l, space, preserveMode, cloneMappings,monitor);
	}

	/**
	 * Inserts a set of entities from any net into the passed net. 
	 * The method will create new IDs for all passed entities. 
	 * Hence you can use this method to "clone" entities within the same net.
	 * Depending on the preservation mode you chose, the method will try to preserve all
	 * links (PM_PRESERVE_ALL), only links between members of the passed set of entities
	 * (PM_PRESERVE_INTER) or no links at all (PM_PRESERVE_NONE).
	 * @param net The net where the entity is to be inserted. 
	 * @param entities A list of NetEntity objects.
	 * @param space The space where the entity is to be inserted 
	 * @param preserveMode Link preservation mode. One of the values PM_PRESERVE_ALL\nPM_PRESERVE_NONE\nPM_PRESERVE_INTER
	 * @param cloneMappings Pass a hashmap here to be filled with the id mappings. Can be null
	 * @param monitor a progress monitor, can be null
	 * @return A List of Strings with warnings. Will not be null.
	 * @throws NetIntegrityException if the target space does not exist.
	 * @throws MicropsiException if your net is messed up in some unexpected way
	 */
	public static List<String> insertEntities(NetFacadeIF net, List<NetEntity> entities, String space, int preserveMode, HashMap<String, String> cloneMappings, ProgressMonitorIF monitor) throws NetIntegrityException,MicropsiException {
		ArrayList<String> warnings = new ArrayList<String>();

		if(monitor != null) monitor.beginTask("Cloning entities...");	
		
		if(monitor != null) monitor.reportProgress(0, entities.size()*2, "Sorting spaces...");
		
		//ArrayList cloneRelations = new ArrayList();
		HashMap<String, String> cloneRelations = new HashMap<String, String>(entities.size());	
		HashMap<String, NetEntity> newspacehash = new HashMap<String, NetEntity>(entities.size());
		ArrayList<NodeSpaceModule> newspacelist = new ArrayList<NodeSpaceModule>();

		// first pass: Put all new spaces into a hashtable
		Iterator<NetEntity> iter = entities.iterator();
		while(iter.hasNext()) {
			NetEntity origEntity = iter.next();
			if(origEntity.getEntityType() == NetEntityTypesIF.ET_MODULE_NODESPACE)
				newspacehash.put(origEntity.getID(),origEntity);
		}
		
		// second pass: Sort the new spaces in a list
		Iterator<String> newspaces = newspacehash.keySet().iterator();
		while(newspaces.hasNext()) {
			String key = newspaces.next();
			NodeSpaceModule m = (NodeSpaceModule)newspacehash.get(key);
	
			boolean found = false;
			for(int i=0;i<newspacelist.size();i++) {
				if(newspacelist.get(i).getID().equals(m.getParentID())) {
					newspacelist.add(i+1,m);
					found = true;
					break;
				}
			}
			if(!found) newspacelist.add(0,m);	
		}
		
		if(monitor != null) monitor.reportProgress(0, entities.size()*2, "Creating spaces...");
				
		// third pass: create the new nodespace hierarchy
		for(int i=0;i<newspacelist.size();i++) {
			NodeSpaceModule m = newspacelist.get(i);
			
			// if the parent is not one of the new spaces, the space is to be created
			// in the space passed to the method
			NodeSpaceModule newspace;
			if(!newspacehash.containsKey(m.getParentID())) {
				newspace = net.createNodeSpace(space);
			} else {
				String newParentId = cloneRelations.get(m.getParentID());
				newspace = net.createNodeSpace(newParentId);
			}
			cloneRelations.put(m.getID(), newspace.getID());
			
			Iterator slots = m.getSlots();
			while(slots.hasNext())
				net.createNodeSpaceSlot(newspace.getID(), ((Slot)slots.next()).getType());

			Iterator gates = m.getGates();
			while(gates.hasNext())
				net.createNodeSpaceGate(newspace.getID(), ((Gate)gates.next()).getType());
		}

		// now that all possible parents have been created, we can create the other
		// entities 
		
		int i = 0;
		iter = entities.iterator();
		while(iter.hasNext()) {
			// create a newEntity for every entity
		
			NetEntity origEntity = iter.next();
			NetEntity newEntity = null;
			switch(origEntity.getEntityType()) {
				case NetEntityTypesIF.ET_NODE:
					String spaceid = space;
					Node n = (Node)origEntity;
					if(newspacehash.containsKey(n.getParentID()))
						spaceid = cloneRelations.get(n.getParentID());
					newEntity = net.createNode(((Node)origEntity).getType(), spaceid);
					
					if(n.getType() == NodeFunctionalTypesIF.NT_SENSOR) {
						if(((SensorNode)n).isConnected())
							((SensorNode)newEntity).connectSensor(((SensorNode)n).getDataType());
					} else if(n.getType() == NodeFunctionalTypesIF.NT_ACTOR) {
						if(((ActorNode)n).isConnected())
							((ActorNode)newEntity).connectActor(((ActorNode)n).getDataType());
					} else if(n.getType() == NodeFunctionalTypesIF.NT_CHUNK) {
						((ChunkNode)newEntity).setState(((ChunkNode)n).getState());
					}
					
					cloneRelations.put(origEntity.getID(),newEntity.getID());			
					break;
				case NetEntityTypesIF.ET_MODULE_NODESPACE:
					// nodespaces have already been created
					spaceid = cloneRelations.get(origEntity.getID());
					newEntity = net.getNodeSpaceModule(spaceid);
					break;
				case NetEntityTypesIF.ET_MODULE_NATIVE:
					spaceid = space;
					NativeModule mod = (NativeModule)origEntity;
					if(newspacehash.containsKey(mod.getParentID()))
						spaceid = cloneRelations.get(mod.getParentID());
					newEntity = net.createNativeModule(
						mod.getImplementationClassName(),
						spaceid, 
						mod.isDefiant()
					);
					NativeModule newMod = (NativeModule)newEntity; 
					if(	mod.getImplementation() != null && 
						newMod.getImplementation() != null) {
							newMod.getImplementation().getInnerStates().setMap(
								new HashMap<String, String>(mod.getInnerStates().getMap())	
							);
					}
					cloneRelations.put(origEntity.getID(),newEntity.getID());
					break;
			}
			newEntity.setEntityName(origEntity.getEntityName());
			i++;
			if(monitor != null) monitor.reportProgress(i, entities.size()*2, "Cloning...");								
		}
		
		// another pass: create the links and adjust gate values
		iter = entities.iterator();
		while(iter.hasNext()) {
	
			NetEntity original = iter.next();
			NetEntity clone = net.getEntity(cloneRelations.get(original.getID()));
			
			Iterator gatelist = original.getGates();
			while(gatelist.hasNext()) {
				Gate originalgate = (Gate)gatelist.next();
				Gate clonegate = clone.getGate(originalgate.getType());
				clonegate.setActivation(originalgate.getActivation());
				clonegate.setAmpfactor(originalgate.getAmpfactor());
				clonegate.setDecayCalculatorType(originalgate.getDecayCalculatorType());
				clonegate.setGateFactor(originalgate.getGateFactor());
				clonegate.setMaximum(originalgate.getMaximum());
				clonegate.setMinimum(originalgate.getMinimum());
				clonegate.setOutputFunction(originalgate.getOutputFunction());
				clonegate.setOutputFunctionParameters(originalgate.getCurrentOutputFunctionParameters());
				clonegate.setActivation(originalgate.getActivation());
				clonegate.setConfirmedActivation(originalgate.getConfirmedActivation());
				
				// exit if no links are to be created
				if(preserveMode == PM_PRESERVE_NONE) continue;
				
				Iterator linklist = originalgate.getLinks();
				while(linklist.hasNext()) {
					Link l = (Link)linklist.next();
					
					// exit if the link is not a link withing set members
					if(!cloneRelations.containsKey(l.getLinkedEntityID()) && (preserveMode == PM_PRESERVE_INTER))
						continue;
					
					NetEntity entityToLink = null;
					if(cloneRelations.containsKey(l.getLinkedEntityID())) {
						entityToLink = net.getEntity(cloneRelations.get(l.getLinkedEntityID()));
					} else {
						entityToLink = net.getEntity(l.getLinkedEntityID());
					}	
					if(entityToLink == null) {
						warnings.add("Could not find entity "+l.getLinkedEntityID()+" to be linked from new entity "+clone.getID());
						continue;
					}
					 
					switch(l.getType()) {
						case LinkTypesIF.LINKTYPE_SIMPLEASSOCIATION:
							net.createLink(
								clone.getID(),
								originalgate.getType(),
								entityToLink.getID(),
								l.getLinkedSlot().getType(),
								l.getWeight(),
								l.getConfidence(),
								false
							);
							break;
						case LinkTypesIF.LINKTYPE_SPACIOTEMPORAL:
							LinkST stl = (LinkST)l;
							LinkST nl = (LinkST)net.createLink(
								clone.getID(),
								originalgate.getType(),
								entityToLink.getID(),
								l.getLinkedSlot().getType(),
								l.getWeight(),
								l.getConfidence(),
								true
							);
							nl.setT(stl.getT());
							nl.setX(stl.getX());
							nl.setY(stl.getY());
							nl.setZ(stl.getZ());
							break;	 	
					}
				}
			}
			
			if(clone.isActive())
				clone.entityManager.reportActiveEntity(clone);
			
			// exit if no links are to be created (we don't even need to iterate the slots)
			if(preserveMode == PM_PRESERVE_NONE) continue;
			
			Iterator slotlist = original.getSlots();
			while(slotlist.hasNext()) {
				Slot s = (Slot)slotlist.next();
				Iterator linklist = s.getIncomingLinks();
				while(linklist.hasNext()) {
					Link l = (Link)linklist.next();
					
					// exit if the link is a link withing set members (we already created it)
					if(cloneRelations.containsKey(l.getLinkingEntity().getID()))
						continue;
						
					// exit if the preservation mode is not ALL (nothing left to do
					if(preserveMode != PM_PRESERVE_ALL)
						continue;
					
					if(!net.entityExists(l.getLinkingEntity().getID())) {
						warnings.add("Could not find entity "+l.getLinkingEntity().getID()+" to be linked to new entity "+clone.getID());
						continue;
					}
					
					NetEntity from = net.getEntity(l.getLinkingEntity().getID());
																
					switch(l.getType()) {
						case LinkTypesIF.LINKTYPE_SIMPLEASSOCIATION:
							net.createLink(
								from.getID(),
								l.getLinkingGate().getType(),
								clone.getID(),
								l.getLinkedSlot().getType(),
								l.getWeight(),
								l.getConfidence(),
								false
							);
							break;
						case LinkTypesIF.LINKTYPE_SPACIOTEMPORAL:
							LinkST stl = (LinkST)l;
							LinkST nl = (LinkST)net.createLink(
								from.getID(),
								l.getLinkingGate().getType(),
								clone.getID(),
								l.getLinkedSlot().getType(),
								l.getWeight(),
								l.getConfidence(),
								true
							);
							nl.setT(stl.getT());
							nl.setX(stl.getX());
							nl.setY(stl.getY());
							nl.setZ(stl.getZ());
							break;	 	
					}

				}
			}
			i++;
			if(monitor != null) monitor.reportProgress(i, entities.size()*2, "Linking clones...");								
		}
		
		if(cloneMappings != null)
			cloneMappings.putAll(cloneRelations);
			
		if(monitor != null) monitor.endTask();
				
		return warnings;
	}
}
