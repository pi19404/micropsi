package org.micropsi.nodenet.modules.khepera;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class RandomNumber extends AbstractNativeModuleImpl {

	
	
	
	public static LocalNetFacade net; 
	
	
	public static final int GT_Random1_Value = 11000;
	public static final int GT_Random2_Value = 11001;
	public static final int GT_Random3_Value = 11002;
	//public static final int GT_Input1_Value = 11001;
	//public static final int GT_Input2_Value = 11002;
	private double activation,interval;
	
	
	public static final int ST_Input1_Sensor=11003;
	public static final int ST_Input2_Sensor=11004;
	public static final int ST_Input3_Sensor=11005;
	public static final int ST_Input4_Sensor=11006;
	
	private double random;
	private double sensor1,sensor2,sensor3,sensor4;
	
	
	private final int[] gateTypes = {
			GT_Random1_Value,
			GT_Random2_Value,
			GT_Random3_Value
	//		GT_Input1_Value,
	//		GT_Input2_Value
			
	};
	
	private final int[] slotTypes = {
			ST_Input1_Sensor,
			ST_Input2_Sensor,
			ST_Input3_Sensor,
			ST_Input4_Sensor
			
	};
	
	
	
	
	private Slot input1,input2,input3,input4;
	
	
	@Override
	protected int[] getGateTypes() {
		
		return gateTypes;
		// TODO Auto-generated method stub
	}

	protected int[] getSlotTypes() {
		return slotTypes;
	}

	private void catchSlots(Slot[] slots) {
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
				case ST_Input1_Sensor :
					input1 = slots[i];
					break;
				case ST_Input2_Sensor :
					input2 = slots[i];
					break;
				case ST_Input3_Sensor :
					input3 = slots[i];
					break;
				case ST_Input4_Sensor :
					input4 = slots[i];
					break;
					
			}
		}
	}
	
	

	
	
	boolean firsttime = true;
	
	
	public void initialize(){
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			public String getExtensionID() {
				return "Random Generation";
			}

			public String gateType(int type) {
				switch(type) {
					case GT_Random1_Value: return "Random1";
					case GT_Random2_Value: return "Random2";
					case GT_Random3_Value: return "Random3";
					//case GT_Input1_Value: return "Signal 1";
					//case GT_Input2_Value: return "Signal 2";
					
				}
				return null;
			}

			public String slotType(int type) {
				switch(type) {
				case ST_Input1_Sensor: return "Signal 1";
				case ST_Input2_Sensor: return "Signal 2";
				case ST_Input3_Sensor: return "Signal 3";
				case ST_Input4_Sensor: return "Signal 4";
			}
				return null;
			}
			
		});
		firsttime = false;
	}
	
		

	
	
	
	
	
	

	@Override
	public void calculate(Slot[] slots, GateManipulator manipulator, long netstep)
			throws NetIntegrityException {
		
		
		if(firsttime){ 
			initialize();
			catchSlots(slots);
		}
		
		
		innerstate.ensureStateExistence("random","0");
		innerstate.setState("Stepnumber",20);
		innerstate.ensureStateExistence("Theta1","0");
		innerstate.setState("Theta1",0);
		innerstate.ensureStateExistence("Theta2","0");
		innerstate.setState("Theta2",0);
		innerstate.ensureStateExistence("Theta3","0");
		innerstate.setState("Theta3",0);
		innerstate.ensureStateExistence("Theta4","0");
		innerstate.setState("Theta4",0);
		
		
		
		
		double stepnumber=innerstate.getStateDouble("Stepnumber");
		double theta1=innerstate.getStateDouble("Theta1");
		double theta2=innerstate.getStateDouble("Theta2");
		double theta3=innerstate.getStateDouble("Theta3");
		double theta4=innerstate.getStateDouble("Theta4");
		
		sensor1=input1.getIncomingActivation();
		sensor2=input2.getIncomingActivation();
		sensor3=input3.getIncomingActivation();
		sensor4=input4.getIncomingActivation();
		
		if((sensor1-theta1)<=0 |(sensor2-theta2)<=0 |(sensor3-theta3)<=0 |(sensor4-theta4)<=0 ){
			random=-1;
			innerstate.setState("random",random);
		}
		else{
			if((netstep%stepnumber)==0){
				random=Math.random();
				innerstate.setState("random",random);
			}
		}
			
		interval=innerstate.getStateDouble("random");
		if (interval<=0.15){
			manipulator.setGateActivation(GT_Random1_Value,1);
			manipulator.setGateActivation(GT_Random2_Value,0);
			manipulator.setGateActivation(GT_Random3_Value,0);
		}
		if(interval>0.15 & interval<=0.85){
			manipulator.setGateActivation(GT_Random1_Value,0);
			manipulator.setGateActivation(GT_Random2_Value,1);
			manipulator.setGateActivation(GT_Random3_Value,0);
		}
		if(interval>0.85){
			manipulator.setGateActivation(GT_Random1_Value,0);
			manipulator.setGateActivation(GT_Random2_Value,0);
			manipulator.setGateActivation(GT_Random3_Value,1);
		}
		if(interval==-1){
			manipulator.setGateActivation(GT_Random1_Value,0);
			manipulator.setGateActivation(GT_Random2_Value,0);
			manipulator.setGateActivation(GT_Random3_Value,0);
		}		
		
		// TODO Auto-generated method stub

	}

}
