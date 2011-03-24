/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/timer/TimerComponent.java,v 1.6 2006/01/18 02:32:33 vuine Exp $
 */
package org.micropsi.comp.timer;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.common.AbstractComponent;
import org.micropsi.comp.common.ActiveComponentIF;
import org.micropsi.comp.common.AnswerDispatcherIF;
import org.micropsi.comp.common.ProtocolVersionChecker;
import org.micropsi.comp.common.TouchCreator;
import org.micropsi.comp.messages.MAnswer;
import org.micropsi.comp.messages.MConfirmation;
import org.micropsi.comp.messages.MTick;
import org.micropsi.comp.timer.conserv.QTypeGetInfo;
import org.micropsi.comp.timer.conserv.QTypeSetSimulationsSpeed;
import org.micropsi.comp.timer.conserv.QTypeSwitchSync;

public class TimerComponent extends AbstractComponent implements ActiveComponentIF {

	public Thread theThread;
	boolean startit = true;
	private TouchCreator toucher;	
	private long simstep = 0;
	private int STEPLENGTH = 100;
	private boolean start = false;
	private boolean syncmode = false;
	private Timestamp starttime;
	
	private ArrayList<String> nonReadyComponents = new ArrayList<String>(10);
	private ArrayList<String> readyComponents = new ArrayList<String>(10);

	private ComChannelRequest req;
//	private ComChannelResponse resp;
	private MTick tick;
	
	private long lasttick;
	private long durationsum;
	private long durationcounter = 1;
	private long timingproblemcounter = 0;
	
	protected void performInitialisation() throws MicropsiException {
		STEPLENGTH = config.getIntConfigValue(prefixKey+".steplength");
		start = config.getBoolConfigValue(prefixKey+".autostart");
		syncmode = config.getBoolConfigValue(prefixKey+".syncmode");

		tick = new MTick();
		req = new ComChannelRequest("tick",tick,getComponentID());
		lasttick = System.currentTimeMillis();
		
		servers.registerRequestHandler("timeragentserver", 
			new AgentRequestHandler(this));

		consoleService.registerQuestionType(new QTypeSetSimulationsSpeed(this));
		consoleService.registerQuestionType(new QTypeGetInfo(this));
		consoleService.registerQuestionType(new QTypeSwitchSync(this));
	
		toucher = new TouchCreator("timerserverclient",this,new AnswerDispatcherIF() {
			public void dispatchAnswers(List<AnswerIF> answers) {
			}

			public void reportException(MicropsiException e) {
				logger.error("Server not reachable, stopping timer.");
				theThread = null;
				exproc.handleException(e);
			}
			
		}, null);
		toucher.start();
	}

	public void start() {
		if(start) {
			simstep = 0;
			starttime = new Timestamp(System.currentTimeMillis());
    		if (theThread == null) {
        		theThread = new Thread(this, getComponentID());
	        	theThread.start();
		    }
		}
	}

	public void run() {
		
		try {
			ProtocolVersionChecker.checkServerVersion("timerserverclient",clients,getComponentID());
		} catch (MicropsiException e) {
			exproc.handleException(e);
			logger.fatal("Didn't start the timer: "+getComponentID());
			startit = false;			
		}
		
		Thread myThread = Thread.currentThread();
		long diff = 0;
	    while (theThread == myThread && startit) {
        	try {
            	Thread.sleep(10);
            	diff = System.currentTimeMillis()-lasttick;
            	if(diff >= STEPLENGTH) {            	
	            	if(!syncmode) {
	            		tickNow();
    	        	} else {
    					if(nonReadyComponents.size() == 0) tickNow();	        		
        	    	}
            	}
        	} catch (InterruptedException e) {
        		exproc.handleException(e);
        	}
	    }	    
	}
	
	protected synchronized void tickNow() {
		if(isSyncmode()) {
			synchronized(readyComponents) { synchronized(nonReadyComponents) {
				nonReadyComponents.addAll(readyComponents);
				readyComponents.clear();
			}}
		}
		lasttick = System.currentTimeMillis();
   		simstep++;
		tick.setTime(simstep);
		try {
			toucher.reportTouch();
			MConfirmation confirm =
				(MConfirmation)clients.performRequest("timerserverclient",req).getResponseData();
			List<AnswerIF> answers = consoleService.answerStoredQuestions(null, getSimstep());
			answers.addAll(consoleService.answerQuestions(confirm.getQuestions(),getSimstep()));
			tick.clearLists();
			for(int i=0;i<answers.size();i++) tick.addAnswer((MAnswer)answers.get(i));
		} catch (MicropsiException e) {
			exproc.handleException(e);
		}
		
		long duration = System.currentTimeMillis() - lasttick;
		if(duration == 0) {
			// if duration is 0 the system's time granularity is insufficient. Typically, this is the case
			// on windows machines that can only measure time differences of about 10ms. So we assume an
			// average value to avoid claiming that a single tick doesn't use any time.
			duration = 5;
		}
		if(duration > STEPLENGTH) {
			timingproblemcounter++;
			logger.error("Step "+simstep+": Real step duration "+duration+" > STEPLENGTH "+STEPLENGTH);
		}
		durationsum += duration;
		durationcounter++;
		if(durationsum > (Long.MAX_VALUE - (2*STEPLENGTH))) {
			durationsum = duration;
			durationcounter = 1;
		}
	}
	
	protected boolean registerSynchronizedComponent(String name) {
		if(nonReadyComponents.contains(name)) return false;
		nonReadyComponents.add(name);
		return true;
	}
	
	protected void unregisterSynchronizedComponent(String name) {
		synchronized(nonReadyComponents) {
			if(nonReadyComponents.contains(name))			
				nonReadyComponents.remove(name);
		}	
		synchronized(readyComponents) {
			if(readyComponents.contains(name))			
				readyComponents.remove(name);
		}
	}
	
	protected synchronized void receiveSyncReady(String name) {
		String transfer;
		if(nonReadyComponents.contains(name)) {
			transfer = nonReadyComponents.get(nonReadyComponents.indexOf(name));
			nonReadyComponents.remove(transfer);
			readyComponents.add(transfer);
		}
	}	
	
	public long getSimstep() {
		return simstep;
	}

	public int getSteplength() {
		return STEPLENGTH;
	}

	public boolean isSyncmode() {
		return syncmode;
	}
	
	public void switchSyncmode() {
		syncmode = !syncmode;
	}
	

	public List<String> getNonReadyComponents() {
		return nonReadyComponents;
	}

	public List<String> getReadyComponents() {
		return readyComponents;
	}

	/**
	 * Returns the starttime.
	 * @return Timestamp
	 */
	public Timestamp getStarttime() {
		return starttime;
	}
	
	public int getInnerType() {
		// timers do not have inner types
		return 0;
	}

	public void setStepLength(int length) {
		STEPLENGTH = length;
	}

	public void shutdown() {
		toucher.shutdown();
		startit = false;
	}

	public long getTimingProblems() {
		return timingproblemcounter;
	}

	public double getAverageRealStepDuration() {
		return durationsum / durationcounter;
	}
}
