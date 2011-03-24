/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/conserv/QTypeGetInnerStates.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
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
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.nodenet.InnerStateInspectorIF;
import org.micropsi.nodenet.LocalNetFacade;

public class QTypeGetInnerStates implements ConsoleQuestionTypeIF {

	private static final String QNAME = "getinnerstates";
	private MicroPsiAgent agent;
	private ExceptionProcessor exproc;

	public QTypeGetInnerStates(MicroPsiAgent agent, ExceptionProcessor exproc) {
		this.agent = agent;
		this.exproc = exproc;
	}

	public String getQuestionName() {
		return QNAME;
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {		
		
		String moduleID = question.retrieveParameter(0);
		MTreeNode toReturn = new MTreeNode("innerstates",moduleID,null);
		try {	
			InnerStateInspectorIF inspector = ((LocalNetFacade)agent.getNet()).getModuleInspector(moduleID);
			String[] innerstates = inspector.getInnerStates();
			for(int i=0;i<innerstates.length;i++)
				toReturn.addChild(innerstates[i], inspector.getInnerState(innerstates[i]));
					
		} catch (MicropsiException e) {
			String error = exproc.handleException(e);
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,question,error,step);
		}
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,question,toReturn,step);
	}

}
