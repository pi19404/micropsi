/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/timer/conserv/QTypeSwitchSync.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.timer.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.timer.TimerComponent;

public class QTypeSwitchSync implements ConsoleQuestionTypeIF {

	private TimerComponent timer; 
	
	public QTypeSwitchSync(TimerComponent timer) {
		this.timer = timer;
	}
	
	public String getQuestionName() {
		return "switchsync"; 
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question,long step) {
		timer.switchSyncmode();		
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_OK,question,null,step);
	}

}
