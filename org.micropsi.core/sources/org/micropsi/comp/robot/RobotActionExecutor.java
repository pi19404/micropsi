/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/robot/RobotActionExecutor.java,v 1.7 2005/05/12 13:31:12 vuine Exp $ 
 */
package org.micropsi.comp.robot;

import org.apache.log4j.Logger;
import org.micropsi.common.config.ConfigurationReaderIF;
import org.micropsi.common.config.MicropsiConfigException;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.messages.MAction;
import org.micropsi.comp.messages.MActionResponse;


public abstract class RobotActionExecutor {

	private static RobotActionExecutor instance;
	
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
			instance = (RobotActionExecutor)o;
			instance.tecLayer = tecLayer;
			instance.config = config;
			instance.prefix = prefix;
		} catch (ClassCastException e) {
			throw new MicropsiException(18,"RobotActionExecutor",e);
		}
		
	}
	
	protected final static RobotActionExecutor getInstance() throws MicropsiException {
		if(instance == null) throw new MicropsiException(19,"RobotActionExecutor");
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
	 * Implementations should execute the desired action and return 
	 * immediate results here. "Immediate results" doesn't have to mean all to
	 * much with robots, as probably no results will be known at that time. And as
	 * this method should return <i>immediately</i>, the immediate results will,
	 * in robot environments, probably be simply something like "Ok I can use that
	 * actuator at that point". Again there's the distribution issue, see 
	 * @see RobotPerceptionExtractor for a discussion of 
	 * possible problems with distribution. <br><br>
	 * Once more, the constraints:
	 * <ol>
	 * <li> Return immediately, do not wait until the action has been executed fully
	 * <li> This method is called exactly once per Micropsi system cycle
	 * <li> MActionResponse doesn't need to contain anything, if there is nothing
	 * to report at that point of time. 
	 * </ol>
	 * @param action the Action to be executed.
	 * @return an MActionResponse with immediate results which must not be null
	 * and MUST have the agent name set. (get the Agent name via getConnectedAgent()).
	 */
	public abstract MActionResponse executeAction(MAction action);
	
	/**
	 * Implementations should return an array of contributions to the console Q/A system.
	 * If you want to have any communication with consoles, this is the place to implement
	 * it. The question types you return here will be registered with the RobotWorldComponent
	 * and then be available for the system's consoles.<br/>
	 * There is another getConsoleQuestionContributions() method in the perception extractor.
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
