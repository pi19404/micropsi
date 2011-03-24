/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.whiskerbot/sources/org/micropsi/comp/agent/kheperaTurtle/KheperaProximityPerceptTL.java,v 1.3 2006/04/26 18:20:21 dweiller Exp $ 
 */
package org.micropsi.comp.agent.kheperaTurtle;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.agent.aaa.PerceptTranslatorIF;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.comp.messages.MPercept;
import org.micropsi.nodenet.LocalNetFacade;

public class KheperaProximityPerceptTL implements PerceptTranslatorIF {

	private static final String TYPE = "PROXIMITY_DATA";

	public String getPerceptID() {
		return TYPE;
	}
	
	private Logger logger;
	private LocalNetFacade net;
	private MicroPsiAgent agent;
	private HashMap dataSources = new HashMap();	
	
	public KheperaProximityPerceptTL(LocalNetFacade net, MicroPsiAgent agent, Logger logger) {
		this.net = net;
		this.agent = agent;
		this.logger = logger;
	}

	public void receivePercept(MPercept percept) {
	
		String objectclass = percept.getParameter("PROXIMITY_SENSOR1");
		logger.debug("ha");
		long id = Long.parseLong(percept.getParameter("ID"));
		logger.debug("haha");
		//long id = 1111;
		
		Position position = new Position(percept.getParameter("POSITION"));
		
		logger.debug("Updating: "+objectclass+" at "+position.getX()+"/"+position.getY()+"  in situation object "+agent.getSituation());
						
		agent.getSituation().updateElement(
			objectclass, 
			position.getX(), 
			position.getY(),
			id
		);
			
		/*if(!dataSources.containsKey(objectclass)) {
			PerceptObjectDataSource ds = new PerceptObjectDataSource(objectclass, agent);
			net.getSensorRegistry().registerSensorDataProvider(ds);
			dataSources.put(objectclass,ds);
		}*/
	}

	public void shutdown() {
	}
}
