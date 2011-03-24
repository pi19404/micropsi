package org.micropsi.media.codec.video.jpeg;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

import javax.media.Buffer;
import javax.media.Codec;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.format.VideoFormat;
import javax.media.util.ImageToBuffer;

public class JavaEncoder implements Codec {

	public static final VideoFormat rgbFormat = new VideoFormat(VideoFormat.RGB);
	public static final VideoFormat jpegFormat = new VideoFormat(VideoFormat.JPEG);
	
	VideoFormat realInput = null;
	VideoFormat realOutput = null;
	
	public JavaEncoder() {
	}
	
	public Format[] getSupportedInputFormats() {
//		System.err.println("formats input?");
//		VideoFormat rgbFormat = new RGBFormat(
//				new Dimension(width,height),
//				d.length,
//				Format.intArray,
//				20.0f,
//				24,
//				0x00ff0000,0x0000ff00,0x000000ff,
//				pixelStride,
//				width*pixelStride,RGBFormat.FALSE,RGBFormat.LITTLE_ENDIAN);

		return new Format[] {rgbFormat};
	}

	public Format[] getSupportedOutputFormats(Format input) {
//		System.err.println("formats output? "+input);
		if(input != null) { //&& rgbFormat.matches(input)) {
			return new Format[] {jpegFormat};
		} else {
			return null;
		}
	}

	public Format setInputFormat(Format input) {
		if(input == null) {
			return null;
		}
		if(!(input instanceof VideoFormat)) {
			return null;
		}
		if(!rgbFormat.matches(input)) {
			return null;
		}
		
//		System.err.println("SetInput! "+input);
		
		realInput = (VideoFormat)input;
		return realInput;
	}

	public Format setOutputFormat(Format output) {
		if(output == null) {
			return null;
		}
		if(!(output instanceof VideoFormat)) {
			return null;
		}
		if(!jpegFormat.matches(output)) {
			return null;
		}
		
	//	System.err.println("SetOutput! "+output);
		
	//	VideoFormat reqOutput = (VideoFormat)output; 
		
		
		
		//realOutput = new JPEGFormat(realInput.getSize(),Format.NOT_SPECIFIED,Format.intArray,20.0f,70,JPEGFormat.DEC_422);
		realOutput = new VideoFormat(VideoFormat.JPEG,realInput.getSize(),Format.NOT_SPECIFIED,Format.byteArray,15.0f);
//		realOutput = reqOutput;
		
		return realOutput;
	}

	public int process(Buffer in, Buffer out) {
		
//		System.err.print(".");
		
		int[] rgbData = (int[]) in.getData();
		WritableRaster raster = WritableRaster.createInterleavedRaster(
			DataBuffer.TYPE_INT,
			realInput.getSize().width,
			realInput.getSize().height,
			3,
			null
		);
		
		ColorModel cm = ColorModel.getRGBdefault();
		
		raster.setPixels(0,0,realInput.getSize().width,realInput.getSize().height,rgbData);
		BufferedImage img = new BufferedImage(cm, raster, true, new Hashtable());
		Buffer tmp = ImageToBuffer.createBuffer(img, realOutput.getFrameRate());
		out.copy(tmp,true);
		
		return BUFFER_PROCESSED_OK;
	}

	public String getName() {
		return "RGBtoJPEGEncoder";
	}

	public void open() throws ResourceUnavailableException {
	}

	public void close() {
	}

	public void reset() {
	}

	public Object[] getControls() {
		return null;
	}

	public Object getControl(String arg0) {
		return null;
	}

}
