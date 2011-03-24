package org.micropsi.comp.agent.kheperaTurtle5;

import org.apache.log4j.Logger;
import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.messages.MAction;
import org.micropsi.nodenet.ActorDataTargetIF;
import org.micropsi.nodenet.CycleObserverIF;
import org.micropsi.nodenet.LocalNetFacade;

public class KheperaLEDActionTL implements ActionTranslatorIF {
	
	private class LEDDataTarget implements ActorDataTargetIF, CycleObserverIF {
		
		private String TYPE;
		private double signal = 0;
		private long step;
		private Logger logger;
		private boolean debug; 
		
		public LEDDataTarget(String type) {
			TYPE = type;
		}
		
		public LEDDataTarget(String type, Logger logger, boolean debug) {
			TYPE = type;
			this.logger = logger;
			this. debug = debug;
		}
		
		public String getDataType() {
			return TYPE;
		}
		
		public void addSignalStrength(double value) {
			/* Whenever an ActorNode that is connected to the implementation of this
			 * ActorDataTargetIF fires, it's outgoing activation will be passed to this method.
			 * 
			 * Leo: the ActorNodes output range is [-1,1] with [-1,0] equals LED off 
			 * and (0,1] equals LED on
			 */
			//if (Math.ceil(value) <= 4.0) 
			signal = 0.0;
			if(debug) logger.debug("(LED) signal = "+signal);
			//else signal = 1.0;
			
		}
		
		public double getExecutionSuccess() {
			return 1;
		}
		
		public void startCycle(long netStep) {
			step = netStep;
		}
		
		public void endCycle(long netStep) {
			//signal = 0;
		}
		
		public double getSignal() {
			/* called by KheperaLEDActionTL.calculateAction()
			 */
			return signal;
		}
		
		public long getLastStep() {
			return step;
		}
	}
	
	private static final String TYPE = "LED";
	
	private LEDDataTarget led0;
	private LEDDataTarget led1;
	private long lastStepWithActionLED0;
	private long lastStepWithActionLED1;
	private Logger logger; 
	private boolean debug;
	
	private MAction LEDAction = new MAction(TYPE,"");
	
	public KheperaLEDActionTL(LocalNetFacade net, Logger logger, boolean debug) {
		this.logger = logger;
		this.debug = debug;
		logger.debug("constructor KheperaLEDActionTL...");
		
		//namen werden im actor node menue angezeigt:
		led0 = new LEDDataTarget("LED0", logger, debug);
		led1 = new LEDDataTarget("LED1", logger, debug);
		net.getCycle().registerCycleObserver(led0);
		net.getCycle().registerCycleObserver(led1);
		net.getSensorRegistry().registerActuatorDataTarget(led0);
		net.getSensorRegistry().registerActuatorDataTarget(led1);
	}
	
	public String getActionID() {
		return TYPE;
	}
	
	public double getCurrentActionPriority() {
		/*// Leiche???
		 if((led0.getLastStep() == lastStepWithActionLED0) 
		 || (led1.getLastStep() == lastStepWithActionLED1)) {
		 return 0;
		 }
		 
		 return 1;
		 */
		if(debug) logger.debug("motorPriority 0.5");
		//leo: LED wins if there are no new motor signals:
		return 0.5;
	}
	
	public MAction calculateAction() {
		//logger.debug("KheperaLEDAction.calculateAction: led0.getLastStep() "+led0.getLastStep()+"" +
		//		" | led1.getLastStep() "+led1.getLastStep() );
		lastStepWithActionLED0 = led0.getLastStep();
		lastStepWithActionLED1 = led1.getLastStep();
		
		LEDAction.clearParameters();
		
		// -1 means that the framworkComponent will set ticket, which is
		// fine for us.
		LEDAction.setTicket(-1);
		
		LEDAction.addParameter(""+(int)led0.getSignal());
		LEDAction.addParameter(""+(int)led1.getSignal());
		
		if(debug) logger.debug("return LEDAction");
		return LEDAction;
	}
	
	public void dontCalculateAction() {
	}
	
	public void receiveActionResult(double value) {
	}
	
	public void shutdown() {
	}
	
}
