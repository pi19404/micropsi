/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 23.03.2003
 *
 */
package org.micropsi.comp.console.worldconsole.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import org.micropsi.common.xml.XMLElementNotFoundException;
import org.micropsi.comp.messages.MTreeNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author matthias
 *
 */
public class WorldObjectProperties {
	private long objectId;
	private ArrayList<WorldObjectProperty> properties;
	private boolean listStructureUpdated = true;

	/**
	 * 
	 */
	public WorldObjectProperties() {
		objectId = -1;
		properties = new ArrayList<WorldObjectProperty>(10);
	}
	
	public WorldObjectProperties(long objectId) {
		this.objectId = objectId;
		properties = new ArrayList<WorldObjectProperty>(10);
	}
	
	public WorldObjectProperties(Element element) throws XMLElementNotFoundException {
		properties = new ArrayList<WorldObjectProperty>(10);
		NodeList propertyNodes = element.getElementsByTagName("property");
		for (int i = 0; i < propertyNodes.getLength(); i++) {
			Element currentElement = (Element) propertyNodes.item(i);
			addProperty(new WorldObjectProperty(currentElement));
		}
	}
	
	public WorldObjectProperties(MTreeNode node) {
		objectId = node.intValue();
		properties = new ArrayList<WorldObjectProperty>(10);
		Iterator<MTreeNode> i = node.children();
		if (i != null) {
			while (i.hasNext()) {
				addProperty(new WorldObjectProperty(i.next()));
			}
		}
	}
	
	public boolean propertyExists(String name) {
		return (findProperty(name) >= 0);
	}


	/**
	 * @param property
	 */
	public void addProperty(WorldObjectProperty property) {
		properties.add(property);
		setListStructureUpdated(true);
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

	public void removeProperty(WorldObjectProperty property) {
		properties.remove(findProperty(property.getKey()));
	}
	
	public ListIterator iterator() {
		return properties.listIterator();
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
			res.addChild(((WorldObjectProperty)it.next()).toMTreeNode());
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
	
	public WorldObjectProperty getProperty(String name) {
		int pos = findProperty(name);
		return (pos >= 0)? getProperty(pos) : null;
	}
	
	public WorldObjectProperty getProperty(int i) {
		return properties.get(i);
	}
	
	public int getPropertyCount() {
		return properties.size();
	}

	public void removeProperty(String name) {
		properties.remove(name);
		setListStructureUpdated(true);
	}
	
	public Element toXMLElement(Document doc) {
		Element element = doc.createElement("properties");
		
		Iterator it = iterator();
		while (it.hasNext()) {
			element.appendChild(((WorldObjectProperty) it.next()).toXMLElement(doc));
		}

		return element;
	}
	
	public void updateDataBy(WorldObjectProperties prop) {
		Iterator it = prop.iterator();
		while (it.hasNext()) {
			WorldObjectProperty newProperty = (WorldObjectProperty) it.next();
			WorldObjectProperty oldProperty = getProperty(newProperty.getKey());
			if (oldProperty != null) {
				if (!oldProperty.isNoUpdate()) {
					if (!oldProperty.equals(newProperty)) {
						oldProperty.replaceDataBy(newProperty);
					}
				}
			} else {
				addProperty(newProperty);
			}
		}
	}

	/**
	 * 
	 */
	public void clear() {
		properties.clear();
		setListStructureUpdated(true);
	}

	/**
	 * @return
	 */
	public boolean isListStructureUpdated() {
		return listStructureUpdated;
	}

	/**
	 * @param b
	 */
	public void setListStructureUpdated(boolean b) {
		listStructureUpdated = b;
	}
	
	public void clearUpdateState() {
		setListStructureUpdated(false);
		Iterator it = iterator();
		while (it.hasNext()) {
			((WorldObjectProperty)it.next()).setUpdated(false);
		}
	}

}
