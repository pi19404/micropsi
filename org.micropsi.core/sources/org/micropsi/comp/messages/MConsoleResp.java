/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MConsoleResp.java,v 1.3 2005/01/20 23:24:56 vuine Exp $
 */
package org.micropsi.comp.messages;

public class MConsoleResp extends RootMessage implements MessageIF {

	private static final String EMPTYSTRING = "";

	private String controltext;

	public int getMessageType() {		
		return MessageTypesIF.MTYPE_CONSOLE_RESP;
	}

	/**
	 * @return the "control text" field
	 */
	public String getControltext() {
		if(controltext == null) return EMPTYSTRING;
		return controltext;
	}

	/**
	 * @param controltext The "control text"
	 */
	public void setControltext(String controltext) {
		this.controltext = controltext;
	}

}
