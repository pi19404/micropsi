/*
 * Created on 12.07.2004
 *
 */

package org.micropsi.comp.world.objects;

import org.apache.log4j.Logger;
import org.micropsi.common.coordinates.Position;
import org.micropsi.common.coordinates.WorldVector;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.comp.world.AbstractPropertyAccessor;
import org.micropsi.comp.world.ChangeLog;
import org.micropsi.comp.world.ChangeLogEntry;
import org.micropsi.comp.world.ObjectProperty;
import org.micropsi.comp.world.World;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Matthias
 *
 */
public class AbstractObject extends AbstractObjectPart {
	
	protected boolean persistent;
	protected ChangeLogEntry lastChange;

	protected ObjectState state;
	
	protected String objectName;
	protected WorldVector moveVector;
	protected double weight;

	/**
	 * @param configData
	 * @param logger
	 * @throws MicropsiException
	 */
	public AbstractObject(Element configData, Logger logger)
			throws MicropsiException {
		super(configData, logger);

		state = new ObjectState();
		initObjectState();
	}
	
	/**
	 * @param objectName
	 * @param objectClass
	 * @param pos
	 */
	public AbstractObject(String objectName, String objectClass,
			Position pos) {
		super(objectClass, pos);
		this.objectName = objectName;
		state = new ObjectState();
		initObjectState();
	}
	
	
	// Initialize
	
	/* @see org.micropsi.comp.world.objects.AbstractObjectPart#initProperties()*/
	protected void initProperties() {
		addRequiredProperty(new AbstractPropertyAccessor("name", ObjectProperty.VTYPE_STRING) {
			protected boolean _setProperty(ObjectProperty prop) {
				setObjectName(prop.getValue());
				return true;
			}
			protected String getValue() {
				return getObjectName();
			}
			
		});
		super.initProperties();
		addOptionalProperty(new AbstractPropertyAccessor("weight", ObjectProperty.VTYPE_DOUBLE) {
			protected boolean _setProperty(ObjectProperty prop) {
				setWeight(prop.getDoubleValue());
				return true;
			}
			protected String getValue() {
				return Double.toString(getWeight());
			}
		});
	}
	
	protected void initObjectState() {
	}
	
	
	// External
	
	/**
	 * Returns the objectName.
	 * @return String
	 */
	public String getObjectName() {
		return objectName;
	}
	
	/**
	 * Sets the objectName.
	 * @param objectName The objectName to set
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
		logChange(ChangeLogEntry.CT_CHANGE_OTHER);
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AbstractObjectPart#toMTreeNode()
	 */
	public MTreeNode toMTreeNode() {
		MTreeNode res = super.toMTreeNode();
		res.addChild("objectname", getObjectName());
		MTreeNode stateNode = getState().toMTreeNode();
		if (stateNode != null) {
			res.addChild(stateNode);
		}
		return res;
	}

	/**
	 * Returns the moveVector.
	 * @return ForceVector
	 */
	public WorldVector getMoveVector() {
		return moveVector;
	}

	/**
	 * Returns the weight.
	 * @return long
	 */
	public double getWeight() {
		return weight;
	}

	public boolean isPersistent() {
		return persistent;
	}

	public void setState(String key, String value) {
		getState().setState(key, value);
		logChange(ChangeLogEntry.CT_CHANGE_STATE);
	}

	public void defaultState(String key, String value) {
		if (getState(key) == null) {
			setState(key, value);
		}
	}

	public String getState(String key) {
		return state.getState(key);
	}

	/**
	 * Sets the moveVector.
	 * @param moveVector The moveVector to set
	 */
	public void setMoveVector(WorldVector vector) {
		this.moveVector = vector;
	}

	/**
	 * Sets the weight.
	 * @param weight The weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}


	public ChangeLogEntry getLastChange() {
		return lastChange;
	}

	
	public Element toXMLElement(Document doc) {
		return toXMLElement(doc, true, false);
	}


	public Element toXMLElement(Document doc, boolean saveState, boolean saveId) {
		Element element = doc.createElement("object");
	
		Element currentElement;
		currentElement = doc.createElement("class");
		currentElement.appendChild(doc.createTextNode(getClass().getName()));
		element.appendChild(currentElement);
	
		if (saveId) {
			currentElement = doc.createElement("id");
			currentElement.appendChild(doc.createTextNode(Long.toString(getId())));
			element.appendChild(currentElement);
		}
	
		currentElement = doc.createElement("data");
		currentElement.appendChild(getProperties().toXMLElement(doc));
		if (saveState) {
			currentElement.appendChild(getState().toXMLElement(doc));
		}
		element.appendChild(currentElement);
	
		return element;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "name: " + getObjectName() + "\n" + super.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AbstractObjectPart#rotateBy(double)
	 */
	public void rotateBy(double angle) {
		super.rotateBy(angle);
		logChange(ChangeLogEntry.CT_CHANGE_POSITION);
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AbstractObjectPart#scaleBy(double, double, double)
	 */
	public void scaleBy(double xFactor, double yFactor, double zFactor) {
		// TODO Auto-generated method stub
		super.scaleBy(xFactor, yFactor, zFactor);
		logChange(ChangeLogEntry.CT_CHANGE_POSITION);
	}
	
	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AbstractObjectPart#setSize(double, double, double)
	 */
	public void setSize(double x, double y, double z) {
		// TODO Auto-generated method stub
		super.setSize(x, y, z);
		logChange(ChangeLogEntry.CT_CHANGE_POSITION);
	}
	
	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AbstractObjectPart#init(org.micropsi.comp.world.World)
	 */
	public void init(World world) {
		super.init(world);
		logChange(ChangeLogEntry.CT_CREATE);
	}

	
	
	// Internal
	
	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AbstractObjectPart#setPosition(org.micropsi.common.coordinates.Position)
	 */
	protected void setPosition(Position position) {
		super.setPosition(position);
		logChange(ChangeLogEntry.CT_CHANGE_POSITION);
	}

	/**
	 * @return ObjectState
	 */
	protected ObjectState getState() {
		return state;
	}


	protected void logChange(int changeType) {
		if (isAlive() && isHighLevelObject()) {
			ChangeLog log = world.getChangeLog();
			if (log != null) {
				if (lastChange != null) {
					log.updateChangeLogEntry(lastChange, world.getSimStep(), changeType);
				} else {
					lastChange = log.addChange(this, world.getSimStep(), changeType);
				}
				if ((changeType & ChangeLogEntry.CT_REMOVE) != 0) {
					lastChange.setObjectRemoved();
				}
			}
		}
	}

	protected String getObjectIdentification() {
		if (objectName != null) {
			return "object " + getObjectName() + "(" + getId() + ")";
		} else {
			return super.getObjectIdentification();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.micropsi.comp.world.objects.AbstractObjectPart#_remove()
	 */
	public void _remove() {
		logChange(ChangeLogEntry.CT_REMOVE);
		super._remove();
	}
	/* @see org.micropsi.comp.world.objects.AbstractObjectPart#initObjectParameters()*/
	protected void initObjectParameters() {
		super.initObjectParameters();
		persistent = true;
		lastChange = null;

		moveVector = null;
		weight = 0;
	}
}
