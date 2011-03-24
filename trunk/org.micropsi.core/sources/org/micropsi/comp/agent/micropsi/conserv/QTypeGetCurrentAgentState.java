package org.micropsi.comp.agent.micropsi.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;

/**
 * 
 * 
 * 
 */
public class QTypeGetCurrentAgentState implements ConsoleQuestionTypeIF {

	private static final String QNAME = "getcurrentstate";
	private MicroPsiAgent agent; 
	
	public QTypeGetCurrentAgentState(MicroPsiAgent agent) {
		this.agent = agent;
	}

	public String getQuestionName() {
		return QNAME;
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {
		String response = agent.getCurrentAgentState();
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_STRING,question,response,step);
	}

}
