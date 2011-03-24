/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MConsoleReq.java,v 1.3 2005/06/03 16:09:40 vuine Exp $
 */
package org.micropsi.comp.messages;

public class MConsoleReq extends RootMessage implements MessageIF {

	public static final int CONSOLEREQ_FIRSTTIME = 0;
	public static final int CONSOLEREQ_REGULAR = 1;
	public static final int CONSOLEREQ_LASTTIME = 2;

	int requestType = CONSOLEREQ_REGULAR;

	public int getMessageType() {
		return MessageTypesIF.MTYPE_CONSOLE_REQ;
	}
	
	/**
	 * @return the request type
	 */
	public int getRequestType() {
		return requestType;
	}

	/**
	 * @param type the request type
	 */
	public void setRequestType(int type) {
		requestType = type;
	}

}

