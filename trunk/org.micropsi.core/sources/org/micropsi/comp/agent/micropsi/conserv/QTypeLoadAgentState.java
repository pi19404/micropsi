package org.micropsi.comp.agent.micropsi.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.exception.ExceptionProcessor;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;

/**
 * 
 * 
 * 
 */
public class QTypeLoadAgentState implements ConsoleQuestionTypeIF {

	private static final String QNAME = "loadstate";
	private MicroPsiAgent agent; 
	private ExceptionProcessor exproc;
	
	public QTypeLoadAgentState(MicroPsiAgent agent, ExceptionProcessor exproc) {
		this.agent = agent;
		this.exproc = exproc;
	}

	public String getQuestionName() {
		return QNAME;
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {
		
		try {
			String newstate = question.retrieveParameter(0);
			
			if(newstate != null) {
				if(newstate.equals("null")) newstate = null;
			}
			
			agent.setCurrentAgentState(newstate);
		} catch (MicropsiException e) {
			String toReturn = exproc.handleException(e);
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,question,toReturn,step);
		}
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_OK,question,null,step);
	}

}
