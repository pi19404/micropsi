/*
 * Created on 13.04.2005
 *
 */

package org.micropsi.comp.console.worldconsole;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.console.worldconsole.model.AbstractWorldObject;
import org.micropsi.comp.console.worldconsole.model.WorldObject;

/**
 * @author Matthias
 */
public class EditSession extends AbstractController implements ILocalWorldListener {
//	private LocalWorld localWorld;
	private Position objectCreatePosition;

	private Set<AbstractWorldObject> selectedObjects = new HashSet<AbstractWorldObject>(10);
	private Set<AbstractWorldObject> selectedObjectParts = new HashSet<AbstractWorldObject>(10);
	private Set<AbstractWorldObject> objectsWithSelectedParts = new HashSet<AbstractWorldObject>(10);
	private AbstractWorldObject lastSelectedObjectPart;

	public EditSession(LocalWorld localWorld) {
//		this.localWorld = localWorld;
		objectCreatePosition = new Position(0, 0, 0);
	}

	public void selectObject(AbstractWorldObject o) {
		selectObject(o, false);
	}
	
	public void selectObject(AbstractWorldObject o, boolean keepSelection) {
		Collection<AbstractWorldObject> changeList;
		if (!keepSelection) {
			changeList = new ArrayList<AbstractWorldObject>(getSelectedObjectParts());
			_unselectAll();
		} else {
			changeList = new ArrayList<AbstractWorldObject>(1);
		}
		if (!isSelected(o)) {
			_select(o, true);
			changeList.add(o);
		}
		notifySelectionChangeListeners(changeList);
	}

	public void selectObjects(Collection objects, boolean keepSelection) {
		Collection<AbstractWorldObject> changeList;
		if (!keepSelection) {
			changeList = new ArrayList<AbstractWorldObject>(getSelectedObjectParts());
			_unselectAll();
		} else {
			changeList = new ArrayList<AbstractWorldObject>(objects.size());
		}
		for (Iterator it = objects.iterator(); it.hasNext();) {
			AbstractWorldObject o = (AbstractWorldObject) it.next();
			if (!isSelected(o)) {
				_select(o, true);
				changeList.add(o);
			}
		}
		notifySelectionChangeListeners(changeList);
	}

	public void unselectObject(AbstractWorldObject o) {
		if (isSelected(o)) {
			_unselect(o, true);
			ArrayList<AbstractWorldObject> changeList = new ArrayList<AbstractWorldObject>(1);
			changeList.add(o);
			notifySelectionChangeListeners(changeList);
		}
	}
	
	public void unselectAll() {
		Collection<AbstractWorldObject> changeList = new ArrayList<AbstractWorldObject>(getSelectedObjectParts());
		_unselectAll();
		notifySelectionChangeListeners(changeList);
	}
	
	public boolean isSelected(AbstractWorldObject o) {
		return getSelectedObjectParts().contains(o);
	}
	
	public boolean hasSelectedPart(WorldObject o) {
		return objectsWithSelectedParts.contains(o);
	}

	/**
	 * notify all change listeners of change
	 */
	public void notifySelectionChangeListeners(Collection changedObjects) {
		if (changedObjects.isEmpty()) {
			return;
		}
		Iterator it = listeners.iterator();
		while (it.hasNext()) {
			WeakReference ref = (WeakReference) it.next();
			Object referenced = ref.get();
			if (referenced != null) {
				((IEditSessionListener) referenced).onSelectionChanged(this, changedObjects);
			} else {
				it.remove();
			}
		}
	}

	public Set getSelectedObjects() {
		return selectedObjects;
	}
	
	public Set<AbstractWorldObject> getSelectedObjectParts() {
		return selectedObjectParts; 
	}
	
	public WorldObject getSelectedObject() {
		if (getSelectedObjects().size() > 0) {
			return (WorldObject) getSelectedObjects().iterator().next();
		} else {
			return null;
		}
	}
	
	public AbstractWorldObject getSelectedObjectPart() {
		return lastSelectedObjectPart;
	}
	
	/**
	 * @return Returns the objectCreatePosition.
	 */
	public Position getObjectCreatePosition() {
		return objectCreatePosition;
	}

	/**
	 * @param objectCreatePosition The objectCreatePosition to set.
	 */
	public void setObjectCreatePosition(Position objectCreatePosition) {
		this.objectCreatePosition = objectCreatePosition;
	}
	
	protected void unselectSubParts(AbstractWorldObject obj) {
		Collection subparts = obj.getSubParts();
		if (subparts != null) {
			for (Iterator it = subparts.iterator(); it.hasNext(); ) {
				AbstractWorldObject part = (AbstractWorldObject) it.next();
				if (isSelected(part)) {
					_unselect(part, false);
				}
				unselectSubParts(part);
			}
		}
	}
	
