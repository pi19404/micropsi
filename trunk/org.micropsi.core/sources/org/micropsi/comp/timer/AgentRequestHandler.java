/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/timer/AgentRequestHandler.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.timer;

import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.communication.ComChannelResponse;
import org.micropsi.common.communication.RequestHandlerIF;

public class AgentRequestHandler implements RequestHandlerIF {

	public static final String HANDLEREQ = "agent";
	
	private TimerComponent timer;
	
	public AgentRequestHandler(TimerComponent timer) {
		this.timer = timer;
	}
	
	public ComChannelResponse handleRequest(ComChannelRequest request) {
		ComChannelResponse resp = new ComChannelResponse(
			request.getRequestName(),
			ComChannelResponse.RESPONSE_OK,
			null
		);

		if(request.getRequestName().equals("tellready")) {
			timer.receiveSyncReady(request.getSender());
		} else if(request.getRequestName().equals("register")) {
			if(!timer.registerSynchronizedComponent(request.getSender()))
				resp.setResponseType(ComChannelResponse.RESPONSE_ERROR);
		} else if(request.getRequestName().equals("unregister")) {
			timer.unregisterSynchronizedComponent(request.getSender());
		}
		return resp;
	}

	public String getHandledRequest() {
		return HANDLEREQ;
	}

}
