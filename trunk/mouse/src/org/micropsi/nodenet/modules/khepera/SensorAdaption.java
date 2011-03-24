package org.micropsi.nodenet.modules.khepera;

import java.util.Iterator;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class SensorAdaption extends AbstractNativeModuleImpl {


	public static LocalNetFacade net; 
	private static double decay1=100.0;
	private static double decay2=10;
	
	private static boolean doonce=true;
	
	private static double discont1=-0.4;
	private static double discont2=-0.4;
	private static double discont3=-0.2;
	private static double discont4=-0.4;
	private static double discont5=-0.2;
	private static double discont6=-0.4;
	
	private static double slope1=2.5;
	private static double slope2=5.0;
	private static double slope3=4.5;
	private static double slope4=5.0;
	private static double slope5=4.5;
	private static double slope6=2.5;
	
	
	
	
	
	public static final int GT_PROX_SENSOR1 = 20000;
	public static final int GT_PROX_SENSOR2 = 20001;
	public static final int GT_PROX_SENSOR3 = 20002;
	public static final int GT_PROX_SENSOR4 = 20003;
	public static final int GT_PROX_SENSOR5 = 20004;
	public static final int GT_PROX_SENSOR6 = 20005;
	
	
	public static final int ST_Prox_SENSOR1 = 20006;
	public static final int ST_Prox_SENSOR2 = 20007;
	public static final int ST_Prox_SENSOR3 = 20008;
	public static final int ST_Prox_SENSOR4 = 20009;
	public static final int ST_Prox_SENSOR5 = 20010;
	public static final int ST_Prox_SENSOR6 = 20011;
	public static final int ST_Trigger_SENSOR = 20012;
	
	
	private final int[] slotTypes = {
			ST_Prox_SENSOR1,
			ST_Prox_SENSOR2,
			ST_Prox_SENSOR3,
			ST_Prox_SENSOR4,
			ST_Prox_SENSOR5,
			ST_Prox_SENSOR6,
			ST_Trigger_SENSOR
			
	};
	
	private final int[] gateTypes = {
			GT_PROX_SENSOR1,
			GT_PROX_SENSOR2,
			GT_PROX_SENSOR3,
			GT_PROX_SENSOR4,
			GT_PROX_SENSOR5,
			GT_PROX_SENSOR6,
	};
	
	
	///	proximity:
	private Slot sensor1,sensor2,sensor3,sensor4,sensor5,sensor6,trigger; 
	private boolean firsttime = true;
	
	
	
	@Override
	protected int[] getGateTypes() {
		return gateTypes;
	}

	@Override
	protected int[] getSlotTypes() {
		return slotTypes;
	}

	private double [] calculOut(double act,double fact,double dis,double slope){
		
		double[] back=new double[2];
		
		fact+=(Math.abs(fact)-((1-Math.signum(act))/2.0))/(decay1*(1-Math.signum(act))/2.0+decay2*(1+Math.signum(act))/2.0);
		
		if(fact<-1)
			fact=-1;
		if(fact>0)
			fact=0;
//		if(act<0.0){
//			logger.debug("ACT1 :"+act);
//			logger.debug("FACT :"+fact);
//		}
		act+=fact;
//		if(act<0.0){
//			logger.debug("ACT2 :"+act);
//		}
		
		if(act<dis && slope>0){
			act=act*slope;
		}
		back[0]=fact;
		back[1]=act;
		
		
		return back;
	}
	
	
	private void catchSlots(Slot[] slots) {
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
				case ST_Prox_SENSOR1 :
					sensor1 = slots[i];
					break;
				case ST_Prox_SENSOR2 :
					sensor2 = slots[i];
					break;
				case ST_Prox_SENSOR3 :
					sensor3 = slots[i];
					break;
				case ST_Prox_SENSOR4 :
					sensor4 = slots[i];
					break;
				case ST_Prox_SENSOR5 :
					sensor5 = slots[i];
					break;
				case ST_Prox_SENSOR6 :
					sensor6 = slots[i];
					break;
				case ST_Trigger_SENSOR:
					trigger=slots[i];
					break;
					
			}
		}
	}
	
	public void initialize(){
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			public String getExtensionID() {
				return "SensorAdaption";
			}

			public String gateType(int type) {
				switch(type) {
					case GT_PROX_SENSOR1: return "Sensor1"; 
					case GT_PROX_SENSOR2: return "Sensor2"; 
					case GT_PROX_SENSOR3: return "Sensor3"; 
					case GT_PROX_SENSOR4: return "Sensor4"; 
					case GT_PROX_SENSOR5: return "Sensor5"; 
					case GT_PROX_SENSOR6: return "Sensor6"; 
				}
				return null;
			}

			public String slotType(int type) {
				switch(type) {
					case ST_Prox_SENSOR1: return "Sensor1";
					case ST_Prox_SENSOR2: return "Sensor2";
					case ST_Prox_SENSOR3: return "Sensor3";
					case ST_Prox_SENSOR4: return "Sensor4";
					case ST_Prox_SENSOR5: return "Sensor5";
					case ST_Prox_SENSOR6: return "Sensor6";
					case ST_Trigger_SENSOR:return "Trigger";
				}
				return null;
			}
			
		});
		firsttime = false;
	}
	

	
	@Override
	public void calculate(Slot[] slots, GateManipulator manipulator, long netstep) throws NetIntegrityException {
		
		innerstate.ensureStateExistence("Sensor1","0.0");
		innerstate.ensureStateExistence("Sensor2","0.0");
		innerstate.ensureStateExistence("Sensor3","0.0");
		innerstate.ensureStateExistence("Sensor4","0.0");
		innerstate.ensureStateExistence("Sensor5","0.0");
		innerstate.ensureStateExistence("Sensor6","0.0");
		
		
		
		
		if(firsttime){ 
			initialize();
			catchSlots(slots);
			innerstate.setState("Sensor1",0.0);
			innerstate.setState("Sensor2",0.0);
			innerstate.setState("Sensor3",0.0);
			innerstate.setState("Sensor4",0.0);
			innerstate.setState("Sensor5",0.0);
			innerstate.setState("Sensor6",0.0);
		}
		
		double factor1=innerstate.getStateDouble("Sensor1");
		double factor2=innerstate.getStateDouble("Sensor2");
		double factor3=innerstate.getStateDouble("Sensor3");
		double factor4=innerstate.getStateDouble("Sensor4");
		double factor5=innerstate.getStateDouble("Sensor5");
		double factor6=innerstate.getStateDouble("Sensor6");
		double trigsig=trigger.getIncomingActivation();
		
		
//		logger.debug("TriggerSignal :"+trigsig);
		
//		Adaption of the levels:
//		sensor1
		double[] look=new double[2];
		if(trigsig>0)
			look=calculOut(sensor1.getIncomingActivation()+0.45,factor1,discont1,slope1);
		else
			look=calculOut(sensor1.getIncomingActivation(),factor1,discont1,slope1);
		if(!doonce)
			innerstate.setState("Sensor1",look[0]);
		manipulator.setGateActivation(GT_PROX_SENSOR1,look[1]);
		
		
		
		
		if(trigsig>0)		
			look=calculOut(sensor2.getIncomingActivation()+0.575,factor2,discont2,slope2);
		else
			look=calculOut(sensor2.getIncomingActivation(),factor2,discont2,slope2);
		if(!doonce)
			innerstate.setState("Sensor2",look[0]);
		manipulator.setGateActivation(GT_PROX_SENSOR2,look[1]);
		
		
		
		
		
		if(trigsig>0)
			look=calculOut(sensor3.getIncomingActivation()+0.625,factor3,discont3,slope3);
		else
			look=calculOut(sensor3.getIncomingActivation(),factor3,discont3,slope3);
		if(!doonce)
			innerstate.setState("Sensor3",look[0]);
		manipulator.setGateActivation(GT_PROX_SENSOR3,look[1]);
		
		
		
		
		if(trigsig>0)
			look=calculOut(sensor4.getIncomingActivation()+0.625,factor4,discont4,slope4);
		else
			look=calculOut(sensor4.getIncomingActivation(),factor4,discont4,slope4);
		if(!doonce)
			innerstate.setState("Sensor4",look[0]);
		manipulator.setGateActivation(GT_PROX_SENSOR4,look[1]);
		
		
		
		if(trigsig>0)
			look=calculOut(sensor5.getIncomingActivation()+0.375,factor5,discont5,slope5);
		else
			look=calculOut(sensor5.getIncomingActivation(),factor5,discont5,slope5);
		if(!doonce)
			innerstate.setState("Sensor5",look[0]);
		manipulator.setGateActivation(GT_PROX_SENSOR5,look[1]);
		
		
		
		if(trigsig>0)
			look=calculOut(sensor6.getIncomingActivation()+0.45,factor6,discont6,slope6);
		else
			look=calculOut(sensor6.getIncomingActivation(),factor6,discont6,slope6);
		if(!doonce)
			innerstate.setState("Sensor6",look[0]);
		manipulator.setGateActivation(GT_PROX_SENSOR6,look[1]);
		
		doonce=false;
		
	}

}
