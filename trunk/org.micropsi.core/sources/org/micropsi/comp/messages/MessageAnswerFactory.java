/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MessageAnswerFactory.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.comp.messages;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.QuestionIF;

public class MessageAnswerFactory implements AnswerFactoryIF {

	public AnswerIF createAnswer(int type,QuestionIF question,Object content,long step) {
		return new MAnswer(type,(MQuestion)question,content,step);
	}
	
}
