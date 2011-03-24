package org.micropsi.comp.console;

import org.micropsi.common.consoleservice.AnswerIF;

public interface AnswerQueueIF extends Runnable { 

	/**
     * 
     * This method is called to dispatch one answer.
     * 
	 * @param answer
	 */
	public void dispatchAnswer(AnswerIF answer);
	
	/**
	 * 
     * This method is called when the caller thinks, it is time to handle
     * collected answers. It should deliver the answers.
     * 
	 */
	public void handleAnswers();
    
}
