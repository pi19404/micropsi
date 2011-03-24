/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/NetFacadeIF.java,v 1.6 2006/06/27 19:37:02 rvuine Exp $
 */
package org.micropsi.nodenet;

import java.util.Iterator;

import org.micropsi.common.exception.MicropsiException;

/**
 * Implementations of this interface <b> must </b> be used to communicate with,
 * use, create, change and run any MicroPSI net. Do <b> not </b> attempt to go
 * any other way of manipulating anything in this package - always use the
 * facade. Otherwise you'll break the concept, confuse everything and cause
 * terrible things to happen.<br/><br/> The NetFacadeIF implementations provide
 * all kind of interaction any system should ever want to have with a MicroPSI
 * net: Change the structure, check the integrity and state of the net, monitor
 * the whole thing or parts of it and run, suspend or block it via the NetCycle
 * object that can be retrieved through the facade.
 *
 */
public interface NetFacadeIF {
	
	
	// basic operations

	/**
	 * Returns the current netstep
	 * @return long the current netstep.
	 */
	public long getNetstep();
	
	/**
	 * Returns the net's cycle object. Use cycle objects to run the net.
	 * @return NetCycle the net's cycle object.
	 */
	public NetCycleIF getCycle();

	/**
	 * Register an observer to monitor the whole net. Warning: The greater the
	 * net, the higher the load this will probably cause. Note that there will
	 * be quite much movement in those nets, as every propagation of activation
	 * is considered a "change" - so, use SpaceObservers wherever possible.
	 * <br/><br/> For performance reasons, it is crucial that you unregister the
	 * observer as soon as possible - don't keep observers registered and throw
	 * away the information they provide!
	 * @param newObserver the new observer to be registered.
	 */
	public void registerNetObserver(NetObserverIF newObserver);
	
	/**
	 * Unregister an observer and cease monitoring the net.
	 * @param observer The previously registered observer.
	 */
	public void unregisterNetObserver(NetObserverIF observer);
	
	/**
	 * Register an observer to monitor a given NodeSpaceModule. Remember that
	 * Modules are hierarchical things, so the higher the monitored
	 * NodeSpaceModule is in the hierarchy of modules, the higher the load that
	 * the monitoring causes will be.
	 * <br/><br/> For performance reasons, it is crucial that you unregister the
	 * observer as soon as possible - don't keep observers registered and throw
	 * away the information they provide!
	 * @param space the space to observe
	 * @param newObserver the observer to be registered
	 * @throws MicropsiException if there is no such NodeSpaceModule
	 */
	public void registerSpaceObserver(String space, NodeSpaceObserverIF newObserver) throws MicropsiException;
	
	/**
	 * Unregister an observer and cease monitoring the NodeSpaceModule.
	 * @param space the monitored module
	 * @param observer the previously registered observer
	 * @throws MicropsiException if there is no such NodeSpaceModule
	 */
	public void unregisterSpaceObserver(String space, NodeSpaceObserverIF observer) throws MicropsiException;

	/**
	 * Checks whether an entity exists in the net
	 * @param id the id of the entity
	 * @return true if the entity exists
	 */
	public boolean entityExists(String id);
	
	// integrity operations	
	
	/**
	 * Checks the integrity of the net.
	 * @throws NetIntegrityException if the net's integrity is violated
	 * @see NetIntegrityIF
	 */	
	public void checkIntegrity() throws NetIntegrityException; 
	
	/**
	 * Returns the integrity status of the net
	 * @return String the integrity status
	 * @see NetIntegrityIF
	 */
	public String getIntegrityStatus(); 

	
	// retrieval operations

	/**
	 * Returns all entities in the net. <br/> Do not call <pre> remove() </pre>
	 * on the Iterator. Really: Do not at all call it. 
	 * @return Iterator all entities in the net. The iterator contains instances
	 * (not IDs)
	 */
	public Iterator<NetEntity> getAllEntities();
	
	/**
	 * Returns all modules in the net. Don't call <pre> remove() </pre> on the
	 * Iterator. 
	 * @return Iterator all modules in the net. The iterator contains instances
	 * (not IDs)
	 */
	public Iterator<Module> getAllModules();
	
	/**
	 * Retrieves a NetEntity by it's ID.
	 * @param id The ID of the entity to be retrieved.
	 * @return NetEntity the entity.
	 * @throws MicropsiException if there is no such entity.
	 */
	public NetEntity getEntity(String id) throws MicropsiException;
	
