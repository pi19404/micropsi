/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/agent/micropsi/percepts/WorldContentPercept.java,v 1.5 2005/07/12 12:55:17 vuine Exp $ 
 */
package org.micropsi.comp.agent.micropsi.percepts;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.agent.aaa.PerceptTranslatorIF;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.comp.messages.MPercept;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.SensorDataSourceIF;

public class WorldContentPercept implements PerceptTranslatorIF {

	private static final String TYPE = "OBJECT";

	public String getPerceptID() {
		return TYPE;
	}
	
	private Logger logger;
	private LocalNetFacade net;
	private MicroPsiAgent agent;
	private HashMap<String,SensorDataSourceIF> dataSources = new HashMap<String,SensorDataSourceIF>();	
	
	public WorldContentPercept(LocalNetFacade net, MicroPsiAgent agent, Logger logger) {
		this.net = net;
		this.agent = agent;
		this.logger = logger;
	}

	public void receivePercept(MPercept percept) {
	
		String objectclass = percept.getParameter("CLASS");
		long id = Long.parseLong(percept.getParameter("ID"));
		Position position = new Position(percept.getParameter("POSITION"));
		
		logger.debug("Updating: "+objectclass+" at "+position.getX()+"/"+position.getY()+"  in situation object "+agent.getSituation());
						
		agent.getSituation().updateElement(
			objectclass, 
			position.getX(), 
			position.getY(),
			id);
			
		if(!dataSources.containsKey(objectclass)) {
			PerceptObjectDataSource ds = new PerceptObjectDataSource(objectclass, agent);
			net.getSensorRegistry().registerSensorDataProvider(ds);
			dataSources.put(objectclass,ds);
		}
	}

	public void shutdown() {
	}
}
