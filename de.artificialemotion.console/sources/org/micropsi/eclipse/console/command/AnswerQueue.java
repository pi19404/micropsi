package org.micropsi.eclipse.console.command;


import java.util.ArrayList;
import java.util.ListIterator;

import org.eclipse.swt.widgets.Display;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.comp.console.AbstractAnswerQueue;
import org.micropsi.comp.console.AnswerHandlerIF;
import org.micropsi.comp.console.AnswerQueueIF;

/**
 * @author daniel
 * 
 * This class is intended to receive Answers from the @see de.artificialemotion.comp.consoleapp.plugin.eclipseconsole.ConsoleCommandProcessor
 * and to handle them appropriately. 
 *
 * Inheritants can override the @see #handleAnswers method to find their own
 * clever way of answer delivering. SWT calls can only be placed in
 * @see #handleAnswers.
 * 
 * Classes who want to dispatch answers should do it the following way:
 * 
 * <pre>
 *      //                ...
 * 	    Display.getCurrent().asyncExec (answerQueue);
 * </pre>
 * 
 * or
 * 
 * <pre>
 * 		// ...
 * 		Display.getCurrent().syncExec(answerQueue);
 * </pre>
 * 
 * Normally you would use the first way, but if you have a more sophisticated
 * @see #handleAnswers method and you have to wait for its execution, use the
 * latter one.
 */
public class AnswerQueue extends AbstractAnswerQueue implements AnswerQueueIF {

    /**
	 * The part which sent the question.
	 */
	protected AnswerHandlerIF part;
    	
	
    /**
	 * The answers as an ArrayList of @see org.micropsi.common.consoleservice.AnswerIF
	 * 
	 */
	protected ArrayList<AnswerIF> answers; 

    /**
	 * Method AnswerQueue.
	 * @param part
	 */
	public AnswerQueue(AnswerHandlerIF part){
        this.part = part;
        this.answers = new ArrayList<AnswerIF>();
    }
    
    /**
     * Method dispatchAnswer
	 * @see org.micropsi.comp.console.plugin.eclipseconsole.AnswerQueueIF#dispatchAnswer(org.micropsi.common.consoleservice.AnswerIF)
	 * 
	 * This method is called from @see de.artificialemotion.comp.consoleapp.plugin.eclipseconsole.AnswerDispatcher
	 * 
	 * Normally you don't want to override this method.
	 */
	public void dispatchAnswer(AnswerIF answer){
		synchronized (answers) {
			answers.add(answer);
		}
    }
    
	/**
	 * @see java.lang.Runnable#run()
	 * 
	 * Please don't override this method. Use @see #handleAnswers instead.
	 */
	public final void run() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {	
				handleAnswers();	
			}
		});

	}

	/**
	 * Method handleAnswers.
	 * 
	 * Delivers the answers. This method is called everytime the
	 * AnswerDispatcher thinks, it is time to handle answers (actually it calls the
     * run method). Inheritants should
	 * override this method to use their own clever way of answer delivering.
	 * 
	 * You have to ensure, that @see #answers always contains only the answers which
	 * are not yet consumed by the @see #part.
	 * 
	 * The default method is to dispatch all answers at once.
	 */
	public void handleAnswers(){
        ArrayList<AnswerIF> copy = new ArrayList<AnswerIF>();
		synchronized (answers){
			copy.addAll(answers);
			answers.clear();
		}
        
        ListIterator i = copy.listIterator();
		while(i.hasNext()) {
			part.handleAnswer((AnswerIF) i.next());
		}
	}
    
    
    public AnswerHandlerIF getHandler(){
        return part;
    }

}
