/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/NetEntity.java,v 1.11 2006/06/27 19:37:02 rvuine Exp $
 */
package org.micropsi.nodenet;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The abstract ancestor of all net entities. An entity is basically a thing in
 * a MicroPSI net that can have links to other entities and can be linked by
 * entities. Incoming links are attached to <i>Slots</i>, outgoing links are
 * maintained by <i>Gates</i>. (Slots, Gates and Links are <i>not</i> entities
 * themselves, but part of entities.) <br/> NetEntities are managed by the
 * NetEntityManager and produced by the NetEntityFactory.<br/><br/>
 * 
 * 
 * @see org.micropsi.nodenet.NetEntityFactory
 * @see org.micropsi.nodenet.NetEntityManager
 * @see org.micropsi.nodenet.Gate
 * @see org.micropsi.nodenet.Slot
 * @see org.micropsi.nodenet.NetEntityTypesIF
 */

public abstract class NetEntity implements NetIntegrityIF {

	protected ArrayList<Gate> gates = new ArrayList<Gate>(9);
	protected ArrayList<Slot> slots = new ArrayList<Slot>(9);

	private String id;
	private String entityName;
	protected NetEntityManager entityManager;
	private String parentID;	
	private NodeSpaceModule parentCache;
	private final int internalIndex;
	
	/**
	 * Creates a new NetEntity, using the given id. This should only be called
	 * by the NetEntityFactory.
	 * @param id The id of the entity
	 * @param manager
	 */
	protected NetEntity(String id, NetEntityManager manager) {
		this.id = id;
		this.entityManager = manager;
		internalIndex = manager.getNextInternalIndex();
	}
	
	/**
	 * Sets the parent ID
	 */
	protected void setParentID(String parentID) {
		this.parentID = parentID;
		this.parentCache = null;
	}
	
	/**
	 * Returns the ID of the entity's parent entity
	 * @return String
	 */
	public String getParentID() {
		return parentID;	
	}
		
	/**
	 * Returns the unique ID of the entity.
	 * @return String the ID of the entity.
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Returns the type of the entity. For possible values @see NetEntityTypesIF
	 * @return int the type
	 * 
	 * @see NetEntityTypesIF
	 */
	public abstract int getEntityType();
	
	/**
	 * Returns a gate of the entity.
	 * @param type The type of the gate to be retrieved.
	 * @return Gate or null if there is no gate of that type
	 * @see Gate
	 */
	public Gate getGate(int type) {
		for(int i=0;i<gates.size();i++) {
			Gate g = gates.get(i);
			if(g.getType() == type) return g;
		}
		return null;
	}
	
	/**
	 * Returns the number of gates of this entity.
	 * @return int the number of gates
	 */
	public int getNumberOfGates() {
		return gates.size();
	}
	
	/**
	 * Returns all gates of this entity. Do <b> not </b> call <pre> remove()
	 * </pre>.
	 * @return Iterator an iterator with all gates.
	 */
	public Iterator<Gate> getGates() {
		return gates.iterator();
	}
	
	/**
	 * Returns all slots of this entity. Do <b> not </b> call <pre> remove()
	 * </pre>.
	 * @return Iterator an iterator with all slots.
	 */
	public Iterator<Slot> getSlots() {
		return slots.iterator();
	}

	/**
	 * Returns the number of slots.
	 * @return int the number of slots.
	 */
	public int getNumberOfSlots() {
		return slots.size();
	}
	
	/**
	 * Get the slot with the given type.
	 * @param type the type of the slot to be retrieved.
	 * @return Slot the slot or null if there is no slot of that type
	 */
	public Slot getSlot(int type) {
		for(int i=0;i<slots.size();i++) {
			Slot s = slots.get(i);
			if(s.getType() == type) return s;
		}
		return null;
	}
	
