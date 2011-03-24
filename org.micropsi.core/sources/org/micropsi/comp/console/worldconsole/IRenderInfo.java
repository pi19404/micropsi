/*
 * Created on 14.06.2005
 *
 */

package org.micropsi.comp.console.worldconsole;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.console.worldconsole.model.WorldObject;



public interface IRenderInfo {

	public abstract int getScreenX(Position pos);

	public abstract int getScreenX(double x);

	public abstract int getScreenY(Position pos);

	public abstract int getScreenY(double y);

	public abstract Position getWorldPosition(int screenX, int screenY);

	/**Returns a rectangle describing the image extends of the given object in screen coordinates.
	 * May return null.
	 * @param obj - the object
	 * @return - rectangle of object icon, null if object doesnot exist.
	 */
	public abstract Rectangle getObjectBounds(WorldObject obj);

	public abstract Point getSizeRenderedWorld();

	/**
	 * @return Returns the worldHighestCoords.
	 */
	public abstract Position getWorldHighestCoords();

	/**
	 * @return Returns the worldLowestCoords.
	 */
	public abstract Position getWorldLowestCoords();

	/**
	 * @return Returns the scaleX.
	 */
	public abstract double getScaleX();

	/**
	 * @return Returns the scaleY.
	 */
	public abstract double getScaleY();

}