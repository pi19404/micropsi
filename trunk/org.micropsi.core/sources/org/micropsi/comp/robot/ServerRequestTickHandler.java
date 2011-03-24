/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/robot/ServerRequestTickHandler.java,v 1.9 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.robot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.communication.ComChannelResponse;
import org.micropsi.common.communication.RequestHandlerIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.messages.MAction;
import org.micropsi.comp.messages.MActionResponse;
import org.micropsi.comp.messages.MAnswer;
import org.micropsi.comp.messages.MUpdateWorld;
import org.micropsi.comp.messages.MWorldResponse;

public class ServerRequestTickHandler implements RequestHandlerIF {

	public static final String HANDLEREQ = "updateworld";
	
	private RobotWorldComponent world;

	private ComChannelResponse resp = new ComChannelResponse(HANDLEREQ, ComChannelResponse.RESPONSE_OK);
	
	public ServerRequestTickHandler(RobotWorldComponent world) {
		this.world = world;
	}

	public ComChannelResponse handleRequest(ComChannelRequest request) {
		MUpdateWorld update = (MUpdateWorld) request.getRequestData();
		
		if(update.getNewAgents().size() > 0) {
			if(world.getConnectedAgent() == null) {
				
				String s = update.getNewAgents().get(0);
				int splitPos = s.lastIndexOf(',');

				String agentName = s.substring(0, splitPos);
				//String agentJavaClass = s.substring(splitPos + 1);

				world.setConnectedAgent(agentName);
				
				world.getLogger().info("Agent "+world.getConnectedAgent()+" gained control over robot "+world.getComponentID());
			} else {
				world.getLogger().warn("Another agent wanted to get the control over robot "+world.getComponentID()+", but there is already a ghost in the shell");
			}
				
			if(update.getNewAgents().size() > 1) {
				world.getLogger().warn("More than one agent wanted to get the control over robot "+world.getComponentID()+". All agents but "+world.getConnectedAgent()+" will be ignored");
			}
		}
		
		if(update.getDeletedAgents().size() > 0) {
			String deletedAgent = update.getDeletedAgents().get(0);
			if(deletedAgent.equals(world.getConnectedAgent())) {
				world.getLogger().info("Agent "+world.getConnectedAgent()+" lost control over robot "+world.getComponentID());
				world.setConnectedAgent(null);
			}
		}

		ArrayList<MActionResponse> responses = new ArrayList<MActionResponse>();
		
		if(world.getConnectedAgent() != null) {
			world.tick(update.getTime());

			//process actions
			Iterator it = update.getActions().iterator();
			while (it.hasNext()) {
				MAction action = (MAction) it.next();
				if(!action.getAgentName().equals(world.getConnectedAgent())) {
					world.getLogger().warn("Robot "+world.getComponentID()+" ignored action "+action.getActionType()+" for agent "+action.getAgentName()+" because it is controlled by "+world.getConnectedAgent());
				}
				try {
					MActionResponse actionResponse = 
						RobotActionExecutor.getInstance().executeAction(action);			
					responses.add(actionResponse);
				} catch (MicropsiException e) {
					world.getExproc().handleException(e);	
				}
			}
		}
		
		MWorldResponse worldResponse = new MWorldResponse(responses);
		resp.setResponseData(worldResponse);

		List<QuestionIF> questions = update.getQuestions();
		// process questions (answer them!)
		List<AnswerIF> answers = world.getConsoleService().answerStoredQuestions(null, world.getSimStep());
		answers.addAll(world.getConsoleService().answerQuestions(questions,world.getSimStep()));
		for (int i = 0; i < answers.size(); i++) {
			worldResponse.addAnswer((MAnswer) answers.get(i));
		}

		return resp;
	}

	public String getHandledRequest() {
		return HANDLEREQ;
	}

}
