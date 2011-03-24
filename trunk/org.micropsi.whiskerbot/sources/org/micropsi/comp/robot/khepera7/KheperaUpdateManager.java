package org.micropsi.comp.robot.khepera7;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.micropsi.comp.robot.khepera7.tracker.KheperaTracker;
import org.micropsi.media.VideoServer;
import org.micropsi.media.VideoServerRegistry;

public class KheperaUpdateManager extends Thread{
	
	private Khepera khepera;
	private Logger logger = null;
	private boolean debug = false;
	private long startP = 0, stopP = 0, startL = 0, stopL = 0;
	private byte i = 0;
	private boolean update = true;
    private ArrayList al = new ArrayList();
    private KheperaTracker tracker;
    
	public KheperaUpdateManager(Khepera khepera, Logger logger, boolean debug) {
		super();
		logger.info("KheperaUpdateManager constructor...");
		this.khepera = khepera;
		this.logger = logger;
		this.debug = debug;
	}

    public KheperaUpdateManager(Khepera khepera, Logger logger, boolean debug, List l) {
        super();
        this.khepera = khepera;
        this.logger = logger;
        this.debug = debug;       
    }
    
    public void connectToTrackerIfAvailable() {
    	
    	if(tracker == null) {
	    	VideoServer server = VideoServerRegistry.getInstance().getServer("tracker");
	    	if(server == null) {
	//		  	logger.warn("No video server 'tracker' found. No position data available.");
			} else {
				logger.info("Video server 'tracker' found at "+server+", starting tracker.");
			    tracker = new KheperaTracker(server,logger);
			    // grab a frame to ensure everything works
			    logger.debug("test frame grab");
			    server.getVisualComponent().repaint();
			    server.grabFrame();
			    // start
			    logger.debug("now starting tracker");
			    tracker.start();
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
    
    public Collection getArrayList(){
        return al;
    }
    
	public void setUpdate(boolean update){
		this.update = update;
	}
	
	public void run(){
		
        while(update){
            i++;
            //System.out.println("\t\ti:\t"+i);
            //logger.debug("(UpdateManager) running: i="+Math.abs(i)%3);
            
            switch(Math.abs(i)%3){
            	case 0: {khepera.getAmbientLightSensors(); break;}
            	case 1: {khepera.getProximitySensors(); break;}
            	case 2: {getTrackerData(); break;}
            	default: break;
            }
            
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
			if(debug) logger.debug("(Update) Ps1:"+khepera.getPs1()+", "+(stopP-startP)+"ms\tLs1:"+khepera.getLs1()+", "+(stopL-startL)+"ms");
		}
        
//		if(debug) System.out.println("(Update) run fertig: Ps1_mean:"+(sumP/i)+"\tLs1_mean:"+(sumL/i));
//      System.out.println(System.currentTimeMillis()+"(Update) run fertig: Ps1_mean:"+(sumP/i)+"\tLs1_mean:"+(sumL/i));
	}
	
}
