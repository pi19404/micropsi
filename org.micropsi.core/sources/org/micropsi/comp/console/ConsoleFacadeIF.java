/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/console/ConsoleFacadeIF.java,v 1.4 2005/09/30 12:27:52 vuine Exp $ 
 */
package org.micropsi.comp.console;

import org.apache.log4j.Logger;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.exception.ExceptionProcessor;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.messages.MQuestion;

public interface ConsoleFacadeIF {
	
	public static final int ZERO_TOLERANCE = 0;

	public static final int TOTAL_TOLERANCE = -1;

	public static final int DEFAULT_TIMEOUT = 30;

	public static final int DEFAULT_LIFETIME = 10;

	/**
	 * 
	 * Sends a command to a component.
	 * 
	 * @param tolerance - How old (in steps) might a cached answer be? The values 
	 * @see #TOTAL_TOLERANCE and #ZERO_TOLERANCE are predefined. 
	 * @param lifetime - How long to wait for an answer.
	 * @param componentID - The component to be asked
	 * @param command
	 * @param parameters
	 * @param additionalData
	 * @param blocking
	 */
	public abstract void sendCommand(
		int tolerance,
		int lifetime,
		String componentID,
		String command,
		String parameters,
		Object additionalData,
		boolean blocking);
	/**
	 * Method sendCommand.
	 * 
	 * @see #sendCommand(int, int, String, String, String, Object)
	 * 
	 * lifetime = @see #DEFAULT_LIFETIME.
	 * 
	 * @param tolerance Maximal number of steps for cached commands.
	 * @param componentID
	 * @param command
	 * @param parameters
	 * @param additionalData
	 */
	public abstract void sendCommand(
		int tolerance,
		String componentID,
		String command,
		String parameters,
		Object additionalData,
		boolean blocking);
	
	/**
	 * Method sendCommand.
	 * 
	 * @see #sendCommand(int, int, String, String, String, Object)
	 * 
	 * No additionalData, lifetime = @see #DEFAULT_LIFETIME.
	 * 
	 * @param tolerance Maximal number of steps for cached commands.
	 * @param componentID
	 * @param command
	 * @param parameters
	 */
	public abstract void sendCommand(
		int tolerance,
		String componentID,
		String command,
		String parameters,
		boolean blocking);
	
