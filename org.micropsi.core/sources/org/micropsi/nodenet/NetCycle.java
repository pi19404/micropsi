/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/NetCycle.java,v 1.13 2006/10/15 15:39:49 rvuine Exp $
 */
package org.micropsi.nodenet;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A NetCycle object can be used to run a MicroPSI net, that is: to propagate
 * activation and calculate new gate values.
 */
public class NetCycle implements NetCycleIF {
	
	private Logger logger;
	private NetEntityManager entityManager;
	private ModuleManager spaceManager;
	private ArrayList<WeakReference<CycleObserverIF>> cycleObservers = new ArrayList<WeakReference<CycleObserverIF>>();
	private boolean blocked = false;
	private boolean suspended = false;
	long lastCycleLength = 0;
	
	/**
	 * Constructor. 
	 * @param entityManager the entity manager of the net
	 * @param spaceManager the space manager of the net
	 * @param logger the logger
	 */
	protected NetCycle(NetEntityManager entityManager, ModuleManager spaceManager, Logger logger) {
		this.entityManager = entityManager;
		this.spaceManager = spaceManager;
		this.logger = logger;
	}
	
	/**
	 * Calculates the new gate values. This calls calculateGates() on all netEntities
	 * with the "changed" or the "defiant" flag, that ist: all entities that got 
	 * activation in the last cycle or are marked defiant. 
	 * When this method is invoked first, it calls calculateGates() on
	 * all entities to give them a chance to initialize and fill their gates with
	 * the first activations.<br><br>
	 * This sets the "active" flags according to the return values of the abstract
	 * calculateGates() method. NetEntities that want to have their activation
	 * propagated should return true from calculateGates().
	 */
	private void calculateNewActivation() throws NetIntegrityException {

		// reset the hasDeletedLinks flag on all node spaces
		spaceManager.resetDeletedLinksFlags();

		// first pass: calculate new gate values

		Iterator<String> defiantEntityIDs = entityManager.accessDefiantEntitiesIDList().iterator();
		while(defiantEntityIDs.hasNext()) {
			NetEntity e = entityManager.getEntity(defiantEntityIDs.next());
			
			// remove the entity from the changed entities list so no check is necessary
			// to prevent defiant entities from being calculated twice.
			entityManager.accessChangedEntitiesIDList().set(e.getInternalIndex(),null);
			e.calculateGates();
		}
				
		Iterator<NetEntity> changedEntities = entityManager.accessChangedEntitiesIDList().iterator();
		while(changedEntities.hasNext()) {
			NetEntity changed = changedEntities.next();
			if(changed == null) continue;
			changed.calculateGates();
		}
		
		// second pass: confirm them for the next cycle

		defiantEntityIDs = entityManager.accessDefiantEntitiesIDList().iterator();
		while(defiantEntityIDs.hasNext()) {
			NetEntity e = entityManager.getEntity(defiantEntityIDs.next());
			e.confirmNewActivation();
		}
				
		changedEntities = entityManager.accessChangedEntitiesIDList().iterator();
		while(changedEntities.hasNext()) {
			NetEntity changed = changedEntities.next();
			if(changed == null) continue;
			changed.confirmNewActivation();
		}
	
	}
	
