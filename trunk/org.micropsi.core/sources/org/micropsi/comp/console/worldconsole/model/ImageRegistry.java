/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/console/worldconsole/model/ImageRegistry.java,v 1.4 2005/07/12 12:55:16 vuine Exp $ 
 */
package org.micropsi.comp.console.worldconsole.model;

import java.util.HashMap;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;


public class ImageRegistry {

	private HashMap<String,Image> images = new HashMap<String,Image>();
	
	/**
	 * 
	 * @param iconKey
	 * @return
	 */
	public Image get(String iconKey) {
		return images.get(iconKey);
	}

	/**
	 * 
	 * @param iconKey
	 * @param file
	 */
	public void add(String iconKey, String file) {
		images.put(iconKey, new Image(Display.getDefault(),file));
	}

}
