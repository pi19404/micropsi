/*
 * Created on 24.03.2005
 *
 */
package org.micropsi.comp.agent.kheperaTurtle2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.agent.AgentIF;
import org.micropsi.comp.agent.aaa.ActionTranslatorIF;
import org.micropsi.comp.agent.aaa.AgentControllerIF;
import org.micropsi.comp.agent.aaa.AgentWorldAdapterIF;
import org.micropsi.comp.agent.aaa.PerceptTranslatorIF;
import org.micropsi.comp.agent.aaa.UrgeCreatorIF;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
//import org.micropsi.comp.agent.turtle.MovementAction;
import org.micropsi.comp.messages.MPerceptionValue;
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

	private MicroPsiAgent micropsi;
	private KheperaMovementActionTL movement;
	private HashMap urges;
	private Logger logger;

	
	public void initialize(AgentIF agent, Logger logger) throws MicropsiException {
	/*	if(agent.getAgentType() != AgentTypesIF.AGENT_MICROPSI_TURTLE)
			throw new MicropsiException(500,Integer.toString(agent.getAgentType()));*/
		this.logger = logger;
		this.logger.debug("Initializing KheperaWorldAdapter...");
		
		micropsi = (MicroPsiAgent)agent;
		movement = new KheperaMovementActionTL((LocalNetFacade)micropsi.getNet(),logger);
	
		urges = new HashMap();
		
		//gibt namen im sensor menue an:
		/*
		//KheperaProximityUrge pUrge1 = new KheperaProximityUrge("prox_phalanx_left");
		KheperaProximityUrge pUrge1 = new KheperaProximityUrge("PROX_PHALANX_LEFT");
		//urgetypen in urges-hashmap registrieren:
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(pUrge1);
		urges.put("PROX_PHALANX_LEFT",pUrge1);
		KheperaProximityUrge pUrge2 = new KheperaProximityUrge("PROX_PHALANX_RIGHT");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(pUrge2);
		urges.put("PROX_PHALANX_RIGHT",pUrge2);		
		*/

		KheperaProximityUrge pUrge1 = new KheperaProximityUrge("PROX_SENS_1");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(pUrge1);
		urges.put("PROX_SENS_1",pUrge1);		
		
		KheperaProximityUrge pUrge2 = new KheperaProximityUrge("PROX_SENS_2");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(pUrge2);
		urges.put("PROX_SENS_2",pUrge2);		
		
		KheperaProximityUrge pUrge3 = new KheperaProximityUrge("PROX_SENS_3");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(pUrge3);
		urges.put("PROX_SENS_3",pUrge3);		
		
		KheperaProximityUrge pUrge4 = new KheperaProximityUrge("PROX_SENS_4");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(pUrge4);
		urges.put("PROX_SENS_4",pUrge4);		
		
		KheperaProximityUrge pUrge5 = new KheperaProximityUrge("PROX_SENS_5");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(pUrge5);
		urges.put("PROX_SENS_5",pUrge5);		
		
		KheperaProximityUrge pUrge6 = new KheperaProximityUrge("PROX_SENS_6");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(pUrge6);
		urges.put("PROX_SENS_6",pUrge6);		
		
		KheperaProximityUrge pUrge7 = new KheperaProximityUrge("PROX_SENS_7");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(pUrge7);
		urges.put("PROX_SENS_7",pUrge7);		
		
		KheperaProximityUrge pUrge8 = new KheperaProximityUrge("PROX_SENS_8");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(pUrge8);
		urges.put("PROX_SENS_8",pUrge8);		

	
		
		KheperaLightUrge lUrge1 = new KheperaLightUrge("LIGHT_SENS_1");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(lUrge1);
		urges.put("LIGHT_SENS_1",lUrge1);		
		
		KheperaLightUrge lUrge2 = new KheperaLightUrge("LIGHT_SENS_2");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(lUrge2);
		urges.put("LIGHT_SENS_2",lUrge2);		
		
		KheperaLightUrge lUrge3 = new KheperaLightUrge("LIGHT_SENS_3");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(lUrge3);
		urges.put("LIGHT_SENS_3",lUrge3);		
		
		KheperaLightUrge lUrge4 = new KheperaLightUrge("LIGHT_SENS_4");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(lUrge4);
		urges.put("LIGHT_SENS_4",lUrge4);		
		
		KheperaLightUrge lUrge5 = new KheperaLightUrge("LIGHT_SENS_5");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(lUrge5);
		urges.put("LIGHT_SENS_5",lUrge5);		
		
		KheperaLightUrge lUrge6 = new KheperaLightUrge("LIGHT_SENS_6");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(lUrge6);
		urges.put("LIGHT_SENS_6",lUrge6);		
		
		KheperaLightUrge lUrge7 = new KheperaLightUrge("LIGHT_SENS_7");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(lUrge7);
		urges.put("LIGHT_SENS_7",lUrge7);		
		
		KheperaLightUrge lUrge8 = new KheperaLightUrge("LIGHT_SENS_8");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(lUrge8);
		urges.put("LIGHT_SENS_8",lUrge8);		
		
		/*KheperaSpeedUrge tmp2 = new KheperaSpeedUrge("motor_speed_left");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(tmp2);
		urges.put("MOTOR_SPEED_LEFT",tmp2);		
		tmp2 = new KheperaSpeedUrge("motor_speed_right");
		((LocalNetFacade)micropsi.getNet()).getSensorRegistry().registerSensorDataProvider(tmp2);
		urges.put("MOTOR_SPEED_RIGHT",tmp2);		
		*/
	}

	public AgentControllerIF createController() {
		return this;
	}

	public ActionTranslatorIF[] createActionTranslators() {
		return new ActionTranslatorIF[] {movement};
		
		//ActionTranslatorIF[] desc = new ActionTranslatorIF[1];
		
		//desc[0] = new KheperaMovementActionTL((LocalNetFacade)micropsi.getNet(),logger);
	
		//desc[0] = new EatAction(micropsi.getNet(),micropsi,logger);
		//desc[1] = new DrinkAction(micropsi.getNet(),micropsi,logger); 
		//desc[2] = new NoopAction(micropsi.getNet(),logger);
		//desc[3] = new MoveAction(micropsi.getNet(),logger);
		//desc[4] = new FocusAction(micropsi,logger);
		
		//return desc;
	}

	public PerceptTranslatorIF[] createPerceptTranslators() {
		return new PerceptTranslatorIF[]{};
		
		/*PerceptTranslatorIF[] percepts = new PerceptTranslatorIF[1];
		percepts[0] = new KheperaProximityPerceptTL((LocalNetFacade)micropsi.getNet(),micropsi,logger);
		percepts[1] = new KheperaSpeedPerceptTL((LocalNetFacade)micropsi.getNet(),micropsi,logger);
		percepts[3] = new KheperaPositionPerceptTL((LocalNetFacade)micropsi.getNet(),micropsi,logger);
		
		return percepts;
		*/
	}
		
	public UrgeCreatorIF[] createUrgeCreators() {
		
		UrgeCreatorIF[] toReturn = new UrgeCreatorIF[urges.size()];
		Iterator urgeIterator = urges.values().iterator();
		logger.debug("createUrgeCreators: urges.size="+urges.size());
		for(int i=0;i<urges.size();i++) 
			toReturn[i] = (UrgeCreatorIF)urgeIterator.next();
		
		return toReturn;		
	}

	public boolean wantsPerception() {
		return true;
	}
	
	// leo: wird ueber AgentControlerIF von agentenobjekt aufgerufen
	public void receiveBodyPropertyChanges(ArrayList propertyChanges) {
		try{
		//for(int i=0;i<propertyChanges.size();i++) {
		//leo: the first eight items in propertyChanges are the proximity sensors:
			for(int i=0;i<=7;i++) {
				
			//logger.debug("Leo: BPChanges size: "+propertyChanges.size()+" | i="+i);
			
			MPerceptionValue tmp = (MPerceptionValue)propertyChanges.get(i);
			//logger.debug("MPerceptionValue: "+tmp.getKey());
			//logger.debug("urges: "+urges.toString());
			//KheperaSpeedUrge urge = (KheperaSpeedUrge)urges.get(tmp.getKey());
			//urge.setSignalStrength(Double.parseDouble(tmp.getValue()));
			//KheperaSpeedUrge urge = (KheperaSpeedUrge)urges.get(tmp.getKey());
			//urge.setSignalStrength(Double.parseDouble(tmp.getValue()));
			
			KheperaProximityUrge urge = (KheperaProximityUrge)urges.get(tmp.getKey());
			
			/*logger.debug("urge: "+urge.getDataType());
			logger.debug("Leo: BPChanges1 received in agent "+tmp.getKey()+" "+tmp.getValue());
			logger.debug("Leo: "+Double.parseDouble(tmp.getValue()));
			*/
			//urge.setSignalStrength((Double.parseDouble(tmp.getValue()))/1000);
			urge.setSignalStrength(Double.parseDouble(tmp.getValue()));
			//logger.debug("Leo:urgeStrength: "+urge.getSignalStrength());
				
			//logger.debug("Leo: geschafft!");
		}

		//leo: the second eight items in propertyChanges are the ambient light sensors:
		for(int i=8;i<=15;i++) {
			
			//logger.debug("Leo: BPChanges size: "+propertyChanges.size()+" | i="+i);
			
			MPerceptionValue tmp = (MPerceptionValue)propertyChanges.get(i);
			//logger.debug("MPerceptionValue: "+tmp.getKey());
			//logger.debug("urges: "+urges.toString());
			//KheperaSpeedUrge urge = (KheperaSpeedUrge)urges.get(tmp.getKey());
			//urge.setSignalStrength(Double.parseDouble(tmp.getValue()));
			//KheperaSpeedUrge urge = (KheperaSpeedUrge)urges.get(tmp.getKey());
			//urge.setSignalStrength(Double.parseDouble(tmp.getValue()));
			
			KheperaLightUrge urge = (KheperaLightUrge)urges.get(tmp.getKey());
			
			/*logger.debug("urge: "+urge.getDataType());
			logger.debug("Leo: BPChanges1 received in agent "+tmp.getKey()+" "+tmp.getValue());
			logger.debug("Leo: "+Double.parseDouble(tmp.getValue()));
			*/
			//urge.setSignalStrength((Double.parseDouble(tmp.getValue()))/1000);
			urge.setSignalStrength(Double.parseDouble(tmp.getValue()));
			//logger.debug("Leo:urgeStrength: "+urge.getSignalStrength());
				
			//logger.debug("Leo: geschafft!");
		}
		
		} catch (Exception e){
			logger.debug("receiveBodyPropertyChanges not evaluated");
		}
	}
	

	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.AgentControllerIF#notifyOfPerception()
	 */
	public void notifyOfPerception() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.AgentControllerIF#notifyOfAction()
	 */
	public void notifyOfAction() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.AgentControllerIF#notifyOfActionResult(java.lang.String, double)
	 */
	public void notifyOfActionResult(String arg0, double arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.AgentControllerIF#shutdown()
	 */
	public void shutdown() {
		// TODO Auto-generated method stub

	}

}
