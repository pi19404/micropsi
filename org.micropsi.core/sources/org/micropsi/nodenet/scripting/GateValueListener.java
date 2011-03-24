/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/scripting/GateValueListener.java,v 1.2 2005/01/02 20:53:02 vuine Exp $ 
 */
package org.micropsi.nodenet.scripting;

import org.micropsi.nodenet.CycleObserverIF;
import org.micropsi.nodenet.Gate;
import org.micropsi.nodenet.LocalNetFacade;

/**
 * GateValueListener
 * Listener to listen for value changes at one specific gate.
 * Subclass and install this to get notified when a gate value changes. 
 *
 * @author rv
 */
public abstract class GateValueListener extends AbstractNetScriptEventListener implements CycleObserverIF {

	private Gate gate;
	private double previousValue;
	
	/*
	 * (non-Javadoc)
	 * @see org.micropsi.nodenet.scripting.AbstractNetScriptEventListener#install(org.micropsi.nodenet.LocalNetFacade)
	 */
	protected final void install(LocalNetFacade net) {
		this.gate = getGate();
		previousValue = gate.getConfirmedActivation();
		net.getCycle().registerCycleObserver(this);
	}

	/* (non-Javadoc)
	 * @see org.micropsi.nodenet.scripting.AbstractNetScriptEventListener#deinstall(org.micropsi.nodenet.LocalNetFacade)
	 */
	protected final void deinstall(LocalNetFacade net) {
		net.getCycle().unregisterCycleObserver(this);
	}

	/* (non-Javadoc)
	 * @see org.micropsi.nodenet.CycleObserverIF#startCycle(long)
	 */
	public void startCycle(long netStep) {
		
	}

	/* (non-Javadoc)
	 * @see org.micropsi.nodenet.CycleObserverIF#endCycle(long)
	 */
	public void endCycle(long netStep) {		
		if(previousValue != gate.getConfirmedActivation()) {
			fire(netStep, gate.getConfirmedActivation());
		}
		previousValue = gate.getConfirmedActivation();
	}
	
	/**
	 * Must return the gate that this listener should monitor. Whenever
	 * the output value of this gate changes, the listener fires.
	 * 
	 * @return the gate to be monitored. Must not be null.
	 */
	public abstract Gate getGate();

	/**
	 * Called when the gate value changed.
	 * 
	 * @param netStep the step when the change happened
	 * @param newActivation the new value of the gate.
	 */
	public abstract void fire(long netStep, double newActivation);

}
