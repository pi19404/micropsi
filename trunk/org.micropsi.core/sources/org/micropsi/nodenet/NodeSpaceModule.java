/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/NodeSpaceModule.java,v 1.12 2006/08/03 15:40:37 rvuine Exp $
 */
package org.micropsi.nodenet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * NodeSpaceModules (aka NodeSpaces) are modules that contain nodes. Besides any
 * number of concept, register, sensor and actor nodes, NodeSpaces contain
 * exactly one of the special node types: associator, dissociator, activator,
 * deactivator and one directional acivator for each gate type.<br/><br/>
 * NodeSpaces are modules, hence they are hierarchical. Any non-special node to
 * be member of a NodeSpace is also a member of all higher NodeSpaces within
 * the NodeSpace's branch.<br/><br/> As modules, NodeSpaces can have Gates and
 * Slots themselves. The values of the slots can be read within the NodeSpace by
 * sensor nodes, the gates can be written via actor nodes - the dataSources and
 * dataTargets will be created automatically with the slots and gates.<br/> This
 * makes modules very flexible: It is possible to replace some module written in
 * Java (as a NativeModule) by a module working with node scripts (in a
 * NodeSpace) afterwards without having to change the system - if the timing is
 * the same or the timining does not matter. 
 */
public class NodeSpaceModule extends Module {

	/**
	 * An iterator wrapper for filtering iterator contents.
	 */
	class EntityIterator implements Iterator<NetEntity> {
		
		private NetEntityManager manager;
		private Iterator<String> nodeList;
		
		protected EntityIterator(NetEntityManager manager, Collection<String> nodeList) {
			this.manager = manager;
			this.nodeList = nodeList.iterator();
		}
		
		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return nodeList.hasNext();
		}
		
		/**
		 * @see java.util.Iterator#next()
		 */
		public NetEntity next() throws NoSuchElementException {
			if(!hasNext()) throw new NoSuchElementException();
			try {
				return manager.getEntity(nodeList.next());
			} catch (NetIntegrityException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		/**
		 * This will throw an UnsupportedOperationException in any case!
		 * @see java.util.Iterator#remove()
		 */
		public void remove() throws NoSuchElementException {
			throw new UnsupportedOperationException("You cannot remove entities here.");
		}
	}
	
	/**
	 * Another filtering wrapper.
	 */
	class SpaceContentIterator implements Iterator<String> {

		private NodeSpaceModule space;
		private Iterator<String> keyIterator = null;
		private Iterator<NetEntity> entityIterator = null;
		private String next = null;
		
		protected SpaceContentIterator(Iterator<String> iter, NodeSpaceModule space) {
			this.keyIterator = iter;
			this.space = space;
			calcNext();
		}

		protected SpaceContentIterator(Iterator<NetEntity> iter, NodeSpaceModule space, boolean dummy) {
			this.entityIterator = iter;
			this.space = space;
			calcNext();
		}
		
		private void calcNext() {
			if(keyIterator != null) {
				while(true) {
					if(!keyIterator.hasNext()) {
						next = null;
						return;
					} else {
						next = keyIterator.next();
						if(space.containsEntity(next)) return;
					}
				}
			} else {
				while(true) {
					if(!entityIterator.hasNext()) {
						next = null;
						return;
					} else {
						NetEntity next = entityIterator.next(); 
						if(next != null) {
							if(space.containsEntity(next.getID())) return;
						}
					}
				}				
			}
		}
		
		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return (next != null);
		}
		
		/**
		 * @see java.util.Iterator#next()
		 */
		public String next() throws NoSuchElementException {
			String toReturn = next;
			if(toReturn == null) throw new NoSuchElementException();
			calcNext();
			return toReturn;
		}
		
		/**
		 * This will throw an UnsupportedOperationException in any case!
		 * @see java.util.Iterator#remove()
		 */
		public void remove() throws NoSuchElementException {
			throw new UnsupportedOperationException("You cannot remove anything here.");
		}
	}
	
	/**
	 * Another filtering wrapper.
	 */
	class SpaceContentEntityIterator implements Iterator<NetEntity> {

		private NodeSpaceModule space;
		private Iterator<NetEntity> iter;
		private NetEntity next = null;
		
