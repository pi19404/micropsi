/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/communication/LocalChannelClient.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.communication;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.utils.ThreadPool;

public class LocalChannelClient implements ComChannelClientIF {
	
	private class NonBlockingRequest implements Runnable {
		
		private CallBackIF callback;
		private ComChannelRequest request;
		
		public NonBlockingRequest(ComChannelRequest request, CallBackIF callback) {
			this.callback = callback;
			this.request = request;
		}
		
		public void run() {
			try {
				ComChannelResponse r = server.processRequest(request);			
				callback.callBack(r);
			} catch (MicropsiException e) {
				callback.reportException(e);
			}
		}
	}
	
	protected ComChannelServerIF server;
	protected boolean useThreadPool = false;
	
	public LocalChannelClient(ComChannelServerIF server, boolean useThreadPool) {
		this.server = server;
		this.useThreadPool = useThreadPool;
	}

	public ComChannelResponse performRequest(ComChannelRequest request) throws MicropsiException {		
		return server.processRequest(request);
	}
	
	public void performRequestNB(ComChannelRequest request, CallBackIF callback) throws MicropsiException {
		if(useThreadPool) {
			ThreadPool.getDefaultInstance().addJob(new NonBlockingRequest(request,callback));
		} else {
			Thread t = new Thread(new NonBlockingRequest(request,callback));
			t.start();
		}	
	}

}
