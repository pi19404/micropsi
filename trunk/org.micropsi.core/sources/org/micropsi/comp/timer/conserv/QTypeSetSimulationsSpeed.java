/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/timer/conserv/QTypeSetSimulationsSpeed.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.timer.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.timer.TimerComponent;

public class QTypeSetSimulationsSpeed implements ConsoleQuestionTypeIF {

	TimerComponent timer;
	
	public QTypeSetSimulationsSpeed(TimerComponent timer) {
		this.timer = timer;
	}

	public String getQuestionName() {
		return "setsimulationspeed";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		String toReturn = "Cycle length changed from "+timer.getSteplength()+" to ";
		
		
		try {
			int length = Integer.parseInt(question.retrieveParameter(0));
			toReturn += length;
			timer.setStepLength(length);
		} catch (RuntimeException e) {
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,question,"Could not change - not a number?",step);
		}

		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_STRING,question,toReturn,step);		
	}

}
