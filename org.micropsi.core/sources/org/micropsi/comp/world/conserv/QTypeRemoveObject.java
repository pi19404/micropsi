/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeRemoveObject.java,v 1.6 2004/11/27 14:03:39 fuessel Exp $
 */
package org.micropsi.comp.world.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.world.WorldComponent;
import org.micropsi.comp.world.objects.AbstractObjectPart;
import org.micropsi.comp.world.objects.AgentObjectIF;

/**
 *  $Header $ 
 *  @author matthias <matthias.fuessel@gmx.net>
 * 
 * Removes object with given id.
 */
public class QTypeRemoveObject implements ConsoleQuestionTypeIF {
	
	WorldComponent world;
	
	public QTypeRemoveObject(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "removeobject";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		boolean resultOk = true;
		String toReturn = null;
		String[] parameters = question.getParameters();
				 // Format: <object id>
		if (parameters.length < 1) {
			toReturn = "removeobject: \n";
			toReturn += "required Paramters: \n";
			toReturn += "<object id>";
			resultOk = false;
		} else {
			AbstractObjectPart obj = world.getWorld().getObjectPart(Long.parseLong(parameters[0]));
			if (obj == null) {
				toReturn = "Object with id " + parameters[0] + " does not exist.";
				resultOk = false;
			} else if (obj instanceof AgentObjectIF) { 
				toReturn = "Can't remove agent representation (" + parameters[0] + ").";
				resultOk = false;
			} else {
				world.getWorld().removeObject(obj);
				toReturn = "Object with id " + parameters[0] + " removed.";
			}
		}
		
		return factory.createAnswer(resultOk? AnswerTypesIF.ANSWER_TYPE_OK : 
								AnswerTypesIF.ANSWER_TYPE_ERROR,question,toReturn,step);
	}

}
