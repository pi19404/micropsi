/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MPerceptionReq.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.comp.messages;


public class MPerceptionReq extends RootMessage implements MessageIF {

	private String agentID;

	public MPerceptionReq() {
	}

	public MPerceptionReq(String agentID){
		this.agentID = agentID;
	}

	/**
	 * @see org.micropsi.comp.messages.MessageIF#getMessageType()
	 */
	public int getMessageType() {
		return MessageTypesIF.MTYPE_AGENT_PERCEPTIONREQ;
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

}
