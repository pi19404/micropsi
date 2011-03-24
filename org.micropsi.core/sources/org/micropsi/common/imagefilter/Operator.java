package org.micropsi.common.imagefilter;

import java.awt.image.WritableRaster;

public class Operator {

	public static final double[][] LAPLACE = {
		{-1,-1,-1,-1,-1},
		{-1,-1,-1,-1,-1},
		{-1,-1,24,-1,-1},
		{-1,-1,-1,-1,-1},
		{-1,-1,-1,-1,-1}		
	};

	public static final double[][] GAUSSIAN = {
		{2,4,5,4,2},
		{4,9,12,9,4},
		{5,12,15,12,5},
		{4,9,12,9,4},
		{2,4,5,4,2}		
	};

	public static final double[][] MEXICANHAT = {
		{0,0,-1,-1,-1,0,0},
		{0,-1,-3,-3,-3,-1,0},
		{-1,-3,0,7,0,-3,-1},
		{-1,-3,7,24,7,-3,-1},
		{-1,-3,0,7,0,-3,-1},
		{0,-1,-3,-3,-3,-1,0},
		{0,0,-1,-1,-1,0,0}
	};
	
	public static final double[][] SOBEL_HOR = {
		{1,2,1},
		{0,0,0},
		{-1,-2,-1},
	};

	public static final double[][] SOBEL_VER = {
		{-1,0,1},
		{-2,0,2},
		{-1,0,1},
	};
	
	private double[][] operator;
	private double factor;
	private double[] data;
	private int frame;
	
	public Operator(double[][] operator, int width, int height, double factor) {
		assert operator != null: "operator must not be null";
		assert operator.length > 0: "operator size must be > 0";
		assert operator[0].length == operator.length: "operator must be square";
		
		this.operator = operator;
		this.factor = factor;
		data =  new double[width*height];
		frame = (int)Math.floor(operator.length / 2.0);
	}
	
	public void apply(WritableRaster raster, boolean limit) {
		
		data = raster.getSamples(0,0,raster.getWidth(),raster.getHeight(),0,data);
		
		for(int y=frame;y<raster.getHeight()-frame;y++) {
			for(int x=frame;x<raster.getWidth()-frame;x++) {
				double pixel = 0;
				for(int i=-frame;i<=frame;i++) {					
					for(int j=-frame;j<=frame;j++) {							
						pixel += operator[i+frame][j+frame] * raster.getSample(x+i,y+j,0);
					}
				}					
				if(limit) {
					if(pixel > 255) pixel = 255;
					if(pixel < 0) pixel = 0;
				}
				data[y*raster.getWidth() + x] = 255-(pixel * factor);
			}
		}
		
		raster.setSamples(0,0,raster.getWidth(),raster.getHeight(),0,data);
	}

}
