package org.micropsi.comp.robot.khepera7.tracker;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
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
	
	private double phi, xy, yy;
	private int height, width, modyell, modred;
	private double orientx, orienty, skp;

	private BufferedImage lastCaptured = null;

	private VideoServer server;
	private Logger logger;

	public KheperaTracker(VideoServer server, Logger logger) {
		logger.debug("Constructing khepera tracker");
		this.server = server;
		this.logger = logger;
	}
	
	public void run() {
		logger.debug("Start tracking on video server "+server.getName());
		try {
			doTracking();
		} catch (InterruptedException e) {
			logger.error("Tracker sleep interrupted",e);
		}
		logger.debug("Tracking stopped");
	}

	public void doTracking() throws InterruptedException {
		int sumred, sumyell;
		double xred, xyell, yred, yyell, xr, yr, quadr;

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
			sumyell = sumred = 0;
			xred = yred = xyell = yyell = 0;
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < width; j++) {
					raster.getPixel(j, i, pix);
					int red = pix[0];
					int blue = pix[1];
					int green = pix[2];

					modyell = blue - green;
					modred = red - blue;
					if (modyell < 80)
						modyell = 0;
					if (modred < 40)
						modred = 0;
					sumyell += modyell;
					sumred += modred;

					xred += modred * (j);
					yred += modred * (i);

					xyell += modyell * (j);
					yyell += modyell * (i);

					if(drawComponent != null) {
						raster.setPixel(j, i, new int[] { modred, 0, 0 });
					}

				}
			}

			xr = xred / sumred / width;
			yr = yred / sumred / height;

			xy = xyell / sumyell / width;
			yy = yyell / sumyell / height;

			orienty = yr - yy;
			orientx = xr - xy;
			skp = orientx / (Math.sqrt(orientx * orientx + orienty * orienty));
			quadr = Math.round(orienty * 100);
			phi = (Math.acos(skp) / Math.PI) * 180;
			if (quadr > 0)
				phi = 360 - phi;
			
//			logger.debug("tracking done: "+phi+" / "+xy+" / "+yy);
			
			// tracking done, do augmentation
			if(drawComponent != null) {			
				lastCaptured.getGraphics().drawString("red at: " + xr + "/" + yr,
						20, 20);
				lastCaptured.getGraphics().drawString("yell at: " + xy + "/" + yy,
						20, 100);
				lastCaptured.getGraphics().drawString(
						"orientation: " + phi + "orientx " + orientx, 20, 180);
				//lastCaptured.getGraphics().drawString("gelb",(int)(xy*640),(int)(yy*480));
				
				drawComponent.repaint();
			}
		}
	}
	
	public double getX() {
		return xy;
	}
	
	public double getY() {
		return yy;
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

		Properties props = new Properties();
		props.setProperty("name", "usbcam");
		props.setProperty("type", "camera");
		props.setProperty("devicename","vfw:Microsoft WDM Image Capture (Win32):0");
		props.setProperty("encoding", "yuv");
		props.setProperty("size", "640x480");
		props.setProperty("framerate", "-1");
		props.setProperty("maxdatalength", "460800");

		VideoServer server = new VideoServer("usbcam", props, null);
		server.showVideo();
		
		Thread.sleep(1000);

		Logger l = Logger.getRootLogger();
		l.setLevel(Level.DEBUG);
		l.addAppender(new ConsoleAppender(new SimpleLayout()));
		
		KheperaTracker tracker = new KheperaTracker(server, l);
		tracker.showDebug();
		tracker.doTracking();

	}

}
