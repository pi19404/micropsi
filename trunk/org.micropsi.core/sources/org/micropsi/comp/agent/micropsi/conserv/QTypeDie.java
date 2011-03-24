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
public class QTypeDie implements ConsoleQuestionTypeIF {

	private static final String QNAME = "die";
	private MicroPsiAgent agent;

	public QTypeDie(MicroPsiAgent agent) {
		this.agent = agent;
	}


	public String getQuestionName() {
		return QNAME;
	}

	/**
	 * @see org.micropsi.common.consoleservice.ConsoleQuestionTypeIF#answerQuestion(org.micropsi.common.consoleservice.AnswerFactoryIF, org.micropsi.common.consoleservice.QuestionIF, long)
	 */
	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {
		agent.die("Question 'die' was received. Sender: "+question.getOrigin()+" Step: "+step);
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_OK,question,null,step);
	}

}
