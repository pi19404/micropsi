/*
 * Created on 21.04.2005
 */
package org.micropsi.comp.agent.percepts;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.agent.MouseMicroPsiAgent;
import org.micropsi.comp.agent.aaa.PerceptTranslatorIF;
import org.micropsi.comp.messages.MPercept;
import org.micropsi.nodenet.LocalNetFacade;

/**
 * @author Markus
 *
 */
public class MouseWorldContentPercept implements PerceptTranslatorIF {

    private static final String TYPE = "OBJECT";

	private MouseMicroPsiAgent agent;
	
	public MouseWorldContentPercept(MouseMicroPsiAgent agent, LocalNetFacade net, Logger logger) {
	    this.agent = agent;
	}
	
	public String getPerceptID() {
		return TYPE;
	}
	
    public void receivePercept(MPercept percept) {
        String objectclass = percept.getParameter("CLASS");
		long id = Long.parseLong(percept.getParameter("ID"));
		Position position = new Position(percept.getParameter("POSITION"));
		
		agent.getSituation().updateElement(
				objectclass, 
				position.getX(), 
				position.getY(),
				id);
		
		agent.setPosition(new Position(percept.getParameter("AGENTPOSITION")));
		agent.setID(Long.parseLong(percept.getParameter("AGENTID")));
    }

    /* (non-Javadoc)
     * @see org.micropsi.comp.agent.aaa.PerceptTranslatorIF#shutdown()
     */
    public void shutdown() {
    }

}
