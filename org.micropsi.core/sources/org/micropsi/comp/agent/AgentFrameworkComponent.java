/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/AgentFrameworkComponent.java,v 1.20 2005/11/12 17:17:15 vuine Exp $
 */
package org.micropsi.comp.agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.communication.ComChannelResponse;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.agent.aaa.AgentControllerIF;
import org.micropsi.comp.agent.aaa.AgentWorldAdapterIF;
import org.micropsi.comp.agent.aaa.PerceptTranslatorIF;
import org.micropsi.comp.agent.aaa.UrgeCreatorIF;
import org.micropsi.comp.common.AbstractComponent;
import org.micropsi.comp.common.ActiveComponentIF;
import org.micropsi.comp.common.ComponentRunnerException;
import org.micropsi.comp.common.ProtocolVersionChecker;
import org.micropsi.comp.common.TouchCreator;
import org.micropsi.comp.messages.MAction;
import org.micropsi.comp.messages.MActionResponse;
import org.micropsi.comp.messages.MAgentReq;
import org.micropsi.comp.messages.MAgentResp;
import org.micropsi.comp.messages.MAnswer;
import org.micropsi.comp.messages.MPercept;
import org.micropsi.comp.messages.MPerceptionResp;

public class AgentFrameworkComponent extends AbstractComponent implements ActiveComponentIF {

	/**
	 * The implementation of the agent. Note that especially micropsi-type agents
	 * may again vary very much on how they work and what percepts/actions they use.
	 * The theAgent object just specifies what the agent is made of (node nets, BDI
	 * or whatever someone may implement) 
	 */
	private AgentIF theAgent;
	
	/**
	 * The agentType field is only used for transmission to the world. The world
	 * decides by the agentType what representation ("agent object") it will use
	 * for this agent. (Not all agent objects have all functionality of action and
	 * perception). This is true independently of the theAgent field - there may be
	 * more than one agent <i> implementations </i> for the same agentType -- or more
	 * than one agentType that use the same implementation. An example for agentType
	 * would be "bird", causing the world to create an agent object that can fly, but
	 * not use tools. The kind of intelligence the bird uses is specified by the 
	 * theAgent implementation.  
	 */
	private String agentType;
	
	/**
	 * List of all actionDescriptors (from the aaa) that this agent uses 
	 */
	private ArrayList<ActionTranslatorIF> actionDescriptors = new ArrayList<ActionTranslatorIF>();
	
	/**
	 * List of all urge creators (from the aaa)
	 */
	private ArrayList<UrgeCreatorIF> urgeCreators = new ArrayList<UrgeCreatorIF>();
	
	/**
	 * Map of all perceptDescriptors (aaa) that this agent uses
	 */
	private HashMap<String,PerceptTranslatorIF> perceptDescriptors = new HashMap<String,PerceptTranslatorIF>(10);
	
	/**
	 * List of all agentControllers
	 */
	private ArrayList<AgentControllerIF> agentControllers = new ArrayList<AgentControllerIF>();
	
	/**
	 * Length of one cycle in the simulation. This is currently read from config, but,
	 * later on, will have to be obtained from the server.
	 */
	private int cyclelength;
	
	/**
	 * 10 percent of the cycle length (just to avoid calculating that again and again)
	 */
	private int cycletenpercent;
	
	/**
	 * cycle counter holds the number of passed (simulation!) cycles. 
	 */
	private long cyclecounter = 0;
	
	/**
	 * The thread that tells the agent thread about the timing and causes actions to
	 * be sent regularly.
	 */
	private Thread theControllerThread;
	
	/**
	 * The thread that the agent itself runs in
	 */
	private Thread theAgentThread;
	
	private MAction dummyAction = new MAction(MAction.NOOP,"");

	private MAgentReq lastrequest = new MAgentReq();
	
	private HashMap<String,ActionTranslatorIF> actionsOnTheWay = new HashMap<String,ActionTranslatorIF>();
	
	private long delta = 0;
	
	private int deadcounter = 0;
	
	private static final int MAX_DEAD_CYCLES = 10;
	
	private TouchCreator toucher;

	private long prevTicket = 0;
	
	private Object shutdownLock = new Object();
	
	/**
	 * Returns the next action ticket.
	 * @return the next free ticket for an action
	 */
	protected long getNextTicket() {
		prevTicket++;
		if(prevTicket > Long.MAX_VALUE-2) {
			prevTicket = 0;
		}
		return prevTicket;
	}
	