	/**
	 * Adds a gate to the entity.
	 * @param g the gate to be added
	 * @throws NetIntegrityException if there is already a gate of the same type
	 */
	protected void addGate(Gate g) throws NetIntegrityException {
		
		if(getGate(g.getType()) != null) 
			throw new NetIntegrityException(NetIntegrityIF.DUPLICATE_KEY,g.getType()+"",this);
		
		gates.add(g);
		entityManager.reportChangedEntity(this);
	}
	
	/**
	 * Adds a slot to the entity.
	 * @param s the slot to be added
	 * @throws NetIntegrityException if there is already a slot of the same type
	 */
	protected void addSlot(Slot s) throws NetIntegrityException {

		if(getSlot(s.getType()) != null) 
			throw new NetIntegrityException(NetIntegrityIF.DUPLICATE_KEY,s.getType()+"",this);

		slots.add(s);
		entityManager.reportChangedEntity(this);
	}
	
	/**
	 * Deletes a gate and all links originating from it. The method will simply
	 * return if there is no such gate.
	 * @param gateType the type of the gate to be deleted
	 * @throws NetIntegrityException if the gate has bad links
	 */
	protected void deleteGate(int gateType) throws NetIntegrityException {

		Gate g = getGate(gateType); 
		if(g == null) return;
		
		g.unlinkCompletely();
		
		gates.remove(g);
	}
	
	/**
	 * Deletes a slot and all links attached to it. The method will simply
	 * return if there is no such slot.
	 * @param slotType the type of the slot that will be deleted
	 * @throws NetIntegrityException if the slot has bad links
	 */
	protected void deleteSlot(int slotType) throws NetIntegrityException {
		
		Slot s = getSlot(slotType); 
		if(s == null) return;
		
		s.unlinkCompletely();
		
		slots.remove(s);
	}
	
	/**
	 * Checks if one of the gates is active
	 * @return boolean true if any gate is active, false otherwise
	 */
	public boolean isActive() {
		for(int i=0;i<gates.size();i++)
			if(gates.get(i).isActive()) return true;
		return false;
	}
		
	/**
	 * Confirms the current activations of the gates.
	 */
	protected void confirmNewActivation() {
		boolean isActive = false;
		for(int i=0;i<gates.size();i++)
			if(gates.get(i).confirmActivation()) isActive = true;

		for(int i=0;i<slots.size();i++) 
			slots.get(i).killActivation();

		if(isActive) entityManager.reportActiveEntity(this); 	
	}

	/**
	 * Calculates the gates.
	 * @throws NetIntegrityException
	 */			
	protected abstract void calculateGates() throws NetIntegrityException;
	
	/**
	 * Propagates the activation of all active gates along all links attached to
	 * active gates, calculating new activation values and putting it into the
	 * linked slots.
	 * @throws NetIntegrityException if there is a bad link
	 */
	protected void propagateActivation() throws NetIntegrityException {		

		for(int i=0;i<gates.size();i++) {
			Gate g = gates.get(i);
			if(g.getConfirmedActivation() != 0) {
				if(parentCache == null) parentCache = (NodeSpaceModule)entityManager.getEntity(parentID);
				if(parentCache.isDecayAllowed()) g.calculateDecays(entityManager.getNetstep());
				int n = g.getNumberOfLinks();
				for(int j=0;j<n;j++) {
					Link l = g.getLinkAt(j);
					l.getLinkedSlot().putActivation(g.getConfirmedActivation() * l.getWeight() * l.getConfidence());					
					l.setUsed(true);	
				}
			}
			g.setActivation(0);
			entityManager.reportChangedEntity(this);
		}
	}

	/**
	 * Returns the first link of the given gate or null if there is no link at
	 * that gate.
	 * @param gate the gate
	 * @return Link the link
	 * @throws NullPointerException if there is no such gate
	 */
	public Link getFirstLinkAt(int gate) {
		return getGate(gate).getLinkAt(0);
	}

