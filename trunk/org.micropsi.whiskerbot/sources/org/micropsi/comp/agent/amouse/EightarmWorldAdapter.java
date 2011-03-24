/*
 * Created on Dec 18, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.micropsi.comp.agent.amouse;

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
import org.micropsi.comp.agent.amouse.move.Movement;
import org.micropsi.comp.agent.amouse.percept.LightUrges;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.comp.messages.MPerceptionValue;



/**
 * @author Daniel
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EightarmWorldAdapter implements AgentWorldAdapterIF,
		AgentControllerIF {
	
	private MicroPsiAgent amouse;
	private HashMap urges;
	private RegistryNode register = new RegistryNode();
	private Movement movement;


	
	public void initialize(AgentIF agent, Logger logger) throws MicropsiException {
	
		logger.debug("Initializing EightarmWorld Adapter");
		amouse = (MicroPsiAgent)agent;
		urges = new HashMap();
		
		
		LightUrges whisker1 = new LightUrges("whisker1");
		register.regestry(whisker1,amouse);
		urges.put("Whisker1",whisker1);
		LightUrges whisker2 = new LightUrges("whisker2");
		register.regestry(whisker2,amouse);
		urges.put("Whisker2",whisker2);
		LightUrges whisker3 = new LightUrges("whisker3");
		register.regestry(whisker3,amouse);
		urges.put("Whisker3",whisker3);
		LightUrges whisker4 = new LightUrges("whisker4");
		register.regestry(whisker4,amouse);
		urges.put("Whisker4",whisker4);
		LightUrges whisker5 = new LightUrges("whisker5");
		register.regestry(whisker5,amouse);
		urges.put("Whisker5",whisker5);
		LightUrges whisker6 = new LightUrges("whisker6");
		register.regestry(whisker6,amouse);
		urges.put("Whisker6",whisker6);
		LightUrges whisker7 = new LightUrges("whisker7");
		register.regestry(whisker7,amouse);
		urges.put("Whisker7",whisker7);
		LightUrges whisker8 = new LightUrges("whisker8");
		register.regestry(whisker8,amouse);
		urges.put("Whisker8",whisker8);
		LightUrges whisker9 = new LightUrges("whisker9");
		register.regestry(whisker9,amouse);
		urges.put("Whisker9",whisker9);
		
		

	}
	

	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.AgentWorldAdapterIF#createController()
	 */
	public AgentControllerIF createController() {
		// TODO Auto-generated method stub
		return this;
	}
	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.AgentWorldAdapterIF#createActionTranslators()
	 */
	public ActionTranslatorIF[] createActionTranslators() {
		return new ActionTranslatorIF[] {movement};
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.AgentWorldAdapterIF#createPerceptTranslators()
	 */
	public PerceptTranslatorIF[] createPerceptTranslators() {
		   return new PerceptTranslatorIF[] {};
	}

	
	public UrgeCreatorIF[] createUrgeCreators() {
		UrgeCreatorIF[] toReturn = new UrgeCreatorIF[urges.size()];
		Iterator urgeIterator = urges.values().iterator();
		
		for(int i=0;i<urges.size();i++) 
			toReturn[i] = (UrgeCreatorIF)urgeIterator.next();
		
		return toReturn;	
	}

	
	public boolean wantsPerception() {
		/*
		 * Always wants to perceive data
		 */
		return true;
	}

	
	public void receiveBodyPropertyChanges(ArrayList propertyChanges) {
		for(int i=0;i<propertyChanges.size();i++) {
			MPerceptionValue tmp = (MPerceptionValue)propertyChanges.get(i);
			LightUrges urge = (LightUrges)urges.get(tmp.getKey());
			urge.setSignalStrength(Double.parseDouble(tmp.getValue()));
		}
		

	}

	
	public void notifyOfPerception() {
		// TODO Auto-generated method stub

	}

	
	public void notifyOfAction() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.AgentControllerIF#shutdown()
	 */
	public void shutdown() {
		// TODO Auto-generated method stub

	}


	/*(non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.AgentControllerIF#notifyOfActionResult(java.lang.String, double)
	 */
	public void notifyOfActionResult(String actionName, double actionResult) {
		
	}

}