	/**
	 * Initializes the agent framework. Reads config, initializes word adapters,
	 * creates the theAgent implementation object.
	 * @throws MicropsiException 
	 */
	protected void performInitialisation() throws MicropsiException {
		
		String agentclass = config.getConfigValue(prefixKey + ".agentclass");
		agentType = config.getConfigValue(prefixKey + ".agenttype");
		cyclelength = config.getIntConfigValue(prefixKey + ".cyclelength");
		logger.info("Agent cyclelength: "+cyclelength);
		cycletenpercent = cyclelength / 10;
		
		try {
			theAgent = (AgentIF) Class.forName(agentclass).newInstance();
		} catch (Exception e) {
			throw new MicropsiException(10, agentclass, e);
		}
		
		ConsoleQuestionTypeIF[] qtypes = theAgent.initialize(this,prefixKey,config);
		if (qtypes != null) {
			for (int i = 0; i < qtypes.length; i++)
				consoleService.registerQuestionType(qtypes[i],false);
		}
				
		initializeWorldAdapters();
	}
	
	/**
	 * Registers an additional questiontype (with the override flag, so it's no
	 * problem to register the same type again). This is mainly meant to be used
	 * by world adapters that want to provide their own question types.
	 * @param q the new question type to be registered
	 */
	public void registerAdditionalQuestionType(ConsoleQuestionTypeIF q) {
		try {
			consoleService.registerQuestionType(q,true);
		} catch (MicropsiException e) {
			// should not happen
			exproc.handleException(e);
		}
	}
	
	/**
	 * Loads and initializes the world adapters, creating action and percept 
	 * descriptors.
	 * @throws MicropsiException
	 */
	public void initializeWorldAdapters() throws MicropsiException {
				
		actionDescriptors.clear();
		perceptDescriptors.clear();
		urgeCreators.clear();
		agentControllers.clear();
		
		List<String> v = config.getConfigurationValues(prefixKey + ".worldadapters");
		for(int i=0;i<v.size();i++) {		
			String adapterclass = v.get(i);			
			try {
				AgentWorldAdapterIF adapter = 
					(AgentWorldAdapterIF) Class.forName(adapterclass).newInstance();
					
				adapter.initialize(theAgent, logger);
				
				agentControllers.add(adapter.createController());
				
				ActionTranslatorIF[] adesc = adapter.createActionTranslators();
				if(adesc != null) {
					for(int j=0;j<adesc.length;j++)
						actionDescriptors.add(adesc[j]);
				}
									
				PerceptTranslatorIF[] pdesc = adapter.createPerceptTranslators();
				if(pdesc != null) {
					for(int j=0;j<pdesc.length;j++)
						perceptDescriptors.put(pdesc[j].getPerceptID(), pdesc[j]);
				}
				
				UrgeCreatorIF[] ucreat = adapter.createUrgeCreators();
				if(ucreat != null) {
					for(int j=0;j<ucreat.length;j++)
						urgeCreators.add(ucreat[j]);
				}				
				
			} catch (Exception e) {
				logger.error("Unable to initialize worldAdapter "+adapterclass+". Reason: "+e.getMessage(),e);				
			}
		} 
	}
	
