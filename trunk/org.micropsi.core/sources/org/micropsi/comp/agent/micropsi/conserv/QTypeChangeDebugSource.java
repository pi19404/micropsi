package org.micropsi.comp.agent.micropsi.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.agent.micropsi.DebugDataSource;

/**
 * 
 * 
 * 
 */
public class QTypeChangeDebugSource implements ConsoleQuestionTypeIF {

	private static final String QNAME = "changedebugsource";
	
	private DebugDataSource ds;

	public QTypeChangeDebugSource(DebugDataSource data) {
		ds = data;
	}

	public String getQuestionName() {
		return QNAME;
	}

	/**
	 * @see org.micropsi.common.consoleservice.ConsoleQuestionTypeIF#answerQuestion(org.micropsi.common.consoleservice.AnswerFactoryIF, org.micropsi.common.consoleservice.QuestionIF, long)
	 */
	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {
		
		try {
			double nv = Double.parseDouble(question.retrieveParameter(0));
			ds.setStrength(nv);
		} catch (Exception e) {
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,question,e.getMessage(),step);
		}
		
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_OK,question,null,step);
	}

}
