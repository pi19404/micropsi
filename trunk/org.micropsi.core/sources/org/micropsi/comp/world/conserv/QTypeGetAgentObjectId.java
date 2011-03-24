/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeGetAgentObjectId.java,v 1.3 2005/01/31 18:12:02 fuessel Exp $
 */
package org.micropsi.comp.world.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.world.WorldComponent;
import org.micropsi.comp.world.objects.AbstractObject;

/**
 *  $Header $
 *  @author matthias <matthias.fuessel@gmx.net>
 *
 * Handles questions with name 'getagentobjectid'. Returns the id (long) of the agent object with
 * the specified name.
 */
public class QTypeGetAgentObjectId implements ConsoleQuestionTypeIF {
	
	WorldComponent world;
	
	public QTypeGetAgentObjectId(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "getagentobjectid";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		String[] parameters = question.getParameters();
		String toReturn;
		boolean resultOk = true;
		if (parameters.length < 1) {
			toReturn = "getagentobjectid: \n";
			toReturn += "required Paramters: \n";
			toReturn += "<agent name>";
			resultOk = false;
		} else {
			AbstractObject agentObject = world.getAgent(parameters[0]);
			if (agentObject == null) {
				toReturn = "there is no agent object with name '" + parameters[0] + "'.";
				resultOk = false;
			} else {
				toReturn = Long.toString(agentObject.getId());
			}
		}
		
		return factory.createAnswer(resultOk == true? AnswerTypesIF.ANSWER_TYPE_STRING : AnswerTypesIF.ANSWER_TYPE_ERROR,question, toReturn ,step);
	}

}
