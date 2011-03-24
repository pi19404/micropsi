package org.micropsi.comp.robot.khepera8.camera;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.micropsi.media.VideoServer;

import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators.AttributeIterator;

public class KheperaFront extends Thread {
	private boolean debug=false;
	private String namehsv="HSVRawImageBlue1";
	private String namergb="RGBRawImageBlue1";
	private String path="/mnt/dweiller/NavigationProject/Matrices/250906/PictureCalibration/";
///////////////////////////////////////////////////////DEBUG
	private double[][]storehsv=new double[3][320*240];
	private double[][]storergb=new double[3][320*240];
	private static int count=0;
	private static double mean=0;
	

	class DrawComponent extends JComponent {
		public void paint(Graphics g) {
			if (lastCaptured != null)
				g.drawImage(lastCaptured, 0, 0, null);
		}
	}
	
	private BufferedImage lastCaptured = null;
	
	
	private int height,width;
	private double sendred,sendgreen,sendblue,sendyellow,modred,modgreen,modblue,modyell,lookred,lookyell,lookblue,lookgreen;
	
	private VideoServer server;
	private Logger logger;
	public static boolean calibration=true;
	

	public KheperaFront(VideoServer server, Logger logger) {
		logger.debug("Constructing khepera FrontCam with camera type ");
		this.server = server;
		this.logger = logger;
	}
	
	public void run() {
		logger.debug("Start tracking on video server "+server.getName());
		try {doFrontCamAnalysis();
		} catch (InterruptedException e) {
			logger.error("Tracker sleep interrupted",e);
		}
		logger.debug("Tracking stopped");
	}



	public void doFrontCamAnalysis() throws InterruptedException {
		
		float[] test = new float[3];
		
		while (true) {
			if(count<120 && debug)
				count++;
//			count++;
			
			
			
			int[] pix=new int[3];
			// calc every 33 ms
			Thread.sleep(33);
			// grab image
			lastCaptured = server.grabImageAWTRGB();
			WritableRaster raster=lastCaptured.getRaster();
			height = server.getVisualSize().height;
			width = server.getVisualSize().width;
			lookyell=lookgreen=lookred=lookblue=0;
				
			for(int j=0;j<width;j++){
				for(int i=0;i<height;i++){
					raster.getPixel(j,i,pix);
					int red=pix[0];
					int green=pix[1];
					int blue=pix[2];
					
					modred=modyell=modblue=modgreen=0;
					Color.RGBtoHSB(red,green,blue,test);
					
//					for the Frontcamera Settings
//					if(test[0]<0.16 && test[0]>0.05 && test[1]>0.6 && test[1]<0.9)
//						modyell=100;
//					if(test[0]<0.3 && test[0]>0.2 && test[1]>0.4 && test[1]<1)
//						modgreen=100;
//					if(test[0]<0.7 && test[0]>0.55 && test[1]>0.75 && test[1]<1)
//						modblue=100;
//					if(test[0]<1 && test[0]>0.91 && test[1]>0.8 && test[1]<1)
//						modred=100;
					
					
//					for the Omnidirectional Settings
					if(test[0]<0.11 && test[0]>0.05 && test[1]>0.75 && test[1]<1 && test[2]>0.4 && test[2]<0.85)
						modyell=100;
					if(test[0]<0.3 && test[0]>0.2 && test[1]>0.6 && test[1]<1 && test[2]>0.2 && test[2]<0.6)
						modgreen=100;
					if(test[0]<0.66 && test[0]>0.57 && test[1]>=0.9 && test[1]<=1 && test[2]>0.14 && test[2]<0.4)
						modblue=100;
					if(test[0]<=1 && test[0]>=0.96 && test[1]>=0.95 && test[1]<=1)
						modred=100;
					
					
					
					
					
					
					
					
					if(debug && count==100){
//						logger.debug("red "+red);
						Color.RGBtoHSB(red,green,blue,test);
						storehsv[0][j*height+i]=test[0];
						storehsv[1][j*height+i]=test[1];
						storehsv[2][j*height+i]=test[2];
						storergb[0][j*height+i]=red;
						storergb[1][j*height+i]=green;
						storergb[2][j*height+i]=blue;
//						logger.debug("RedTSored "+storergb[0][j*height+i]);
					}
					
//					
//					modred=red-green;
//					if(modred<100)
//						modred=0;
//					else modred=100;
//					
//					modgreen=green-blue;
//					if(modgreen<60)//45
//						modgreen=0;
//					else modgreen=100;
//					
//					modblue=blue-red;
//					if(modblue<50)//50
//						modblue=0;
//					else modblue=100;
//					
//					modyell=green-blue-modgreen;
//					//logger.debug(modyell);
//					if(modyell<50 || modyell>80)//50
//						modyell=0;
////					else modyell=100;
					
					
					
					lookred+=modred;
					lookgreen+=modgreen;
					lookblue+=modblue;
					lookyell+=modyell;
					if(drawComponent != null) {
						raster.setPixel(j, i, new int[] {(int)modyell,0,0 });
						}
					
				}
			}
			
			
			if(count==100 && debug){
				savePictureMatlabreadable(storehsv,namehsv,3*height*width);
				savePictureMatlabreadable(storergb,namergb,3*height*width);
				logger.debug("Stored Pictures");
			}
//			For the Frontcamera Settings;
//			280000
//			if (lookblue<201000){//200000
//				sendblue=0;
//			}else{ 
//				sendblue=1;
//			}
//			//
//			if(lookgreen<240000){//240000
//				sendgreen=0;
//			}else{
//				sendgreen=1;
//			}
//			
//			if(lookred<500000){//260000
//				sendred=0;
//			}else{
//				sendred=1;
//			}
//			//400000
//			if(lookyell<271000){//270000
//				sendyellow=0;
//			}else{
//				sendyellow=1;
//			}
			lookgreen-=29200;
			lookyell-=4200;
			lookred-=3550;
			lookblue-=8200;
			
//			Omnidirectional Camera
			if (lookblue<22000){//200000
				sendblue=0;
			}else{ 
				sendblue=1;
			}
			//
			if(lookgreen<18000){//240000
				sendgreen=0;
			}else{
				sendgreen=1;
			}
			
			if(lookred<20000){//260000
				sendred=0;
			}else{
				sendred=1;
			}
			//400000
			if(lookyell<14000){//270000
				sendyellow=0;
			}else{
				sendyellow=1;
			}
			
					
//			mean+=lookblue;
//			logger.debug("Mean "+mean/count);
			
			
			if(drawComponent != null) {
				lastCaptured.getGraphics().drawString("Yellow: "+sendyellow,
						20,120 );
				lastCaptured.getGraphics().drawString("Red: "+sendred,
						20,180 );
				lastCaptured.getGraphics().drawString("Green: "+sendgreen,
						20,150 );
				lastCaptured.getGraphics().drawString("Blue: "+sendblue,
						20,210 );
				drawComponent.repaint();
			}
			
			
		}
    	}
	
	
	public double getRed() {
//		return (xg + xr) / 2.0;
		return sendred;
		
	}
	
