/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/common/conserv/QTypeGetComponentDescriptor.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.common.conserv;

import java.util.Iterator;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.common.AbstractComponent;
import org.micropsi.comp.common.ComponentDescriptor;
import org.micropsi.comp.messages.MTreeNode;

public class QTypeGetComponentDescriptor implements ConsoleQuestionTypeIF {

	private static final String QNAME = "getcomponentdescriptor";
	private AbstractComponent comp;
	
	public QTypeGetComponentDescriptor(AbstractComponent comp) {
		this.comp = comp;
	}

	public String getQuestionName() {
		return QNAME;
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {
		ComponentDescriptor desc = comp.getComponentDescriptor();
		MTreeNode toReturn = new MTreeNode("descriptor",desc.getComponentID(),null);
		toReturn.addChild("class",desc.getComponentClass());
		toReturn.addChild("innertype",desc.getInnerType());
		Iterator iter = desc.getQuestiontypes().iterator();
		while(iter.hasNext()) toReturn.addChild("qtype",(String)iter.next());
		/*iter = desc.getServers().iterator();
		while(iter.hasNext()) toReturn.addChild("server",(String)iter.next());*/
		/*iter = desc.getClients().iterator();
		while(iter.hasNext()) toReturn.addChild("client",(String)iter.next());*/
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,question,toReturn,step);
	}

}
