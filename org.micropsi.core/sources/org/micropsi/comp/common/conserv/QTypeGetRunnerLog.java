/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/common/conserv/QTypeGetRunnerLog.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.common.conserv;

import java.io.File;
import java.io.FileInputStream;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.common.AbstractComponent;

public class QTypeGetRunnerLog implements ConsoleQuestionTypeIF {

	private static final String QNAME = "getrunnerlog";
	private AbstractComponent comp;
	
	public QTypeGetRunnerLog(AbstractComponent comp) {
		this.comp = comp;
	}

	public String getQuestionName() {
		return QNAME;
	}

	public AnswerIF answerQuestion(AnswerFactoryIF factory,QuestionIF question,long step) {
		
		try {
			File file = new File(comp.getComponentRunner().getLogFile());
			FileInputStream inp = new FileInputStream(file);
			
			byte[] b = new byte[(int)file.length()];
			inp.read(b);
			
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_STRING,question,new String(b),step);
		} catch (Exception e) {
			return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_ERROR,question,"Unable to read logfile: "+e.getMessage(),step);
		}
	}

}
