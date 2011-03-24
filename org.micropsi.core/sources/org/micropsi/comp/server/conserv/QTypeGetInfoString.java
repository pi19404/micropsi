/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/server/conserv/QTypeGetInfoString.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.server.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.server.ServerComponent;

public class QTypeGetInfoString implements ConsoleQuestionTypeIF {
	
	ServerComponent server;

	/**
	 * Constructs the questiontype setting the corresponding server
	 * @param server The server object to interact with.
	 */	
	public QTypeGetInfoString(ServerComponent server) {
		this.server = server;
	}

	/**
	 * @see org.micropsi.common.consoleservice.ConsoleQuestionTypeIF#getQuestionName()
	 */
	public String getQuestionName() {
		return "getinfostring";
	}

	/**
	 * @see org.micropsi.common.consoleservice.ConsoleQuestionTypeIF#answerQuestion(org.micropsi.common.consoleservice.AnswerFactoryIF, org.micropsi.common.consoleservice.QuestionIF, long)
	 */
	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		String toReturn = "Server. \n";
		toReturn += "SimStep: "+server.getSimStep()+"\n";
		
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_STRING,question,toReturn,step);
	}

}
