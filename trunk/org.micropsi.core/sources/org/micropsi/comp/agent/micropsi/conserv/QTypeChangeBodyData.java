package org.micropsi.comp.agent.micropsi.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.agent.micropsi.urges.BodySimulator;

/**
 * 
 * 
 * 
 */
public class QTypeChangeBodyData implements ConsoleQuestionTypeIF {

	private static final String QNAME = "changebodydata";
	private BodySimulator body;

	public QTypeChangeBodyData(BodySimulator body) {
		this.body = body;
	}


	public String getQuestionName() {
		return QNAME;
	}

	/**
	 * @see org.micropsi.common.consoleservice.ConsoleQuestionTypeIF#answerQuestion(org.micropsi.common.consoleservice.AnswerFactoryIF, org.micropsi.common.consoleservice.QuestionIF, long)
	 */
	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {
		
		String parm = question.retrieveParameter(0);
		double amount = Double.parseDouble(question.retrieveParameter(1)); 
		
		if(parm.equals("food")) {
			body.eat(amount);
		} else if(parm.equals("water")) {
			body.drink(amount);
		} else if(parm.equals("integrity")) {
			body.damage(-amount);
		} else
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,question,"No such body parameter: "+parm,step);
		
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_OK,question,null,step);
	}

}
