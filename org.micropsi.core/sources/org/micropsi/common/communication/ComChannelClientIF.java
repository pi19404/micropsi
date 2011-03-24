/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/communication/ComChannelClientIF.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.communication;

import org.micropsi.common.exception.MicropsiException;

public interface ComChannelClientIF {

	public ComChannelResponse performRequest(ComChannelRequest request) throws MicropsiException;

	public void performRequestNB(ComChannelRequest request, CallBackIF callback) throws MicropsiException;

}
