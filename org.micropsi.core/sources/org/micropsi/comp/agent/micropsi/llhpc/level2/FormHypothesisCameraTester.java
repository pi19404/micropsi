package org.micropsi.comp.agent.micropsi.llhpc.level2;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import org.micropsi.media.VideoServer;

public class FormHypothesisCameraTester {

	class DrawComponent extends JComponent {
		public void paint(Graphics g) {
			
			g.drawImage(image,0,0,320,240,null);
			
//			double[][] hypo = circleHypo.getHypotheses();			
//			g.setColor(Color.RED);
//			SampleGenerator.drawLinePattern(g,SampleGenerator.classify(hypo));
			
			double[][] real = circleHypo.getLastMatchRealValues();			
			g.setColor(Color.RED);
			SampleGenerator.drawLinePattern(g,SampleGenerator.classify(real));
			
			g.setColor(Color.RED);
			g.drawString(Double.toString(circleHypoMatch),20,220);
//			((Graphics2D)g).setPaint()
		}
	}
	
	private Logger logger;
	private VideoServer server;
	private BufferedImage image;
	private Frame hypoFrame;
	private Operator hatFilter = new Operator(Operator.MEXICANHAT,320,240,1.0);
	
	private FormHypothesis circleHypo;
	private BufferedImage circleHypoImg;
	private double circleHypoMatch;
	
	public FormHypothesisCameraTester(Logger logger, VideoServer server) throws MicropsiException, IOException {
			
		this.server = server;
		this.logger = logger;
			
		image = server.grabImageAWT(320,240,BufferedImage.TYPE_BYTE_GRAY);
		
		circleHypoImg = new BufferedImage(320,240,BufferedImage.TYPE_BYTE_GRAY);
		SampleGenerator.createEllipsoidHypothesis(circleHypoImg,60,20,150,150);
		circleHypo = new FormHypothesis("circle",circleHypoImg,1.0,logger);
		
		hypoFrame = new JFrame("Hypothesis image");
		hypoFrame.setSize(320,240);
		hypoFrame.add(new DrawComponent());
		hypoFrame.setAlwaysOnTop(true);
		hypoFrame.setVisible(true);
		hypoFrame.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				System.exit(0);
			}
		});
		
	}
	
	private void checkHypos() throws MicropsiException {
		image = server.grabImageAWT(320,240,BufferedImage.TYPE_BYTE_GRAY);	
		
		hatFilter.apply(image.getRaster(),true);
		
		circleHypoMatch = circleHypo.calculateMatch(image,0,0);
		
		hypoFrame.repaint();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		
	}

	
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

		FormHypothesisCameraTester p = new FormHypothesisCameraTester(l,server);
		
		while(true) {
			p.checkHypos();
		}
	}
}
