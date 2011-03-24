package org.micropsi.nodenet.modules.khepera;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class FilterMotor extends AbstractNativeModuleImpl {

public static LocalNetFacade net; 
	
	
	public static final int ST_Inputl_Value = 19000;
	public static final int ST_Inputr_Value = 19001;
	public static final int ST_Input1_Value = 19002;
	public static final int ST_Input2_Value = 19003;
	public static final int ST_Input3_Value = 19008;
	public static final int ST_Input4_Value = 19009;
	public static final int ST_Input5_Value = 19010;
	public static final int ST_Input6_Value = 19011;
	public static final int ST_Trigger_Value = 19004;
	public static final int ST_NegSensors_Value = 19005;
	
	
	public static final int GT_Motorl_Value = 19006;
	public static final int GT_Motorr_Value = 19007;
	
	private Slot inputl,inputr,input1,input2,input3,input4,input5,input6,trigger,negative;

	
	private final int[] gateTypes = {
			GT_Motorl_Value,
			GT_Motorr_Value
	};
	
	private final int[] slotTypes = {
			ST_Inputl_Value,
			ST_Inputr_Value,
			ST_Input1_Value,
			ST_Input2_Value,
			ST_Input3_Value,
			ST_Input4_Value,
			ST_Input5_Value,
			ST_Input6_Value,
			ST_Trigger_Value,
			ST_NegSensors_Value
	};
	
	
	
	@Override
	protected int[] getGateTypes() {
		
		return gateTypes;
	}

	@Override
	protected int[] getSlotTypes() {
		return slotTypes;
	}

	private double motorSetInt(double signal){
		
		
		if(signal>-1.0 && signal<0.0)
			return -1.0;
		else{
			if(signal>0.0 && signal<1.0)
				return 1.0;
			else
				return signal;
		}
		
	}
	
	
	private void catchSlots(Slot[] slots) {
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
				case ST_Inputl_Value :
					inputl = slots[i];
					break;
				case ST_Inputr_Value :
					inputr = slots[i];
					break;
				case ST_Input1_Value :
					input1 = slots[i];
					break;
				case ST_Input2_Value :
					input2 = slots[i];
					break;
				case ST_Input3_Value :
					input3 = slots[i];
					break;
				case ST_Input4_Value :
					input4 = slots[i];
					break;
				case ST_Input5_Value :
					input5 = slots[i];
					break;
				case ST_Input6_Value :
					input6 = slots[i];
					break;
				case ST_Trigger_Value :
					trigger = slots[i];
					break;
				case ST_NegSensors_Value:
					negative =slots[i];
					
			}
		}
	}
	
	

	
	
	boolean firsttime = true;
	
	
	public void initialize(){
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			public String getExtensionID() {
				return "FilterMotor";
			}

			public String gateType(int type) {
				switch(type) {
					case GT_Motorl_Value: return "Left";
					case GT_Motorr_Value: return "Right";
										
				}
				return null;
			}

			public String slotType(int type) {
				switch(type) {
				case ST_Trigger_Value: return "Trigger";
				case ST_Input1_Value: return "Sensor1";
				case ST_Input2_Value: return "Sensor2";
				case ST_Input3_Value: return "Sensor3";
				case ST_Input4_Value: return "Sensor4";
				case ST_Input5_Value: return "Sensor5";
				case ST_Input6_Value: return "Sesnor6";
				case ST_Inputl_Value: return "LeftSignal";
				case ST_Inputr_Value: return "RightSignal";
				case ST_NegSensors_Value: return "NegativeSensors";
				}
				return null;
			}
			
		});
		firsttime = false;
	}
	
	
	
	
	
	
	
	public void calculate(Slot[] slots, GateManipulator manipulator, long netstep)
			throws NetIntegrityException {
		
		
		if(firsttime){
			initialize();
			catchSlots(slots);
		}
		
		double sendleft;
		double sendright;
		double motorleft=inputl.getIncomingActivation();
		double motorright=inputr.getIncomingActivation();
		double triggersig=trigger.getIncomingActivation();
		double sensor1=input1.getIncomingActivation();
		double sensor2=input2.getIncomingActivation();
		double sensor3=input3.getIncomingActivation();
		double sensor4=input4.getIncomingActivation();
		double sensor5=input5.getIncomingActivation();
		double sensor6=input6.getIncomingActivation();
		double braitleft=sensor1+sensor2+sensor3;
		double braitright=sensor4+sensor5+sensor6;
		
		
		if(triggersig > 0 || negative.getIncomingActivation()<1){
			sendleft=motorleft;
			sendright=motorright;
		}
		else {
			sendleft=braitleft;
			sendright=braitright;
		}
		
		
		if(sensor1>0 && sensor2>0 && sensor3>0 && sensor4>0 && sensor5>0 && sensor6>0 && Math.abs(motorleft)!=Math.abs(motorright)){
			sendleft=6;
			sendright=6;
			logger.debug("LOOOOK!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		}
		
		manipulator.setGateActivation(GT_Motorr_Value,motorSetInt(sendright));
		manipulator.setGateActivation(GT_Motorl_Value,motorSetInt(sendleft));
			
//		logger.debug("out "+out1);
	}

}
