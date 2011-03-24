package org.micropsi.comp.agent.micropsi.conserv;

import java.util.Iterator;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.nodenet.agent.Situation;
import org.micropsi.nodenet.agent.SituationElement;

/**
 * 
 * 
 * 
 */
public class QTypeGetSituation implements ConsoleQuestionTypeIF {

	private static final String QNAME = "getsituation";
	private MicroPsiAgent agent;

	public QTypeGetSituation(MicroPsiAgent agent) {
		this.agent = agent;
	}

	public String getQuestionName() {
		return QNAME;
	}

	/**
	 * @see org.micropsi.common.consoleservice.ConsoleQuestionTypeIF#answerQuestion(org.micropsi.common.consoleservice.AnswerFactoryIF, org.micropsi.common.consoleservice.QuestionIF, long)
	 */
	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {		
		Situation situation = agent.getSituation();
		MTreeNode toReturn = new MTreeNode("situation",Long.toString(step),null);
		
		MTreeNode elementsNode = new MTreeNode("elements","",toReturn);
		Iterator elements = situation.getWholeSituation();
		while(elements.hasNext()) {
			SituationElement e = (SituationElement)elements.next();
			MTreeNode elementNode = new MTreeNode(e.getType(),"",elementsNode);
			elementNode.addChild("x",e.getX());
			elementNode.addChild("y",e.getY());
		}
		
		MTreeNode fovea = new MTreeNode("fovea","",toReturn);
		fovea.addChild("x", situation.getFoveaX());
		fovea.addChild("y", situation.getFoveaY());

		MTreeNode attention = new MTreeNode("attention","",toReturn);
		attention.addChild("x", situation.getFoveaX());
		attention.addChild("y", situation.getFoveaY());
		
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,question,toReturn,step);
	}

}
