package org.micropsi.comp.agent.micropsi.llhpc.level1;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.imagefilter.Operator;
import org.micropsi.common.utils.MultiPassInputStream;
import org.micropsi.comp.agent.micropsi.llhpc.level1.pattern.PatternIF;
import org.micropsi.comp.agent.micropsi.llhpc.level1.pattern.PatternIF.Pattern;
import org.micropsi.media.VideoServer;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetCycleIF;
import org.micropsi.nodenet.NetPropertiesIF;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.SensorDataSourceIF;


public class PatternCameraTester {

private class PatternDataSource implements SensorDataSourceIF {
		
		private int x,y;
		
		public PatternDataSource(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public String getDataType() {
			return "patch_"+x+"/"+y;
		}

		public double getSignalStrength() {
			if(netPattern == null) return 0;
			
			Pattern p = netPattern[x][y];
			switch(p) {
				case HORIZONTAL:
					return 0;
				case VERTICAL:
					return 1;
				case ASCENDING:
					return 0.5;
				case DESCENDING:
					return 0.75;
			}
			
			return -1;
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
	
	private JFrame imageFrame;
	private Logger logger;
	private VideoServer server;

	private BufferedImage image;
	private LocalNetFacade lineDetectors;
	private NetCycleIF lineDetectorCycle;

	private Pattern[][] netPattern;
	private LocalNetFacade cornerDetectors;
	private NetCycleIF cornerDetectorCycle;

	
	private int imagex;
	private int imagey;
	
	double[][] hor;
	double[][] ver;
	double[][] asc;
	double[][] dsc;

	double[][] corners; 
	double[][] cornerVals;
	
	class DrawComponent extends JComponent {
		public void paint(Graphics g) {
			//g.drawImage(image,0,0,320,240,null);
			for(int x=0;x<hor.length;x++) {
				for(int y=0;y<hor[0].length;y++) {

					Pattern which = classify(x,y);
					
					int dx = x;
					int dy = y-2;
					
					g.setColor(new Color(255,0,0));
					switch(which) {
						case HORIZONTAL:
							g.drawLine(dx*10,(dy*10)+5,(dx+1)*10,(dy*10)+5);
							break;
						case VERTICAL:
							g.drawLine((dx*10)+5,(dy*10),(dx*10)+5,(dy+1)*10);
							break;
						case ASCENDING:
							g.drawLine(dx*10,(dy+1)*10,(dx+1)*10,dy*10);
							break;
						case DESCENDING:
							g.drawLine(dx*10,dy*10,(dx+1)*10,(dy+1)*10);
							break;	
					}
					
					g.setColor(new Color(0,255,0));
					if(corners[x][y] > 0.85) {
						
						int cx = 0;
						int cy = 0;
						
						if(cornerVals[x][y] <= 0.125) {
							cx = -10;
							cy = 10;
						} else if(cornerVals[x][y] > 0.125 && cornerVals[x][y] <= 0.375) {
							cx=10;
							cy=10;
						} else if(cornerVals[x][y] > 0.375 && cornerVals[x][y] <= 0.625) {
							cx=10;
							cy=-10;							
						} else if(cornerVals[x][y] > 0.625 && cornerVals[x][y] <= 0.875) {
							cx=-10;
							cy=-10;														
						} else {
							cx = -10;
							cy = 10;							
						}
						
						g.drawLine(dx*10,dy*10,(dx*10)+cx,dy*10);
						g.drawLine(dx*10,dy*10,dx*10,(dy*10)+cy);
					}
				}
			}			
		}
	}
	
	public PatternCameraTester(Logger logger, VideoServer server) throws MicropsiException, IOException {
		super();
		
		this.server = server;
		this.logger = logger;
		
		NetPropertiesIF dummy = new NetPropertiesIF() {
			public String getProperty(String propertyName) {
				throw new RuntimeException("No such property: "+propertyName);
			}
		};
		
		lineDetectors = new LocalNetFacade(logger,dummy);
		lineDetectors.loadNet(new MultiPassInputStream(getClass().getResourceAsStream("detectors.mpn")),false);
		for(int x=0;x<PatternIF.SIZE;x++) {
			for(int y=0;y<PatternIF.SIZE;y++) {
				lineDetectors.getSensorRegistry().registerSensorDataProvider(new PixelDataSource(x,y));
			}
		}
		lineDetectorCycle = lineDetectors.getCycle();

				
		cornerDetectors = new LocalNetFacade(logger,dummy);
		cornerDetectors.loadNet(new MultiPassInputStream(getClass().getResourceAsStream("corners.mpn")),false);
		for(int x=0;x<5;x++) {
			for(int y=0;y<5;y++) {
				cornerDetectors.getSensorRegistry().registerSensorDataProvider(new PatternDataSource(x,y));
			}
		}
		cornerDetectorCycle = cornerDetectors.getCycle();		
		
		netPattern = new Pattern[5][5];
		image = server.grabImageAWT(320,240,BufferedImage.TYPE_BYTE_GRAY);
		
		hor = new double[image.getWidth()/10][image.getHeight()/10];
		ver = new double[image.getWidth()/10][image.getHeight()/10];
		asc = new double[image.getWidth()/10][image.getHeight()/10];
		dsc = new double[image.getWidth()/10][image.getHeight()/10];
		
		corners = new double[image.getWidth()/10][image.getHeight()/10];
		cornerVals = new double[image.getWidth()/10][image.getHeight()/10];
		
		imageFrame = new JFrame("Line detector image");
		imageFrame.setSize(320,240);
		imageFrame.add(new DrawComponent());
		imageFrame.setAlwaysOnTop(true);
		imageFrame.setVisible(true);
		imageFrame.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				System.exit(0);
			}
		});
		
	}
	
//	Operator gaussFilter = new Operator(Operator.GAUSSIAN,320,240,1.0/115);
	Operator laplaceFilter = new Operator(Operator.LAPLACE,320,240,1.0);
	Operator hatFilter = new Operator(Operator.MEXICANHAT,320,240,1.0);
	
