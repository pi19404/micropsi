/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MActionResponse.java,v 1.6 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.messages;

import java.util.ArrayList;


public class MActionResponse implements MessageIF {
	
	private String agentName = "";
	private double success;
	private long ticket;
	private ArrayList<MPerceptionValue> bodyPropertyChanges = new ArrayList<MPerceptionValue>();

	protected MActionResponse() {}
	
	public MActionResponse(String agentName, double success, long ticket) {
		this.success = success;
		this.agentName = agentName;
		this.ticket = ticket;
	}
	
	/**
	 * @see org.micropsi.comp.messages.MessageIF#getMessageType()
	 */
	public int getMessageType() {
		return MessageTypesIF.MTYPE_AGENT_ACTIONRESPONSE;
	}

	/**
	 * Returns the success.
	 * @return double
	 */
	public double getSuccess() {
		return success;
	}

	/**
	 * Returns the agentName.
	 * @return String
	 */
	public String getAgentName() {
		return agentName;
	}

	/**
	 * Sets the agentName.
	 * @param agentName The agentName to set
	 */
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	/**
	 * Sets the success.
	 * @param success The success to set
	 */
	public void setSuccess(double success) {
		this.success = success;
	}
	
	public ArrayList getBodyPropertyChanges() {
		return bodyPropertyChanges;
	}

	public void clearBodyPropertyChanges() {
		bodyPropertyChanges.clear();
	}
	
	public void addBodyPropertyChange(MPerceptionValue bpc) {
		bodyPropertyChanges.add(bpc);
	}
	
	/**
	 * 
	 * @param propertyKey
	 * @return can be null
	 */
	public String getPropertyChangeValue(String propertyKey) {
		for(int i=0;i<bodyPropertyChanges.size();i++) {
			MPerceptionValue tmp = bodyPropertyChanges.get(i);
			if(tmp.getKey().equals(propertyKey)) return tmp.getValue();
		}
		return null;
	}
	
	public long getTicket() {
		return ticket;
	}
	
	public void setTicket(long ticket) {
		this.ticket = ticket;
	}
}
