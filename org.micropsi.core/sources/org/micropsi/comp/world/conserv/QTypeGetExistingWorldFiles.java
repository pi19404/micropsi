/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeGetExistingWorldFiles.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.world.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.comp.world.WorldComponent;

/**
 *  $Header $
 *  @author matthias <matthias.fuessel@gmx.net>
 *
 * Handles questions with name 'getexistingworldfiles'. Returns a list of filenames (MTreeNode).
 */
public class QTypeGetExistingWorldFiles implements ConsoleQuestionTypeIF {
	
	WorldComponent world;
	
	public QTypeGetExistingWorldFiles(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "getexistingworldfiles";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		String[] fileNames = world.getWorldFilenames();
		MTreeNode toReturn = new MTreeNode("filenames", "", null);
		if (fileNames != null) {
			for (int i = 0; i < fileNames.length; i++) {
				toReturn.addChild(fileNames[i], "");
			}
		}
		
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,question,toReturn,step);
	}

}
