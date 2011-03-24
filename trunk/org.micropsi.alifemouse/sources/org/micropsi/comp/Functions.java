/*
 * Created on 21.12.2004
 *
 */
package org.micropsi.comp;

import org.micropsi.common.coordinates.WorldVector;

/**
 * @author Markus
 *
 */
public class Functions {
    
    /**
     * @param vector
     * @return the angle between vector and (1,0,0)
     */
    public static double getAngle(WorldVector vector) {
        double length = vector.getLength();
        double y = Math.abs(vector.getY());
        double angle = Math.toDegrees(Math.asin(y / length));
        
        if (vector.getX() < 0)
            angle = 180.0 - angle;
        if (vector.getY() < 0)
            angle *= -1.0;
            
        return angle;
    }
    
    public static int min(int v1, int v2) {
        return (v1 < v2) ? v1 : v2;
    }
    
    public static int[] getRGB(long ID) {
        int[] RGB = new int[3];
        long temp = ID;
        
        if(temp >= Math.pow(256, 3))
            temp = temp % (long)Math.pow(256, 3);
        for(int i = 2; i >=0; i--) {
            if(temp >= Math.pow(256, i)) {
                RGB[i] = (int)(temp / Math.pow(256, i));
                temp -= RGB[i] * Math.pow(256 , i);
            } else
                RGB[i] = 0;
        }
        
        return RGB;
    }
    
    public static long getID(int[] RGB) {
        long ID = 0;
        
        for(int i = 0; i < 3; i++) {
            ID += RGB[i] * Math.pow(256, i);
        }
        
        return ID;
    }
}
