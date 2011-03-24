package org.micropsi.comp.agent.micropsi.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.agent.micropsi.AgentStateRepository;

/**
 * 
 * 
 * 
 */
public class QTypeGetAgentStateMetadataPath implements ConsoleQuestionTypeIF {

	private static final String QNAME = "getstatemetadatapath";
	private AgentStateRepository states;
	
	public QTypeGetAgentStateMetadataPath(AgentStateRepository states) {
		this.states = states;
	}

	public String getQuestionName() {
		return QNAME;
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {

		String agentstate = question.retrieveParameter(0);
		String response = states.getAgentStateMetadataPath(agentstate);
		
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_STRING,question,response,step);
	}

}
