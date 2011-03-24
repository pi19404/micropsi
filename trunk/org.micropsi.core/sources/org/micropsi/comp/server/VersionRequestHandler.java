/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/server/VersionRequestHandler.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.server;

import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.communication.ComChannelResponse;
import org.micropsi.common.communication.RequestHandlerIF;
import org.micropsi.comp.messages.MVersion;

public class VersionRequestHandler implements RequestHandlerIF {

	public static final String HANDLEREQ = "version";

	private ComChannelResponse resp = new ComChannelResponse(HANDLEREQ,ComChannelResponse.RESPONSE_OK);

	public VersionRequestHandler() {
		resp.setResponseData(new MVersion());
	}

	public ComChannelResponse handleRequest(ComChannelRequest request) {
		return resp;
	}

	public String getHandledRequest() {
		return HANDLEREQ;
	}

}
