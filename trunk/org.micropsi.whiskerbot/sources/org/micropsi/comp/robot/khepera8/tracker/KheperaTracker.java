package org.micropsi.comp.robot.khepera8.tracker;

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

public class KheperaTracker extends Thread {

	class DrawComponent extends JComponent {
		public void paint(Graphics g) {
			if (lastCaptured != null)
				g.drawImage(lastCaptured, 0, 0, null);
		}
	}
	private boolean debug=false;
	private String namehsv="HSVRawImageGreen1";
	private String namergb="RGBRawImageGreen1";
	///////////////////////////////////////////////////DEBUG
	private double[][]storehsv=new double[3][320*240];
	private double[][]storergb=new double[3][320*240];
	private String path="/home/student/d/dweiller/NavigationProject/Matrices/CalibartionTracker/";
	float[] test = new float[3];
	int look=0;
	///////////////////////////////Debug
	
	private volatile double xg, yg, xr, yr, xy, yy, getyr, getyy,sendx,sendy,xror,yror,yyor,xyor;
	private volatile double phi = 0;
	private int height, width, modgreen, modred, modyell, modwhite;
	private double orientx, orienty, skp;

	private BufferedImage lastCaptured = null;

	private VideoServer server;
	private Logger logger;
	private String cameraType = null;
	public static boolean calibration=true;
	public static long[][] lookupx=new long[240][320];
	public static long[][] lookupy=new long[240][320];
	public boolean visualize=false;
	private long meanxreal=-1;
	private long meanyreal=-1;
	private long meanx=-1;
	private long meany=-1;
	private static boolean startlower=true;
	private static boolean lookfirst=true;
	
	/////mean over time
	private double [][] meanvalue=new double[3][4];
	private int count=0;

	public KheperaTracker(VideoServer server, Logger logger, String cameraType) {
		this.cameraType = cameraType;
		logger.debug("Constructing khepera tracker with camera type "+cameraType);
		this.server = server;
		this.logger = logger;
	}
	
	public void run() {
		logger.debug("Start tracking on video server "+server.getName());
		try {
			if (cameraType.equals("Philips USB"))
				doTrackingPhilipsUSBCam();
			if (cameraType.equals("Heinicke Analog"))
				doTrackingHeinickeAnalogCam();
		} catch (InterruptedException e) {
			logger.error("Tracker sleep interrupted",e);
		}
		logger.debug("Tracking stopped");
	}

/* 
 * helper methods for doTrackingHeinickeAnalogCam()
 */
	private void Visualization(){
		visualize  = true;
	}
	
//	Calibration of the Camera building an look up table
	private void doCalibrate(int mesure){
		
		
		double Metrikx= 1701.92456;
		double Metriky= 1727.01453;

		double x0=738.68676758;
		double y0=638.66467285;

		double k1=-0.43472454;
		double k2=0.19871280;
		double p1=0.00284341909;
		double p2=0.00511034066;
		double vi,ui,ri;
		double xcorrect,ycorrect;
		
	
		
		for(int y=1;y<=1200;y +=mesure){
			for(int x=1;x<=1600;x +=mesure){
	        	ui=(x-x0)/Metrikx;
		        vi=(y-y0)/Metriky;
		        ri=Math.sqrt(Math.pow(ui,2)+Math.pow(vi,2));
		        xcorrect=((Metrikx*(ui+ui*(k1*Math.pow(ri,2)+k2*Math.pow(ri,4)))+2.0*p1*ui*vi+p2*(Math.pow(ri,2)+2*Math.pow(ui,2))))+x0;
		        ycorrect=((Metriky*(vi+vi*(k1*Math.pow(ri,2)+k2*Math.pow(ri,4)))+p1*(Math.pow(ri,2)+2.0*Math.pow(vi,2))+2*p2*ui*vi))+y0;
		        
		       
		        lookupx[(y-1)/mesure][(x-1)/5]=Math.round(xcorrect*1/mesure);   
				lookupy[(y-1)/mesure][(x-1)/5]=Math.round(ycorrect*1/mesure);
		        
		        
		        }
	          }
		calibration=false;
	}
	
	
	