	/**
	 * Returns the last link of the given gate or null if there is no link at
	 * that gate.
	 * @param gate the gate
	 * @return Link the link
	 * @throws NullPointerException if there is no such gate
	 */
	public Link getLastLinkAt(int gate) {
		return getGate(gate).getLinkAt(getGate(gate).getNumberOfLinks()-1);
	}
	
	/**
	 * Returns the n-th link of the given gate or null if there is no link at
	 * that gate
	 * @param gate the gate
	 * @param at the index of the link
	 * @return Link the link
	 * @throws NullPointerException if there is no such gate
	 */
	public Link getLink(int gate, int at) {
		return getGate(gate).getLinkAt(at);
	}
	
	/**
	 * Creates a new link. This will set all references correctly. (Also the one
	 * at the "remote" slot)
	 * 
	 * @param id the NetEntity to be linked.
	 * @param slot the type of the slot the linke shall be attached to
	 * @param from the gate where the link originates from
	 * @param weight the initial weight of the link
	 * @param confidence the initial confidence of the link
	 * @param x the X value of the link 
	 * @param y the Y value of the link
	 * @param z the Z value of the link
	 * @param t the T value of the link
	 * @throws NetIntegrityException if the linked entity does not exist or has
	 * no slot of the given type.
	 * 
	 * @see Link
	 * @see LinkFactory
	 */
	protected void createLinkTo(String id, int slot, Gate from, double weight, double confidence, double x, double y, double z, int t) throws NetIntegrityException {
				
		LinkST l = (LinkST)LinkFactory.getInstance().createLink(
					LinkTypesIF.LINKTYPE_SPACIOTEMPORAL,
					entityManager,
					from,
					id,
					slot,
					weight,
					confidence);
					
		l.setX(x);
		l.setY(y);
		l.setZ(z);
		l.setT(t);
		
		Slot s = entityManager.getEntity(id).getSlot(slot);
		if(s == null) throw new NetIntegrityException(NetIntegrityIF.BAD_SLOT,this.getID(),l);
		from.addLink(l);
		s.attachIncomingLink(l);
		entityManager.reportChangedEntity(this);
	}
	

	/**
	 * Creates a new link. This will set all references correctly. (Also the one
	 * at the "remote" slot)
	 * @param id the NetEntity to be linked.
	 * @param slot the type of the slot the linke shall be attached to
	 * @param from the gate where the link originates from
	 * @param weight the initial weight of the link
	 * @param confidence the initial confidence of the link
	 * @return the newly created link
	 * @throws NetIntegrityException if the linked entity does not exist or has
	 * no slot of the given type.
	 * 
	 * @see Link
	 * @see LinkFactory
	 */
	protected Link createLinkTo(String id, int slot, Gate from, double weight, double confidence) throws NetIntegrityException {
		Link l = LinkFactory.getInstance().createLink(
					LinkTypesIF.LINKTYPE_SIMPLEASSOCIATION,
					entityManager,
					from,
					id,
					slot,
					weight,
					confidence);
		Slot s = entityManager.getEntity(id).getSlot(slot);
		if(s == null) throw new NetIntegrityException(NetIntegrityIF.BAD_SLOT,this.getID(),l);
		from.addLink(l);
		s.attachIncomingLink(l);
		entityManager.reportChangedEntity(this);
		return l;
	}
	
	/**
	 * Deletes a link originating from this entity. This will remove all
	 * references correctly, including the one at the "remote" slot. The link
	 * won't be referenced after a call to this method and will be eaten by the
	 * GC.
	 * @param gate the type of the gate the link originates from 
	 * @param to the linked entity
	 * @param slot the linked entity's slot
	 * @throws NetIntegrityException if the link is bad
	 * @throws NullPointerException if this entity doesn't have a gate of the
	 * given type
	 */
	protected void deleteLink(int gate, String to, int slot) throws NetIntegrityException {
		Gate g = getGate(gate);
		Link l = g.getLinkTo(to,slot);
		l.getLinkedSlot().detachIncomingLink(l);
		g.deleteLink(l);
		entityManager.reportChangedEntity(this);
	}

