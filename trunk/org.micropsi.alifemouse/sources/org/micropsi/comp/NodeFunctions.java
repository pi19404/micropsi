/*
 * Created on 29.04.2005
 *
 */
package org.micropsi.comp;

import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.ConstantValues;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntity;

/**
 * @author Markus
 *
 */
public class NodeFunctions {

    public static Position getPosition(NetEntity node) {
        if(node == null)
            return null;
        Position toReturn = null;
        Link xValue = node.getLink(GateTypesIF.GT_SUB, 0);
        Link yValue = node.getLink(GateTypesIF.GT_SUB, 1);
        
        if(xValue != null && yValue != null) {
            toReturn = new Position(xValue.getWeight() * ConstantValues.WORLDMAXX, yValue.getWeight() * ConstantValues.WORLDMAXY);          
        }
        
        return toReturn;
    }
    
    public static Position getPosition(double x, double y) {
        return new Position(x * ConstantValues.WORLDMAXX, y * ConstantValues.WORLDMAXY);
    }
    
    public static int[] getRGB(NetEntity node) {
    	if(node == null)
    		return null;
    	int[] RGB = new int[3];
    	
    	RGB[0] = (int)(node.getLink(GateTypesIF.GT_SUB,0).getWeight() * 255);
    	RGB[1] = (int)(node.getLink(GateTypesIF.GT_SUB,1).getWeight() * 255);
    	RGB[2] = (int)(node.getLink(GateTypesIF.GT_SUB,2).getWeight() * 255);
    	
    	return RGB;
    }
    
    public static String positionToString(Position position) {
    	return "(" + position.getX() + ": " + position.getY() + ")";
    }
}
