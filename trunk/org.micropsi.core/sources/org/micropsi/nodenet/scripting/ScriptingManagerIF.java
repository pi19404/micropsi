/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/scripting/ScriptingManagerIF.java,v 1.1 2004/11/23 01:27:13 vuine Exp $ 
 */
package org.micropsi.nodenet.scripting;

/**
 * ScriptingManagerIF
 * Interface to be implemented by agents that use nodenets and want
 * to offer scripting.
 *
 * @author rv
 */
public interface ScriptingManagerIF {

	/**
	 * Executes a script.
	 * 
	 * @param script the script to be executed.
	 * @return true iff this scripting manager could execute the script at
	 * the current point of time.
	 */
	public boolean executeScript(Script script);

	/**
	 * Sends a terminate request to the current script.
	 *  
	 * @return false iff there was no current script. 
	 */
	public boolean terminateScript();
	
	/**
	 * Forces the script's thread to stop. This is meant to be the
	 * last ressort and should not be used programmatically to stop a script.
	 */
	public void forceTerminateScript();

	/**
	 * Adds a listener to get notified when the script state changes. 
	 * @param listener the listener, must not be null
	 */
	public void addScriptStateListener(ScriptStateListenerIF listener);

	/**
	 * Removes a script state listener
	 * 
	 * @param listener the listener, must not be null.
	 */
	public void removeScriptStateListener(ScriptStateListenerIF listener);
}