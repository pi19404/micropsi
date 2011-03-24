package org.micropsi.media.image;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.MediaLocator;
import javax.media.Time;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;

public class ImageDataSource extends PullBufferDataSource {

	ImageSourceStream stream;
	
	ImageDataSource(int width, int height, float frameRate, String file) {
		stream = new ImageSourceStream(width, height, frameRate, file);
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
	
	class ImageSourceStream implements PullBufferStream {

		int width, height;
		String imageFile;
		BufferedImage img;
		VideoFormat format;

		byte[] data;
		
		public ImageSourceStream(int width, int height, float frameRate, String imageFile) {
			this.width = width;
			this.height = height;
			this.imageFile = imageFile;			
			format = new VideoFormat(VideoFormat.JPEG, new Dimension(width,height), Format.NOT_SPECIFIED, Format.byteArray, 30f);
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

			if(data == null) {
				RandomAccessFile raFile;
				raFile = new RandomAccessFile(imageFile, "r");
				if (buf.getData() instanceof byte[]) {
					data = (byte[]) buf.getData();
				}
				if (data == null || data.length < raFile.length()) {
					data = new byte[(int) raFile.length()];
					buf.setData(data);
				}
				raFile.readFully(data, 0, (int) raFile.length());
				raFile.close();
			}
			
			buf.setDuration(Long.MAX_VALUE);
			buf.setData(data);
			buf.setOffset(0);
			buf.setLength(data.length);
			buf.setFormat(format);
			buf.setFlags(buf.getFlags() | Buffer.FLAG_KEY_FRAME);
			
////		System.err.println("readframe "+seqNo);
//			
//			if(img == null) {
//				img = ImageIO.read(new File(imageFile));	
//			}
//			
//			Buffer tmp = ImageToBuffer.createBuffer(img, format.getFrameRate());
//			buf.copy(tmp);
////			buf.setFlags(buf.getFlags() | Buffer.FLAG_KEY_FRAME);
//			
//			
////			long time = (long)(seqNo * (1000 / format.getFrameRate()) * 1000000);
////			buf.setTimeStamp(time);
////			buf.setSequenceNumber(seqNo++);		
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
