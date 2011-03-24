/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/NetEntityManager.java,v 1.8 2006/06/27 19:37:02 rvuine Exp $
 */
package org.micropsi.nodenet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * The NetEntityManager keeps record of all existing entities and their states,
 * allowing quick access to all nodes that are currently existing, have
 * recent changes, have been deleted recently, are currently active or are
 * marked "defiant". This is especially important for fast calculation of
 * activation propagation, as, using the NetEntityManager, not all entities have
 * to be regarded when doing the calculations but only those really involved.
 * Besides that, the NetEntityManager allows the observation of the whole net
 * and provides some net-wide services.<br/><br/> It is important to realise
 * that the NetEntityManager is the central management class of a net. Trying to
 * use more than one entity manager within one net would, if possible, be quite
 * pointless.
 */
public final class NetEntityManager implements NetIntegrityIF {
	
	private Logger logger;
	private LocalNetFacade theFacade;
	private HashMap<String, NetEntity> entities = new HashMap<String, NetEntity>();
	private HashSet<String> newEntities = new HashSet<String>();
	private HashSet<String> defiantEntities = new HashSet<String>();
	private HashSet<String> deletedEntities = new HashSet<String>();
	private ArrayList<NetEntity> changedEntities = new ArrayList<NetEntity>();
	private ArrayList<NetEntity> activeEntities = new ArrayList<NetEntity>();
	
	/**
	 * the current netstep. This variable is the mother of all netstep variables
	 * - the <b> real </b> one. You know, all the other ones, they are just
	 * shadows on the cave's wall.
	 */
	protected long netstep = 0;
	
	private int nextInternalIndex = -1;
	
	private Vector<NetObserverIF> observers = new Vector<NetObserverIF>();
	
	/**
	 * Creates a new NetEntityManager
	 * @param logger The log4j logger to be used with this net.
	 */
	protected NetEntityManager(Logger logger, LocalNetFacade theFacade) {
		this.logger = logger;
		this.theFacade = theFacade;
		
		// make room for the first element
		activeEntities.add(null);
		changedEntities.add(null);
	}
	
	/**
	 * Preapares this manager to load a given number of entities 
	 * @param estimatedNumber the estimated number of entities to be loaded
	 */
	protected void prepareLoad(int estimatedNumber) {
		
		System.err.println("estimated:  "+estimatedNumber);
		
		nextInternalIndex = -1;
		entities = new HashMap<String, NetEntity>(estimatedNumber);
		newEntities = new HashSet<String>(estimatedNumber);
		defiantEntities = new HashSet<String>(estimatedNumber);
		deletedEntities = new HashSet<String>(estimatedNumber);
		
		changedEntities = new ArrayList<NetEntity>(estimatedNumber);
		activeEntities = new ArrayList<NetEntity>(estimatedNumber);
		
		// make room for the first element
		activeEntities.add(null);
		changedEntities.add(null);

		System.gc();
	}
	
	/**
	 * Returns the next internal index, unique in this net instance, for
	 * array access. Only to be called by the entity constructor!
	 * @return
	 */
	protected int getNextInternalIndex() {
		nextInternalIndex++;
		return nextInternalIndex;
	}
	
	/**
	 * Returns the LocalNetFacade of the net managed by this NetEntityManager.
	 * @return the facade
	 */
	protected LocalNetFacade getCorrespondingFacade() {
		return theFacade;
	}
	
	/**
	 * Adds an entity to the net. Before passed to this method, the entity is
	 * not part of the net. Do not link entities that have not been passed to
	 * this method, as this will cause NetIntegrityExceptions. Or worse.
	 * @param entity the entity - pass all entities here right after creation!
	 * @throws NetIntegrityException if there is already an entity in the net
	 * with the same ID.
	 */
	protected void addEntity(NetEntity entity) throws NetIntegrityException {
		if(entities.containsKey(entity.getID())) throw new NetIntegrityException(NetIntegrityIF.DUPLICATE_KEY,entity.getID(),this);
		entities.put(entity.getID(),entity);
		newEntities.add(entity.getID());
		
		// now grow
		activeEntities.add(null);
		changedEntities.add(null);
	}

