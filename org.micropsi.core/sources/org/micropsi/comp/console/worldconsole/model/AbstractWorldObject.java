/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 06.04.2004
 *
 */
package org.micropsi.comp.console.worldconsole.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.micropsi.comp.messages.MTreeNode;

/**
 * @author Matthias
 *
 */
public abstract class AbstractWorldObject {

	protected LocalWorldModel worldModel;
	protected long id;
	protected String objectName = null;
	protected String objectClass = null;
	
	private List<AbstractWorldObject> subParts = null;

	public long getId() {
		return id;
	}

	public String getObjectClass() {
		return objectClass;	
	}

	/**
	 * @return Returns the subParts.
	 */
	public Collection<AbstractWorldObject> getSubParts() {
		return subParts;
	}
	
	protected void appendAllParts(Collection<AbstractWorldObject> coll) {
		coll.addAll(getSubParts());
		for (Iterator it = getSubParts().iterator(); it.hasNext(); ) {
			AbstractWorldObject obj = (AbstractWorldObject) it.next();
			obj.appendAllParts(coll);
		}
	}
	
	public Collection<AbstractWorldObject> getAllParts() {
		Collection<AbstractWorldObject> res = new ArrayList<AbstractWorldObject>(10);
		appendAllParts(res);
		return res;
	}
	
	public AbstractWorldObject findPartById(long id) {
		Collection parts = getSubParts();
		if (parts != null) {
			for (Iterator it = parts.iterator(); it.hasNext(); ) {
				AbstractWorldObject obj = (AbstractWorldObject) it.next();
				if (obj.getId() == id) {
					return obj;
				}
				AbstractWorldObject obj2 = obj.findPartById(id);
				if (obj2 != null) {
					return obj2;
				}
			}
		}
		return null;
	}
	
	/**
	 * @param subParts The subParts to set.
	 */
	public void setSubParts(List<AbstractWorldObject> subObjects) {
		this.subParts = subObjects;
	}
	
	public abstract AbstractWorldObject getParent();
	
	public abstract WorldObject getRootObject();
	
	public abstract boolean isHighLevelObject();

	/**
	 * @param node
	 * @return
	 */
	protected List<AbstractWorldObject> buildSubObjects(MTreeNode node) {
		List<AbstractWorldObject> subObjects = new ArrayList<AbstractWorldObject>();
		Iterator<MTreeNode> i = node.children();
		if (i != null) {
			while (i.hasNext()) {
				MTreeNode subNode = i.next();
				subObjects.add(new WorldObjectPart(this, subNode));
			}
			if (subObjects.size() > 0) {
				return subObjects;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public String getObjectName() {
		return objectName;
	}
	
	abstract public String getLabelText();

	/**
	 *  AbstractWorldObjects are considered equal if and only if they have the same id.
	 */
	public int hashCode() {
		return (int) (getId() % Integer.MAX_VALUE);
	}
	
	
	/**
	 *  AbstractWorldObjects are considered equal if and only if they have the same id.
	 */
	public boolean equals(Object obj) {
		if (obj instanceof AbstractWorldObject) {
			return getId() == ((AbstractWorldObject) obj).getId();
		} else {
			return super.equals(obj);
		}
	}
}
