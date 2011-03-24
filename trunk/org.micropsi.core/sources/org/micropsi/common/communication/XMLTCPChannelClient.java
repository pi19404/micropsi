/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/communication/XMLTCPChannelClient.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.communication;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.utils.ThreadPool;


public class XMLTCPChannelClient implements ComChannelClientIF {
	
	private class NonBlockingRequest implements Runnable {
		
		private CallBackIF callback;
		private ComChannelRequest request;
		
		public NonBlockingRequest(ComChannelRequest request, CallBackIF callback) {
			this.callback = callback;
			this.request = request;
		}
		
		public void run() {
			try {
				URLConnection c = servletUrl.openConnection();
				c.setDoOutput(true);
				c.setDoInput(true);
				c.connect();
				codec.writeRequest(request,c.getOutputStream());
				ComChannelResponse response = codec.receiveResponse(c.getInputStream());
				callback.callBack(response);				
			} catch (MicropsiException e) {
				callback.reportException(e);
			} catch (IOException e) {
				callback.reportException(new MicropsiException(13,e.getMessage(),e));
			}
		}
	}
	
	protected URL servletUrl;
	protected AbstractXMLObjectCodec codec;
	protected boolean useThreadPool;
	
	public XMLTCPChannelClient(String servletUrl,AbstractXMLObjectCodec codec, boolean useThreadPool) throws MicropsiException {
		this.codec = codec;
		this.useThreadPool = useThreadPool;
		try {
			this.servletUrl = new URL(servletUrl);
		} catch (MalformedURLException e) {
			throw new MicropsiException(12,servletUrl,e);
		}
	}

	public ComChannelResponse performRequest(ComChannelRequest request) throws MicropsiException {
		try {
			URLConnection connection = servletUrl.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
		
			//connection.connect();
			codec.writeRequest(request,connection.getOutputStream());
			//@todo: check if or why this is necessary
			synchronized(codec) {
				return codec.receiveResponse(connection.getInputStream());
			}
		} catch (IOException e) {
			throw new MicropsiException(14,e.getMessage(),e);
		}
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
