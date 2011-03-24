/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeGetAgentList.java,v 1.1 2005/01/30 21:36:21 fuessel Exp $
 */
package org.micropsi.comp.world.conserv;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.comp.world.WorldComponent;
import org.micropsi.comp.world.objects.AbstractAgentObject;

/**
 *  $Header $
 *  @author matthias <matthias.fuessel@gmx.net>
 *
 * Handles questions with name 'getagentlist'. Returns a list of agent object names with their ids
 */
public class QTypeGetAgentList implements ConsoleQuestionTypeIF {
	
	WorldComponent world;
	
	public QTypeGetAgentList(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "getagentlist";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		MTreeNode toReturn = new MTreeNode("agents", "", null);
		for (Iterator it = world.getAgents().entrySet().iterator(); it.hasNext(); ) {
			Map.Entry agentEntry = (Entry) it.next();
			toReturn.addChild(new MTreeNode((String) agentEntry.getKey(), Long.toString(((AbstractAgentObject) agentEntry.getValue()).getId()), null));
		}
		
		
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE, question, toReturn, step);
	}

}
