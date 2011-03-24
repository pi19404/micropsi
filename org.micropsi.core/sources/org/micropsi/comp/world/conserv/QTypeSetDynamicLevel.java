/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeSetDynamicLevel.java,v 1.3 2004/11/27 14:03:39 fuessel Exp $
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
 * Handles "Questions" with name 'setdynamiclevel'. Sets the level of dynamics in the world.
 */
public class QTypeSetDynamicLevel implements ConsoleQuestionTypeIF {
	
	WorldComponent world;
	
	public QTypeSetDynamicLevel(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "setdynamiclevel";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		boolean resultOk = true;
		String toReturn = null;
		String[] parameters = question.getParameters();
				 // Format: <level>
		if (parameters.length < 1) {
			resultOk = false;
			toReturn = "setdynamiclevel: \n";
			toReturn += "required Paramters: \n";
			toReturn += "<level>";
		} else {
			try {
				world.getWorld().setDynamicLevel(Integer.parseInt(parameters[0]));
				toReturn = "Changed level of dynamics to " + world.getWorld().getDynamicLevel() + ".";
			} catch (NumberFormatException e) {
				toReturn = "<level> has to be an integer.";
				resultOk = false;
			}
		}
		
		return factory.createAnswer(resultOk? AnswerTypesIF.ANSWER_TYPE_OK :
							AnswerTypesIF.ANSWER_TYPE_ERROR,question,toReturn,step);
	}

}
