/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 30.03.2004
 *
 */
package org.micropsi.comp.console.worldconsole.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

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
public class IconKeySet implements IIconKeyRetriever {
	
	private String iconKeyTemplate = null;
	private Point imageCenter = null;
	private Set<Integer> orientationSet = null;
	
	/**
	 * @param element
	 * @param images
	 */
	public IconKeySet(Element element, ImageRegistry images, ConsoleFacadeIF console, String objectImageDir) throws MicropsiException {
		String orientations = null;
		orientationSet = new HashSet<Integer>(4);
		try {
			iconKeyTemplate = XMLElementHelper.getElementValueByTagName(element, "sourcetemplate");
			orientations = XMLElementHelper.getElementValueByTagName(element, "orientations");
		} catch (XMLElementNotFoundException e) {
			throw new MicropsiException(0, "WorldConsole config: 'imageset' must contain a 'sourcetemplate' and an 'orientations' tag.", e);
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

		// load images for all orientations
		StringTokenizer tok = new StringTokenizer(orientations, ",");
		while (tok.hasMoreTokens()) {
			String orientation = tok.nextToken().trim();
			orientationSet.add(new Integer(orientation));

			String iconKey = getRealName(orientation);
			if (images.get(iconKey) == null) {
				try {
					images.add(iconKey, objectImageDir+"/"+iconKey);
				} catch (SWTException e) {
					console.getLogger().warn("Error loading image '" + objectImageDir + iconKey + ". Will use default image if available.", e);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.console.worldconsole.model.IIconKeyRetriever#getIconKey(int)
	 */
	public String getIconKey(int orientation) {
		int minDiff = 360;
		Integer choosenOrientation = new Integer(-1);
		Iterator it = orientationSet.iterator();
		while (it.hasNext()) {
			Integer orientationCandidate = (Integer) it.next();
			if (Math.min(Math.min(Math
					.abs(orientationCandidate.intValue() - orientation), Math
					.abs(orientationCandidate.intValue() - 360 - orientation)), Math
					.abs(orientationCandidate.intValue() + 360 - orientation)) < minDiff) {
				choosenOrientation = orientationCandidate;
				minDiff = Math.min(Math.min(Math
						.abs(orientationCandidate.intValue() - orientation), Math
						.abs(orientationCandidate.intValue() - 360 - orientation)), Math
						.abs(orientationCandidate.intValue() + 360 - orientation));
			}
		}
		
		return getRealName(choosenOrientation.toString()); 
	}
	
	private String getRealName(String orientation) {
		return iconKeyTemplate.replaceAll("\\$orientation", orientation);
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.console.worldconsole.model.IIconKeyRetriever#getImageCenter()
	 */
	public Point getImageCenter() {
		return imageCenter;
	}

}
