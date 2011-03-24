package org.micropsi.comp.agent.micropsi.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.exception.ExceptionProcessor;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;

/**
 * 
 * 
 * 
 */
public class QTypeSetCycleLength implements ConsoleQuestionTypeIF {

	private static final String QNAME = "setcyclelength";
	private MicroPsiAgent agent;
	private ExceptionProcessor exproc;

	public QTypeSetCycleLength(MicroPsiAgent agent, ExceptionProcessor exproc) {
		this.agent = agent;
		this.exproc = exproc;
	}

	public String getQuestionName() {
		return QNAME;
	}

	/**
	 * @see org.micropsi.common.consoleservice.ConsoleQuestionTypeIF#answerQuestion(org.micropsi.common.consoleservice.AnswerFactoryIF, org.micropsi.common.consoleservice.QuestionIF, long)
	 */
	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {
		try {
			int newlength = Integer.parseInt(question.retrieveParameter(0));
			agent.setCycleDelay(newlength);
		} catch (Exception e) {
			String toReturn = exproc.handleException(e);
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,question,toReturn,step);
		}
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_OK,question,null,step);
	}

}
