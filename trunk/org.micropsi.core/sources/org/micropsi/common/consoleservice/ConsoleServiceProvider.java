/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/consoleservice/ConsoleServiceProvider.java,v 1.7 2005/10/20 14:10:37 vuine Exp $
 */
package org.micropsi.common.consoleservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.micropsi.common.exception.MicropsiException;

public class ConsoleServiceProvider {
	
	
	/**
	 * Wrapper class for "stored questions". A stored question is a question that is requested to be answered
	 * more than one time. The ConsoleServiceProvider wraps the question into an StoredQuestionWrapper
	 * object and keeps it until it is asked to remove the stored question. 
	 */
	protected class StoredQuestionWrapper {
	
		private QuestionIF q;
		private long lastexecutionstep;
	
		private StoredQuestionWrapper() {
			// hidden empty constructor
		}
		
		public StoredQuestionWrapper(QuestionIF q, long step) {
			this.q = q;
			lastexecutionstep = step;
		}
		
		public boolean answerQuestionInStep(long step) {			
			boolean toReturn = true;
			switch(q.getAnswerMode()) {
				case QuestionIF.AM_ANSWER_CONTINUOUSLY: 
					break;
				case QuestionIF.AM_ANSWER_EVERY_5_STEPS:
					toReturn = (step - lastexecutionstep >= 5); 
					break;
				case QuestionIF.AM_ANSWER_EVERY_10_STEPS:
					toReturn = (step - lastexecutionstep >= 10); 
					break;
				case QuestionIF.AM_ANSWER_EVERY_50_STEPS:
					toReturn = (step - lastexecutionstep >= 50); 
					break;
				case QuestionIF.AM_ANSWER_EVERY_100_STEPS:
					toReturn = (step - lastexecutionstep >= 100); 
					break;
				default: throw new RuntimeException("FIX THIS: Bad answermode ("+q.getAnswerMode()+") in wrapper. Question: "+q.toString());
			}
			if(toReturn) lastexecutionstep = step;
			return toReturn;
		}
		
		public QuestionIF getQuestion() {
			return q;
		}
	}
	
	private HashMap<String,ConsoleQuestionTypeIF> questionTypes;
	private HashMap<String,StoredQuestionWrapper> storedQuestions = new HashMap<String,StoredQuestionWrapper>();
	private AnswerFactoryIF defFact;
	private Logger logger;
	
	/**
	 * Creates the ConsoleServiceProvider and sets the AnswerFactoryIF that will create the answer objects.
	 * @param defFact the AnswerFactoryIF to be used with this ConsoleServiceProvider.
	 */
	public ConsoleServiceProvider(AnswerFactoryIF defFact, Logger logger) {
		questionTypes = new HashMap<String,ConsoleQuestionTypeIF>();	
		this.defFact = defFact;
		this.logger = logger;
	}

	/**
	 * Registers a ConsoleQuestionTypeIF so the ConsoleServiceProvider can be asked such questions.
	 * @param newQuestionType the QuestionType to be added.
	 * @param override if true, an already registered questionType with the same ID is
	 * overriden without warning.
	 * @throws MicropsiException 20 if there is already a questionType registered with the same questionName and override is false
	 */
	public void registerQuestionType(ConsoleQuestionTypeIF newQuestionType, boolean override) throws MicropsiException {
		if(!questionTypes.containsKey(newQuestionType.getQuestionName()) || override) {
			questionTypes.put(newQuestionType.getQuestionName(),newQuestionType);
		} else {
			throw new MicropsiException(20);
		}
	}
	
	/**
	 * Convenience method for registering question types without the "override" flag.
	 * Calling this is equivalent to calling registerQuestionType(newQuestionType,false);
	 * @param newQuestionType the new question type
	 * @throws MicropsiException 20 if there is already a questionType registered with the same questionName
	 */
	public void registerQuestionType(ConsoleQuestionTypeIF newQuestionType) throws MicropsiException {
		registerQuestionType(newQuestionType,false);
	}

