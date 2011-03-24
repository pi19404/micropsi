/*
 * Created on 23.11.2004
 *
 */

package org.micropsi.common.coordinates;

import org.micropsi.common.xml.XMLElementHelper;
import org.micropsi.common.xml.XMLElementNotFoundException;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.comp.world.WorldComponent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Matthias
 *
 */
public class Area2D {
	
	private Position lowestCoords, highestCoords;
	
	/**
	 * @param lowestCoords
	 * @param highestCoords
	 */
	public Area2D(Position lowestCoords, Position highestCoords) {
		super();
		this.lowestCoords = lowestCoords;
		this.highestCoords = highestCoords;
	}
	
	public Area2D(Element element) throws XMLElementNotFoundException {
		setLowestCoords(new Position(XMLElementHelper.getElementValueByTagName(element, "lowerleftcoords")));
		setHighestCoords(new Position(XMLElementHelper.getElementValueByTagName(element, "upperrightcoords")));
	}
	
	public Area2D(MTreeNode node) {
		lowestCoords = new Position(node.searchChild("lower bound").getValue());
		highestCoords = new Position(node.searchChild("upper bound").getValue());
	}
	
	public Area2D(Element element, Position defaultLowestCoords, Position defaultHighestCoords) {
		try {
			setLowestCoords(new Position(XMLElementHelper.getElementValueByTagName(element, "lowerleftcoords")));
		} catch (NumberFormatException e1) {
			WorldComponent.logger.warn("loading world: Number format error in config file (visiblearea.lowerleftcoords).", e1);
		} catch (XMLElementNotFoundException e1) {
			// ignore: Parameters optional
			setLowestCoords(defaultLowestCoords);
		}
		try {
			setHighestCoords(new Position(XMLElementHelper.getElementValueByTagName(element, "upperrightcoords")));
		} catch (NumberFormatException e1) {
			WorldComponent.logger.warn("loading world: Number format error in config file (visiblearea.lowerleftcoords).", e1);
		} catch (XMLElementNotFoundException e1) {
			// ignore: Parameters optional
			setHighestCoords(defaultHighestCoords);
		}

	}
	
	public Element toXMLElement(Document doc, String tagName) {
		Element element = doc.createElement(tagName);
		Element currentElement = doc.createElement("lowerleftcoords");
		currentElement.appendChild(doc.createTextNode(getLowestCoords().toString()));
		element.appendChild(currentElement);

		currentElement = doc.createElement("upperrightcoords");
		currentElement.appendChild(doc.createTextNode(getHighestCoords().toString()));
		element.appendChild(currentElement);

		return element;
	}

	public MTreeNode toMTreeNode(String nodeName) {
		MTreeNode node = new MTreeNode(nodeName, null, null);
		node.addChild(new MTreeNode("lower bound", getLowestCoords().toString(), null));
		node.addChild(new MTreeNode("upper bound", getHighestCoords().toString(), null));
		return node;
	}
	
	/**
	 * @return Returns the highestCoords.
	 */
	public Position getHighestCoords() {
		return highestCoords;
	}
	/**
	 * @param highestCoords The highestCoords to set.
	 */
	public void setHighestCoords(Position highestCoords) {
		this.highestCoords = highestCoords;
	}
	/**
	 * @return Returns the lowestCoords.
	 */
	public Position getLowestCoords() {
		return lowestCoords;
	}
	/**
	 * @param lowestCoords The lowestCoords to set.
	 */
	public void setLowestCoords(Position lowestCoords) {
		this.lowestCoords = lowestCoords;
	}
}
