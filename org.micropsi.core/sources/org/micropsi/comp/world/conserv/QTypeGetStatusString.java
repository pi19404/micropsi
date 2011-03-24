/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeGetStatusString.java,v 1.3 2004/11/27 14:03:39 fuessel Exp $
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
 * Handles questions with name 'getstatustring'. Returns a string containing
 * info about the world component (more than getinfostring).
 */
public class QTypeGetStatusString implements ConsoleQuestionTypeIF {
	
	WorldComponent world;
	
	public QTypeGetStatusString(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "getstatusstring";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		String toReturn = "World. \n";
		toReturn += "ID: "+world.getComponentID()+"\n";
		toReturn += "SimStep: "+world.getSimStep()+"\n";
		toReturn += "Objekte: "+world.getWorld().getNumberOfObjects()+"\n";
		
		
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_STRING,question,toReturn,step);
	}

}
