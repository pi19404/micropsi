package org.micropsi.comp.agent.micropsi.llhpc.level3;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.imagefilter.Operator;
import org.micropsi.comp.agent.micropsi.llhpc.level2.FormHypothesis;
import org.micropsi.comp.agent.micropsi.llhpc.level2.HypothesisIF;
import org.micropsi.comp.agent.micropsi.llhpc.level2.SampleGenerator;
import org.micropsi.comp.agent.micropsi.llhpc.level3.HypoSpace.AssessedHypo;
import org.micropsi.media.VideoServer;


public class MultiHypothesisCameraTester {

	class DrawComponent extends JComponent {
		public void paint(Graphics g) {
			
			g.drawImage(image,0,0,320,240,null);

			g.setColor(Color.RED);
			g.drawRect(objectAtX-5,10,10,10);

//			g.setColor(Color.GREEN);
			AssessedHypo a = hypoSpace.getBestHypothesis();
			if(a == null) {
				return;
			}
			
			ArrayList<FormHypothesis> forms = new ArrayList<FormHypothesis>();
			HypothesisIF bestHypo = a.hypo;
			if(bestHypo instanceof FormHypothesis) {
				forms.add((FormHypothesis)bestHypo);
			} else if(bestHypo instanceof GroupHypothesis) {
				forms.addAll(((GroupHypothesis)bestHypo).getFormHypotheses());
			}
			
			for(int i=0;i<forms.size();i++) {
				double[][] real = forms.get(i).getLastMatchRealValues();
				SampleGenerator.drawLinePattern(g,SampleGenerator.classify(real));
				
			}
						
			g.setColor(Color.RED);
			g.drawString(bestHypo.getName(),20,210);			

		}
	}
	
	private VideoServer server;
	private BufferedImage image;
	private Frame hypoFrame;
	private Operator hatFilter = new Operator(Operator.MEXICANHAT,320,240,1.0);
	
	private HypoSpace hypoSpace;
	
	private int objectAtX = 0;
	
	public MultiHypothesisCameraTester(Logger logger, VideoServer server) throws MicropsiException, IOException {
			
		goon = true;
		
		this.server = server;
			
		image = server.grabImageAWT(320,240,BufferedImage.TYPE_BYTE_GRAY);
		
		hypoSpace = new HypoSpace(new TestHypothesesGenerator(logger));
				
		hypoFrame = new JFrame("Hypothesis image");
		hypoFrame.setSize(320,240);
		hypoFrame.add(new DrawComponent());
		hypoFrame.setAlwaysOnTop(true);
		hypoFrame.setVisible(true);
		hypoFrame.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				goon = false;
			}
		});
		
	}
	
	static boolean goon = true;
	boolean checkCurrent = false;
	int ssx = 0;
	int ssy = 0;
	
	private void checkHypos() throws MicropsiException {
		image = server.grabImageAWT(320,240,BufferedImage.TYPE_BYTE_GRAY);
		hatFilter.apply(image.getRaster(),true);
		
//		int maxFlipsAt = 0;
//		int numberOfMaxFlips = 0;
		
		int sumflips = 0;
		double flipr = 0;
		
		// todo: go to 300 with correct camera image
		for(int x=20;x<300;x++) {
			
			boolean high = image.getRaster().getSample(x,0,0) > 128;
			int flips = 0;
			for(int y=20;y<220;y++) {
				double val = image.getRaster().getSample(x,y,0);
				if(high && val < 128) {
					flips++;
					high = false;
				} else if(!high && val > 128) {
					flips++;
					high = true;
				}
			}
			flipr += flips*(x-20); 
			sumflips += flips;
			
//			if(flips > numberOfMaxFlips) {
//				numberOfMaxFlips = flips;
//				maxFlipsAt = x;
//			}
		}
		
		int e = (int)Math.round(flipr/sumflips);
		this.objectAtX = e;
		
		if(((objectAtX / 10) - 16) < ssx) ssx--;
		if(((objectAtX / 10) - 16) > ssx) ssx++;
		//ssx = (maxFlipsAt / 10) - 16;
		
		HypothesisIF hypo = hypoSpace.getNextToTest().hypo;
		double match = hypo.calculateMatch(image,ssx,ssy);		
		hypoSpace.update(hypo,match);
		
		hypoFrame.repaint();
		try {
			Thread.sleep(100);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}

		
	}
	
	public static void doTheJob(Logger l, VideoServer server) throws MicropsiException, IOException {
		
		MultiHypothesisCameraTester p = new MultiHypothesisCameraTester(l,server);
		while(goon) {
			p.checkHypos();
		}
		
	}

	
	public static void main(String[] args) throws Exception{
		
		Properties props = new Properties();
//		props.setProperty("name", "camera");
//		props.setProperty("type", "net-rtp");
//		props.setProperty("devicename","239.60.60.60:51372");		
//		props.setProperty("size", "320x240");

		props.setProperty("name", "camera");
		props.setProperty("type", "camera");
		props.setProperty("devicename","v4l:Generic Zc0305b:0");		
		props.setProperty("size", "320x240");
		props.setProperty("encoding", "rgb");

		
		VideoServer server = new VideoServer("camera", props, null,false);
		server.showVideo();
		
		Thread.sleep(1000);

		Logger l = Logger.getRootLogger();
		l.setLevel(Level.DEBUG);
		l.addAppender(new ConsoleAppender(new SimpleLayout()));

		doTheJob(l,server);
	}
}
