/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/common/ComponentServerFacade.java,v 1.4 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.common;

import java.util.HashMap;
import java.util.Iterator;

import org.micropsi.common.communication.ComChannelServerFactory;
import org.micropsi.common.communication.ComChannelServerIF;
import org.micropsi.common.communication.RequestHandlerIF;
import org.micropsi.common.config.ConfigurationReaderIF;
import org.micropsi.common.config.MicropsiConfigException;
import org.micropsi.common.exception.MicropsiException;

/**
 * A components server facade. Use this from within the component to register
 * your request handlers. Typically this will be done in the component's
 * performInitialisation() method.
 */ 
public class ComponentServerFacade {
	
	private static HashMap<String,ComChannelServerIF> mapInstance = new HashMap<String,ComChannelServerIF>();
	
	private static HashMap<String,ComChannelServerIF> getServerMap() {
		return mapInstance;
	}
	
	/**
	 * Creates the facade. Channelservers will be created according to the configuration.
	 */
	public ComponentServerFacade(ConfigurationReaderIF config, String prefix) throws MicropsiConfigException,MicropsiException {
	
		Iterator<String> configValues = config.getConfigurationValues(prefix).iterator();
		String next;
		int servertype;
		String servername;
		ComChannelServerIF server;
		while(configValues.hasNext()) {
			next = configValues.next();
			servertype = config.getIntConfigValue(prefix+"."+next+".type");
			servername = config.getConfigValue(prefix+"."+next+".name");
			server = ComChannelServerFactory.createChannelServer(servertype,servername);
			getServerMap().put(servername,server);
		}
	
	}
	
	/**
	 * Register a new request handler with the channelserver "servername". You don't have
	 * to care about threads, but you have to know which channelservers are there. Every
	 * component will define this and use a corresponding configuration.
	 */	
	public synchronized void registerRequestHandler(String servername, RequestHandlerIF handler) throws MicropsiException {
		if(!getServerMap().containsKey(servername)) throw new MicropsiException(301,servername);		
		ComChannelServerIF server = getServerMap().get(servername);
		server.registerRequestHandler(handler);
	}
	
	/**
	 * unregister a RequestHandler. You probably won't need this unless you want
	 * to change the behaviour of your component very drastically during runtime.
	 */
	public synchronized void unregisterRequestHandler(String servername, String handlername) throws MicropsiException {
		if(!getServerMap().containsKey(servername)) throw new MicropsiException(301,servername);
		ComChannelServerIF server = getServerMap().get(servername);
		server.deregisterRequestHandler(handlername);
	}
	
	/**
	 * Return the channelserver "servername"
	 */
	public ComChannelServerIF getServer(String servername) throws MicropsiException {
		if(!getServerMap().containsKey(servername)) throw new MicropsiException(301,servername);
		return getServerMap().get(servername);
	}

}
