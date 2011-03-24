/*
 * Created on 20.04.2005
 *
 */

package org.micropsi.comp.console.worldconsole;

import org.micropsi.common.consoleservice.AnswerIF;

/**
 * @author Matthias
 */
public interface IRequestResultHandler {
	
	public void handleRequestResult(int requestId, AnswerIF answer);

}
