package org.micropsi.comp.console;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.ListIterator;

import org.micropsi.common.consoleservice.AnswerIF;

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
public class DefaultAnswerQueue extends AbstractAnswerQueue implements AnswerQueueIF {

    /**
	 * The part which sent the question.
	 */
	protected AnswerHandlerIF part;
    
    
    /**
     * The method which should handle the question.
     */
    protected Method handle;
	
	
    /**
	 * The answers as an ArrayList of @see org.micropsi.common.consoleservice.AnswerIF
	 * 
	 */
	protected ArrayList<AnswerIF> answers; 

    /**
	 * Method AnswerQueue.
	 * @param part
	 */
	public DefaultAnswerQueue(AnswerHandlerIF part){
        this.part = part;
        this.answers = new ArrayList<AnswerIF>();
    }
    
    
    /**
     * This constructor is for static parts, which cannot give themselves as argument
     * to the constructor.
     * 
	 * @param handle
	 */
/*	public AnswerQueue(Method handle){
        this.handle = handle;
        this.part = this;
    }*/

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
		handleAnswers();
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
        
        ListIterator<AnswerIF> i = copy.listIterator();
		while(i.hasNext()) {
			part.handleAnswer(i.next());
		}
	}


	/**
	 * @see org.micropsi.comp.console.plugin.eclipseconsole.AnswerHandlerIF#handleAnswer(org.micropsi.common.consoleservice.AnswerIF)
     * 
     * This method is called, when the we are the part.
     * 
	 */
/*	public void handleAnswer(AnswerIF a) {
        Object[] args = {a};
		try {
			handle.invoke(null, args);
		} catch (IllegalAccessException e) {
			ConsoleRuntimeUser.getInstance().handleException(e);
		} catch (InvocationTargetException e) {
			ConsoleRuntimeUser.getInstance().handleException(e);
		}

	}
*/    
    
    public Method getHandle(){
        return handle;
    }
    
    
    public AnswerHandlerIF getHandler(){
        return part;
    }

}
