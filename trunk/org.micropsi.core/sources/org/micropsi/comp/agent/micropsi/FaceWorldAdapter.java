package org.micropsi.comp.agent.micropsi;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.agent.AgentIF;
import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.agent.aaa.AgentControllerIF;
import org.micropsi.comp.agent.aaa.AgentWorldAdapterIF;
import org.micropsi.comp.agent.aaa.PerceptTranslatorIF;
import org.micropsi.comp.agent.aaa.UrgeCreatorIF;
import org.micropsi.comp.agent.micropsi.actions.MuscleParameterDataTarget;
import org.micropsi.nodenet.LocalNetFacade;

public class FaceWorldAdapter implements AgentWorldAdapterIF, AgentControllerIF {

	private MicroPsiAgent micropsi;
//	private Logger logger;
	
	public void initialize(AgentIF agent, Logger logger) throws MicropsiException {
		
		logger.debug("Initializing face WorldAdapter");
		
//		this.logger = logger;
		micropsi = (MicroPsiAgent)agent;
		
		ActorValueCache actorValueCache = micropsi.getActorValueCache();
		LocalNetFacade net = (LocalNetFacade) micropsi.getNet();
		
		// actuators for the emotional parameters
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_neck_upper_nod",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_neck_upper_rotation",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_jaw_intermediate",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_jaw",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_head_nod",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_head_rotation",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_upper_lip",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_lower_lip",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_tongue",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_eye_left_h",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_eye_left_v",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_eye_right_h",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_eye_right_v",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_eyelid_left_upper",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_eyelid_left_lower",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_eyebrow_left_outer",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_eyebrow_left_inner",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_eyebrow_left_center",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_eyelid_right_upper",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_eyelid_right_lower",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_eyebrow_right_outer",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_eyebrow_right_inner",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_eyebrow_right_center",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_mouth_width_left",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_mouth_width_right",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_mouth_left_upper",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_mouth_right_upper",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_mouth_corner_left",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_mouth_corner_right",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_nose_wing_left",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_nose_wing_right",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_cheek_left",actorValueCache));
		net.getSensorRegistry().registerActuatorDataTarget(new MuscleParameterDataTarget("face_cheek_right",actorValueCache));
		
	}

	public AgentControllerIF createController() {
		return this;
	}

	public ActionTranslatorIF[] createActionTranslators() {
		return new ActionTranslatorIF[0];
	}

	public PerceptTranslatorIF[] createPerceptTranslators() {
		return new PerceptTranslatorIF[0];
	}

	public UrgeCreatorIF[] createUrgeCreators() {
		return new UrgeCreatorIF[0];
	}

	public boolean wantsPerception() {
		return false;
	}

	public void receiveBodyPropertyChanges(ArrayList parameterChanges) {
	}

	public void notifyOfPerception() {
	}

	public void notifyOfAction() {
	}

	public void notifyOfActionResult(String actionName, double result) {
	}

	public void shutdown() {
	}

}
