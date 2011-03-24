/*
 * Created on 24.03.2005
 *
 */
package org.micropsi.comp.agent.kheperaTurtle5;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.agent.AgentIF;
import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.agent.aaa.AgentControllerIF;
import org.micropsi.comp.agent.aaa.AgentWorldAdapterIF;
import org.micropsi.comp.agent.aaa.PerceptTranslatorIF;
import org.micropsi.comp.agent.aaa.UrgeCreatorIF;
import org.micropsi.comp.agent.kheperaTurtle5.OutputFunctions.LargereqalT1smallerthant2;
import org.micropsi.comp.agent.kheperaTurtle5.OutputFunctions.OFRandomSignal;
import org.micropsi.comp.agent.kheperaTurtle5.OutputFunctions.OFSensorDistanceContinuous2;
import org.micropsi.comp.agent.kheperaTurtle5.OutputFunctions.OFSensorDistanceDiscrete;
import org.micropsi.comp.agent.kheperaTurtle5.OutputFunctions.OFSensorDistanceContinuous;
import org.micropsi.comp.agent.kheperaTurtle5.OutputFunctions.ValuesInRange;
import org.micropsi.comp.agent.kheperaTurtle5.conserv.QTypeGetMemoryPoints;
import org.micropsi.comp.agent.kheperaTurtle5.conserv.QTypeTalkative;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.comp.messages.MPerceptionValue;
import org.micropsi.nodenet.GateOutputFunctions;
import org.micropsi.nodenet.LocalNetFacade;

/**
 * @author Uni WLAN
 *
 * created first in project org.micropsi.whiskerbot
 * 
 * second version with bodyProperties and urges instead og percepts
 */
