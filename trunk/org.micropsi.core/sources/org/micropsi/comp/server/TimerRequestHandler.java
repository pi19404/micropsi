/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/server/TimerRequestHandler.java,v 1.4 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.server;

import java.util.List;

import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.communication.ComChannelResponse;
import org.micropsi.common.communication.RequestHandlerIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.comp.messages.MAnswer;
import org.micropsi.comp.messages.MConfirmation;
import org.micropsi.comp.messages.MQuestion;
import org.micropsi.comp.messages.MTick;

public class TimerRequestHandler implements RequestHandlerIF {

	public static final String HANDLEREQ = "tick";
	private ServerComponent server;
	
	public TimerRequestHandler(ServerComponent server) {
		this.server = server;
	}

	public ComChannelResponse handleRequest(ComChannelRequest request) {
		server.touchServer(request.getSender());		
		
		MTick tick = (MTick)request.getRequestData();
		server.tick(tick.getTime());

		ComChannelResponse resp = new ComChannelResponse(HANDLEREQ,ComChannelResponse.RESPONSE_OK);
		MConfirmation confirm = new MConfirmation();
		resp.setResponseData(confirm);
		
		// timers cannot ask questions, but perhaps there are answers?
		List<AnswerIF> answers = tick.getAnswers();
		for(int i=0;i<answers.size();i++) server.addConsoleAnswer((MAnswer)answers.get(i));

		// send the questions to the timer
		List<MQuestion> questions = server.getConsoleQuestions(request.getSender());
		for(int i=0;i<questions.size();i++) confirm.addQuestion(questions.get(i));

		return resp;
	}

	public String getHandledRequest() {
		return HANDLEREQ;
	}

}
