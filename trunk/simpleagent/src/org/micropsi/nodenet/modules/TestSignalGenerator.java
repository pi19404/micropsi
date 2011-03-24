/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/simpleagent/src/org/micropsi/nodenet/modules/TestSignalGenerator.java,v 1.1 2004/05/07 21:48:06 vuine Exp $
 */
package org.micropsi.nodenet.modules;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

/**
 * 
 *
 */
public class TestSignalGenerator extends AbstractNativeModuleImpl {

	// slots
		
	// gates
	private static final int ONE			=	20000;
	private static final int LINEAR			=	20001;
	
	
	private final int[] slotTypes = {
	};

	private final int[] gateTypes = {
		ONE,
		LINEAR
	};

	private boolean firsttime = true;
	
	protected int[] getGateTypes() {
		return gateTypes;
	}

	protected int[] getSlotTypes() {
		return slotTypes;
	}
	
	private void catchSlots(Slot[] slots) {
		for(int i=0;i<slots.length;i++) {
			switch(slots[i].getType()) {
			}
		}		
	}
	
	public TestSignalGenerator() {

		TypeStrings.activateExtension(new TypeStringsExtensionIF() {
			public String getExtensionID() {
				return "testsignalgenerator";
			}
			public String gateType(int type) {
				switch(type) {
					case ONE:			return "One [1,1]";
					case LINEAR:		return "Lin [-4,4]";
					default:			return null;
				}				
			}
			public String slotType(int type) {
				switch(type) {
					default: 				return null;
				}
			}
		});

	}

	double linearval = -4;
	double step = 0.05;
	
	public void calculate(Slot[] slots, GateManipulator gates, long netstep) throws NetIntegrityException {
		if(firsttime) { 
			catchSlots(slots);
			firsttime = false;
		}
		
		gates.setGateActivation(ONE,1.0);
		gates.setGateActivation(LINEAR,linearval);

		linearval += step; 
		if(linearval >= 4) linearval = -4;
		
		
	}
}
