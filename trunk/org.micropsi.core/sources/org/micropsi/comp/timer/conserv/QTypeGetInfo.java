/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/timer/conserv/QTypeGetInfo.java,v 1.4 2006/01/18 02:32:53 vuine Exp $
 */
package org.micropsi.comp.timer.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.comp.timer.TimerComponent;

public class QTypeGetInfo implements ConsoleQuestionTypeIF {

	private static final String QNAME = "getinfo";
	private TimerComponent timer;
	
	public QTypeGetInfo(TimerComponent timer) {
		this.timer = timer;
	}

	public String getQuestionName() {
		return QNAME;
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {
		MTreeNode toReturn = new MTreeNode("infostring",timer.getComponentID(),null);
		toReturn.addChild("simstep",timer.getSimstep());
		toReturn.addChild("steplength",timer.getSteplength());
		toReturn.addChild("starttime",timer.getStarttime().toString());
		MTreeNode work = toReturn.addChild("syncmode",timer.isSyncmode());
		if(timer.isSyncmode()) {
			for(int i=0;i<timer.getNonReadyComponents().size();i++)
				work.addChild("waitingfor",timer.getNonReadyComponents().get(i));
		}
		toReturn.addChild("timingproblems",timer.getTimingProblems());
		toReturn.addChild("averagerealstep",timer.getAverageRealStepDuration());
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,question,toReturn,step);		
	}

}
