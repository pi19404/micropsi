/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MPercept.java,v 1.3 2005/07/12 12:55:17 vuine Exp $ 
 */
package org.micropsi.comp.messages;

import java.util.ArrayList;
import java.util.List;

public class MPercept implements MessageIF {

	private String name = "";
	private List<MPerceptionValue> perceptionValues = new ArrayList<MPerceptionValue>();
	
	public int getMessageType() {
		return MessageTypesIF.MTYPE_AGENT_PERCEPT;
	}
	
	protected MPercept() {
	}
	
	public MPercept(String name) {
		this.name = name;
	}

	public void clearParameters() {
		perceptionValues.clear();
	}
	
	public void addParameter(String key, String value) {
		perceptionValues.add(new MPerceptionValue(key, value));
	}
	
	public void addParameter(MPerceptionValue value) {
		perceptionValues.add(value);
	}
	
	public String getParameter(String key) {
		for (int i = 0; i < perceptionValues.size(); i++) {
			MPerceptionValue v = perceptionValues.get(i);
			if (key.equals(v.getKey())) {
				return v.getValue();
			} 
		}
		return null;
	}
	
	/**
	 * @return the percept's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param string the percept's name
	 */
	public void setName(String string) {
		name = string;
	}

	public List<MPerceptionValue> getPerceptionValues() {
		return perceptionValues;
	}

}