	public double getGreen() {
//		return (yg + yr) / 2.0;
		
		return sendgreen;
		
		
	}
	
	public double getBlue() {
		return sendblue;
	}
	
	public double getYell() {
		return sendyellow;
	}
	
	private DrawComponent drawComponent;
	public void showDebug() {
		JFrame frame = new JFrame("Augmented image");
		frame.setSize(server.getVisualSize().width,server.getVisualSize().height);
		drawComponent = new DrawComponent();
		drawComponent.setSize(frame.getSize());
		frame.add(drawComponent);
		frame.setEnabled(true);
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
		frame.setLocation(0, 500);
	}

	public static void main(String[] args) throws Exception {
				
		//final String cameraType = "Philips USB";
		final String cameraType = "FrontCam";
		
		Logger l = Logger.getRootLogger();
		l.setLevel(Level.DEBUG);
		l.addAppender(new ConsoleAppender(new SimpleLayout()));

//	==========================================
		/* 
		 * get System porperties:
		 */
		String operatingSystem = null;
		Properties p = System.getProperties();
		Enumeration e = p.elements();
		while (e.hasMoreElements()) {
			operatingSystem = e.nextElement().toString();
//			System.out.println(operatingSystem);
			if(operatingSystem.equals("Linux") || operatingSystem.equals("Windows XP")) {
				l.debug("found "+operatingSystem+" Operating System");
				break;
			}
		}
//	===============end===========================
			
		Properties props = new Properties();
		props.setProperty("name", cameraType);
		
		props.setProperty("type", "camera");
		if(operatingSystem.equals("Linux")) 
			props.setProperty("devicename","v4l:BT878 video (Hauppauge (bt878)):1");
		if(operatingSystem.equals("Windows XP"))
			props.setProperty("devicename","vfw:Microsoft WDM Image Capture (Win32):0");
			props.setProperty("encoding", "rgb");
			props.setProperty("size", "320x240");
			props.setProperty("maxdatalength", "153600");
			props.setProperty("framerate", "-1");
		
		
//		VideoServer server = new VideoServer("usbcam", props, null);
		VideoServer server = new VideoServer(cameraType, props, null);
		server.showVideo();
		
		Thread.sleep(1000);

		
		
		KheperaFront tracker = new KheperaFront(server, l);
		tracker.showDebug();
		tracker.doFrontCamAnalysis();

	}

	private void savePictureMatlabreadable(double[][] decisionLog,String name,int size) {

		 
		 double[] decisionArray = new double[size];
		    
		 int look = 0;
			for (int k = 0; k < 3; k++) {
				for (int i = 0; i <size/3; i++) {
						decisionArray[look] = decisionLog[k][i];
						look++;
				}
			}
			saveArrayMatlabreadable(decisionArray, path+name);
			
		}


	private void saveArrayMatlabreadable(double[] matrix,
				String filename) {

			// read out the data as column from the matrix into the text file
		try {
				PrintWriter out = new PrintWriter(new FileWriter(filename));
				// go through the slices first

				// go through the rows first
				//for (int i = 0; i < matrix.rows(); i++) {

					// go through the row and fill in the values into the text file
					for (int j = 0; j < matrix.length; j++) {
						out.println(matrix[j]);
					}
			//	}

				out.close();
			} catch (IOException e) {
				logger.error("The writing process to external files is erroneous!");
			}

		}

	

}
