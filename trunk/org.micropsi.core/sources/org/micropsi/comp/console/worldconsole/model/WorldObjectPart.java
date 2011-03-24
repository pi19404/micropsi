/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 09.04.2004
 *
 */
package org.micropsi.comp.console.worldconsole.model;

import java.util.List;

import org.micropsi.comp.messages.MTreeNode;

/**
 * @author Matthias
 *
 */
public class WorldObjectPart extends AbstractWorldObject {
	
	private AbstractWorldObject parent = null;
	/**
	 * 
	 */
	public WorldObjectPart() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public WorldObjectPart(AbstractWorldObject parentObject, MTreeNode node) {
		parent = parentObject;
		id = Long.parseLong(node.getName());
		objectName = null;
		objectClass = node.getValue();
		List<AbstractWorldObject> subObjects = buildSubObjects(node);
		setSubParts(subObjects);
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.console.worldconsole.model.AbstractWorldObject#getParent()
	 */
	public AbstractWorldObject getParent() {
		return parent;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.console.worldconsole.model.AbstractWorldObject#getRootObject()
	 */
	public WorldObject getRootObject() {
		return parent.getRootObject();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof WorldObjectPart) {
			WorldObjectPart subObj = (WorldObjectPart) obj;
			return (subObj.getId() == getId())
					&& subObj.getObjectClass().equals(getObjectClass())
					&& ((subObj.getSubParts() == null && getSubParts() == null) || (subObj
							.getSubParts() != null && subObj.getSubParts()
							.equals(getSubParts())));
		} else {
			return super.equals(obj);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (int) (getId() + getObjectClass().hashCode() + (getSubParts() == null? 0 : getSubParts().hashCode())) % Integer.MAX_VALUE;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.comp.console.worldconsole.model.AbstractWorldObject#isHighLevelObject()
	 */
	public boolean isHighLevelObject() {
		return false;
	}

	/* @see org.micropsi.comp.console.worldconsole.model.AbstractWorldObject#getLabelText()*/
	public String getLabelText() {
		return getObjectClass() + " (id=" + getId() + ")";
	}
}
