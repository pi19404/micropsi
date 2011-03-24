/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeGetObjectListString.java,v 1.4 2004/11/27 14:03:39 fuessel Exp $
 */
package org.micropsi.comp.world.conserv;


import java.util.Collection;
import java.util.Iterator;

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
 * Handles questions with name 'getobjectlist'. Returns a string containing all
 * objects with their id, objectName and objectClass.
 */
public class QTypeGetObjectListString implements ConsoleQuestionTypeIF {
	
	WorldComponent world;
	
	public QTypeGetObjectListString(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "getobjectliststring";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		Collection objects = world.getWorld().getObjects();
		String toReturn = "World has " + objects.size() + " objects: \n";
		Iterator it = objects.iterator();
		while (it.hasNext()) {
			AbstractObject obj = (AbstractObject) it.next();
			toReturn += obj.getId() + ":" + obj.getObjectName() + "," + obj.getObjectClass() + "\n";
		}
		
		
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_STRING,question,toReturn,step);
	}

}
