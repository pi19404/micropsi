/*
 * Created on 12.04.2003
 *
 */
package org.micropsi.comp.console;

import org.micropsi.common.consoleservice.AnswerIF;

/**
 * @author daniel
 * 
 * This interface is intended for receiving answers. If you send questions, 
 * you have to implement this. If you are a View or Editor, you should use
 * @see ConsoleWorkbenchPartIF instead.
 * 
 */   
public interface AnswerHandlerIF {
     
    public void handleAnswer(AnswerIF a);

}