	private double remember(double value,int count,int number){
		
		
		int look=count%3;
		double back=0;
		switch(look){
		case 0: meanvalue[0][number]=value;
				break;
		case 1:meanvalue[1][number]=value;
				break;
		case 2:meanvalue[2][number]=value;
				break;
		default: back= 0;
				break;
		
		}
		
		if( count>3){
			for(int i=0;i<=2;i++)
				back += meanvalue[i][number];
			}
		
		return back/3;
	}

	public void doTrackingHeinickeAnalogCam() throws InterruptedException {
		int sumred, sumyell,newx,newy,starti,startj,stopi,stopj;
		double xred, xyell, yred, yyell;
		
		logger.debug("[KheperaTracker.doTrackingHeinickeAnalogCam()] is running...");
		// enter main loop
		while (true) {
			if(debug){
				if(look<120)
					look++;
			}
			
			
			
			// calc every 33 ms
			Thread.sleep(33);
					
			// grab image
			lastCaptured = server.grabImageAWTRGB();
			height = server.getVisualSize().height;
			width = server.getVisualSize().width;
			
			
			if(calibration){
				int number=1200/height;
				doCalibrate(number);
			}
			
			int[] pix = new int[3];
			WritableRaster raster = lastCaptured.getRaster();
			WritableRaster newraster=null;
			
			//to visualize the data;
			if(visualize)
			  newraster=raster.createCompatibleWritableRaster();
			
			
			// store Picture;
			
			
//			RenderedImage save=server.grabImageAWTRGB();
//			
//			//logger.debug("meanx :"+meanx+" meany :"+meany);
//			if(count==100){
//				File store =new File("C:/Picturetest/danieltest1.png");	
//				
//			try {
//				ImageIO.write(save,"PNG",store);
//				logger.debug("picture is saved;");
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				logger.debug("Scheisse");
//			}
//			}
			
			
			sumyell = sumred = 0;
			xred = yred = xyell = yyell = 0;
	        
			if(startlower){
				starti=startj=0;
				stopi=height;
				stopj=width;
			}else{
				starti=(int)(meany-30);
				stopi=(int)(meany+30);
				startj=(int)(meanx-30);
				stopj=(int)(meanx+30);
			
				if(starti<0)
					starti=0;
				if(stopi<0)
					stopi=0;
				if(stopi>height)
					stopi=height;
				if(startj<0)
					startj=0;
				if(stopj<0 )
					stopj=0;
				if(stopj>width)
					stopj=width;
			
			}
				
			
		   for (int i = starti; i < stopi; i++) {
				for (int j = startj; j < stopj; j++) {
					meanx=0;
					meany=0; 
							
					newx=(int)lookupx[i][j];
					newy=(int)lookupy[i][j];
					
					raster.getPixel(newx, newy, pix);
					int red = pix[0];
					int green = pix[1];
					int blue = pix[2];
					if(debug && look==100){
						Color.RGBtoHSB(red,green,blue,test);
						storehsv[0][newx*height+newy]=test[0];
						storehsv[1][newx*height+newy]=test[1];
						storehsv[2][newx*height+newy]=test[2];
						storergb[0][newx*height+newy]=red;
						storergb[1][newx*height+newy]=green;
						storergb[2][newx*height+newy]=blue;
					}
					Color.RGBtoHSB(red,green,blue,test);
					
					if(test[0]>0 && test[0]<0.06 && test[1]>0)
						modred=red;
					else 
						modred=0;
					
					if(test[0]>0.17 && test[0]<0.3 && test[1]>0 && test[2]>0)
						modyell=green;
					else
						modyell=0;
					
//					modwhite=green;
					
//					if(modwhite<60)
//						modwhite=0;
////					else modwhite=100;
//					
//					modyell-=modwhite;
					
					if(modred<140)
						modred=0;
					
					if(modyell<50)
						modyell=0;
//					else
//						modyell=100;
					
					
//					if(green>180)
//						modgreen=0;
//					else 
//						modgreen=green;
//					
//					modred = red - green;
//					if (modred < 90) //75
//						modred = 0;
//					else modred=100;
						
//					modwhite=red;
//					if (modwhite<140 && modwhite>70) //85
//						modwhite=0;
//					//else modwhite=255;
						
//					modyell = blue-red;//-modwhite;
//					if (modyell < 40) //40//130
//						modyell = 0;
					//else modyell=100;
											
					sumyell += modyell;
						
					sumred += modred;
						
					
						
						
					xred += modred* (j);
					yred += modred *(height-i);
					getyr +=modred*i;
	
					xyell += modyell * (j);
					yyell += modyell *(height-i);
					getyy +=modyell*i;
					
					
					// to visualize the data
					if(visualize){
						if(drawComponent != null) {
							newraster.setPixel(j, i, new int[] {modred,modyell,0 });
							}
						}
				}
			}
			
		   
			if(sumred<1000){
				startlower=true;
			}else{
				startlower=false;
			}
		   
		   
		   if(look==100 && debug){
				savePictureMatlabreadable(storehsv,namehsv,3*height*width);
				savePictureMatlabreadable(storergb,namergb,3*height*width);
				logger.debug("Stored Pictures");
			}
		   
			
			if(visualize)
			lastCaptured.setData(newraster);
						
			if(sumred>0){
				xr = xred / sumred;
				yr = yred / sumred;
				getyr=getyr/sumred;
			}else
				xr=yr=getyr=0;
			
				
			
			if(sumyell>0){
				xy = xyell / sumyell;
				yy = yyell / sumyell;
				getyy=getyy/sumyell;
			}else
				xy=yy=getyy=0;
			
			meanxreal=Math.round(xr+(xy-xr)/2);
			meanyreal=Math.round(getyr+(getyy-getyr)/2);
			meanx=(int)xr;
			meany=(int)(getyr+(getyy-getyr)/2);		
			
			//round over time
			xror=remember(xr,count,0);
			yror=remember(yr,count,1);	
			
			xr=xror/width;
			yr=yror/height;
			
			xyor=remember(xy,count,2);
			yyor=remember(yy,count,3);
			xy=xyor/width;
			yy=yyor/height;
			
			
			
//			Roud to each pixel
			yyor=Math.rint(yyor*10.0)*0.1;
			xyor=Math.rint(xyor*10.0)*0.1;
			xror=Math.rint(xror*10.0)*0.1;
			yror=Math.rint(yror*10.0)*0.1;
			
			
			count +=1;
			
			
//			round position
			xr=Math.rint(xr*1000)*0.1;
			yr=Math.rint(yr*1000)*0.1;
			xy=Math.rint(xy*1000)*0.1;
			yy=Math.rint(yy*1000)*0.1;
						
			orienty = yyor - yror;
			orientx = xyor - xror;
			sendx=Math.ceil((xr+(xy-xr)/2)*10)/10;
			sendy=Math.ceil((yr+(yy-yr)/2)*10)/10;
			
			if(orientx==0 && orienty==0)
				skp=0;
			else
			skp = orientx / (Math.sqrt(orientx * orientx + orienty * orienty));
			phi = Math.rint(((Math.acos(skp) / Math.PI) * 180.0));
			if (orienty > 0)
				phi = 360.0 - phi;
								
			// tracking done, do augmentation
			if(drawComponent != null) {		
				lastCaptured.getGraphics().drawString("orient"+phi,
						20,50 );
				lastCaptured.getGraphics().drawString("posx: "+sendx+" posy: "+sendy,
						20,90 );
//				lastCaptured.getGraphics().drawString(" xr: "+xror+" yr: "+yror,
//						20,70 );
//				lastCaptured.getGraphics().drawString(" meanx: "+sendx+" meany: "+sendy,
//						20,220 );
				lastCaptured.getGraphics().drawString(".",(int)meanxreal,(int)meanyreal);
				lastCaptured.getGraphics().drawPolygon(new int[]{((int)meanx-30),((int)meanx+30),((int)meanx+30),((int)meanx-30)},new int[]{((int)meany-30),((int)meany-30),((int)meany+30),((int)meany+30)},4);
				drawComponent.repaint();
			}
		}
	}
	
	
	