	/**
	 * Deletes an entity, unlinking it and preparing the instance for garbage
	 * collection. If the entity is a NodeSpace module, everything contained in
	 * the NodeSpace will be deleted also.
	 * @param id the ID of the entity to be deleted.
	 * @throws NetIntegrityException if there is no such ID or one of the links
	 * of the entity is bad.
	 */				
	protected void deleteEntity(String id) throws NetIntegrityException {		
		if(!entities.containsKey(id)) throw new NetIntegrityException(NetIntegrityIF.UNKNOWN_ENTITY,id,this);
		
		NetEntity entity = entities.get(id);
				
		if(entity.getEntityType() == NetEntityTypesIF.ET_MODULE_NODESPACE) {
			ArrayList<NetEntity> children = new ArrayList<NetEntity>();
			Iterator<NetEntity> iter = ((NodeSpaceModule)entity).getAllLevelOneEntities();
			while(iter.hasNext()) {
				children.add(iter.next());
			}

			iter = children.iterator();
			while(iter.hasNext()) {
				NetEntity nestedEntity = iter.next();
				deleteEntity(nestedEntity.getID());
			}
		}
		
		((NodeSpaceModule)getEntity(entity.getParentID())).reportEntityDeletion(id);
		
		entity.unlinkCompletely();
		changedEntities.remove(id);
		defiantEntities.remove(id);
		newEntities.remove(id);
		activeEntities.remove(id);
		entities.remove(id);
		deletedEntities.add(id);
	}
	
	/**
	 * Deletes an entity, unlinking it and preparing the instance for garbage
	 * collection.
	 * @param entity the entity to be deleted.
	 * @throws NetIntegrityException if there is no such ID or one of the links
	 * of the entity is bad.
	 */
	protected void deleteEntity(NetEntity entity) throws NetIntegrityException {
		deleteEntity(entity.getID());
	}
	
	/**
	 * Retrieves an entity.
	 * @param id the ID of the entity
	 * @return NetEntity the entity
	 * @throws NetIntegrityException if there is no such entity
	 */
	public NetEntity getEntity(String id) throws NetIntegrityException {
		if(!entities.containsKey(id)) throw new NetIntegrityException(NetIntegrityIF.UNKNOWN_ENTITY,id,this);
		return entities.get(id);
	}
	
	/**
	 * Report an entity that has changed its inner state
	 * @param id the ID of the entity.
	 */
	protected void reportChangedEntity(NetEntity entity) {
		changedEntities.set(entity.getInternalIndex(),entity);
	}
	
	/**
	 * Report an entity that has active gates.
	 * @param id the ID of the entity.
	 */
	protected void reportActiveEntity(NetEntity entity) {
		activeEntities.set(entity.getInternalIndex(),entity);
	}
	
	/**
	 * Mark an entity "defiant"
	 * @param id the ID of the entity
	 */
	protected void reportDefiantEntity(String id) {
		defiantEntities.add(id);
	}
	
	/**
	 * End the defiance of the entity.
	 * @param id the ID of the entity.
	 */
	protected void reportEndOfDefiance(String id) {
		defiantEntities.remove(id);
	}
	
	/**
	 * Clear the list of active entities
	 */
	protected void emptyActiveEntityList() {
		activeEntities.clear();
	}
	
	/**
	 * Returns the map of entities. Know what you're doing when using this
	 * hashmap. Don't delete anything - that would cause bad integrity
	 * violations.
	 * @return HashMap the hashmap of all entities. THE hashmap.
	 */			
	protected HashMap<String, NetEntity> accessEntityMap() {
		return entities;
	}
	
	/**
	 * Returns a list with entries that are either null or active
	 * DO NOT CLEAR THIS LIST! (you may set entries to null, but DO NOT CLEAR)
	 * @return a list with entities or nulls.
	 */
	protected List<NetEntity> accessActiveEntitiesIDList() {
		return activeEntities;
	}
	
	/**
	 * Returns a list with entries that are either null or have changed
	 * DO NOT CLEAR THIS LIST! (you may set entries to null, but DO NOT CLEAR)
	 * @return a list with entities or nulls
	 */
	protected List<NetEntity> accessChangedEntitiesIDList() {
		return changedEntities;
	}

	/**
	 * Returns the (String) IDs of all new entities.
	 * @return Collection the IDs
	 */
	protected Collection<String> accessNewEntitiesIDList() {
		return newEntities;
	}

	/**
	 * Returns the (String) IDs of all deleted entities.
	 * @return Collection the IDs
	 */
	protected Collection<String> accessDeletedEntitiesIDList() {
		return deletedEntities;
	}
	
	/**
	 * Returns the (String) IDs of all defiant entities.
	 * @return Collection the IDs
	 */
	protected Collection<String> accessDefiantEntitiesIDList() {
		return defiantEntities;
	}
				
	public boolean isDefiant(String id) {
		return defiantEntities.contains(id);	
	}		
	
	/**
	 * Increase the net step. (This is <b> not </b> about running the net. It's
	 * merely increasing the netstep variable by one and returning it. For
	 * information on running a net, see NetCycle.)
	 * @return long the new netstep
	 * @see NetCycle
	 */
	protected long increaseNetStep() {
		netstep++;
		return netstep;
	}

	/**
	 * Returns the current netstep
	 * @return long the current netstep
	 */
	public long getNetstep() {
		return netstep;
	}
	
	/**
	 * Register an observer for the whole net. (Beware of high load!)
	 * @param newObserver the observer to be registered
	 */
	protected void registerObserver(NetObserverIF newObserver) {
		observers.add(newObserver);	
	}
	
