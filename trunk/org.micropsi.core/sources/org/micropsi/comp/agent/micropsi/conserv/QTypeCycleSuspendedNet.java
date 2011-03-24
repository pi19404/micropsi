/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/conserv/QTypeCycleSuspendedNet.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.agent.micropsi.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.exception.ExceptionProcessor;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.nodenet.NetIntegrityException;

public class QTypeCycleSuspendedNet implements ConsoleQuestionTypeIF {

	private static final String QNAME = "cyclesuspendednet";
	private MicroPsiAgent agent;
	private ExceptionProcessor exproc;

	public QTypeCycleSuspendedNet(MicroPsiAgent agent, ExceptionProcessor exproc) {
		this.agent = agent;
		this.exproc = exproc;
	}

	public String getQuestionName() {
		return QNAME;
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {		
		if(!agent.getNet().getCycle().isSuspended()) 
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,question,null,step);
		
		int cycles = Integer.parseInt(question.retrieveParameter(0));
		try {
			agent.getNet().getCycle().continueNCycles(cycles,false);
		} catch (NetIntegrityException e) {
			String toReturn = exproc.handleException(e);
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,question,toReturn,step);
		}
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_OK,question,null,step);
	}

}