	private AnswerIF answerSingleQuestion(QuestionIF question, long step) {
		if(!questionTypes.containsKey(question.getQuestionName())) {
			return defFact.createAnswer(
				AnswerTypesIF.ANSWER_TYPE_ERROR,				
				question,
				"This questiontype is unknown: "+question.getQuestionName(),
				step
			);
		} else {
			ConsoleQuestionTypeIF questionType = 
	  			questionTypes.get(question.getQuestionName());
			return questionType.answerQuestion(defFact,question,step);					
		}
	}
		
	private ArrayList<AnswerIF> answerQList(Iterator it, long step) {
		ArrayList<AnswerIF> toReturn = new ArrayList<AnswerIF>();

		while(it.hasNext())
			toReturn.add(answerSingleQuestion((QuestionIF)it.next(),step));			
		return toReturn;
	}
	
	/**
	 * Answers all stored questions for the given component or all components.
	 * 
	 * @param forComponent component filter: answer only stored questions from this component. null for all.
	 * @param step the current netsteü
	 * @return a list of AnswerIFs
	 */
	public ArrayList<AnswerIF> answerStoredQuestions(String forComponent, long step) {
		ArrayList<AnswerIF> toReturn = new ArrayList<AnswerIF>();
		Iterator iter = storedQuestions.values().iterator();
		while(iter.hasNext()) {
			StoredQuestionWrapper wrap = (StoredQuestionWrapper)iter.next();
			if(wrap.answerQuestionInStep(step) && (forComponent == null || forComponent.equals(wrap.getQuestion().getOrigin()))) { 
				toReturn.add(this.answerSingleQuestion(wrap.getQuestion(),step));
			}
		}
		return toReturn;
	}
	
	/**
	 * Answers a list of given questions.
	 * 
	 * @param questions An ArrayList of questions. All objects in the list must be QuestionIFs. Can be null.
	 * @param step The current simulation step
	 * @return An ArrayList of AnswerIFs.
	 * @throws ClassCastException if there is anything within the questions parameter that is no QuestionIF
	 */
	public List<AnswerIF> answerQuestions(List<QuestionIF> questions, long step) {
		ArrayList<AnswerIF> toReturn = new ArrayList<AnswerIF>();
	
		if(questions != null) {
			QuestionIF q;
			for(int i=questions.size()-1;i>=0;i--) {
				q = questions.get(i);
				switch(q.getAnswerMode()) {
					case QuestionIF.AM_ANSWER_ONCE:
						// do nothing (this leaves the question in the list, it will be answered once and not be stored)
						break;
					case QuestionIF.AM_DONT_ANSWER:
						// execute the question, but throw away the answer 
						answerSingleQuestion(q,step);
						questions.remove(i);
						break;
					case QuestionIF.AM_STOP_ANSWERING:
						// remove the question form the list of stored questions, don't answer any more
						if(storedQuestions.containsKey(q.getStorageKey()))
							storedQuestions.remove(q.getStorageKey());
						logger.debug("No longer answering stored question "+questions.get(i));
						questions.remove(i);
						break;
					default: 
						// add the question to the list of stored questions and leave it in the list of
						// questions to be answered immediately
						if(!storedQuestions.containsKey(q.getStorageKey())) {
							logger.debug("Storing question "+q+" with storage key "+q.getStorageKey());
							storedQuestions.put(
								q.getStorageKey(), new StoredQuestionWrapper(q,step)
							);
						}
				}
			}
			
			toReturn.addAll(answerQList(questions.iterator(),step));			
		}
		
		return toReturn;
	}
	
	/**
	 * Returns the names of the registered questionTypes.
	 * @return An array with the registered questionType names. Will not be null.
	 */
	public String[] getKnownQuestionTypes() {
		String[] toReturn = new String[questionTypes.size()];
		Object[] olist = questionTypes.keySet().toArray();
		for(int i=0;i<olist.length;i++) toReturn[i] = (String)olist[i];
		return toReturn;
	}

	/**
	 * removes all stored questions
	 * 
	 */
	public void shutdown() {
		storedQuestions.clear();		
	}

}
