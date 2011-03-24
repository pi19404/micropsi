package org.micropsi.media.isight;

import java.util.Properties;

import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;

import org.micropsi.media.IVideoSourceProvider;

import quicktime.QTException;
import quicktime.QTSession;
import quicktime.qd.QDGraphics;
import quicktime.qd.QDRect;
import quicktime.std.StdQTConstants;
import quicktime.std.sg.SGVideoChannel;
import quicktime.std.sg.SequenceGrabber;

/**
 * This implementation does NOT use Quicktime AWT components, but instead just grabs images and builds
 * a stream of JPGs to emulate a JMF data source. ISight is not supported by Mac JMF, so this 
 * workaround is needed to be at least able to grab images using the standard JMF API.
 *  
 * @author rvuine
 */
public class ISight implements IVideoSourceProvider {

	private ISightDataSource videoSource; 
	
	public ISight(Properties props) throws QTException {
		
		float framerate = Float.parseFloat(props.getProperty("framerate"));
		String size = props.getProperty("size");
		int width = Integer.parseInt(size.substring(0,size.indexOf("x")));
		int height = Integer.parseInt(size.substring(size.indexOf("x")+1));
//		String devicename = props.getProperty("devicename");
		
		QTSession.open();
		QDRect bounds = new QDRect(width, height);
		QDGraphics graphics = new QDGraphics(bounds);
		SequenceGrabber grabber = new SequenceGrabber();
		grabber.setGWorld(graphics, null);
		SGVideoChannel channel = new SGVideoChannel(grabber);
		channel.setBounds(bounds);
		channel.setUsage(StdQTConstants.seqGrabPreview);
		//channel.settingsDialog();
//		grabber.prepare(true, true);
//		grabber.startRecord();
		
//		channel.settingsDialog();
		grabber.prepare(true, false);
		grabber.startPreview();

		
		videoSource = new ISightDataSource(width,height,framerate,grabber,graphics);
	}

	public DataSource getVideoSource() {
		return videoSource;
	}

	public VideoFormat getFormat() {
		return videoSource.getFormat();
	}

}
