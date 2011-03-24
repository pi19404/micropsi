/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeGetObjectTypes.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
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
import org.micropsi.comp.world.WorldObjectType;

/**
 *  $Header $
 *  @author matthias <matthias.fuessel@gmx.net>
 *
 * Returns all object types one can create.
 */
public class QTypeGetObjectTypes implements ConsoleQuestionTypeIF {
	
	WorldComponent world;
	
	public QTypeGetObjectTypes(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "getobjecttypes";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		Collection types = world.getObjectTypes().getTypes();
		MTreeNode toReturn = new MTreeNode("objecttypes", null, null);
		Iterator it = types.iterator();
		while (it.hasNext()) {
			toReturn.addChild("type", ((WorldObjectType) it.next()).getTypeName());
		}
		
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,question,toReturn,step);
	}

}
