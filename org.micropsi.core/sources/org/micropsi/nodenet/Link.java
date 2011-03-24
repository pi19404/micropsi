/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/Link.java,v 1.3 2004/09/05 15:37:49 vuine Exp $
 */
package org.micropsi.nodenet;

/**
 * Links connect NetEntities. They are stored and maintained at gates and end in
 * slots. When activation is propagated along some link, the activation is
 * multiplied with the link's weight and confidence value. Links can be "bad":
 * this is the case if any of the both entities does not exist or does not have
 * the slot/gate of the link. When the net comes across a bad link, a
 * NetIntegrityException is thrown. A bad link is a sign that there is something
 * seriously wrong with the implementation of some entity runnig inside the net:
 * Either the Node/NodeSpace implementations are buggy or (more likely) some
 * additional NativeModuleImplementation is the evildoer.
 */
public class Link implements NetIntegrityIF {
	
	public static final int LINKPARAM_WEIGHT = 0;
	public static final int LINKPARAM_CONFIDENCE = 1;
	
	private Gate from;
	
	private String to;
	private int toSlot;
	
	private Slot toCache;
	
	private double weight;
	private double confidence;
	private boolean usedFlag;
	private NetEntityManager manager;
	
	private Link() {
	}
	
	/**
	 * Creates the link. This alone does not yet connect the two entities, it
	 * merely creates the instance, which will have to be placed at the Gate
	 * passed as the "from" parameter and attached to the correct slot. The
	 * creation of the link does not check for NetIntegrity and explicitly
	 * allows the creation of bad links (as there may be good reasons for doing
	 * so, e.g. when re-constructing the net from a file)<br/><br/>
	 * This should only be called by the LinkFactory!
	 * @param from the gate where the link originates
	 * @param to the linked entity's ID
	 * @param slot the slot at the linked entity to be used
	 * @param manager the NetEntityManager
	 * @param weight the initial weight of the link
	 * @param confidence the initial confidence of the link
	 * @see LinkFactory
	 */
	protected Link(Gate from, String to, int slot, NetEntityManager manager, double weight, double confidence) {
		this.from = from;
		this.to = to;
		this.toSlot = slot;
		try {
			if(manager.entityExists(to)) toCache = manager.getEntity(to).getSlot(slot);
		} catch (NetIntegrityException e) {}
		this.weight = weight;
		this.manager = manager;
		this.confidence = confidence;
	}
	
	/**
	 * Returns the linked entity's ID.
	 * @return String the ID
	 */
	public String getLinkedEntityID() {
		return to;
	}
	
	/**
	 * Returns the slot in which the link ends - an instance. If the link is
	 * bad, and the linked slot does not exist at the linked entity or the
	 * linked entity itself is absent, this will fail.
	 * @return Slot the linked slot.
	 * @throws NetIntegrityException if the link is bad.
	 */
	public Slot getLinkedSlot() throws NetIntegrityException {
		if(toCache != null) return toCache;
		toCache = manager.getEntity(to).getSlot(toSlot);
		return toCache;
	}
	
	/**
	 * Returns the linked entity. 
	 * @return NetEntity the linked entity
	 * @throws NetIntegrityException if the link is bad.
	 */
	public NetEntity getLinkedEntity() throws NetIntegrityException {
		return getLinkedSlot().getNetEntity(); 
	}

	/**
	 * Returns the gate that the link originates from.
	 * @return Gate the gate.
	 */
	public Gate getLinkingGate() {
		return from;
	}
	
	/**
	 * Returns the linking entity. This is a convenience method, the
	 * link itself only knows it's gate.
	 * @return NetEntity
	 */
	public NetEntity getLinkingEntity() {
		return from.getNetEntity();
	}
	
	/**
	 * Returns the link's current weight.
	 * @return double
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * Sets a new weight for the link
	 * @param weight
	 */
	protected void setWeight(double weight) {
		this.weight = weight;
	}
	
	/**
	 * Checks if this link violates the net's integrity (= is bad)
	 * @see org.micropsi.nodenet.NetIntegrityIF#checkIntegrity()
	 */
	public void checkIntegrity() throws NetIntegrityException {
		if(to == null) 
			throw new NetIntegrityException(NetIntegrityIF.UNKNOWN_ENTITY,"null",this);
		if(!manager.entityExists(to))
			throw new NetIntegrityException(NetIntegrityIF.UNKNOWN_ENTITY,to,this); 
		if(from == null) 
			throw new NetIntegrityException(NetIntegrityIF.UNKNOWN_ENTITY,"null",this);
		if(!manager.entityExists(from.getNetEntity().getID()))
			throw new NetIntegrityException(NetIntegrityIF.UNKNOWN_ENTITY,from.getNetEntity().getID(),this);
	}
	
	/**
	 * Returns the (technical) type of the link (simple association, spacio-
	 * temporal link etc)
	 * @return int the link's type
	 */
	public int getType() {
		return LinkTypesIF.LINKTYPE_SIMPLEASSOCIATION;
	}
	
	/**
	 * @see org.micropsi.nodenet.NetIntegrityIF#reportIntegrityStatus()
	 */
	public String reportIntegrityStatus() {
		return "From "+from+" to "+to;
	}
	
	/**
	 * Returns the confidence.
	 * @return double
	 */
	public double getConfidence() {
		return confidence;
	}

	/**
	 * Sets the confidence.
	 * @param confidence The confidence to set
	 */
	protected void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	
	/**
	 * Sets the "used" flag.
	 * @param flag
	 */
	protected void setUsed(boolean flag) {
		this.usedFlag = flag;
	}
	
	/**
	 * Checks the "used" flag.
	 * @return boolean
	 */
	protected boolean wasUsed() {
		return usedFlag;
	}

	protected void destroy() {
		manager = null;
		from = null;	
		toCache = null;
		to = null;
	}

}
