/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/common/ProtocolVersionChecker.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.comp.common;

import org.micropsi.common.communication.ComChannelRequest;
import org.micropsi.common.communication.ComChannelResponse;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.messages.MVersion;

public class ProtocolVersionChecker {
	
	public static void checkServerVersion(String channel, ComponentClientFacade com, String sender) throws MicropsiException {
		ComChannelRequest req = new ComChannelRequest("version",null,sender);
		ComChannelResponse resp = com.performRequest(channel,req);
		MVersion remote = (MVersion)resp.getResponseData();  
		if(!remote.isRemoteVersionOK()) {
			throw new MicropsiException(30, "remote: "+remote.getRemoteMajor()+"."+remote.getRemoteMinor()+"("+remote.getRemoteName()+") "+
										"local: "+MVersion.MAJOR+"."+MVersion.MINOR+"("+MVersion.NAME+")");
		}
	}

}
