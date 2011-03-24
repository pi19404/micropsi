package org.micropsi.comp.agent.kheperaTurtle5.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.agent.kheperaTurtle5.KheperaWorldAdapter;
import org.micropsi.comp.robot.khepera8.KheperaActionExecutor;

public class QTypeTalkative implements ConsoleQuestionTypeIF {

	private static final String QNAME = "talkative";
	
	private KheperaWorldAdapter wa;
	
	public QTypeTalkative(KheperaWorldAdapter wa) {
		this.wa = wa;
	}
	
	public String getQuestionName() {
		return QNAME;
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		boolean enabled = "true".equalsIgnoreCase(question.getParameters()[0]);
		wa.setTalkative(enabled);
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_OK,question,null,step);
	}

}
