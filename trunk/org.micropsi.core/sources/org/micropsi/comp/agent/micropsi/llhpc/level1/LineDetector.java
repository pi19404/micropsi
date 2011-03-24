package org.micropsi.comp.agent.micropsi.llhpc.level1;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.utils.MultiPassInputStream;
import org.micropsi.comp.agent.micropsi.llhpc.level1.pattern.PatternIF;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetCycleIF;
import org.micropsi.nodenet.NetPropertiesIF;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.SensorDataSourceIF;


public class LineDetector {

	private class SignalDataSource implements SensorDataSourceIF {
		
		private double value = 0;
		private String type;
		
		public SignalDataSource(String type) {
			this.type = "patch_"+type;
		}

		public String getDataType() {
			return type;
		}

		public double getSignalStrength() {
			return value;
		}
		
		public void setValue(double value) {
			this.value = value;
		}
		
	}
	
	private class PixelDataSource implements SensorDataSourceIF {
		
		private int x,y;
		
		public PixelDataSource(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public String getDataType() {
			return "patch_"+x+"/"+y;
		}

		public double getSignalStrength() {
			if(image == null) return 0;
			return (double)image.getRaster().getSample((imagex*10)+x,(imagey*10)+y,0) / (double)255;
		}
		
	}
	
	int imagex = 0;
	int imagey = 0;
	
	private BufferedImage image;
	private SignalDataSource hypo;
	private Logger logger;
	private LocalNetFacade detectors;
	private NetCycleIF cycle;

	private double[][] angle;
	private double[][] grey;
	private double[][] hypoMatch;
	
	public LineDetector(Logger logger, int sizeX, int sizeY) throws MicropsiException, IOException {
		this.logger = logger;
		
		NetPropertiesIF dummy = new NetPropertiesIF() {
			public String getProperty(String propertyName) {
				throw new RuntimeException("No such property: "+propertyName);
			}
		};
		
		detectors = new LocalNetFacade(this.logger,dummy);
		detectors.loadNet(new MultiPassInputStream(getClass().getResourceAsStream("continuous-with-hypo.mpn")),false);
		
		for(int x=0;x<PatternIF.SIZE;x++) {
			for(int y=0;y<PatternIF.SIZE;y++) {
				detectors.getSensorRegistry().registerSensorDataProvider(new PixelDataSource(x,y));
			}
		}
		
		hypo = new SignalDataSource("hypo");
		detectors.getSensorRegistry().registerSensorDataProvider(hypo);

		cycle = detectors.getCycle();
		
		angle = new double[sizeX][sizeY];
		grey = new double[sizeX][sizeY];
		hypoMatch = new double[sizeX][sizeY];

	}
	
	public void detectLines(BufferedImage img, double hypotheses[][]) throws MicropsiException {
		image = img;
		
		imagex = 0;
		imagey = 0;
		hypo.setValue(Math.random());
//		cycle.nextCycle(false);
//		cycle.nextCycle(false);
//		cycle.nextCycle(false);
		
		for(imagex=0;imagex<image.getWidth()/10;imagex++) {
			for(imagey=0;imagey<image.getHeight()/10;imagey++) {
				double hypothesis = hypotheses != null ? hypotheses[imagex][imagey] : Math.random();
				detectLine(img,imagex,imagey,hypothesis,true);
			}			
		}
	}
	
	public void detectLine(BufferedImage img ,int x, int y, double hypothesis, boolean allowDelay) throws MicropsiException { 
		
		image = img;
		imagex = x;
		imagey = y;

		if(imagex > 0 && imagex < angle.length && imagey > 0 && imagey < angle[0].length) {
		
			hypo.setValue(hypothesis);
			cycle.nextCycle(false);
			if(!allowDelay) {
				cycle.nextCycle(false);
				cycle.nextCycle(false);
			}

			angle[imagex][imagey] = ((Node)detectors.getEntity("100-050918/191642")).getGenActivation();
			grey[imagex][imagey] = ((Node)detectors.getEntity("3-050925/163530")).getGenActivation();
			hypoMatch[imagex][imagey] = ((Node)detectors.getEntity("1-050926/134540")).getGenActivation();
		}
	}
	
	public double[][] getAngle() {
		return angle;
	}

	public double[][] getGrey() {
		return grey;
	}

	public double[][] getHypoMatch() {
		return hypoMatch;
	}

	public double getMatchThreshold() {
		return 0.62;
	}

}
