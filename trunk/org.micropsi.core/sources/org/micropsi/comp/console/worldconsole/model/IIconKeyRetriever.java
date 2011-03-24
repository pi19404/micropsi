/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 30.03.2004
 *
 */
package org.micropsi.comp.console.worldconsole.model;

import org.eclipse.swt.graphics.Point;

/**
 * @author Matthias
 *
 */
public interface IIconKeyRetriever {
	
	String getIconKey(int orientation);
	Point getImageCenter();

}
