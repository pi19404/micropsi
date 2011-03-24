package org.micropsi.comp.agent.micropsi.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.agent.micropsi.urges.BodySimulator;
import org.micropsi.comp.messages.MTreeNode;

/**
 * 
 * 
 * 
 */
public class QTypeGetBodyData implements ConsoleQuestionTypeIF {

	private static final String QNAME = "getbodydata";
	private BodySimulator body;

	public QTypeGetBodyData(BodySimulator simulator) {
		this.body = simulator;
	}

	public String getQuestionName() {
		return QNAME;
	}

	/**
	 * @see org.micropsi.common.consoleservice.ConsoleQuestionTypeIF#answerQuestion(org.micropsi.common.consoleservice.AnswerFactoryIF, org.micropsi.common.consoleservice.QuestionIF, long)
	 */
	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {
		MTreeNode toReturn = new MTreeNode("bodydata","",null);
		
		MTreeNode foodnode = new MTreeNode("food","",toReturn);
		new MTreeNode("is",Double.toString(body.getFoodLevel()),foodnode);
		new MTreeNode("shouldbe",Double.toString(100),foodnode);
		new MTreeNode("urge",Double.toString(body.getFoodUrge()),foodnode);
		new MTreeNode("decay",Double.toString(BodySimulator.FOOD_DECAY),foodnode);

		MTreeNode waternode = new MTreeNode("water","",toReturn);
		new MTreeNode("is",Double.toString(body.getWaterLevel()),waternode);
		new MTreeNode("shouldbe",Double.toString(100),waternode);
		new MTreeNode("urge",Double.toString(body.getWaterUrge()),waternode);
		new MTreeNode("decay",Double.toString(BodySimulator.WATER_DECAY),waternode);
		
		MTreeNode integritynode = new MTreeNode("integrity","",toReturn);
		new MTreeNode("is",Double.toString(body.getIntegrityLevel()),integritynode);
		new MTreeNode("shouldbe",Double.toString(100),integritynode);
		new MTreeNode("urge",Double.toString(body.getIntegrityUrge()),integritynode);
		new MTreeNode("decay","0.0",integritynode);

		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,question,toReturn,step);
	}

}
