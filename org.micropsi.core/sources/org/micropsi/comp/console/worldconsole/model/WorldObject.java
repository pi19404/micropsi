package org.micropsi.comp.console.worldconsole.model;

import java.util.List;

import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.messages.MTreeNode;

/**
 * @author david
 *
 * representation of world object used by WorldMapView
 */
public class WorldObject extends AbstractWorldObject implements Cloneable {
	
	public static final int CT_CHANGE_POSITION = 1;
	public static final int CT_CHANGE_STATE = CT_CHANGE_POSITION << 1;
	public static final int CT_CHANGE_SUBPART = CT_CHANGE_STATE << 1;
	public static final int CT_CREATE = CT_CHANGE_SUBPART << 1;
	public static final int CT_REMOVE = CT_CREATE << 1;
	public static final int CT_CHANGE_OTHER = CT_REMOVE << 1;
	public static final int CT_CHANGE_ANY = Integer.MAX_VALUE;

	private boolean removed = false;
	
	private Position position = null;
	private ObjectStates objectStates = null;
	private ObjectVisualInfo visualInfo = null;	// visualInfo used to display this object
	public boolean _marked = false;		// used for updating object list
	
	public WorldObject oldObjectData;
	public int changeType = 0;
	
	private int orientationAngle = 0;

	public WorldObject(LocalWorldModel m, MTreeNode mObj) {
		worldModel = m;
		updateFromTreeNode(mObj);
		changeType |= CT_CREATE;
	}
	
	private boolean eq(Object o1, Object  o2) {
		return (o1 == null && o2 == null) || (o1 != null && o2 != null && o1.equals(o2));
	}
	
	public boolean updateFromTreeNode(MTreeNode mObj) {
		long newObjID = mObj.searchChild("id").longValue();
		String newObjClass = mObj.searchChild("objectclass").getValue();
		String newObjName = mObj.searchChild("objectname").getValue();
		Position newObjPos = new Position(mObj.searchChild("position").getValue());
		ObjectStates newObjectStates = null;
		MTreeNode node = mObj.searchChild("objectstates");
		if (node != null) {
			newObjectStates = new ObjectStates(node);
		}
		int newOrientationAngle = 0;
		node = mObj.searchChild("orientation");
		if (node != null) {
			newOrientationAngle = (int) Math.round(node.doubleValue());
		}
		List<AbstractWorldObject> newSubObjects = null;
		node = mObj.searchChild("subobjects");
		if (node != null) {
			newSubObjects = buildSubObjects(node);
		}
		
		boolean res = false;
		if (getId() != newObjID
			|| !eq(getObjectClass(), newObjClass)
			|| !eq(getObjectName(), newObjName)
			|| !eq(newObjectStates, objectStates)
			|| !(newOrientationAngle == orientationAngle)) {
			beginChange();
			changeType |= CT_CHANGE_OTHER;
			id = newObjID;
			objectClass = newObjClass;
			objectName = newObjName;
			setSubParts(newSubObjects);
			setOrientationAngle(newOrientationAngle);
			updateVisualInfo();
			res = true;
		}
		if (!eq(newSubObjects, getSubParts())) {
			if (!res) {
				beginChange();
			}
			changeType |= CT_CHANGE_SUBPART;
			setObjectStates(newObjectStates);
			res = true;
		}
		if (!eq(position, newObjPos)) {
			if (!res) {
				beginChange();
			}
			position = newObjPos;
			changeType |= CT_CHANGE_POSITION;
			res = true;
		}
		return res;
	}
	
	public void beginChange() {
		oldObjectData = null;
		changeType = 0;
		try {
			oldObjectData = (WorldObject) this.clone();
		} catch (CloneNotSupportedException e) {
			// always cloneable
		}
	}

	public Position getPosition() {
		return position;
	}
	
	public ObjectVisualInfo getVisualInfo() {
		return visualInfo;
	}
	
	public void setPos(Position pos) {
		position = pos;
	}
		
	private void updateVisualInfo() {
		visualInfo = worldModel.getImageLibrary().getVisualInfo(getObjectClass(), objectStates, (orientationAngle));
	}
	
	/**
	 * @return
	 */
	public boolean isRemoved() {
		return removed;
	}

	/**
	 * @param removed
	 */
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	/**
	 * @return
	 */
	public WorldObject getOldObjectData() {
		return oldObjectData;
	}

	/**
	 * @return Returns the stateDescription.
	 */
	public String getStateDescription() {
		return visualInfo.stateDescription;
	}

	/**
	 * @return Returns the orientationAngle.
	 */
	public double getOrientationAngle() {
		return orientationAngle;
	}
	
	/**
	 * @param orientationAngle The orientationAngle to set.
	 */
	public void setOrientationAngle(int orientationAngle) {
		this.orientationAngle = orientationAngle;
	}

	/**
	 * @return Returns the objectStates.
	 */
	public ObjectStates getObjectStates() {
		return objectStates;
	}

	/**
	 * @param objectStates The objectStates to set.
	 */
	public void setObjectStates(ObjectStates objectStates) {
		this.objectStates = objectStates;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.console.worldconsole.model.AbstractWorldObject#getParent()
	 */
	public AbstractWorldObject getParent() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.console.worldconsole.model.AbstractWorldObject#getRootObject()
	 */
	public WorldObject getRootObject() {
		return this;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.console.worldconsole.model.AbstractWorldObject#isHighLevelObject()
	 */
	public boolean isHighLevelObject() {
		return true;
	}

	/* @see org.micropsi.comp.console.worldconsole.model.AbstractWorldObject#getLabelText()*/
	public String getLabelText() {
		return getObjectName() + " (id=" + getId() + ", " + getObjectClass() + ")";
	}
}
