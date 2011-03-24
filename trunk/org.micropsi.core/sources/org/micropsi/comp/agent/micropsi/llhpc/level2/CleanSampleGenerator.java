package org.micropsi.comp.agent.micropsi.llhpc.level2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;


public class CleanSampleGenerator {

	public CleanSampleGenerator() {
		super();
	}
	
	public static double[][] generateElipsoidHypothesis(int cx, int cy, int xradius, int yradius) {

		double c = Math.sqrt(xradius * xradius + yradius * yradius);
		
		double f1x = cx - c;
		double f2x = cx + c;
		
		// - Winkel zwischen F2P und F1P
		// - Winkel halbieren
		// - Die Orthogonale dazu
		// - und auf [0,1] normieren mit 0 -> 1 -> horizontal
		
		double values[][] = new double[32][24];
		for(int x=0;x<values.length;x++) {
			for(int y=0;y<values[0].length;y++) {
				values[x][y] = -1;
			}
		}
		
		int x,y,xchange,ychange;
		int ellipseError;
		int twoASquare, twoBSquare;
		int stoppingx, stoppingy;
		
		twoASquare = 2 * xradius * xradius;
		twoBSquare = 2 * yradius * yradius;
		
		x = xradius;
		y = 0;
		
		xchange = yradius * yradius * (1 - 2*xradius); 
		ychange = xradius * xradius;
		
		ellipseError = 0;
		
		stoppingx = twoASquare * xradius;
		stoppingy = 0;
		
		while(stoppingx >= stoppingy) {			
			plot4EllipsePoints(cx,cy,x,y,values);
			y++;
			stoppingy += twoASquare;
			ellipseError += ychange;
			ychange += twoASquare;
			if((2*ellipseError + xchange) > 0) {
				x--;
				stoppingx -= twoBSquare;
				ellipseError += xchange;
				xchange += twoBSquare;
			}
		}
		
		x = 0;
		y = yradius;
		
		xchange = yradius * yradius;
		ychange = xradius * xradius * (1 - 2*yradius);
		
		ellipseError = 0;
		
		stoppingx = 0;
		stoppingy = twoASquare * yradius;
		
		while(stoppingx <= stoppingy) {
			plot4EllipsePoints(cx,cy,x,y,values);
			x++;
			stoppingx += twoBSquare;
			ellipseError += xchange;
			ychange += twoBSquare;
			if((2*ellipseError + ychange) > 0) {
				y--;
				stoppingx += twoASquare;
				ellipseError += ychange;
				ychange += twoASquare;
			}
		}
		
		return values;
	}

	private static void plot4EllipsePoints(int cx, int cy, int x, int y, double[][] values) {
		values[cx+x][cy+y] = 0.5;
		values[cx-x][cy+y] = 0.5;
		values[cx-x][cy-y] = 0.5;
		values[cx+x][cy-y] = 0.5;
	}
	

	// ----------------------------------------------------
	
	
	public static void drawHypothesisAsPattern(Graphics g, double[][] hypothesis) {
		for(int x=0;x<hypothesis.length;x++) {	
			for(int y=0;y<hypothesis[0].length;y++) {

				int dx = x;
				int dy = y;
								
				double val = hypothesis[x][y];
				
				if(val < 0) continue;
				
				if(val > 0 && val <= 0.125) {
					g.drawLine(dx*10,(dy*10)+5,(dx+1)*10,(dy*10)+5);					
				} else if(val > 0.125 && val <= 0.375) {
					g.drawLine(dx*10,dy*10,(dx+1)*10,(dy+1)*10);
				} else if(val > 0.375 && val <= 0.625) {
					g.drawLine((dx*10)+5,(dy*10),(dx*10)+5,(dy+1)*10);
				} else if(val > 0.625 && val <= 0.875) {
					g.drawLine(dx*10,(dy+1)*10,(dx+1)*10,dy*10);
				} else {
					g.drawLine(dx*10,(dy*10)+5,(dx+1)*10,(dy*10)+5);
				}					
			}	
		}		
	}
	
	public static void main(String[] args) throws Exception {
		
		final Logger logger = Logger.getRootLogger();
		logger.addAppender(new ConsoleAppender(new SimpleLayout()));
		
		class CleanHypoComponent extends JComponent {
			
			double[][] hypo;
			
			public CleanHypoComponent() {
				this.addMouseListener(new MouseAdapter() {
							
					public void mouseClicked(MouseEvent e) {
						hypo = generateElipsoidHypothesis(16, 12, 7, 5);
						repaint();
					}		
				});
			}
			
			public void paint(Graphics g) {
				if(hypo != null) {					
					g.setColor(new Color(255,0,0));
					drawHypothesisAsPattern(g, hypo);
				}
			}
		}
				
		JFrame patternFrame = new JFrame("Test");
		patternFrame.setSize(320,240);
				
		patternFrame.add(new CleanHypoComponent());
		patternFrame.setEnabled(true);
		patternFrame.setAlwaysOnTop(true);
		patternFrame.setVisible(true);
		
	}

}
