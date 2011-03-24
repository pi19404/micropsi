/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeGetObjectList.java,v 1.7 2005/01/02 23:06:23 fuessel Exp $
 */
package org.micropsi.comp.world.conserv;

import java.util.Collection;
import java.util.Iterator;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.comp.world.WorldComponent;
import org.micropsi.comp.world.objects.AbstractObject;

/**
 *  $Header $
 *  @author matthias <matthias.fuessel@gmx.net>
 *
 * Handles questions with name 'getobjectlist'. Returns a string containing all
 * objects with their id, objectName and objectClass.
 */
public class QTypeGetObjectList implements ConsoleQuestionTypeIF {

	WorldComponent world;

	public QTypeGetObjectList(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "getobjectlist";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		String[] parameters = question.getParameters();
		Collection objects = null;
		if (parameters.length < 4) {
			objects = world.getWorld().getObjects();
		} else {
			//todo: Bereich
		}
		
		MTreeNode res = new MTreeNode("success", null, null);
		MTreeNode worldObjects = new MTreeNode("object list", "", null);
		Iterator it = objects.iterator();
		while (it.hasNext()) {
			AbstractObject object = (AbstractObject) it.next();
			worldObjects.addChild(object.toMTreeNode());
		}
		res.addChild(worldObjects);
		res.addChild(new MTreeNode("globals version", Integer.toString(world.getWorld().getVersionOfGlobalData()), null));

		return factory.createAnswer(
			AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,
			question,
			res,
			step);
	}

}
