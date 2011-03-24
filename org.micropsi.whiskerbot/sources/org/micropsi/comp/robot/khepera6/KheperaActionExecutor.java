/*
 * Created on 24.03.2005
 *
 */
package org.micropsi.comp.robot.khepera6;

import org.apache.log4j.Logger;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.comp.messages.MAction;
import org.micropsi.comp.messages.MActionResponse;
import org.micropsi.comp.messages.MPerceptionValue;
import org.micropsi.comp.robot.RobotActionExecutor;
import org.micropsi.comp.robot.khepera6.conserv.QTypeExternalPositioning;
import org.micropsi.comp.robot.khepera6.conserv.QTypeReset;
import org.micropsi.comp.robot.khepera6.conserv.QTypeTalkative;

/**
 * @author Uni WLAN
 *
 * created first in project org.micropsi.whiskerbot
 * 
 * second version with bodyProperties for sensor data instead of percepts
 */
public class KheperaActionExecutor extends RobotActionExecutor {
  
  //user: set true if you want to see logger messages:
  private boolean debug = false; //user: choose to switch logger for robot component on|off!!!
  
  private Logger logger;
  private String lastActionType = "";
  private String lastActionTypeHelp = "";
  private String p0_tmp = "1";
  private String p1_tmp = "1";
  private long start,stop,global_stop,start_prox,stop_prox,start_light,stop_light;
  private long start_speed,stop_speed,start_LED0,stop_LED0,start_LED1,stop_LED1; 
  private long global_start = 0;
  private boolean activateSensors = true;
  private boolean rememberRestart = false; //reestablishing motor signal from stopped nodenet
  
  //private MPerceptionValue bodyPropertyChange;
  private MPerceptionValue bodyPropertyChange1 = new MPerceptionValue();
  private MPerceptionValue bodyPropertyChange2 = new MPerceptionValue();
  private MPerceptionValue bodyPropertyChange3 = new MPerceptionValue();
  private MPerceptionValue bodyPropertyChange4 = new MPerceptionValue();
  private MPerceptionValue bodyPropertyChange5 = new MPerceptionValue();
  private MPerceptionValue bodyPropertyChange6 = new MPerceptionValue();
  private MPerceptionValue bodyPropertyChange7 = new MPerceptionValue();
  private MPerceptionValue bodyPropertyChange8 = new MPerceptionValue();
  private MPerceptionValue bodyPropertyChange9 = new MPerceptionValue();
  private MPerceptionValue bodyPropertyChange10 = new MPerceptionValue();
  private MPerceptionValue bodyPropertyChange11 = new MPerceptionValue();
  private MPerceptionValue bodyPropertyChange12 = new MPerceptionValue();
  private MPerceptionValue bodyPropertyChange13 = new MPerceptionValue();
  private MPerceptionValue bodyPropertyChange14 = new MPerceptionValue();
  private MPerceptionValue bodyPropertyChange15 = new MPerceptionValue();
  private MPerceptionValue bodyPropertyChange16 = new MPerceptionValue();
  private MPerceptionValue bodyPropertyChange17 = new MPerceptionValue();
  private MPerceptionValue bodyPropertyChange18 = new MPerceptionValue();
  private MPerceptionValue bodyPropertyChange19 = new MPerceptionValue();   
  private MPerceptionValue bodyPropertyChange20 = new MPerceptionValue();   
   
  private KheperaManager comPort;
  private boolean change;
  private boolean useExternalWSIMPositioning = false;
  
  private Createsocket socket;
  
  public KheperaActionExecutor() {}
  
  private QTypeReset resetQuestion = new QTypeReset();
  
