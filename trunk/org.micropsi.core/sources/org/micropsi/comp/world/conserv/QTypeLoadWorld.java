/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeLoadWorld.java,v 1.4 2004/11/28 18:16:54 vuine Exp $
 */
package org.micropsi.comp.world.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.world.WorldComponent;

/**
 *  @author matthias <matthias.fuessel@gmx.net>
 * 
 * Handles "Questions" with name 'loadworld'. Removes all persistent objects from the current world
 * and loads a new world. Objects that are not persistent (e.g. agent objects) will remain where they are.
 */
public class QTypeLoadWorld implements ConsoleQuestionTypeIF {
	
	WorldComponent world;
	
	public QTypeLoadWorld(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "loadworld";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		boolean resultOk = true;
		String toReturn = null;
		String[] parameters = question.getParameters();
		if (parameters.length < 1) {
			resultOk = false;
			toReturn = getQuestionName() + ": \n";
			toReturn += "required Paramters: \n";
			toReturn += "filename - name of the world file to load.";
		} else {
			try {
				world.replaceWorld(parameters[0]);
				toReturn = "world has been replaced.";
			} catch (Exception e) {
				toReturn = "Exception while loading new world: " + e;
				resultOk = false;
			}
		}
		
		return factory.createAnswer(resultOk? AnswerTypesIF.ANSWER_TYPE_OK :
			AnswerTypesIF.ANSWER_TYPE_ERROR,question,toReturn,step);
	}

}