	/**
	 * This removes all incoming and outgoing links, leaving the entity
	 * completely alone. This method will clear all references so all the links
	 * can now be eaten by the GC.
	 * @throws NetIntegrityException if there are any bad links attached to this
	 * entity.
	 */
	protected void unlinkCompletely() throws NetIntegrityException {

		// tell all entities linking this entity to drop their references
		for(int i=0;i<slots.size();i++)
			slots.get(i).unlinkCompletely();		

		// drop all outgoing links
		for(int i=0;i<gates.size();i++)
			gates.get(i).unlinkCompletely();

		entityManager.reportChangedEntity(this);
	}
	
	/**
	 * @see org.micropsi.nodenet.NetIntegrityIF#checkIntegrity()
	 */
	public void checkIntegrity() throws NetIntegrityException {

		Iterator<Gate> iter = getGates();
		while(iter.hasNext()) {
			Gate g = iter.next();
			Iterator linkiter = g.getLinks();
			while(linkiter.hasNext()) {
				Link l = (Link)linkiter.next();
				if(!l.getLinkedSlot().accessIncomingLinks().contains(l))
					throw new NetIntegrityException(	
						NetIntegrityIF.BAD_LINK,
						l.getLinkedEntityID(),
						l);
			}
		}
	}
	
	/**
	 * @see org.micropsi.nodenet.NetIntegrityIF#reportIntegrityStatus()
	 */
	public String reportIntegrityStatus() {
		String toReturn = "NetIntegrity report for entity "+getID()+":\n";
		toReturn += "-- gate / slot --";
		return toReturn;
	}

	/**
	 * Returns the name of the entity, if the entity has a name. Otherwise this
	 * method will return the ID of the entity.
	 * @return String the name of the entity.
	 */
	public String getEntityName() {
		return (entityName != null) ? entityName : this.getID();
	}

	/**
	 * Sets the entity's name.
	 * @param name the entity's name.
	 */
	protected void setEntityName(String name) {
		if(	(name != null && name.equals(getID())) ||
			(name != null && name.equals(""))) {
			this.entityName = null;
		} else {
			this.entityName = name;
		}
		
		entityManager.reportChangedEntity(this);
	}

	/**
	 * Returns true if the entity has a name.
	 * @return boolean if there is a name, false otherwise
	 */
	public boolean hasName() {
		return (entityName != null);
	}
	
