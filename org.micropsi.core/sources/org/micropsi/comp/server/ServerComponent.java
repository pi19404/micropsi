/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/server/ServerComponent.java,v 1.13 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.communication.ComChannelResponse;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.common.AbstractComponent;
import org.micropsi.comp.messages.MAction;
import org.micropsi.comp.messages.MActionResponse;
import org.micropsi.comp.messages.MAnswer;
import org.micropsi.comp.messages.MPerceptionReq;
import org.micropsi.comp.messages.MPerceptionResp;
import org.micropsi.comp.messages.MQuestion;
import org.micropsi.comp.messages.MUpdateWorld;
import org.micropsi.comp.messages.MWorldResponse;
import org.micropsi.comp.server.conserv.QTypeGetAgentList;
import org.micropsi.comp.server.conserv.QTypeGetEnvironment;
import org.micropsi.comp.server.conserv.QTypeGetInfoString;
import org.micropsi.comp.server.conserv.QTypeReleaseLock;
import org.micropsi.comp.server.conserv.QTypeRequestLock;

/**
 * Manages the communication between all the other Micropsi components.
 */
public class ServerComponent extends AbstractComponent {
	
	protected class ComponentData {
		public String componentID;
		public String lockedBy;
		public long lastTouch;
		
		public void lock(String lock) {
			this.lockedBy = lock;
		}
		
		public void unlock(String unlock) {
			this.lockedBy = null;
		}	
		
		public boolean isLocked() {
			return lockedBy != null;
		}
	}

	private static final int LIFETIME = 100;
	private static final int MAX_MISSED_STEPS = 20;
	
	private ArrayList<MQuestion> consoleQuestionCache = new ArrayList<MQuestion>(50);
	private ArrayList<MAnswer> consoleAnswerCache = new ArrayList<MAnswer>(50);
	
	protected AgentManager agentManager;
	protected ActionGatherer actionGatherer;
	protected HashMap<String,ComponentData> components = new HashMap<String,ComponentData>();
	protected long simstep = 0;
	private long missedsteps = 0;
	
	private boolean debugmode = false;
	
	protected String useworld = "";
	private Object stepLock = new Object();
	
	private static ServerComponent firstInstance;
	
	public static ServerComponent getFirstInstance() {
		return firstInstance;
	}
	
	public ServerComponent() {
		if(firstInstance == null) firstInstance = this;
	}
	
	protected void performInitialisation() throws MicropsiException {
		agentManager = new AgentManager(logger);
		actionGatherer = new ActionGatherer();	

		VersionRequestHandler versionhandler = new VersionRequestHandler();
		TouchRequestHandler touchhandler = new TouchRequestHandler(this);
		
		servers.registerRequestHandler("serverconsoleserver", new ConsoleRequestHandler(this));
		servers.registerRequestHandler("serverconsoleserver", versionhandler);
		servers.registerRequestHandler("serverconsoleserver", touchhandler);
		servers.registerRequestHandler("serveragentserver", new AgentRequestHandler(this));
		servers.registerRequestHandler("serveragentserver",new PerceptionRequestHandler(this));
		servers.registerRequestHandler("serveragentserver", versionhandler);
		servers.registerRequestHandler("serveragentserver", touchhandler);		
		servers.registerRequestHandler("servertimerserver", new TimerRequestHandler(this));
		servers.registerRequestHandler("servertimerserver", versionhandler);
		servers.registerRequestHandler("servertimerserver", touchhandler);		
					
		consoleService.registerQuestionType(new QTypeGetInfoString(this));
		consoleService.registerQuestionType(new QTypeGetEnvironment(this));
		consoleService.registerQuestionType(new QTypeRequestLock(this));
		consoleService.registerQuestionType(new QTypeReleaseLock(this));
		consoleService.registerQuestionType(new QTypeGetAgentList(agentManager));

		useworld = config.getConfigValue(prefixKey+".useworld");
		
		touchServer(useworld);
	}
	
