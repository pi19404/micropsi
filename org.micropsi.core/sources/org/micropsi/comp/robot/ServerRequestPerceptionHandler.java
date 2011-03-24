/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/robot/ServerRequestPerceptionHandler.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.robot;

import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.communication.ComChannelResponse;
import org.micropsi.common.communication.RequestHandlerIF;
import org.micropsi.common.exception.MicropsiException;

public class ServerRequestPerceptionHandler implements RequestHandlerIF {

	public static final String HANDLEREQ = "getperception";
	private RobotWorldComponent world;

	public ServerRequestPerceptionHandler(RobotWorldComponent world) {
		this.world = world;
	}

	public ComChannelResponse handleRequest(ComChannelRequest request) {
		//MPerceptionReq pr = (MPerceptionReq) request.getRequestData();

		ComChannelResponse resp = new ComChannelResponse(HANDLEREQ, ComChannelResponse.RESPONSE_OK);

		try {
			resp.setResponseData(RobotPerceptionExtractor.getInstance().extractPerception());
		} catch (MicropsiException e) {
			world.getExproc().handleException(e);
			resp.setResponseType(ComChannelResponse.RESPONSE_ERROR);			
		}

		return resp;
	}

	public String getHandledRequest() {
		return HANDLEREQ;
	}

}
