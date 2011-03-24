package org.micropsi.media;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Format;
import javax.media.format.VideoFormat;


public class VideoServerRegistry {

	private static VideoServerRegistry instance = new VideoServerRegistry();
	
	public static VideoServerRegistry getInstance() {
		return instance;
	}
	
	private HashMap<String,VideoServer> servers = new HashMap<String,VideoServer>();
	
	public void createVideoServer(String name, Properties source, Properties[] services) throws MediaServerException {
		VideoServer newServer = new VideoServer(name,source,services);
		servers.put(name,newServer);
	}
	
	public void removeVideoServer(String name) throws MediaServerException {
		VideoServer server = servers.get(name);
		server.shutdown();
	}
	
	public Iterator<VideoServer> getServers() {
		return servers.values().iterator();
	}

	public void shutdown() throws MediaServerException {
		for(Iterator<VideoServer> i=getServers();i.hasNext();) {
			i.next().shutdown();
		}
		servers.clear();
	}

	public VideoServer getServer(String name) {
		if(!servers.containsKey(name)) return null;
		return servers.get(name);
	}
	
	public Iterator<String> enumerateJMFVideoCaptureDevices() {
		HashSet<String> devices = new HashSet<String>();
		Iterator i = CaptureDeviceManager.getDeviceList(null).iterator();
		while(i.hasNext()) {
			CaptureDeviceInfo info = (CaptureDeviceInfo)i.next();
			
			Iterator<String> videoFormats = enumerateJMFVideoFormats(info.getName());
			if(videoFormats.hasNext()) {
				devices.add(info.getName());
			}
		}
		return devices.iterator();
	}
	
	public Iterator<String> enumerateJMFVideoFormats(String device) {
		ArrayList<String> results = new ArrayList<String>();
		
		CaptureDeviceInfo info = CaptureDeviceManager.getDevice(device);
		if(info == null) {
			return results.iterator();
		}
		
		Format[] formats = info.getFormats();
		if(formats == null) {
			formats = new Format[0];
		}
		for(int i=0;i<formats.length;i++) {
			if(formats[i] instanceof VideoFormat) {
				String formatString = "";
				VideoFormat vf = (VideoFormat)formats[i];
				formatString += "size="+vf.getSize().width+"x"+vf.getSize().height+",";
				formatString += "encoding="+vf.getEncoding()+",";
				formatString += "framerate="+vf.getFrameRate()+",";
				formatString += "maxdatalength="+vf.getMaxDataLength();
				results.add(formatString);	
			}
		}
		
		return results.iterator();
	}
}
