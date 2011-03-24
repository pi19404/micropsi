/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 23.03.2003
 *
 */
package org.micropsi.comp.world;

import java.util.ArrayList;
import java.util.Iterator;

import org.micropsi.common.xml.XMLElementNotFoundException;
import org.micropsi.comp.messages.MTreeNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author matthias
 *
 */
public class ObjectProperties {
	private long objectId;
	private ArrayList<ObjectProperty> properties;

	/**
	 * 
	 */
	public ObjectProperties() {
		objectId = -1;
		properties = new ArrayList<ObjectProperty>(10);
	}
	
	public ObjectProperties(long objectId) {
		this.objectId = objectId;
		properties = new ArrayList<ObjectProperty>(10);
	}
	
	public ObjectProperties(Element element) throws XMLElementNotFoundException {
		properties = new ArrayList<ObjectProperty>(10);
		NodeList propertyNodes = element.getElementsByTagName("property");
		for (int i = 0; i < propertyNodes.getLength(); i++) {
			Element currentElement = (Element) propertyNodes.item(i);
			addProperty(new ObjectProperty(currentElement));
		}
	}
	
	public ObjectProperties(MTreeNode node) {
		objectId = node.intValue();
		properties = new ArrayList<ObjectProperty>(10);
		Iterator<MTreeNode> i = node.children();
		if (i != null) {
			while (i.hasNext()) {
				addProperty(new ObjectProperty(i.next()));
			}
		}
	}
	
	public boolean propertyExists(String name) {
		return (findProperty(name) >= 0);
	}


	/**
	 * @param property
	 */
	public void addProperty(ObjectProperty property) {
		properties.add(property);
	}
	
	public int findProperty(String key) {
		int i = 0;
		while (i < properties.size()) {
			if (properties.get(i).getKey().equals(key)) {
				break;
			} else {
				i++;
			}
		}
		return (i < properties.size())? i : -1;
	}

	public void removeProperty(ObjectProperty property) {
		properties.remove(findProperty(property.getKey()));
	}
	
	public Iterator iterator() {
		return properties.iterator();
	}

	public String toString() {
		String res = "Properties:\n";
		Iterator it = iterator();
		while (it.hasNext()) {
			res = res + it.next() + "\n";
		}
		return res;
	}
	
	public MTreeNode toMTreeNode() {
		MTreeNode res = new MTreeNode("properties", Long.toString(objectId), null);
		Iterator it = iterator();
		while (it.hasNext()) {
			res.addChild(((ObjectProperty)it.next()).toMTreeNode());
		}
		return res;
	}

	/**
	 * @return int
	 */
	public long getObjectId() {
		return objectId;
	}

	/**
	 * Sets the objectId.
	 * @param objectId The objectId to set
	 */
	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}
	
	public ObjectProperty getProperty(String name) {
		int prop = findProperty(name);
		return prop >= 0? getProperty(prop) : null;
	}
	
	public ObjectProperty getProperty(int i) {
		return properties.get(i);
	}
	
	public int getPropertyCount() {
		return properties.size();
	}

	public void removeProperty(String name) {
		int prop = findProperty(name);
		properties.remove(prop);
	}
	
	public Element toXMLElement(Document doc) {
		Element element = doc.createElement("properties");
		
		Iterator it = iterator();
		while (it.hasNext()) {
			ObjectProperty prop = (ObjectProperty) it.next();
			if (!prop.isRedundantProperty()) {
				element.appendChild(prop.toXMLElement(doc));
			}
		}

		return element;
	}

	/**
	 * @return
	 */
	public int size() {
		return properties.size();
	}
}
