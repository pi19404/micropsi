/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/communication/ComChannelRequest.java,v 1.3 2004/11/24 16:13:53 vuine Exp $
 */
package org.micropsi.common.communication;

public class ComChannelRequest {
	
	protected String requestName;
	protected Object requestData;
	protected String sender;
	
	public ComChannelRequest(String requestName, Object requestData, String senderID) {
		this.requestName = requestName;
		this.requestData = requestData;
		this.sender = senderID;
	}
	
	protected ComChannelRequest() {
		// empty hidden constructor
	}
	
	public Object getRequestData() {
		return requestData;
	}

	public String getRequestName() {
		return requestName;
	}

	public void setRequestData(Object requestData) {
		this.requestData = requestData;
	}

	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

}
