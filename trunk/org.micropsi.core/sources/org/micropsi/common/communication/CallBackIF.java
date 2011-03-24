/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/communication/CallBackIF.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.communication;

import org.micropsi.common.exception.MicropsiException;

public interface CallBackIF {

	public void callBack(ComChannelResponse response);
	
	public void reportException(MicropsiException e);
}
