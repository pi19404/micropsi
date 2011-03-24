package org.micropsi.media.camera;

import java.util.Iterator;
import java.util.Properties;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.control.FormatControl;
import javax.media.format.VideoFormat;
import javax.media.protocol.CaptureDevice;
import javax.media.protocol.DataSource;

import org.micropsi.media.IVideoSourceProvider;

public class Camera implements IVideoSourceProvider {
	
	private CaptureDeviceInfo deviceInfo;
	private VideoFormat videoFormat;
	private DataSource videoSource; 
	
	public Camera(Properties props) throws CameraException {
		
		String devicename = props.getProperty("devicename");		
		deviceInfo = CaptureDeviceManager.getDevice(devicename);
		
		if(deviceInfo == null) {
			Iterator devices = CaptureDeviceManager.getDeviceList(null).iterator();
			while(devices.hasNext()) {
				System.err.println(devices.next());
			}
			throw new CameraException("Could not find device: "+props.getProperty("devicename"));
		}
						
		String encoding = props.getProperty("encoding");
		//float framerate = Float.parseFloat(props.getProperty("framerate"));
		String size = props.getProperty("size");
		int maxdatalength = -1;
		if(props.getProperty("maxdatalength") != null) {
			maxdatalength = Integer.parseInt(props.getProperty("maxdatalength"));
		}
					
		Format[] formatList = deviceInfo.getFormats();
		for(int i=0;i<formatList.length;i++) {
			VideoFormat vf = (VideoFormat)formatList[i];
			String sizeString = vf.getSize().width+"x"+vf.getSize().height;
			if(	encoding.equals(vf.getEncoding()) &&
				//framerate == vf.getFrameRate() &&
				size.equals(sizeString) &&
				(maxdatalength == vf.getMaxDataLength() || maxdatalength == -1)) {
					videoFormat = vf;
			}	
		}					
		
		if(videoFormat == null) {
			dumpFormatsOf(devicename);
			throw new CameraException("Could not find format with the given properties");
		}	
		
		MediaLocator locator = deviceInfo.getLocator();
		try {
			videoSource = Manager.createDataSource(locator);
			FormatControl controls[] = ((CaptureDevice)videoSource).getFormatControls();
			for(int i=0;i<controls.length;i++) {
				if(controls[i].getSupportedFormats() == null) {
					continue;
				}
				Format supportedFormats[] = controls[i].getSupportedFormats(); 
				for(int j=0;j<supportedFormats.length;j++) {
					if(supportedFormats[j].matches(videoFormat)) {
						controls[i].setFormat(videoFormat);
					}
				}
			}
		} catch (Exception e) {
			throw new CameraException("Could not initialize data source",e);
		}		
	}

	public DataSource getVideoSource() {
		return videoSource;
	}

	public VideoFormat getFormat() {
		return videoFormat;
	}
	
	public static void dumpFormatsOf(String devicename) throws CameraException {
		CaptureDeviceInfo deviceInfo = CaptureDeviceManager.getDevice(devicename);
		
		if(deviceInfo == null) {
			throw new CameraException("Could not find device: "+devicename);
		}

		Format[] formatList = deviceInfo.getFormats();
		for(int i=0;i<formatList.length;i++) {
			VideoFormat vf = (VideoFormat)formatList[i];
			System.out.println("----- Format "+i+" ----");
			System.out.println("dataType="+vf.getDataType());
			System.out.println("encoding="+vf.getEncoding());
			System.out.println("framerate="+vf.getFrameRate());
			System.out.println("maxdatalength="+vf.getMaxDataLength());
			System.out.println("size="+vf.getSize().width+"x"+vf.getSize().height);
		}					

	}
	
}
