/*
 * Created on 30.05.2005
 *
 */

package org.micropsi.comp.console.worldconsole;

import java.util.Collection;
import java.util.Iterator;

import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.console.AnswerHandlerIF;
import org.micropsi.comp.console.AnswerQueueIF;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.comp.console.worldconsole.model.WorldObject;

/**
 * @author Matthias
 */
public class RemoteWorld implements AnswerHandlerIF {

	private ConsoleFacadeIF console;
	private AnswerHandlerIF questionErrorHandler = null;
	protected AnswerQueueIF answerQueue = null;


	private final String worldComponentName;

	/**
	 * @param answerQueueClassToUse 
	 * @throws MicropsiException 
	 * 
	 */
	public RemoteWorld(String worldComponentName, ConsoleFacadeIF console, Class answerQueueClassToUse) throws MicropsiException {
		this.worldComponentName = worldComponentName;
		this.console = console;
		try {
			answerQueue = (AnswerQueueIF)answerQueueClassToUse.getConstructor(new Class[] {AnswerHandlerIF.class}).newInstance(new Object[] {this});
		} catch (Exception e) {
			throw new MicropsiException(10,answerQueueClassToUse.getName(),e);
		}
		
	}
	
	public void createObject(String objectType, String objectPosition, String objectName) {
		if (objectName == null) {
			objectName = "";
		}
		getConsole().getInformation(
				0,
				100,
				getWorldComponentName(),
				"createobject",
				objectType + " " + objectName + " " + withoutSpaces(objectPosition),
				answerQueue,
				null,
				true);
	}
	
	public void removeObject(WorldObject obj) {
		getConsole().getInformation(
				0,
				100,
				getWorldComponentName(),
				"removeobject",
				Long.toString(obj.getId()),
				answerQueue,
				null,
				true);
	}
	
	public void removeObjects(Collection coll) {
		for (Iterator it = coll.iterator(); it.hasNext(); ) {
			WorldObject obj = (WorldObject) it.next();
			removeObject(obj);
		}
	}

	/**
	 * @return Returns the console.
	 */
	public ConsoleFacadeIF getConsole() {
		return console;
	}
	/**
	 * @return Returns the questionErrorHandler.
	 */
	public AnswerHandlerIF getQuestionErrorHandler() {
		return questionErrorHandler;
	}
	/**
	 * @param questionErrorHandler The questionErrorHandler to set.
	 */
	public void setQuestionErrorHandler(AnswerHandlerIF questionErrorHandler) {
		this.questionErrorHandler = questionErrorHandler;
	}
	/**
	 * @return Returns the worldComponentName.
	 */
	public String getWorldComponentName() {
		return worldComponentName;
	}

	/* @see org.micropsi.comp.console.AnswerHandlerIF#handleAnswer(org.micropsi.common.consoleservice.AnswerIF)*/
	public void handleAnswer(AnswerIF answer) {
		if (getQuestionErrorHandler() != null) {
			getQuestionErrorHandler().handleAnswer(answer);
		}
	}
	
	protected String withoutSpaces(String s) {
		StringBuffer res = new StringBuffer("");
		int i = 0;
		int j;
		do {
			j = s.indexOf(" ", i);
			if (j >= 0) {
				res.append(s.substring(i, j));
				i = j + 1;
			} else {
				res.append(s.substring(i));
			}
		} while (j >= 0);
		return res.toString();

	}
}
