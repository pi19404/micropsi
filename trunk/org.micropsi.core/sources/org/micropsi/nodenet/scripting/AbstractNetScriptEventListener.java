/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/scripting/AbstractNetScriptEventListener.java,v 1.1 2004/11/23 01:27:13 vuine Exp $ 
 */
package org.micropsi.nodenet.scripting;

import org.micropsi.nodenet.LocalNetFacade;

/**
 * AbstractNetScriptEventListener.
 * Abstract ancestor of all net script event listeners.
 *
 * @author rv
 */
abstract class AbstractNetScriptEventListener {

	/**
	 * Installs this listener at the given net. 
	 * @param net the net where the listener is to be installed.
	 */
	abstract void install(LocalNetFacade net);
	
	/**
	 * Deinstalls this listener from the given net. 
	 * @param net the net where this listener is registered currently
	 */
	abstract void deinstall(LocalNetFacade net);

}
