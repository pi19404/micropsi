package org.micropsi.nodenet.modules.khepera;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class SensorScalings extends AbstractNativeModuleImpl {

	public static LocalNetFacade net; 
	
	public static final int GT_PROX_SENSOR = 10000;
	public static final int ST_PROX_SENSOR = 10001;
	
	public static final int GT_LIGHT_SENSOR = 10002;
	public static final int ST_LIGHT_SENSOR = 10003;
	
	public static final int GT_ADAPTIVE_SENSOR = 10004;
	public static final int ST_ADAPTIVE_SENSOR = 10005;
	public static final int GT_ADAPTIVE_MAX_SENSOR = 10006;
	public static final int GT_ADAPTIVE_MIN_SENSOR = 10007;
	
	private final int[] slotTypes = {
			ST_PROX_SENSOR,
			ST_LIGHT_SENSOR,
			ST_ADAPTIVE_SENSOR
	};
	
	private final int[] gateTypes = {
			GT_PROX_SENSOR,
			GT_LIGHT_SENSOR,
			GT_ADAPTIVE_SENSOR,
			GT_ADAPTIVE_MIN_SENSOR,
			GT_ADAPTIVE_MAX_SENSOR
	};
	
	// Buffer of the sensor make a low pass filter for negative values
	private MakeLowPassFilter negbuffer = new MakeLowPassFilter(3);
	
	
	///	proximity:
	private Slot prox; 
	private double p;
	private double out_p;
	
	/// light:
	private Slot light; 
	private double l;
	private double out_l;
	
	/// adaptive (for all sensor types):
	private Slot adaptive; 
	private double a;
	private double out_a;
	
	private boolean firsttime = true;
	private static long firstnetstep;
	
	
	
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
				case ST_PROX_SENSOR :
					prox = slots[i];
					break;
				case ST_LIGHT_SENSOR : 
					light = slots[i];
					break;
				case ST_ADAPTIVE_SENSOR : 
					adaptive = slots[i];
					break;
			}
		}
	}
	
	public void initialize(){
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			public String getExtensionID() {
				return "sensorscaling";
			}

			public String gateType(int type) {
				switch(type) {
					case GT_PROX_SENSOR: return "Prox out"; 
					case GT_LIGHT_SENSOR: return "Light out";
					case GT_ADAPTIVE_SENSOR: return "Adapt out";
					case GT_ADAPTIVE_MAX_SENSOR: return "Adapt MAX";
					case GT_ADAPTIVE_MIN_SENSOR: return "Adapt MIN";
				}
				return null;
			}

			public String slotType(int type) {
				switch(type) {
					case ST_PROX_SENSOR: return "Prox in";
					case ST_LIGHT_SENSOR: return "Light in";
					case ST_ADAPTIVE_SENSOR: return "Adapt in";
				}
				return null;
			}
			
		});
		firsttime = false;
	}
	

	
	@Override
	public void calculate(Slot[] slots, GateManipulator manipulator, long netstep) throws NetIntegrityException {
		
		if(firsttime){ 
			initialize();
			catchSlots(slots);
			firstnetstep=netstep;
		}

		
		
//	   	 =====================================================================================
//		 proximity sensors: work with fixed min|max values ([0,1023]->[0,1]=[far,near]):
//		 =====================================================================================
				
        /// calibration helpers:
		innerstate.ensureStateExistence("MIN_PROX","0");
		innerstate.ensureStateExistence("MAX_PROX","1023");
		innerstate.ensureStateExistence("MIN_PROX","0");
		innerstate.ensureStateExistence("MAX_PROX","1023");
		p = prox.getIncomingActivation();
		double MAX_PROX = innerstate.getStateDouble("MAX_PROX");
		double MIN_PROX = innerstate.getStateDouble("MIN_PROX");
		
//		    //rescaled to [0,1] with high values meaning short distance to obstacle:
//				out_p = (p - MIN_PROX)/(MAX_PROX - MIN_PROX);
			
			//rescaled to [0,1] with low values meaning short distance to obstacle:
			out_p = (1-((p - MIN_PROX)/(MAX_PROX - MIN_PROX)));
		
			if(out_p < 0 || firstnetstep==netstep)
					out_p = 0;
				
			if(out_p < 0){
				if(negbuffer.negative()){
					negbuffer.clean();
				}else{
					negbuffer.update(out_p);
					out_p=1;
				}
			}
			manipulator.setGateActivation(GT_PROX_SENSOR, out_p);
		
		

//		=====================================================================================
//		light sensors: work with fixed min|max values ([0,512]->[1,0]=[bright,dark]):
//		=====================================================================================
				
        /// calibration helpers:
		innerstate.ensureStateExistence("MIN_LIGHT","0");
		innerstate.ensureStateExistence("MAX_LIGHT","512");
		l = light.getIncomingActivation();
		double MAX_LIGHT = innerstate.getStateDouble("MAX_LIGHT");
		double MIN_LIGHT = innerstate.getStateDouble("MIN_LIGHT");
		
		    //rescaled to [0,1] with low values meaning darkness:
				out_l = (l - MIN_LIGHT)/(MAX_LIGHT - MIN_LIGHT);
				if(out_l < 0)
					out_l = 0;
				
				manipulator.setGateActivation(GT_LIGHT_SENSOR, out_l);
		
				
				
//	       	 =====================================================================================
//			 light and proximity sensors: work with adaptive calibration (updated min/max) and different scalings:
//			 =====================================================================================
				
        /// calibration helpers:
		innerstate.ensureStateExistence("MIN_ADAPTIVE","200"); //ensureStateExistence to avoid nullpointer collision!!
		innerstate.ensureStateExistence("MAX_ADAPTIVE","400");
		a = adaptive.getIncomingActivation();
		
//		//reset by hand if needed (uncommend..):
//		innerstate.setState("MIN_ADAPTIVE", 200);
//		innerstate.setState("MAX_ADAPTIVE", 400);
		
		double MIN_ADAPTIVE = innerstate.getStateDouble("MIN_ADAPTIVE");
		double MAX_ADAPTIVE = innerstate.getStateDouble("MAX_ADAPTIVE");
		
		//dialog options:
//		1.reset slot: aktivierung resetet
//		2. user interaction object: a)prompt, b)alternative, c)dialog
//		
		//Frage: 1.wie bekomme ich nodenet zugriff? 2.wie kann ich bei bedarf auf userinteraktion zugreifen (ausser mit isSuspended())?
		
//		if(net.getCycle().isSuspended()){
//			String[] user = new String[1];
//			user = null;
//			user = userinteraction.selectFromAlternatives(new String[]{"reset MIN|MAX: YES!","reset MIN|MAX: NO!"});
//		userinteraction.displayInformation(user[1]);
//		}
		    ///in first if: value>1 needed because of values equal to 0 send at start up:
//				if ( (Double.compare(a, 1) > 0) && (Double.compare(a, MIN_ADAPTIVE)) < 0 ) {
				if ( (a > 1) && (a < MIN_ADAPTIVE) ) {
					innerstate.setState("MIN_ADAPTIVE", a);
					MIN_ADAPTIVE = a;
				} else if (a > MAX_ADAPTIVE) {
					innerstate.setState("MAX_ADAPTIVE", a);
					MAX_ADAPTIVE = a;
				}
				
//		    //rescaled to [0,1] with low values meaning short distance:
//				out_a = (a - MIN_ADAPTIVE)/(MAX_ADAPTIVE - MIN_ADAPTIVE);

				//rescaled to [0,1] with low values meaning short distance to obstacle:
				out_a = (((a - MIN_ADAPTIVE)/(MAX_ADAPTIVE - MIN_ADAPTIVE)) - 1) * (-1);
			
				if(out_a < 0)
					out_a = 0;
				
				
				
				manipulator.setGateActivation(GT_ADAPTIVE_SENSOR, out_a);
				manipulator.setGateActivation(GT_ADAPTIVE_MAX_SENSOR, innerstate.getStateDouble("MAX_ADAPTIVE"));
				manipulator.setGateActivation(GT_ADAPTIVE_MIN_SENSOR, innerstate.getStateDouble("MIN_ADAPTIVE"));

		
		
//		innerstate.ensureStateExistence("sepp","1");
//		manipulator.setGateActivation(GT_PROX_SENSOR, innerstate.getStateDouble("sepp"));

	}

}
