/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 29.03.2003
 *
 */
package org.micropsi.comp.world;

import org.w3c.dom.Element;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.xml.XMLElementHelper;
import org.micropsi.common.xml.XMLElementNotFoundException;

/**
 * @author matthias
 *
 */
public class WorldObjectType {
	
	String typeName;
	String objectClass;
	String javaClass;
	ObjectProperties properties;

	public WorldObjectType(Element element) throws MicropsiException {
		try {
			typeName =	XMLElementHelper.getElementValueByTagName(element, "typename");
			objectClass = XMLElementHelper.getElementValueByTagName(element, "objectclass");
			javaClass = XMLElementHelper.getElementValueByTagName(element, "javaclass");
		} catch (XMLElementNotFoundException e) {
			throw new MicropsiException(10, "Error reading world object type: required element 'typename'," +
										" 'objectclass' or 'javaclass' missing.", e);
		}
		if (javaClass.indexOf(".") < 0) {
			javaClass = "org.micropsi.comp.world.objects." + javaClass;
		}
		try {
			properties = new ObjectProperties(XMLElementHelper.getElementByTagName(element, "properties"));
		} catch (XMLElementNotFoundException e1) {
			// properties are optional
		}
		
	}

	/**
	 * @return String
	 */
	public String getJavaClass() {
		return javaClass;
	}

	/**
	 * @return String
	 */
	public String getObjectClass() {
		return objectClass;
	}

	/**
	 * @return ObjectProperties
	 */
	public ObjectProperties getProperties() {
		return properties;
	}

	/**
	 * @return String
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * Sets the javaClass.
	 * @param javaClass The javaClass to set
	 */
	public void setJavaClass(String javaClass) {
		this.javaClass = javaClass;
	}

	/**
	 * Sets the objectClass.
	 * @param objectClass The objectClass to set
	 */
	public void setObjectClass(String objectClass) {
		this.objectClass = objectClass;
	}

	/**
	 * Sets the properties.
	 * @param properties The properties to set
	 */
	public void setProperties(ObjectProperties properties) {
		this.properties = properties;
	}

	/**
	 * Sets the typeName.
	 * @param typeName The typeName to set
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

}
