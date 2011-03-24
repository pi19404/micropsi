package org.micropsi.comp.agent.omni;

import org.apache.log4j.Logger;
import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.messages.MAction;
import org.micropsi.nodenet.ActorDataTargetIF;
import org.micropsi.nodenet.CycleObserverIF;
import org.micropsi.nodenet.LocalNetFacade;

public class MovementAction implements ActionTranslatorIF {

	private class WheelDataTarget implements ActorDataTargetIF, CycleObserverIF {

		private String TYPE;
		private double signal = 0;
		private long step;
		
		public WheelDataTarget(String type) {
			TYPE = type;
		}
		
		public String getDataType() {
			return TYPE;
		}

		public void addSignalStrength(double value) {
			signal += value;
		}

		public double getExecutionSuccess() {
			return 1;
		}

		public void startCycle(long netStep) {
			step = netStep;
		}

		public void endCycle(long netStep) {
			signal = 0;
		}
		
		public double getSignal() {
			return signal;
		}
		
		public long getLastStep() {
			return step;
		}
	}
	
	private static final String TYPE = "SETSPEED";

	private WheelDataTarget left;
	private WheelDataTarget right;
	private WheelDataTarget back;
	private long lastStepWithAction;
	
	private MAction movementAction = new MAction(TYPE,"");
	
	public MovementAction(LocalNetFacade net, Logger logger) {
		left = new WheelDataTarget("left");
		right = new WheelDataTarget("right");
		back = new WheelDataTarget("back");
		net.getCycle().registerCycleObserver(left);
		net.getCycle().registerCycleObserver(right);
		net.getCycle().registerCycleObserver(back);
		net.getSensorRegistry().registerActuatorDataTarget(left);
		net.getSensorRegistry().registerActuatorDataTarget(right);
		net.getSensorRegistry().registerActuatorDataTarget(back);
	}
	
	public String getActionID() {
		return TYPE;
	}

	public double getCurrentActionPriority() {
		if(right.getLastStep() == lastStepWithAction) {
			return 0;
		}
		
		return 1;
	}

	public MAction calculateAction() {
		lastStepWithAction = right.getLastStep();
		movementAction.clearParameters();
		
		// -1 means that the framworkComponent will set ticket, which is
		// fine for us.
		movementAction.setTicket(-1);
		movementAction.addParameter(""+left.getSignal());
		movementAction.addParameter(""+right.getSignal());
		movementAction.addParameter(""+back.getSignal());
		return movementAction;
	}

	public void dontCalculateAction() {
	}

	public void receiveActionResult(double value) {
	}

	public void shutdown() {
	}

}
