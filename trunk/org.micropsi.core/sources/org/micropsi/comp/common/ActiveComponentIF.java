/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/common/ActiveComponentIF.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.common;

/**
 * AbstractComponents that need to be run as threads must implement this
 * interface.
 */

public interface ActiveComponentIF extends Runnable {
	
	public void start();
	
	public void run();

}
