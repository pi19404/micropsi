/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/CycleObserverIF.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.nodenet;

/**
 * Implementations of this interface can register with the net to receive
 * notification whenever the net starts or ends a cycle (also called "netstep")
 * Normally, you won't want to implement both methods, as it is probably only
 * important to you to do one calculation per step. Anyway, there is a
 * difference in the inner states of the net between the two points of time: The
 * time between the calls to endCycle() and startCycle() should be nearly zero
 * in realtime, but the netstep has increased by one during that time. When
 * startCycle() is called, activation is still in the slots. When endCycle() is
 * called, the new activation of the gates has been calculated and is about to
 * be propagated along the links.
 */
public interface CycleObserverIF {

	/**
	 * This method will be called when a new cycle (aka "netstep") just started.
	 * The gates of the nodes are not calculated yet, and all activation is
	 * still in the slots. 
	 * @param netStep the current netstep.
	 */
	public void startCycle(long netStep);
	
	/**
	 * This method will be called when the current cycle is about to end and all
	 * gates have been just calculated. All slots have zero activation.
	 * @param netStep the netstep just about to end.
	 */
	public void endCycle(long netStep);
	
}
