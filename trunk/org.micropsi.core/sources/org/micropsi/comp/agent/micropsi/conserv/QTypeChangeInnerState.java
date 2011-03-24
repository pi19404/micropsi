/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/conserv/QTypeChangeInnerState.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.agent.micropsi.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.exception.ExceptionProcessor;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.nodenet.LocalNetFacade;

public class QTypeChangeInnerState implements ConsoleQuestionTypeIF {

	private static final String QNAME = "changeinnerstate";
	private MicroPsiAgent agent;
	private ExceptionProcessor exproc;

	public QTypeChangeInnerState(MicroPsiAgent agent, ExceptionProcessor exproc) {
		this.agent = agent;
		this.exproc = exproc;
	}

	public String getQuestionName() {
		return QNAME;
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {		
		
		String moduleID = question.retrieveParameter(0);
		String key = question.retrieveParameter(1);
		String value = question.retrieveParameter(2);
		
		try {
			((LocalNetFacade)agent.getNet()).getModuleInspector(moduleID).changeInnerState(key, value);
		} catch (MicropsiException e) {
			String error = exproc.handleException(e);
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,question,error,step);	
		}

		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_OK,question,null,step);
	}

}
