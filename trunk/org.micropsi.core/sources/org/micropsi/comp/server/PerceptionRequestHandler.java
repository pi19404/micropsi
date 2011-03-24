/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/server/PerceptionRequestHandler.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.server;

import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.communication.ComChannelResponse;
import org.micropsi.common.communication.RequestHandlerIF;
import org.micropsi.comp.messages.MPerceptionResp;
import org.micropsi.comp.messages.MPerceptionReq;

public class PerceptionRequestHandler implements RequestHandlerIF {

	public static final String HANDLEREQ = "perception";
	
	private ServerComponent server;
	
	public PerceptionRequestHandler(ServerComponent server) {
		this.server = server;
	}
	
	public ComChannelResponse handleRequest(ComChannelRequest request) {
		server.touchServer(request.getSender());
		 
		MPerceptionReq perceptreq = new MPerceptionReq(request.getSender()); 
		MPerceptionResp percepts = server.retrievePerceptionFromWorld(perceptreq);		
		
		ComChannelResponse resp = new ComChannelResponse(HANDLEREQ,ComChannelResponse.RESPONSE_OK);
		resp.setResponseData(percepts);
		
		return resp;

	}

	public String getHandledRequest() {
		return HANDLEREQ;
	}

}
