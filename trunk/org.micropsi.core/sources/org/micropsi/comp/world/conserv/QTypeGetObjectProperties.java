/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeGetObjectProperties.java,v 1.4 2004/11/27 14:03:39 fuessel Exp $
 */
package org.micropsi.comp.world.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.comp.world.WorldComponent;
import org.micropsi.comp.world.objects.AbstractObjectPart;

/**
 *  $Header $ 
 *  @author matthias <matthias.fuessel@gmx.net>
 * 
 * Handles "Questions" with name 'getobjectinfostring'. Take an object id and return a string
 * describtion of the specified object.
 *
 */
public class QTypeGetObjectProperties implements ConsoleQuestionTypeIF {
	
	WorldComponent world;
	
	public QTypeGetObjectProperties(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "getobjectproperties";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		MTreeNode toReturn = null;
		String[] parameters = question.getParameters();
				 // Format: <object id>
		if (parameters.length < 1) {
			String s = "getobjectproperties: \n";
			s += "required Paramters: \n";
			s += "<object id>";
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,question,s,step);
		} else {
			AbstractObjectPart obj = world.getWorld().getObjectPart(Long.parseLong(parameters[0]));
			if (obj == null) {
				String s = "getobjectproperties: \n";
				s += "No object with id " + parameters[0];
				return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,question,s,step);
			}
			toReturn = new MTreeNode("success", "", null);
			toReturn.addChild(obj.getProperties().toMTreeNode()); 
		}
		
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,question,toReturn,step);
	}

}
