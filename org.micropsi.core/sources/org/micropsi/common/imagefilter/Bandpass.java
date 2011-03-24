package org.micropsi.common.imagefilter;

import java.awt.image.WritableRaster;

public class Bandpass {

	private double lowBorder = 0;
	private double highBorder= 255;
	
	public Bandpass(double lowBorder, double highBorder) {
		this.lowBorder = lowBorder;
		this.highBorder = highBorder;
	}

	public void apply(WritableRaster raster, boolean dynamic) {
		for(int y=0;y<raster.getHeight()-0;y++) {
			for(int x=0;x<raster.getWidth()-0;x++) {
				double sample = raster.getSample(x,y,0);
				if(sample < lowBorder) sample = 0;
				if(sample > highBorder) sample = 255;
				raster.setSample(x,y,0,sample);
			}
		}
		
	}
}
