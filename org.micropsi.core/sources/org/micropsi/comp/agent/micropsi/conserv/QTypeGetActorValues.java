/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/conserv/QTypeGetActorValues.java,v 1.1 2005/10/14 23:04:22 vuine Exp $
 */
package org.micropsi.comp.agent.micropsi.conserv;

import java.util.Iterator;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.comp.messages.MTreeNode;

public class QTypeGetActorValues implements ConsoleQuestionTypeIF {

	private static final String QNAME = "getactorvalues";
	private MicroPsiAgent agent;

	public QTypeGetActorValues(MicroPsiAgent agent) {
		this.agent = agent;
	}

	public String getQuestionName() {
		return QNAME;
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {		
		MTreeNode result = new MTreeNode();
		result.setName("actorValues");
		Iterator<String> keys = agent.getActorValueCache().getKeys();
		while(keys.hasNext()) {
			String key = keys.next();
			result.addChild(key,agent.getActorValueCache().queryValue(key));
		}
		
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,question,result,step);
	}

}
