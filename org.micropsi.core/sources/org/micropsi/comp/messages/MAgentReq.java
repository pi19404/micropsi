/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MAgentReq.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.comp.messages;

public class MAgentReq extends RootMessage implements MessageIF {
	
	public static final int AGENTREQ_REGISTER = 0;
	public static final int AGENTREQ_NORMALOP = 1;
	public static final int AGENTREQ_UNREGISTER = 2;
	
	private MAction action;
	private int requestType = AGENTREQ_NORMALOP;
	private String agentID;
	private String agentType;
	
	public MAgentReq() {
	}
	
	public int getMessageType() {
		return MessageTypesIF.MTYPE_AGENT_REQ;
	}
	
	public void setAction(MAction action) {
		this.action = action;
	}
	
	public MAction getAction() {
		return action;
	}
		
	/**
	 * Returns the requestType.
	 * @return int
	 */
	public int getRequestType() {
		return requestType;
	}

	/**
	 * Sets the requestType.
	 * @param requestType The requestType to set
	 */
	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}

	/**
	 * Returns the agentID.
	 * @return String
	 */
	public String getAgentID() {
		return agentID;
	}

	/**
	 * Sets the agentID.
	 * @param agentID The agentID to set
	 */
	public void setAgentID(String agentID) {
		this.agentID = agentID;
	}

	/**
	 * @return the agent type
	 */
	public String getAgentType() {
		return agentType;
	}

	/**
	 * @param string
	 */
	public void setAgentType(String string) {
		agentType = string;
	}

}