	private void _unselectAll() {
		selectedObjects.clear();
		selectedObjectParts.clear();
		objectsWithSelectedParts.clear();
		lastSelectedObjectPart = null;
	}
	
	private void _select(AbstractWorldObject o, boolean updateRoot) {
		if (o instanceof WorldObject) {
			selectedObjects.add(o);
		}
		selectedObjectParts.add(o);
		if (o.getRootObject() != o && updateRoot && !hasSelectedPart(o.getRootObject())) {
			objectsWithSelectedParts.add(o.getRootObject());
		}
		lastSelectedObjectPart = o;
	}

	private void _unselect(AbstractWorldObject o, boolean updateRoot) {
		if (o instanceof WorldObject) {
			selectedObjects.remove(o);
		}
		selectedObjectParts.remove(o);
		if (o.getRootObject() != o && updateRoot && hasSelectedPart(o.getRootObject())) {
			if (!checkForSelectedPart(o.getRootObject())) {
				objectsWithSelectedParts.remove(o.getRootObject());
			}
		}
		if (lastSelectedObjectPart == o) {
			if (!selectedObjectParts.isEmpty()) {
				lastSelectedObjectPart = selectedObjectParts.iterator().next();
			} else {
				lastSelectedObjectPart = null;
			}
		}
	}
	
	protected boolean checkForSelectedPart(AbstractWorldObject obj) {
		Collection parts = obj.getSubParts();
		if (parts != null) {
			for (Iterator it = parts.iterator(); it.hasNext(); ) {
				AbstractWorldObject obj2 = (AbstractWorldObject) it.next();
				if (isSelected(obj2)) {
					return true;
				}
				boolean found = checkForSelectedPart(obj2);
				if (found) {
					return found;
				}
			}
		}
		return false;

	}

	/* @see org.micropsi.eclipse.worldconsole.ILocalWorldListener#onObjectChanged(org.micropsi.eclipse.worldconsole.LocalWorld, org.micropsi.comp.console.worldconsole.model.WorldObject)*/
	public void onObjectChanged(LocalWorld mgr, WorldObject changedObject) {
		Collection<AbstractWorldObject> coll = new ArrayList<AbstractWorldObject>(1);
		handleChangedObject(changedObject, coll);
		if (!coll.isEmpty()) {
			notifySelectionChangeListeners(coll);
		}
	}

	/* @see org.micropsi.eclipse.worldconsole.ILocalWorldListener#onMultipleObjectsChanged(org.micropsi.eclipse.worldconsole.LocalWorld, java.util.Collection)*/
	public void onMultipleObjectsChanged(LocalWorld mgr, Collection<AbstractWorldObject> changedObjects) {
		Collection<AbstractWorldObject> changed = new ArrayList<AbstractWorldObject>(5);
		for (Iterator it = changedObjects.iterator(); it.hasNext(); ) {
			WorldObject changedObject = (WorldObject) it.next();
			handleChangedObject(changedObject, changed);
		}
		if (!changed.isEmpty()) {
			notifySelectionChangeListeners(changed);
		}
		
	}

	private void handleChangedObject(WorldObject changedObject, Collection<AbstractWorldObject> changeList) {
		if (changedObject.isRemoved() && isSelected(changedObject)) {
			_unselect(changedObject, true);
			changeList.add(changedObject);
		}
		if (changedObject.isRemoved() && hasSelectedPart(changedObject)) {
			unselectSubParts(changedObject);
			objectsWithSelectedParts.remove(changedObject);
			changeList.add(changedObject);
		}
		if (((changedObject.changeType & WorldObject.CT_CHANGE_SUBPART) != 0) && hasSelectedPart(changedObject)) {
			Collection parts = changedObject.getOldObjectData().getAllParts();
			boolean changed = false;
			for (Iterator it = parts.iterator(); it.hasNext(); ) {
				AbstractWorldObject obj = (AbstractWorldObject) it.next();
				if (isSelected(obj)) {
					changed = true;;
					_unselect(obj, false);
					AbstractWorldObject newObj = changedObject.findPartById(obj.getId());
					if (newObj != null) {
						_select(newObj, false);
					}
				}
			}
			if (changed) {
				changeList.add(changedObject);
				if (!checkForSelectedPart(changedObject)) {
					objectsWithSelectedParts.remove(changedObject);
				}
			}
		}
	}

	/* @see org.micropsi.eclipse.worldconsole.ILocalWorldListener#onObjectListRefreshed(org.micropsi.eclipse.worldconsole.LocalWorld)*/
	public void onObjectListRefreshed(LocalWorld mrg) {
		// TODO Auto-generated method stub
		
	}

	/* @see org.micropsi.eclipse.worldconsole.ILocalWorldListener#onGlobalsChanged()*/
	public void onGlobalsChanged() {
		// globals? what globals? ;-)
	}

}
