/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MPerceptionValue.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $ 
 */
package org.micropsi.comp.messages;


public class MPerceptionValue implements MessageIF {

	private String key = "";
	private String value = "";

	public MPerceptionValue() {
	}
	
	public MPerceptionValue(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public int getMessageType() {
		return MessageTypesIF.MTYPE_AGENT_PERCEPTIONVALUE;
	}
	
	/**
	 * @return the perception's key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param string the key
	 */
	public void setKey(String string) {
		key = string;
	}

	/**
	 * @param string the value
	 */
	public void setValue(String string) {
		value = string;
	}

}
