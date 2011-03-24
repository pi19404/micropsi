/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/scripting/AgentScriptingManager.java,v 1.6 2005/07/12 12:55:16 vuine Exp $ 
 */
package org.micropsi.comp.agent.micropsi.scripting;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.scripting.Script;
import org.micropsi.nodenet.scripting.ScriptStateListenerIF;
import org.micropsi.nodenet.scripting.ScriptThread;
import org.micropsi.nodenet.scripting.ScriptingManagerIF;

/**
 * AgentScriptingManager
 * MicroPsi agent implementation of the ScriptingManagerIF.
 * 
 * @author rv
 */
public class AgentScriptingManager implements ScriptingManagerIF {
	
	private Logger logger;
	private LocalNetFacade net;
	private MicroPsiAgent agent;
	
	private ArrayList<ScriptStateListenerIF> stateListenerCache = new ArrayList<ScriptStateListenerIF>();
	
	/**
	 * Constructor with initialisation parameters for script threads.
	 * 
	 * @param agent the agent
	 * @param net the net 
	 * @param logger the logger
	 */
	public AgentScriptingManager(LocalNetFacade net, MicroPsiAgent agent, Logger logger) {
		this.logger = logger;
		this.net = net;
		this.agent = agent;
	}
	
	private ScriptThread currentScriptThread = null;
	
	/*
	 * (non-Javadoc)
	 * @see org.micropsi.nodenet.scripting.ScriptingManagerIF#executeScript(org.micropsi.nodenet.scripting.Script)
	 */
	public boolean executeScript(Script script) {		
		if(currentScriptThread != null && currentScriptThread.isAlive()) return false;
		
		currentScriptThread = new ScriptThread(script,net,agent.getSituation(),logger);
		for(int i=0;i<stateListenerCache.size();i++) {
			ScriptStateListenerIF listener = stateListenerCache.get(i);
			currentScriptThread.addScriptStateListener(listener);
		}
				
		currentScriptThread.start();
		
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.micropsi.nodenet.scripting.ScriptingManagerIF#terminateScript()
	 */
	public boolean terminateScript() {
		if(currentScriptThread == null) return false;
		
		currentScriptThread.terminateScript();
		return true;		
	}

	/*(non-Javadoc)
	 * @see org.micropsi.nodenet.scripting.ScriptingManagerIF#forceTerminateScript()
	 */
	public void forceTerminateScript() {
		if(currentScriptThread == null) return;
		currentScriptThread.forceTerminateScript();
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.nodenet.scripting.ScriptingManagerIF#addScriptStateListener(org.micropsi.nodenet.scripting.ScriptStateListenerIF)
	 */
	public void addScriptStateListener(ScriptStateListenerIF listener) {
		stateListenerCache.add(listener);
		if(currentScriptThread != null) {
			currentScriptThread.addScriptStateListener(listener);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.micropsi.nodenet.scripting.ScriptingManagerIF#removeScriptStateListener(org.micropsi.nodenet.scripting.ScriptStateListenerIF)
	 */
	public void removeScriptStateListener(ScriptStateListenerIF listener) {
		stateListenerCache.remove(listener);
		if(currentScriptThread != null) {
			currentScriptThread.removeScriptStateListener(listener);
		}		
	}

	
}
