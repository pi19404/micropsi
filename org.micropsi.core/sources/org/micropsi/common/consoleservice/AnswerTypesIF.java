/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/consoleservice/AnswerTypesIF.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.consoleservice;

public interface AnswerTypesIF {
	
	/**
	 * A simple string answer. The content field of the AnswerIF will be of type String.
	 */
	public static final int ANSWER_TYPE_STRING = 0;
	
	/**
	 * No real answer, just a confirmation that the question was executed
	 */
	public static final int ANSWER_TYPE_OK = 1;
	
	/**
	 * No real answer but a confirmation that the question was executed and a notification of the fact that
	 * the execution failed. The content field MAY contain a String with an error description, but it may
	 * also be null.
	 */
	public static final int ANSWER_TYPE_ERROR = 2;
	
	/**
	 * A complex answer. The question type itself determines the type of the "content" value of the answer.
	 * (In all current implementations it is of type @link de.artificialemotion.common.message.MTreeNode)
	 */
	public static final int ANSWER_TYPE_COMPLEX_MESSAGE = 3;
	
}