	public void doTrackingPhilipsUSBCam() throws InterruptedException {
		int sumred, sumgreen,starti,startj,stopi,stopj;
		double xred, xgreen, yred, ygreen, quadr;
		
		//sets length (pixel) of tracking region of interest (square box):
		int BOX = 45;
		
		logger.debug("[KheperaTracker.doTrackingPhilipsUSBCam()] is running...");
		
//		 ==========================================
		/* 
		 * get System porperties:
		 */
		boolean linux = false;
		Properties p = System.getProperties();
		Enumeration e = p.elements();
		while (e.hasMoreElements()) {
			String operatingSystem = e.nextElement().toString();
			if(operatingSystem.equals("Linux")){
//				logger.debug("found "+operatingSystem+" Operating System");
				linux = true;
				break;
			}
		}
//	===============end===========================
			
		int[] phi_buffer = {0,0,0};
		int cycle = 0;	
		// enter main loop
		while (true) {
			// calc every 100 ms
			Thread.sleep(33);
			// grab image
			lastCaptured = server.grabImageAWTRGB();
			
			height = server.getVisualSize().height;
			width = server.getVisualSize().width;
			
			int[] pix = new int[3];
			WritableRaster raster = lastCaptured.getRaster();

			
	
//			sumgreen = sumred = 1; //init != 0 to prevent division by zero!
			sumgreen = sumred = 0;
			xred = yred = xgreen = ygreen = 0;
			
			//take a window of the current frame
			
			if(startlower){
				starti=startj=0;
				stopi=height;
				stopj=width;
			}else{
				starti=(int)(meany-BOX);
				stopi=(int)(meany+BOX);
				startj=(int)(meanx-BOX);
				stopj=(int)(meanx+BOX);
				
				if(starti<0)
					starti=0;
				if(stopi<0)
					stopi=0;
				if(stopi>height)
					stopi=height;
				if(startj<0)
					startj=0;
				if(stopj<0 )
					stopj=0;
				if(stopj>width)
					stopj=width;
			}
				
			
			
			
			for (int i = starti; i < stopi; i++) {
				for (int j = startj; j < stopj; j++) {
					raster.getPixel(j, i, pix);
					int red = pix[0];
					int green = pix[1];
					int blue = pix[2];
					
					modred = (red-green);
					if(linux){
						if ( (modred < 60) ) //leo: linux: <60 (mit setpwc -s 55000 -w auto -g 0)
							modred = 0;
						else modred=100;
					}
					
					else{
						if ( (modred < 95) ) //leo: windows: < 95
							modred = 0;
						else modred=100;
					}
					
					if(linux){
						modgreen=green-red;     
						if ( green < 50 || green > 100 || modgreen < 32) //leo: linux: ( green < 50 || green > 100 || modgreen < 32) (mit setpwc -s 55000 -w auto -g 0) 
						if (green < 70 || modgreen < 39)
							modgreen = 0;
						else modgreen=100;
					}
					else{
						modgreen=green-red;     
						if ( (modgreen < 41) )//|| (blue > 50) )//(modgreen < 41) || (blue > 50)
							modgreen = 0;
						else modgreen=100;
					}

					
					sumgreen += modgreen;
					sumred += modred;
					
					xred += modred * (j);
					yred += modred * (height-i);
					getyr +=modred * (i);

					xgreen += modgreen * (j);
					ygreen += modgreen * (height-i);
					getyy  += modgreen * (i);
					

					// to visualize the data
					if(drawComponent != null) {						
						boolean r = false;
						boolean y = false;
						if(modred!=0){
							raster.setPixel(j, i, new int[] { 0, 0, 255 });
							r = true;
						}
						
						if(modgreen!=0){
							raster.setPixel(j, i, new int[] { 0, 255, 0 });
							y = true;
						}
						
						if(modred==0 && modgreen==0)
							raster.setPixel(j, i, new int[] { 0, 0, 0 });
						
						if (r&&y)
							System.out.println("red&&green!!!!");
						
						if((i==height-5) && (j==width-5)){
							lastCaptured.getGraphics().drawString("white in (1,1) corner: "+red+"\t\t "+green+"\t\t "+blue+"" ,
									400, 20);
						}
					}
			}
		}
			
			
			
			if(sumred<1000){
				startlower=true;
			}else{
				startlower=false;
			}
			
			
			if(sumred>0){
				xr = xred / sumred;
				yr = yred / sumred;
				getyr=getyr/sumred;
			}
			else
				xr=yr=getyr=0;
					
			
			if(sumgreen>0){
				xy = xgreen / sumgreen;
				yy = ygreen / sumgreen;
				getyy=getyy/sumgreen;
			}
			else
				xy=yy=getyy=0;

			meanx=Math.round(xy+(xr-xy)/2);
			meany=Math.round(getyy+(getyr-getyy)/2);
				
	
			
				
			    
			
			 
			xr = xred / sumred / width;
			yr = yred / sumred / height;
					
			xg = xgreen / sumgreen / width;
			yg = ygreen / sumgreen / height;
			
			

			orienty = yr - yg;
			orientx = xr - xg;
			skp = orientx / (Math.sqrt(orientx * orientx + orienty * orienty));
			quadr = Math.round(orienty * 100);
			double phi_help = Math.rint((Math.acos(skp) / Math.PI) * 180);
			if (quadr > 0)
				phi_help = 360 - phi_help;
			
			cycle++;
			phi_buffer[Math.abs(cycle) % phi_buffer.length] = (int)phi_help;
			phi = (phi_buffer[0]+phi_buffer[1]+phi_buffer[2]) / 3;
			
			xr=Math.rint(xr*1000)*0.1;
			yr=Math.rint(yr*1000)*0.1;
			xy=Math.rint(xy*1000)*0.1;
			yy=Math.rint(yy*1000)*0.1;
			
			sendx=xr+(xy-xr)/2;
			sendy=(yr+(yy-yr)/2);
			
//			logger.debug("tracking done: "+phi+" / "+xg+" / "+yg);
			
			// tracking done, do augmentation
			if(drawComponent != null) {			
				lastCaptured.getGraphics().drawString("red at: " + Math.round(xr*100.)/100. + "   \t\t| " + Math.round(yr*100.)/100.+"  sumred "+sumred,
						20, 20);
				lastCaptured.getGraphics().drawString("green at: " + Math.round(xg*100.)/100. + "   \t\t| " + Math.round(yg*100.)/100.,
						20, 100);
				lastCaptured.getGraphics().drawString(
						"orientation: " + phi + "", 20, 180);//Math.round(phi*100.)/100. + "", 20, 180);
				lastCaptured.getGraphics().drawPolygon(new int[]{((int)meanx-BOX),((int)meanx+BOX),((int)meanx+BOX),((int)meanx-BOX)},new int[]{((int)meany-BOX),((int)meany-BOX),((int)meany+BOX),((int)meany+BOX)},4);
				
				lastCaptured.getGraphics().drawString(".",(int)meanx,(int)meany);
				
				
//				if(drawComponent != null) {			
//				lastCaptured.getGraphics().drawString("red at: " + xr + "/" + yr,
//				20, 20);
//				lastCaptured.getGraphics().drawString("green at: " + xg + "/" + yg,
//				20, 100);
//				lastCaptured.getGraphics().drawString(
//				"orientation: " + phi + "orientx " + orientx, 20, 180);
//				//lastCaptured.getGraphics().drawString("gelb",(int)(xg*640),(int)(yg*480));
				
				drawComponent.repaint();
			}
		}
	}
	
