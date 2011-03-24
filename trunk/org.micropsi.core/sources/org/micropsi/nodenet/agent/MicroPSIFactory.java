/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/agent/MicroPSIFactory.java,v 1.3 2005/08/20 16:24:49 vuine Exp $
 */
package org.micropsi.nodenet.agent;

import java.io.File;

import org.apache.log4j.Logger;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.nodenet.NetPropertiesIF;

public class MicroPSIFactory {

	private static MicroPSIFactory instance = new MicroPSIFactory();
	
	public static MicroPSIFactory getInstance() { 
		return instance;
	}
	
	public MicroPSICore createMicroPSI(Logger logger, File agentState, NetPropertiesIF netProperties) throws MicropsiException {
		MicroPSICore toReturn = new MicroPSICore(logger, agentState, netProperties);
		
		return toReturn;
	}
	

}
