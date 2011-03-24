/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/conserv/QTypeCreateWorldMessage.java,v 1.5 2006/01/22 18:12:06 fuessel Exp $
 */
package org.micropsi.comp.world.conserv;

import java.lang.reflect.Constructor;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.world.PostOffice;
import org.micropsi.comp.world.World;
import org.micropsi.comp.world.WorldComponent;
import org.micropsi.comp.world.WorldMessageHandlerIF;
import org.micropsi.comp.world.messages.AbstractWorldMessage;
import org.micropsi.comp.world.objects.AbstractObjectPart;

/**
 *  $Header $ 
 *  @author matthias <matthias.fuessel@gmx.net>
 * 
 * Handles "Questions" with name 'createworldmessage'. Creates the requested
 * WorldMessage, sends it and returns a string describing whether it has worked.
 *
 */
public class QTypeCreateWorldMessage implements ConsoleQuestionTypeIF {
	
	World world;
	
	public QTypeCreateWorldMessage(WorldComponent worldComponent) {
		world = worldComponent.getWorld();
	}

	public String getQuestionName() {
		return "createworldmessage";
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		boolean resultOk = true;
		String resultString = null;
		
		String[] parameters = question.getParameters();
				 // Format: <sender id> <recipient id> <message java class> <parameters>
		if (parameters.length < 3) {
			resultOk = false;
			resultString = "createworldmessage: \n";
			resultString += "required Paramters: \n";
			resultString += "<sender id> <recepient id> <message java class> <message constructor parameters>";
		} else {
			try {
				int constructorParameterCount = parameters.length - 3;
				String className = parameters[2];
				if (className.indexOf(".") < 0) {
					className = "org.micropsi.comp.world.messages." + className;
				}
				Class[] parameterTypes = new Class[constructorParameterCount + 1];
				Object[] parameterValues = new Object[constructorParameterCount + 1];
				
				for (int i = 0; i < constructorParameterCount; i++) {
					parameterTypes[i] = Class.forName("java.lang.String");
					parameterValues[i] = parameters[3 + i];
				}

				AbstractObjectPart sender = world.getObjectPart(Long.parseLong(parameters[0]));
				WorldMessageHandlerIF recipient = world.getObjectPart(Long.parseLong(parameters[1]));
				
				parameterTypes[constructorParameterCount] = Class.forName("org.micropsi.comp.world.objects.AbstractObjectPart");
				parameterValues[constructorParameterCount] = sender;
				
				Constructor myConstr = Class.forName(className).getConstructor(parameterTypes);
				AbstractWorldMessage m = (AbstractWorldMessage) myConstr.newInstance(parameterValues);
				
				PostOffice.sendMessage(m, recipient);
				resultString = "createworldmessage: message " + className + " for object " + parameters[0] + " has been sent.";
				
			} catch (Exception e) {
				resultOk = false;
				resultString = "Exception trying to construct " + parameters[2] + " for Object with id " + parameters[1] + ":\n" + e;
			}
		}
		
		return factory.createAnswer(resultOk? AnswerTypesIF.ANSWER_TYPE_OK
										: AnswerTypesIF.ANSWER_TYPE_ERROR,question,resultString,step);
	}

}
