/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/scripting/ScriptThread.java,v 1.6 2005/07/12 12:55:17 vuine Exp $ 
 */
package org.micropsi.nodenet.scripting;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.UserInteractionIF;
import org.micropsi.nodenet.agent.Situation;

/**
 * ScriptThread
 * The thread that runs a script. This class is meant to be used by
 * implementors of ScriptingManagerIF.
 *
 * @author rv
 */
public final class ScriptThread extends Thread {
	
	private Script script;
	private Logger logger;
	private LocalNetFacade net;
	private ArrayList<ScriptStateListenerIF> stateListeners = new ArrayList<ScriptStateListenerIF>();
	private UserInteractionIF originalUserInteraction;
	
	/**
	 * Constructor with parameters for script initialisation.
	 * 
	 * @param script the script
	 * @param net the net
	 * @param logger the logger
	 */
	public ScriptThread(Script script, LocalNetFacade net, Situation situation, Logger logger) {
		super("micropsi script");
		this.script = script;
		this.net = net;
		this.logger = logger;
		script.setup(this,net,situation,logger);
		
		originalUserInteraction = net.getUserInteractionImplementation();
		if(script instanceof UserInteractionIF) {
			net.setUserInteractionImplementation((UserInteractionIF)script);
		}
	}
	
	/**
	 * Adds a listener to get notified when the script state changes. 
	 * @param listener the listener, must not be null
	 */
	public void addScriptStateListener(ScriptStateListenerIF listener) {
		stateListeners.add(listener);
	}
	
	/**
	 * Removes a script state listener
	 * 
	 * @param listener the listener, must not be null.
	 */
	public void removeScriptStateListener(ScriptStateListenerIF listener) {
		stateListeners.remove(listener);
	}
	
	/**
	 * Actually runs the script.
	 */
	public void run() {
		// notify all state listeners (eg. GUIs)
		for(int i=0;i<stateListeners.size();i++) {
			ScriptStateListenerIF listener = stateListeners.get(i);
			listener.scriptStarted();
		}
		
		// run the script
		try {
			script.run();
		} catch(Exception e) {
			logger.error("Script execution failed: ", e);
		}
		
		terminateScript();
		
		// ensure that the net is free of script listeners
		script.unregisterAllListeners();

		net.setUserInteractionImplementation(originalUserInteraction);
		
		// notify all state listeners of the script termination
		for(int i=0;i<stateListeners.size();i++) {
			ScriptStateListenerIF listener = stateListeners.get(i);
			listener.scriptTerminated();
		}
	}
		
	/**
	 * Asks the script to terminate.
	 */
	public void terminateScript() {
		script.terminate();
	}
	
	/**
	 * Forces the script's thread to stop. This is meant to be the
	 * last ressort and should not be used programmatically to stop a script.
	 */
	public void forceTerminateScript() {
		// ensure that the net is free of script listeners
		script.unregisterAllListeners();

		net.setUserInteractionImplementation(originalUserInteraction);
		
		// notify all state listeners of the script termination
		for(int i=0;i<stateListeners.size();i++) {
			ScriptStateListenerIF listener = stateListeners.get(i);
			listener.scriptTerminated();
		}

		this.stop(); 
	}

}
