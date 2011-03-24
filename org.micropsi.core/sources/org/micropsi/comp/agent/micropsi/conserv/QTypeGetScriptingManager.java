package org.micropsi.comp.agent.micropsi.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.agent.micropsi.scripting.AgentScriptingManager;

/**
 * 
 * 
 * 
 */
public class QTypeGetScriptingManager implements ConsoleQuestionTypeIF {

	private static final String QNAME = "getscriptingmanager";
	private AgentScriptingManager scriptingManager;

	public QTypeGetScriptingManager(AgentScriptingManager scriptingManager) {
		this.scriptingManager = scriptingManager;
	}

	public String getQuestionName() {
		return QNAME;
	}

	/**
	 * @see org.micropsi.common.consoleservice.ConsoleQuestionTypeIF#answerQuestion(org.micropsi.common.consoleservice.AnswerFactoryIF, org.micropsi.common.consoleservice.QuestionIF, long)
	 */
	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,question,scriptingManager,step);
	}

}
