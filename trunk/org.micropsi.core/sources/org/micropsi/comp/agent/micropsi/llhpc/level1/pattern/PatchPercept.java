package org.micropsi.comp.agent.micropsi.llhpc.level1.pattern;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.micropsi.comp.agent.aaa.PerceptTranslatorIF;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.comp.agent.micropsi.llhpc.level1.pattern.PatternIF.Noise;
import org.micropsi.comp.agent.micropsi.llhpc.level1.pattern.PatternIF.Pattern;
import org.micropsi.comp.messages.MPercept;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.SensorDataSourceIF;

public class PatchPercept implements PerceptTranslatorIF {

	class PatternComponent extends JComponent {
		
		private double rot = 0;
		
		public PatternComponent() {
			this.addMouseListener(new MouseAdapter() {
				
				double angle = 0;
				
				public void mouseClicked(MouseEvent e) {
					Noise n = Noise.LOWNOISE;					
					if(e.getX()<50 && e.getX()>0) {
						angle = 0;
					} else if(e.getX()<100 && e.getX()>50) {
						angle = Math.PI/2;
					} else if(e.getX()<150 && e.getX()>100) {
						angle = Math.PI / 4;
					} else if(e.getX()<200 && e.getX()>150) {
						angle = Math.PI/2 + Math.PI/4;
					} 
					
					angle /= Math.PI; 				
					patternImage = PatternGenerator.getInstance().generateDirection(angle,1.0,true);
					
					if(e.getX()<250 && e.getX()>200) {
						n = Noise.LOWNOISE;
						rot += 0.1;
						if(rot > 0.9) rot = 0;
						
						angle = rot;
						//angle /= Math.PI;
						patternImage = PatternGenerator.getInstance().generateDirection(angle,1.0,true);
					} else if(e.getX()<300 && e.getX()>250) {
						patternImage = PatternGenerator.getInstance().generateDirection(Math.random()*0.9,1.0,true);
					}
					
					PatternComponent.this.repaint();
				}		
			});
		}
		
		public void paint(Graphics g) {
			
			g.setColor(new Color(0,0,0));
			g.fillRect(0,0,300,50);
			
			g.setColor(new Color(255,255,255));
			g.draw3DRect(0,0,50,50,true);
			g.drawLine(10,25,40,25);
			
			g.draw3DRect(50,0,50,50,true);
			g.drawLine(50+25,10,50+25,40);
						
			g.draw3DRect(100,0,50,50,true);
			g.drawLine(100+10,40,100+40,10);
			
			g.draw3DRect(150,0,50,50,true);
			g.drawLine(150+10,10,150+40,40);
			
			g.draw3DRect(200,0,50,50,true);
			g.drawString("~",220,30);
			
			g.draw3DRect(250,0,50,50,true);
			g.drawString("x",275,30);
			
			g.draw3DRect(300,0,50,50,true);
			if(patternImage != null) {
				g.drawImage(patternImage,300,0,45,48,null);
			}
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
			if(patternImage == null) return 0;
			
			return (double)patternImage.getRaster().getSample(x,y,0) / (double)255;
		}
		
	}
	
	private static final String TYPE = "PATCH";
	
	private LocalNetFacade net;
	private MicroPsiAgent agent;
	private Logger logger;
	
	private BufferedImage patternImage;
	
	public PatchPercept(LocalNetFacade net, MicroPsiAgent agent, Logger logger) {
		this.net = net;
		this.agent = agent;
		this.logger = logger;
		
		for(int x=0;x<PatternIF.SIZE;x++) {
			for(int y=0;y<PatternIF.SIZE;y++) {
				net.getSensorRegistry().registerSensorDataProvider(new PixelDataSource(x,y));
			}
		}
		
		JFrame patternFrame = new JFrame("Patterns");
		patternFrame.setSize(352,75);
		patternFrame.add(new PatternComponent());
		patternFrame.setEnabled(true);
		patternFrame.setAlwaysOnTop(true);
		patternFrame.setVisible(true);
	}

	public String getPerceptID() {
		return TYPE;
	}

	public void receivePercept(MPercept percept) {
	}

	public void shutdown() {
	}

}
