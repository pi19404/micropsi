/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeGetAgentStatistics.java,v 1.2 2005/01/31 18:12:02 fuessel Exp $
 */
package org.micropsi.comp.world.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.comp.world.WorldComponent;
import org.micropsi.comp.world.objects.AbstractAgentObject;
import org.micropsi.comp.world.objects.AbstractObject;

/**
 *  $Header $
 *  @author matthias <matthias.fuessel@gmx.net>
 *
 * Handles questions with name 'getagentstatistics'. Returns a MTreeNode with some statistical data.
 */
public class QTypeGetAgentStatistics implements ConsoleQuestionTypeIF {
	
	WorldComponent world;
	
	public QTypeGetAgentStatistics(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "getagentstatistics";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		String[] parameters = question.getParameters();
		String toReturn = null;
		
		if (parameters.length < 1) {
			toReturn = "getagentstatistics: \n";
			toReturn += "required Paramters: \n";
			toReturn += "<agent object id>";
		} else {
			long id;
			try {
				id = Long.parseLong(parameters[0]);
			} catch (NumberFormatException e) {
				toReturn = "getagentstatistics: \n";
				toReturn += "required Paramters: \n";
				toReturn += "<agent object id>\n";
				toReturn += "'" + parameters[0] + "' is no object id";
				return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR, question, toReturn ,step);
			}
			AbstractObject worldObject = world.getWorld().getObject(id);
			if (worldObject == null) {
				toReturn = "there is no object with id '" + parameters[0] + "'.";
			} else if (!(worldObject instanceof AbstractAgentObject)) {
				toReturn = "object with id '" + parameters[0] + "' is no agent object.";
			} else {
				AbstractAgentObject agentObject = (AbstractAgentObject) worldObject;
				MTreeNode node = new MTreeNode(agentObject.getAgentName(), Long.toString(agentObject.getId()), null);
				agentObject.insertStatisticDataIn(node);
				return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE, question, node, step);
			}
		}
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR, question, toReturn, step);
	}

}
