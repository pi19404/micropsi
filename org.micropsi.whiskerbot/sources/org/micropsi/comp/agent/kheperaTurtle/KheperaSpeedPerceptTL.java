/*
 * Created on 06.04.2005
 *
 */
package org.micropsi.comp.agent.kheperaTurtle;

import org.apache.log4j.Logger;
import org.micropsi.comp.agent.aaa.PerceptTranslatorIF;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.comp.messages.MPercept;
import org.micropsi.nodenet.LocalNetFacade;

/**
 * @author Uni WLAN
 *
 * created first in project org.micropsi.whiskerbot
 */
public class KheperaSpeedPerceptTL implements PerceptTranslatorIF {

	private Logger logger;
	private LocalNetFacade net;
	private MicroPsiAgent agent;

	
	public KheperaSpeedPerceptTL(LocalNetFacade net, MicroPsiAgent agent, Logger logger) {
			this.net = net;
			this.agent = agent;
			this.logger = logger;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.PerceptTranslatorIF#getPerceptID()
	 */
	public String getPerceptID() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.PerceptTranslatorIF#receivePercept(org.micropsi.comp.messages.MPercept)
	 */
	public void receivePercept(MPercept percept) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.agent.aaa.PerceptTranslatorIF#shutdown()
	 */
	public void shutdown() {
		// TODO Auto-generated method stub

	}

}
