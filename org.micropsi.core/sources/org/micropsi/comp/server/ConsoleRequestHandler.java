/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/server/ConsoleRequestHandler.java,v 1.5 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.server;

import java.util.List;

import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.communication.ComChannelResponse;
import org.micropsi.common.communication.RequestHandlerIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.comp.messages.MAnswer;
import org.micropsi.comp.messages.MConsoleReq;
import org.micropsi.comp.messages.MConsoleResp;

public class ConsoleRequestHandler implements RequestHandlerIF {

	public static final String HANDLEREQ = "console";
	private ServerComponent server;
	
	public ConsoleRequestHandler(ServerComponent server) {
		this.server = server;
	}
	
	public ComChannelResponse handleRequest(ComChannelRequest request) {
		
		
		MConsoleReq consolereq = (MConsoleReq)request.getRequestData();
		MConsoleResp consoleresp = new MConsoleResp();
		
		if(consolereq.getRequestType() == MConsoleReq.CONSOLEREQ_FIRSTTIME) {
			String newName = server.announceConsole(request.getSender());
			consoleresp.setControltext(newName);
			server.touchServer(newName);
		} else if(consolereq.getRequestType() == MConsoleReq.CONSOLEREQ_LASTTIME) {
			server.touchServer(request.getSender());
			server.removeConsole(request.getSender());
		} else {
			server.touchServer(request.getSender());
		}
		
		consoleresp.setTime(server.getSimStep());

		List<AnswerIF> answers = server.routeCSData(consolereq.getQuestions(),request.getSender());
		consolereq.clearLists();
		for(int i=0;i<answers.size();i++) consoleresp.addAnswer((MAnswer)answers.get(i));
				
		return new ComChannelResponse(	HANDLEREQ,
											ComChannelResponse.RESPONSE_OK,
											consoleresp);
	}

	public String getHandledRequest() {
		return HANDLEREQ;
	}

}
