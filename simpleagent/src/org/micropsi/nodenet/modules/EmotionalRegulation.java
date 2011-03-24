/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/simpleagent/src/org/micropsi/nodenet/modules/EmotionalRegulation.java,v 1.1 2004/05/07 21:48:06 vuine Exp $
 */
package org.micropsi.nodenet.modules;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

/**
 * 
 * 
 * 
 *
 */

public class EmotionalRegulation extends AbstractNativeModuleImpl {

	// gates

	public static final int EMO_RESOLUTION 	= 	1000;
	public static final int EMO_THRESHOLD 	= 	1001;
	public static final int EMO_COMPETENCE	= 	1002;
	public static final int EMO_CERTAINTY	=	1003;
	public static final int EMO_ACTIVATION 	=	1004;
	public static final int EMO_OKAYNESS		=	1005;
	
	public static final int URGE_CERTAINTY 	=	1100;
	public static final int URGE_EFFICIENCY	=	1101;
	public static final int URGE_AFFILIATION=	1102;

	// slots

	public static final int EMO_SIG_CERTAINTY 		=	1000;
	public static final int EMO_SIG_EFFICIENCY		= 	1001;
	public static final int EMO_SIG_AFFILIATION		= 	1002;
	
	public static final int URGESIG_FOOD				=	1003;
	public static final int URGESIG_LIQUIDITY		=	1004;
	public static final int URGESIG_INTEGRITY		=	1005;	

	private static final String COMPETENCE 			= "competence";
	private static final String ACTIVATION 			= "activation";
	private static final String RESOLUTION			= "resolution";
	private static final String THRESHOLD			= "threshold";
	private static final String OKAYNESS				= "okayness";
	private static final String CERTAINTY			= "certainty";
	
	
	// local constants
	
	private double MAX_CERTAINTY = 1.5;
	private double MIN_CERTAINTY = 0;

	private double MAX_COMPETENCE = 1.5;
	private double MIN_COMPETENCE = 0;
	
	private double TARGET_CERTAINTY = 1.0;
	private double TARGET_COMPETENCE = 1.0;
	private double TARGET_OKAYNESS = 1.0;
	
	private double OKAYNESS_DECAY = 0.9;
	
	// cognitive urges
	
	private double affiliationUrge = 0;
	private double certaintyUrge = 0;
	private double efficiencyUrge = 0;
	
	// technical data
	
	private boolean firsttime = true;
	
	private Slot certaintySignals;
	private Slot efficiencySignals;
	private Slot affiliationSignals;
	
	private Slot foodUrgeSignal;
	private Slot liquidityUrgeSignal;
	private Slot integrityUrgeSignal;

	private final int[] gateTypes = {
		EMO_ACTIVATION,
		EMO_CERTAINTY,
		EMO_COMPETENCE,
		EMO_OKAYNESS,
		EMO_RESOLUTION,
		EMO_THRESHOLD,
		URGE_AFFILIATION,
		URGE_CERTAINTY,
		URGE_EFFICIENCY
	};
	
	private final int[] slotTypes = {
		EMO_SIG_CERTAINTY,
		EMO_SIG_EFFICIENCY,
		EMO_SIG_AFFILIATION,
		URGESIG_FOOD,
		URGESIG_LIQUIDITY,
		URGESIG_INTEGRITY
	};
	
	protected int[] getGateTypes() {
		return gateTypes;
	}

	protected int[] getSlotTypes() {
		return slotTypes;
	}
	
	private void catchSlots(Slot[] slots) {
		for(int i=0;i<slots.length;i++) {
			switch(slots[i].getType()) { 
				case EMO_SIG_CERTAINTY:
					certaintySignals = slots[i];
					break;
				case EMO_SIG_EFFICIENCY:
					efficiencySignals = slots[i];
					break;
				case EMO_SIG_AFFILIATION:
					affiliationSignals = slots[i];
					break;
				case URGESIG_FOOD:
					foodUrgeSignal = slots[i];
					break;
				case URGESIG_LIQUIDITY:
					liquidityUrgeSignal = slots[i];
					break;
				case URGESIG_INTEGRITY:
					integrityUrgeSignal = slots[i];
					break;			
			}
		}
	}
	
	private void updateGates(GateManipulator manipulator) {
		manipulator.setGateActivation(EMO_ACTIVATION,innerstate.getStateDouble(ACTIVATION));
		manipulator.setGateActivation(EMO_CERTAINTY,innerstate.getStateDouble(CERTAINTY)); 
		manipulator.setGateActivation(EMO_COMPETENCE,innerstate.getStateDouble(COMPETENCE));
		manipulator.setGateActivation(EMO_OKAYNESS,innerstate.getStateDouble(OKAYNESS));
		manipulator.setGateActivation(EMO_RESOLUTION,innerstate.getStateDouble(RESOLUTION));
		manipulator.setGateActivation(EMO_THRESHOLD,innerstate.getStateDouble(THRESHOLD));
		manipulator.setGateActivation(URGE_AFFILIATION,affiliationUrge);
		manipulator.setGateActivation(URGE_CERTAINTY,certaintyUrge);
		manipulator.setGateActivation(URGE_EFFICIENCY,efficiencyUrge);
	}
	
