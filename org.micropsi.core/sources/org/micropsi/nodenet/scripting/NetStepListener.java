/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/scripting/NetStepListener.java,v 1.1 2004/11/23 01:27:13 vuine Exp $ 
 */
package org.micropsi.nodenet.scripting;

import org.micropsi.nodenet.CycleObserverIF;
import org.micropsi.nodenet.LocalNetFacade;

/**
 * NetStepListener.
 * A listener that fires when the net step changes.
 *
 * @author rv
 */
public abstract class NetStepListener extends AbstractNetScriptEventListener implements CycleObserverIF {
	
	/*
	 * (non-Javadoc)
	 * @see org.micropsi.nodenet.scripting.AbstractNetScriptEventListener#install(org.micropsi.nodenet.LocalNetFacade)
	 */
	protected final void install(LocalNetFacade net) {
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
		fire(netStep);
	}

	/**
	 * Called when the net went on one step.
	 * 
	 * @param netStep the new net step.
	 */
	public abstract void fire(long netStep);

}
