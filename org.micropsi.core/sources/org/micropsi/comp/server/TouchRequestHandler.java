/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/server/TouchRequestHandler.java,v 1.4 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.server;

import java.util.List;

import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.communication.ComChannelResponse;
import org.micropsi.common.communication.RequestHandlerIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.comp.messages.MAnswer;
import org.micropsi.comp.messages.MConfirmation;
import org.micropsi.comp.messages.MTouch;

public class TouchRequestHandler implements RequestHandlerIF {

	public static final String HANDLEREQ = "touch";
	
	private ServerComponent server;
	
	public TouchRequestHandler(ServerComponent server) {
		this.server = server;
	}

	public ComChannelResponse handleRequest(ComChannelRequest request) {
		server.touchServer(request.getSender());

		MTouch touch = (MTouch)request.getRequestData();

		ComChannelResponse resp = new ComChannelResponse(HANDLEREQ,ComChannelResponse.RESPONSE_OK);
		MConfirmation confirm = new MConfirmation();
		confirm.setTime(server.getSimStep());
			
		List<AnswerIF> answers = server.routeCSData(touch.getQuestions(),request.getSender());
		for(int i=0;i<answers.size();i++) confirm.addAnswer((MAnswer)answers.get(i));		
		resp.setResponseData(confirm);
				
		return resp;
	}

	public String getHandledRequest() {
		return HANDLEREQ;
	}

}
