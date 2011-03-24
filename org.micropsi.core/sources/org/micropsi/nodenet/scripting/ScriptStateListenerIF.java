/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/scripting/ScriptStateListenerIF.java,v 1.1 2004/11/23 01:27:13 vuine Exp $ 
 */
package org.micropsi.nodenet.scripting;

/**
 * ScriptStateListenerIF
 * Offers methods that get called when the script actually did start or
 * stop.
 *
 * @author rv
 */
public interface ScriptStateListenerIF {

	/**
	 * Called immediately before the script starts. 
	 */
	public void scriptStarted();
		
	/**
	 * Called immediately after the script terminated.
	 */
	public void scriptTerminated();
	
}
