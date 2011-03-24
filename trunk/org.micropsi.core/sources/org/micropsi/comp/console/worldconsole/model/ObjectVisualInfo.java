/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 30.03.2004
 *
 */
package org.micropsi.comp.console.worldconsole.model;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;


public class ObjectVisualInfo {
	public Image image = null;
	public String imageKey = null;
	public Point imageCenter = null;
	public String stateDescription = "";
	
	public ObjectVisualInfo() {
	}
	
	public ObjectVisualInfo(ObjectVisualInfo ic) {
		image = ic.image;
		imageCenter = ic.imageCenter;
		stateDescription = "";
	}
	
	public Rectangle getImageBounds() {
		if (image != null) {
			return image.getBounds();
		} else {
			return new Rectangle(0, 0, 10, 10);
		}
	}
	
}