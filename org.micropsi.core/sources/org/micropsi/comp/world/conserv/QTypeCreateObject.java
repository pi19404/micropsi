/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeCreateObject.java,v 1.5 2005/06/01 19:39:57 fuessel Exp $
 */
package org.micropsi.comp.world.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.comp.world.WorldComponent;
import org.micropsi.comp.world.WorldObjectType;

/**
 *  $Header $ 
 *  @author matthias <matthias.fuessel@gmx.net>
 * 
 * Creates a new object in the world. Parameter: <object type> <object name> <position>
 */
public class QTypeCreateObject implements ConsoleQuestionTypeIF {
	
	WorldComponent world;
	
	public QTypeCreateObject(WorldComponent world) {
		this.world = world;
	}

	public String getQuestionName() {
		return "createobject";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		String[] parameters = question.getParameters();
				 // Format: <object type> [<object name>] <position>
		if (parameters.length < 2) {
			String resultString = "createobject: \n";
			resultString += "required Paramters: \n";
			resultString += "<object type> [<object name>] <position>";
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,question,resultString,step);
		}
		
		String typeParameter, nameParameter, positionParameter;
		if (parameters.length == 3) {
			typeParameter = parameters[0];
			nameParameter = parameters[1];
			positionParameter = parameters[2];
		} else {
			typeParameter = parameters[0];
			nameParameter = null;
			positionParameter = parameters[1];
		}
		
		Position pos;
		try {
			pos = new Position(positionParameter);
		} catch (NumberFormatException e) {
			String resultString = "createobject: \n";
			resultString += "Parameter 'position' has invalid format.";
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,question,resultString,step);
		}
		
		WorldObjectType type = world.getObjectTypes().getType(typeParameter);
		if (type == null) {
			String resultString = "createobject: \n";
			resultString += "Unknown object type: " + typeParameter;
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,question,resultString,step);
		}
		
		if (nameParameter == null || nameParameter.equals("")) {
			nameParameter = world.getWorld().getObjectNameGenerator().createName(type.getObjectClass());
		}
		
		long id;
		try {
			id =
				world.getWorld().createObject(
					type.getJavaClass(),
					nameParameter,
					type.getObjectClass(),
					pos,
					type.getProperties());
		} catch (MicropsiException e1) {
			String resultString = "createobject: \n";
			resultString += "Exception creating object type " + typeParameter + ":\n" + e1;
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,question,resultString,step);
		}
		
		MTreeNode toReturn = new MTreeNode("success", Long.toString(id), null);
		
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,question,toReturn,step);
	}

}
