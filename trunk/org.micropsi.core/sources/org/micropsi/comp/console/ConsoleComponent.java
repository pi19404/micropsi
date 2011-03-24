package org.micropsi.comp.console;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.apache.log4j.Level;
import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.communication.ComChannelResponse;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.utils.ThreadPool;
import org.micropsi.comp.common.AbstractComponent;
import org.micropsi.comp.common.ProtocolVersionChecker;
import org.micropsi.comp.common.TimeDispatcherIF;
import org.micropsi.comp.common.TouchCreator;
import org.micropsi.comp.messages.MConsoleReq;
import org.micropsi.comp.messages.MConsoleResp;
import org.micropsi.comp.messages.MQuestion;


public class ConsoleComponent extends AbstractComponent implements ConsoleFacadeIF {

	private ArrayList questions;
	private QuestionCache cache;
	private AnswerDispatcher andis;
	private TouchCreator serverToucher;
	private boolean giveup = false;
	private long currentNetStep = 0;
	
	public ConsoleComponent() throws MicropsiException {
		questions = new ArrayList();
		cache = QuestionCache.getInstance();
		andis = new AnswerDispatcher(this);		
	}
	
	/*(non-Javadoc)
	 * @see org.micropsi.comp.common.AbstractComponent#performInitialisation()
	 */
	protected void performInitialisation() throws MicropsiException {
		
		ProtocolVersionChecker.checkServerVersion(
				"consoleserverclient",
				clients,
				getComponentID());

		MConsoleReq consoleReq = new MConsoleReq();
		consoleReq.setRequestType(MConsoleReq.CONSOLEREQ_FIRSTTIME);

		ComChannelRequest req = new ComChannelRequest("console", consoleReq, getComponentID());
		
		ComChannelResponse resp = clients.performRequest("consoleserverclient", req);
		MConsoleResp cres = (MConsoleResp) resp.getResponseData();
		overrideComponentID(cres.getControltext(),true);
		logger.info("Console registered. New componentID: "+cres.getControltext());

		serverToucher = new TouchCreator("consoleserverclient",this,andis,new TimeDispatcherIF() {
			public void newTimeIs(long time) {
				currentNetStep = time;
			}
		},100);
		serverToucher.start();
		
		String functionality = config.getConfigValue(prefixKey+".functionality");
		if(functionality == null) functionality = "";
		
		String[] elements = functionality.split(",");
		for(int i=0;i<elements.length;i++) {
			String f = elements[i].trim();
						
			try {
				ConsoleFunctionalityIF instance = (ConsoleFunctionalityIF)Class.forName(f).newInstance();
				instance.initialize(this,config,prefixKey);
			} catch (Exception e) {
				throw new MicropsiException(10, f, e);
			}
			
		}
		
	}

	/*(non-Javadoc)
	 * @see org.micropsi.comp.common.AbstractComponent#getInnerType()
	 */
	protected int getInnerType() {
		return 0;
	}

