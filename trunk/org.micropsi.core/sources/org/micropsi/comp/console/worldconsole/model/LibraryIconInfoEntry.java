/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 30.03.2004
 *
 */
package org.micropsi.comp.console.worldconsole.model;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.xml.XMLElementHelper;
import org.micropsi.common.xml.XMLElementNotFoundException;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.w3c.dom.Element;

/**
 * @author Matthias
 *
 */
public class LibraryIconInfoEntry {
	
	private ObjectStates objectStates = null;
	private int score = 1;
	private IIconKeyRetriever icon = null;

	/**
	 * 
	 */
	public LibraryIconInfoEntry(Element config, ImageRegistry images, ConsoleFacadeIF console, String objectImageDir) throws MicropsiException {
		try {
			Element el = XMLElementHelper.getElementByTagName(config, "objectstates");
			objectStates = new ObjectStates(el);
		} catch (XMLElementNotFoundException e) {
		// objectStates == null is ok.
		}
		try {
			score = Integer.parseInt(XMLElementHelper.getElementValueByTagName(config, "score"));
		} catch (NumberFormatException e1) {
			console.getLogger().error("Worldconsole: in iconinfo 'score' must be an integer", e1);
			score = objectStates == null? 1 : objectStates.getNumberOfStates() + 1;
		} catch (XMLElementNotFoundException e1) {
			score = objectStates == null? 1 : objectStates.getNumberOfStates() + 1;
		}
		try {
			icon = new IconKey(XMLElementHelper.getElementByTagName(config, "image"), images, console, objectImageDir);
		} catch (XMLElementNotFoundException e1) {
			try {
				icon = new IconKeySet(XMLElementHelper.getElementByTagName(config, "imageset"), images, console, objectImageDir);
			} catch (XMLElementNotFoundException e2) {
				throw new MicropsiException(0, "WorldConsole: in iconinfo must exist 'image' or 'imageset' tag");
			}
		}
	}
	
	public boolean matches(ObjectStates otherStates) {
		if (objectStates == null) {
			return true;
		}
		if (otherStates == null) {
			return false;
		}
		return otherStates.containsAll(objectStates);
	}
	
	public int getScore() {
		return score;
	}
	
	public IIconKeyRetriever getIconRetriever() {
		return icon;
	}

}
