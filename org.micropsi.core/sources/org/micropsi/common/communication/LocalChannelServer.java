/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/communication/LocalChannelServer.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.communication;

import org.micropsi.common.exception.MicropsiException;

public class LocalChannelServer extends AbstractChannelServer {

	public LocalChannelServer(String servername) {
		super(servername);
	}
	  
	public ComChannelResponse processRequest(ComChannelRequest request) throws MicropsiException {
		return getHandler(request.getRequestName()).handleRequest(request);
	}
	

}