	/**
	 * Method getInformation.
	 * 
	 * Receive an information <b>once</b>.
	 * 
	 * This method looks in the cache, if the question already exists and the
	 * answer is not older than tolerance steps. If there is no answer in the
	 * cache, we ask the component.
	 * 
	 * @param tolerance  - The allowed age of the answer in ticks. 
	 * If @see #ZERO_TOLERANCE, the cache is not considered. If greater than zero, 
	 * we look first, if a similar question is cached. 
	 * If @see #TOTAL_TOLERANCE, a cached question, if exists, is taken, 
	 * regardless of its age.
	 * @param lifetime - How long should be waited for an answer.
	 * @param componentID - The component to be asked.
	 * @param question - The question.
	 * @param parameters - The parameter string.
	 * @param callback - The callback for non blocking questions.
	 * @param additionalData - Additional Data.
	 * @param ignoreWaitingQuestions - Normally queations are registered. If a
	 * a question is already waiting for an answer, the same question is not sent
	 * once more. In certain cases it might be desired to send the same question twice
	 * (e.g. for saving documents). In this case choose true, normally you would take
	 * false here.
	 */
	public abstract void getInformation(
		int tolerance,
		int lifetime,
		String componentID,
		String question,
		String parameters,
		AnswerQueueIF callback,
		Object additionalData,
		boolean ignoreWaitingQuestions);
	/**
	 * Method getInformation.
	 * 
	 * Receive an information <b>once</b>.
	 * 
	 * This method looks in the cache, if the question already exists and the
	 * answer is not older than tolerance steps. If there is no answer in the
	 * cache, we ask the component.
	 * 
	 * @see #getInformation(int, int, String, String, String, AnswerQueueIF, Object, boolean)
	 * 
	 * lifetime = @see #DEFAULT_LIFETIME, we are waiting for similar questions.
	 * 
	 * @param tolerance  - The allowed age of the answer in ticks. 
	 * If @see #ZERO_TOLERANCE, the cache is not considered. If greater than zero, 
	 * we look first, if a similar question is cached. 
	 * If @see #TOTAL_TOLERANCE, a cached question, if exists, is taken, 
	 * regardless of its age.
	 * @param componentID - The component to be asked.
	 * @param question - The question.
	 * @param parameters - The parameter string.
	 * @param callback - The callback for non blocking questions.
	 * @param additionalData - Additional Data.
	 */
	public abstract void getInformation(
		int tolerance,
		String componentID,
		String question,
		String parameters,
		AnswerQueueIF callback,
		Object additionalData);
	/**
	 * @see #getInformation(int, int, String, String, String, AnswerQueueIF, Object, boolean)
	 * The question is sent without additionalData, lifetime = @see #DEFAULT_LIFETIME and
	 * it will be registered.
	 * 
	 * @param tolerance
	 * @param componentID
	 * @param question
	 * @param parameters
	 * @param callback
	 */
	public abstract void getInformation(
		int tolerance,
		String componentID,
		String question,
		String parameters,
		AnswerQueueIF callback);
	/**
	 * Subscribes to frequent answers to a question.
	 * 
	 * @param frequency - Frequency of answers in steps. At the moment only 1, 5
	 * 10, 50, 100 
	 * @param componentID
	 * @param question
	 * @param parameters
	 * @param additopnalData 
	 */
	public abstract void subscribe(
		int frequency,
		String componentID,
		String question,
		String parameters,
		AnswerQueueIF callback,
		Object additionalData);
	/**
	 * @param frequency
	 * @param componentID
	 * @param question
	 * @param parameters
	 * @param callback
	 */
	public abstract void subscribe(
		int frequency,
		String componentID,
		String question,
		String parameters,
		AnswerQueueIF callback);
	/**
	 * Method unsubscribe.
	 * 
	 * Unsubscribes from a subscribed question.
	 * 
	 * @param componentID - The callers componentID
	 * @param question - The questions name.
	 * @param parameters - Parameters of the question.
	 * @param callback - The callback provided with the subscribe request
	 * @param additionalData
	 */
	public abstract void unsubscribe(
		String componentID,
		String question,
		String parameters,
		AnswerQueueIF callback,
		Object additionalData);
	/**
	 * 
	 * Unsubscribes the callback from the given question.
	 * 
	 * @param q
	 * @param callback
	 */
	public abstract void unsubscribe(MQuestion q, AnswerQueueIF callback);
	/**
	 * 
	 * Unsubscribes the callback from all question it is registered to.
	 * 
	 * @param callback
	 */
	public abstract void unsubscribeAll(AnswerQueueIF callback);
	/**
	 * 
	 * Unsubscribes all callbacks from all questions.
	 *
	 */
	public abstract void unsubscribeAll();
	/**
	 * Method unsubscribe.
	 * 
	 * Unsubscribes from a subscribed question.
	 * 
	 * @param componentID - The callers componentID
	 * @param question - The questions name.
	 * @param parameters - Parameters of the question.
	 * @param callback - The callback provided with the subscribe request
	 */
	public abstract void unsubscribe(
		String componentID,
		String question,
		String parameters,
		AnswerQueueIF callback);
	/**
	 * Method askBlockingQuestion.
	 * 
	 * This method sends a Question and waits for its answer. answerMode must be
	 * @see org.micropsi.common.consoleservice.QuestionIF.AM_ANSWER_ONCE
	 * 
	 * @param q - The question to be asked.
	 * @param timeout - Timeout in seconds.
	 * @return AnswerIF - The answer or null, if the answerMode was wrong.
	 * @throws MicropsiException if there is no answer after a timeout of timeout seconds.
	 * @deprecated
	 */
	public abstract AnswerIF askBlockingQuestion(MQuestion q, int timeout) throws MicropsiException;

	/**
	 * Method askBlockingQuestion.
	 * 
	 * This method sends a Question and waits for its answer. answerMode must be
	 * @see org.micropsi.common.consoleservice.QuestionIF.AM_ANSWER_ONCE
	 * 
	 * @param q - The question to be asked.
	 * @return AnswerIF - The answer or null, if the answerMode was wrong.
	 * @throws MicropsiException if there is no answer after a timeout of 30 seconds.
	 * @deprecated
	 */
	public abstract AnswerIF askBlockingQuestion(MQuestion q) throws MicropsiException;
	
	/**
	 * Returns the exception processor for this console.
	 * 
	 * @return an exception processor, never null
	 */
	public ExceptionProcessor getExproc();
	
	/**
	 * Returns the logger for this component
	 * 
	 * @return a logger, never null
	 */
	public Logger getLogger();
}