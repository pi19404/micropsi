/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 23.03.2003
 *
 */
package org.micropsi.comp.world;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.micropsi.common.xml.XMLElementHelper;
import org.micropsi.common.xml.XMLElementNotFoundException;
import org.micropsi.comp.messages.MTreeNode;

/**
 * @author matthias
 *
 */
public class ObjectProperty {
	public static final int VTYPE_BOOLEAN = 3;
	public static final int VTYPE_DOUBLE = 1;
	public static final int VTYPE_INT = 2;
	public static final int VTYPE_STRING = 0;
	private boolean redundantProperty = false;  // if true, property will not be saved, ...
	private boolean editable = true;

	
	private String key;
	private String comment = null;
	private String value;
	private int valueType = VTYPE_STRING;
	
	public ObjectProperty(Element element) throws XMLElementNotFoundException {
		try {
			key =
				XMLElementHelper.getElementValueByTagName(
					element,
					"name");
		} catch (XMLElementNotFoundException e) {
			throw new XMLElementNotFoundException("Error reading object property: property with missing 'name' item.");
		}
		try {
			value =
				XMLElementHelper.getElementValueByTagName(
					element,
					"value");
		} catch (XMLElementNotFoundException e) {
			value = "true";
		}
		
	}
	
	public ObjectProperty(MTreeNode node) {
		key = node.searchChild("key").getValue();
		value = node.searchChild("value").getValue();
		valueType = node.searchChild("valueType").intValue();
		editable = node.searchChild("editable").boolValue();
		MTreeNode subnode = node.searchChild("comment");
		if (subnode != null) {
			comment = subnode.getValue();
		}
	}

	public ObjectProperty(String key, String value) {
		this.key = key; this.value = value;
	}

	public ObjectProperty(String key, String value, int valueType) {
		this.key = key; this.value = value;
		setValueType(valueType);
	}

	public ObjectProperty(String key, String value, int valueType, boolean editable) {
		this.key = key; this.value = value; this.editable = editable;
		setValueType(valueType);
	}

	public ObjectProperty(String key, String value, int valueType, boolean editable, boolean redundantProperty) {
		this.key = key; this.value = value; this.editable = editable;
		setValueType(valueType);
		this.redundantProperty = redundantProperty;
	}
	
	public boolean getBoolValue() {
		return Boolean.getBoolean(value);
	}
	
	public double getDoubleValue() {
		return Double.parseDouble(value);
	}
	
	public int getIntValue() {
		return Integer.parseInt(value);
	}

	/**
	 * @return String
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return String
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return String
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @return int
	 */
	public int getValueType() {
		return valueType;
	}

	/**
	 * @return
	 */
	public boolean isRedundantProperty() {
		return redundantProperty;
	}

	/**
	 * @return boolean
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * @param redundantProperty
	 */
	public void setRedundantProperty(boolean convenienceProperty) {
		this.redundantProperty = convenienceProperty;
	}

	/**
	 * Sets the editable.
	 * @param editable The editable to set
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	/**
	 * Sets the key.
	 * @param key The key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Sets the comment.
	 * @param comment The comment to set
	 */
	public void setComment(String result) {
		this.comment = result;
	}

	/**
	 * Sets the value.
	 * @param value The value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Sets the valueType.
	 * @param valueType The valueType to set
	 */
	public void setValueType(int valueType) {
		this.valueType = valueType;
	}
	
	public MTreeNode toMTreeNode() {
		MTreeNode res = new MTreeNode();
		res.addChild("key", getKey());
		res.addChild("value", getValue());
		res.addChild("valueType", getValueType());
		res.addChild("editable", isEditable());
		if (getComment() != null) {
			res.addChild("comment", getComment());
		}
		return res;
	}
	
	public String toString() {
		String res ="[" + getKey() + ": " + getValue() + "]";
		if (getComment() != null) {
			res += " - " + getComment();
		}
		return res;
	}
	
	public Element toXMLElement(Document doc) {
		Element element = doc.createElement("property");

		Element currentElement;
		currentElement = doc.createElement("name");
		currentElement.appendChild(doc.createTextNode(getKey()));
		element.appendChild(currentElement);

		currentElement = doc.createElement("value");
		currentElement.appendChild(doc.createTextNode(getValue()));
		element.appendChild(currentElement);

		return element;
	}

}
