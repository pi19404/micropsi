package org.micropsi.comp.agent.micropsi.llhpc.level1;

import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.imagefilter.Operator;
import org.micropsi.common.utils.MultiPassInputStream;
import org.micropsi.comp.agent.micropsi.llhpc.level1.pattern.PatternGenerator;
import org.micropsi.comp.agent.micropsi.llhpc.level1.pattern.PatternIF;
import org.micropsi.comp.agent.micropsi.llhpc.level1.pattern.PatternIF.Noise;
import org.micropsi.comp.agent.micropsi.llhpc.level1.pattern.PatternIF.Pattern;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetCycleIF;
import org.micropsi.nodenet.NetPropertiesIF;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.SensorDataSourceIF;


public class PatternTester {

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
//			if(tested == null) return 0;
//			return (double)tested.getRaster().getSample(x,y,0) / (double)255;
		}
		
	}
	
	private JFrame imageFrame;
	private BufferedImage image;
//	private BufferedImage tested;
	
	private int imagex;
	private int imagey;
	
	class DrawComponent extends JComponent {
		public void paint(Graphics g) {
			if (image != null)
				g.drawImage(image, 0, 0, null);
		}
	}
		
//		public void paint(Graphics g) {
//			
//			g.setColor(new Color(0,0,0));
//			g.fillRect(0,0,300,50);
//			
//			g.setColor(new Color(255,255,255));
//			g.draw3DRect(0,0,50,50,true);
//			g.drawLine(10,25,40,25);
//			
//			g.draw3DRect(50,0,50,50,true);
//			g.drawLine(50+25,10,50+25,40);
//						
//			g.draw3DRect(100,0,50,50,true);
//			g.drawLine(100+10,40,100+40,10);
//			
//			g.draw3DRect(150,0,50,50,true);
//			g.drawLine(150+10,10,150+40,40);
//			
//			g.draw3DRect(200,0,50,50,true);
//			g.drawString("~",220,30);
//			
//			g.draw3DRect(250,0,50,50,true);
//			g.drawString("x",275,30);
//			
//			g.draw3DRect(300,0,50,50,true);
//			if(currentPattern != null) {
//				g.drawImage(currentPattern,300,0,45,48,null);
//			}
//		}

	
	public PatternTester() {
		super();
		
		imageFrame = new JFrame("Image");
		imageFrame.setAlwaysOnTop(true);
		imageFrame.setVisible(false);
		imageFrame.addComponentListener(new ComponentAdapter() {
			public void componentHidden(ComponentEvent e) {
				System.exit(0);
			}
		});
		
	}

	public void openImage() throws FileNotFoundException, IOException {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().endsWith("jpg") || pathname.getName().endsWith("png") || pathname.getName().endsWith("bmp");
			}

			public String getDescription() {
				return "image files";
			}
		});
				
		chooser.showOpenDialog(imageFrame);
				
		File file = chooser.getSelectedFile();
//		BufferedImage tmp = ImageIO.read(new FileInputStream(file));
//		image = new BufferedImage(tmp.getWidth(),tmp.getHeight(),BufferedImage.TYPE_BYTE_GRAY);
//		image.getGraphics().drawImage(tmp,0,0,tmp.getWidth(),tmp.getHeight(),0,0,tmp.getWidth(),tmp.getHeight(),null);
		image = ImageIO.read(new FileInputStream(file));		
		
		Operator gaussFilter = new Operator(Operator.GAUSSIAN,image.getWidth(),image.getHeight(),1.0/115);
		gaussFilter.apply(image.getRaster(),false);

//		Bandpass cutter = new Bandpass(110,200);
//		cutter.apply(image.getRaster(),false);
		
		Operator laplaceFilter = new Operator(Operator.LAPLACE,image.getWidth(),image.getHeight(),1.0);
		laplaceFilter.apply(image.getRaster(),true);

		imageFrame.setSize(image.getWidth()+20,image.getHeight()+30);
		imageFrame.add(new DrawComponent());
		imageFrame.setEnabled(true);
		imageFrame.setLocation(30,100);
		imageFrame.setVisible(true);	
		
	}
	
	double[][] hor;
	double[][] ver;
	double[][] asc;
	double[][] dsc;

