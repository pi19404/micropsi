package org.micropsi.comp.robot.khepera8.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.robot.khepera8.KheperaActionExecutor;
import org.micropsi.comp.robot.khepera8.khepera.Khepera;


public class QTypeReset implements ConsoleQuestionTypeIF {

	private static final String QNAME = "reset";
	
	private Khepera khepera;
	
	public QTypeReset() {
	}

	public void setKhepera(Khepera k) {
		this.khepera = k;
	}
	
	public String getQuestionName() {
		return QNAME;
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		khepera.restart();
//		khepera.shutdown();
//		KheperaActionExecutor.this.khepera = null;
//		KheperaActionExecutor.this.giveKhepera();
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_OK,question,null,step);
	}

}
