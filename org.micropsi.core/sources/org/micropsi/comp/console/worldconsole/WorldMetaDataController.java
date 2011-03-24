/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 16.04.2003
 *
 */
package org.micropsi.comp.console.worldconsole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.console.AnswerHandlerIF;
import org.micropsi.comp.console.AnswerQueueIF;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.comp.messages.MTreeNode;

/**
 * @author matthias
 *
 */
public class WorldMetaDataController extends AbstractController implements AnswerHandlerIF {
	
	private AnswerQueueIF answerQueue;
	private ConsoleFacadeIF console;
	private AnswerHandlerIF questionErrorHandler = null;
	
	private Set<String> objectTypes = new TreeSet<String>();
	private Collection<String> worldFileNames = new ArrayList<String>(10);


	public WorldMetaDataController(ConsoleFacadeIF console, Class answerQueueClassToUse) throws MicropsiException {
		super();
		this.console = console;
		
		try {
			answerQueue = (AnswerQueueIF)answerQueueClassToUse.getConstructor(new Class[] {AnswerHandlerIF.class}).newInstance(new Object[] {this});
		} catch (Exception e) {
			throw new MicropsiException(10,answerQueueClassToUse.getName(),e);
		}

		refreshTypes();
		refreshWorldFileNames();
	}

	public Set getObjectTypes() {
		return objectTypes;
	}
	
	public void refreshTypes() {
		getConsole().getInformation(0, "world", "getobjecttypes", "", answerQueue);
	}

	public void refreshWorldFileNames() {
		getConsole().getInformation(0, "world", "getexistingworldfiles", "", answerQueue);
	}

	/* (non-Javadoc)
	 * @see de.artificialemotion.comp.consoleapp.plugin.eclipseconsole.AnswerHandlerIF#handleAnswer(org.micropsi.common.consoleservice.AnswerIF)
	 */
	public void handleAnswer(AnswerIF answer) {
		if (answer.getAnsweredQuestion().getQuestionName().equals("getobjecttypes")) {
			if (answer.getAnswerType() == AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE) {
				MTreeNode node = (MTreeNode) answer.getContent();
				if (node.getName().equals("objecttypes")) {
					objectTypes.clear();
					Iterator<MTreeNode> i = node.children();
					if (i != null) {
						while (i.hasNext()) {
							objectTypes.add(i.next().getValue());
						}
					}
					setData(this);
				}
			}
		} else if (answer.getAnsweredQuestion().getQuestionName().equals("getexistingworldfiles")) {
			if (answer.getAnswerType() == AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE) {
				MTreeNode node = (MTreeNode) answer.getContent();
				if (node.getName().equals("filenames")) {
					worldFileNames.clear();
					Iterator<MTreeNode> i = node.children();
					while (i.hasNext()) {
						worldFileNames.add(i.next().getName());
					}
					setData(this);
				}
			}
		} else {
			if (getQuestionErrorHandler() != null) {
				getQuestionErrorHandler().handleAnswer(answer);
			}
		}
		
	}

	/**
	 * @return Returns the worldFileNames.
	 */
	public Collection getWorldFileNames() {
		return worldFileNames;
	}
	/**
	 * @return Returns the console.
	 */
	protected ConsoleFacadeIF getConsole() {
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
}
