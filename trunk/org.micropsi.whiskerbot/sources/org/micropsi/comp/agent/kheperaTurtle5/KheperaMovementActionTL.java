package org.micropsi.comp.agent.kheperaTurtle5;

import org.apache.log4j.Logger;
import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.messages.MAction;
import org.micropsi.nodenet.ActorDataTargetIF;
import org.micropsi.nodenet.CycleObserverIF;
import org.micropsi.nodenet.LocalNetFacade;

public class KheperaMovementActionTL implements ActionTranslatorIF {

	private class WheelDataTarget implements ActorDataTargetIF, CycleObserverIF {
		
		private String TYPE;
		private int signal = 0;
		private long step;
		private int h;
		private Logger logger;
		private boolean debug;
		
		public WheelDataTarget(String type, Logger logger, boolean debug) {
			TYPE = type;
			this.logger = logger;
			this.debug = debug;
		}
		
		public String getDataType() {
			return TYPE;
		}
		
		public void addSignalStrength(double value) {
			/* Whenever an ActorNode that is connected to the implementation of this
			 * ActorDataTargetIF fires, it's outgoing activation will be passed to this method.
			 * 
			 * Leo: the ActorNodes output range is [-1,1] with 0 equals stop, -1 equals maximum 
			 * speed backwards. this range is scaled to [-5,5] for the khepera motors.
			 */
			
			signal = (int)(value);
      
//			h = Double.compare(value, 0);
//			if (h > 0)
//				signal = (int)Math.ceil(value);
//			else if (h < 0)
//				signal = (int)Math.floor(value);
//			else if (h == 0)
//				signal = 0;
      if(debug) logger.debug("MOVE) signal = "+signal);
			
			
			//signal = Math.round(value * 5.0);// * 5.0;
			//signal = (Math.round(value * 5.0) - 2.0) * 2.0; //motor strength minus 2 -> shift
		
		}

		public double getExecutionSuccess() {
			return 1;
		}

		public void startCycle(long netStep) {
			step = netStep;
		}

		public void endCycle(long netStep) {
			//signal = 0;     //Leo: command caused arbitrary stops! -> commented out
		}
		
		
		public int getSignal() { //public double getSignal() {
			return signal;
		}
		
		public long getLastStep() {
			return step;
		}
	}
	
	private static final String TYPE = "MOVE";
	
	private WheelDataTarget left;
	private WheelDataTarget right;
	private long lastStepWithAction;
	private int signalLeftHelper  = 1000;
	private int signalRightHelper = 1000;
	private double motorPriorityHelper = 0;
	private Logger logger; 
	private boolean debug;
	//private int signal_left, signal_right;
	
	private MAction movementAction = new MAction(TYPE,"");
	
	public KheperaMovementActionTL(LocalNetFacade net, Logger logger, boolean debug) {
		this.logger = logger;
		this.debug = debug;
		logger.debug("constructor KheperaMovementActionTL...");
		
		//names are shown in the actor node menue:
		left = new WheelDataTarget("left",logger,debug);
		right = new WheelDataTarget("right",logger,debug);
		net.getCycle().registerCycleObserver(left);
		net.getCycle().registerCycleObserver(right);
		net.getSensorRegistry().registerActuatorDataTarget(left);
		net.getSensorRegistry().registerActuatorDataTarget(right);
	}
	
	public String getActionID() {
		return TYPE;
	}
	
	public double getCurrentActionPriority() {
		///leo: if motor signal has not changed -> low priority: (problem: low priority after stop action!)
//		if( (left.getSignal() == signalLeftHelper) && (right.getSignal() == signalRightHelper) ){
//		motorPriorityHelper = 0.1;
//		return 0.1;
//		}
		
		
		
		/*if((right.getLastStep() == lastStepWithAction) || (left.getLastStep() == lastStepWithAction)) {
		 return 0;
		 }
		 */
		signalLeftHelper  = left.getSignal();
		signalRightHelper = right.getSignal();
		if(debug) logger.debug("motorPriority 1");
		motorPriorityHelper = 1;
		return 2;
	}
	
	public MAction calculateAction() {
		
		
		MAction movementAction = new MAction(TYPE,"");
		
//		movementAction.clearParameters(); //leo: is this command usefull?
		
		// -1 means that the framworkComponent will set ticket, which is
		// fine for us.
		movementAction.setTicket(-1);
	
		movementAction.addParameter(""+left.getSignal());
		movementAction.addParameter(""+right.getSignal());
		
//		logger.debug("motor:  "+left.getSignal()+" | "+right.getSignal());
//		
//		if (motorPriorityHelper != 1)
//		logger.error("false motorPriority!!!");
//		
//		logger.debug("movementAction:  "+movementAction.getParameter(0)+" | "+movementAction.getParameter(1));
		if(debug) logger.debug("return movementAction");
		return movementAction;
	}
	
	public void dontCalculateAction() {
	}
	
	public void receiveActionResult(double value) {
	}
	
	public void shutdown() {
	}

}
