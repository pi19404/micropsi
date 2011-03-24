/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/agent/MicroPSICore.java,v 1.7 2005/08/20 16:24:49 vuine Exp $
 */
package org.micropsi.nodenet.agent;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.utils.MultiPassInputStream;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetCycle;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.NetPropertiesIF;

public class MicroPSICore {
		
	private LocalNetFacade net;
	private NetCycle cycle;
	
	/**
	 * Constructor.
	 * @param logger The logger to be used for this agent's core
	 * @param agentState The file with the agent's state.
	 * @throws MicropsiException
	 */
	public MicroPSICore(Logger logger, File agentState, NetPropertiesIF netProperties) throws MicropsiException {
	
		// create the brain!
		net = new LocalNetFacade(logger, netProperties);
		
		// retrieve the cycle
		cycle = (NetCycle)net.getCycle();	
			
		// load the net!
		if(agentState != null && agentState.exists()) {
			try {
				MultiPassInputStream finp = new MultiPassInputStream(agentState);
				net.loadNet(finp,false);
				finp.close();
			} catch (IOException e) {
				logger.error("IO Exception: ",e);
			}
		}
	} 

	/**
	 * Makes the net calculate the next step unless it is in suspend mode.
	 * @throws NetIntegrityException
	 */
	public void nextCycle() throws NetIntegrityException {
		if(cycle == null) return;
		cycle.nextCycle(true);
	}
	
	/**
	 * Suspends the net, nextCycle() won't work after this has been called
	 * until resume() is called.
	 */
	public void suspend() {
		if(cycle == null) return;
		cycle.suspend();
	}
	
	/**
	 * Resumes the net, nextCycle() will work after this call.
	 */
	public void resume() {
		if(cycle == null) return;
		cycle.resume();
	}	
	
	/**
	 * Blocks the net. 
	 */
	public void block() {
		if(cycle == null) return;
		cycle.block();		
	}
	
	/**
	 * Unblocks the net. 
	 */
	public void unblock() {
		if(cycle == null) return;
		cycle.unblock();		
	}


	/**
	 * Returns the net.
	 * @return LocalNetFacade
	 */
	public LocalNetFacade getNet() {
		return net;
	}
			
}
