/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MAgentResp.java,v 1.4 2005/01/20 23:24:56 vuine Exp $
 */
package org.micropsi.comp.messages;

public class MAgentResp extends RootMessage implements MessageIF {

	public static final int AGENTRESP_REGISTRATION = 0;
	public static final int AGENTRESP_ERROR = 1;
	public static final int AGENTRESP_KICK = 2;
	public static final int AGENTRESP_NORMALOP = 3;
	
	private static final String EMPTYSTRING = "";
	
	private String controltext;

	private MActionResponse previousActionResponse;
	private int responseType = AGENTRESP_NORMALOP;	
	
	public int getMessageType() {
		return MessageTypesIF.MTYPE_AGENT_RESP;
	}

	/**
	 * Returns the controltext.
	 * @return String
	 */
	public String getControltext() {
		if(controltext == null) return EMPTYSTRING;
		return controltext;
	}

	/**
	 * Sets the controltext.
	 * @param controltext The controltext to set
	 */
	public void setControltext(String controltext) {
		this.controltext = controltext;
	}

	/**
	 * Returns the responseType.
	 * @return int
	 */
	public int getResponseType() {
		return responseType;
	}

	/**
	 * Sets the responseType.
	 * @param responseType The responseType to set
	 */
	public void setResponseType(int responseType) {
		this.responseType = responseType;
	}

	/**
	 * Returns the previousActionResponse.
	 * @return MActionResponse
	 */
	public MActionResponse getPreviousActionResponse() {
		return previousActionResponse;
	}

	/**
	 * Sets the previousActionResponse.
	 * @param previousActionResponse The previousActionResponse to set
	 */
	public void setPreviousActionResponse(MActionResponse previousActionResponse) {
		this.previousActionResponse = previousActionResponse;
	}

}
