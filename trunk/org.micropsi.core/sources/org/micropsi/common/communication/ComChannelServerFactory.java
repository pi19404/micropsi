/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/communication/ComChannelServerFactory.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.communication;

import org.micropsi.common.exception.MicropsiException;

public class ComChannelServerFactory {
	
	public static ComChannelServerIF createChannelServer(int type, String name) throws MicropsiException {
		switch(type) {
			case ComChannelTypesIF.CHANNELTYPE_LOCAL:
				return new LocalChannelServer(name);
			case ComChannelTypesIF.CHANNELTYPE_XML:
				return new XMLTCPChannelServer(name);
			default:
				throw new MicropsiException(304,Integer.toString(type));
		}
	}

}