public class KheperaWorldAdapter implements AgentWorldAdapterIF,
AgentControllerIF {
  
  
//=================================================================
//register custom OutputFunctions:
//================================================================= 
  static {
    GateOutputFunctions.registerOutputFunction(OFSensorDistanceDiscrete.class);
    GateOutputFunctions.registerOutputFunction(OFSensorDistanceContinuous.class);
    GateOutputFunctions.registerOutputFunction(OFSensorDistanceContinuous2.class);
    GateOutputFunctions.registerOutputFunction(OFRandomSignal.class);
    GateOutputFunctions.registerOutputFunction(ValuesInRange.class);
    GateOutputFunctions.registerOutputFunction(LargereqalT1smallerthant2.class);
  }
//=================================================================
  
  private boolean debug = false;  /// testing option: true=logger output for agent component
  private MicroPsiAgent micropsi;
  //private KheperaMovementActionTL movement;
  //private KheperaStopActionTL stop;
  private Hashtable urges = new Hashtable();
  private Logger logger;
  
  
  public void initialize(AgentIF agent, Logger logger) throws MicropsiException {
    this.logger = logger;
    this.logger.debug("Initializing KheperaWorldAdapter...");
    
    micropsi = (MicroPsiAgent)agent;
    
    micropsi.registerAdditionalQuestionType(new QTypeTalkative(this));
    micropsi.registerAdditionalQuestionType(new QTypeGetMemoryPoints(micropsi,logger));
    
    //movement = new KheperaMovementActionTL((LocalNetFacade)micropsi.getNet(),logger);
    //stop = new KheperaStopActionTL((LocalNetFacade)micropsi.getNet(),logger);
    
    //urges = new HashMap();
    
    KheperaProximityUrge pUrge1 = new KheperaProximityUrge("PROX_SENS_1", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(pUrge1);
    urges.put("PROX_SENS_1",pUrge1);		
    
    KheperaProximityUrge pUrge2 = new KheperaProximityUrge("PROX_SENS_2", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(pUrge2);
    urges.put("PROX_SENS_2",pUrge2);		
    
    KheperaProximityUrge pUrge3 = new KheperaProximityUrge("PROX_SENS_3", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(pUrge3);
    urges.put("PROX_SENS_3",pUrge3);		
    
    KheperaProximityUrge pUrge4 = new KheperaProximityUrge("PROX_SENS_4", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(pUrge4);
    urges.put("PROX_SENS_4",pUrge4);		
    
    KheperaProximityUrge pUrge5 = new KheperaProximityUrge("PROX_SENS_5", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(pUrge5);
    urges.put("PROX_SENS_5",pUrge5);		
    
    KheperaProximityUrge pUrge6 = new KheperaProximityUrge("PROX_SENS_6", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(pUrge6);
    urges.put("PROX_SENS_6",pUrge6);		
    
    KheperaProximityUrge pUrge7 = new KheperaProximityUrge("PROX_SENS_7", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(pUrge7);
    urges.put("PROX_SENS_7",pUrge7);		
    
    KheperaProximityUrge pUrge8 = new KheperaProximityUrge("PROX_SENS_8", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(pUrge8);
    urges.put("PROX_SENS_8",pUrge8);		
    
    
    
    KheperaLightUrge lUrge1 = new KheperaLightUrge("LIGHT_SENS_1", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(lUrge1);
    urges.put("LIGHT_SENS_1",lUrge1);
    
    KheperaLightUrge lUrge2 = new KheperaLightUrge("LIGHT_SENS_2", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(lUrge2);
    urges.put("LIGHT_SENS_2",lUrge2);
    
    KheperaLightUrge lUrge3 = new KheperaLightUrge("LIGHT_SENS_3", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(lUrge3);
    urges.put("LIGHT_SENS_3",lUrge3);
    
    KheperaLightUrge lUrge4 = new KheperaLightUrge("LIGHT_SENS_4", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(lUrge4);
    urges.put("LIGHT_SENS_4",lUrge4);
    
    KheperaLightUrge lUrge5 = new KheperaLightUrge("LIGHT_SENS_5", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(lUrge5);
    urges.put("LIGHT_SENS_5",lUrge5);
    
    KheperaLightUrge lUrge6 = new KheperaLightUrge("LIGHT_SENS_6", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(lUrge6);
    urges.put("LIGHT_SENS_6",lUrge6);
    
    KheperaLightUrge lUrge7 = new KheperaLightUrge("LIGHT_SENS_7", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(lUrge7);
    urges.put("LIGHT_SENS_7",lUrge7);	
    
    KheperaLightUrge lUrge8 = new KheperaLightUrge("LIGHT_SENS_8", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(lUrge8);
    urges.put("LIGHT_SENS_8",lUrge8);    
    
    
    
    KheperaPositionUrge xUrge = new KheperaPositionUrge("POSITION_X", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(xUrge);
    urges.put("POSITION_X",xUrge);    
    
    KheperaPositionUrge yUrge = new KheperaPositionUrge("POSITION_Y", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(yUrge);
    urges.put("POSITION_Y",yUrge);   
    
    KheperaOrientationUrge orientationUrge = new KheperaOrientationUrge("ORIENTATION", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(orientationUrge);
    urges.put("ORIENTATION",orientationUrge);  
    
    KheperaColorUrge redUrge = new KheperaColorUrge("REDINTENSITY", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(redUrge);
    urges.put("REDINTENSITY",redUrge);  
    
    KheperaColorUrge greenUrge = new KheperaColorUrge("GREENINTENSITY", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(greenUrge);
    urges.put("GREENINTENSITY",greenUrge);   
    
    KheperaColorUrge blueUrge = new KheperaColorUrge("BLUEINTENSITY", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(blueUrge);
    urges.put("BLUEINTENSITY",blueUrge);   
    
    KheperaColorUrge yellowUrge = new KheperaColorUrge("YELLOWINTENSITY", logger, debug);
    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(yellowUrge);
    urges.put("YELLOWINTENSITY",yellowUrge);   
    
//    KheperaFoodUrge foodUrge = new KheperaFoodUrge("FOOD", logger, debug);
//    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(foodUrge);
//    urges.put("FOOD",foodUrge);   
//    
//    KheperaWaterUrge waterUrge = new KheperaWaterUrge("WATER", logger, debug);
//    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(waterUrge);
//    urges.put("WATER",waterUrge);   
//  
//    KheperaObstacleUrge obstacleUrge = new KheperaObstacleUrge("OBSTACLE", logger, debug);
//    ((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(obstacleUrge);
//    urges.put("OBSTACLE",obstacleUrge);   
  

  }
  
  
  public AgentControllerIF createController() {
    return this;
  }
  
  public ActionTranslatorIF[] createActionTranslators() {	
    
    ActionTranslatorIF[] desc = new ActionTranslatorIF[3];   
    desc[0] = new KheperaMovementActionTL((LocalNetFacade)micropsi.getNet(),logger,debug);
    desc[1] = new KheperaStopActionTL((LocalNetFacade)micropsi.getNet(),logger,debug);
    desc[2] = new KheperaLEDActionTL((LocalNetFacade)micropsi.getNet(),logger,debug);
    
    return desc;
  }
  
  public PerceptTranslatorIF[] createPerceptTranslators() {
    return new PerceptTranslatorIF[]{};
  }
  
  public UrgeCreatorIF[] createUrgeCreators() {
    
    UrgeCreatorIF[] out = new UrgeCreatorIF[urges.size()];
    Iterator urgeIterator = urges.values().iterator();
    if(debug) logger.debug("createUrgeCreators: urges.size="+urges.size());
    for(int i=0;i<urges.size();i++) {
      out[i] = (UrgeCreatorIF)urgeIterator.next();
    }
    
    return out;		
  }
  
  public boolean wantsPerception() {
    return true;
  }
  
  /// leo: wird ueber AgentControlerIF von agentenobjekt aufgerufen
  public void receiveBodyPropertyChanges(ArrayList propertyChanges) {
    
    if(debug) logger.debug("begin receiving BPCs...");
    
    try{
      
      ///leo: the first eight items in propertyChanges are the proximity sensors:
      for(int i=0;i<=7;i++) {
        MPerceptionValue tmp = (MPerceptionValue)propertyChanges.get(i);
        KheperaProximityUrge urge = (KheperaProximityUrge)urges.get(tmp.getKey());
        
        try {
          urge.setSignalStrength(Double.parseDouble(tmp.getValue()));
        } catch (RuntimeException e1) {
          logger.error("ProximityUrge exception!",e1);
        }
      }
      
      /// leo: the 2nd eight items in propertyChanges are the ambient light sensors:
      for(int i=8;i<=15;i++) {
        MPerceptionValue tmp = (MPerceptionValue)propertyChanges.get(i);
        KheperaLightUrge urge = (KheperaLightUrge)urges.get(tmp.getKey());
        
        try {
          urge.setSignalStrength(Double.parseDouble(tmp.getValue()));
        } catch (RuntimeException e1) {
          logger.error("LightUrge exception2!",e1);
        }
      }
      
      /// leo: the 3rd three items in propertyChanges are the positions X,Y and direction sensors:
      {int i = 16;  
        MPerceptionValue tmp = (MPerceptionValue)propertyChanges.get(i);
        KheperaPositionUrge urge = (KheperaPositionUrge)urges.get(tmp.getKey());
//        KheperaFoodUrge    	urge2 = (KheperaFoodUrge)urges.get("FOOD");
//        KheperaWaterUrge   	urge3 = (KheperaWaterUrge)urges.get("WATER");
//        KheperaObstacleUrge urge4 = (KheperaObstacleUrge)urges.get("OBSTACLE");
        
        try {
          double value = Double.parseDouble(tmp.getValue());
          urge.setSignalStrength(value);
//          urge2.setXSignalStrength(value);
//          urge3.setXSignalStrength(value);
//          urge4.setXSignalStrength(value);
        } catch (RuntimeException e1) {
          logger.error("PositionUrge exception!",e1);
        }
      }

    {int i = 17;  
      MPerceptionValue tmp = (MPerceptionValue)propertyChanges.get(i);
      KheperaPositionUrge urge = (KheperaPositionUrge)urges.get(tmp.getKey());
//      KheperaFoodUrge	  urge2 = (KheperaFoodUrge)urges.get("FOOD");
//      KheperaWaterUrge    urge3 = (KheperaWaterUrge)urges.get("WATER");
//      KheperaObstacleUrge urge4 = (KheperaObstacleUrge)urges.get("OBSTACLE");
      
      try {
    	    double value = Double.parseDouble(tmp.getValue());
            urge.setSignalStrength(value);
//            urge2.setYSignalStrength(value);
//            urge3.setYSignalStrength(value);
//            urge4.setYSignalStrength(value);
      } catch (RuntimeException e1) {
        logger.error("PositionUrge exception!",e1);
      }
    
     }
    
        /// leo: the 4th single item in propertyChanges is the orientation sensor:
	{int i = 18;
		MPerceptionValue tmp = (MPerceptionValue) propertyChanges.get(i);
		KheperaOrientationUrge urge = (KheperaOrientationUrge) urges.get(tmp.getKey());

	try {
		urge.setSignalStrength(Double.parseDouble(tmp.getValue()));
		} catch (RuntimeException e1) {
			logger.error("OrientationUrge exception!", e1);
		}
	}
		
		
		
		for(int i= 19;i<23;i++){
			MPerceptionValue tmp = (MPerceptionValue) propertyChanges.get(i);
			KheperaColorUrge urge = (KheperaColorUrge) urges.get(tmp.getKey());

			try {
				urge.setSignalStrength(Double.parseDouble(tmp.getValue()));
			} catch (RuntimeException e1) {
				logger.error("COLORUrge exception!", e1);
			}
		}
		
		
    } catch (Exception e){
      logger.debug("receiveBodyPropertyChanges not evaluated");
    }
    
    if(debug) logger.debug("...ended receiving BPCs");
    
  }
  
  public void setTalkative(boolean enabled) {
	  debug = enabled;
  }
  
  /* (non-Javadoc)
   * @see org.micropsi.comp.agent.aaa.AgentControllerIF#notifyOfPerception()
   */
  public void notifyOfPerception() {
  }
  
  /* (non-Javadoc)
   * @see org.micropsi.comp.agent.aaa.AgentControllerIF#notifyOfAction()
   */
  public void notifyOfAction() {   
  }
  
  /* (non-Javadoc)
   * @see org.micropsi.comp.agent.aaa.AgentControllerIF#notifyOfActionResult(java.lang.String, double)
   */
  public void notifyOfActionResult(String arg0, double arg1) {
    //logger.debug("notifyOfActionResult: "+arg0+" = "+arg1);
  }
  
  /* (non-Javadoc)
   * @see org.micropsi.comp.agent.aaa.AgentControllerIF#shutdown()
   */
  public void shutdown() {
  }
  
}
