/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/Slot.java,v 1.8 2006/08/03 15:40:37 rvuine Exp $
 */
package org.micropsi.nodenet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Slots are the endpoints of links. During activation propagation, slots
 * collect the activation of the incoming links. <br/><br/> There isn't much
 * magic to slots - they are basically a varible with the incoming activation
 * and a list of incoming links to allow an entity to unlink itself.
 */
public class Slot {
	
	private static final int DEFAULT_SLOTLOAD_ASSUMPTION = 2;
	
	/**
	 * The slot's activation. 
	 */
	protected double activation = 0;
	private int type = -1;
	private ArrayList<Link> incomingLinks = new ArrayList<Link>(DEFAULT_SLOTLOAD_ASSUMPTION);
	private NetEntity center;
	private NetEntityManager manager;
	
	/**
	 * Creates a new Slot. Should only be called by implementations of
	 * NetEntity.
	 * @param type the type of the new slot
	 * @param center the NetEntity the Slot belongs to
	 * @param manager the entity manager
	 */
	protected Slot(int type, NetEntity center, NetEntityManager manager) {
		this.type = type;
		this.center = center;
		this.manager = manager;
	}	

	/**
	 * Returns the type of the slot. Remember that there can only be one slot of
	 * a type at an entity.
	 * @return int the type
	 * @see SlotTypesIF
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Returns the entity the slot belongs to.
	 * @return NetEntity the entity
	 */
	public NetEntity getNetEntity() {
		return center;
	} 
	
	/**
	 * Puts activation into the slot. Adds the activation value to the
	 * slot's value.
	 * @param act the value to be added to the slot's activation
	 */
	protected void putActivation(double act) {
		activation += act;
		manager.reportChangedEntity(center);
	}
	
	/**
	 * Takes activation from the slot. Decreases the slot's activation but
	 * only down to a minimum of zero.
	 * @param act the value to be substracted from the slot's activation
	 */
	protected void takeActivationGreaterThanZero(double act) {
		activation -= act;
		if(activation < 0) {
			activation = 0;
		}
		manager.reportChangedEntity(center);
	}
	
	/**
	 * Sets the slot's activation to zero.
	 */
	protected void killActivation() {
		activation = 0;
	}
	
	/**
	 * Returns the slot's activation.
	 * @return double the slot's activation.
	 */
	public double getIncomingActivation() {
		return activation;
	}
	
	/**
	 * Attaches an incoming link. This method is low-level and does not check
	 * for net integrity nor does it ensure it. This method should only be
	 * called if the link contains this slot as its target.
	 * @param link the link to be added to the list of incoming links.
	 */
	protected void attachIncomingLink(Link link) {
		incomingLinks.add(link);
	}
	
	/**
	 * Detaches an incoming link. This method is low-level and does not care
	 * about net integrity. It should only be called if the link's instance is
	 * about to be dropped.
	 * @param link the link to be deleted from the list of incoming links.
	 */
	protected void detachIncomingLink(Link link) {
		incomingLinks.remove(link);
	}
	
	/**
	 * Retuns the instances of all incoming links. Don't try remove(), you won't
	 * like the results.
	 * @return Iterator the links.
	 */
	public Iterator<Link> getIncomingLinks() {
		return incomingLinks.iterator();
	}
	
	/**
	 * Returns the n-th link attached to the slot.
	 * @param n the index of the link
	 * @return Link the n-th link or null if there is no such link.
	 */
	public Link getIncomingLinkAt(int n) {
		if(n>=incomingLinks.size()) return null;
		return incomingLinks.get(n);
	}
	
	/**
	 * Checks if this slot has incoming links
	 * @return true if the slot has links
	 */
	public boolean hasIncomingLinks() {
		return incomingLinks.size() > 0;
	}
	
	/**
	 * Returns the number of links incoming at that gate.
	 * @return the number of links
	 */
	public int getNumberOfIncomingLinks() {
		return incomingLinks.size();
	}
	
	/**
	 * Provides direct access to the list of incoming links. Know what you're
	 * doing when calling this. This is NOT a way to change the net's structure
	 * in any way!
	 * @return ArrayList the links
	 */
	protected List<Link> accessIncomingLinks() {
		return incomingLinks;
	}
	
	/**
	 * Unlinks the slot completely, that is: Kills all links that end at the
	 * slot. This method preserves the net's integrity and removes the links
	 * from the linking gates also.
	 */
	protected void unlinkCompletely() {
		for(int i=incomingLinks.size()-1;i>=0;i--) {
			Link link = incomingLinks.get(i);
			
			// delete link from the gate that links us
			link.getLinkingGate().deleteLink(link);
						
			// delete our "incoming" reference to the link
			incomingLinks.remove(i);
			
			link.destroy();
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString()+" Type: "+type+" Act: "+activation;
	}
		
	/**
	 * Frees as many references as possible to avoid too much gc activity
	 */
	protected void destroy() {
		Iterator iter = getIncomingLinks();
		while(iter.hasNext()) {
			Link l = (Link)iter.next();
			l.destroy();
			iter.remove();
		}
		incomingLinks = null;
		manager = null;
		center = null;
	}

}
