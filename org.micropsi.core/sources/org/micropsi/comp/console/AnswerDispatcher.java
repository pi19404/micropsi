/*
 * Created on 22.02.2003
 *
 */
package org.micropsi.comp.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.common.AnswerDispatcherIF;


public class AnswerDispatcher implements AnswerDispatcherIF {

	public static final int QUESTION_ALREADY_REGISTERED = 0;
	public static final int NEW_QUESTION = 1;

	private static final int[] STORED_MODES =
	{
		QuestionIF.AM_ANSWER_CONTINUOUSLY,
		QuestionIF.AM_ANSWER_EVERY_100_STEPS,
		QuestionIF.AM_ANSWER_EVERY_10_STEPS,
		QuestionIF.AM_ANSWER_EVERY_50_STEPS,
		QuestionIF.AM_ANSWER_EVERY_5_STEPS };

	private static final int STORED_MODES_SIZE = STORED_MODES.length;
	protected static int QUESTION_REGISTRY_INITIAL_CAPACITY = 100;
	protected static int CALLBACKS_PER_QUESTION_INITIAL_CAPACITY = 5;
	
	protected Map<QuestionIF,List<RegisteredCallback>> questionRegistry;	
	private QuestionCache questionCache;
	private ConsoleComponent console;

	
	protected AnswerDispatcher(ConsoleComponent console) {
		questionRegistry = new HashMap<QuestionIF,List<RegisteredCallback>>(QUESTION_REGISTRY_INITIAL_CAPACITY);
		questionCache = QuestionCache.getInstance();
		this.console = console;
	}

	public int getQuestionRegistrySize() {
		return questionRegistry.size();
	}

	
    /**
     * 
     * Returns a List of the questions subscribed by the callback.
     * This method is slow and should not be called frequently.
     * 
	 * @param callback, may be null to get all subscriptions from all callbacks
	 * @return
	 */
	public List<QuestionIF> getSubscriptions(AnswerQueueIF callback){
        ArrayList<QuestionIF> ret = new ArrayList<QuestionIF>();
        
        if(callback != null) {
	        if(questionRegistry.containsValue(callback)){
	            Iterator<Map.Entry<QuestionIF, List<RegisteredCallback>>> entries = questionRegistry.entrySet().iterator();
	            while(entries.hasNext()){
	                Map.Entry<QuestionIF, List<RegisteredCallback>> entry = entries.next();
	                if(((DefaultAnswerQueue)entry.getValue()).equals(callback)){
	                    ret.add(entry.getKey());
	                }
	            }
	        }
        } else {
            Iterator<Map.Entry<QuestionIF, List<RegisteredCallback>>> entries = questionRegistry.entrySet().iterator();
            while(entries.hasNext()){
                Map.Entry<QuestionIF, List<RegisteredCallback>> entry = entries.next();
                ret.add(entry.getKey());
            }
        }
        
        return ret;
    }

	/**
	 * 
	 * Registers a question.
	 * 
	 * @param q - The question.
	 * @param callback - The callback for the answer.
	 * @return boolean - true, if q was not in the registry before. 
	 */
	public synchronized boolean registerQuestion(
		QuestionIF q,
		AnswerQueueIF callback,
		int lifetime) {
		boolean status = true;
		RegisteredCallback rc = new RegisteredCallback(callback, lifetime);

		switch (q.getAnswerMode()) {

			case QuestionIF.AM_STOP_ANSWERING :
				QuestionIF qcl = (QuestionIF) q.clone();
				for (int i = 0; i < STORED_MODES_SIZE; i++) {
					qcl.setAnswerMode(STORED_MODES[i]);
					if (questionRegistry.containsKey(qcl)) {
						List<RegisteredCallback> c = questionRegistry.get(qcl);
						c.remove(callback);
						if (c.isEmpty()) {
							questionRegistry.remove(qcl);
						} else
							questionRegistry.put(qcl, c);
					}
				}
				break;
			case QuestionIF.AM_DONT_ANSWER :
				// since we do not expect an answer, we don't have to register 
				// the question, but we have to cache it.
				CachedAnswer a =
					new NoAnswer(console.getTime());
				questionCache.addQuestion(new CachedQuestion(q), a);
				break;
			default :
				List<RegisteredCallback> callbacks = questionRegistry.get(q);
				if ((callbacks) != null) {
					if (callbacks.contains(rc)) {
					  	// Question already registered.
						int i = callbacks.indexOf(rc);
						if (callbacks.get(i)
							.isAlive()) {
							// Callback already registered
						} else {
							// replacing old callback
							callbacks.set(i, rc);
                            status = false;
						}
					} else {
						// Adding callback.
                        callbacks.add(rc); 
                        status = false;
					}
				} else {
					// QuestionRegistry: This question is new.
					callbacks =
						new ArrayList<RegisteredCallback>(CALLBACKS_PER_QUESTION_INITIAL_CAPACITY);
					callbacks.add(rc);
					
				}
                questionRegistry.put(q, callbacks);
		}

		return status;
	}

	/**
	 * @param answers
	 */
	public synchronized void dispatchAnswers(List<AnswerIF> answers) {

		Iterator answerIterator = answers.iterator();
		ArrayList callbacks;

		while (answerIterator.hasNext()) {
			AnswerIF answer = (AnswerIF) answerIterator.next();
						
			QuestionIF q = answer.getAnsweredQuestion();
			int mode = q.getAnswerMode();

			callbacks = (ArrayList) questionRegistry.get(q);

			if (mode == QuestionIF.AM_ANSWER_ONCE) {
				questionRegistry.remove(q);
			}

			if (callbacks != null) {
				Iterator callbackIterator = callbacks.iterator();

				while (callbackIterator.hasNext()) {
					final AnswerQueueIF callback =
						(AnswerQueueIF) callbackIterator.next();
					
					if(callback == null) {
						console.getLogger().error("Something went wrong. No callback.");
					} else {
						callback.dispatchAnswer(answer);	 
					}
					
					callback.run();
				}
			} else {
				console.getLogger().log(
					Level.ERROR,
					"getting answers, but having no callback: \n"
						+ answer.getContent());				
			}

			questionCache.addQuestion(
				new CachedQuestion(q),
				new CachedAnswer(answer));
		}
	}

	private class RegisteredCallback extends AbstractAnswerQueue implements AnswerQueueIF {
		private long lifetime;
        private AnswerQueueIF callback;

		public RegisteredCallback(AnswerQueueIF callback, int lifetime) {
            this.callback = callback;
			this.lifetime = System.currentTimeMillis() + lifetime * 1000;
		}
		public boolean isAlive() {
			return System.currentTimeMillis() <= lifetime;
		}
		/* (non-Javadoc)
		 * @see de.artificialemotion.comp.consoleapp.plugin.eclipseconsole.AnswerQueueIF#dispatchAnswer(org.micropsi.common.consoleservice.AnswerIF)
		 */
		public void dispatchAnswer(AnswerIF answer) {
			callback.dispatchAnswer(answer);			
		}
		/* (non-Javadoc)
		 * @see de.artificialemotion.comp.consoleapp.plugin.eclipseconsole.AnswerQueueIF#handleAnswers()
		 */
		public void handleAnswers() {
			callback.handleAnswers();			
		}
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			callback.run();			
		}

	}

/*	private boolean cleanUpList(List l) {
		Iterator i = l.iterator();
		while (i.hasNext()) {
			if (!((RegisteredCallback) i.next()).isAlive()) {
				i.remove();
			}
		}
		return l.isEmpty();
	}*/

	public void reportException(MicropsiException e) {
		console.getExproc().handleException(e);		
	}

}
