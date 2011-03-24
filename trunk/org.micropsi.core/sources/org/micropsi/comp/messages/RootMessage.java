/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/RootMessage.java,v 1.4 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.messages;

import java.util.ArrayList;
import java.util.List;

import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.QuestionIF;

public abstract class RootMessage implements MessageIF {

	private ArrayList<QuestionIF> questions = new ArrayList<QuestionIF>();
	private ArrayList<AnswerIF> answers = new ArrayList<AnswerIF>();
	private long time;
	
	public void clearLists() {
		for(int i=0;i<questions.size();i++) questions.remove(i);
		for(int i=0;i<answers.size();i++) answers.remove(i);		
	}
	
	public void addQuestion(MQuestion newQuestion) {
		questions.add(newQuestion);
	}
	
	public void addAnswer(MAnswer answer) {
		answers.add(answer);
	}
		
	public List<QuestionIF> getQuestions() {
		return questions;
	}
	
	public List<AnswerIF> getAnswers() {
		return answers;
	}

	public long getTime() {
		return time;
	}
	
	public void setTime(long time) {
		this.time = time;
	}
}
