/*
 * Created on 28.01.2005
 *
 */

package org.micropsi.comp.world.messages;

import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.world.ObjectProperties;
import org.micropsi.comp.world.objects.AbstractObjectPart;

/**
 * @author Matthias
 *
 */
public class ObjectCreationMessage extends WorldMessage {

	private String objectName;
	private String objectClass;
	private String objectJavaClass;
	private Position objectPos;
	private ObjectProperties objectProperties;

	public ObjectCreationMessage(String objectName, String objectClass, String objectJavaClass, Position objectPos, ObjectProperties objectProperties, AbstractObjectPart sender) {
		super("object management", "create object", sender);
		this.objectName = objectName;
		this.objectClass = objectClass;
		this.objectJavaClass = objectJavaClass;
		this.objectPos = objectPos;
		this.objectProperties = objectProperties;
	}

	public ObjectCreationMessage(String objectName, String objectClass, String objectJavaClass, Position objectPos, AbstractObjectPart sender) {
		this(objectName, objectClass, objectJavaClass, objectPos, null, sender);
	}

	/**
	 * @return Returns the objectProperties.
	 */
	public ObjectProperties getObjectProperties() {
		return objectProperties;
	}
	/**
	 * @param objectProperties The objectProperties to set.
	 */
	public void setObjectProperties(ObjectProperties objectProperties) {
		this.objectProperties = objectProperties;
	}
	/**
	 * @return Returns the objectClass.
	 */
	public String getObjectClass() {
		return objectClass;
	}
	/**
	 * @return Returns the objectJavaClass.
	 */
	public String getObjectJavaClass() {
		return objectJavaClass;
	}
	/**
	 * @return Returns the objectName.
	 */
	public String getObjectName() {
		return objectName;
	}
	/**
	 * @return Returns the objectPos.
	 */
	public Position getObjectPos() {
		return objectPos;
	}
	/* @see java.lang.Object#toString()*/
	public String toString() {
		return super.toString() + "\nObject name: " + getObjectName()
				+ "\nObject class: " + getObjectClass() + "\nJava class: "
				+ getObjectJavaClass() + "\nPosition: " + getObjectPos()
				+ "\nProperties: " + getObjectProperties();
	}
}
