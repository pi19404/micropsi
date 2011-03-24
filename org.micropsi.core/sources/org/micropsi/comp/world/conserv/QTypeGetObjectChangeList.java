/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeGetObjectChangeList.java,v 1.1 2005/01/02 23:06:23 fuessel Exp $
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
import org.micropsi.comp.world.ChangeLog;
import org.micropsi.comp.world.ChangeLogEntry;
import org.micropsi.comp.world.WorldComponent;
import org.micropsi.comp.world.objects.AbstractObject;

/**
 *  $Header $
 *  @author matthias <matthias.fuessel@gmx.net>
 *
 * Handles questions with name 'getmodifiedobjectlist'. Returns a string containing all
 * objects with their id, objectName and objectClass.
 */
public class QTypeGetObjectChangeList implements ConsoleQuestionTypeIF {

	WorldComponent world;

	public QTypeGetObjectChangeList(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "getobjectchangelist";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		String[] parameters = question.getParameters();
		long sinceTick = -1;
		if (parameters.length >= 1) {
			sinceTick = world.getSimStep() - Long.parseLong(parameters[0]) - 1;
		} else {
			sinceTick = world.getSimStep() - 2;
		}
		MTreeNode res = new MTreeNode("success", null, null);
		ChangeLog changeLog = world.getWorld().getChangeLog();
		if (changeLog != null && changeLog.getOldestPreservedTick() <= sinceTick) {
			MTreeNode worldObjects = new MTreeNode("object change list", "", null);
			Collection updates = changeLog.getChangeEntries(sinceTick);
			for (Iterator it = updates.iterator(); it.hasNext(); ) {
				ChangeLogEntry update = (ChangeLogEntry) it.next();
				if (update.matchesChangeType(ChangeLogEntry.CT_REMOVE)) {
					worldObjects.addChild(new MTreeNode("remove", Long.toString(update.getObjectId()), null));
				} else {
					worldObjects.addChild(update.getObject().toMTreeNode());
				}
			}
			res.addChild(worldObjects);
		} else {
			MTreeNode worldObjects = new MTreeNode("object list", world.getWorld().getFileName(), null);
			// @todo2 Hack to communicate world filename
			Collection objects = world.getWorld().getObjects();
			Iterator it = objects.iterator();
			while (it.hasNext()) {
				AbstractObject object = (AbstractObject) it.next();
				worldObjects.addChild(object.toMTreeNode());
			}
			res.addChild(worldObjects);
		}
		res.addChild(new MTreeNode("globals version", Integer.toString(world.getWorld().getVersionOfGlobalData()), null));
		
		return factory.createAnswer(
			AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,
			question,
			res,
			step);
	}

}