	public void detectLines() throws MicropsiException {
		image = server.grabImageAWT(320,240,BufferedImage.TYPE_BYTE_GRAY);
		hatFilter.apply(image.getRaster(),true);
		//laplaceFilter.apply(image.getRaster(),true);
		for(imagex=0;imagex<image.getWidth()/10;imagex++) {
			for(imagey=0;imagey<image.getHeight()/10;imagey++) {		
				lineDetectorCycle.nextCycle(false);				
				hor[imagex][imagey] = ((Node)lineDetectors.getEntity("100-050918/191642")).getGenActivation();
				ver[imagex][imagey] = ((Node)lineDetectors.getEntity("3-050925/163530")).getGenActivation();
				asc[imagex][imagey] = ((Node)lineDetectors.getEntity("0-050927/151556")).getGenActivation();
				dsc[imagex][imagey] = ((Node)lineDetectors.getEntity("1-050927/151556")).getGenActivation();
			}			
		}

		for(imagex=3;imagex<(image.getWidth()/10)-3;imagex++) {
			for(imagey=3;imagey<(image.getHeight()/10)-3;imagey++) {
				
				for(int x=0;x<5;x++) {
					for(int y=0;y<5;y++) {
						netPattern[x][y] = classify(imagex+(x-2),imagey+(y-2));
					}
				}
				
				cornerDetectorCycle.nextCycle(false);
				cornerDetectorCycle.nextCycle(false);
				cornerDetectorCycle.nextCycle(false);
				cornerVals[imagex][imagey] = ((Node)cornerDetectors.getEntity("100-050918/191642")).getGenActivation();
				corners[imagex][imagey] = ((Node)cornerDetectors.getEntity("70-050927/213642")).getGenActivation();
			}			
		}

		
		try {
			Thread.sleep(100);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		imageFrame.repaint();
	}
	
	private Pattern classify(int x, int y) {
		double highest = hor[x][y];
		Pattern which = Pattern.HORIZONTAL;

		if (ver[x][y] > highest) {
			highest = ver[x][y];
			which = Pattern.VERTICAL;
		}
		if (asc[x][y] > highest) {
			highest = asc[x][y];
			which = Pattern.ASCENDING;
		}
		if (dsc[x][y] > highest) {
			highest = dsc[x][y];
			which = Pattern.DESCENDING;
		}

		if (highest < 0.6) {
			which = Pattern.UNCLEAR;
		}
		
		return which;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		Properties props = new Properties();
		props.setProperty("name", "camera");
		props.setProperty("type", "net-rtp");
		props.setProperty("devicename","239.60.60.60:51372");		
		props.setProperty("size", "320x240");

		VideoServer server = new VideoServer("camera", props, null,false);
		server.showVideo();
		
		Thread.sleep(1000);

		Logger l = Logger.getRootLogger();
		l.setLevel(Level.DEBUG);
		l.addAppender(new ConsoleAppender(new SimpleLayout()));

		PatternCameraTester p = new PatternCameraTester(l,server);
		
		while(true) {
			p.detectLines();
		}
	}


}