	/*(non-Javadoc)
	 * @see org.micropsi.comp.common.AbstractComponent#shutdown()
	 */
	public void shutdown() {
		
		serverToucher.shutdown();
		
		try {
			MConsoleReq lastrequest = new MConsoleReq();
			lastrequest.setRequestType(MConsoleReq.CONSOLEREQ_LASTTIME);
			ComChannelRequest req = new ComChannelRequest("console", lastrequest, getComponentID());
			clients.performRequest("consoleserverclient", req);
			logger.debug("Last console request successfully sent.");
		} catch (MicropsiException e) {
			logger.error("Shutdown exception: ",e);
		}
	}

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
    public void sendCommand(
        int tolerance,
        int lifetime,
        String componentID,
        String command,
        String parameters,
        Object additionalData,
		boolean blocking) {

    	int answerType = QuestionIF.AM_DONT_ANSWER;
    	if(blocking) {
    		answerType = QuestionIF.AM_ANSWER_ONCE;
    	}
    	
        MQuestion q = new MQuestion(command, answerType);
        q.setDestination(componentID);
        StringTokenizer tokenizer = new StringTokenizer(parameters);
        while (tokenizer.hasMoreTokens()) {
            q.addParameter(tokenizer.nextToken());
        }
        if (additionalData != null) {
            q.setAdditionalData(additionalData);
        }

        if(!blocking) {
        	lookUpQuestion(tolerance, q, null, lifetime, false);
    	} else {
    		try {
				askBlockingQuestion(q, lifetime);
			} catch (MicropsiException e) {
				getExproc().handleException(e);
			}
    	}
    }


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
    public void sendCommand(int tolerance, String componentID, String command, String parameters,
                            Object additionalData, boolean blocking){
                                sendCommand(tolerance, DEFAULT_LIFETIME, componentID,
                                command, parameters, additionalData, blocking);
                            }


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
	public void sendCommand(
		int tolerance,
		String componentID,
		String command,
		String parameters,
		boolean blocking) {
		sendCommand(tolerance, componentID, command, parameters, null, blocking);
	}

    


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
	public void getInformation(
		int tolerance,
        int lifetime,
		String componentID,
		String question,
		String parameters,
		AnswerQueueIF callback,
		Object additionalData,
        boolean ignoreWaitingQuestions) {

		MQuestion q = new MQuestion(question, QuestionIF.AM_ANSWER_ONCE);
		q.setDestination(componentID);
		StringTokenizer tokenizer = new StringTokenizer(parameters);
		while (tokenizer.hasMoreTokens()) {
			q.addParameter(tokenizer.nextToken());
		}

		if (additionalData != null) {
			q.setAdditionalData(additionalData);
		}
		lookUpQuestion(tolerance, q, callback,lifetime, ignoreWaitingQuestions);

	}



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
    public void getInformation(
        int tolerance,
        String componentID,
        String question,
        String parameters,
        AnswerQueueIF callback,
        Object additionalData) {

        getInformation(tolerance, DEFAULT_LIFETIME, componentID, question, parameters, callback,
        additionalData,false);
    }

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
	public void getInformation(
		int tolerance,
		String componentID,
		String question,
		String parameters,
		AnswerQueueIF callback) {
		getInformation(
			tolerance,
            DEFAULT_LIFETIME,
			componentID,
			question,
			parameters,
			callback,
			null, false);
	}

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
	public void subscribe(
		int frequency,
		String componentID,
		String question,
		String parameters,
		AnswerQueueIF callback,
		Object additionalData) {

		int answerMode =
			frequency >= 100
				? QuestionIF.AM_ANSWER_EVERY_100_STEPS
				: frequency >= 50
				? QuestionIF.AM_ANSWER_EVERY_50_STEPS
				: frequency >= 10
				? QuestionIF.AM_ANSWER_EVERY_10_STEPS
				: frequency >= 5
				? QuestionIF.AM_ANSWER_EVERY_5_STEPS
				: QuestionIF.AM_ANSWER_CONTINUOUSLY;

		MQuestion q = new MQuestion(question, answerMode);
		StringTokenizer tokenizer = new StringTokenizer(parameters);
		while (tokenizer.hasMoreTokens()) {
			q.addParameter(tokenizer.nextToken());
		}
		q.setDestination(componentID);

		q.setAdditionalData(additionalData);

		askQuestion(q, callback);
	}

	/**
	 * @param frequency
	 * @param componentID
	 * @param question
	 * @param parameters
	 * @param callback
	 */
	public void subscribe(
		int frequency,
		String componentID,
		String question,
		String parameters,
		AnswerQueueIF callback) {
		subscribe(frequency, componentID, question, parameters, callback, null);
	}

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
	public void unsubscribe(
		String componentID,
		String question,
		String parameters,
		AnswerQueueIF callback,
		Object additionalData) {
		MQuestion q = new MQuestion(question, QuestionIF.AM_STOP_ANSWERING);
		StringTokenizer tokenizer = new StringTokenizer(parameters);
		while (tokenizer.hasMoreTokens()) {
			q.addParameter(tokenizer.nextToken());
		}
		q.setDestination(componentID);
		q.setAdditionalData(additionalData);
		askQuestion(q, callback);
	}
    
    /**
     * 
     * Unsubscribes the callback from the given question.
     * 
	 * @param q
	 * @param callback
	 */
	public void unsubscribe(MQuestion q, AnswerQueueIF callback){
        q.setAnswerMode(QuestionIF.AM_STOP_ANSWERING);
        askQuestion(q, callback);
    }
    
    /**
     * 
     * Unsubscribes the callback from all question it is registered to.
     * 
	 * @param callback
	 */
	public void unsubscribeAll(AnswerQueueIF callback){
              
        Iterator i = andis.getSubscriptions(callback).iterator(); 
        
        while(i.hasNext()){
            MQuestion q = (MQuestion) i.next();
            this.unsubscribe(q, callback);
        }
    }
	
