/*
 * Created on May 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.micropsi.comp.agent.kheperaTurtle5;

import org.apache.log4j.Logger;
import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.messages.MAction;
import org.micropsi.nodenet.LocalNetFacade;

/**
 * @author Administrator
 *
 * tells the robot to stop all movements
 */
public class KheperaStopActionTL implements ActionTranslatorIF {
	
	private static final String TYPE = "STOP";
	private MAction stopAction = new MAction(TYPE,"");
	private Logger logger;
	private boolean debug;
	private LocalNetFacade net;
	
	
	public KheperaStopActionTL(LocalNetFacade net, Logger logger, boolean debug){
		this.net = net;
		this.logger = logger;
		this.debug = debug;
		logger.debug("constructor KheperaStopActionTL...");
	}
	
	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.ActionTranslatorIF#getActionID()
	 */
	public String getActionID() {
		return TYPE;
	}
	
	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.ActionTranslatorIF#getCurrentActionPriority()
	 */
	public double getCurrentActionPriority() {
		if(net.getCycle().isSuspended()){
			return 1000; // exclusive winning (highest CurrentActionPriority)
		}
		if(debug) logger.debug("motorPriority 1000");
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.ActionTranslatorIF#calculateAction()
	 */
	public MAction calculateAction() {
		//logger.debug("KheperaStopAction.calculateAction()");
		
		// -1 means that the framworkComponent will set ticket, which is
		// fine for us.
		stopAction.clearParameters();
		stopAction.setTicket(-1);
		
		return stopAction;
	}
	
	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.ActionTranslatorIF#dontCalculateAction()
	 */
	public void dontCalculateAction() {
		// TODO Auto-generated method stub
	}
	
	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.ActionTranslatorIF#receiveActionResult(double)
	 */
	public void receiveActionResult(double value) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.ActionTranslatorIF#shutdown()
	 */
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
	
}
