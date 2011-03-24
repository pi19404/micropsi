/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/common/AnswerDispatcherIF.java,v 1.3 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.common;

import java.util.List;

import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.exception.MicropsiException;

public interface AnswerDispatcherIF {

	public void dispatchAnswers(List<AnswerIF> answers);
	
	public void reportException(MicropsiException e);

}
