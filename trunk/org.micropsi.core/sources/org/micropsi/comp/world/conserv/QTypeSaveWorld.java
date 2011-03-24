/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeSaveWorld.java,v 1.3 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.world.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.world.WorldComponent;

/**
 *  @author matthias <matthias.fuessel@gmx.net>
 * 
 * Handles "Questions" with name 'saveworld'.Saves the current world setup - as default setup or in a
 * file with a given name.
 */
public class QTypeSaveWorld implements ConsoleQuestionTypeIF {
	
	WorldComponent world;
	
	public QTypeSaveWorld(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "saveworld";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		boolean resultOk = true;
		String toReturn = null;
		String[] parameters = question.getParameters();
				 // Format: <object id>
		try {
			if (parameters.length > 0) {
				world.saveWorld(parameters[0]);
			} else {
				world.saveWorld();
			}
			toReturn = "world saved. (again ;-) )";
		} catch (MicropsiException e) {
			toReturn = "Exception saving world: " + e;
			resultOk = false;
		}
		
		return factory.createAnswer(resultOk? AnswerTypesIF.ANSWER_TYPE_OK :
							AnswerTypesIF.ANSWER_TYPE_ERROR,question,toReturn,step);
	}

}
