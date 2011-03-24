/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/consoleservice/AnswerFactoryIF.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.consoleservice;

public interface AnswerFactoryIF {

	/**
	 * Creates an AnswerIF of type "type", answering the question "question". Note that it is
	 * not the job of the AnswerFactory to really answer the question - it just creates the
	 * appropriate object from the parametes. The actual answer is to be created in the ConsoleQuestionTypeIF
	 * implementations that then call this method for creating the AnswerIF object, passing these values:
	 * @param type The type of the AnswerIF to be created (@link AnswerTypesIF)
	 * @param question The answered question. This is only needed to get some values, not for an actual answer!
	 * @param content The content to be transported. May be null if the type does not require any content.
	 * @param step The current simulation step
	 * @return A new AnserIF of the given type with the given content.
	 * @see AnswerTypesIF
	 */
	public AnswerIF createAnswer(int type, QuestionIF question, Object content, long step);

}
