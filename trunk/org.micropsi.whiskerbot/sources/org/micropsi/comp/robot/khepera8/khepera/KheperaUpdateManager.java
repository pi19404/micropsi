package org.micropsi.comp.robot.khepera8.khepera;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.micropsi.comp.robot.khepera8.camera.KheperaFront;
import org.micropsi.comp.robot.khepera8.tracker.KheperaTracker;
import org.micropsi.media.MediaServerException;
import org.micropsi.media.VideoServer;
import org.micropsi.media.VideoServerRegistry;

public class KheperaUpdateManager extends Thread{
	
	//	set your camera type!
//	private String cameraType = "Philips USB";
	private String cameraType = "Heinicke Analog";
//  For the frontcam	
//	private String cameraTypeFront = "Heinicke Analog";
	
	private Khepera khepera;
	private Logger logger = null;
	private boolean debug = false;
	private long startP = 0, stopP = 0, startL = 0, stopL = 0;
	private byte i = 0;
	private boolean update = true;
    private ArrayList al = new ArrayList();
    private KheperaTracker tracker;
    private KheperaFront frontcam;
	
    
	public KheperaUpdateManager(Khepera khepera, Logger logger, boolean debug) {
		super();
		logger.info("[KheperaUpdateManager] constructor...");
		this.khepera = khepera;
		this.logger = logger;
		this.debug = debug;
	}

//    public KheperaUpdateManager(Khepera khepera, Logger logger, boolean debug, List l) {
//        super();
//        this.khepera = khepera;
//        this.logger = logger;
//        this.debug = debug;       
//    }
    
    public void connectToTrackerIfAvailable() {
    	
    	if(tracker == null) {
    		logger.debug("[KheperaUpdateManager.connectToTrackerIfAvailable()] getting server...");
	    	VideoServer server = VideoServerRegistry.getInstance().getServer("tracker");

	    	if(server == null) {
			  	logger.warn("[KheperaUpdateManager.connectToTrackerIfAvailable()] No video server 'tracker' found. No position data available.");
			} else {
				logger.info("[KheperaUpdateManager.connectToTrackerIfAvailable()] Video server 'tracker' found at "+server+", starting tracker.");
			    tracker = new KheperaTracker(server,logger,cameraType);
			    // grab a frame to ensure everything works
			    logger.debug("[KheperaUpdateManager.connectToTrackerIfAvailable()] test frame grab");
			    server.getVisualComponent().repaint();
			    server.grabFrame();
			    // start
			    logger.debug("[KheperaUpdateManager.connectToTrackerIfAvailable()] now starting tracker");
			    tracker.start();
			}
    	}
    }

    
 public void connectToFrontIfAvailable() {
    	
    	if(frontcam == null) {
    		logger.debug("[KheperaUpdateManager.connectToTrackerIfAvailable()] getting server...");
	    	VideoServer server = VideoServerRegistry.getInstance().getServer("frontCam");

	    	if(server == null) {
			  	logger.warn("[KheperaUpdateManager.connectToFrontIfAvailable()] No video server 'frontCam' found. No position data available.");
			} else {
				logger.info("[KheperaUpdateManager.connectToFrontIfAvailable()] Video server 'frontCam' found at "+server+", starting frontCam.");
			    frontcam = new KheperaFront(server,logger);
			    // grab a frame to ensure everything works
			    logger.debug("[KheperaUpdateManager.connectToFrontIfAvailable()] test frame grab");
			    server.getVisualComponent().repaint();
			    server.grabFrame();
			    // start
			    logger.debug("[KheperaUpdateManager.connectToFrontIfAvailable()] now starting grabing frames");
			    frontcam.start();
			}
    	}
    }
    
    
    public void getTrackerData() {
    	connectToTrackerIfAvailable();
    	if(tracker == null) return;
    	khepera.setPositionX(Double.toString(tracker.getX()));
    	khepera.setPositionY(Double.toString(tracker.getY()));
    	khepera.setOrientation(Double.toString(tracker.getPhi()));
    }    
    
    // For the frontcam
    public void getFrontData() {
    	connectToFrontIfAvailable();
    	if(frontcam == null) return;
    	khepera.setIntensityRed(Double.toString(frontcam.getRed()));
//    	logger.debug("RED "+frontcam.getRed());
    	khepera.setIntensityGreen(Double.toString(frontcam.getGreen()));
//    	logger.debug("GREEN "+frontcam.getGreen());
    	khepera.setIntensityBlue(Double.toString(frontcam.getBlue()));
//    	logger.debug("BLUE "+frontcam.getBlue());
    	khepera.setIntensityYell(Double.toString(frontcam.getYell()));
//    	logger.debug("YELLOW "+frontcam.getYell());
    }    
    
    
    public Collection getArrayList(){
        return al;
    }
    
	public void setUpdate(boolean update){
		this.update = update;
	}
	
	public void run(){
		logger.debug("[KheperaUpdateManager.run()] start..."); 
		
        while(update){
            //System.out.println("\t\ti:\t"+i);
//            logger.debug("[KheperaUpdateManager.run()] loop i="+Math.abs(i)%3);
            i++;
            
            switch(Math.abs(i)%4){
            	case 0: {khepera.updateAmbientLightSensors(); break;}
            	case 1: {khepera.updateProximitySensors(); break;}
            	case 2: {getTrackerData(); break;}
            	case 3: {getFrontData(); break;}
            	default: break;
            }
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
//			if(debug) logger.debug("(Update) Ps1:"+khepera.getPs1()+", "+(stopP-startP)+"ms\tLs1:"+khepera.getLs1()+", "+(stopL-startL)+"ms");
		}
        logger.debug("[KheperaUpdateManager.run()] ...stop"); 
        
//		if(debug) System.out.println("(Update) run fertig: Ps1_mean:"+(sumP/i)+"\tLs1_mean:"+(sumL/i));
//      System.out.println(System.currentTimeMillis()+"(Update) run fertig: Ps1_mean:"+(sumP/i)+"\tLs1_mean:"+(sumL/i));
	}
	
}
