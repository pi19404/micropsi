/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MTouch.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.comp.messages;

public class MTouch extends RootMessage {

	/**
	 * @see org.micropsi.comp.messages.MessageIF#getMessageType()
	 */
	public int getMessageType() {
		return MessageTypesIF.MTYPE_COMMON_TOUCH;
	}

}
