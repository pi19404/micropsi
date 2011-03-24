package org.micropsi.media.image;

import java.io.File;
import java.util.Properties;

import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;

import org.micropsi.media.IVideoSourceProvider;
import org.micropsi.media.MediaServerException;

public class Image implements IVideoSourceProvider {
	
	private ImageDataSource videoSource; 
	
	public Image(Properties props) throws MediaServerException {
		
		float framerate = Float.parseFloat(props.getProperty("framerate"));
		String size = props.getProperty("size");
		int width = Integer.parseInt(size.substring(0,size.indexOf("x")));
		int height = Integer.parseInt(size.substring(size.indexOf("x")+1));
		String devicename = props.getProperty("devicename");
		
		File device = new File(devicename);
		if(!device.exists() || !device.canRead()) {
			throw new MediaServerException("Can not access image file");
		}
		
		videoSource = new ImageDataSource(width,height,framerate,devicename);
	}

	public DataSource getVideoSource() {
		return videoSource;
	}

	public VideoFormat getFormat() {
		return videoSource.getFormat();
	}
	
}
