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


public class DirectionCameraTester {

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
	
	private JFrame imageFrame;
	private BufferedImage image;
	private SignalDataSource hypo;
	private Logger logger;
	private VideoServer server;
	private LocalNetFacade detectors;
	private NetCycleIF cycle;
	
	private int imagex;
	private int imagey;
	
	double[][] res;
	double[][] verd;
	
	int timeindex = 0;
	
	class DrawComponent extends JComponent {
		public void paint(Graphics g) {
			g.drawImage(image,0,0,320,240,null);
//			g.setColor(new Color(255,255,255));
//			g.fillRect(0,0,320,240);
			
			for(int x=1;x<res.length;x++) {
								
				for(int y=3;y<res[0].length;y++) {
	
					int dx = x;
					int dy = y-2;
					
					Pattern which = classify(verd[x][y],res[x][y]);
										
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
						case UNCLEAR:
							break;
					}
						
				}	
			}			
		}
	}
	
	public DirectionCameraTester(Logger logger, VideoServer server) throws MicropsiException, IOException {
		super();
		
		this.server = server;
		this.logger = logger;
		
		NetPropertiesIF dummy = new NetPropertiesIF() {
			public String getProperty(String propertyName) {
				throw new RuntimeException("No such property: "+propertyName);
			}
		};
		
		detectors = new LocalNetFacade(logger,dummy);
		detectors.loadNet(new MultiPassInputStream(getClass().getResourceAsStream("continuous-wo-hypo.mpn")),false);
		
		for(int x=0;x<PatternIF.SIZE;x++) {
			for(int y=0;y<PatternIF.SIZE;y++) {
				detectors.getSensorRegistry().registerSensorDataProvider(new PixelDataSource(x,y));
			}
		}
		
		hypo = new SignalDataSource("hypo");
		detectors.getSensorRegistry().registerSensorDataProvider(hypo);

		cycle = detectors.getCycle();
		
		image = server.grabImageAWT(320,240,BufferedImage.TYPE_BYTE_GRAY);
		
		res = new double[image.getWidth()/10][image.getHeight()/10];
		verd = new double[image.getWidth()/10][image.getHeight()/10];
		
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
				hypo.setValue(Math.random());
				cycle.nextCycle(false);
				
				double result = ((Node)detectors.getEntity("100-050918/191642")).getGenActivation();
				double verdict = ((Node)detectors.getEntity("3-050925/163530")).getGenActivation();
				
				res[imagex][imagey] = (res[imagex][imagey] + result) / 2;
				verd[imagex][imagey] = (verd[imagex][imagey] + verdict) / 2;
			}			
		}
		
		imageFrame.repaint();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private Pattern classify(double verd, double dir) {
		Pattern which = Pattern.UNCLEAR;
		
		if(verd < 0.62) {
//			if(res[x][y] > 0 && res[x][y] <= 0.125) which = Pattern.HORIZONTAL; else
//			if(res[x][y] > 0.125 && res[x][y] <= 0.375) which = Pattern.ASCENDING; else
//			if(res[x][y] > 0.375 && res[x][y] <= 0.625) which = Pattern.VERTICAL; else
//			if(res[x][y] > 0.625 && res[x][y] <= 0.875) which = Pattern.DESCENDING; else
//			which = Pattern.HORIZONTAL;
			if(dir > 0 && dir <= 0.25) which = Pattern.HORIZONTAL; else
			if(dir > 0.25 && dir <= 0.35) which = Pattern.ASCENDING; else
			if(dir > 0.35 && dir <= 0.7) which = Pattern.VERTICAL; else
			if(dir > 0.7 && dir <= 0.875) which = Pattern.DESCENDING; else
			which = Pattern.HORIZONTAL;			
		}
		return which;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		Properties props = new Properties();
		props.setProperty("name", "camera");
		props.setProperty("type", "camera");
		props.setProperty("devicename","vfw:Microsoft WDM Image Capture (Win32):0");
		props.setProperty("encoding", "yuv");
		props.setProperty("size", "320x240");
		props.setProperty("framerate", "-1");
		props.setProperty("maxdatalength", "153600");
//		props.setProperty("name", "camera");
//		props.setProperty("type", "net-rtp");
//		props.setProperty("devicename","239.60.60.60:51372");		
//		props.setProperty("size", "320x240");

		
		VideoServer server = new VideoServer("camera", props, null,false);
		server.showVideo();
		
		Thread.sleep(1000);

		Logger l = Logger.getRootLogger();
		l.setLevel(Level.DEBUG);
		l.addAppender(new ConsoleAppender(new SimpleLayout()));

		DirectionCameraTester p = new DirectionCameraTester(l,server);
		
		while(true) {
			p.detectLines();
		}
	}


}

