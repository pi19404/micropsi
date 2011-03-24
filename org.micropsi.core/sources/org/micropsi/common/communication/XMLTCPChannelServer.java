/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/communication/XMLTCPChannelServer.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.micropsi.common.exception.MicropsiException;

public class XMLTCPChannelServer extends AbstractChannelServer {
    
	public XMLTCPChannelServer(String name) {
		super(name);
	}

	public ComChannelResponse processRequest(ComChannelRequest request) throws MicropsiException {
		return getHandler(request.getRequestName()).handleRequest(request);
	}
	
	public void processRequest(InputStream inp, OutputStream outp, AbstractXMLObjectCodec codec) throws IOException,MicropsiException {
		ComChannelRequest req = codec.receiveRequest(inp);
		ComChannelResponse resp = getHandler(req.getRequestName()).handleRequest(req);
		codec.writeResponse(resp,outp);
	}

}