  /* (non-Javadoc)
   * @see org.micropsi.comp.robot.RobotActionExecutor#executeAction(org.micropsi.comp.messages.MAction)
   */
  public MActionResponse executeAction(MAction action) {
    
    // TODO Implementieren. Hier muss schlicht die action, die im Agenten gebaut wurde,
    // decodiert und konkret an die Hardware geschickt werden. Das hier ist der Ort,
    // wo wirklich die Aktorik der Hardware angesprochen werden muss. Beispiel siehe unten
    
    logger  = getLogger();
    comPort = KheperaManager.getInstance(logger, debug); //muss das hier stehen oder besser konstruktor?
    resetQuestion.setKhepera(comPort.khepera);
    
    // socket read and write
    if(useExternalWSIMPositioning) {
        socket = Createsocket.sharedinstance(logger);
	    socket.send("Data");
	    String rec = socket.read();
	    
	    logger.debug("rec: "+rec);
	    
	    String[] data = rec.split(",");
	    if(data.length != 3) {
	    	logger.warn("Received illegal data packet from WSIM: "+rec);
	    } else {
	    	comPort.khepera.setPositionX(data[0]);
	    	comPort.khepera.setPositionY(data[1]);
	    	comPort.khepera.setOrientation(data[2]);
	    }
    }
    
    MActionResponse resp = new MActionResponse(getConnectedAgent(),1.0,action.getTicket());	
    resp.setSuccess(1.0);
    
    activateSensors = true;
//  ===========================================================================================
//  STOP action: execute only if parameters are different from previous 
//  OR if STOP action was last executed (the STOP action term allows for execution
//  even if parameters have not changed)
//  ===========================================================================================
    
    if( (action.getActionType().equals("STOP")) && !(lastActionType.equals("STOP")) ) {
      
      if(debug) logger.debug("executeAction: STOP | ticket "+action.getTicket());
      
      /*try {
       KheperaManager.getInstance(logger).update.pause(true);
       } catch (RuntimeException e) {
       logger.error("Exc: ",e);
       }*/
      rememberRestart = true;
      comPort.khepera.stop();
      
      /*try {
       KheperaManager.getInstance(logger).update.pause(false);
       } catch (RuntimeException e) {
       logger.error("Exc: ",e);
       }*/
      
      lastActionTypeHelp = "STOP";
      activateSensors = false;
    }
    
    
//  ===========================================================================================
//  MOVE action: execute only if parameters are different from previous 
//  OR if STOP action was last executed (the STOP action term allows for execution
//  even if parameters have not changed)
//  ===========================================================================================
    if( (action.getActionType().equals("MOVE")) && 
        (!(action.getParameter(0).equals(p0_tmp) && action.getParameter(1).equals(p1_tmp)) || 
            (lastActionType.equals("STOP"))) ){
      
      if(debug) logger.warn(action.getActionType().equals("MOVE")+"|"+action.getParameter(0).equals(p0_tmp)+
          "|"+action.getParameter(1).equals(p1_tmp)+" | "+action.getParameter(0)+"|"+p0_tmp+"||"+
          action.getParameter(1)+"|"+p1_tmp+"| ticket "+action.getTicket());
      
      if(debug) logger.debug("executeAction: MOVE ("+action.getParameter(0)+" | "+action.getParameter(1)+")");
      
      /*try {
       KheperaManager.getInstance(logger).update.pause(true);
       } catch (RuntimeException e) {
       logger.error("Exc: ",e);
       }*/
      
      start_speed = System.currentTimeMillis();
      
      if (lastActionType.equals("STOP") || rememberRestart){ ///establish old values after restart from stopped nodenet
        logger.debug("(Motor) restart..."+p0_tmp+","+p1_tmp);
        //comPort.khepera.restart();
        if ((Integer.valueOf(p0_tmp) == 0)&&(Integer.valueOf(p1_tmp)==0)){
          logger.debug("(Motor) new restarting values: -1,1");
          comPort.khepera.setSpeed_motor("-1","1");
        }
        else {
          logger.debug("(Motor) reestablishing old values: "+p0_tmp+","+p1_tmp);
          comPort.khepera.setSpeed_motor(p0_tmp, p1_tmp);
        }
        rememberRestart = false;
      }
      else   
        comPort.khepera.setSpeed_motor(action.getParameter(0),action.getParameter(1));
      
      stop_speed = System.currentTimeMillis();
      
      if(debug) logger.debug("an robot: OK!");
      
      /*try {
       KheperaManager.getInstance(logger).update.pause(false);
       } catch (RuntimeException e) {
       logger.error("Exc: ",e);
       }
       */
      p0_tmp = action.getParameter(0);
      p1_tmp = action.getParameter(1);
      lastActionTypeHelp = "MOVE";
      activateSensors = false;
    }
    
//  ===========================================================================================
//  LED action: 
//  ===========================================================================================
    
    if( (action.getActionType().equals("LED")) && !(lastActionType.equals("LED")) )  {
      
      if(debug) logger.debug("executeAction: LED ("+action.getParameter(0)+" | "+action.getParameter(1)
          +" | ticket "+action.getTicket()+")");
      /*try {
       KheperaManager.getInstance(logger).update.pause(true);
       } catch (RuntimeException e) {
       logger.error("Exc: ",e);
       }*/
      start_LED0 = System.currentTimeMillis();
      comPort.khepera.setLED0(action.getParameter(0));
      stop_LED0 = System.currentTimeMillis();
      
      start_LED1 = System.currentTimeMillis();
      comPort.khepera.setLED1(action.getParameter(1));
      stop_LED1 = System.currentTimeMillis();
      
      /*try {
       KheperaManager.getInstance(logger).update.pause(false);
       } catch (RuntimeException e) {
       logger.error("Exc: ",e);
       }*/
      
      lastActionTypeHelp = "LED";
      activateSensors = false;	
    }	
    
    
//  ===========================================================================================
//  bodyPropertyChanges: executed all the time! 
//  ===========================================================================================
    
    if (!(lastActionType.equals(lastActionTypeHelp))) {
      lastActionType = lastActionTypeHelp;
      logger.warn("lastActionType = "+lastActionType);
    }
    
    //start = System.currentTimeMillis();
    if(debug) logger.debug("sensors ticket "+action.getTicket());
    
    
//    start_prox = System.currentTimeMillis();
//    comPort.khepera.getProximitySensors();
//    stop_prox = System.currentTimeMillis();
    
    if (activateSensors) {
      if(change){
        start_prox = System.currentTimeMillis();
        comPort.khepera.getProximitySensors();
        stop_prox = System.currentTimeMillis();
      }
      else{
        start_light = System.currentTimeMillis();
        comPort.khepera.getAmbientLightSensors();
        stop_light = System.currentTimeMillis();
      }
      change = !(change);
    }
     
    //MPerceptionValue bodyPropertyChange1 = new MPerceptionValue();
    bodyPropertyChange1.setKey("PROX_SENS_1");
    bodyPropertyChange1.setValue(comPort.khepera.getPs1());
    resp.addBodyPropertyChange(bodyPropertyChange1);
    
    //MPerceptionValue bodyPropertyChange2 = new MPerceptionValue();
    bodyPropertyChange2.setKey("PROX_SENS_2");
    bodyPropertyChange2.setValue(comPort.khepera.getPs2());
    resp.addBodyPropertyChange(bodyPropertyChange2);
    //logger.debug("P2: Key "+bodyPropertyChange2.getKey()+" | Value "+ bodyPropertyChange2.getValue());
    
    //MPerceptionValue bodyPropertyChange3 = new MPerceptionValue();
    bodyPropertyChange3.setKey("PROX_SENS_3");
    bodyPropertyChange3.setValue(comPort.khepera.getPs3());
    resp.addBodyPropertyChange(bodyPropertyChange3);
    //logger.debug("P3: Key "+bodyPropertyChange3.getKey()+" | Value "+ bodyPropertyChange3.getValue());
    
    //MPerceptionValue bodyPropertyChange4 = new MPerceptionValue();
    bodyPropertyChange4.setKey("PROX_SENS_4");
    bodyPropertyChange4.setValue(comPort.khepera.getPs4());
    resp.addBodyPropertyChange(bodyPropertyChange4);
    
    //MPerceptionValue bodyPropertyChange5 = new MPerceptionValue();
    bodyPropertyChange5.setKey("PROX_SENS_5");
    bodyPropertyChange5.setValue(comPort.khepera.getPs5());
    resp.addBodyPropertyChange(bodyPropertyChange5);
    
    //MPerceptionValue bodyPropertyChange6 = new MPerceptionValue();
    bodyPropertyChange6.setKey("PROX_SENS_6");
    bodyPropertyChange6.setValue(comPort.khepera.getPs6());
    resp.addBodyPropertyChange(bodyPropertyChange6);
    
    //MPerceptionValue bodyPropertyChange7 = new MPerceptionValue();
    bodyPropertyChange7.setKey("PROX_SENS_7");
    bodyPropertyChange7.setValue(comPort.khepera.getPs7());
    resp.addBodyPropertyChange(bodyPropertyChange7);
    
    //MPerceptionValue bodyPropertyChange8 = new MPerceptionValue();
    bodyPropertyChange8.setKey("PROX_SENS_8");
    bodyPropertyChange8.setValue(comPort.khepera.getPs8());
    resp.addBodyPropertyChange(bodyPropertyChange8);
    
    
    //MPerceptionValue bodyPropertyChange9 = new MPerceptionValue();
    bodyPropertyChange9.setKey("LIGHT_SENS_1");
    bodyPropertyChange9.setValue(comPort.khepera.getLs1());
    resp.addBodyPropertyChange(bodyPropertyChange9);
    
    //MPerceptionValue bodyPropertyChange10 = new MPerceptionValue();
    bodyPropertyChange10.setKey("LIGHT_SENS_2");
    bodyPropertyChange10.setValue(comPort.khepera.getLs2());
    resp.addBodyPropertyChange(bodyPropertyChange10);
    
    //MPerceptionValue bodyPropertyChange11 = new MPerceptionValue();
    bodyPropertyChange11.setKey("LIGHT_SENS_3");
    bodyPropertyChange11.setValue(comPort.khepera.getLs3());
    resp.addBodyPropertyChange(bodyPropertyChange11);
    
    //MPerceptionValue bodyPropertyChange12 = new MPerceptionValue();
    bodyPropertyChange12.setKey("LIGHT_SENS_4");
    bodyPropertyChange12.setValue(comPort.khepera.getLs4());
    resp.addBodyPropertyChange(bodyPropertyChange12);
    
    //MPerceptionValue bodyPropertyChange13 = new MPerceptionValue();
    bodyPropertyChange13.setKey("LIGHT_SENS_5");
    bodyPropertyChange13.setValue(comPort.khepera.getLs5());
    resp.addBodyPropertyChange(bodyPropertyChange13);
    
    //MPerceptionValue bodyPropertyChange14 = new MPerceptionValue();
    bodyPropertyChange14.setKey("LIGHT_SENS_6");
    bodyPropertyChange14.setValue(comPort.khepera.getLs6());
    resp.addBodyPropertyChange(bodyPropertyChange14);
    
    //MPerceptionValue bodyPropertyChange15 = new MPerceptionValue();
    bodyPropertyChange15.setKey("LIGHT_SENS_7");
    bodyPropertyChange15.setValue(comPort.khepera.getLs7());
    resp.addBodyPropertyChange(bodyPropertyChange15);
    
    //MPerceptionValue bodyPropertyChange16 = new MPerceptionValue();
    bodyPropertyChange16.setKey("LIGHT_SENS_8");
    bodyPropertyChange16.setValue(comPort.khepera.getLs8());
    resp.addBodyPropertyChange(bodyPropertyChange16);
   
    bodyPropertyChange17.setKey("POSITION_X");
    bodyPropertyChange17.setValue(comPort.khepera.getPositionX());
    resp.addBodyPropertyChange(bodyPropertyChange17);
    
    bodyPropertyChange18.setKey("POSITION_Y");
    bodyPropertyChange18.setValue(comPort.khepera.getPositionY());
    resp.addBodyPropertyChange(bodyPropertyChange18);
    
    bodyPropertyChange19.setKey("ORIENTATION");
    bodyPropertyChange19.setValue(comPort.khepera.getOrientation());
    resp.addBodyPropertyChange(bodyPropertyChange19);
    
    
    /*	logger.debug("executeAction.bodyProperty" +
     "Prox1:"+bodyPropertyChange1.getValue()+
     " | Light1:"+bodyPropertyChange9.getValue());
     */	
    
    /*bodyPropertyChange.setKey("speed_motor_left");
     bodyPropertyChange.setValue(comPort.khepera.getSpeed_motor_left());
     resp.addBodyPropertyChange(bodyPropertyChange);
     bodyPropertyChange.setKey("speed_motor_right");
     bodyPropertyChange.setValue(comPort.khepera.getSpeed_motor_right());
     resp.addBodyPropertyChange(bodyPropertyChange);		
     */
    
    
    /*else if(action.getActionType().equals("drink")) {
     etc pp, was immer Du für aktionen hast.
     Du musst übrigens nur den Success setzen, um body property changes musst Du Dich
     nicht scheren, wenn Du nicht magst.
     }	
     if(action.getActionType().equals("eat")) {
     // nehmen wir mal an, essen klappt immer.
      resp.setSuccess(1.0);
      
      // und als Ergebnis: Mehr im Magen!
       MPerceptionValue bodyPropertyChange = new MPerceptionValue();
       bodyPropertyChange.setKey("stomach content");
       bodyPropertyChange.setValue("+1");
       resp.addBodyPropertyChange(bodyPropertyChange);
       } else if(action.getActionType().equals("drink")) {
       // etc pp, was immer Du für aktionen hast.
        // Du musst Ÿbrigens nur den Success setzen, um body property changes musst Du Dich
         // nicht scheren, wenn Du nicht magst.
          }
          */
    resp.setAgentName(getConnectedAgent());
    
    if(debug){
      stop = System.currentTimeMillis();
      logger.debug("=========================");
      logger.debug("p:"+(stop_prox - start_prox)+" l:"+(stop_light - start_light)+" s:"+(stop_speed - start_speed)+" l:"+(stop_LED0 - start_LED0)+" l:"+(stop_LED1 - start_LED1)+" ms");
      logger.debug("executeAction: "+(stop-start)+" ms");
      global_stop = System.currentTimeMillis();		
      logger.debug("CycleTime: "+(global_stop-global_start)+" ms");
      global_start = System.currentTimeMillis();
      logger.debug("=========================");
      
      stop=-1;start=1;stop_prox=-1;start_prox=1;stop_speed=-1;start_speed=1;
      stop_LED0=-1;start_LED0=1;stop_LED1=-1;start_LED1=1;start_light=1;stop_light=-1;
    }
    
    return resp;
  }
  
  /* (non-Javadoc)
   * @see org.micropsi.comp.robot.RobotActionExecutor#tick(long)
   */	
  public void tick(long simstep) {
    
  }
  
  /* (non-Javadoc)
   * @see org.micropsi.comp.robot.RobotActionExecutor#getConsoleQuestionContributions()
   */
  public ConsoleQuestionTypeIF[] getConsoleQuestionContributions() {
	  ConsoleQuestionTypeIF[] types = new ConsoleQuestionTypeIF[] {
		resetQuestion,
		new QTypeExternalPositioning(this),
		new QTypeTalkative(this)
	  };
	  
    return types;
  }
  
  public void setEnabledExternalPositioning(boolean enabled) {
	useExternalWSIMPositioning = enabled;
  }

  public void setTalkative(boolean enabled) {
    debug = enabled;
  }
  
  /* (non-Javadoc)
   * @see org.micropsi.comp.robot.RobotActionExecutor#shutdown()
   */
  public void shutdown() {
    // TODO Auto-generated method stub
  }
}
