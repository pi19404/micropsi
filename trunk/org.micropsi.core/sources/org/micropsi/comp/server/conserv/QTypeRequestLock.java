/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/server/conserv/QTypeRequestLock.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.server.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.server.ServerComponent;

public class QTypeRequestLock implements ConsoleQuestionTypeIF {

	private String QNAME = "requestlock";
	private ServerComponent server;

	/**
	 * Constructs the questiontype setting the corresponding server
	 * @param server The server object to interact with.
	 */
	public QTypeRequestLock(ServerComponent server) {
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
	public AnswerIF answerQuestion(
		AnswerFactoryIF factory,
		QuestionIF question,
		long step) {
		String lockComponent = question.retrieveParameter(0);
		if (lockComponent == null) {
			return factory.createAnswer(
				AnswerTypesIF.ANSWER_TYPE_ERROR,
				question,
				"Parameter missing. Which component should be locked?",
				step);
		} else {
			boolean alreadyLocked = false;

			try {
				alreadyLocked = server.requestComponentLock(lockComponent);
			} catch (MicropsiException e) {
				return factory.createAnswer(
					AnswerTypesIF.ANSWER_TYPE_ERROR,
					question,
					server.getExproc().handleException(e),
					step);
			}

			if (!alreadyLocked)
				return factory.createAnswer(
					AnswerTypesIF.ANSWER_TYPE_OK,
					question,
					null,
					step);
			else
				return factory.createAnswer(
					AnswerTypesIF.ANSWER_TYPE_ERROR,
					question,
					"Already locked",
					step);
		}
	}

}
