/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/server/AgentRequestHandler.java,v 1.5 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.server;

import java.util.List;

import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.communication.ComChannelResponse;
import org.micropsi.common.communication.RequestHandlerIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.messages.MAction;
import org.micropsi.comp.messages.MAgentReq;
import org.micropsi.comp.messages.MAgentResp;
import org.micropsi.comp.messages.MAnswer;
import org.micropsi.comp.messages.MQuestion;

public class AgentRequestHandler implements RequestHandlerIF {

	public static final String HANDLEREQ = "agent";
	
	private ServerComponent server;
	
	public AgentRequestHandler(ServerComponent server) {
		this.server = server;
	}
	
	public ComChannelResponse handleRequest(ComChannelRequest request) {
		server.touchServer(request.getSender());
		
		MAgentReq areq = (MAgentReq)request.getRequestData();
		MAgentResp aresp = new MAgentResp();
		
		switch(areq.getRequestType()) {
			case MAgentReq.AGENTREQ_REGISTER:
				String newName = server.registerAgent(areq.getAgentID(),areq.getAgentType());
				aresp.setResponseType(MAgentResp.AGENTRESP_REGISTRATION);
				aresp.setControltext(newName);
				aresp.setTime(server.getSimStep());
				break;
			case MAgentReq.AGENTREQ_NORMALOP:
				if(server.agentManager.isAgentKnown(areq.getAgentID())) {
					aresp.setResponseType(MAgentResp.AGENTRESP_NORMALOP);
					aresp.setTime(server.getSimStep());
					
					try {
						aresp.setPreviousActionResponse(server.retrieveActionResponse(request.getSender()));
					} catch (MicropsiException e) {
						aresp.setResponseType(MAgentResp.AGENTRESP_ERROR);
						aresp.setControltext("PREV_ACTION FAILURE: "+server.getExproc().handleException(e));						
					}
					
					if(	areq.getAction() != null &&
						!areq.getAction().getActionType().equals(MAction.NOOP)) {
						try {
							// add action!
							if(!server.addAgentAction(areq.getAgentID(),areq.getAction())) {
								aresp.setResponseType(MAgentResp.AGENTRESP_ERROR);
								aresp.setControltext("ALREADYSENT");
							}
						} catch (MicropsiException e) {
							aresp.setResponseType(MAgentResp.AGENTRESP_ERROR);
							aresp.setControltext(server.getExproc().handleException(e));
						}
					}
				} else {
					aresp.setResponseType(MAgentResp.AGENTRESP_ERROR);
					aresp.setControltext("Agent ID unknown");
				}
				break;
			case MAgentReq.AGENTREQ_UNREGISTER:
				server.removeAgent(areq.getAgentID());
				aresp.setResponseType(MAgentResp.AGENTRESP_KICK);
				aresp.setTime(server.getSimStep());
				aresp.setControltext("bye");
		}
		
		ComChannelResponse resp = new ComChannelResponse(HANDLEREQ,ComChannelResponse.RESPONSE_OK);
		resp.setResponseData(aresp);
		
		// agents cannot ask questions, but perhaps there are answers?
		List<AnswerIF> answers = areq.getAnswers();
		for(int i=0;i<answers.size();i++) server.addConsoleAnswer((MAnswer)answers.get(i));

		// send the questions to the timer
		List<MQuestion> questions = server.getConsoleQuestions(request.getSender());
		for(int i=0;i<questions.size();i++) aresp.addQuestion(questions.get(i));

		return resp;

	}

	public String getHandledRequest() {
		return HANDLEREQ;
	}

}
