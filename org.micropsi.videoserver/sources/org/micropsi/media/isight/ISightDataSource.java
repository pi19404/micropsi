package org.micropsi.media.isight;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.io.IOException;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.MediaLocator;
import javax.media.Time;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;
import javax.media.util.ImageToBuffer;

import quicktime.qd.PixMap;
import quicktime.qd.QDGraphics;
import quicktime.std.sg.SequenceGrabber;
import quicktime.util.RawEncodedImage;

public class ISightDataSource extends PullBufferDataSource {

	ISightSourceStream stream;
	
	ISightDataSource(int width, int height, float frameRate, SequenceGrabber grabber,QDGraphics graphics) {
		stream = new ISightSourceStream(width, height, frameRate, grabber, graphics);
	}

	public void setLocator(MediaLocator source) {
	}

	public MediaLocator getLocator() {
		return null;
	}

	/**
	 * Content type is of RAW since we are sending buffers of video
	 * frames without a container format.
	 */
	public String getContentType() {
		return ContentDescriptor.RAW;
	}

	public void connect() {
	}

	public void disconnect() {
	}

	public void start() {
	}

	public void stop() {
	}

	/**
	 * Return the ImageSourceStreams.
	 */
	public PullBufferStream[] getStreams() {
		return new PullBufferStream[] {stream};
	}

	/**
	 * We could have derived the duration from the number of
	 * frames and frame rate.  But for the purpose of this program,
	 * it's not necessary.
	 */
	public Time getDuration() {
		return DURATION_UNKNOWN;
	}

	public Object[] getControls() {
		return new Object[0];
	}

	public Object getControl(String type) {
		return null;
	}
	
	public VideoFormat getFormat() {
		return stream.getFormat();
	}
	
	class ISightSourceStream implements PullBufferStream {

		int width, height;
		float framerate;
		VideoFormat format;
		SequenceGrabber grabber;
		QDGraphics graphics;
		
		int[] pixels;
		int videoWidth;
		RawEncodedImage rawEncodedImage;
		WritableRaster raster;

		BufferedImage image;

		
		long seqNo = 0;

		int pixelStride = 3;
		int[] d;
		
		public ISightSourceStream(int width, int height, float frameRate, SequenceGrabber grabber, QDGraphics graphics) {
			this.width = width;
			this.height = height;
			this.grabber = grabber;
			this.graphics = graphics;
			this.framerate = frameRate;
			
			d = new int[width*height*pixelStride];
			
			PixMap pixMap = graphics.getPixMap();
			rawEncodedImage = pixMap.getPixelData();
						
			videoWidth = width + (rawEncodedImage.getRowBytes() - width * 4) / 4;
						
			pixels = new int[videoWidth * height];
			raster = WritableRaster.createPackedRaster(DataBuffer.TYPE_INT,videoWidth, height, new int[] { 0x00ff0000, 0x0000ff00, 0x000000ff}, null);
			format = new RGBFormat(new Dimension(width,height),d.length,Format.intArray,20.0f,24,0x00ff0000,0x0000ff00,0x000000ff,pixelStride,width*pixelStride,RGBFormat.FALSE,RGBFormat.LITTLE_ENDIAN);
			image = new BufferedImage(videoWidth, height,BufferedImage.TYPE_INT_RGB);			
		}	
			
		/**
		 * We should never need to block assuming data are read from files.
		 */
		public boolean willReadBlock() {
			return false;
		}
		
		/**
		 * This is called from the Processor to read a frame worth
		 * of video data.
		 */
		public void read(Buffer buf) throws IOException {

			try {
				grabber.idleMore();
			} catch (Exception e) {
			}
			
			rawEncodedImage.copyToArray(0, pixels, 0, pixels.length);
			raster.setDataElements(0, 0, videoWidth, height, pixels);				
			image.setData(raster);
			Buffer tmp = ImageToBuffer.createBuffer(image,framerate);			
			buf.copy(tmp,true);
			
			long time = (long)(seqNo * (1000 / format.getFrameRate()) * 10000);
			buf.setTimeStamp(time);
			buf.setSequenceNumber(seqNo);			
		}

		/**
		 * Return the format of each video frame.  That will be JPEG.
		 */
		public VideoFormat getFormat() {
			return format;
		}

		public ContentDescriptor getContentDescriptor() {
			return new ContentDescriptor(ContentDescriptor.RAW);
		}

		public long getContentLength() {
			return LENGTH_UNKNOWN;
		}

		public boolean endOfStream() {
			return false;
		}

		public Object[] getControls() {
			return new Object[0];
		}

		public Object getControl(String type) {
			return null;
		}
	}

}
