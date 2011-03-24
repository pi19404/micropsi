/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 06.10.2003
 *
 */
package org.micropsi.comp.console.worldconsole.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;
import org.micropsi.common.coordinates.Area2D;
import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.comp.messages.MTreeNode;

/**
 * @author Matthias
 *
 */
public class LocalWorldModel {

	public class ChangedObject {
		public WorldObject currentObject;
		public WorldObject oldObject;
		
		public ChangedObject(WorldObject currentObject, WorldObject oldObject) {
			this.currentObject = currentObject;
			this.oldObject = oldObject;
		}
	}
	
	private Map<Long,WorldObject> objects;					// list of all knows objects, hashed by id
	private List<WorldObject> selectedObjects;			// list of selected objects
	
	private String worldFileName = null;
	private String groundmapImageFile = null;
	private long simStep = -1;
	
	private Collection<WorldObject> timedLockedObjects = new ArrayList<WorldObject>(2);

	private ImageLibrary objectClassLibrary;

	// global data
	private Area2D visibleArea = null;
	private Area2D groundmapArea = null;
	private int versionGlobalData = -1;
	
	/**
	 * 
	 */
	public LocalWorldModel(String configFileURL, ConsoleFacadeIF console) {
		objects  = new HashMap<Long,WorldObject>();
		selectedObjects = new ArrayList<WorldObject>();
		objectClassLibrary = new ImageLibrary(configFileURL, console);
		setVisibleArea(new Area2D(new Position(0, 0, 0), new Position(50, 50)));
		setGroundmapArea(new Area2D(null, null));
	}

	public ImageLibrary getImageLibrary() {
		return objectClassLibrary;
	}
	
	public WorldObject processObjectUpdate(MTreeNode mobj, List<AbstractWorldObject> changeList) {
		long id = mobj.searchChild("id").longValue();
		WorldObject o = getObjectByID(id);
		if(o != null) {
			if (o.updateFromTreeNode(mobj)) {
				changeList.add(o);
			}
		} else {
			o = new WorldObject(this, mobj);
			addObject(o);
			changeList.add(o);
		}
		return o;
	}

	public int numObjects() {
		return objects.size();
	}
	
	/**
	 * return object with given id or null if id is not found
	 */
	public WorldObject getObjectByID(long objID) {
		return objects.get(new Long(objID));
	}

	public void addObject(WorldObject obj) {
		objects.put(new Long(obj.getId()), obj);
	}
	
	public void removeObject(WorldObject obj) {
		obj.setRemoved(true);
		objects.remove(new Long(obj.getId()));
	}


	public int numSelectedObjects() {
		return selectedObjects.size();
	}


	public AbstractWorldObject getSelectedObject(int i) {
		return selectedObjects.get(i);
	}


	/**
	 * @return
	 */
	public Collection getObjects() {
		return objects.values();
	}
	
	public void updateAllObjects(MTreeNode objectListNode, List<AbstractWorldObject> changeList) {
		synchronized(this) {
			Iterator<MTreeNode> i = objectListNode.children();
			if (i != null) {
				while (i.hasNext()) {
					WorldObject obj = processObjectUpdate(i.next(), changeList);
					obj._marked = true;
				}
			}
			Iterator it = getObjects().iterator();
			while (it.hasNext()) {	// remove objects that have not been updated.
				WorldObject obj = (WorldObject) it.next();
				if (obj._marked) {
					obj._marked = false;
				} else {
					changeList.add(obj);
					// emulates "removeObject(obj)", but does this on the iterator
					obj.setRemoved(true);
					it.remove();
				}
			}
			
		}
	}
	
	public void updateChangedObjects(MTreeNode objectListNode, List<AbstractWorldObject> changeList) {
		synchronized(this) {
			Iterator<MTreeNode> i = objectListNode.children();
			if (i != null) {
				while (i.hasNext()) {
					MTreeNode changeEntry = i.next();
					if (changeEntry.getName().equals("object")) {
						processObjectUpdate(changeEntry, changeList);
					} else if (changeEntry.getName().equals("remove")) {
						WorldObject obj = getObjectByID(changeEntry.longValue());
						if (obj != null) {
							changeList.add(obj);
							removeObject(obj);
						}
					}
				}
			}
		}
	}
	
	public Image getGroundImage() {
		return getImageLibrary().getGroundmapImage();
	}


	/**
	 * @return Returns the worldFileName.
	 */
	public String getWorldFileName() {
		return worldFileName;
	}
	
	public void setWorldFileName(String worldFileName) {
		this.worldFileName = worldFileName;
	}
	/**
	 * @return Returns the groundmapArea.
	 */
	public Area2D getGroundmapArea() {
		return groundmapArea;
	}
	/**
	 * @param groundmapArea The groundmapArea to set.
	 */
	public void setGroundmapArea(Area2D groundmapArea) {
		this.groundmapArea = groundmapArea;
	}
	/**
	 * @return Returns the visibleArea.
	 */
	public Area2D getVisibleArea() {
		return visibleArea;
	}
	/**
	 * @param visibleArea The visibleArea to set.
	 */
	public void setVisibleArea(Area2D visibleArea) {
		this.visibleArea = visibleArea;
	}
	/**
	 * @return Returns the versionGlobalData.
	 */
	public int getVersionGlobalData() {
		return versionGlobalData;
	}
	/**
	 * @param versionGlobalData The versionGlobalData to set.
	 */
	public void setVersionGlobalData(int versionGlobalData) {
		this.versionGlobalData = versionGlobalData;
	}
	/**
	 * @return Returns the groundmapImageFile.
	 */
	public String getGroundmapImageFile() {
		return groundmapImageFile;
	}
	/**
	 * @param groundmapImageFile The groundmapImageFile to set.
	 */
	public void setGroundmapImageFile(String groundmapImageFile) {
		this.groundmapImageFile = groundmapImageFile;
		getImageLibrary().setGroundmapImageFile(groundmapImageFile);
	}
	/**
	 * @return Returns the simStep.
	 */
	public long getSimStep() {
		return simStep;
	}
	/**
	 * @param simStep The simStep to set.
	 */
	public void setSimStep(long simStep) {
		this.simStep = simStep;
	}
	
	/**
	 * @return Returns the timedLockedObjects.
	 */
	public Collection getTimedLockedObjects() {
		return timedLockedObjects;
	}
	
	public void addTimedLockedObject(WorldObject obj) {
		timedLockedObjects.add(obj);
	}
	public void removeTimedLockedObject(WorldObject obj) {
		timedLockedObjects.remove(obj);
	}
}
