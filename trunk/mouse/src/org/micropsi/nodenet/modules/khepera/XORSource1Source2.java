package org.micropsi.nodenet.modules.khepera;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class XORSource1Source2 extends AbstractNativeModuleImpl {

	
	
	public static LocalNetFacade net; 
	
	
	public static final int ST_Input1_Value = 12000;
	public static final int ST_Input2_Value = 12001;
		
	public static final int GT_Run_Value = 12002;
	
	private Slot input1,input2;
	private double out1;
	
	private final int[] gateTypes = {
			GT_Run_Value			
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
				return "XOR";
			}

			public String gateType(int type) {
				switch(type) {
					case GT_Run_Value: return "Signal";
										
				}
				return null;
			}

			public String slotType(int type) {
				switch(type) {
				case ST_Input1_Value: return "Trigger";
				case ST_Input2_Value: return "Inputsignal";
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
		
		
		double in1=input1.getIncomingActivation();
		double in2=input2.getIncomingActivation();
			
		
		
		if(in1 > 0 ){
			out1=0;
		}
		else {
			out1=in2;
		}
		
		if(out1>0.1 && out1<0.9)
			out1=1;
		if(out1<-0.1 && out1>-0.9)
			out1=-1;
		manipulator.setGateActivation(GT_Run_Value,out1);
//		logger.debug("out "+out1);
	}

}
