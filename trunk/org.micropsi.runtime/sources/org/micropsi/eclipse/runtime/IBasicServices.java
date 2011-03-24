/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.runtime/sources/org/micropsi/eclipse/runtime/IBasicServices.java,v 1.2 2004/08/10 14:40:51 fuessel Exp $ 
 */
package org.micropsi.eclipse.runtime;

import org.apache.log4j.Logger;


public interface IBasicServices {

	public Logger getLogger();

	public String handleException(Throwable e);
	
	public String getServerID();

}