//	double[][] shor;
//	double[][] sver;
//	double[][] sasc;
//	double[][] sdsc;

	
	public void detectLines() throws MicropsiException, IOException {
		
		Logger logger = Logger.getRootLogger();
		logger.addAppender(new ConsoleAppender(new SimpleLayout()));
		
		NetPropertiesIF dummy = new NetPropertiesIF() {
			public String getProperty(String propertyName) {
				throw new RuntimeException("No such property: "+propertyName);
			}
		};
		
//		LocalNetFacade horizontal = new LocalNetFacade(logger,dummy);
//		LocalNetFacade vertical = new LocalNetFacade(logger,dummy);
//		LocalNetFacade ascending = new LocalNetFacade(logger,dummy);
//		LocalNetFacade descending = new LocalNetFacade(logger,dummy);
		
		LocalNetFacade detectors = new LocalNetFacade(logger,dummy);
		
//		horizontal.loadNet(new MultiPassInputStream(getClass().getResourceAsStream("horizontal.mpn")),false);
//		vertical.loadNet(new MultiPassInputStream(getClass().getResourceAsStream("vertical.mpn")),false);
//		ascending.loadNet(new MultiPassInputStream(getClass().getResourceAsStream("ascending.mpn")),false);
//		descending.loadNet(new MultiPassInputStream(getClass().getResourceAsStream("descending.mpn")),false);
		
		detectors.loadNet(new MultiPassInputStream(getClass().getResourceAsStream("halfdetectors.mpn")),false);
		
		for(int x=0;x<PatternIF.SIZE;x++) {
			for(int y=0;y<PatternIF.SIZE;y++) {
				detectors.getSensorRegistry().registerSensorDataProvider(new PixelDataSource(x,y));
//				horizontal.getSensorRegistry().registerSensorDataProvider(new PixelDataSource(x,y));
//				vertical.getSensorRegistry().registerSensorDataProvider(new PixelDataSource(x,y));
//				ascending.getSensorRegistry().registerSensorDataProvider(new PixelDataSource(x,y));
//				descending.getSensorRegistry().registerSensorDataProvider(new PixelDataSource(x,y));
			}
		}

		hor = new double[image.getWidth()/10][image.getHeight()/10];
		ver = new double[image.getWidth()/10][image.getHeight()/10];
		asc = new double[image.getWidth()/10][image.getHeight()/10];
		dsc = new double[image.getWidth()/10][image.getHeight()/10];

//		shor = new double[image.getWidth()/10][image.getHeight()/10];
//		sver = new double[image.getWidth()/10][image.getHeight()/10];
//		sasc = new double[image.getWidth()/10][image.getHeight()/10];
//		sdsc = new double[image.getWidth()/10][image.getHeight()/10];
		
//		NetCycleIF cycleHor = horizontal.getCycle();
//		NetCycleIF cycleVer = vertical.getCycle();
//		NetCycleIF cycleAsc = ascending.getCycle();
//		NetCycleIF cycleDsc = descending.getCycle();
	
		NetCycleIF cycle = detectors.getCycle();
		
		long start = System.currentTimeMillis();
		
		for(imagex=0;imagex<image.getWidth()/10;imagex++) {
			for(imagey=0;imagey<image.getHeight()/10;imagey++) {
//				tested = new BufferedImage(10,10,BufferedImage.TYPE_BYTE_GRAY);
//				tested.createGraphics().drawImage(image,0,0,10,10,(imagex*10),(imagey*10),(imagex*10)+10,(imagey*10)+10,null);
								
//				for(int i=0;i<3;i++) {
//					cycleHor.nextCycle(false);
//					cycleVer.nextCycle(false);
//					cycleAsc.nextCycle(false);
//					cycleDsc.nextCycle(false);
//				}
				cycle.nextCycle(false);
				
//				double verdictHor = ((Node)horizontal.getEntity("100-050918/191642")).getGenActivation();
//				double verdictVer = ((Node)vertical.getEntity("100-050918/191642")).getGenActivation();
//				double verdictAsc = ((Node)ascending.getEntity("100-050918/191642")).getGenActivation();
//				double verdictDsc = ((Node)descending.getEntity("100-050918/191642")).getGenActivation();

				double verdictHor = ((Node)detectors.getEntity("265-050922/155209")).getGenActivation();
				double verdictVer = ((Node)detectors.getEntity("376-050922/155227")).getGenActivation();
				double verdictAsc = ((Node)detectors.getEntity("487-050922/155345")).getGenActivation();
				double verdictDsc = ((Node)detectors.getEntity("598-050922/155441")).getGenActivation();

//				487-050922/155345
//				598-050922/155441
//				376-050922/155227
//				265-050922/155209
				
			    double highest = verdictHor;
			    Pattern which = Pattern.HORIZONTAL;
			    
			    if(verdictVer > highest) {
			     highest = verdictVer;
			     which = Pattern.VERTICAL;
			    }
			    if(verdictAsc > highest) {
			     highest = verdictAsc;
			     which = Pattern.ASCENDING;
			    }    
			    if(verdictDsc > highest) {
			     highest = verdictAsc;
			     which = Pattern.DESCENDING;
			    }
			    
			    if(highest < 0.4) {
			     which = Pattern.UNCLEAR;
			    }
			    			    
			    image.getGraphics().drawImage(
			     PatternGenerator.getInstance().generatePattern(which,Noise.NONOISE,false),
			     imagex*10,
			     (imagey)*10,
			     (imagex+1)*10,
			     (imagey+1)*10,
			     0,
			     0,
			     10,
			     10,
			     null
			    );
			    imageFrame.repaint();
			    
				
//				hor[imagex][imagey] = ((Node)horizontal.getEntity("100-050918/191642")).getGenActivation();
//				ver[imagex][imagey] = ((Node)vertical.getEntity("100-050918/191642")).getGenActivation();
//				asc[imagex][imagey] = ((Node)ascending.getEntity("100-050918/191642")).getGenActivation();
//				dsc[imagex][imagey] = ((Node)descending.getEntity("100-050918/191642")).getGenActivation();
			}			
		}
		
		
		System.err.println("ms: "+(System.currentTimeMillis()-start));

//		for(imagex=1;imagex<(image.getWidth()/10)-1;imagex++) {
//			for(imagey=1;imagey<(image.getHeight()/10)-1;imagey++) {
//				tested = new BufferedImage(10,10,BufferedImage.TYPE_BYTE_GRAY);
//				tested.createGraphics().drawImage(image,0,0,10,10,(imagex*10)+5,(imagey*10)+5,(imagex*10)+15,(imagey*10)+15,null);
//				
//				for(int i=0;i<3;i++) {
//					cycleHor.nextCycle(false);
//					cycleVer.nextCycle(false);
//					cycleAsc.nextCycle(false);
//					cycleDsc.nextCycle(false);
//				}
//				
//				shor[imagex][imagey] = ((Node)horizontal.getEntity("100-050918/191642")).getGenActivation();
//				shor[imagex][imagey] += hor[imagex-1][imagey-1] + hor[imagex][imagey-1] + hor[imagex][imagey] + hor[imagex-1][imagey];
//				
//				sver[imagex][imagey] = ((Node)vertical.getEntity("100-050918/191642")).getGenActivation() * 0.9;
//				sver[imagex][imagey] += ver[imagex-1][imagey-1] + ver[imagex][imagey-1] + ver[imagex][imagey] + ver[imagex-1][imagey];
//				
//				sasc[imagex][imagey] = ((Node)ascending.getEntity("100-050918/191642")).getGenActivation();
//				sasc[imagex][imagey] *= 4;
//				
//				sdsc[imagex][imagey] = ((Node)descending.getEntity("100-050918/191642")).getGenActivation();
//				sdsc[imagex][imagey] *= 4;
//				
//			}			
//		}

		
//		for(imagex=1;imagex<(image.getWidth()/10)-1;imagex++) {
//			for(imagey=1;imagey<(image.getHeight()/10)-1;imagey++) {
//				
//			    double highest = hor[imagex][imagey];
//			    Pattern which = Pattern.HORIZONTAL;
//			    
//			    if(ver[imagex][imagey] > highest) {
//			     highest = ver[imagex][imagey];
//			     which = Pattern.VERTICAL;
//			    }
//			    if(asc[imagex][imagey] > highest) {
//			     highest = asc[imagex][imagey];
//			     which = Pattern.ASCENDING;
//			    }    
//			    if(dsc[imagex][imagey] > highest) {
//			     highest = dsc[imagex][imagey];
//			     which = Pattern.DESCENDING;
//			    }
//			    
//			    if(highest < 2) {
//			     which = Pattern.UNCLEAR;
//			    }
//				
//				image.getGraphics().drawImage(
//			     PatternGenerator.getInstance().generatePattern(which,Noise.NONOISE),
//			     imagex*10,
//			     imagey*10,
//			     (imagex+1)*10,
//			     (imagey+1)*10,
//			     0,
//			     0,
//			     10,
//			     10,
//			     null
//			    );	
//			}
//		}
//	    imageFrame.repaint();

		
		ImageIO.write(image,"PNG",new File("C:/result.png"));
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		PatternTester p = new PatternTester();
		p.openImage();
		p.detectLines();
	}


}

