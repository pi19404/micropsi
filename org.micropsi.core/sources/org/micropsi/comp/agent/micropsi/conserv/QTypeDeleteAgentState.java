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
public class QTypeDeleteAgentState implements ConsoleQuestionTypeIF {

	private static final String QNAME = "deletestate";
	private AgentStateRepository states;
	
	public QTypeDeleteAgentState(AgentStateRepository states) {
		this.states = states;
	}

	public String getQuestionName() {
		return QNAME;
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {
		states.deleteAgentState(question.retrieveParameter(0));
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_OK,question,null,step);
	}

}