	public void doTrackingPhilipsUSBCam_old() throws InterruptedException {
		int sumred, sumgreen;
		double xred, xgreen, yred, ygreen, quadr;

		logger.debug("[KheperaTracker.doTrackingPhilipsUSBCam()] is running...");
		
//		 ==========================================
		/* 
		 * get System porperties:
		 */
		boolean linux = false;
		Properties p = System.getProperties();
		Enumeration e = p.elements();
		while (e.hasMoreElements()) {
			String operatingSystem = e.nextElement().toString();
			if(operatingSystem.equals("Linux")){
//				logger.debug("found "+operatingSystem+" Operating System");
				linux = true;
				break;
			}
		}
//	===============end===========================
			
		int[] phi_buffer = {0,0,0};
		int cycle = 0;	
		// enter main loop
		while (true) {
			// calc every 100 ms
			Thread.sleep(100);
			// grab image
			lastCaptured = server.grabImageAWTRGB();
			
			height = server.getVisualSize().height;
			width = server.getVisualSize().width;
			
			int[] pix = new int[3];
			WritableRaster raster = lastCaptured.getRaster();
			sumgreen = sumred = 1; //init != 0 to prevent division by zero!
			xred = yred = xgreen = ygreen = 0;
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					raster.getPixel(j, i, pix);
					int red = pix[0];
					int green = pix[1];
					int blue = pix[2];
					
					modred = (red-green);
					if(linux){
						if ( (modred < 60) ) //leo: linux: <60 (mit setpwc -s 55000 -w auto -g 0)
							modred = 0;
						else modred=100;
					}
					
					else{
						if ( (modred < 95) ) //leo: windows: < 95
							modred = 0;
						else modred=100;
					}
					
					if(linux){
						modgreen=green-red;     
						if ( green < 180 || modgreen < 38) //leo: linux: ( green < 180 || modgreen < 38) (mit setpwc -s 55000 -w auto -g 0) 
							modgreen = 0;
						else modgreen=100;
					}
					else{
						modgreen=green-red;     
						if ( (modgreen < 41) )//|| (blue > 50) )//(modgreen < 41) || (blue > 50)
							modgreen = 0;
						else modgreen=100;
					}

					
					sumgreen += modgreen;
					sumred += modred;

					xred += modred * (j);
					yred += modred * (i);

					xgreen += modgreen * (j);
					ygreen += modgreen * (i);

					if(drawComponent != null) {
						boolean r = false;
						boolean y = false;
						if(modred!=0){
//							System.out.println("1");
							raster.setPixel(j, i, new int[] { 0, 0, 255 });
//							System.out.println("modred="+modred);
							r = true;
						}
						if(modgreen!=0){
//							System.out.println("2");
							raster.setPixel(j, i, new int[] { 0, 255, 0 });
//							System.out.println("modgreen="+modgreen);
							y = true;
						}
						
						if(modred==0 && modgreen==0){
//						else{
//							System.out.println("3");
							raster.setPixel(j, i, new int[] { 0, 0, 0 });
						}
						if (r&&y)
							System.out.println("red&&green!!!!");
//						if((i==height-5) && (j==width-5)){
//							lastCaptured.getGraphics().drawString("white in (1,1) corner: "+red+"\t\t "+green+"\t\t "+blue+"" ,
//									400, 20);
//						}
						
//						raster.setPixel(j, i, new int[] { modred, 0, 0 });
					}
//					raster.setPixel(j, i, new int[] { modred, 0, 0 });
				}
			}
			
