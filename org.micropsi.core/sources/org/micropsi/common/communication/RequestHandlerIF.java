/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/communication/RequestHandlerIF.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.communication;

public interface RequestHandlerIF {
	
	public ComChannelResponse handleRequest(ComChannelRequest request);
	
	public String getHandledRequest();

}
