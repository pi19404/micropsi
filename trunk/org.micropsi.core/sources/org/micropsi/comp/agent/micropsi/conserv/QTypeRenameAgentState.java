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
public class QTypeRenameAgentState implements ConsoleQuestionTypeIF {

	private static final String QNAME = "renamestate";
	private AgentStateRepository states;
	
	public QTypeRenameAgentState(AgentStateRepository states) {
		this.states = states;
	}

	public String getQuestionName() {
		return QNAME;
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {
		states.renameAgentState(
			question.retrieveParameter(0),
			question.retrieveParameter(1)
		);
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_OK,question,null,step);
	}

}
