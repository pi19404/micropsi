/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeResetWorld.java,v 1.3 2004/08/10 14:38:17 fuessel Exp $
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
 * Handles "Questions" with name 'resetworld'. Replaces all persistent objects by new Instances
 * from world setup file.
 */
public class QTypeResetWorld implements ConsoleQuestionTypeIF {
	
	WorldComponent world;
	
	public QTypeResetWorld(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "resetworld";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		boolean resultOk = true;
		String toReturn = null;
		try {
			world.resetWorld();
			toReturn = "persistent objects have been reset.";
		} catch (MicropsiException e) {
			toReturn = "Exception while reseting world: " + e;
			resultOk = false;
		}
		
		return factory.createAnswer(resultOk? AnswerTypesIF.ANSWER_TYPE_OK :
							AnswerTypesIF.ANSWER_TYPE_ERROR,question,toReturn,step);
	}

}