	/**
	 * Retrieves a NodeSpaceModule by it's ID.
	 * @param id the ID of the nodespace to be retrieved.
	 * @return NodeSpaceModule the nodespace
	 * @throws MicropsiException if there is no such entity or the entity is not a
	 * NodeSpaceModule
	 */	
	public NodeSpaceModule getNodeSpaceModule(String id) throws MicropsiException;
	
	/**
	 * Returns the root NodeSpace. 
	 * @return NodeSpaceModule the root NodeSpace
	 * @throws MicropsiException if there is no root (the net is empty) or the root
	 * is not a NodeSpace, but a NativeModule (that would mean that the whole
	 * net contains only that one NativeModule. Kinda pointless.)
	 */
	public NodeSpaceModule getRootNodeSpaceModule() throws MicropsiException;
	
	// structure modification operations
	
	/**
	 * Creates a new Node at the given NodeSpace and returns it. For possible
	 * types see NodeFunctionalTypesIF.
	 * @param type the type of the node to be created
	 * @param space the NodeSpace where the node shall be created
	 * @return Node the newly created node
	 * @throws MicropsiException If there is no such space or the creation failed.
	 * @see NodeFunctionalTypesIF
	 */
	public Node createNode(int type, String space) throws MicropsiException;
	
	/**
	 * Creates a new NodeSpace.
	 * @param parent The parent of the new NodeSpace
	 * @return NodeSpaceModule the newly created NodeSpace
	 * @throws MicropsiException if the parent doesn't exist or the creation failed
	 */
	public NodeSpaceModule createNodeSpace(String parent) throws MicropsiException;

	/**
	 * Add a new Slot to a NodeSpaceModule.
	 * @param spaceID the ID of the nodespace
	 * @param type the type of the new Slot
	 * @return Slot the newly created and attached slot.
	 * @throws MicropsiException if there is already a slot of that type attached to
	 * the NodeSpace or the space with the given ID doesn't exist.
	 */
	public Slot createNodeSpaceSlot(String spaceID, int type) throws MicropsiException;
	
	/**
	 * Add a new Gate to a NodeSpaceModule.
	 * @param spaceID the ID of the nodespace
	 * @param type the type of the new gate
	 * @return Gate the newly created and attached gate.
	 * @throws MicropsiException if there is already a gate of that type attached to
	 * the nodespace or the space with the given ID doesn't exist
	 */
	public Gate createNodeSpaceGate(String spaceID, int type) throws MicropsiException;
	
	/**
	 * Delete a nodespace's slot.
	 * @param spaceID the id of the nodespace
	 * @param type the type of the slot to be deleted
	 * @throws MicropsiException if there is no such slot or the nodespace doesn't
	 * exist.
	 */
	public void deleteNodeSpaceSlot(String spaceID, int type) throws MicropsiException;
	
	/**
	 * Delete a nodespace's gate.
	 * @param spaceID the ID of the nodespace
	 * @param type the type of the gate to be deleted
	 * @throws MicropsiException if there is no such gate or the nodespace doesn't
	 * exist.
	 */
	public void deleteNodeSpaceGate(String spaceID, int type) throws MicropsiException;

	/**
	 * Creates a new NativeModule. The classname passed to this method must be
	 * the full qualifying class name of an implementation of
	 * AbstractNativeModuleImpl. 
	 * @param classname the implementation of the NativeModule to be used
	 * @param parent the NodeSpace where the module is to be placed
	 * @param defiant specifies if the new nativemodule will calculate it's gate
	 * "defiantly", that is: even if it recieved no activation in the previous
	 * cycle.
	 * @return NativeModule the newly created NativeModule
	 * @throws MicropsiException if the implementation can't be instantiated or the
	 * parent does not exist or is not a NodeSpace.
	 */
	public NativeModule createNativeModule(String classname, String parent, boolean defiant) throws MicropsiException;
	
	/**
	 * Creates a new link. 
	 * @param from the entity where the link originates from
	 * @param gate the type of the gate where the link originates from
	 * @param to the NetEntity to be linked.
	 * @param slot the type of the slot the linke shall be attached to
	 * @param weight the initial weight of the link
	 * @param confidence the initial confidence of the link
	 * @return the newly created link
	 * @throws MicropsiException if one of the parameters from, to, gate or slot does
	 * not exist at the correct place.
	 * @see AbstractNativeModuleImpl
	 */
	public Link createLink(String from, int gate, String to, int slot, double weight, double confidence, boolean st) throws MicropsiException;
	
