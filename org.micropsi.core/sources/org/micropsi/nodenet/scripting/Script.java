/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/scripting/Script.java,v 1.4 2005/07/12 12:55:17 vuine Exp $ 
 */
package org.micropsi.nodenet.scripting;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.agent.Situation;

/**
 * Script. Abstract ancestor of all scripts that can be executed by an
 * implementation of ScriptingManagerIF.
 * 
 * Scripts are meant to be run in a separate thread, so the run() method
 * defines how long the script is alive. terminate() comes from outside,
 * telling the run() method to stop. run() and terminate must be implemented
 * correctly or you will mess up the ScriptingManagerIF implementation.
 *
 * This abstract baseclass allows the registration of 
 * AbstractNetScriptEventListeners that fire upon different net events like
 * netstep changes, gate or slot value changes etc.
 *
 * @author rv
 */
public abstract class Script {
	
	private LocalNetFacade net;
	private Logger logger;
	private Situation situation;
	
	private ArrayList<AbstractNetScriptEventListener> netListeners = new ArrayList<AbstractNetScriptEventListener>();
		
	/**
	 * Setup method. Do not call this. This is meant to be called only by the
	 * ScriptThread. 
	 * @param thread The script thread calling.
	 * @param net The net.
	 * @param logger The logger for this script.
	 */
	final void setup(ScriptThread thread, LocalNetFacade net, Situation situation, Logger logger) {
		this.net = net;
		this.situation = situation;
		this.logger = logger;
	}
		
	/**
	 * Returns the logger.
	 * 
	 * @return the logger.
	 */
	protected Logger getLogger() {
		return logger;
	}
	
	/**
	 * Returns the agent's current situation
	 * 
	 * @return the situation
	 */
	protected Situation getSituation() {
		return situation;
	}
	
	/**
	 * Registern an AbstractNetScriptEventListener. Note that after the run()
	 * method has finished, all listeners will automatically removed. So you
	 * will have to keep alive run() as long you want to listen for events.
	 * @param listener a listener
	 */
	protected void registerEventListener(AbstractNetScriptEventListener listener) {
		listener.install(net);
		netListeners.add(listener);
	}
	
	/**
	 * Unregisters the given listener. 
	 * @param listener the listener to be unregistered.
	 */
	protected void unregisterEventListener(AbstractNetScriptEventListener listener) {
		netListeners.remove(listener);
		listener.deinstall(net);
	}
	
	/**
	 * Unregisters all registered listeners.
	 */
	protected void unregisterAllListeners() {
		for(int i=0;i<netListeners.size();i++) {
			AbstractNetScriptEventListener listener = netListeners.get(i);
			listener.deinstall(net);
		}
		netListeners.clear();
	}
	
	/**
	 * Runs the script. This method <b>must</b> terminate! Pay
	 * attention to terminate() calls to get notification of the user's whish
	 * to cancel.
	 * Keep this method alive as long as you want to listen for events from
	 * the registered listeners! When this method finishes, all listeners are
	 * removed and the script is done.
	 * 
	 * @throws MicropsiException,InterruptedException
	 */
	public abstract void run() throws MicropsiException,InterruptedException;
	
	/**
	 * Called when the run() method should come to an end.
	 */
	public abstract void terminate();

}
