package org.micropsi.comp.agent.micropsi.llhpc.level2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.micropsi.comp.agent.micropsi.llhpc.level1.LineDetectorWOHypotheses;
import org.micropsi.comp.agent.micropsi.llhpc.level1.pattern.PatternIF.Pattern;


public class SampleGenerator {

	private static SampleGenerator instance = new SampleGenerator();
	
	public static SampleGenerator getInstance() {
		return instance;
	}
	
	private LineDetectorWOHypotheses detector;
	
	private SampleGenerator() {		
	}
	
	public double[][] generateSample(BufferedImage img, double randomThreshold, Logger logger) {
		try {		

			try {
				detector = new LineDetectorWOHypotheses(logger,32,24);
			} catch (Exception e) {
				e.printStackTrace();
			}

			detector.detectLines(img,false);
			
			double[][] r = detector.getAngle();
			double[][] grey = detector.getGrey();
			
			for(int x=0;x<r.length;x++) {
				for(int y=0;y<r[0].length;y++) {
					if(grey[x][y] > 0.62) {
						r[x][y] = -1;
					}
					
					if(Math.random() > randomThreshold) {						
						if(Math.random() > randomThreshold) {
							r[x][y] = -1;
						} else {
							r[x][y] = Math.random();	
						}
					}
				}
			}				
			return r;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Pattern[][] classify(double[][] data) {
		
		Pattern[][] p = new Pattern[data.length][data[0].length];
		
		for(int x=0;x<data.length;x++) {
			for(int y=0;y<data[0].length;y++) {
				Pattern which = Pattern.UNCLEAR;
				if(data[x][y] > 0) {
					if(data[x][y] > 0 && data[x][y] <= 0.25) which = Pattern.HORIZONTAL; else
					if(data[x][y] > 0.25 && data[x][y] <= 0.35) which = Pattern.ASCENDING; else
					if(data[x][y] > 0.35 && data[x][y] <= 0.7) which = Pattern.VERTICAL; else
					if(data[x][y] > 0.7 && data[x][y] <= 0.875) which = Pattern.DESCENDING; else
					which = Pattern.HORIZONTAL;			
				}
				p[x][y] = which;
			}
		}
		
		return p;
	}
	
	public static void createEllipsoidHypothesis(BufferedImage img, int x, int y, int width, int height) {
		Graphics2D g = img.createGraphics();
		
		g.setColor(new Color(255,255,255));
		g.fillRect(0,0,320,240);
		
		g.setColor(new Color(0,0,0));
		g.setStroke(new BasicStroke(5));
		g.drawOval(x,y,width,height);
	
	}

	public static void createRectangularHypothesis(BufferedImage img, int x, int y, int width, int height) {
		Graphics2D g = img.createGraphics();
		
		g.setColor(new Color(255,255,255));
		g.fillRect(0,0,320,240);
		
		g.setColor(new Color(0,0,0));
		g.setStroke(new BasicStroke(5));
		g.drawRect(x,y,width,height);
	
	}

	
	public static void main(String[] args) throws Exception {
		
		final Logger logger = Logger.getRootLogger();
		logger.addAppender(new ConsoleAppender(new SimpleLayout()));
		
		class LinePatternComponent extends JComponent {
			
			BufferedImage img = new BufferedImage(320,240,BufferedImage.TYPE_BYTE_GRAY); 
			Pattern[][] p;
			
			public LinePatternComponent() {
				this.addMouseListener(new MouseAdapter() {
							
					public void mouseClicked(MouseEvent e) {
						
						createRectangularHypothesis(img, 95, 22, 120, 160);
						p = classify(SampleGenerator.getInstance().generateSample(img,1,logger));
												
						repaint();
					}		
				});
			}
			
			public void paint(Graphics g) {
				if(p != null) {

					g.drawImage(
						img,
						0,
						0,
						320,
						240,
						null
					);
					
					g.setColor(new Color(255,0,0));
					drawLinePattern(g,p);
				}
			}
		}
				
		JFrame patternFrame = new JFrame("Test");
		patternFrame.setSize(320,240);
				
		patternFrame.add(new LinePatternComponent());
		patternFrame.setEnabled(true);
		patternFrame.setAlwaysOnTop(true);
		patternFrame.setVisible(true);
		
	}
	
	public static void drawLinePattern(Graphics g, Pattern[][] pattern) {
		for(int x=0;x<pattern.length;x++) {	
			for(int y=0;y<pattern[0].length;y++) {

				int dx = x;
				int dy = y;
								
				Pattern which = pattern[x][y];
									
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