	public synchronized void tick(long simstep) {

		String[] newAgents = null;
		String[] deletedAgents = null;
		
		synchronized (stepLock) {
			actionGatherer.nextStep();
			newAgents = agentManager.getNewAgentIDs();
			deletedAgents = agentManager.getDeletedAgentIDs();
			agentManager.clearAllFlags();
			this.simstep = simstep;
		}
		
		// see the implemenation of actionGatherer to understand why this is thread-safe here
		// (there are two lists that have been switched in the nextStep method call above)
		List<MAction> actions = actionGatherer.retreiveAndClearStorage();
/*		for(int i=0;i<actions.size();i++) {
			logger.info("Action -> World from agent "+((MAction)actions.get(i)).getAgentName());
		}
*/		
	
		MUpdateWorld update = new MUpdateWorld();
		
		if(newAgents != null) {
			ArrayList<String> nal = new ArrayList<String>();
			for(int i=0;i<newAgents.length;i++) nal.add(newAgents[i]);
			update.setNewAgents(nal);		
		}
		
		if(deletedAgents != null) {
			ArrayList<String> dal = new ArrayList<String>();
			for(int i=0;i<deletedAgents.length;i++) dal.add(deletedAgents[i]);
			update.setDeletedAgents(dal);
		}
				
		update.setTime(simstep);
		List<MQuestion> questions = this.getConsoleQuestions(useworld);
		for(int i=0;i<questions.size();i++) update.addQuestion(questions.get(i));
		Iterator it = actions.iterator();
		while(it.hasNext()) 
			update.addAction((MAction)it.next());
		ComChannelRequest req = new ComChannelRequest("updateworld", update, getComponentID());
		try {
			ComChannelResponse resp = clients.performRequest("serverworldclient",req);
			touchServer(useworld);
			MWorldResponse wresp = (MWorldResponse)resp.getResponseData();
			
			ArrayList responses = wresp.getAgentResponses(); // ---> MActionResponse
			for(int i=0;i<responses.size();i++) {
				MActionResponse actionresp = (MActionResponse)responses.get(i);
				agentManager.setActionResponse(actionresp.getAgentName(), actionresp);
			}
				
			List<AnswerIF> answers = wresp.getAnswers();
			for(int i=0;i<answers.size();i++)
				this.addConsoleAnswer((MAnswer)answers.get(i));
			missedsteps = 0;
		} catch (MicropsiException e) {
			missedsteps++;
			if(missedsteps < MAX_MISSED_STEPS) {
				logger.error("The world missed step "+(simstep-1)+" or messed up something -- actions will be re-sent in the next step");
				Iterator<MAction> iter = actions.iterator();
				while(iter.hasNext()) actionGatherer.addAction(iter.next());
				exproc.handleException(e);
			} else if(missedsteps == MAX_MISSED_STEPS) {
				logger.fatal("The world missed the last "+MAX_MISSED_STEPS+" steps and probably won't recover. Giving up. Please fix the problem and restart.");
			}
		}
		
/*		if(simstep % 100 == 0) {
			logger.debug("Server stats:"+
				" actionc="+this.actionGatherer.getNumberOfActionsInCache()+
				" qc="+this.consoleQuestionCache.size()+
				" ac="+this.consoleAnswerCache.size()+
				" ccount="+this.components.size());
		}*/		
	}
	
	public String registerAgent(String proposedID, String agentclass) {
		return agentManager.addAgent(proposedID,agentclass);
	}
	
	public void removeAgent(String agentID) {
		componentCleanUp(agentID);
		agentManager.removeAgent(agentID);
		synchronized(components) {
			components.remove(agentID);
		}
	}
	
	public String announceConsole(String proposedID) {
		
		String id;
		int counter = components.size();
		synchronized(components) {
			if(components.containsKey(proposedID)) {			
				do {
					id = proposedID += "_"+counter;
					counter++;
				} while(components.containsKey(id));
			} else {
				id = proposedID;
			}
		}
		
		return id;
	}
	
	public void removeConsole(String componentID) {
		componentCleanUp(componentID);
		synchronized(components) {
			components.remove(componentID);
		}
		logger.info("Console removed: "+componentID);
	}
	
	public boolean addAgentAction(String agentID, MAction action) throws MicropsiException {
		if(debugmode) logger.debug("Adding action from "+agentID);
		if(agentManager.isActionSentFlag(agentID)) return false;
		synchronized (stepLock) {
			agentManager.setActionSentFlag(agentID);
			actionGatherer.addAction(action);
		}
		return true;
	}
	
	public void addConsoleQuestion(MQuestion question) {
		if(question == null) {
			logger.debug("Dropping question (question was null)");
			return;
		}
		
		if(question.getOrigin() == null) {
			logger.debug("Dropping question (origin was null)");
			return;			
		}
		
		if(isLocked(question.getDestination())) {
			logger.debug("Dropping question (destination was locked)");
			return;
		}
		
		if(debugmode) logger.debug("Adding question: "+question.getQuestionName()+" for: "+question.getDestination());		
		synchronized(consoleQuestionCache) {
			consoleQuestionCache.add(question);
		}
	}
		
