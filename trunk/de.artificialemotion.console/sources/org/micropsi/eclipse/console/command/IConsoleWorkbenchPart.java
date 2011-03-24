/*
 * Created on 12.04.2003
 *
 */
package org.micropsi.eclipse.console.command;

import org.eclipse.ui.IWorkbenchPart;
import org.micropsi.comp.console.AnswerHandlerIF;

/**
 * @author daniel
 * 
 * AnswerHandler, Views and Editors of the console should implement this.
 * 
 */
public interface IConsoleWorkbenchPart extends IWorkbenchPart, AnswerHandlerIF {

}
