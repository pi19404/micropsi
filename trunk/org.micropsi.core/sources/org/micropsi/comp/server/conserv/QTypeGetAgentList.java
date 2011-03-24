/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/server/conserv/QTypeGetAgentList.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.server.conserv;

import java.util.Iterator;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.comp.server.AgentManager;

public class QTypeGetAgentList implements ConsoleQuestionTypeIF {

	private String QNAME = "getagentlist";
	private AgentManager agentManager;
	
	/**
	 * Constructs the questiontype setting the corresponding server
	 * @param agentManager The agentManager object to interact with
	 */
	public QTypeGetAgentList(AgentManager agentManager) {
		this.agentManager = agentManager;
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
		MTreeNode toReturn = new MTreeNode("agents",Long.toString(step),null);
		
		Iterator iter = agentManager.getAllAgentIDs();
		while(iter.hasNext())
			toReturn.addChild("ag",(String)iter.next());
		
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,question,toReturn,step);		
	}

}
