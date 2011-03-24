/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 17.04.2003
 *
 */
package org.micropsi.eclipse.worldconsole;

import org.eclipse.jface.dialogs.MessageDialog;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.comp.console.AnswerHandlerIF;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.eclipse.console.command.AnswerQueue;

/**
 * @author matthias
 *
 * This class can be used to handle answers, if the object that asks the question doesnot want to
 * handle the answer, or doesnot know wether it is still alive, when the answer is returned.
 * 
 * QuestionErrorHandler ignores OK-Answers and unknown answers, and otherwise displays an error dialog.
 */
public class QuestionErrorHandler implements AnswerHandlerIF {
	
	private static QuestionErrorHandler instance = null;
	
	private AnswerQueue answerQueue;
	
	public static QuestionErrorHandler getInstance() {
		if (instance == null) {
			instance = new QuestionErrorHandler();
		}
		return instance;
	}

	/**
	 * 
	 */
	public QuestionErrorHandler() {
		answerQueue = new AnswerQueue(this);
	}

	/* (non-Javadoc)
	 * @see de.artificialemotion.comp.consoleapp.plugin.eclipseconsole.AnswerHandlerIF#handleAnswer(org.micropsi.common.consoleservice.AnswerIF)
	 */
	public void handleAnswer(AnswerIF answer) {
		if (answer.getAnswerType() == AnswerTypesIF.ANSWER_TYPE_ERROR) {
			String[] parameters = answer.getAnsweredQuestion().getParameters();
			String message = answer.getAnsweredQuestion().getQuestionName();
			for (int i = 0; i < parameters.length; i++) {
				message+= " " + parameters[i];
			}
			message+= ":\n" + answer.getContent();
			MessageDialog.openError(null, "Remote Error", message);
		} else if (answer.getAnswerType() == AnswerTypesIF.ANSWER_TYPE_STRING) {
			String[] parameters = answer.getAnsweredQuestion().getParameters();
			String message = answer.getAnsweredQuestion().getQuestionName();
			for (int i = 0; i < parameters.length; i++) {
				message+= " " + parameters[i];
			}
			message+= ":\n" + answer.getContent();
			MessageDialog.openInformation(null, "Remote Information", message);
		} else if (answer.getAnswerType() == AnswerTypesIF.ANSWER_TYPE_OK) {
			if (answer.getAnsweredQuestion().getQuestionName().equals("saveworld")) {
				String[] parameters = answer.getAnsweredQuestion().getParameters();
				String message = answer.getAnsweredQuestion().getQuestionName();
				for (int i = 0; i < parameters.length; i++) {
					message+= " " + parameters[i];
				}
				message+= ":\n" + answer.getContent();
				MessageDialog.openInformation(null, "Remote Information", message);
			} // normally ignore 'ok'-Messages
		} else if (answer.getAnswerType() == AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE) {
			String[] parameters = answer.getAnsweredQuestion().getParameters();
			String message = answer.getAnsweredQuestion().getQuestionName();
			for (int i = 0; i < parameters.length; i++) {
				message+= " " + parameters[i];
			}
			message+=":\n";
			if (answer.getAnsweredQuestion().getQuestionName().equals("changeobjectproperties")) {
				MTreeNode node = (MTreeNode) answer.getContent();
				message+= node.getValue();
				MessageDialog.openError(null, "Remote Error", message);
			} else {
				// show complex answer...
			}
		}

	}

	/**
	 * @return
	 */
	public AnswerQueue getAnswerQueue() {
		return answerQueue;
	}

}