	/**
	 * Propagates activation along the links. Only activation of entities with the
	 * "active" flag is propagated.<br><br>
	 * This sets the "changed" flag on the affected Entities so their calculateGates()
	 * method will be called in the next cycle.
	 */
	private void propagateActivation() throws NetIntegrityException {		
		Iterator<NetEntity> it = entityManager.accessActiveEntitiesIDList().iterator();	
		while(it.hasNext()) {
			NetEntity e = it.next();
			if(e == null) continue;
			e.propagateActivation();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.micropsi.nodenet.NetCycleIF#nextCycle(boolean)
	 */
	public synchronized boolean nextCycle(boolean considerSuspension) throws NetIntegrityException {
		
		if(blocked) return false;
		if(considerSuspension && suspended) return false;
		
		long start = System.currentTimeMillis();
		
		try {
			notifyObserversOfCycleEnd(entityManager.getNetstep());
		} catch (Throwable e) {
			logger.error("Observer notification problem", e);
		}
	
		entityManager.increaseNetStep();
		
		synchronized(this) {

			// propagate all active entities, creating the new changes
			propagateActivation();
					
			// calculate all entities with changes and create the new active flags
			List<NetEntity> l = entityManager.accessActiveEntitiesIDList();
			for(int i=0;i<l.size();i++) {
				l.set(i,null);
			}

			calculateNewActivation();
		}
			
		try {
			entityManager.notifyObservers();
			spaceManager.notifyObservers();
			
			entityManager.accessDeletedEntitiesIDList().clear();
			entityManager.accessNewEntitiesIDList().clear();
			List<NetEntity> l = entityManager.accessChangedEntitiesIDList();
			for(int i=0;i<l.size();i++) {
				l.set(i,null);
			}
			
			notifyObserversOfCycleStart(entityManager.getNetstep());
		} catch (Throwable e) {
			logger.error("Observer notification problem", e);
		}

		lastCycleLength = System.currentTimeMillis()-start;
		
		return true;
			
	}
	
	/**
	 * Blocks the net. The net can not proceed while blocked. (There is no way
	 * of bypassing this). Blockage and suspend mode are handled indepenently.
	 */	
	public void block() {
		blocked = true;
	}
	
	/**
	 * Remove the blockage. Blockage and suspend mode are handled indepenently.
	 */
	public void unblock() {
		blocked = false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.nodenet.NetCycleIF#isBlocked()
	 */
	public boolean isBlocked() {
		return blocked;
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.nodenet.NetCycleIF#suspend()
	 */
	public void suspend() {
		if(!suspended) {
			logger.debug(entityManager.getNetstep()+": net suspended");
			suspended = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.nodenet.NetCycleIF#resume()
	 */
	public void resume() {
		if(suspended) {
			logger.debug(entityManager.getNetstep()+": net resumed");
			suspended = false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.micropsi.nodenet.NetCycleIF#isSuspended()
	 */
	public boolean isSuspended() {
		return suspended;
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.nodenet.NetCycleIF#continueNCycles(int, boolean)
	 */
	public void continueNCycles(int n, boolean considerSuspension) throws NetIntegrityException {
		for(int i=0;i<n;i++) nextCycle(considerSuspension);
	}
	
	private void notifyObserversOfCycleStart(long netStep) {
		for(int i=cycleObservers.size()-1;i>=0;i--) {
			WeakReference ref = cycleObservers.get(i);
			Object o = ref.get();
			if(o == null) {
				cycleObservers.remove(i);
			} else {
				((CycleObserverIF)o).startCycle(netStep);
			}
		}
	}

	private void notifyObserversOfCycleEnd(long netStep) {
		for(int i=cycleObservers.size()-1;i>=0;i--) {
			WeakReference ref = cycleObservers.get(i);
			Object o = ref.get();
			if(o == null) {
				cycleObservers.remove(i);
			} else {
				((CycleObserverIF)o).endCycle(netStep);
			}
		}
	}
	
	public long getLastCycleLength() {
		return lastCycleLength;
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.nodenet.NetCycleIF#registerCycleObserver(org.micropsi.nodenet.CycleObserverIF)
	 */
	public void registerCycleObserver(CycleObserverIF observer) {		
		for(int i=cycleObservers.size()-1;i>=0;i--) {
			WeakReference ref = cycleObservers.get(i);
			Object o = ref.get();
			if(o == null) {
				cycleObservers.remove(i);
			} else {
				if(o.equals(observer)) 
					return;
			}
		}
		
		WeakReference<CycleObserverIF> ref = new WeakReference<CycleObserverIF>(observer);
		cycleObservers.add(ref);
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.nodenet.NetCycleIF#unregisterCycleObserver(org.micropsi.nodenet.CycleObserverIF)
	 */
	public void unregisterCycleObserver(CycleObserverIF observer) {
		for(int i=cycleObservers.size()-1;i>=0;i--) {
			WeakReference ref = cycleObservers.get(i);
			Object o = ref.get();
			if(o == null) {
				cycleObservers.remove(i);
			} else {
				if(o.equals(observer)) {
					cycleObservers.remove(i);
					return;
				}
			}
		}
	}

	public String toString() {
		return "netstep: "+entityManager.getNetstep();
	}
}
