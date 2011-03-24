package org.micropsi.nodenet.modules.khepera;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class AllocentricEgocentric extends AbstractNativeModuleImpl {

	
	//Parameter which can be set
	
	private static final int speed=6;
	private static boolean rotate=false;	
	
	public static final int GT_Motorleft_Value = 15000;
	public static final int GT_Motorright_Value = 15001;
	public static final int GT_Trigger_Value = 15008;
	public static final int GT_Reflex_Value = 15009;
	public static final int GT_Rotating_Value = 15010;
	
	public static final int ST_Alpha_Sensor=15002;
	public static final int ST_Sensor1_Sensor=15003;
	public static final int ST_Sensor2_Sensor=15004;
	public static final int ST_Orient_Sensor=15005;
	public static final int ST_Sensor3_Sensor=15006;
	public static final int ST_Sensor4_Sensor=15007;
	public static final int ST_Sensor5_Sensor=150011;
	public static final int ST_Sensor6_Sensor=150012;
	
	

	private final int[] gateTypes = {
			GT_Motorleft_Value,
			GT_Motorright_Value,
			GT_Trigger_Value,
			GT_Reflex_Value,
			GT_Rotating_Value
		};
	
	private final int[] slotTypes = {
			ST_Orient_Sensor,
			ST_Alpha_Sensor,
			ST_Sensor1_Sensor,
			ST_Sensor2_Sensor,
			ST_Sensor3_Sensor,
			ST_Sensor4_Sensor,
			ST_Sensor5_Sensor,
			ST_Sensor6_Sensor
			
	};	
	
	
	private Slot alpha,sensor1,sensor2,sensor3,sensor4,sensor5,sensor6,orient; 
	
	
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
				case ST_Sensor1_Sensor :
					sensor1 = slots[i];
					break;
				case ST_Sensor2_Sensor :
					sensor2 = slots[i];
					break;
				case ST_Sensor3_Sensor :
					sensor3 = slots[i];
					break;
				case ST_Sensor4_Sensor :
					sensor4 = slots[i];
					break;
				case ST_Sensor5_Sensor :
					sensor5 = slots[i];
					break;
				case ST_Sensor6_Sensor :
					sensor6 = slots[i];
					break;
				case ST_Alpha_Sensor :
					alpha = slots[i];
					break;
				case ST_Orient_Sensor :
					orient=slots[i];
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
				return "AllocentricEgocentric";
			}

			public String gateType(int type) {
				switch(type) {
					case GT_Motorleft_Value: return "Motorleft";
					case GT_Motorright_Value: return "Motorright";
					case GT_Trigger_Value: return "Trigger";
					case GT_Reflex_Value: return "ReflexTrigger";
					case GT_Rotating_Value: return "Rotate";
					
				}
				return null;
			}

			public String slotType(int type) {
				switch(type) {
				case ST_Sensor1_Sensor: return "Sensor1";
				case ST_Sensor2_Sensor: return "Sensor2";
				case ST_Sensor3_Sensor: return "Sensor3";
				case ST_Sensor4_Sensor: return "Sensor4";
				case ST_Sensor5_Sensor: return "Sensor5";
				case ST_Sensor6_Sensor: return "Sensor6";
				case ST_Alpha_Sensor: return "Orient";
				case ST_Orient_Sensor: return "Orienttogo";
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
		
		double aimorient=orient.getIncomingActivation();
		double motorleft,motorright,togoorient;
		
		
		innerstate.ensureStateExistence("threshold","15.0*Math.PI/180");
		innerstate.setState("threshold",3.0*Math.PI/180);
		innerstate.ensureStateExistence("StoreOrient","-1");
		innerstate.ensureStateExistence("NumberCorrection","0");
		innerstate.ensureStateExistence("Once","0");
		innerstate.ensureStateExistence("Moveright","0");
		innerstate.ensureStateExistence("Moveleft","0");
//		Look for the Problem of beeing in left ore right we have to adjust the angle in a proper way otherwise the robot is turning;
		innerstate.ensureStateExistence("Orientold","0.0");
//		innerstate.ensureStateExistence("right","0");
//		innerstate.ensureStateExistence("left","0");
		
		manipulator.setGateActivation(GT_Rotating_Value,0);
//		Initialize the values for each orientation which should get installed
		if(aimorient>0){
			innerstate.setState("Moveleft",0.0);
			innerstate.setState("Moveright",0.0);
			innerstate.setState("Once",0);
			innerstate.setState("Orientation",0.0);
		}
		
		
		
		
//		In case of rotating in direction of a wall start rotating ot another direction setting parameter of which way should be rotated
		if(innerstate.getStateDouble("Once")==0 && innerstate.getStateDouble("StoreOrient")!=-1 && (sensor1.getIncomingActivation()<0 || sensor2.getIncomingActivation()<0 || sensor5.getIncomingActivation()<0) && (sensor3.getIncomingActivation()>0 && sensor4.getIncomingActivation()>0 && sensor6.getIncomingActivation()>0)){
			innerstate.setState("Moveright",1.0);
			innerstate.setState("Moveleft",0.0);
			innerstate.setState("Once",1.0);
			rotate=true;
			logger.debug("Right------------------------------------------------------------------------");
		}
		if(innerstate.getStateDouble("Once")==0 && innerstate.getStateDouble("StoreOrient")!=-1 && (sensor1.getIncomingActivation()>0 && sensor2.getIncomingActivation()>0 && sensor5.getIncomingActivation()>0) && (sensor3.getIncomingActivation()<0 || sensor4.getIncomingActivation()<0 || sensor6.getIncomingActivation()<0)){
			innerstate.setState("Moveleft",1.0);
			innerstate.setState("Moveright",0.0);
			innerstate.setState("Once",1.0);
			innerstate.setState("Braitenberg",0.0);
			rotate=true;
			logger.debug("Left--------------------------------------------------------------------------");
			
			}

		if(innerstate.getStateDouble("Moveright")==1 && (sensor3.getIncomingActivation()<0 || sensor4.getIncomingActivation()<0 || sensor6.getIncomingActivation()<0)){
			logger.debug("Get A Reflex Right");
			innerstate.setState("StoreOrient",-1);
			innerstate.setState("Once",0);
			manipulator.setGateActivation(GT_Reflex_Value,0);
			manipulator.setGateActivation(GT_Rotating_Value,-1);
			manipulator.setGateActivation(GT_Trigger_Value,0);
			rotate=false;
			innerstate.setState("Orientold",0.0);
			innerstate.setState("Moveright",0.0);
			motorright=speed;
			motorleft=speed;
		}
		
		if(innerstate.getStateDouble("Moveleft")==1 && (sensor1.getIncomingActivation()<0 || sensor2.getIncomingActivation()<0 || sensor5.getIncomingActivation()<0)){
			logger.debug("Get A Reflex Left");
			innerstate.setState("StoreOrient",-1);
			innerstate.setState("Once",0);
			manipulator.setGateActivation(GT_Reflex_Value,0);
			manipulator.setGateActivation(GT_Rotating_Value,-1);
			manipulator.setGateActivation(GT_Trigger_Value,0);
			innerstate.setState("Orientold",0.0);
			rotate=false;
			innerstate.setState("Moveleft",0.0);
			motorright=speed;
			motorleft=speed;
		}
		
		if((sensor1.getIncomingActivation()<0 || sensor2.getIncomingActivation()<0 || sensor5.getIncomingActivation()<0) && (sensor3.getIncomingActivation()<0 || sensor4.getIncomingActivation()<0 || sensor6.getIncomingActivation()<0)){
//			logger.debug("Get A Reflex");
			innerstate.setState("StoreOrient",-1);
			manipulator.setGateActivation(GT_Reflex_Value,0);
			innerstate.setState("Once",0);
			manipulator.setGateActivation(GT_Trigger_Value,0);
			motorright=speed;
			motorleft=speed;
			innerstate.setState("Moveleft",0.0);
			innerstate.setState("Moveright",0.0);
			innerstate.setState("Once",0);
			innerstate.setState("Orientold",0.0);
			rotate=false;
		}
		
		
		if(aimorient>0)
		logger.debug("AimOrient "+Math.toDegrees(aimorient));
//		Set Value for OrientationSensor to -.5 to stop the robot
		if(aimorient==-.5){
			//urge is reached!!!
			motorleft=0;
			motorright=0;
			manipulator.setGateActivation(GT_Trigger_Value,1);
		}else{
			if(aimorient!=-1)
				innerstate.setState("StoreOrient",aimorient);
			
			 //logger.debug("Orientation : "+alpha.getIncomingActivation()*360);
			 //logger.debug(" AlloEgo :"+Math.toDegrees(innerstate.getStateDouble("StoreOrient")));
			if((sensor1.getIncomingActivation()<0 || sensor2.getIncomingActivation()<0 || sensor3.getIncomingActivation()<0 || sensor4.getIncomingActivation()<0 || sensor5.getIncomingActivation()<0 || sensor6.getIncomingActivation()<0)&&innerstate.getStateDouble("StoreOrient")==-1){
//			   logger.debug("Depp!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				motorleft=speed;
			   motorright=speed;
			   manipulator.setGateActivation(GT_Trigger_Value,0);
			   manipulator.setGateActivation(GT_Reflex_Value,0);
			   innerstate.setState("StoreOrient",-1);
			   rotate=false;
			   innerstate.setState("Orientold",0.0);
			}
			else{
				if(innerstate.getStateDouble("StoreOrient")==-1){
					motorright=speed;
					motorleft=speed;
					manipulator.setGateActivation(GT_Trigger_Value,0);
					innerstate.setState("Orientold",0.0);
				}else{
					
					togoorient=innerstate.getStateDouble("StoreOrient");
							
					//	logger.debug("MOVEMENTXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
					manipulator.setGateActivation(GT_Trigger_Value,1);
									
										
					double sinhalfdiff;
					double orientation=alpha.getIncomingActivation()*2.0*Math.PI;
					//double aimorient=0*Math.PI/180;
//					Adjusting the orientation in the right way in the rotation state.
//					logger.debug("Rotation : "+innerstate.getStateDouble("Orientold"));
					if(rotate && innerstate.getStateDouble("Orientold")!=0.0){
//						logger.debug("Rotation : "+rotate);
						if(Math.abs(orientation-innerstate.getStateDouble("Orientold"))<1.75*Math.PI){
							if(innerstate.getStateDouble("Orientold")>orientation){
								if(togoorient<=innerstate.getStateDouble("Orientold") && togoorient>=orientation){
									rotate=false;
									logger.debug("Overdriven");
								}else{
									rotate=true;
								}
							}else{
								if(togoorient>=innerstate.getStateDouble("Orientold") && togoorient<=orientation){
									rotate=false;
									logger.debug("Overdriven");
								}else{
									rotate=true;
								}
							}
						}else{
							if(togoorient==2*Math.PI){
								rotate=false;
							}else{
								rotate=true;
							}
								
						}
					}
//					logger.debug("Rotation "+rotate);
					// differnt ways to evaluate the confition
					if(innerstate.getStateDouble("Moveright")==1 && rotate){
						sinhalfdiff=-Math.abs(Math.sin((togoorient-orientation)/2));
						innerstate.setState("Orientold",orientation);
//						logger.debug("MOVE RIGHT!!!!!!!!!!!!!!!!!!"+orientation);
						manipulator.setGateActivation(GT_Reflex_Value,1);
					}else{
						if(innerstate.getStateDouble("Moveleft")==1 && rotate){
							sinhalfdiff=Math.abs(Math.sin((togoorient-orientation)/2));
							innerstate.setState("Orientold",orientation);
//							logger.debug("MOVE Left!!!!!!!!!!!!!!!!!!!!!"+orientation);
							manipulator.setGateActivation(GT_Reflex_Value,1);
						}else{
//							if (Math.abs(togoorient-orientation)>Math.PI)
//								   sinhalfdiff=Math.sin((togoorient-orientation)/2);
//								else
							innerstate.setState("Orientold",0.0);
							sinhalfdiff=-1.0*Math.signum(Math.sin(Math.abs(togoorient-orientation)))*Math.sin((togoorient-orientation)/2);
							manipulator.setGateActivation(GT_Reflex_Value,0);
						}
					}
					
					
					if(Math.abs(togoorient-orientation)>innerstate.getStateDouble("threshold") && Math.abs(togoorient-orientation)<(2*Math.PI-innerstate.getStateDouble("threshold"))){
						motorright=sinhalfdiff*6.0;
						motorleft=-sinhalfdiff*6.0;	
					
						if (Math.abs(motorright)<1 || Math.abs(motorleft)<1){
							motorright=Math.signum(motorright);
							motorleft=Math.signum(motorleft);
							}
//						logger.debug("SetValueToOne!!!!!!");
						manipulator.setGateActivation(GT_Rotating_Value,1.0);
						
					}else{				
							motorright=speed;
							motorleft=speed;
							innerstate.setState("Orientold",0.0);
							rotate=false;
							//Correct the position of the Robot 14 times
							if (innerstate.getStateDouble("NumberCorrection")>14){
								logger.debug("Orient at the end"+Math.toDegrees(orientation));
								innerstate.setState("StoreOrient",-1);
								innerstate.setState("NumberCorrection",0);
								innerstate.setState("Once",0);
								innerstate.setState("Moveright",0);
								innerstate.setState("Moveleft",0);
								manipulator.setGateActivation(GT_Trigger_Value,0);
								manipulator.setGateActivation(GT_Rotating_Value,0);
								innerstate.setState("Orientold",0.0);
								rotate=false;
							}else{
								innerstate.setState("NumberCorrection",innerstate.getStateDouble("NumberCorrection")+1);
							}
					}		
				
				}
							
			}
		}//end of if urge is reached
	
//		if(innerstate.getStateDouble("right")!=motorright || innerstate.getStateDouble("left")!=motorleft){
//			 logger.debug("motorleft : "+motorleft);
//			 logger.debug("motorright : "+motorright);
//			 logger.debug("orientation :"+Math.toDegrees(alpha.getIncomingActivation()*2.0*Math.PI));
//		}
		
		manipulator.setGateActivation(GT_Motorleft_Value,motorleft);
		manipulator.setGateActivation(GT_Motorright_Value,motorright);
	
//		innerstate.setState("right",motorright);
//		innerstate.setState("left",motorleft);
		


	}


}
