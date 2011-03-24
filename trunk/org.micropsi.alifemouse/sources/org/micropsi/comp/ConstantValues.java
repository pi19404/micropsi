/*
 * Created on 09.04.2005
 *
 */
package org.micropsi.comp;

/**
 * @author Markus
 *
 */
public class ConstantValues {
    // agent
    public static final int CYCLE_LENGTH = 20;
	
    // BodySimulator
    public static final double HEALING_RATE 	 = 0.5;
    public static final double FOOD_DECAY 	     = 0.01;
    public static final int MAX_FOOD 			 = 100;
    public static final int MAX_WATER 			 = 100;
    public static final double INTERACTION_RANGE = 4.0;
    public static final double AFFILIATION_GAIN  = 40;
    
    public static final double PERCEPTION_ANGLE = 360.0;
    public static final double PERCEPTION_RANGE = 10.0;
    
    public static final int MAX_AGENT_COUNT = 5;
    
    // world
    public static final double WORLDMAXX = 100.0;
    public static final double WORLDMAXY = 100.0;
    
    // nodenets
    public static final double STUBBORNESS   	   = 0.2;
    public static final double NODEMINDISTANCE     = 1.0;
    public static final double MINOBJECTSIZE       = 3.0;
    public static final double MAXMERGEDISTANCE    = 3.0;
    public static final double REGIONMINDISTANCE   = 3.5;
    public static final double STEPLENGTH    	   = 0.7;
    public static final double LINKTHRESHOLD 	   = 0.4; // nodes with links with weights lower will be deleted
    public static final double EXPLORATIONSTRENGTH = 0.4;
    
    // regions
    public static final int MAX_CLUSTER_SIZE = 6;
    public static final boolean advancedMapping = true;
    
    public static String PATH;
    public static void setPATH(String path) {
        PATH = path;
    }
}