	public EmotionalRegulation() {

		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			public String getExtensionID() {
				return "emotionalregulation";
			}
			public String gateType(int type) {
				switch(type) {				
					case EMO_ACTIVATION: 	return "Activation";
					case EMO_CERTAINTY:	return "Certainty";
					case EMO_COMPETENCE: 	return "Competence";
					case EMO_OKAYNESS:		return "Okayness";
					case EMO_RESOLUTION:	return "Resolution";
					case EMO_THRESHOLD:	return "Threshold";
					case URGE_AFFILIATION:	return "UAffiliate";
					case URGE_CERTAINTY:	return "UCertainty";
					case URGE_EFFICIENCY:	return "UEfficiency";
					default:				return null;
				}				
			}
			public String slotType(int type) {
				switch(type) {
					case EMO_SIG_AFFILIATION:	return "SAffili";
					case EMO_SIG_CERTAINTY:		return "SCertain";
					case EMO_SIG_EFFICIENCY:	return "SEffici";
					case URGESIG_FOOD:			return "USFood";
					case URGESIG_INTEGRITY:		return "USIntegr";
					case URGESIG_LIQUIDITY:		return "USLiquid";
					default: 					return null;
				}
			}
		});
			
	}

	public void calculate(Slot[] slots, GateManipulator manipulator, long netstep) {
		
		// technical: when first called, assign variables for the incoming slots
		if(firsttime) {
			catchSlots(slots); 
		    firsttime = false;
		}
		
		double certainty = innerstate.getStateDouble(CERTAINTY);
//		System.err.println("--------------------------------------------------------------");
//		System.err.println("Initial certainty: "+certainty);
		
		// calculate new certainty		
		certainty += certaintySignals.getIncomingActivation();
		
		if(certainty > MAX_CERTAINTY) certainty = MAX_CERTAINTY;
		if(certainty < MIN_CERTAINTY) certainty = MIN_CERTAINTY;
//		System.err.println("New certainty: "+certainty);
		
		// calculate new certaintyUrge
		certaintyUrge = TARGET_CERTAINTY - certainty;
		if(certaintyUrge < 0) certaintyUrge = 0;
		
		double competence = innerstate.getStateDouble(COMPETENCE);
//		System.err.println("Initial competence: "+competence);
		
		// calculate new competence
		competence += efficiencySignals.getIncomingActivation();
		
		if(competence > MAX_COMPETENCE) competence = MAX_COMPETENCE;
		if(competence < MIN_COMPETENCE) competence = MIN_COMPETENCE;
//		System.err.println("New competence: "+competence);

		// calculate new efficiencyUrge
		efficiencyUrge = TARGET_COMPETENCE - competence;
		if(efficiencyUrge < 0) efficiencyUrge = 0;		
		
		double okayness = innerstate.getStateDouble(OKAYNESS);
//		System.err.println("Initial okayness: "+okayness);
		
		// decay okayness ("consume" your social wellness)
		okayness *= OKAYNESS_DECAY;
		
		// calculate new okayness
		okayness += affiliationSignals.getIncomingActivation();
//		System.err.println("New okayness: "+okayness);
		
		// calculate new affiliationUrge
		affiliationUrge = TARGET_OKAYNESS - okayness;
		if(affiliationUrge < 0) affiliationUrge = 0;
		
		//-------------------------------------------
			affiliationUrge = 0;
		//-------------------------------------------
		
		// find out the value of the strongest of all urges
		double strongestUrgeValue = 0;
		
		if(affiliationUrge > strongestUrgeValue) { 
			strongestUrgeValue = affiliationUrge;
//			System.err.println("sturg: affiliation");
		}
		if(certaintyUrge > strongestUrgeValue) {
			strongestUrgeValue = certaintyUrge;
//			System.err.println("sturg: certainty");
		}
		if(efficiencyUrge > strongestUrgeValue) {
			strongestUrgeValue = efficiencyUrge;
//			System.err.println("sturg: efficiency");
		}
		if(foodUrgeSignal.getIncomingActivation() > strongestUrgeValue) { 
			strongestUrgeValue = foodUrgeSignal.getIncomingActivation();
//			System.err.println("sturg: food");
		}
		if(liquidityUrgeSignal.getIncomingActivation() > strongestUrgeValue) {
			strongestUrgeValue = liquidityUrgeSignal.getIncomingActivation();
//			System.err.println("sturg: liquidity");
		}
		if(integrityUrgeSignal.getIncomingActivation() > strongestUrgeValue) {
			strongestUrgeValue = integrityUrgeSignal.getIncomingActivation();
//			System.err.println("sturg: integrity");
		}
		
//		System.err.println("Strongest urge: "+strongestUrgeValue);
			
		double activation = innerstate.getStateDouble(ACTIVATION);
//		System.err.println("Initial activation: "+activation);

		// calculate new activation (Doerner: ARaS)
		// probably there will need to be magic numbers around here to ensure that
		// the activation is always in the range 0 to 1.x - the following doesn't make
		// much sense with higher activation values. 
		activation = strongestUrgeValue - competence;
//		System.err.println("new activation: "+activation);
		
		double resolution = innerstate.getStateDouble(RESOLUTION);
//		System.err.println("Initial resolution: "+resolution); 
		
		// calculate new resolution
		resolution = 1 - Math.sqrt(activation);
//		System.err.println("New resolution: "+resolution);
		
		double selectionthreshold = innerstate.getStateDouble(THRESHOLD);
//		System.err.println("Initial threshold: "+selectionthreshold);
		
		// calculate new selection threshold
		// clearly the activation should be around 1 for this to work
		selectionthreshold = activation * selectionthreshold;
//		System.err.println("New threshold: "+selectionthreshold);
		
		innerstate.setState(THRESHOLD,selectionthreshold);
		innerstate.setState(RESOLUTION,resolution);
		innerstate.setState(ACTIVATION,activation);
		innerstate.setState(OKAYNESS,okayness);
		innerstate.setState(CERTAINTY,certainty);
		innerstate.setState(COMPETENCE,competence);
				
		updateGates(manipulator);
	}

}