			xr = xred / sumred / width;
			yr = yred / sumred / height;
					
			xg = xgreen / sumgreen / width;
			yg = ygreen / sumgreen / height;
			

			orienty = yr - yg;
			orientx = xr - xg;
			skp = orientx / (Math.sqrt(orientx * orientx + orienty * orienty));
			quadr = Math.round(orienty * 100);
			double phi_help = Math.rint((Math.acos(skp) / Math.PI) * 180);
			if (quadr > 0)
				phi_help = 360 - phi_help;
			
			cycle++;
			phi_buffer[Math.abs(cycle) % phi_buffer.length] = (int)phi_help;
			phi = (phi_buffer[0]+phi_buffer[1]+phi_buffer[2]) / 3;
			
//			logger.debug("tracking done: "+phi+" / "+xg+" / "+yg);
			
			// tracking done, do augmentation
			if(drawComponent != null) {			
				lastCaptured.getGraphics().drawString("red at: " + Math.round(xr*100.)/100. + "   \t\t| " + Math.round(yr*100.)/100.,
						20, 20);
				lastCaptured.getGraphics().drawString("green at: " + Math.round(xg*100.)/100. + "   \t\t| " + Math.round(yg*100.)/100.,
						20, 100);
				lastCaptured.getGraphics().drawString(
						"orientation: " + phi + "", 20, 180);//Math.round(phi*100.)/100. + "", 20, 180);
				
//				if(drawComponent != null) {			
//				lastCaptured.getGraphics().drawString("red at: " + xr + "/" + yr,
//				20, 20);
//				lastCaptured.getGraphics().drawString("green at: " + xg + "/" + yg,
//				20, 100);
//				lastCaptured.getGraphics().drawString(
//				"orientation: " + phi + "orientx " + orientx, 20, 180);
//				//lastCaptured.getGraphics().drawString("gelb",(int)(xg*640),(int)(yg*480));
				
				drawComponent.repaint();
			}
		}
	}
	
	public double getX() {
//		return (xg + xr) / 2.0;
		return sendx;
	}
	
	public double getY() {
//		return (yg + yr) / 2.0;
		return sendy;
		
	}
	
	public double getPhi() {
		return phi;
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
		final String cameraType = "Heinicke Analog";
		
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
		if (cameraType.equals("Philips USB")) {
			props.setProperty("type", "camera");
			if(operatingSystem.equals("Linux")) 
				props.setProperty("devicename","v4l:Philips 740 webcam:0");
			if(operatingSystem.equals("Windows XP"))
				props.setProperty("devicename","vfw:Microsoft WDM Image Capture (Win32):0");
			props.setProperty("encoding", "yuv");
			props.setProperty("size", "320x240");
			props.setProperty("maxdatalength", "115200");
//			props.setProperty("size", "640x480");
//			props.setProperty("maxdatalength", "460800");
			props.setProperty("framerate", "-1");
		}
		if (cameraType.equals("Heinicke Analog")) {
			props.setProperty("type", "camera");
			if(operatingSystem.equals("Linux")) 
				props.setProperty("devicename","v4l:BT878 video (Hauppauge (bt878)):0");
			if(operatingSystem.equals("Windows XP"))
				props.setProperty("devicename","vfw:Microsoft WDM Image Capture (Win32):0");
			props.setProperty("encoding", "rgb");
			props.setProperty("size", "320x240");
			props.setProperty("maxdatalength", "153600");
			props.setProperty("framerate", "-1");
		}
		
		
//		VideoServer server = new VideoServer("usbcam", props, null);
		VideoServer server = new VideoServer(cameraType, props, null);
		server.showVideo();
		
		Thread.sleep(1000);

		
		
		KheperaTracker tracker = new KheperaTracker(server, l, cameraType);
		tracker.showDebug();
		if (cameraType.equals("Philips USB"))
			tracker.doTrackingPhilipsUSBCam();
		if (cameraType.equals("Heinicke Analog")){
			tracker.Visualization();
			tracker.doTrackingHeinickeAnalogCam();
		}

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
