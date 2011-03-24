/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeChangeObjectProperties.java,v 1.5 2005/01/28 16:33:59 fuessel Exp $
 */
package org.micropsi.comp.world.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.comp.world.ObjectProperties;
import org.micropsi.comp.world.WorldComponent;
import org.micropsi.comp.world.objects.AbstractObjectPart;

/**
 *  $Header $ 
 *  @author matthias <matthias.fuessel@gmx.net>
 * 
 * Changes properties of object with given id.
 */
public class QTypeChangeObjectProperties implements ConsoleQuestionTypeIF {
	
	WorldComponent world;
	
	public QTypeChangeObjectProperties(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "changeobjectproperties";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		String[] parameters = question.getParameters();
				 // Format: <object id>
		if (parameters.length < 1 || question.getAdditionalData() == null) {
			String toReturn = "changeobjectproperties: \n";
			toReturn += "required Paramters: \n";
			toReturn += "<object id> \n";
			toReturn += "(question.getAdditionalData() must return MTreeNode object containing ObjectProperties)";
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,question,toReturn,step);
		} 
		AbstractObjectPart obj = world.getWorld().getObjectPart(Long.parseLong(parameters[0]));
		if (obj == null) {
			String toReturn = "Object with id " + parameters[0] + " does not exist.";
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,question,toReturn,step);
		}
		
		ObjectProperties properties = new ObjectProperties((MTreeNode) question.getAdditionalData());
		
		if (obj.setProperties(properties)) {
			String toReturnString = "Changed properties for object with id " + parameters[0] + ".";
			MTreeNode toReturn;
			if (properties.size() > 0) {
				toReturn = new MTreeNode("success, comments" + " The following property changes produced comments.", toReturnString, null);
				toReturn.addChild(properties.toMTreeNode());
			} else {
				toReturn = new MTreeNode("success", toReturnString, null);
			}
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_OK,question,toReturn,step);
		}
		
		MTreeNode toReturn = new MTreeNode("error", "Error changing following properties for object id " + parameters[0], null);
		toReturn.addChild(properties.toMTreeNode());
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,question,toReturn,step);
	}

}
