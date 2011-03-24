/*
 * Created on 02.03.2003
 */
package org.micropsi.comp.console;

import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.QuestionIF;

/**
 * @author daniel
 * 
 * A lightweight Answer class for caching. Does not contain the question.
 * 
 */
public class CachedAnswer implements AnswerIF{
	
	long step;
	Object content;
	int answerType;
	
    public CachedAnswer(){
        step = 0;
        content = null;
        answerType = 0;
    }
    
	public CachedAnswer(AnswerIF a){
		step = a.getStep();
		content = a.getContent();
		answerType = a.getAnswerType();		
	}

	/**
	 * @return int
	 */
	public int getAnswerType() {
		return answerType;
	}

	/**
	 * @return Object
	 */
	public Object getContent() {
		return content;
	}

	/**
	 * @return int
	 */
	public long getStep() {
		return step;
	}

	/** 
	 * @see org.micropsi.common.consoleservice.AnswerIF#getAnsweredQuestion()
	 */
	public QuestionIF getAnsweredQuestion() {
		return null;
	}

}
