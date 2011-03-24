/*
 * Created on 24.03.2005
 *
 */
package org.micropsi.comp.robot.minipsi;

import org.apache.log4j.Logger;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.comp.agent.minipsi.MovementAction;
import org.micropsi.comp.messages.MAction;
import org.micropsi.comp.messages.MActionResponse;
import org.micropsi.comp.robot.RobotActionExecutor;

public class MinipsiActionExecutor extends RobotActionExecutor {

	private boolean isInitialized = false;
	private boolean isDead = false;
	private Logger logger = null;
	private MacMiniBot bot = null;
	
	private synchronized void initialize() {
		if(isInitialized) return;
		
		logger = getLogger();
		try {
			bot = new MacMiniBot("/dev/tty.usbserial-FTCCBANG");
		} catch (Exception e) {
			logger.error("Could not set up communication with the robot",e);
			isDead = true;
		}
		
		isInitialized = true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.micropsi.comp.robot.RobotActionExecutor#executeAction(org.micropsi.comp.messages.MAction)
	 */
	public MActionResponse executeAction(MAction action) {
		if(!isInitialized) {
			initialize();
		}
		MActionResponse response = new MActionResponse(action.getAgentName(),1.0,action.getTicket()); 
		if(isDead) return response;
		
		String actionName = action.getAgentName();
		if(MovementAction.TYPE.equals(actionName)) {
			bot.setSpeed(Integer.parseInt(action.getParameter(0)),Integer.parseInt(action.getParameter(1)),Integer.parseInt(action.getParameter(2)));
		} else {
			logger.warn("Action "+actionName+" not recognized by MinipsiActionExecutor");
		}
		
		return response;
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.comp.robot.RobotActionExecutor#getConsoleQuestionContributions()
	 */
	public ConsoleQuestionTypeIF[] getConsoleQuestionContributions() {
		return new ConsoleQuestionTypeIF[0];
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.comp.robot.RobotActionExecutor#tick(long)
	 */
	public void tick(long simStep) {
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.comp.robot.RobotActionExecutor#shutdown()
	 */
	public void shutdown() {
		if(bot != null) {
			bot.shutdown();
			bot = null;
		}
	}  
}
