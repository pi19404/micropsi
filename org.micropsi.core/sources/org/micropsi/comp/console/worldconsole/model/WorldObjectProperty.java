/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 23.03.2003
 *
 */
package org.micropsi.comp.console.worldconsole.model;

import org.micropsi.common.xml.XMLElementHelper;
import org.micropsi.common.xml.XMLElementNotFoundException;
import org.micropsi.comp.messages.MTreeNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author matthias
 *
 */
public class WorldObjectProperty {
	public static final int VTYPE_STRING = 0;
	public static final int VTYPE_DOUBLE = 1;
	public static final int VTYPE_INT = 2;
	public static final int VTYPE_BOOLEAN = 3;

	
	private String key;
	private String value;
	private boolean editable = true;
	private int valueType = VTYPE_STRING;
	private String comment = null;
	private int currentUpdateId = -1; // id of question that tries to set this property in remote world.
	private boolean noUpdate = false; // don't update this property, for instance because it is being edited.
	private boolean updated = true;

	public WorldObjectProperty(String key, String value) {
		this.key = key; this.value = value;
	}

	public WorldObjectProperty(String key, String value, int valueType) {
		this.key = key; this.value = value;
		setValueType(valueType);
	}

	public WorldObjectProperty(String key, String value, int valueType, boolean editable) {
		this.key = key; this.value = value; this.editable = editable;
		setValueType(valueType);
	}
	
	public WorldObjectProperty(MTreeNode node) {
		key = node.searchChild("key").getValue();
		value = node.searchChild("value").getValue();
		valueType = node.searchChild("valueType").intValue();
		editable = node.searchChild("editable").boolValue();
		MTreeNode subnode = node.searchChild("comment");
		if (subnode != null) {
			comment = subnode.getValue();
		}
	}
	
	public WorldObjectProperty(Element element) throws XMLElementNotFoundException {
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

	/**
	 * @return boolean
	 */
	public boolean isEditable() {
		return editable;
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
	
	public int getIntValue() {
		return Integer.parseInt(value);
	}
	
	public boolean getBoolValue() {
		return Boolean.getBoolean(value);
	}
	
	public double getDoubleValue() {
		return Double.parseDouble(value);
	}

	/**
	 * Sets the editable.
	 * @param editable The editable to set
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
		updated = true;
	}

	/**
	 * Sets the key.
	 * @param key The key to set
	 */
	public void setKey(String key) {
		this.key = key;
		updated = true;
	}

	/**
	 * Sets the comment.
	 * @param comment The comment to set
	 */
	public void setComment(String result) {
		this.comment = result;
		updated = true;
	}

	/**
	 * Sets the value.
	 * @param value The value to set
	 */
	public void setValue(String value) {
		this.value = value;
		updated = true;
	}

	/**
	 * @return int
	 */
	public int getValueType() {
		return valueType;
	}

	/**
	 * Sets the valueType.
	 * @param valueType The valueType to set
	 */
	public void setValueType(int valueType) {
		this.valueType = valueType;
	}
	
	public String toString() {
		String res ="[" + getKey() + ": " + getValue() + "]";
		if (getComment() != null) {
			res += " - " + getComment();
		}
		return res;
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
	
	/**
	 * @return
	 */
	public int getCurrentUpdateId() {
		return currentUpdateId;
	}

	/**
	 * @param i
	 */
	public void setCurrentUpdateId(int i) {
		currentUpdateId = i;
	}

	/**
	 * @return
	 */
	public boolean isNoUpdate() {
		return noUpdate;
	}

	/**
	 * @param b
	 */
	public void setNoUpdate(boolean b) {
		noUpdate = b;
	}
	
	public void replaceDataBy(WorldObjectProperty prop) {
		setKey(prop.getKey());
		setValue(prop.getValue());
		setValueType(prop.getValueType());
		setEditable(prop.isEditable());
		setNoUpdate(prop.isNoUpdate());
		setComment(prop.getComment());
		setCurrentUpdateId(prop.getCurrentUpdateId());
		setUpdated(true);
	}

	/**
	 * @return
	 */
	public boolean isUpdated() {
		return updated;
	}

	/**
	 * @param b
	 */
	public void setUpdated(boolean b) {
		updated = b;
	}

	private boolean eq(Object o1, Object o2){
		return (o1 == null && o2 == null) || (o1 != null && o2 != null && o1.equals(o2));
	}
	
	private int hashKey(Object o){
		return (o==null? 0: o.hashCode());
	}

	public boolean equals(Object o) {
 		if (o instanceof WorldObjectProperty) {
			WorldObjectProperty p = (WorldObjectProperty) o;
			return (
				eq(getKey(), p.getKey())
					&& eq(getValue(), p.getValue())
					&& eq(getComment(), p.getComment())
					&& getValueType() == p.getValueType()
					&& isEditable() == p.isEditable());
		} else {
			return false;
		}
	}

	public int hashCode() {
		return hashKey(getKey())
			+ hashKey(getValue())
			+ hashKey(getComment())
			+ getValueType()
			+ (isEditable() ? 1 : 0);
	}

}