	/**
	 * Unsubscribes all callbacks from all questions.
	 * 
	 *
	 */
	public void unsubscribeAll() {

		Iterator i = andis.getSubscriptions(null).iterator(); 
		
		while(i.hasNext()){
			MQuestion q = (MQuestion) i.next();
			q.setAnswerMode(QuestionIF.AM_STOP_ANSWERING);
			ThreadPool.getDefaultInstance().addJob((new NonBlockingQuestion(q,this)));
		}
	}

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
	public void unsubscribe(
		String componentID,
		String question,
		String parameters,
		AnswerQueueIF callback) {
		unsubscribe(componentID, question, parameters, callback, null);
	}


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
     */
	public AnswerIF askBlockingQuestion(MQuestion q, int timeout) throws MicropsiException {
        long time = System.currentTimeMillis();
        long t = timeout * 1000;
		AnswerIF ret;
		if (q.getAnswerMode() != QuestionIF.AM_ANSWER_ONCE) {
			ret = null;
		} else {
			BlockingAnswerQueue callback = new BlockingAnswerQueue();
			askQuestion(q, callback);
			while (!callback.isAnswered()) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					getExproc().handleException(e);
				}
                if(System.currentTimeMillis() - time > t){
                    throw new MicropsiException(4000, q.toString());
                }
			}
			ret = callback.getAnswer();
		}
		return ret;
	}

    /**
     * Method askBlockingQuestion.
     * 
     * This method sends a Question and waits for its answer. answerMode must be
     * @see org.micropsi.common.consoleservice.QuestionIF.AM_ANSWER_ONCE
     * 
     * @param q - The question to be asked.
	 * @return AnswerIF - The answer or null, if the answerMode was wrong.
	 * @throws MicropsiException if there is no answer after a timeout of 30 seconds.
	 */
	public AnswerIF askBlockingQuestion(MQuestion q) throws MicropsiException {
        return askBlockingQuestion(q, DEFAULT_TIMEOUT);
    }

	public class BlockingAnswerQueue implements AnswerQueueIF {
		private boolean answered = false;
		private AnswerIF a = null;
		public void dispatchAnswer(AnswerIF answer) {
			a = answer;
			answered = true;
		}
		public void handleAnswers() {
		}

		public void run() {
			handleAnswers();
		}
		public boolean isAnswered() {
			return answered;
		}
		public AnswerIF getAnswer() {
			return a;
		}
	}

	private void lookUpQuestion(
		int tolerance,
		MQuestion question,
		AnswerQueueIF callback, int lifetime, boolean ignoreWaitingQuestions) {

		if (tolerance == ZERO_TOLERANCE)
			askQuestion(question, callback);
		else {
			CachedQuestion q = new CachedQuestion(question);
			AnswerIF answer = cache.findAnswer(q);
			if (answer != null
				//console.now() generates network traffic. So do not paralellize this condition
				&& (((tolerance == TOTAL_TOLERANCE) && !ignoreWaitingQuestions) 
						|| currentNetStep - answer.getStep() <= tolerance)) {

				if (callback != null) {
					callback.dispatchAnswer(answer);
					callback.handleAnswers();
				}

			} else {
				askQuestion(question, callback, ignoreWaitingQuestions, lifetime);
			}
		}
	}

    private void askQuestion(MQuestion q, AnswerQueueIF callback, boolean ignoreWait, int lifetime){

        q.setOrigin(getComponentID());
        if (q.getDestination() == null) {
				getLogger().log(Level.ERROR,"Question w/o destination dropped.");
        } else {

            if (andis.registerQuestion(q, callback,lifetime)||ignoreWait) {
                //q is not pending. Otherwise we don't have to send the question.
            	ThreadPool.getDefaultInstance().addJob(new NonBlockingQuestion(q,this));
            }
        }
    }

	/**
	 * Method askQuestion.
	 * 
	 * Sends a question to the Server.
	 * 
	 * @param question The question.
	 * @param callback The callback for non blocking questions. 
	 */
	private void askQuestion(MQuestion q, AnswerQueueIF callback) {
        askQuestion(q, callback, false, DEFAULT_LIFETIME);
	}

	
	void askRemoteQuestion(MQuestion question) {
		
		if(giveup) {
			getLogger().warn("No server connection. Question not sent: "+question);
			return;
		}
		
		MConsoleReq request = new MConsoleReq();

		serverToucher.reportTouch();

		request.addQuestion(question);
		
		try {
			MConsoleResp resp = 
			((MConsoleResp) clients.performRequest(
					"consoleserverclient", new ComChannelRequest(
							"console", request,getComponentID())).getResponseData());

			currentNetStep = resp.getTime();
			
			List<AnswerIF> answers = resp.getAnswers();
			andis.dispatchAnswers(answers);
			
		} catch (Exception e) {
			getExproc().handleException(e);            
		}
	}
	
	public boolean isSystemReachable() {
		return (!giveup);
	}
	
	/**
	 * Method processQueue.
	 * 
	 * Processes the questions. This method is called by @see EclipseConsole,
	 * shortly after creation.
	 */
	void processQueue() {
		ListIterator i = questions.listIterator();
		while (i.hasNext()) {
			((Thread) i.next()).start();
			i.remove();
		}
	}

	public long getTime() {
		return currentNetStep;
	}
	
}
