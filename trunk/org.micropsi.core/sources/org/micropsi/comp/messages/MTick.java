/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MTick.java,v 1.3 2005/01/20 23:24:56 vuine Exp $
 */
package org.micropsi.comp.messages;

public class MTick extends RootMessage implements MessageIF {

	public int getMessageType() {
		return MessageTypesIF.MTYPE_TIMER_TICK;
	}	

}