	/**
	 * Deletes a link.
	 * @param from the origin of the link
	 * @param gate the origin of the link
	 * @param to the target of the link
	 * @param slot the target of the link
	 * @throws MicropsiException if any of the given entities does not exist or there
	 * is no such link.
	 */
	public void deleteLink(String from, int gate, String to, int slot) throws MicropsiException;
	
	/**
	 * Unlinks an entity completely. This is a convenience method so you don't
	 * have to remember all linked when cleaning up.
	 * @param id the entity to be unlinked
	 * @throws MicropsiException if the given entity does not exist or there is a
	 * bad link attached.
	 */
	public void unlinkEntity(String id) throws MicropsiException;
	
	/**
	 * Unlinks and deletes an entity. It will be eradicated radically into
	 * the sheer void, preparing it for the great nirvana of objects way beyond
	 * the GC.
	 * @param id the entity's ID
	 * @throws MicropsiException if there is no such entity or the entity has bad
	 * links and cannot be unlinked
	 */
	public void deleteEntity(String id) throws MicropsiException;
	
	// internal modification operations
	
	/**
	 * Changes a parameter of an entity or one of it's slots or gates. 
	 * Possible values of parameterType can be found in NetParametersIF.
	 * @param parameterID the parameter to be changed
	 * @param entityID the entity to be changed
	 * @param subID the subid. Depending on the parameterType used, this can be
	 * the type of a slot, a gate or the key of a parameter of a hidden
	 * implementation (typically of a NativeModule)
	 * @param newValue The new value as String. If you are changing an output function
	 * parameter, the newValue must be in the format "PARAM=VALUE", where PARAM is the
	 * name of the parameter you want to change and VALUE a double value.
	 * @throws MicropsiException if the slot or gate was not found or if a conversion
	 * of the newValue had to be done and failed
	 * @see NetParametersIF
	 */
	public void changeParameter(int parameterID, String entityID, int subID, String newValue) throws MicropsiException; 
	
	/**
	 * Changes a parameter of a link
	 * @param parameterID the parameter to be changed. For possible values see
	 * Link and LinkST.
	 * @param fromID The link's origin
	 * @param gate the gate at that entity
	 * @param toID the links end
	 * @param slot the slot at that entity
	 * @param newValue the new value of the parameter
	 */
	public void changeLinkParameter(int parameterID, String fromID, int gate, String toID, int slot, double newValue) throws MicropsiException;
		
	// sensor/actor operations
	
	/**
	 * Returns a list of data sources that SensorNodes can connect to
	 * @return Iterator the iterator (containing Strings)
	 */
	public Iterator<String> getAvailableDataSources();
	
	/**
	 * Returns a list of data targets that ActorNodes can connect to
	 * @return Iterator the iterator (containing Strings)
	 */
	public Iterator<String> getAvailableDataTargets();
	
	/**
	 * Connects a sensor node to the given dataSource
	 * @param sensorNodeID the sensor node to connect
	 * @param dataType the name of the datasource to connect the sensor to
	 * @throws MicropsiException if there is no entity with the given ID or the
	 * entity is not a sensor.
	 */
	public void connectSensor(String sensorNodeID, String dataType) throws MicropsiException;
	
	/**
	 * Disconnects a sensor node from a datasource
	 * @param sensorNodeID the sensornode to be disconnected
	 * @throws MicropsiException if there is no such entity or the entity is not a
	 * sensor.
	 */
	public void disconnectSensor(String sensorNodeID) throws MicropsiException;
	
	/**
	 * Connects an actor node to a datatarget.
	 * @param actorNodeID the actor node to connect
	 * @param dataType the datatarget to connect the actor to
	 * @throws MicropsiException if there is no such entity or the entity is not an
	 * actor
	 */
	public void connectActor(String actorNodeID, String dataType) throws MicropsiException;
	
	/**
	 * Disconnects an actor from it's datatarget
	 * @param actorNodeID the actor node to be disconnected
	 * @throws MicropsiException if the given entity does not exist or is not an
	 * actor.
	 */
	public void disconnectActor(String actorNodeID) throws MicropsiException;
	
	/*
	 * Returns a native module's inspector for reading or manipulating the inner states
	 * 
	 * @param nativeModuleID the id of the module
	 * @return InnerStateInspectorIF the inspector
	 * @throws MicropsiException if there is no such module
	 */
//	public InnerStateInspectorIF getModuleInspector(String nativeModuleID) throws MicropsiException;
	
}
