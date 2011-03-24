/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/communication/ComChannelResponse.java,v 1.3 2004/11/24 16:13:53 vuine Exp $
 */
package org.micropsi.common.communication;

public class ComChannelResponse {

	public static final int RESPONSE_OK = 0;
	public static final int RESPONSE_ERROR = 1;
	
	private static final byte[] OK = {'0'};
	private static final byte[] ERROR = {'1'};

	protected String requestName;
	protected Object responseData;
	protected int responseType;

	public ComChannelResponse(String requestName, int responseType, Object responseData) {
		this.requestName = requestName;
		this.responseData = responseData;
		this.responseType = responseType;		
	}
	
	protected ComChannelResponse() {
		// empty hidden constructor
	}

	public ComChannelResponse(String requestName, int responseType) {
		this.requestName = requestName;
		this.responseType = responseType;
	}

	public String getRequestName() {
		return requestName;
	}

	public Object getResponseData() {
		return responseData;
	}

	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

	public void setResponseData(Object responseData) {
		this.responseData = responseData;
	}

	public int getResponseType() {
		return responseType;
	}
	
	public byte[] getBResponseType() {
		switch(responseType) {
			case 0: return OK;
			case 1: return ERROR;
			default: throw new RuntimeException("FIX THIS: BAD RESPONSE TYPE");
		}
	}

	public void setResponseType(int responseType) {
		this.responseType = responseType;
	}

}
