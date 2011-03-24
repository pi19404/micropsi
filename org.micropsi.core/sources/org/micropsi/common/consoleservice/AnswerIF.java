/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/consoleservice/AnswerIF.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.consoleservice;

public interface AnswerIF { 
	
	/**
	 * Returns the name of the aswered question (also the value returned by the getQuestionName() method
	 * in ConsoleQuestionTypeIF.) - the returned value is the unique id of the question's type.
	 * @return the unique name of the answered question
	 */
	public QuestionIF getAnsweredQuestion();

	/**
	 * Returns the "content" field of the answer. Answer contents differ, dependent on the type of the answer
	 * (@link AnswerTypesIF).
	 * @return the content
	 */
	public Object getContent();	

	/**
	 * Returns the type of the answer - this is NOT related in any way to the question type but mainly
	 * specifies what kind of data is transported in the "content" field. The values that may be returned
	 * can be found here: @link AnswerTypesIF
	 * @return the answer's type
	 */
	public int getAnswerType(); 
	
	/**
	 * Returns the simulation step of the answer's creation; that is, of course, the time when the corresponding
	 * question was answered.
	 * @return the simulation step.
	 */
	public long getStep();
	

}
