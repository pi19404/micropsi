/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 04.10.2003
 *
 */
package org.micropsi.comp.world;

import org.micropsi.comp.world.objects.AbstractObject;

/**
 * @author Matthias
 *
 */
public class ChangeLogEntry {
	public static final int CT_CHANGE_POSITION = 1;
	public static final int CT_CHANGE_STATE = CT_CHANGE_POSITION << 1;
	public static final int CT_CHANGE_SUBOBJECT = CT_CHANGE_STATE << 1;
	public static final int CT_CREATE = CT_CHANGE_SUBOBJECT << 1;
	public static final int CT_REMOVE = CT_CREATE << 1;
	public static final int CT_CHANGE_OTHER = CT_REMOVE << 1;
	public static final int CT_CHANGE_ANY = Integer.MAX_VALUE;
	
	private long changeTime;
	private int changeType;
	private AbstractObject object;
	private long objectId;

	/**
	 * @param object
	 * @param changeTime
	 * @param changeType
	 */
	public ChangeLogEntry(AbstractObject object, long changeTime, int changeType) {
		super();
		this.object = object;
		this.changeTime = changeTime;
		this.changeType = changeType;
		objectId = object.getId();
	}

	/**
	 * @param changeType
	 */
	public void addChangeType(int changeType) {
		this.changeType = this.changeType | changeType;
	}

	/**
	 * @return
	 */
	public long getChangeTime() {
		return changeTime;
	}
	/**
	 * @return
	 */
	public int getChangeType() {
		return changeType;
	}

	/**
	 * @return
	 */
	public AbstractObject getObject() {
		return object;
	}

	/**
	 * @param changeType
	 */
	public boolean matchesChangeType(int changeType) {
		return (this.changeType & changeType) != 0;
	}

	/**
	 * @param changeTime
	 */
	public void setChangeTime(long changeTime) {
		this.changeTime = changeTime;
	}

	/**
	 * @param changeType
	 */
	public void setChangeType(int changeType) {
		this.changeType = changeType;
	}
	
	public void setObjectRemoved() {
		object = null;
	}

	/**
	 * @return
	 */
	public long getObjectId() {
		return objectId;
	}

}
