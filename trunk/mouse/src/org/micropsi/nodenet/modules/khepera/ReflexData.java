package org.micropsi.nodenet.modules.khepera;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class ReflexData extends AbstractNativeModuleImpl {

	public static LocalNetFacade net; 
	
	//Store the negative activation of the proximity snesors in the (number) last netsteps
	
	private static MakeLowPassFilter prox1buffer = new MakeLowPassFilter(2);
	private static MakeLowPassFilter prox2buffer = new MakeLowPassFilter(5);
	private static MakeLowPassFilter prox3buffer = new MakeLowPassFilter(5);
	private static MakeLowPassFilter prox4buffer = new MakeLowPassFilter(2);
	private static MakeLowPassFilter prox5buffer = new MakeLowPassFilter(2);
	private static MakeLowPassFilter prox6buffer = new MakeLowPassFilter(2);
	
	private static MakeLowPassFilter prox1buffer2 = new MakeLowPassFilter(2);
	private static MakeLowPassFilter prox2buffer2 = new MakeLowPassFilter(2);
	private static MakeLowPassFilter prox3buffer2 = new MakeLowPassFilter(2);
	private static MakeLowPassFilter prox4buffer2 = new MakeLowPassFilter(2);
	private static MakeLowPassFilter prox5buffer2 = new MakeLowPassFilter(2);
	private static MakeLowPassFilter prox6buffer2 = new MakeLowPassFilter(2);
	

	
	public static final int ST_Input1_Value = 185000;
	public static final int ST_Input2_Value = 185001;
	public static final int ST_Input3_Value = 185002;
	public static final int ST_Input4_Value = 185003;
	public static final int ST_Input5_Value = 185004;
	public static final int ST_Input6_Value = 185005;
	public static final int ST_Input7_Value = 185006;
	
	
	public static final int GT_Out_Value = 85007;
	public static final int GT_Braitenberg_Value = 85008;
	public static final int GT_Buffer_Sensor1 = 85009;
	public static final int GT_Buffer_Sensor2 = 85010;
	public static final int GT_Buffer_Sensor3 = 85011;
	public static final int GT_Buffer_Sensor4 = 85012;
	public static final int GT_Buffer_Sensor5 = 85013;
	public static final int GT_Buffer_Sensor6 = 85014;
	
	private Slot input1,input2,input3,input4,input5,input6,input7;
	
	private final int[] gateTypes = {
			GT_Out_Value	,
			GT_Braitenberg_Value,
			GT_Buffer_Sensor1,
			GT_Buffer_Sensor2,
			GT_Buffer_Sensor3,
			GT_Buffer_Sensor4,
			GT_Buffer_Sensor5,
			GT_Buffer_Sensor6,
			
	};
	
	private final int[] slotTypes = {
			ST_Input1_Value,
			ST_Input2_Value,
			ST_Input3_Value,
			ST_Input4_Value,
			ST_Input5_Value,
			ST_Input6_Value,
			ST_Input7_Value
	};
	
	
	
	@Override
	protected int[] getGateTypes() {
		
		return gateTypes;
	}

	@Override
	protected int[] getSlotTypes() {
		return slotTypes;
	}

	
	
	
	private void catchSlots(Slot[] slots) {
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
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
				case ST_Input7_Value :
					input7 = slots[i];
					break;
					
			}
		}
	}
	
	

	
	
	boolean firsttime = true;
	
	
	public void initialize(){
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			public String getExtensionID() {
				return "ReflexNode";
			}

			public String gateType(int type) {
				switch(type) {
					case GT_Out_Value: return "ReflexSignal";
					case GT_Braitenberg_Value: return "BraitenbergSwitch";
					case GT_Buffer_Sensor1: return "BufferSensor1";
					case GT_Buffer_Sensor2: return "BufferSensor2";
					case GT_Buffer_Sensor3: return "BufferSensor3";
					case GT_Buffer_Sensor4: return "BufferSensor4";
					case GT_Buffer_Sensor5: return "BufferSensor5";
					case GT_Buffer_Sensor6: return "BufferSensor6";
					
										
				}
				return null;
			}

			public String slotType(int type) {
				switch(type) {
				case ST_Input1_Value: return "Sensor1";
				case ST_Input2_Value: return "Sensor2";
				case ST_Input3_Value: return "Sensor3";
				case ST_Input4_Value: return "Sensor4";
				case ST_Input5_Value: return "Sensor5";
				case ST_Input6_Value: return "Sensor6";
				case ST_Input7_Value: return "Trigger";
				}
				return null;
			}
			
		});
		firsttime = false;
	}
	
	@Override
	public void calculate(Slot[] slots, GateManipulator manipulator,
			long netstep) throws NetIntegrityException {
		
		
		if(firsttime){
			initialize();
			catchSlots(slots);
		}
//		For the Braitenberg Analysis
		double prox12=input1.getIncomingActivation(); 
		double prox22=input2.getIncomingActivation(); 
		double prox32=input3.getIncomingActivation(); 
		double prox42=input4.getIncomingActivation(); 
		double prox52=input5.getIncomingActivation(); 
		double prox62=input6.getIncomingActivation(); 
		
		if(prox12!=prox1buffer2.getEntry(0))
			prox1buffer2.update(prox12);
		if(prox22!=prox2buffer2.getEntry(0))
			prox2buffer2.update(prox22);
		if(prox32!=prox3buffer2.getEntry(0))
			prox3buffer2.update(prox32);
		if(prox42!=prox4buffer2.getEntry(0))
			prox4buffer2.update(prox42);
		if(prox52!=prox5buffer2.getEntry(0))
			prox5buffer2.update(prox52);
		if(prox62!=prox6buffer2.getEntry(0))
			prox6buffer2.update(prox62);
		
		if(prox1buffer2.negative() || prox2buffer2.negative() || prox3buffer2.negative() || prox4buffer2.negative() || prox5buffer2.negative() || prox6buffer2.negative()){
			manipulator.setGateActivation(GT_Braitenberg_Value,1);
		}else{
			manipulator.setGateActivation(GT_Braitenberg_Value,0);
		}
		
		
		
		
		
		if(prox1buffer2.negative()){
			manipulator.setGateActivation(GT_Buffer_Sensor1,prox1buffer2.getEntry(0));
//			logger.debug("Sensor1");
		}else{
			manipulator.setGateActivation(GT_Buffer_Sensor1,1.0);
		}
		
		if(prox2buffer2.negative()){
			manipulator.setGateActivation(GT_Buffer_Sensor2,prox2buffer2.getEntry(0));
//			logger.debug("Sensor2");
		}else{
			manipulator.setGateActivation(GT_Buffer_Sensor2,1.0);
		}
		
		if(prox3buffer2.negative()){
			manipulator.setGateActivation(GT_Buffer_Sensor3,prox3buffer2.getEntry(0));
//			logger.debug("Sensor3");
		}else{
			manipulator.setGateActivation(GT_Buffer_Sensor3,1.0);
		}
		
		if(prox4buffer2.negative()){
			manipulator.setGateActivation(GT_Buffer_Sensor4,prox4buffer2.getEntry(0));
//			logger.debug("Sensor4");
		}else{
			manipulator.setGateActivation(GT_Buffer_Sensor4,1.0);
		}
		
		if(prox5buffer2.negative()){
			manipulator.setGateActivation(GT_Buffer_Sensor5,prox5buffer2.getEntry(0));
//			logger.debug("Sensor5");
		}else{
			manipulator.setGateActivation(GT_Buffer_Sensor5,1.0);
		}
		
		if(prox6buffer2.negative()){
			manipulator.setGateActivation(GT_Buffer_Sensor6,prox6buffer2.getEntry(0));
//			logger.debug("Sensor6");
		}else{
			manipulator.setGateActivation(GT_Buffer_Sensor6,1.0);
		}		
		
		
/////////////////////////////////////////////////////////////////////////////////
		
		
		
		
		
		if(input7.getIncomingActivation()==0){
			double prox1=input1.getIncomingActivation(); 
			double prox2=input2.getIncomingActivation(); 
			double prox3=input3.getIncomingActivation(); 
			double prox4=input4.getIncomingActivation(); 
			double prox5=input5.getIncomingActivation(); 
			double prox6=input6.getIncomingActivation(); 
			
			
			
			
//			if(prox1<0 || prox2<0){
//			logger.debug("Prox1"+prox1);
//			logger.debug("Prox4"+prox4);
//			}
			
			//LowPassFilter of the proximity Data;
			if(prox1!=prox1buffer.getEntry(0))
				prox1buffer.update(prox1);
			if(prox2!=prox2buffer.getEntry(0))
				prox2buffer.update(prox2);
			if(prox3!=prox3buffer.getEntry(0))
				prox3buffer.update(prox3);
			if(prox4!=prox4buffer.getEntry(0))
				prox4buffer.update(prox4);
			if(prox5!=prox5buffer.getEntry(0))
				prox5buffer.update(prox5);
			if(prox6!=prox6buffer.getEntry(0))
				prox6buffer.update(prox6);
		
			if(prox1buffer.negative() || prox2buffer.negative() || prox3buffer.negative() || prox4buffer.negative() || prox5buffer.negative() || prox6buffer.negative()){
				manipulator.setGateActivation(GT_Out_Value,-1);
//				logger.debug("!!!!!!!!!!!!!!!!!!!!Reflex!!!!!!!!!!!From Module");
				prox1buffer.clean();
				prox2buffer.clean();
				prox3buffer.clean();
				prox4buffer.clean();
				prox5buffer.clean();
				prox6buffer.clean();
			}else{
				manipulator.setGateActivation(GT_Out_Value,0);
			}
		}else{
			prox1buffer.clean();
			prox2buffer.clean();
			prox3buffer.clean();
			prox4buffer.clean();
			prox5buffer.clean();
			prox6buffer.clean();
		}
		
		
	}
	
}
