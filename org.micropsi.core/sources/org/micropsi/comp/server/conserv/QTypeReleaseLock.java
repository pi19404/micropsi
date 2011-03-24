/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/server/conserv/QTypeReleaseLock.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.server.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.server.ServerComponent;

public class QTypeReleaseLock implements ConsoleQuestionTypeIF {

	private String QNAME = "releaselock";
	private ServerComponent server;

	/**
	 * Constructs the questiontype setting the corresponding server
	 * @param server The server object to interact with.
	 */
	public QTypeReleaseLock(ServerComponent server) {
		this.server = server;
	}

	/**
	 * @see org.micropsi.common.consoleservice.ConsoleQuestionTypeIF#getQuestionName()
	 */
	public String getQuestionName() {
		return QNAME;
	}

	/**
	 * @see org.micropsi.common.consoleservice.ConsoleQuestionTypeIF#answerQuestion(org.micropsi.common.consoleservice.AnswerFactoryIF, org.micropsi.common.consoleservice.QuestionIF, long)
	 */
	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {
		String lockComponent = question.retrieveParameter(0);
		server.releaseComponentLock(lockComponent);
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_OK,question,null,step);
	}

}