	/**
	 * Shuts down the agent's world adapters.
	 * The component will be in a clean state after this method has been called
	 * and can be initialized as if it hat just been created.
	 */
	public void shutdownWorldAdapters() {
		for(int i=0;i<actionDescriptors.size();i++)
			actionDescriptors.get(i).shutdown();
	
		Iterator iter = perceptDescriptors.values().iterator();
		while(iter.hasNext())
			((PerceptTranslatorIF)iter.next()).shutdown();

		for(int i=0;i<urgeCreators.size();i++)
			urgeCreators.get(i).shutdown();
	
		for(int i=0;i<agentControllers.size();i++)
			agentControllers.get(i).shutdown();
	
		actionsOnTheWay.clear();
		actionDescriptors.clear();
		perceptDescriptors.clear();
		urgeCreators.clear();
		agentControllers.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.comp.common.ActiveComponentIF#start()
	 */
	public void start() {

		boolean startit = true;

		try {
			ProtocolVersionChecker.checkServerVersion(
				"agentserverclient",
				clients,
				getComponentID());
		} catch (MicropsiException e) {
			exproc.handleException(e);
			logger.fatal("Didn't start the agent: " + getComponentID());
			startit = false;
		}

		lastrequest.setAgentID(getComponentID());
		lastrequest.setAgentType(agentType);
		lastrequest.setRequestType(MAgentReq.AGENTREQ_REGISTER);
		lastrequest.setAction(null);
		ComChannelRequest req = new ComChannelRequest("agent", lastrequest, getComponentID());
		try {
			ComChannelResponse resp = clients.performRequest("agentserverclient", req);
			MAgentResp ares = (MAgentResp) resp.getResponseData();
			overrideComponentID(ares.getControltext(),true);
			logger.info("Agent registered. New componentID: "+ares.getControltext());
		} catch (MicropsiException e) {
			exproc.handleException(e);
			logger.fatal("Didn't start the agent: " + getComponentID());
			startit = false;
		}

		if (startit) {
			toucher = new TouchCreator("agentserverclient",this,null,null);
			toucher.start();

			if (theControllerThread == null) {
				theControllerThread = new Thread(this, getComponentID()+"-control");
				theAgentThread = new Thread(theAgent, getComponentID() + "-impl");
				theAgentThread.start();
				theControllerThread.start();
			}			
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		MAction tmpAction;
		long half = 0;
		try {
			while (theAgent.isAlive()) {
				theAgent.startCycle(cyclecounter);
				half = (cyclelength / 2) - delta;
				if (half < 0)
					half = 0;
				Thread.sleep(half);
				theAgent.receiveCycleSignal(cyclecounter, CycleSignalsIF.CYCLE_SIGNAL_HALFTIME);
				Thread.sleep(cycletenpercent * 4);
				theAgent.receiveCycleSignal(cyclecounter, CycleSignalsIF.CYCLE_SIGNAL_TENPERCENT);
				Thread.sleep(cycletenpercent);
				theAgent.endCycle(cyclecounter);
				
				if(!theAgent.isAlive()) {
					break;
				}
				
				tmpAction = getDesiredAction();
				long tmp = System.currentTimeMillis();
				
				if(tmpAction == null) { 
					tmpAction = dummyAction;
				}
				
				tmpAction.setAgentName(getComponentID());
				
				synchronized(shutdownLock) {
					sendAgentRequest(tmpAction);
				}
				
				delta = System.currentTimeMillis() - tmp;
			}
		} catch (InterruptedException ie) {
		} catch (Exception e) {
			logger.fatal("Fatal agent error (agent shutting down).");
			exproc.handleException(e);			
		}
		
		try {
			try {
				runner.destroyComponent(getComponentID());
			} catch (ComponentRunnerException exc) {
				exproc.handleException(exc);
			}
	
			logger.info("ActiveComponent (agent) "+getComponentID()+" terminated.");
		} catch (Throwable e) {
			// deliberately empty
		}
	}

	/**
	 * Finds out the currently desired action by looking at the priorities of
	 * all actionDescriptors
	 * @return the newly calculated action from the winning action descriptor
	 */	
	private synchronized MAction getDesiredAction() {
		double highest = 0;
		int sel = -1;
						
		for(int i=0;i<actionDescriptors.size();i++) {
			ActionTranslatorIF desc = actionDescriptors.get(i);
			double val = desc.getCurrentActionPriority();
			if(val > highest) {
				highest = val;
				sel = i;
			}
		}

		for(int i=0;i<actionDescriptors.size();i++) {
			ActionTranslatorIF desc = actionDescriptors.get(i);
			if(i != sel) {
				desc.dontCalculateAction();
			}
		}

		if(sel < 0) {
			return null;
		} 
		
		ActionTranslatorIF translator = actionDescriptors.get(sel); 
		MAction action = translator.calculateAction(); 
		if(!action.hasTicket()) {
			action.setTicket(getNextTicket());
		}
		
		actionsOnTheWay.put(Long.toString(action.getTicket()), translator);
		
		return action;
	}

	/**
	 * Actually sends the action to the world. Agent ID, agent type and request type
	 * will be set  by this method, overwriting anything you might have put there
	 * before. This method will also ask the agent controllers if any of them wants
	 * perception. If so, this method will also send a perception request to the world.
	 * @param action the action to be sent. 
	 */
	private synchronized void sendAgentRequest(MAction action) {
		
		if(action == null) return;
		if(!theAgent.isAlive()) return;
	
		lastrequest.setAgentID(getComponentID());		
		lastrequest.setAgentType(agentType);
		lastrequest.setRequestType(MAgentReq.AGENTREQ_NORMALOP);
			
		lastrequest.setAction(action);
	
		try {

			for(int i=0;i<agentControllers.size();i++) {
				AgentControllerIF controller = agentControllers.get(i);
				controller.notifyOfAction();
			}
			
			ComChannelRequest req = new ComChannelRequest("agent", lastrequest, getComponentID());
			ComChannelResponse resp = clients.performRequest("agentserverclient", req);
			toucher.reportTouch();
			MAgentResp ares = (MAgentResp) resp.getResponseData();
			cyclecounter = ares.getTime()+5;
			
			if(ares.getResponseType() == MAgentResp.AGENTRESP_ERROR) {
				if(!ares.getControltext().equals("ALREADYSENT"))
					logger.warn("agent request failed: "+ares.getControltext());	
			} else {
				MActionResponse r = ares.getPreviousActionResponse();
				String actionWas = "unknown";
				
				if(r != null) {
					String ticket = Long.toString(r.getTicket());
					if(!actionsOnTheWay.containsKey(ticket)) {
						logger.warn("Received action response with invalid ticket "+ticket+", action success will be lost");
					} else {
						ActionTranslatorIF tr = actionsOnTheWay.get(ticket);
						actionWas = tr.getActionID();
						tr.receiveActionResult(r.getSuccess());
						actionsOnTheWay.remove(ticket);
										
						ArrayList changes = r.getBodyPropertyChanges();
				
						for(int i=0;i<agentControllers.size();i++) {
							AgentControllerIF controller = agentControllers.get(i);						
							controller.notifyOfActionResult(actionWas,r.getSuccess());
						
							if(!changes.isEmpty()) {
								controller.receiveBodyPropertyChanges(changes);
							}
						}
						
						for(int i=0;i<urgeCreators.size();i++) {
							UrgeCreatorIF u = urgeCreators.get(i);
							u.notifyOfBodyPropertyChanges();
						}

					}
					
				}
			}
			
			lastrequest.clearLists();
			
			try {
				List<AnswerIF> answers = consoleService.answerStoredQuestions(null, cyclecounter);
				answers.addAll(consoleService.answerQuestions(ares.getQuestions(), cyclecounter));
				for (int i = 0; i < answers.size(); i++)
					lastrequest.addAnswer((MAnswer) answers.get(i));
			} catch (Exception e) {
				// couldn't answer questions, probably some bad question
				// implementation
				exproc.handleException(e);
			}
		} catch (MicropsiException e) {
			deadcounter++;
				
			if(deadcounter == MAX_DEAD_CYCLES) {
				logger.error("Server not reachable after "+deadcounter+" attempts. Giving up.");
				deadcounter = 0;
				try {
					theAgent.stopEverything();
				} catch (MicropsiException exc) {
					exproc.handleException(exc);
				}
				exproc.handleException(e);				
			} 
		}
		
		boolean wantsPerception = false;
		for(int i=0;i<agentControllers.size();i++) {
			if(agentControllers.get(i).wantsPerception()) {
				wantsPerception  = true;
				break;
			}
		}
		
		if(wantsPerception) {

			for(int i=0;i<agentControllers.size();i++)
				agentControllers.get(i).notifyOfPerception();

			ComChannelRequest req = new ComChannelRequest("perception", null, getComponentID());
			try {
				ComChannelResponse resp = clients.performRequest("agentserverclient", req);
				toucher.reportTouch();
				MPerceptionResp perceptresp = (MPerceptionResp) resp.getResponseData();
				
				//perceptresp can be null under certain unclear circumstances
				List<MPercept> percepts;
				if (perceptresp != null) {
					percepts = perceptresp.getPercepts();
				} else {
					percepts = new ArrayList<MPercept>();
				}
				for(int i=0;i<percepts.size();i++) {
					MPercept p = percepts.get(i);		
					PerceptTranslatorIF desc = perceptDescriptors.get(p.getName());
					if(desc != null) {
						desc.receivePercept(p);
					} else {
						logger.warn("Agent received percept "+p.getName()+" but had no translator for it.");
					}
				}
				for(int i=0;i<urgeCreators.size();i++)
					urgeCreators.get(i).notifyOfPerception();
				
			} catch (MicropsiException e) {
				deadcounter++;
				
				if(deadcounter == MAX_DEAD_CYCLES) {
					logger.error("Server not reachable after "+deadcounter+" attempts. Giving up.");
					deadcounter = 0;
					try {
						theAgent.stopEverything();
					} catch (MicropsiException exc) {
						exproc.handleException(exc);
					}
					exproc.handleException(e);				
				} 
			} catch (Exception e) {
				exproc.handleException(e);
			}
		}		
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.comp.common.AbstractComponent#getInnerType()
	 */
	public int getInnerType() {
		return theAgent.getAgentType();
	}
	
	/**
	 * Returns the Agent thread
	 * @return
	 */
	public Thread getAgentThread() {
		return theAgentThread;
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.comp.common.AbstractComponent#shutdown()
	 */
	public void shutdown() {		
		try {
			
			synchronized(shutdownLock) {
				
				toucher.shutdown();
				
				theAgent.stopEverything();
				
				if(theAgentThread.isAlive())
					theAgent.stopEverything();
		
				lastrequest.setAgentID(getComponentID());
				lastrequest.setAction(null);
				lastrequest.setRequestType(MAgentReq.AGENTREQ_UNREGISTER);
				ComChannelRequest req = new ComChannelRequest("agent", lastrequest, getComponentID());
				clients.performRequest("agentserverclient", req);
				
			}
			
		} catch (MicropsiException exc) {
			logger.error("Shutdown exception: ",exc);
		}
		
	}

}
