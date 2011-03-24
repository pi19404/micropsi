/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MAction.java,v 1.5 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.messages;

import java.util.ArrayList;
import java.util.List;

public class MAction implements MessageIF {

	public static final String NOOP = "NOOP";

	private String actionType = NOOP;
	private long targetObject = -1;
	private ArrayList<String> parameters = new ArrayList<String>();
	private String agentName = "";
	private long ticket = -1;
		
	protected MAction() {
	}

	public MAction(String actionType) {
		this.actionType = actionType;
	}
	
	public MAction(String actionType, String agentName) {
		this.actionType = actionType;
		this.agentName = agentName;
	}
	
	public MAction(String actionType, String agentName, List<String> parameters) {
		this.actionType = actionType;
		this.agentName = agentName;
		this.parameters = new ArrayList<String>(parameters);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("action type: "+actionType);
		buf.append("targetObject: " + targetObject + "\n");
		buf.append("parameters: " + parameters + "\n");
		buf.append("agent name: " + agentName+ "\n");
		return buf.toString();
	}
	
	public int getMessageType() {
		return MessageTypesIF.MTYPE_AGENT_ACTION;
	}

	public String getActionType() {
		return actionType;
	}
	
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public long getTargetObject() {
		return targetObject;
	}
	
	public void setTargetObject(long targetoid) {
		this.targetObject = targetoid;
	}

	public String getAgentName() {
		return agentName;
	}
	
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public int getParameterCount() {
		return parameters.size();
	}

	public String getParameter(int index) {
		return parameters.get(index);
	}
	
	public void addParameter(String parameter) {
		parameters.add(parameter);
	}
	
	public void clearParameters() {
		parameters.clear();
	}

	public long getTicket() {
		return ticket;
	}
	
	public void setTicket(long ticket) {
		this.ticket = ticket;
	}
	
	public boolean hasTicket() {
		return ticket != -1;
	}
}
