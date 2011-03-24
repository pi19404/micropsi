package org.micropsi.nodenet.modules.khepera;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class MappingPlaceMotor extends AbstractNativeModuleImpl {

	
	public static final int GT_Motorleft_Value = 15000;
	public static final int GT_Motorright_Value = 15001;

	
	private double activation,interval;
	private static boolean lookPhi=true;
	
	public static final int ST_Deltax_Sensor=15002;
	public static final int ST_Deltay_Sensor=15003;
	public static final int ST_Alpha_Sensor=15004;
	public static final int ST_Goalx_Value=15005;
	public static final int ST_Goaly_Value=15006;
	

	private final int[] gateTypes = {
			GT_Motorleft_Value,
			GT_Motorright_Value,
		};
	
	private final int[] slotTypes = {
			ST_Deltax_Sensor,
			ST_Deltay_Sensor,
			ST_Alpha_Sensor,
			ST_Goalx_Value,
			ST_Goaly_Value
			
	};	
	
	
	private Slot xpos,ypos,alpha,goalx,goaly; 
	
	
	@Override
	protected int[] getGateTypes() {
		// TODO Auto-generated method stub
		return gateTypes;
	}

	@Override
	protected int[] getSlotTypes() {
		
		return slotTypes;
	}

	
	
	private void catchSlots(Slot[] slots) {
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
				case ST_Deltax_Sensor :
					xpos = slots[i];
					break;
				case ST_Deltay_Sensor :
					ypos = slots[i];
					break;
				case ST_Alpha_Sensor :
					alpha = slots[i];
					break;
				case ST_Goalx_Value :
					goalx = slots[i];
					break;
				case ST_Goaly_Value :
					goaly = slots[i];
					break;
					
			}
		}
	}
	
	

	
	
	boolean firsttime = true;
	boolean firststep=true;
	boolean xslope=true;
	boolean orientright=true;
	boolean first=true;
	
	
	public void initialize(){
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			public String getExtensionID() {
				return "MappingPlaceMotor";
			}

			public String gateType(int type) {
				switch(type) {
					case GT_Motorleft_Value: return "Motorleft";
					case GT_Motorright_Value: return "Motorright";
					
				}
				return null;
			}

			public String slotType(int type) {
				switch(type) {
				case ST_Deltax_Sensor: return "X";
				case ST_Deltay_Sensor: return "Y";
				case ST_Alpha_Sensor: return "Alpha";
				case ST_Goalx_Value: return "Goal X";
				case ST_Goaly_Value: return "Goal Y";
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
		
		
		innerstate.ensureStateExistence("goalx","0");
		innerstate.ensureStateExistence("goaly","0");
		innerstate.ensureStateExistence("phi","0");
		innerstate.ensureStateExistence("starty","0");
		innerstate.ensureStateExistence("threshold","5.0*Math.PI/180");
		
		
		double sinhalfdiff,norm,motorleft,motorright,straight;
		double diff=0;
		double orient=alpha.getIncomingActivation()*2.0*Math.PI;
		double xdir=-xpos.getIncomingActivation()+goalx.getIncomingActivation();
		double ydir=-ypos.getIncomingActivation()+goaly.getIncomingActivation();
					
		
		//the lookfor variable stores an angle, because of changing of the orient, while turning aroiund gives Problems near the point ydir=0
	
		if(goalx.getIncomingActivation()!=-1&&goaly.getIncomingActivation()!=-1){
			
			if(goalx.getIncomingActivation()!=innerstate.getStateDouble("goalx") || goaly.getIncomingActivation()!=innerstate.getStateDouble("goaly")){
				innerstate.setState("goalx",goalx.getIncomingActivation());
				innerstate.setState("goaly",goaly.getIncomingActivation());
				lookPhi=true;
			}
			
			
			motorright=motorleft=0;
			straight=Math.pow(xdir,2.0)+Math.pow(ydir,2.0);
			norm=Math.sqrt(straight);

			if(straight>0.35)
				innerstate.setState("threshold",30*Math.PI/180);
			else
				innerstate.setState("threshold",7*Math.PI/180);
			
			
			
			if(straight>0.35){
				
				if(lookPhi){
					innerstate.setState("phi",Math.acos(xdir/norm));
					lookPhi=false;
				}
				
				//if(Math.abs(ydir)<0.5)
				if(ydir<0){
					diff=2.0*Math.PI-innerstate.getStateDouble("phi")-orient;
					logger.debug("Angle to aim : "+Math.toDegrees(2*Math.PI -Math.acos(xdir/norm)));
				}
				else{
					diff=innerstate.getStateDouble("phi")-orient;
					logger.debug("Angle to aim : "+Math.toDegrees(Math.acos(xdir/norm)));
				}
				
				sinhalfdiff=Math.sin(diff/2);
				//logger.debug("sinhalf :"+sinhalfdiff);
				
				if(Math.abs(diff)>innerstate.getStateDouble("threshold")){
				   // logger.debug("diff "+Math.toDegrees(diff));
					//logger.debug("threshold :"+innerstate.getStateDouble("threshold"));
								   			
					motorright=sinhalfdiff*3.0;
					motorleft=-sinhalfdiff*3.0;
								
					
				}
				else{			
					//logger.debug("threshold "+innerstate.getStateDouble("threshold"));
					//logger.debug("diff "+Math.toDegrees(diff));
					lookPhi=true;
					motorright=2*Math.log10(straight+1.0)+Math.abs(Math.log10(straight+100.0))*sinhalfdiff;
					motorleft=2*Math.log10(straight+1.0)-Math.abs(Math.log10(straight+100.0))*sinhalfdiff;
					
				}
			}
			
	
			
			motorright=Math.ceil(Math.abs(motorright))*Math.signum(motorright);
			motorleft=Math.ceil(Math.abs(motorleft))*Math.signum(motorleft);
			
	//		logger.debug("straight :"+straight);
	//		logger.debug("motorright :"+motorright);
	//		logger.debug("motorleft :"+motorleft);
//			
//			logger.debug("Orient : "+Math.toDegrees(orient));
//			logger.debug("diff : "+Math.toDegrees(diff));
//			logger.debug("ydir : "+ydir);
//			logger.debug("xdir : "+xdir);
//			logger.debug("motorleft : "+motorleft);
//			logger.debug("motorright : "+motorright);
							
			
		
		}else{
			motorright=0;
			motorleft=0;
		}
			
		manipulator.setGateActivation(GT_Motorleft_Value,motorleft);
		manipulator.setGateActivation(GT_Motorright_Value,motorright);


	}

}