		protected SpaceContentEntityIterator(Iterator<NetEntity> iter, NodeSpaceModule space) {
			this.iter = iter;
			this.space = space;
			calcNext();
		}
		
		private void calcNext() {
			while(true) {
				if(!iter.hasNext()) {
					next = null;
					return;
				} else {
					next = iter.next();
					if(next != null) {
						if(space.containsEntity(next.getID())) return;
					}
				}
			}
		}
		
		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return (next != null);
		}
		
		/**
		 * @see java.util.Iterator#next()
		 */
		public NetEntity next() throws NoSuchElementException {
			NetEntity toReturn = next;
			if(toReturn == null) throw new NoSuchElementException();
			calcNext();
			return toReturn;
		}
		
		/**
		 * This will throw an UnsupportedOperationException in any case!
		 * @see java.util.Iterator#remove()
		 */
		public void remove() throws NoSuchElementException {
			throw new UnsupportedOperationException("You cannot remove anything here.");
		}
	}
	
	/**
	 * DataProvider to make the values of the NodeSpace's slots available via
	 * sensors within the NodeSpace.
	 */
	class SlotDataProvider implements SensorDataSourceIF {
		
		private final Slot slot;
		private final String dataType;
		
		/**
		 * Constructor, setting the slot
		 * @param s the slot
		 */
		protected SlotDataProvider(Slot s) {
			dataType = "SlotSource("+s.getType()+")_at_"+s.getNetEntity().getID();
			slot = s;
		}

		/**
		 * The return value will be: "SlotSource(SLOTTYPE)_at_NODESPACEID",
		 * where SLOTTYPE is the type of the slot and NODESPACEID is the entity
		 * ID of the NodeSpace the slot belongs to.
		 * @see org.micropsi.nodenet.SensorDataSourceIF#getDataType()
		 */
		public String getDataType() {
			return dataType;
		}

		/**
		 * @see org.micropsi.nodenet.SensorDataSourceIF#getSignalStrength()
		 */
		public double getSignalStrength() {
			return slot.getIncomingActivation();
		}
	
	}
	
	/**
	 * DataTarget to allow actor nodes to write to the NodeSpace's gates.
	 */
	class GateDataTarget implements ActorDataTargetIF {
		
		private final Gate gate;
		private final String dataType;
		
		/**
		 * Constructor, setting the gate
		 * @param g the gate
		 */
		protected GateDataTarget(Gate g) {
			dataType = "GateTarget("+g.getType()+")_at_"+g.getNetEntity().getID();
			gate = g;
		}

		/**
		 * The return value will be: "GateTarget(GATETYPE)_at_NODESPACEID",
		 * where GATETYPE is the type of the gate and NODESPACEID is the entity
		 * ID of the NodeSpace the gate belongs to.
		 * @see org.micropsi.nodenet.ActorDataTargetIF#getDataType()
		 */
		public String getDataType() {
			return dataType;
		}
		
		/**
		 * @see org.micropsi.nodenet.ActorDataTargetIF#setSignalStrength(double)
		 */
		public void addSignalStrength(double value) {			
			gate.setActivation(gate.getActivation() + value);
			entityManager.reportChangedEntity(NodeSpaceModule.this);
		}

		public double getExecutionSuccess() {
			return 0;
		}
	}

	private ArrayList<String> entities = new ArrayList<String>();
	private ArrayList<String> levelOneEntities = new ArrayList<String>();
	private ArrayList<String> deletedEntities = new ArrayList<String>(); 
		
	private ActLinkTypeNode actPor;
	private ActLinkTypeNode actRet;
	private ActLinkTypeNode actSur;
	private ActLinkTypeNode actSub;
	private ActLinkTypeNode actCat;
	private ActLinkTypeNode actExp;
	private ActLinkTypeNode actSym;
	private ActLinkTypeNode actRef;
	
	private AssociatorNode associator;
	private DissociatorNode dissociator;
	private ActivatorNode activator;
	private DeactivatorNode deactivator;
	
	private SensActRegistry sensActRegistry;
	
	private boolean hasDeletedLinks = false;
	
	/**
	 * The NodeSpace's dissociation constant.
	 * @see DissociatorNode
	 */
	protected double DISSOCIATIONCONST = 1.0;
	
	/**
	 * learning constant for associator learning
	 */ 
	protected double LEARNINGCONST = 1.0;
	
	/**
	 * learning constant for strenthening of used links
	 */
	protected double STRENGTHENINGCONST = 1;
	
	/**
	 * Do gates of nodes directly contained in this nodespace caclculate decays?
	 */
	protected boolean DECAYALLOWED = true;
	
	private ArrayList<NodeSpaceObserverIF> observers = new ArrayList<NodeSpaceObserverIF>();
	private HashMap<Gate,GateDataTarget> dataTargets = new HashMap<Gate,GateDataTarget>();
	private HashMap<Slot,SlotDataProvider> dataProviders = new HashMap<Slot,SlotDataProvider>();
		
	/**
	 * Creates a new NodeSpaceModule. This method must only be called by the
	 * NetEntityFactory!
	 * @param id the ID of the module/entity
	 * @param parent the NodeSpace's parent module
	 * @param moduleManager the module manager
	 * @param entityManager the entity manager
	 * @param sensActRegistry the SensActRegistry
	 * @throws NetIntegrityException if the ID already exists within the net
	 */
	protected NodeSpaceModule(String id, String parent, ModuleManager moduleManager, NetEntityManager entityManager,SensActRegistry sensActRegistry) throws NetIntegrityException {
		super(id,parent,entityManager,moduleManager);
		this.sensActRegistry = sensActRegistry;
	}
	
	/**
	 * Attaches an entity to this nodespace and makes it a member. (Members of a
	 * NodeSpaces are Members of all parent NodeSpaces also!).<br/> Note that
	 * this does <b> not </b> make the NodeSpace the <i> parent </i> of the
	 * attached entity - this is something confusingly different. Although
	 * Modules <i> attached </i> to a NodeSpace are normally <i> children </i>
	 * of this space, but there is no such thing as a parent/child relation with
	 * Nodes: Nodes have no parents as modules do, they are just member of a
	 * NodeSpace.
	 * @param id the ID of the entity to be attached.
	 * @throws NetIntegrityException if there is no such entity
	 */
	protected void attachEntity(String id, boolean levelOne) throws NetIntegrityException {		
		NetEntity entity = entityManager.getEntity(id);
		if(!levelOneEntities.contains(id) && levelOne) {
			levelOneEntities.add(id);
			entity.setParentID(this.getID());
		} 
		if(!entities.contains(id)) entities.add(id);
		if(!this.isRoot()) ((NodeSpaceModule)this.getParent()).attachEntity(id,false);
		
		if(entity.getEntityType() == NetEntityTypesIF.ET_NODE && levelOne) {
			
			Node node = (Node)entity;
			
			switch(node.getType()) {
				case NodeFunctionalTypesIF.NT_REGISTER:
				case NodeFunctionalTypesIF.NT_SENSOR:
				case NodeFunctionalTypesIF.NT_ACTOR:
				case NodeFunctionalTypesIF.NT_CONCEPT:
				case NodeFunctionalTypesIF.NT_TOPO:
				case NodeFunctionalTypesIF.NT_CHUNK:	
					break;
				case NodeFunctionalTypesIF.NT_ACT_POR:
					actPor = (ActLinkTypeNode)node;
					actPor.setSpace(this);
					break;
				case NodeFunctionalTypesIF.NT_ACT_RET:
					actRet = (ActLinkTypeNode)node; 
					actRet.setSpace(this);
					break;
				case NodeFunctionalTypesIF.NT_ACT_SUB:
					actSub = (ActLinkTypeNode)node; 
					actSub.setSpace(this);
					break;
				case NodeFunctionalTypesIF.NT_ACT_SUR:
					actSur = (ActLinkTypeNode)node; 
					actSur.setSpace(this);				
					break;
				case NodeFunctionalTypesIF.NT_ACT_CAT:
					actCat = (ActLinkTypeNode)node; 
					actCat.setSpace(this);
					break;
				case NodeFunctionalTypesIF.NT_ACT_EXP:
					actExp = (ActLinkTypeNode)node; 
					actExp.setSpace(this);				
					break;
				case NodeFunctionalTypesIF.NT_ACT_SYM:
					actSym = (ActLinkTypeNode)node; 
					actSym.setSpace(this);
					break;
				case NodeFunctionalTypesIF.NT_ACT_REF:
					actRef = (ActLinkTypeNode)node; 
					actRef.setSpace(this);				
					break;
				case NodeFunctionalTypesIF.NT_ASSOCIATOR:
					associator = (AssociatorNode)node; 
					associator.setSpace(this);
					break;
				case NodeFunctionalTypesIF.NT_DISSOCIATOR:
					dissociator = (DissociatorNode)node;
					dissociator.setSpace(this); 
					break;
				case NodeFunctionalTypesIF.NT_ACTIVATOR:
					activator = (ActivatorNode)node;
					activator.setSpace(this);
					break;
				case NodeFunctionalTypesIF.NT_DEACTIVATOR:
					deactivator = (DeactivatorNode)node; 
					deactivator.setSpace(this);
					break;
				default: throw new RuntimeException("Fix this: bad node type");
			}
		}	
	}
	
	/**
	 * Creates a new gate for the NodeSpace and creates the corresponding
	 * DataTarget.
	 * @param gateType the type of the gate to be created
	 * @return Gate the newly created gate
	 * @throws NetIntegrityException if the NodeSpace already has a gate of that
	 * type
	 */
	protected Gate createGate(int gateType) throws NetIntegrityException {
		Gate g = new Gate(gateType,this,-1);
		this.addGate(g);
		GateDataTarget gdt = new GateDataTarget(g);
		dataTargets.put(g, gdt);
		sensActRegistry.registerActuatorDataTarget(gdt);
		return g;
	}
	
	/**
	 * Deletes a gate and removes the corresponding DataTarget.
	 * @param gateType the type of the gate to be removed
	 * @throws NetIntegrityException if there is no such gate
	 */
	protected void deleteGate(int gateType) throws NetIntegrityException {
		Gate g = getGate(gateType);
		GateDataTarget gdt = dataTargets.get(g);
		sensActRegistry.unregisterActuatorDataTarget(gdt);
		dataTargets.remove(g);
		super.deleteGate(gateType);
	}
	
	/**
	 * Creates a slot and generates the corresponding DataSource.
	 * @param slotType the type of the slot to be created.
	 * @return Slot the newly created slot
	 * @throws NetIntegrityException if the NodeSpace already has a slot of that
	 * type
	 */
	protected Slot createSlot(int slotType) throws NetIntegrityException {
		Slot s = new Slot(slotType,this,entityManager);
		this.addSlot(s);
		SlotDataProvider sdp = new SlotDataProvider(s);
		dataProviders.put(s, sdp);
		sensActRegistry.registerSensorDataProvider(sdp);
		return s;		
	}
	
	/**
	 * Deletes a slot and removes the corresponding DataSource. 
	 * @param slotType the type of the slot to be removed
	 * @throws NetIntegrityException if the space has no such slot
	 */
	protected void deleteSlot(int slotType) throws NetIntegrityException {
		Slot s = getSlot(slotType);
		SlotDataProvider sdp = dataProviders.get(s);
		sensActRegistry.unregisterSensorDataProvider(sdp);
		dataProviders.remove(s);
		super.deleteSlot(slotType);
	}	
							
	/**
	 * Checks if the NodeSpace contains the given entity.
	 * @param entityID the ID of the entity
	 * @return boolean true if the entity is a member of the space
	 */
	public boolean containsEntity(String entityID) {
		return entities.contains(entityID);
	}
	
	/**
	 * Checks if the NodeSpace directly contains the given entity
	 * @param entityID the ID of the entity
	 * @return boolean true if the entity is a direct member of the space
	 */
	public boolean containsEntityDirectly(String entityID) {
		return levelOneEntities.contains(entityID);
	}
	
	/**
	 * Returns all entities (instances) that are members of the NodeSpace.
	 * @return Iterator the entities
	 */
	public Iterator<NetEntity> getAllEntities() {
		return new EntityIterator(entityManager,entities);
	}
	
	/**
	 * Returns the number of entities contained in this space
	 * @return the number of entities in this space
	 */
	public int getNumberOfEntities() {
		return entities.size();
	}
	
	/**
	 * Returns all entities (instances) that are direct members of the NodeSpace
	 * (not of one of the contained NodeSpaces)
	 * @return Iterator
	 */
	public Iterator<NetEntity> getAllLevelOneEntities() {
		return new EntityIterator(entityManager,levelOneEntities);
	}
	
	/**
	 * Returns the number of entities contained in this space directly
	 * (not in one of the contained NodeSpaces)
	 * @return the number of entities direcly contained in this space
	 */
	public int getNumberOfLevelOneEntities() {
		return levelOneEntities.size();
	}	
	
	/**
	 * Returns all NetEntites that are currently active AND members of
	 * the NodeSpace
	 * @return Iterator the ids of the active entities within the space
	 */
	protected Iterator<NetEntity> getActiveEntityIDs() {
		return new SpaceContentEntityIterator(entityManager.accessActiveEntitiesIDList().iterator(),this);
	}
	
	/**
	 * Returns the EntityManager
	 * @return NetEntityManager
	 */
	protected NetEntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * Returns the NodeSpace's dissociation constant.
	 * @return double the dissociation constant
	 */
	public double getDissociationConstant() {
		return this.DISSOCIATIONCONST;
	}
	
	/**
	 * Retuns the net's learning constant.
	 * @return double the learning constant.
	 */
	public double getLearningConstant() {
		return LEARNINGCONST;
	}
	
	/**
	 * Returns the net's strengthening constant (for strengthening-by-use link
	 * weight modifications)
	 * @return double the strengthening constant.
	 */
	public double getStrengtheningConstant() {
		return STRENGTHENINGCONST;
	}	
	
	/**
	 * @see org.micropsi.nodenet.Module#reportEntityDeletion(String)
	 */
	protected void reportEntityDeletion(String id) {
		entities.remove(id);
		levelOneEntities.remove(id);
		deletedEntities.add(id);
		
		if(getParentID() != null) {
			try {
				getParent().reportEntityDeletion(id);
			} catch (NetIntegrityException e) {
			}
		}
	}

	/**
	 * Register an observer to monitor changes in the NodeSpace.
	 * @param observer the observer
	 */
	public void registerObserver(NodeSpaceObserverIF observer) {
		observers.add(observer);
	}
	
	/**
	 * Unregister an observer.
	 * @param observer the observer
	 */
	public void unregisterObserver(NodeSpaceObserverIF observer) {
		observers.remove(observer);
	}
	
	/**
	 * @see org.micropsi.nodenet.Module#notifyObservers()
	 */
	public void notifyObservers() {
		for(int i=0;i<observers.size();i++) {
			NodeSpaceObserverIF observer = observers.get(i);
			observer.createEntities(new SpaceContentIterator(entityManager.accessNewEntitiesIDList().iterator(),this),entityManager.netstep);
			observer.deleteEntities(deletedEntities.iterator(),entityManager.netstep);
			try {			
				observer.updateEntities(new SpaceContentIterator(entityManager.accessChangedEntitiesIDList().iterator(),this,true),entityManager.netstep);
			} catch(Exception e) {
				entityManager.getLogger().warn("Error notifying observer (should have no impact on the behavior)",e);	
			}
		}
		deletedEntities.clear();
	}
	
	/**
	 * @see org.micropsi.nodenet.NetEntity#getEntityType()
	 */
	public int getEntityType() {
		return NetEntityTypesIF.ET_MODULE_NODESPACE;
	}
	
	/**
	 * @see org.micropsi.nodenet.NetEntity#calculateGates()
	 */
	protected void calculateGates() throws NetIntegrityException {
	}
	
	/**
	 * @see org.micropsi.nodenet.NetEntity#changeParameter(int, int, String)
	 */
	protected void changeParameter(int parameterType, int subID, String newValue) throws NetIntegrityException {
		super.changeParameter(parameterType,subID,newValue);

		switch(parameterType) {
			case NetParametersIF.PARM_NODESPACE_ASSO:
				LEARNINGCONST = Double.parseDouble(newValue);
				break;
			case NetParametersIF.PARM_NODESPACE_STRENGTHENING:
				STRENGTHENINGCONST = Double.parseDouble(newValue);
				break;
			case NetParametersIF.PARM_NODESPACE_DISSO:
				this.DISSOCIATIONCONST = Double.parseDouble(newValue);
				break;
			case NetParametersIF.PARM_NODESPACE_DECAYALLOWED:
				this.DECAYALLOWED = "true".equalsIgnoreCase(newValue);
				
		}
	}
		
	/**
	 * Returns the NodeSpace's ActCat node
	 * Can be null if there is no such node assigned to the NodeSpace.
	 * @return NetEntity the ActCat
	 */
	public NetEntity getActCat() {
		return actCat;
	}

	/**
	 * Returns the NodeSpace's ActExp node
	 * Can be null if there is no such node assigned to the NodeSpace.
	 * @return NetEntity the ActExp node
	 */
	public NetEntity getActExp() {
		return actExp;
	}

	/**
	 * Returns the NodeSpace's Activator node
	 * Can be null if there is no such node assigned to the NodeSpace.
	 * @return NetEntity the activator
	 */
	public NetEntity getActivator() {
		return activator;
	}

	/**
	 * Returns the NodeSpace's ActPor node
	 * Can be null if there is no such node assigned to the NodeSpace.
	 * @return NetEntity the ActPor node
	 */
	public NetEntity getActPor() {
		return actPor;
	}

	/**
	 * Returns the NodeSpace's ActRet node
	 * Can be null if there is no such node assigned to the NodeSpace.
	 * @return NetEntity the ActRet node
	 */
	public NetEntity getActRet() {
		return actRet;
	}

	/**
	 * Returns the NodeSpace's ActSub node
	 * Can be null if there is no such node assigned to the NodeSpace.
	 * @return NetEntity the ActSub node
	 */
	public NetEntity getActSub() {
		return actSub;
	}

	/**
	 * Returns the NodeSpace's ActSur node
	 * Can be null if there is no such node assigned to the NodeSpace.
	 * @return NetEntity the ActSur node
	 */
	public NetEntity getActSur() {
		return actSur;
	}
	
	/**
	 * Returns the NodeSpace's ActSym node
	 * Can be null if there is no such node assigned to the NodeSpace.
	 * @return NetEntity the ActSym node
	 */
	public NetEntity getActSym() {
		return actSym;
	}
	
	/**
	 * Returns the NodeSpace's ActRef node
	 * Can be null if there is no such node assigned to the NodeSpace.
	 * @return NetEntity the ActRef node
	 */
	public NetEntity getActRef() {
		return actRef;
	}

	/**
	 * Returns the NodeSpace's associator
	 * Can be null if there is no such node assigned to the NodeSpace.
	 * @return NetEntity the associator
	 */
	public NetEntity getAssociator() {
		return associator;
	}

	/**
	 * Returns the NodeSpace's deactivator.
	 * Can be null if there is no such node assigned to the NodeSpace.
	 * @return NetEntity the deactivator
	 */
	public NetEntity getDeactivator() {
		return deactivator;
	}

	/**
	 * Returns the NodeSpaces's dissociator. 
	 * Can be null if there is no such node assigned to the NodeSpace.
	 * @return NetEntity the dissociator
	 */
	public NetEntity getDissociator() {
		return dissociator;
	}
	
	/**
	 * Sets the "deleted links" flag. This happens when
	 * a link gets deleted from inside the net (by a native module).
	 * @param has deleted links?
	 */
	protected void setHasDeletedLinks(boolean b) {
		hasDeletedLinks = b;
	}

	/**
	 * Returns true if links have been deleted at any of the directly
	 * contained nodes of this nodespace within the last net cycle.
	 * @return true if links have been deleted
	 */
	public boolean hasDeletedLinks() {
		return hasDeletedLinks;
	}
	
	/**
	 * Returns true if nodes in this nodespace have decaying links
	 * (If the decay settings at gates have an effect)
	 * @return true if links originating from entities in this space
	 * undergo decay
	 */
	public boolean isDecayAllowed() {
		return DECAYALLOWED;
	}
	
	public void destroy() {
		observers.clear();
		entities.clear();
		levelOneEntities.clear();
		deletedEntities.clear(); 
		
		actPor = null;
		actRet = null;
		actSur = null;
		actSub = null;
		actCat = null;
		actExp = null;
		actSym = null;
		actRef = null;
	
		associator = null;
		dissociator = null;
		activator = null;
		deactivator = null;
	
		sensActRegistry = null;		
		super.destroy();
	}

}
