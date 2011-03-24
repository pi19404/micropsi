package org.micropsi.comp.agent.micropsi.llhpc.level1.pattern;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import org.micropsi.comp.agent.micropsi.llhpc.level1.pattern.PatternIF.Noise;
import org.micropsi.comp.agent.micropsi.llhpc.level1.pattern.PatternIF.Pattern;

public class PatternGenerator {

	private static PatternGenerator instance = new PatternGenerator();
	
//	private BufferedImage horizontal;
//	private BufferedImage vertical;
//	private BufferedImage ascending;
//	private BufferedImage descending;
//	
	private PatternGenerator() {
//		try {
//			horizontal = ImageIO.read(getClass().getResourceAsStream("horizontal.png"));
//			vertical = ImageIO.read(getClass().getResourceAsStream("vertical.png"));
//			ascending = ImageIO.read(getClass().getResourceAsStream("ascending.png"));
//			descending = ImageIO.read(getClass().getResourceAsStream("descending.png"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	public static PatternGenerator getInstance() {
		return instance;
	}
	
	public BufferedImage generatePattern(Pattern p, Noise n) {
		return generatePattern(p,n,true);
	}
	
	public BufferedImage generatePattern(Pattern p, Noise n, boolean randomize) {
		BufferedImage img = new BufferedImage(10,10,BufferedImage.TYPE_BYTE_GRAY);
		
		if(randomize) {
			drawPatternOnImage(img,p,1.0);	
		} else {
			drawPatternOnImageWithoutRandom(img,p);
		}
		
		addNoiseToImage(img,n,1.0);
		
		return img;
	}

	public BufferedImage generateDirection(double dir, double noise, boolean randomize) {
		BufferedImage img = new BufferedImage(10,10,BufferedImage.TYPE_BYTE_GRAY);

		Graphics2D g = img.createGraphics();
		g.setColor(new Color(255,255,255));
		g.fillRect(0,0,PatternIF.SIZE,PatternIF.SIZE);
		
		dir *= Math.PI;

		if(dir >= Math.PI/2) {
			double x = -Math.cos(dir) * 15;
			double y = Math.sin(dir) * 15;
			
			int shiftx = -3 + (5 - (int)Math.round(dir));
			if(randomize) shiftx += -6 + (int)Math.round(Math.random()*6);
			if(shiftx < -6) shiftx = -6;
						
			g.setColor(new Color(0,0,0));
			g.drawLine(5+shiftx,0,5+(int)Math.round(x)+shiftx,(int)Math.round(y));
			g.drawLine(6+shiftx,0,5+(int)Math.round(x)+shiftx+1,(int)Math.round(y));
			
			g.setColor(new Color(128,128,128));
			g.drawLine(4+shiftx,0,5+(int)Math.round(x)+shiftx-1,(int)Math.round(y));
			g.drawLine(7+shiftx,0,5+(int)Math.round(x)+shiftx+2,(int)Math.round(y));
		} else if(dir >= 0) {
			double x = 10 - (Math.cos(dir) * 15);
			double y = Math.sin(dir) * 15;
			
			int shifty = 5 - (int)Math.round(dir*10);
			if(randomize) shifty += -2 + (int)Math.round(Math.random()*4);
			if(shifty < -6) shifty = -6;
						
			g.setColor(new Color(0,0,0));
			g.drawLine(10,shifty,(int)Math.round(x),(int)Math.round(y)+shifty);
			g.drawLine(10,shifty+1,(int)Math.round(x),(int)Math.round(y)+shifty+1);

			g.setColor(new Color(128,128,128));
			g.drawLine(10,shifty-1,(int)Math.round(x),(int)Math.round(y)+shifty-1);
			g.drawLine(10,shifty+2,(int)Math.round(x),(int)Math.round(y)+shifty+2);

		}
		
		addNoiseToImage(img,Noise.LOWNOISE,noise);
		
		return img;
	}

	
	private void drawPatternOnImage(BufferedImage img, Pattern pattern, double mult) {

		Graphics2D g = img.createGraphics();
		g.setColor(new Color(255,255,255));
		g.fillRect(0,0,PatternIF.SIZE,PatternIF.SIZE);
			
		switch(pattern) {
			case UNCLEAR:
				break;
			case HORIZONTAL:
				int r = (int)Math.round(Math.random() * 10);
				
				g.setColor(new Color(0,0,0));
				g.drawLine(0,r,10,r);
				g.drawLine(0,r+1,10,r+1);
				
				g.setColor(new Color(128,128,128));
				g.drawLine(0,r-1,10,r-1);
				g.drawLine(0,r+2,10,r+2);
				
				break;
			case VERTICAL:
				r = (int)Math.round(Math.random() * 10);

				g.setColor(new Color(0,0,0));
				g.drawLine(r,0,r,10);
				g.drawLine(r+1,0,r+1,10);
				
				g.setColor(new Color(128,128,128));
				g.drawLine(r-1,0,r-1,10);
				g.drawLine(r+2,0,r+2,10);

				break;
			case ASCENDING:
				r = (int)Math.round(Math.random() * 5);
				r+=5;
				
				g.setColor(new Color(0,0,0));
				g.drawLine(0,r,r,0);
				g.drawLine(0,r+1,r+1,0);
				
				g.setColor(new Color(128,128,128));
				g.drawLine(0,r-1,r-1,0);
				g.drawLine(0,r+2,r+2,0);
				
				break;
			case DESCENDING:
				r = (int)Math.round(Math.random() * 5);
				r+=5;
				
				g.setColor(new Color(0,0,0));
				g.drawLine(0,10-r,r,10);
				g.drawLine(0,(10-r)-1,r+1,10);
				
				g.setColor(new Color(128,128,128));
				g.drawLine(0,(10-r)-2,r+2,10);
				g.drawLine(0,(10-r)+1,r-1,10);
				
				break;				
		}
		
	}

