/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/ServerRequestPerceptionHandler.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.world;

import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.communication.ComChannelResponse;
import org.micropsi.common.communication.RequestHandlerIF;
import org.micropsi.comp.messages.MPerceptionReq;
import org.micropsi.comp.world.objects.AgentObjectIF;

public class ServerRequestPerceptionHandler implements RequestHandlerIF {

	public static final String HANDLEREQ = "getperception";
	private WorldComponent world;

	public ServerRequestPerceptionHandler(WorldComponent world) {
		this.world = world;
	}

	public ComChannelResponse handleRequest(ComChannelRequest request) {
		MPerceptionReq pr = (MPerceptionReq) request.getRequestData();
		String agentID = pr.getAgentID();
		AgentObjectIF agent = world.getAgent(agentID);

		ComChannelResponse resp = new ComChannelResponse(HANDLEREQ, ComChannelResponse.RESPONSE_OK);

		if (agent != null) {
			resp.setResponseData(agent.getPerception());
		} else {
			world.getLogger().error(
				"agent "
					+ agentID
					+ " has requested perception but has not registered with the world");
		}

		return resp;
	}

	public String getHandledRequest() {
		return HANDLEREQ;
	}

}
