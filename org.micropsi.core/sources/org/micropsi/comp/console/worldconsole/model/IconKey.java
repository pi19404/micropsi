/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 30.03.2004
 *
 */
package org.micropsi.comp.console.worldconsole.model;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Point;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.xml.XMLElementHelper;
import org.micropsi.common.xml.XMLElementNotFoundException;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.w3c.dom.Element;

/**
 * @author Matthias
 *
 */
public class IconKey implements IIconKeyRetriever {
	
	private String iconKey = null;
	private Point imageCenter = null;

	/**
	 * @param element
	 * @param images
	 */
	public IconKey(Element element, ImageRegistry images, ConsoleFacadeIF console, String objectImageDir) throws MicropsiException {
		try {
			iconKey = XMLElementHelper.getElementValueByTagName(element, "source");
		} catch (XMLElementNotFoundException e) {
			throw new MicropsiException(0, "WorldConsole config: 'image' must contain a 'source' tag.", e);
		}
		try {
			Element el = XMLElementHelper.getElementByTagName(element, "center");
			try {
				int x = Integer.parseInt(XMLElementHelper
						.getElementValueByTagName(el, "x"));
				int y = Integer.parseInt(XMLElementHelper
						.getElementValueByTagName(el, "y"));
				imageCenter = new Point(x, y);
			} catch (Exception e) {
				console.getLogger().error("Worldconsole config file: imagecenter must contain x and y tag, both must be integers", e);
			}
		} catch (XMLElementNotFoundException e2) {
			// imageCenter == null is ok (set to center later)
		}
		if (images.get(iconKey) == null) {
			try {
				images.add(iconKey, objectImageDir+"/"+iconKey);
			} catch (SWTException e) {
				console.getLogger().warn("Error loading image '" + objectImageDir + iconKey + ". Will use default image if available.", e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.console.worldconsole.model.IIconKeyRetriever#getIconKey(int)
	 */
	public String getIconKey(int orientation) {
		return iconKey;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.console.worldconsole.model.IIconKeyRetriever#getImageCenter()
	 */
	public Point getImageCenter() {
		return imageCenter;
	}

}
