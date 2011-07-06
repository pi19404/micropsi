/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/actions/MoveAction.java,v 1.4 2005/01/15 17:44:29 vuine Exp $ 
 */
package org.micropsi.comp.agent.micropsi.actions;

import org.apache.log4j.Logger;

import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.messages.MAction;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetFacadeIF;


public class MoveAction implements ActionTranslatorIF {

	private static final String TYPE = "MOVE";

	private MAction sentAction = null;
	
	private ActionDataTarget move_north;
	private ActionDataTarget move_south;
	private ActionDataTarget move_west;
	private ActionDataTarget move_east;
	private MAction northMoveAction;
	private MAction southMoveAction;
	private MAction westMoveAction;
	private MAction eastMoveAction;
	private Logger logger;
	
	public MoveAction(NetFacadeIF net, Logger logger) {
		
		this.logger = logger;
		
		move_north = new ActionDataTarget("move-north");
		((LocalNetFacade)net).getSensorRegistry().registerActuatorDataTarget(move_north);
		((LocalNetFacade)net).getCycle().registerCycleObserver(move_north);
		northMoveAction = new MAction(TYPE,"");
		northMoveAction.addParameter("0");
		northMoveAction.addParameter("10");
		northMoveAction.addParameter("0");
		//logger.debug("northMoveAction is "+northMoveAction);

		move_south = new ActionDataTarget("move-south");
		((LocalNetFacade)net).getSensorRegistry().registerActuatorDataTarget(move_south);
		((LocalNetFacade)net).getCycle().registerCycleObserver(move_south);
		southMoveAction = new MAction(TYPE,"");
		southMoveAction.addParameter("0");
		southMoveAction.addParameter("-10");
		southMoveAction.addParameter("0");
		//logger.debug("southMoveAction is "+southMoveAction);

		move_west = new ActionDataTarget("move-west");
		((LocalNetFacade)net).getSensorRegistry().registerActuatorDataTarget(move_west);
		((LocalNetFacade)net).getCycle().registerCycleObserver(move_west);
		westMoveAction = new MAction(TYPE,"");
		westMoveAction.addParameter("-10");
		westMoveAction.addParameter("0");
		westMoveAction.addParameter("0");
		//logger.debug("westMoveAction is "+westMoveAction);		

		move_east = new ActionDataTarget("move-east");
		((LocalNetFacade)net).getSensorRegistry().registerActuatorDataTarget(move_east);
		((LocalNetFacade)net).getCycle().registerCycleObserver(move_east);
		eastMoveAction = new MAction(TYPE,"");
		eastMoveAction.addParameter("10");
		eastMoveAction.addParameter("0");
		eastMoveAction.addParameter("0");	
		//logger.debug("eastMoveAction is "+eastMoveAction);	
		
	}

	public String getActionID() {
		return TYPE;
	}

	public MAction calculateAction() {
		
		double highest = 0;		
				
		if(move_north.getSignalStrength() > highest) {
			highest = move_north.getSignalStrength();
			sentAction = northMoveAction;
		}
		if(move_south.getSignalStrength() > highest) {
			highest = move_south.getSignalStrength();
			sentAction = southMoveAction;
		}
		if(move_west.getSignalStrength() > highest) {
			highest = move_west.getSignalStrength();
			sentAction = westMoveAction;
		}		
		if(move_east.getSignalStrength() > highest) {
			highest = move_east.getSignalStrength();
			sentAction = eastMoveAction;
		}
				
		move_north.quiet();
		move_south.quiet();
		move_east.quiet();
		move_west.quiet();
		move_north.confirmActionExecution();
		move_south.confirmActionExecution();
		move_east.confirmActionExecution();
		move_west.confirmActionExecution();
		
		return sentAction;
	}

	public void receiveActionResult(double value) {
		//logger.info("move success: "+value+" to "+sentAction);
		if(sentAction == northMoveAction) {
			move_north.setSuccess(value);
		} else if(sentAction == southMoveAction) {
			move_south.setSuccess(value);
		} else if(sentAction == westMoveAction) {
			move_west.setSuccess(value);
		} else if(sentAction == eastMoveAction) {
			move_east.setSuccess(value);
		} else {
			logger.error("Move success signal could not be sent, sentAction was: "+sentAction);
		}
	}

	public double getCurrentActionPriority() {
		double highest = 0;	
				
		double tmp = move_north.getSignalStrength(); 
		if(tmp > highest)
			highest = tmp;
			
		tmp = move_south.getSignalStrength(); 
		if(tmp > highest)
			highest = tmp;

		tmp = move_west.getSignalStrength(); 
		if(tmp > highest)
			highest = tmp;

		tmp = move_east.getSignalStrength(); 
		if(tmp > highest)
			highest = tmp;

		return highest;
	}

	public void dontCalculateAction() {
		move_north.quiet();
		move_south.quiet();
		move_east.quiet();
		move_west.quiet();
	}

	public void shutdown() {
	}

}
