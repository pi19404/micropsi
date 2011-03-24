/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/communication/ComChannelServerIF.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.communication;

import org.micropsi.common.exception.MicropsiException;

public interface ComChannelServerIF {
	
	public ComChannelResponse processRequest(ComChannelRequest request) throws MicropsiException;
	
	public void registerRequestHandler(RequestHandlerIF handler) throws MicropsiException;
	
	public void deregisterRequestHandler(String requestname) throws MicropsiException;
	
	public String getServerName();
	
	public void setServerName(String name);
	
}
