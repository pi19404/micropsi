package org.micropsi.common.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *  $Header $
 *  @author matthias
 *
 */
public class XMLElementHelper {
	
	public static Element getElementByTagName(Element el, String name) throws XMLElementNotFoundException {
		NodeList nl = el.getElementsByTagName(name);
		if (nl.getLength() > 0){
			return (Element) nl.item(0);
		} else {
			throw new XMLElementNotFoundException("Element " + name + " not found in Element" + el.getNodeName());
		}
	}
	
	public static String getElementValueByTagName(Element el, String name) throws XMLElementNotFoundException {
		NodeList childNodes = getElementByTagName(el, name).getChildNodes();
		String res = null;
		for (int i = 0; i < childNodes.getLength(); i++) {
			if (childNodes.item(i).getNodeType() == Node.TEXT_NODE) {
				res = childNodes.item(i).getNodeValue().trim();
				break;
			}
		}
		return res;
	}
	
	public static void appendTextElement(Document doc, Element element, String key, String value) {
		Element newElement = doc.createElement(key);
		newElement.appendChild(doc.createTextNode(value));
		element.appendChild(newElement);
	}

}
