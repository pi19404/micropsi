/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MConfirmation.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.comp.messages;

public class MConfirmation extends RootMessage implements MessageIF {

	public int getMessageType() {
		return MessageTypesIF.MTYPE_COMMON_CONFIRMATION;
	}

}
