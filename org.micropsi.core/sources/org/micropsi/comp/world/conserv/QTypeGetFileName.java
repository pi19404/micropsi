/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeGetFileName.java,v 1.3 2004/11/27 14:03:39 fuessel Exp $
 */
package org.micropsi.comp.world.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.world.WorldComponent;

/**
 *  $Header $
 *  @author matthias <matthias.fuessel@gmx.net>
 *
 * Handles questions with name 'getfilename'. Returns the filename of the world file.
 */
public class QTypeGetFileName implements ConsoleQuestionTypeIF {
	
	WorldComponent world;
	
	public QTypeGetFileName(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "getfilename";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		String toReturn = world.getWorld().getFileName();
		
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_STRING,question,toReturn,step);
	}

}
