/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/server/conserv/QTypeGetEnvironment.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.server.conserv;

import java.util.Iterator;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.comp.server.ServerComponent;

public class QTypeGetEnvironment implements ConsoleQuestionTypeIF {

	private String QNAME = "getenvironment";
	private ServerComponent server;
	
	/**
	 * Constructs the questiontype setting the corresponding server
	 * @param server The server object to interact with
	 */
	public QTypeGetEnvironment(ServerComponent server) {
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
		MTreeNode toReturn = new MTreeNode("environment",Long.toString(step),null);
		toReturn.addChild("cmp",server.getComponentID());
		Iterator iter = server.getLivingComponents();
		while(iter.hasNext())
			toReturn.addChild("cmp",(String)iter.next());
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,question,toReturn,step);		
	}

}
