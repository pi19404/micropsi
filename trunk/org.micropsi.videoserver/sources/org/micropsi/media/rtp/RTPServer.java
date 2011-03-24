package org.micropsi.media.rtp;

import java.awt.Dimension;
import java.util.Properties;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Processor;
import javax.media.control.TrackControl;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;

import org.micropsi.media.IVideoTarget;

public class RTPServer implements IVideoTarget {
	
	private String devicename;
	private String service;
	
	private MediaLocator outputLocator;
	private Processor toRtpProcessor;
	private javax.media.protocol.DataSource toRtpTranscoder;
	private DataSink transmitter;
	
	public RTPServer(Properties props, DataSource source) throws RTPException {
		devicename = props.getProperty("devicename");
		service = props.getProperty("service");
		
		String rtpURL = "rtp://" + devicename + "/video";// +service;
		outputLocator = new MediaLocator(rtpURL);
		
		//-------------------
		Processor toJpegProcessor;
		try {
			toJpegProcessor = Manager.createProcessor(source);
		} catch (Exception e) {
			throw new RTPException("Could not create processor for source "+source);
		}
		
		toJpegProcessor.addControllerListener(new ControllerListener() {
			public void controllerUpdate(ControllerEvent event) {
				System.err.println("Controller event: "+event);
			}
		});
		
		toJpegProcessor.configure();
		
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		} while (toJpegProcessor.getState() != Processor.Configured);
				
		TrackControl[] tracks = toJpegProcessor.getTrackControls();
		if(tracks == null || tracks.length < 1) {
			throw new RTPException("No tracks found.");
		}

		boolean foundVideo = false;
		
		for(int i=0;i<tracks.length;i++) {
			Format format = tracks[i].getFormat();
			if (tracks[i].isEnabled() && format instanceof VideoFormat && !foundVideo) {
				Dimension size = ((VideoFormat)format).getSize();
				float frameRate = ((VideoFormat)format).getFrameRate();
				int w = (size.width % 8 == 0 ? size.width : (int)(size.width / 8) * 8);
				int h = (size.height % 8 == 0 ? size.height : (int)(size.height / 8) * 8);
				VideoFormat jpegFormat = new VideoFormat(VideoFormat.JPEG,new Dimension(w, h),Format.NOT_SPECIFIED,Format.byteArray,frameRate);
				tracks[i].setFormat(jpegFormat);
				foundVideo = true;
			} else
				tracks[i].setEnabled(false);
		}
		
		if(!foundVideo) {
			throw new RTPException("No video format found.");
		}
		
//		ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.J);
//		toRtpProcessor.setContentDescriptor(cd);

		toJpegProcessor.realize();
		
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		} while (toJpegProcessor.getState() != Processor.Realized);
		
		DataSource jpegSource = toJpegProcessor.getDataOutput();

		
		
		
		//-------------------
		
		
		

		try {
			toRtpProcessor = Manager.createProcessor(jpegSource);
		} catch (Exception e) {
			throw new RTPException("Could not create processor for source "+source);
		}
		
		toRtpProcessor.addControllerListener(new ControllerListener() {
			public void controllerUpdate(ControllerEvent event) {
				System.err.println("Controller event: "+event);
				//if(processor.getState() == Processor.Configured)
			}
		});
		
		toRtpProcessor.configure();
		
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		} while (toRtpProcessor.getState() != Processor.Configured);
				
//		TrackControl[] tracks = toRtpProcessor.getTrackControls();
		tracks = toRtpProcessor.getTrackControls();
		if(tracks == null || tracks.length < 1) {
			throw new RTPException("No tracks found.");
		}

//		boolean foundVideo = false;
		foundVideo = false;
		
		for(int i=0;i<tracks.length;i++) {
			Format format = tracks[i].getFormat();
			if (tracks[i].isEnabled() && format instanceof VideoFormat && !foundVideo) {
				Dimension size = ((VideoFormat)format).getSize();
				float frameRate = ((VideoFormat)format).getFrameRate();
				int w = (size.width % 8 == 0 ? size.width : (int)(size.width / 8) * 8);
				int h = (size.height % 8 == 0 ? size.height : (int)(size.height / 8) * 8);
				VideoFormat jpegFormat = new VideoFormat(VideoFormat.JPEG_RTP,new Dimension(w, h),Format.NOT_SPECIFIED,Format.byteArray,frameRate);
				tracks[i].setFormat(jpegFormat);
				foundVideo = true;
			} else
				tracks[i].setEnabled(false);
		}
		
		if(!foundVideo) {
			throw new RTPException("No video format found.");
		}
		
		ContentDescriptor cd = new ContentDescriptor(ContentDescriptor.RAW_RTP);
		toRtpProcessor.setContentDescriptor(cd);

		toRtpProcessor.realize();
		
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		} while (toRtpProcessor.getState() != Processor.Realized);
		
		toRtpTranscoder = toRtpProcessor.getDataOutput();
		
		
		
		

	}

	public void start() throws RTPException {
		try {
			transmitter = Manager.createDataSink(toRtpTranscoder, outputLocator);
			transmitter.open();
			transmitter.start();
			toRtpTranscoder.start();
		} catch (Exception e) {
			throw new RTPException("Could not start transmission",e);
		}		  
	}

	public void stop() throws RTPException {
		try {
			toRtpTranscoder.stop();
			transmitter.stop();
			transmitter.close();
		} catch (Exception e) {
			throw new RTPException("Could not start transmission",e);
		}
	}

}
