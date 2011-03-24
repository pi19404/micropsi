package org.micropsi.comp.robot.khepera8.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.robot.khepera8.KheperaActionExecutor;

public class QTypeExternalPositioning implements ConsoleQuestionTypeIF {

	private static final String QNAME = "externalpositioning";
	
	private KheperaActionExecutor actionExecutor;
	
	public QTypeExternalPositioning(KheperaActionExecutor executor) {
		this.actionExecutor = executor;
	}
	
	public String getQuestionName() {
		return QNAME;
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		boolean enabled = "true".equalsIgnoreCase(question.getParameters()[0]);
		actionExecutor.setEnabledExternalPositioning(enabled);
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_OK,question,null,step);
	}

}
