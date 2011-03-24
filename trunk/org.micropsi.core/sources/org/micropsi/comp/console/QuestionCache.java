package org.micropsi.comp.console;

import java.util.LinkedHashMap;
import java.util.Map;

import org.micropsi.common.consoleservice.AnswerIF;

/**
 * 
 * A class for caching @see CachedQuestion.
 * 
 * @author daniel
 *
 */
public class QuestionCache {
	
	private Map<CachedQuestion,CachedAnswer> store;
	private static QuestionCache instance;

	/**
	 * The maximal size of the cache.
	 */
	public static int MAX_SIZE=500;
    
    public static int INITIAL_SIZE=500;
	
	/**
	 * Method getInstance.
	 * 
	 * Creates the QuestionCache if it is inexistant. Returns it.
	 * 
	 * @return QuestionCache - The instance.
	 */
	public static QuestionCache getInstance(){
		if(instance == null)
			instance = new QuestionCache();
		return instance;
	}
    
    public int getSize(){
        return store.size();
    }
	
	protected QuestionCache(){
		// @see java.util.LinkedHashMap#removeEldestEntry
		store = new LinkedHashMap<CachedQuestion,CachedAnswer>(INITIAL_SIZE){
			public boolean removeEldestEntry(Map.Entry e){
				return size() > MAX_SIZE;
			}
		};
	}

	/**
	 * Method findAnswer.
	 * 
	 * Returns a previously cached answer.
	 * 
	 * @param q - The question to be answered.
	 * @return AnswerIF - The answer, if it is cached, null otherwise.
	 */
	public AnswerIF findAnswer(CachedQuestion q){
        return store.get(q);
	}
	
	/**
	 * Method addQuestion.
	 * 
	 * Adds a question (and its answer) to the cache. If the size of the cache
	 * is larger than @see #MAX_SIZE, the eldest question is deleted.
	 * If an answer to the same question exists, it is (hopefully) deleted, so
	 * we always keep the newest information.
	 * 
	 * @param q - The Question.
	 * @param a - Its answer. 
	 */
	public synchronized void addQuestion(CachedQuestion q, CachedAnswer a){
		store.put(q, new CachedAnswer(a));
	}
}
