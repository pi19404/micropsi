package org.micropsi.media.rtp;

import java.net.InetAddress;
import java.util.Properties;

import javax.media.control.BufferControl;
import javax.media.format.H261Format;
import javax.media.format.H263Format;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;
import javax.media.rtp.ReceiveStream;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.SessionListener;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.event.SessionEvent;
import javax.media.rtp.rtcp.SourceDescription;

import org.micropsi.media.IVideoSourceProvider;

import com.sun.media.rtp.RTPSessionMgr;

public class RTP implements IVideoSourceProvider, ReceiveStreamListener, SessionListener {
	
	private RTPSessionMgr sessionMgr;
	private VideoFormat videoFormat;
	private DataSource videoSource; 
	private boolean streamStarted = false;
	
	public RTP(Properties props) throws RTPException {
		
		String devicename = props.getProperty("devicename");
		String host = devicename.substring(0,devicename.indexOf(":"));
		int port = Integer.parseInt(devicename.substring(devicename.indexOf(":")+1));
	
		try {
			
			InetAddress sessionAddress = InetAddress.getByName(host);
			
			SessionAddress remoteAddress = new SessionAddress(sessionAddress,port,1);
			SessionAddress localAddress = null;
			if(sessionAddress.isMulticastAddress()) {
				localAddress = new SessionAddress(sessionAddress,port,1);
			} else {
				localAddress = new SessionAddress(InetAddress.getLocalHost(),port); 
			}						
			
			sessionMgr = new RTPSessionMgr();
			sessionMgr.addSessionListener(this);
			sessionMgr.addReceiveStreamListener(this);

			sessionMgr.initialize(localAddress);
			
			BufferControl bc = (BufferControl)sessionMgr.getControl("javax.media.control.BufferControl");
			if (bc != null)
			    bc.setBufferLength(1024);
			    
	    	sessionMgr.addTarget(remoteAddress);
	    	sessionMgr.addFormat(new H263Format(),34);
	    	sessionMgr.addFormat(new H261Format(),31);
	    	sessionMgr.addFormat(new H261Format(),101);
			
			String cname = sessionMgr.generateCNAME();
			String username = "jmf-user";

			SourceDescription[] userdesclist = new SourceDescription[4];
			userdesclist[0] = new SourceDescription(SourceDescription.SOURCE_DESC_EMAIL,"jmf-user@sun.com",1,false);
			userdesclist[1] = new SourceDescription(SourceDescription.SOURCE_DESC_NAME,username,1,false);
			userdesclist[2] = new SourceDescription(SourceDescription.SOURCE_DESC_CNAME,cname,1,false);
			userdesclist[3] = new SourceDescription(SourceDescription.SOURCE_DESC_TOOL,"JMF RTP Player v2.0",1,false);

//			sessionMgr.initSession(localAddress,sessionMgr.generateSSRC(),userdesclist,0.05,0.25);
//			sessionMgr.startSession(remoteAddress,2000,null);			
		
		} catch (Exception e) {
			throw new RTPException("Could not set up RTP client",e);
		}
		
		while(!streamStarted) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		
	}

	public DataSource getVideoSource() {
		return videoSource;
	}

	public VideoFormat getFormat() {
		return videoFormat;
	}

	public void update(ReceiveStreamEvent event) {
		if(event instanceof NewReceiveStreamEvent) {
			ReceiveStream mystream = ((NewReceiveStreamEvent)event).getReceiveStream();
			videoSource = mystream.getDataSource();
			streamStarted = true;
		}
	}

	public void update(SessionEvent event) {
	}
	
}