	public void addConsoleAnswer(MAnswer answer) {
		if(answer == null) {
			logger.debug("Dropping answer (answer was null)");
			return;
		}
		
		if(answer.getStep() < this.getSimStep()) {
			logger.warn("Answer had bad step information: "+answer);
			answer.setStep(simstep);		
		}
	
		if(debugmode) logger.debug("Adding answer: "+answer.getAnsweredQuestion().getQuestionName()+" for: "+answer.getDestination());
		synchronized(consoleAnswerCache) {
			consoleAnswerCache.add(answer);
		}
	}
	
	private void sendStopItQuestionIfNecessary(MAnswer tmp) {
		if(	tmp.getAnsweredQuestion().getAnswerMode() == QuestionIF.AM_ANSWER_CONTINUOUSLY ||
			tmp.getAnsweredQuestion().getAnswerMode() == QuestionIF.AM_ANSWER_EVERY_5_STEPS ||
			tmp.getAnsweredQuestion().getAnswerMode() == QuestionIF.AM_ANSWER_EVERY_10_STEPS ||
			tmp.getAnsweredQuestion().getAnswerMode() == QuestionIF.AM_ANSWER_EVERY_50_STEPS ||
			tmp.getAnsweredQuestion().getAnswerMode() == QuestionIF.AM_ANSWER_EVERY_100_STEPS) {
		
			MQuestion stopItQuestion = new MQuestion();
		
			stopItQuestion.setAdditionalData(tmp.getAnsweredQuestion().getAdditionalData());
			stopItQuestion.setDestination(tmp.getAnsweredQuestion().getDestination());
			stopItQuestion.setParameters(tmp.getAnsweredQuestion().getParameters());
			stopItQuestion.setQuestionName(tmp.getAnsweredQuestion().getQuestionName());
			stopItQuestion.setOrigin(tmp.getAnsweredQuestion().getOrigin());
			stopItQuestion.setAnswerMode(QuestionIF.AM_STOP_ANSWERING);
		
			logger.debug("added stopItQuestion to stop "+stopItQuestion.getQuestionName());
			addConsoleQuestion(stopItQuestion);
		}		
	}
	
	public List<MAnswer> getConsoleAnswers(String remoteid) {
		ArrayList<MAnswer> toReturn = new ArrayList<MAnswer>(5);
		ArrayList<MAnswer> toDelete = new ArrayList<MAnswer>(5);
				
		MAnswer tmp;
		for(int i=0;i<consoleAnswerCache.size();i++) {
			tmp = consoleAnswerCache.get(i);
			if(tmp.getDestination().equals(remoteid)) {
				toReturn.add(tmp);
				toDelete.add(tmp);
			} else if(simstep - tmp.getStep() > LIFETIME) {
				logger.debug(	"Timeout, dropping answer for "+tmp.getDestination()+
								", question was "+tmp.getAnsweredQuestion().getQuestionName()+", timestep: "+tmp.getStep()+", now: "+simstep);			
				toDelete.add(tmp);
				sendStopItQuestionIfNecessary(tmp);
			}
		}
		
		if(!toDelete.isEmpty()) { 
			synchronized(consoleAnswerCache) {
				for(int i=0;i<toDelete.size();i++)
					consoleAnswerCache.remove(toDelete.get(i));
			}
		}
		
		return toReturn;
	}
	
	public List<MQuestion> getConsoleQuestions(String remoteid) {
		ArrayList<MQuestion> toReturn = new ArrayList<MQuestion>(5);
		ArrayList<MQuestion> toDelete = new ArrayList<MQuestion>(5);
				
		MQuestion tmp;
		for(int i=0;i<consoleQuestionCache.size();i++) {
			tmp = consoleQuestionCache.get(i);
			if(tmp.getDestination().equals(remoteid)) {
				toReturn.add(tmp);
				toDelete.add(tmp);
			} else if(simstep - tmp.getStep() > LIFETIME) {
				logger.debug(	"Timeout, dropping question for "+tmp.getDestination()+				
								", question was "+tmp.getQuestionName()+", timestep: "+tmp.getStep()+", now: "+simstep);			
				toDelete.add(tmp);
			}
		}
		
		if(!toDelete.isEmpty()) synchronized(consoleQuestionCache) {
			for(int i=0;i<toDelete.size();i++)
				consoleQuestionCache.remove(toDelete.get(i));
		}
		
		return toReturn;
	}
	
	public long getSimStep() {
		return simstep;
	}

	/**
	 * 
	 * --> For the SOAP binding, does the same job as the console request handler
	 * 
	 */
	public MAnswer[] askQuestion(MQuestion question, String origin) {
		List<QuestionIF> tmp = new ArrayList<QuestionIF>(1);
		question.setOrigin(origin);
		tmp.add(question);
		List<AnswerIF> answers = routeCSData(tmp,origin);
		MAnswer[] toReturn = new MAnswer[answers.size()];
		for(int i=0;i<answers.size();i++) toReturn[i] = (MAnswer)answers.get(i);
		answers.clear(); answers = null;
		return toReturn;
	}
	
