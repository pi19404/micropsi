package org.micropsi.nodenet.modules;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class NameInterfaceModule extends AbstractNativeModuleImpl {

	private final int[] slotTypes = {
	        10800,
	        10801,
	        10802,
	        10803,
	        10804,
	        10805,
	        10806,
	        10807,
	        10808,
	        10809,
	        10810,
	        10811,
	        10812,
	        10813
		};
	
	private final int[] gateTypes = {
	        10900,
	        10901
	};
	
	public NameInterfaceModule() {	
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			private static final String id = "naming";

			public String getExtensionID() {
				return id;
			}

			public String slotType(int type) {
			    switch(type) {
			    	case 10800:  return "x-position";
			    	case 10801:  return "y-position";
			    	case 10802:	 return "foodurge";	 
			    	case 10803:	 return "waterurge";
			    	case 10804:  return "integrityurge";
			    	case 10805:  return "affiliationurge";
			    	case 10806:  return "orientation";
			    	case 10807:	 return "obstacle";
			    	case 10808:  return "poison";
			    	case 10809:  return "stop-protocol-regions";
			    	case 10810:  return "collision";
			    	case 10811:  return "food";
			    	case 10812:  return "water";
			    	case 10813:	 return "healing";
			    	default:     return null;
			    }
			}

			public String gateType(int type) {
				switch(type) {
					case 10900: return "left-wheel";
					case 10901: return "right-wheel";
					default: 	return null;
				}
			}
		});		
	}

	protected int[] getGateTypes() {
		return gateTypes;
	}

	protected int[] getSlotTypes() {
		return slotTypes;
	}

	public void calculate(Slot[] arg0, GateManipulator arg1, long arg2) throws NetIntegrityException {
		return;
	}

}
