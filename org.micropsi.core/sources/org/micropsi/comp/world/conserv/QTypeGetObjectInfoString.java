/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeGetObjectInfoString.java,v 1.4 2004/11/27 14:03:39 fuessel Exp $
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
 * Handles "Questions" with name 'getobjectinfostring'. Take an object id and return a string
 * describtion of the specified object.
 *
 */
public class QTypeGetObjectInfoString implements ConsoleQuestionTypeIF {
	
	WorldComponent world;
	
	public QTypeGetObjectInfoString(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "getobjectinfostring";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		boolean resultOk = true;
		String toReturn = null;
		String[] parameters = question.getParameters();
				 // Format: <object id>
		if (parameters.length < 1) {
			resultOk = false;
			toReturn = "getobjectinfoString: \n";
			toReturn += "required Paramters: \n";
			toReturn += "<object id>";
		} else {
			AbstractObject obj = world.getWorld().getObject(Long.parseLong(parameters[0]));
			if (obj == null) {
				resultOk = false;
				toReturn = "Object with id " + parameters[0] + " does not exist.";
			}
			toReturn = obj.getObjectName() + ":" + obj.getObjectClass() + "\n" + 
						obj.getProperties();
		}
		
		return factory.createAnswer(resultOk? AnswerTypesIF.ANSWER_TYPE_STRING :
								AnswerTypesIF.ANSWER_TYPE_ERROR,question,toReturn,step);
	}

}
