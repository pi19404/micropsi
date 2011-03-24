package org.micropsi.nodenet.modules.khepera;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class FoodNode extends AbstractNativeModuleImpl {

	
	private int remember=0;
	
	public static LocalNetFacade net; 
	
	
	
	public static final int ST_Input1_Value = 82000;
	public static final int ST_Input2_Value = 82001;
		
	public static final int GT_Out_Value = 82002;
	
	private Slot input1,input2;
	private double out1;
	
	private final int[] gateTypes = {
			GT_Out_Value			
	};
	
	private final int[] slotTypes = {
			ST_Input1_Value,
			ST_Input2_Value,
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
					
			}
		}
	}
	
	

	
	
	boolean firsttime = true;
	
	
	public void initialize(){
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			public String getExtensionID() {
				return "FOODNODE";
			}

			public String gateType(int type) {
				switch(type) {
					case GT_Out_Value: return "Activation";
										
				}
				return null;
			}

			public String slotType(int type) {
				switch(type) {
				case ST_Input1_Value: return "Tonic";
				case ST_Input2_Value: return "combination";
				}
				return null;
			}
			
		});
		firsttime = false;
	}
	
	
	

	@Override
	public void calculate(Slot[] slots, GateManipulator manipulator,
			long netstep) throws NetIntegrityException {

		innerstate.ensureStateExistence("Trigger","0.0");
		double act=0;
		if(firsttime){
			initialize();
			catchSlots(slots);
			manipulator.setGateMaximum(GT_Out_Value,100);
		}
		
		double tonic,light;
		
		tonic=input1.getIncomingActivation();
		light=input2.getIncomingActivation();
		
		
		
		if(tonic!=0 && light>=0){
			
			if(light==0){
				act=tonic;
			}else{
				remember++;
				act=tonic;
				}
				
			
			if(remember==2){
				innerstate.setState("Trigger",light*10);
			}
			
		}else{
			act=0;
		}
		
		if(innerstate.getStateDouble("Trigger")>0 && tonic>0){
			act=innerstate.getStateDouble("Trigger");
		}
		
		manipulator.setGateActivation(GT_Out_Value,act);
	
	}
	

}
