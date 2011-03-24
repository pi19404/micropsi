/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/common/ComponentClientFacade.java,v 1.6 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.common;

import java.util.HashMap;
import java.util.Iterator;

import org.micropsi.common.communication.CallBackIF;
import org.micropsi.common.communication.ComChannelClientFactory;
import org.micropsi.common.communication.ComChannelClientIF;
import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.communication.ComChannelResponse;
import org.micropsi.common.communication.ComChannelServerIF;
import org.micropsi.common.config.ConfigurationReaderIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.messages.MessageCodec;

/**
 * Facade class for a component's client capabilities. Basically, this does the
 * setup of abstract channelclients, and you use it from inside the component
 * to send requests. 
 */

public class ComponentClientFacade {
	
	protected HashMap<String,ComChannelClientIF> clientmap = new HashMap<String,ComChannelClientIF>();
	
	/**
	 * Constructs the facade. Called by the ComponentRunner.
	 */
	public ComponentClientFacade(ConfigurationReaderIF config, String prefix, ComponentServerFacade servers) throws MicropsiException {
	
		Iterator<String> configValues = config.getConfigurationValues(prefix).iterator();
		String next;
		int clienttype;
		String servername;
		String clientname;
		ComChannelClientIF client;
		while(configValues.hasNext()) {
			client = null;
			next = configValues.next();
			clienttype = config.getIntConfigValue(prefix+"."+next+".type");
			clientname = config.getConfigValue(prefix+"."+next+".name");
			servername = config.getConfigValue(prefix+"."+next+".connect-to");
			boolean useThreadPool = config.getBoolConfigValue(prefix+"."+next+".usethreadpool");
			switch(clienttype) {
				case 0: 
					ComChannelServerIF server = servers.getServer(servername);
					client = ComChannelClientFactory.createChannelClient(clienttype, server, null, useThreadPool); 
					break;
				case 1:
					try {
						client = ComChannelClientFactory.createChannelClient(clienttype, servername, new MessageCodec(false),useThreadPool);
					} catch (Exception e) {
						throw new MicropsiException(101,e);
					}
					break;
				case 100:
					try {
						client = ComChannelClientFactory.createChannelClient(clienttype, servername, new MessageCodec(true),useThreadPool);
					} catch (Exception e) {
						throw new MicropsiException(101,e);
					}
					break;

			}
			clientmap.put(clientname,client);
		}	
	}
	
	/**
	 * Performs the request request, using the client client. This method blocks
	 * until the response is received. Later on, there will also be a callback
	 * mechanism for non-blocking requests.
	 * You'll need to know what clients you can use in a component as the
	 * names of the available clients depend on the configuration.
	 */
	public ComChannelResponse performRequest(String client, ComChannelRequest request) throws MicropsiException {
		try {
			if(!clientmap.containsKey(client)) throw new MicropsiException(300,client);
			return clientmap.get(client).performRequest(request);
		} catch(Exception e) {
			throw new MicropsiException(306,e.getMessage(),e);
		}
	}
	
	public void performRequestNB(String client, ComChannelRequest request, CallBackIF callback) throws MicropsiException {
		try {
			if(!clientmap.containsKey(client)) throw new MicropsiException(300,client);
			clientmap.get(client).performRequestNB(request,callback);
		} catch(Exception e) {
			throw new MicropsiException(306,e.getMessage(),e);
		}
	}

}
