package org.micropsi.comp.agent.micropsi.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.comp.messages.MTreeNode;

/**
 * 
 * 
 * 
 */
public class QTypeGetCycleLength implements ConsoleQuestionTypeIF {

	private static final String QNAME = "getcyclelength";
	private MicroPsiAgent agent;

	public QTypeGetCycleLength(MicroPsiAgent agent) {
		this.agent = agent;
	}

	public String getQuestionName() {
		return QNAME;
	}

	/**
	 * @see org.micropsi.common.consoleservice.ConsoleQuestionTypeIF#answerQuestion(org.micropsi.common.consoleservice.AnswerFactoryIF, org.micropsi.common.consoleservice.QuestionIF, long)
	 */
	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {
		MTreeNode node = new MTreeNode("length",Integer.toString(agent.getCycleDelay()),null);
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_OK,question,node,step);				
	}

}
