package org.micropsi.nodenet;
/**
 * 
 * 
 * 
 */
public interface NetCycleIF {
	
	/**
	 * Proceeds one cycle: propagates all activation and calculates the new gate
	 * values.
	 * @param considerSuspension if false, the net will proceed even if in
	 * suspend mode
	 * @return boolean true if the net really proceeded, false if it was blocked
	 * or suspendedn (and considerSuspension was false)
	 * @throws NetIntegrityException if activation is propagated along a bad
	 * link
	 */
	public boolean nextCycle(boolean considerSuspension) throws NetIntegrityException;
	
	/**
	 * Checks if the net is currently blocked
	 * @return boolean true if the net is blocked
	 */
	public boolean isBlocked();
	
	/**
	 * Puts the net into suspend mode. The net can only proceed when the
	 * nextCycle method is explicitly told to bypass suspend mode.
	 */
	public void suspend();
	
	/**
	 * Resumes the net out of suspend mode.
	 */
	public void resume();
	
	/**
	 * Checks if the net is currently in suspend mode.
	 * @return boolean if the net is in suspend mode
	 */
	public boolean isSuspended();
	
	/**
	 * Proceeds n netsteps.
	 * @param n how many steps to proceed
	 * @param considerSuspension proceed even if the net is suspended?
	 * @throws NetIntegrityException if there are bad links
	 */
	public void continueNCycles(int n, boolean considerSuspension) throws NetIntegrityException;

	/**
	 * Returns the length of the last cycle in ms
	 * @return the length of the last cycle in ms
	 */
	public long getLastCycleLength();
	
	/**
	 * Register an observer for getting notifications when cycles change.
	 * @param observer the observer to be registered
	 */
	public void registerCycleObserver(CycleObserverIF observer);
	
	/**
	 * Unregister a previously registered observer.
	 * @param observer the observer to be unregistered.
	 */
	public void unregisterCycleObserver(CycleObserverIF observer);
	
}