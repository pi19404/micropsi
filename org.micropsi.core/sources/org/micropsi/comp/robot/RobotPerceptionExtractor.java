/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/robot/RobotPerceptionExtractor.java,v 1.6 2005/05/12 13:31:12 vuine Exp $ 
 */
package org.micropsi.comp.robot;

import org.apache.log4j.Logger;
import org.micropsi.common.config.ConfigurationReaderIF;
import org.micropsi.common.config.MicropsiConfigException;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.messages.MPerceptionResp;


public abstract class RobotPerceptionExtractor {

	private static RobotPerceptionExtractor instance;
	
	protected final static void setup(
			String classname,
			ConfigurationReaderIF config,
			String prefix,
			RobotWorldComponent tecLayer) throws MicropsiException {
		
		Object o;
		try {
			o = Class.forName(classname).newInstance();
		} catch (Exception e) {
			throw new MicropsiException(10,e);
		}
		
		try {
			instance = (RobotPerceptionExtractor)o;
			instance.tecLayer = tecLayer;
			instance.config = config;
			instance.prefix = prefix;
		} catch (ClassCastException e) {
			throw new MicropsiException(18,"RobotActionExecutor",e);
		}
		
	}
	
	protected final static RobotPerceptionExtractor getInstance() throws MicropsiException {
		if(instance == null) throw new MicropsiException(19,"RobotPerceptionExtractor");
		return instance;
	}
	
	private RobotWorldComponent tecLayer;
	private ConfigurationReaderIF config;
	private String prefix;
	
	/**
	 * Returns the logger for this component.
	 * @return a Logger
	 */
	protected Logger getLogger() {
		return tecLayer.getLogger();
	}
	
	/**
	 * Returns a String entry from the configuration.
	 * @param entry the entry's name in the configuration
	 * @return the entry
	 * @throws MicropsiConfigException if there is no such entry
	 */
	protected String getConfigEntry(String entry) throws MicropsiConfigException {
		return config.getConfigValue(prefix+"."+entry);
	}
	
	/**
	 * Returns the name of the agent currently controlling the robot.
	 * @return the name of the agent.
	 */
	protected String getConnectedAgent() {
		return tecLayer.getConnectedAgent();
	}

	/**
	 * Implementations should return perception data here. The method is called 
	 * when the agent implementation requests perception. There are no conventions
	 * on when this will happen or how often - perception can be requested more than
	 * once per cycle! <br><br>
	 * The data returned by this method will be routed via the server to the agent.
	 * If either the server or the agent (or both of them) are in a different VM than
	 * the implementation of this class, there must be encoding for whatever the
	 * implementation puts into the MPerceptionResp object. As creating that encoding
	 * is not trivial, it is strongly recommended to have Micropsi systems with robot
	 * world components NOT distributed. But of course there will be setups that
	 * require distribution, and there will be better encoding strategies some time.
	 * If you plan to have more than one VM at some time in the future, make sure
	 * to return proper message data structures (inside MPerceptionResp), that is:
	 * Don't simply pass references, avoid references to things that are not to be
	 * transmitted (or mark it transient), keep it simple, keep if flat, keep it 
	 * small. If you don't see any need to distribute the components over more than
	 * one VM - don't care about all this. Simply pass whatever you want to. 
	 * @return the MPerceptionResp with perception data
	 */	
	public abstract MPerceptionResp extractPerception();
	
	/**
	 * Implementations should return an array of contributions to the console Q/A system.
	 * If you want to have any communication with consoles, this is the place to implement
	 * it. The question types you return here will be registered with the RobotWorldComponent
	 * and then be available for the system's consoles.<br/>
	 * There is another getConsoleQuestionContributions() method in the action executor.
	 * The two methods are treated independently and equally. Implementations decide where
	 * to register which question type.<br/>
	 * If the implementation does not want to provide answers to any console questions,
	 * this method may return null or an empty array safely.
	 * 
	 * @return an array of ConsoleQuestionTypeIF implementations or null
	 */
	public abstract ConsoleQuestionTypeIF[] getConsoleQuestionContributions();

	
	/**
	 * Called periodically right before a new system cycle begins. 
	 * (Before the actions are executed). 
	 * @param simStep
	 */
	public abstract void tick(long simStep);

	/**
	 * Called when the action executor should terminate.
	 * (When the MRS is going down)
	 */
	public abstract void shutdown();
}