	private void drawPatternOnImageWithoutRandom(BufferedImage img, Pattern pattern) {

		Graphics2D g = img.createGraphics();
		g.setColor(new Color(255,255,255));
		g.fillRect(0,0,PatternIF.SIZE,PatternIF.SIZE);

		g.setColor(new Color(0,0,0));
		
		switch(pattern) {
			case UNCLEAR:
				break;
			case HORIZONTAL:	
				g.drawLine(0,5,10,5);
				break;
			case VERTICAL:
				g.drawLine(5,0,5,10);
				break;
			case ASCENDING:
				g.drawLine(0,10,10,0);
				break;
			case DESCENDING:
				g.drawLine(0,0,10,10);
				break;				
		}
		
	}	
	
//	private void drawPatternOnImage(BufferedImage img, Pattern pattern, double mult) {
//		
//		BufferedImage toDraw = null;
//		
//		switch(pattern) {
//			case UNCLEAR:
//				toDraw = new BufferedImage(PatternIF.SIZE,PatternIF.SIZE,BufferedImage.TYPE_BYTE_GRAY);
//				Graphics2D g = toDraw.createGraphics();
//				g.setColor(new Color(255,255,255));
//				g.fillRect(0,0,PatternIF.SIZE,PatternIF.SIZE);
//				break;
//			case HORIZONTAL:
//				toDraw = horizontal;
//				break;
//			case VERTICAL:
//				toDraw = vertical;
//				break;
//			case ASCENDING:
//				toDraw = ascending;
//				break;
//			case DESCENDING:
//				toDraw = descending;
//				break;				
//		}
//		
//		WritableRaster raster = img.getRaster();
//		for(int x=0;x<PatternIF.SIZE;x++) {
//			for(int y=0;y<PatternIF.SIZE;y++) {
//				raster.setPixel(x,y,new double[] {toDraw.getRaster().getSample(x,y,0)*mult});
//			}
//		}
//	}
	
	private void addNoiseToImage(BufferedImage img, Noise noise, double nfact) {

		WritableRaster raster = img.getRaster();
		switch(noise) {
			case NONOISE:
				break;
			case HIGHNOISE:
				for(int x=0;x<img.getWidth();x++) {
					for(int y=0;y<img.getHeight();y++) {
						double oldPixel = raster.getSample(x,y,0);
						raster.setPixel(x,y,new double[] {oldPixel * Math.random()* nfact});
					}
				}
				break;
			case LOWNOISE:
				for(int x=0;x<img.getWidth();x++) {
					for(int y=0;y<img.getHeight();y++) {
						double oldPixel = raster.getSample(x,y,0);
						raster.setPixel(x,y,new double[] {(oldPixel / 2.0) + ((oldPixel/2.0) * Math.random()*nfact)});					}
				}
				break;
		}		
	}
}
