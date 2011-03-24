/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/communication/ComChannelClientFactory.java,v 1.3 2005/06/02 02:04:20 vuine Exp $
 */
package org.micropsi.common.communication;

import org.micropsi.common.exception.MicropsiException;

public class ComChannelClientFactory {

	public static ComChannelClientIF createChannelClient(int type, Object param, Object codec, boolean useThreadPool) throws MicropsiException {
		switch(type) {
			case ComChannelTypesIF.CHANNELTYPE_LOCAL:
				return new LocalChannelClient((ComChannelServerIF)param,useThreadPool);
			case ComChannelTypesIF.CHANNELTYPE_XML:
				return new XMLTCPChannelClient((String)param,(AbstractXMLObjectCodec)codec, useThreadPool);
			case ComChannelTypesIF.CHANNELTYPE_XML_DEBUG:
				return new XMLTCPChannelClient((String)param,(AbstractXMLObjectCodec)codec, useThreadPool);
			default:
				throw new MicropsiException(305,Integer.toString(type));
		}
	}

}
