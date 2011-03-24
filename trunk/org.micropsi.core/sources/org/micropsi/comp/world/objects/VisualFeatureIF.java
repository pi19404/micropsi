/*
 * Created on 28.06.2004
 *
 */

package org.micropsi.comp.world.objects;

import org.micropsi.common.coordinates.Position;

/**
 * @author Matthias
 *  
 */
public interface VisualFeatureIF {
	/**
	 * Returns the position.
	 * 
	 * @return Position
	 */
	public abstract Position getPosition();
	public abstract double getXSize();
	public abstract double getYSize();
	public abstract double getZSize();
	public abstract double getSize();
//    public abstract String getShape();
//    public abstract ObjectColor getColor();
}