	public List<AnswerIF> routeCSData(List<QuestionIF> questions, String remoteid) {
		ArrayList<AnswerIF> answers;
		
		for(int i=questions.size()-1;i>=0;i--) {
			MQuestion q = (MQuestion)questions.get(i);

			// check for invalid questions
			if(q.getDestination() == null) {
				logger.warn("Question without destination was dropped: "+q.getQuestionName()+" from "+q.getOrigin());
				questions.remove(i);
				continue;
			}

			// override the simstep of the question, as the console may 
			// provide a bad one			
			q.setStep(this.getSimStep());
			if(!q.getDestination().equals(getComponentID())) {
				// this is the pool of questions to be routed
				addConsoleQuestion(q);
				questions.remove(i);
			}
		}
		
		answers = getConsoleService().answerStoredQuestions(remoteid, getSimStep());
		answers.addAll(getConsoleService().answerQuestions(questions,getSimStep()));
		answers.addAll(getConsoleAnswers(remoteid));

		return answers;
	}

	public int getInnerType() {
		// servers do not have inner types
		return 0;
	}
	
	private long lastDeadComponentsCheck = 0;
	
	public void touchServer(String componentID) {
		
		synchronized(components) {
			if(!components.containsKey(componentID)) {
				logger.info("New component: "+componentID);				
				ComponentData data = new ComponentData();
				components.put(componentID,data);
				data.componentID = componentID;
				data.lastTouch = this.simstep;
			} else {
				components.get(componentID).lastTouch = this.simstep;
			}
		}
		if(simstep - lastDeadComponentsCheck > LIFETIME) {
			lastDeadComponentsCheck = simstep;
			Iterator iter = components.values().iterator();
			while(iter.hasNext()) {
				ComponentData data = (ComponentData)iter.next(); 
				if(simstep - data.lastTouch > LIFETIME) {
					logger.info("Component timed out: "+data.componentID+" (last touch: "+data.lastTouch+")");
					agentManager.removeAgent(data.componentID);
					iter.remove();
				}
			}
		}
 	}
 	
 	public Iterator<String> getLivingComponents() {
 		return components.keySet().iterator();
 	}
 	
 	public boolean requestComponentLock(String componentID) throws MicropsiException {
 		ComponentData data = components.get(componentID);
 		if(data == null) throw new MicropsiException(1001,componentID);
 		
 		if(data.isLocked()) return false;
 		data.lock(componentID);
 		return true;
 	}
 	
 	public void releaseComponentLock(String componentID) {
 		ComponentData data = components.get(componentID);
 		if(data == null) return;

 		data.unlock(componentID);
 	}
 	
 	public boolean isLocked(String componentID) {
		ComponentData data = components.get(componentID);
		if(data == null) return false;
		return data.isLocked();
	}
	/**
	 * Method retrievePerceptionFromWorld.
	 * @param perceptreq
	 * @return MPerception
	 */
	public MPerceptionResp retrievePerceptionFromWorld(MPerceptionReq perceptreq) {
		
		ComChannelRequest req = new ComChannelRequest("getperception", perceptreq, getComponentID());
		try {
			ComChannelResponse resp = clients.performRequest("serverworldclient",req);
			touchServer(useworld);
			return (MPerceptionResp)resp.getResponseData();
		} catch (Exception e) {
			exproc.handleException(e);
			MPerceptionResp empty = new MPerceptionResp();
			return empty;
		}	
	}

	public MActionResponse retrieveActionResponse(String agentID) throws MicropsiException {
		return agentManager.retrieveActionResponse(agentID);
	}
	
	public void setDebugMode(boolean debugmode) {
		this.debugmode = debugmode;
	}

	public void shutdown() {
	}

	private void componentCleanUp(String componentID) {
		for(Iterator i=consoleAnswerCache.iterator();i.hasNext();) {
			AnswerIF answer = (AnswerIF) i.next();
			if(answer.getAnsweredQuestion().getDestination().equals(componentID) || answer.getAnsweredQuestion().getOrigin().equals(componentID)) {
				i.remove();
			}
		}
		for(Iterator i=consoleQuestionCache.iterator();i.hasNext();) {
			QuestionIF question = (QuestionIF) i.next();
			if(question.getDestination().equals(componentID) || question.getOrigin().equals(componentID)) {
				i.remove();
			}
		}
	}
}