	/**
	 * Changes a parameter of this entity or one of it's slots, gates or hidden
	 * implementation. Possible values of parameterType can be found in
	 * NetParametersIF.
	 * @param parameterType the parameter to be changed 
	 * @param subID the subid. Depending on the parameterType used, this can be
	 * the type of a slot, a gate or the key of a parameter of a hidden
	 * implementation (typically of a NativeModule)
	 * @param newValue The new value as String
	 * @throws NetIntegrityException if the slot or gate was not found
	 * @throws NumberFormatException if a conversion of the newValue had to be
	 * done and failed
	 * @see NetParametersIF
	 */
	protected void changeParameter(int parameterType, int subID, String newValue) throws NetIntegrityException {
		try {
			switch(parameterType) {
				case NetParametersIF.PARM_ENTITY_NAME:
					setEntityName(newValue);
					break;
				case NetParametersIF.PARM_ENTITY_SLOT_ACTIVATION:
					Slot s = getSlot(subID);
					s.killActivation();
					s.putActivation(Double.parseDouble(newValue));
					break;
				case NetParametersIF.PARM_ENTITY_GATE_ACTIVATION:
					Gate g = getGate(subID);
					g.setActivation(Double.parseDouble(newValue));
					g.confirmActivation();
					if(g.isActive()) entityManager.reportActiveEntity(this);
					break;
				case NetParametersIF.PARM_ENTITY_GATE_FACTOR:
					g = getGate(subID);
					g.setGateFactor(Double.parseDouble(newValue));
					g.confirmActivation();
					if(g.isActive()) entityManager.reportActiveEntity(this);
					break;
				case NetParametersIF.PARM_ENTITY_GATE_AMP:
					g = getGate(subID);
					g.setAmpfactor(Double.parseDouble(newValue));
					g.confirmActivation();
					if(g.isActive()) entityManager.reportActiveEntity(this);
					break;
				case NetParametersIF.PARM_ENTITY_GATE_OUTPUTFUNCTION_PARAMETER:
					g = getGate(subID);
					String[] split = newValue.split("=");
					GateManipulator gman = new GateManipulator(this);
					gman.setOutputFunctionParameter(subID,split[0],Double.parseDouble(split[1]));
					g.confirmActivation();
					if(g.isActive()) entityManager.reportActiveEntity(this);
					break;
				case NetParametersIF.PARM_ENTITY_GATE_MAX:
					g = getGate(subID);
					g.setMaximum(Double.parseDouble(newValue));
					g.confirmActivation();
					if(g.isActive()) entityManager.reportActiveEntity(this);
					break;
				case NetParametersIF.PARM_ENTITY_GATE_MIN:
					g = getGate(subID);
					g.setMinimum(Double.parseDouble(newValue));
					g.confirmActivation();
					if(g.isActive()) entityManager.reportActiveEntity(this);
					break;
				case NetParametersIF.PARM_ENTITY_GATE_DECAYTYPE:
					g = getGate(subID);
					g.setDecayCalculatorType(Integer.parseInt(newValue));
					break;
				case NetParametersIF.PARM_ENTITY_GATE_OUTPUTFUNCTION:
					g = getGate(subID);
					gman = new GateManipulator(this);
					gman.setOutputFunction(subID, newValue);
					g.confirmActivation();
					if(g.isActive()) entityManager.reportActiveEntity(this);
					break;
			}
		} catch (NullPointerException e) {
			throw new NetIntegrityException(NetIntegrityIF.BAD_KEY,subID+"",this);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Class not found: "+e.getMessage()+" (Classloader: "+getClass().getClassLoader()+")");
		}
		entityManager.reportChangedEntity(this);
	}
	
	/**
	 * Updates the decay state of all links attached to this entity. Normally, this
	 * only happens just before the link's decayed weights are needed by the net,
	 * that is: when activation is propagatet on these links. However, you might want
	 * to know the link's current weights without having to propagate activation -
	 * if you work in any way with the weight of links that are subject to decays,
	 * you MUST call this method before doing so.
	 * @throws MicropsiException if there is no such entity
	 */
	public void updateDecayState() throws NetIntegrityException {
		for(int i=0;i<gates.size();i++) {
			Gate g = gates.get(i);
			g.calculateDecays(entityManager.getNetstep());
		}
	}
	
	public String toString() {
		String toReturn = getClass().getName().substring(getClass().getName().lastIndexOf('.')+1);
		if(hasName()) toReturn += " " + getEntityName() +" ";
		toReturn += " (";
		toReturn += getID();
		toReturn += ") ";
		toReturn += "[" + super.toString() +"]";
		return toReturn;
	}

	/**
	 * Returns the internal index of this entity for array access.
	 * @return
	 */
	protected int getInternalIndex() {
		return internalIndex;
	}

	
	/**
	 * Frees as many references as possible to avoid too much gc activity
	 */
	protected void destroy() {
		if(slots != null) {
			Iterator<Slot> iter = slots.iterator();
			while(iter.hasNext()) {
				Slot s = iter.next();
				s.destroy();
				iter.remove();
			}
			slots = null;
		}
		
		if(gates != null) {
			Iterator<Gate> iter = gates.iterator();
			while(iter.hasNext()) {
				Gate g = iter.next();
				g.destroy();
				iter.remove();
			}
			gates = null;
		}

		entityManager = null;		
	}	
}
