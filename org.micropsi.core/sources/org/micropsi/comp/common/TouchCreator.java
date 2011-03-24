/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/common/TouchCreator.java,v 1.9 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.common;

import java.util.ArrayList;

import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.communication.ComChannelResponse;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.utils.ThreadPool;
import org.micropsi.comp.messages.MAnswer;
import org.micropsi.comp.messages.MConfirmation;
import org.micropsi.comp.messages.MTouch;

public class TouchCreator extends Thread {
	
	private long lasttouch;
	private long MAX_IDLE_TIME = 500;
	private int deadcounter = 0;
	private static final int MAX_DEAD = 10;
	private boolean stop = false;
	
	ArrayList<MAnswer> answersToSend = new ArrayList<MAnswer>();
	AbstractComponent component;
	AnswerDispatcherIF answerDispatcher;
	TimeDispatcherIF timeDispatcher;
	String useclient;
	Thread theThread;
	
	public TouchCreator(String useclient, AbstractComponent component, AnswerDispatcherIF dispatcher, TimeDispatcherIF timeDispatcher) {
		this.component = component;
		this.answerDispatcher = dispatcher;
		this.timeDispatcher = timeDispatcher;
		this.useclient = useclient;
		this.setName(component.getComponentID()+"-touchcreator");
	}

	public TouchCreator(String useclient, AbstractComponent component, AnswerDispatcherIF dispatcher, TimeDispatcherIF timeDispatcher, int max_idle_time) {
		this.component = component;
		this.answerDispatcher = dispatcher;
		this.timeDispatcher = timeDispatcher;
		this.useclient = useclient;
		this.MAX_IDLE_TIME = max_idle_time;
		this.setName(component.getComponentID()+"-touchcreator");
	}
	
	public void reportTouch() {
		if(answersToSend.size() == 0) {
			lasttouch = System.currentTimeMillis();
			deadcounter = 0;
		}
	}
	
	public void run() {
		theThread = Thread.currentThread();
		long diff = 0;
	    while (theThread == this && !stop) {
        	try {
            	Thread.sleep(20);
            	diff = System.currentTimeMillis()-lasttouch;
            	if(diff >= MAX_IDLE_TIME) {
            		doTouch();            	
            	}
        	} catch (InterruptedException e) {
        	}
	    }	    
	}
	
	public void addAnswerToSend(MAnswer answer) {
		synchronized(answersToSend) {
			answersToSend.add(answer);
		}
	}

	private void doTouch() {
		MTouch touch = new MTouch();
		
		ComChannelRequest req = new ComChannelRequest("touch", touch, component.getComponentID());
		if(answersToSend.size() > 0) {
			synchronized(answersToSend) {
				for(int i=0;i<answersToSend.size();i++)
					touch.addAnswer(answersToSend.get(i));
				answersToSend.clear();
			}
		}
		
		reportTouch();
				
		try {
			ComChannelResponse resp = component.clients.performRequest(useclient,req);
			final MConfirmation confirm = (MConfirmation)resp.getResponseData();

			if(timeDispatcher != null) try {
				timeDispatcher.newTimeIs(confirm.getTime());
			} catch (Exception e) {
				component.getLogger().error("Time dispatching error: "+e.getMessage(),e);
			}
			
			Runnable dispatchRunnable = new Runnable() {

				public void run() {
					if(answerDispatcher != null) try {
						answerDispatcher.dispatchAnswers(confirm.getAnswers());
					} catch (Exception e) {
						component.getLogger().error("Answer dispatching error: "+e.getMessage(),e);
					}				
				}
			};
			
			if(!confirm.getAnswers().isEmpty()) {
				ThreadPool.getDefaultInstance().addJob(dispatchRunnable);
			}
			
		} catch (MicropsiException e) {
			deadcounter++;
			
			if(deadcounter > MAX_DEAD) {
				component.logger.error("Server not reachable "+deadcounter+" times. Giving up.");
				deadcounter = 0;
				answerDispatcher.reportException(e);
				stop =true;
			}
		}	
	}

	public void shutdown() {
		stop = true;
		theThread.interrupt();
		try {
			theThread.join();
		} catch (InterruptedException e) {
//			e.printStackTrace();
		}
	}


}
