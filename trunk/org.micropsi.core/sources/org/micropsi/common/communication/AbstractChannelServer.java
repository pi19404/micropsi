/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/communication/AbstractChannelServer.java,v 1.3 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.common.communication;

import java.util.HashMap;

import org.micropsi.common.exception.MicropsiException;

public abstract class AbstractChannelServer implements ComChannelServerIF {

	protected String name;
	protected HashMap<String,RequestHandlerIF> handlers = new HashMap<String,RequestHandlerIF>();
	
	public AbstractChannelServer(String name) {
		this.name = name;
	}
	
	protected RequestHandlerIF getHandler(String requestname) throws MicropsiException {
		if(!handlers.containsKey(requestname)) throw new MicropsiException(302,requestname+" in server "+name);
		return handlers.get(requestname);
	}
	
	public void registerRequestHandler(RequestHandlerIF handler) throws MicropsiException {
		if(handlers.containsKey(handler.getHandledRequest())) throw new MicropsiException(303,handler.getHandledRequest());
		handlers.put(handler.getHandledRequest(),handler);
	}

	public void deregisterRequestHandler(String requestname) throws MicropsiException {
		if(!handlers.containsKey(requestname)) throw new MicropsiException(302,requestname+" in server "+name);
		handlers.remove(requestname);
	}

	public String getServerName() {
		return name;
	}

	public void setServerName(String name) {
		this.name = name;
	}

}