	/**
	 * Unregister an observer.
	 * @param observer the observer to be unregistered
	 */
	protected void unregisterObserver(NetObserverIF observer) {
		observers.remove(observer);
	}
	
	/**
	 * Notify all observers of all changed, new and deleted entities and clear
	 * the list of new and deleted entities.
	 */
	protected void notifyObservers() {
		for(int i=0;i<observers.size();i++) {
			NetObserverIF observer = observers.elementAt(i);
			observer.deleteEntities(deletedEntities.iterator(),netstep);
			observer.createEntities(newEntities.iterator(),netstep);
			observer.updateEntities(changedEntities.iterator(),netstep);
		}
/*		deletedEntities.clear();
		newEntities.clear();*/
	}
	
	/**
	 * Returns an Iterator with the instances of all NetEntities. Don't even
	 * think of calling remove().
	 * @return Iterator the entities.
	 */
	public Iterator<NetEntity> getAllEntities() {
		return entities.values().iterator();
	}
	
	/**
	 * Checks if there is an entity with the given ID
	 * @param id the id to be checked
	 * @return boolean true if there is an entity with that ID
	 */
	public boolean entityExists(String id) {
		return entities.containsKey(id);
	}

	/**
	 * @see org.micropsi.nodenet.NetIntegrityIF#checkIntegrity()
	 */
	public void checkIntegrity() throws NetIntegrityException {
		Iterator<NetEntity> iter = entities.values().iterator();
		while(iter.hasNext()) {
			Node node = (Node)iter.next();
			node.checkIntegrity();
		}
	}
	
	/**
	 * @see org.micropsi.nodenet.NetIntegrityIF#reportIntegrityStatus()
	 */
	public String reportIntegrityStatus() {
		Iterator<NetEntity> iter = entities.values().iterator();
		String toReturn = "NetIntegrityReport for "+this+":\n\n";
		while(iter.hasNext()) {
			NetEntity entity = iter.next();
			toReturn += " + " + entity + "\n";
			toReturn += " +---+ Slots\n";
			Iterator slots = entity.getSlots();
			while(slots.hasNext()) {
				Slot s = (Slot)slots.next();
				toReturn += " |   +---+ "+s+"\n";
			}
			toReturn += " +---+ Gates\n";
			Iterator gates = entity.getGates();
			while(gates.hasNext()) {
				Gate g = (Gate)gates.next();
				toReturn += " |   +---+ "+g+"\n";
			}
		}
		return toReturn;
	}
	
	/**
	 * Changes a parameter of an entity or one of it's slots, gates or hidden
	 * implementation. Possible values of parameterType can be found in
	 * NetParametersIF.
	 * @param id the ID of the entity to be changed
	 * @param parameterType the parameter to be changed
	 * @param subID the subid. Depending on the parameterType used, this can be
	 * the type of a slot, a gate or the key of a parameter of a hidden
	 * implementation (typically of a NativeModule)
	 * @param newValue The new value as String
	 * @throws NetIntegrityException if the slot or gate was not found
	 * @see NetParametersIF
	 */
	protected void changeParameter(String id, int parameterType, int subID, String newValue) throws NetIntegrityException {
		NetEntity entity = getEntity(id);
		entity.changeParameter(parameterType, subID, newValue);
		this.reportChangedEntity(entity);
	}
	
	/**
	 * Returns the net's log4j logger.
	 * @return Logger
	 */
	protected Logger getLogger() {
		return logger;
	}
	
	/**
	 * Rebuilds the list of active entities
	 */
	protected void buildActiveEntitiesList() {
		for(int i=0;i<activeEntities.size();i++) {
			activeEntities.set(i,null);
		}
		
		Iterator<NetEntity> allEntities = entities.values().iterator();
		while(allEntities.hasNext()) {
			NetEntity e = allEntities.next();
			if(e.isActive())
				reportActiveEntity(e);
		}
	}
	
	/**
	 * Frees as many references as possible to avoid too much gc activity
	 */
	protected void destroy() {
		reset();
		theFacade = null;
		activeEntities = null;
		changedEntities = null;
		defiantEntities = null;
		deletedEntities = null;
		newEntities = null;
		observers = null;		
		entities = null;
	}

	/**
	 * Frees as many references as possible to avoid too much gc activity, but
	 * keeps the manager intact (for net reload);
	 */
	public void reset() {
		nextInternalIndex = -1;
		activeEntities.clear();
		changedEntities.clear();
		defiantEntities.clear();
		deletedEntities.clear();
		newEntities.clear();
		observers.clear();
		
		Iterator<NetEntity> iter = entities.values().iterator();
		while(iter.hasNext()) {
			NetEntity e = iter.next();
			e.destroy();
		}
		entities.clear();
	}

}
