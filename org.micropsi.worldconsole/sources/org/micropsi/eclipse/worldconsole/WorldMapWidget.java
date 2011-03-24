package org.micropsi.eclipse.worldconsole;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.console.worldconsole.EditSession;
import org.micropsi.comp.console.worldconsole.IEditSessionListener;
import org.micropsi.comp.console.worldconsole.ILocalWorldListener;
import org.micropsi.comp.console.worldconsole.IOverlayChangeNotifier;
import org.micropsi.comp.console.worldconsole.IRequestResultHandler;
import org.micropsi.comp.console.worldconsole.LocalWorld;
import org.micropsi.comp.console.worldconsole.WorldRenderer;
import org.micropsi.comp.console.worldconsole.WorldRenderer.ObjectPainter;
import org.micropsi.comp.console.worldconsole.model.AbstractWorldObject;
import org.micropsi.comp.console.worldconsole.model.WorldObject;

/**This widget shows a 2d view of a LocalWorld.
 * It automatically updates to changes, and gives the user the possibility to make changes
 * to the world.
 * 
 * @author Matthias, David
 */
public class WorldMapWidget extends Canvas implements PaintListener,
		MouseMoveListener, MouseListener, ControlListener,
		ILocalWorldListener, IEditSessionListener, IRequestResultHandler, IOverlayChangeNotifier {
	
	public static final int MODE_SELECT = 0;
	public static final int MODE_CREATE = 1;
	public static final int MODE_ZOOM = 2;

	private static final int DRAG_NO = 0;
	private static final int DRAG_OBJECT = 1;
	private static final int DRAG_SELECTIONRECTANGLE = 2;
	
	private double defaultScale = 15; // scale for zoom=100%
	private int editMode = MODE_SELECT;
	private String createObjectType = null;

	private Cursor selectCursor;
	private Cursor createCursor;
	private Cursor zoomInCursor;
	private Cursor zoomOutCursor;

	private int dragging = DRAG_NO;
	private Point dragPoint;
	private boolean draggedAway = false;	// true, if mouse has been dragged far enough
											// not to be ignored
	private boolean reclickedSelectedObject = false;

	private Image backBuffer = null;
	private boolean doubleBuffering;

	private LocalWorld localWorld = null;
	private EditSession editSession = null;
	
	private WorldRenderer renderer = null;
	protected IWorldWidgetScroller worldScroller = null;
	
	protected Set<AbstractWorldObject> lockedObjects = new HashSet<AbstractWorldObject>(5);
	protected Map<Long,LockedObjectInfo> lockedObjectInfo = new HashMap<Long,LockedObjectInfo>(5);
	
	protected class LockedObjectInfo {
		public LockedObjectInfo(WorldObject object, int requestId) {
			super();
			this.object = object;
			this.requestId = requestId;
		}
		public WorldObject object;
		public int requestId;
	}
	

	/**
	 * Constructor for WorldMapWidget.
	 * 
	 * @param parent - parent composite
	 * @param mgr - the LocalWorld that should be shown.
	 */
	public WorldMapWidget(Composite parent, LocalWorld mgr, EditSession editSession) {
		super(parent, SWT.NO_BACKGROUND);

		localWorld = mgr;
		this.editSession = editSession;

		renderer = new WorldRenderer(mgr, editSession, this);

		selectCursor = new Cursor(Display.getCurrent(), SWT.CURSOR_ARROW);
		createCursor = loadMouseCursor("icons/createobjectcursor.png", "icons/createobjectcursor_mask.png", 10, 10, SWT.CURSOR_CROSS);
		zoomInCursor = loadMouseCursor("icons/zoomincursor.png", "icons/zoomcursor_mask.png", 5, 5, SWT.CURSOR_ARROW);
		zoomOutCursor = loadMouseCursor("icons/zoomoutcursor.png", "icons/zoomcursor_mask.png", 5, 5, SWT.CURSOR_ARROW);

		addPaintListener(this);
		addMouseMoveListener(this);
		addMouseListener(this);

		dragPoint = new Point(0, 0);

		doubleBuffering = true;

		setSize(1500, 1500);
		renderer.setScale(defaultScale, defaultScale);

		onGlobalsChanged();
		
		localWorld.registerView(this);
		editSession.registerView(this);
	}

	private Cursor loadMouseCursor(String image, String mask, int hotspotX, int hotspotY, int fallbackCursorID) {
		URL imageUrl = null;
		try {
			imageUrl = Platform.asLocalURL(WorldPlugin.getDefault().find(new Path(image)));
			ImageData cursorZoomInImage = new ImageData(imageUrl.openConnection().getInputStream());
			imageUrl = Platform.asLocalURL(WorldPlugin.getDefault().find(new Path(mask)));
			ImageData cursorZoomInMask = new ImageData(imageUrl.openConnection().getInputStream());
			return new Cursor(Display.getCurrent(), cursorZoomInImage, cursorZoomInMask, hotspotX, hotspotY);
		} catch (Exception e) {
			return new Cursor(Display.getCurrent(), fallbackCursorID);
		}
	}

	/**
	 * @see org.eclipse.swt.events.PaintListener#paintControl(org.eclipse.swt.events.PaintEvent)
	 */
	public void paintControl(PaintEvent e) {

		GC gc;
		int offsetX, offsetY;

		if (!doubleBuffering) {
			gc = e.gc;
			gc.setClipping(e.x, e.y, e.width, e.height);
			offsetX = 0; offsetY = 0;
		} else {
			if (backBuffer == null) {
				setMaxVisibleArea(e.width, e.height);
			} else {
				Rectangle b = backBuffer.getBounds();
				if (b.width < e.width || b.height < e.height) {
					setMaxVisibleArea(Math.max(e.width, b.width), Math.max(e.height, b.height));
				}
			}
			gc = new GC(backBuffer);
			offsetX = e.x;
			offsetY = e.y;
			gc.setClipping(0, 0, e.width, e.height);
		}

		renderer.paintWorld(gc, offsetX, offsetY);
		
		if (doubleBuffering) {
			e.gc.drawImage(backBuffer, 0, 0, e.width, e.height, e.x, e.y,
					e.width, e.height);
			gc.dispose();
		}
	}

	public void setZoom(int zoomFactor) {
		renderer.setScale(defaultScale * zoomFactor / 100, defaultScale * zoomFactor
				/ 100);
		setSize(renderer.getSizeRenderedWorld());
	}

	/**
	 * @param r - the new selection rectangle. May be null.
	 */
	protected void setSelectionRectangle(Rectangle r) {
		Rectangle update = renderer.updateSelectionRectangle(r);
		if (update != null) {
			redraw(update.x, update.y, update.width + 1, update.height + 1, false);
		}
	}
	
	protected Rectangle getSelectionRectangle() {
		return renderer.getSelectionRectangle();
	}

	public int getZoom() {
		return (int) Math.round(renderer.getScaleX() / defaultScale * 50 + renderer.getScaleX()
				/ defaultScale * 50);
	}

	/**
	 * @see org.eclipse.swt.events.MouseMoveListener#mouseMove(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseMove(MouseEvent e) {
		if (getEditMode() == MODE_ZOOM) {
			mouseMoveZoomMode(e);
		} else if (getEditMode() == MODE_SELECT) {
			mouseMoveSelectMode(e);
		}
	}

	/**
	 * @param e
	 */
	protected void mouseMoveZoomMode(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	protected void mouseMoveSelectMode(MouseEvent e) {
		switch (dragging) {
		case DRAG_OBJECT :
			if (!draggedAway
					&& (Math.abs(e.x - dragPoint.x) >= 5 || Math.abs(e.y
							- dragPoint.y) >= 5)) {
				draggedAway = true;
				startDraggingSelectedObjects();
			}
			if (draggedAway) {
				dragSelectedObjectsBy(e.x - dragPoint.x, e.y - dragPoint.y);
			}
			break;
		
		case DRAG_SELECTIONRECTANGLE :
			if (!draggedAway
					&& (Math.abs(e.x - dragPoint.x) >= 5 || Math.abs(e.y
							- dragPoint.y) >= 5)) {
				draggedAway = true;
			}
			if (draggedAway) {
				Rectangle r = new Rectangle(0, 0, 0, 0);
				r.x = Math.min(dragPoint.x, e.x);
				r.y = Math.min(dragPoint.y, e.y);
				r.width = Math.max(dragPoint.x, e.x) - r.x;
				r.height = Math.max(dragPoint.y, e.y) - r.y;
				setSelectionRectangle(r);
			}
			break;
			
		}
	}

	/**
	 * @see org.eclipse.swt.events.MouseListener#mouseDoubleClick(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDoubleClick(MouseEvent e) {
	}

	/**
	 * @see org.eclipse.swt.events.MouseListener#mouseDown(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseDown(MouseEvent e) {
		if (getEditMode() == MODE_ZOOM) {
			mouseDownZoomMode(e);
		} else if (getEditMode() == MODE_SELECT) {
			mouseDownSelectMode(e);
		} else if (getEditMode() == MODE_CREATE) {
			mouseDownCreateMode(e);
		}
	}

	protected void mouseDownCreateMode(MouseEvent e) {
		if (e.button == 1) {
			Position pos = renderer.getWorldPosition(e.x, e.y);
			if (getCreateObjectType() != null && !getCreateObjectType().equals("")) {
				WorldConsole.getInstance().getGlobalData().getRemoteWorld().createObject(getCreateObjectType(), pos.toString(), null);
			}
		} else if (e.button == 2 || e.button == 3) {
			prepareContextMenu(e);
		}
	}

	protected void mouseDownZoomMode(MouseEvent e) {
		if (e.button == 1) {
			// TODO Auto-generated method stub
		} else if (e.button == 2 || e.button == 3) {
			prepareContextMenu(e);
		}
	}

	protected void mouseDownSelectMode(MouseEvent e) {
		if (e.button == 1) {
			dragPoint.x = e.x;
			dragPoint.y = e.y;

			WorldObject o = renderer.getClickedObject(e.x, e.y);
			if (o != null) {
				dragging = DRAG_OBJECT;
				draggedAway = false;

				if ((e.stateMask & SWT.CONTROL) == 0) { // no CTRL pressed
					reclickedSelectedObject = getEditSession().isSelected(o);
					if (!reclickedSelectedObject) {
						getEditSession().selectObject(o);
					}
				} else { //CTRL pressed
					if (getEditSession().isSelected(o)) {
						getEditSession().unselectObject(o);
					} else {
						getEditSession().selectObject(o, true);
					}
					reclickedSelectedObject = false;
				}
				prepareDraggingSelectedObjects();
			} else {
				dragging = DRAG_SELECTIONRECTANGLE;
				if ((e.stateMask & SWT.CONTROL) == 0) { // no CTRL pressed, so don't preserve selection
					if (!getEditSession().getSelectedObjectParts().isEmpty()) {
						getEditSession().unselectAll();
					}
				}
			}
		} else if (e.button == 2 || e.button == 3) {
			prepareContextMenu(e);
		}
	}

	private void prepareContextMenu(MouseEvent e) {
		WorldObject o = renderer.getClickedObject(e.x, e.y);
		if (o != null && !getEditSession().isSelected(o)) {
			getEditSession().selectObject(o);
		}
		getEditSession().setObjectCreatePosition(
				renderer.getWorldPosition(e.x, e.y));
	}

	/**
	 * @see org.eclipse.swt.events.MouseListener#mouseUp(org.eclipse.swt.events.MouseEvent)
	 */
	public void mouseUp(MouseEvent e) {
		if (getEditMode() == MODE_ZOOM) {
			mouseUpZoomMode(e);
		} else if (getEditMode() == MODE_SELECT){
			mouseUpSelectMode(e);
		}
	}

	/**
	 * @param e
	 */
	protected void mouseUpZoomMode(MouseEvent e) {
		if ((e.stateMask & SWT.SHIFT) == 0) {
			setZoom((int) (getZoom()*1.2));
			if (worldScroller != null) {
				Point offset = worldScroller.getScrollOffset();
				worldScroller.setScrollOffset((int) (offset.x*1.2 + (e.x - offset.x)*0.2), (int) (offset.y*1.2 + (e.y - offset.y)*0.2));
			}
		} else {
			if (worldScroller != null) {
				Point offset = worldScroller.getScrollOffset();
				worldScroller.setScrollOffset((int) (offset.x/1.2 + (e.x - offset.x)*(1/1.2 - 1)), (int) (offset.y/1.2 + (e.y - offset.y)*(1/1.2 - 1)));
			}
			setZoom((int) (getZoom()/1.2));
		}
	}

	protected void mouseUpSelectMode(MouseEvent e) {
		if (e.button == 1) {
			switch (dragging) {
			case DRAG_OBJECT :
				if (draggedAway) {
					confirmDraggingSelectedObjects(e.x - dragPoint.x, e.y - dragPoint.y);
				} else {
					if (reclickedSelectedObject) {
						WorldObject o = renderer.getClickedObjectUnderSelected(e.x, e.y);
						if (o != null && !getEditSession().isSelected(o)) {
							getEditSession().selectObject(o);
						}
					}
				}
				break;
			case DRAG_SELECTIONRECTANGLE :
				getEditSession().selectObjects(renderer.getObjectsInSelectionRectangle(), true);
				setSelectionRectangle(null);
				break;
			}
			dragging = DRAG_NO;
		}
	}

	public void dispose() {
		super.dispose();
		renderer.dispose();
		selectCursor.dispose();
		createCursor.dispose();
		zoomInCursor.dispose();
		zoomOutCursor.dispose();
		if (backBuffer != null) {
			backBuffer.dispose();
		}
	}
	
	/**
	 * @see org.eclipse.swt.events.ControlListener#controlMoved(org.eclipse.swt.events.ControlEvent)
	 */
	public void controlMoved(ControlEvent e) {
	}

	/**
	 * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
	 */
	public void controlResized(ControlEvent e) {
	}
	
	/**Sets the extends of the maximal area to be painted at once. This is only a hint, if you specify
	 * the area to small or not at all, it will be corrected while painting.
	 * @param width - maximal width visible
	 * @param height - maximal height visible
	 */
	public void setMaxVisibleArea(int width, int height) {
		if (doubleBuffering) {
			if (backBuffer != null) {
				backBuffer.dispose();
			}
			if (width > 0 && height > 0) {
				backBuffer = new Image(getDisplay(), new Rectangle(0, 0, width, height));
			} else {
				backBuffer = null;
			}
		}
	}

	public void onGlobalsChanged() {
		renderer.setWorldArea(getLocalWorld().getWorldModel().getVisibleArea());
		renderer.setGroundmapArea(getLocalWorld().getWorldModel().getGroundmapArea());
		setSize(renderer.getSizeRenderedWorld());
		redraw();
	}

	/* (non-Javadoc)
	 * @see org.micropsi.eclipse.worldconsole.ILocalWorldListener#OnObjChanged(org.micropsi.eclipse.worldconsole.LocalWorld, org.micropsi.comp.console.worldconsole.model.WorldObject)
	 */
	public void onObjectChanged(LocalWorld mgr, WorldObject changedObject) {
		if (isDisposed() || lockedObjects.contains(changedObject)) {
			return;
		}
		Rectangle updateRegion = renderer.updateObject(changedObject);
		if (updateRegion != null) {
			redraw(updateRegion.x, updateRegion.y, updateRegion.width + 1, updateRegion.height + 1, false);
		}
	}

	/* (non-Javadoc)
	 * @see org.micropsi.eclipse.worldconsole.ILocalWorldListener#OnMultipleObjectsChanged(org.micropsi.eclipse.worldconsole.LocalWorld, java.util.List)
	 */
	public void onMultipleObjectsChanged(LocalWorld mgr, Collection<AbstractWorldObject> changedObjects) {
		if (isDisposed()) {
			return;
		}
		if (changedObjects.size() <= 3) {
			Iterator<AbstractWorldObject> it = changedObjects.iterator();
			while (it.hasNext()) {
				onObjectChanged(mgr, (WorldObject) it.next());
			}
			return;
		}
		Collection<AbstractWorldObject> updateObjects = new ArrayList<AbstractWorldObject>(changedObjects.size());
		for (Iterator<AbstractWorldObject> it = changedObjects.iterator(); it.hasNext(); ) {
			AbstractWorldObject obj = it.next();
			if (!lockedObjects.contains(obj)) {
				updateObjects.add(obj);
			}
		}
		Rectangle updateRegion = renderer.updateMultipleObjects(updateObjects);
		if (updateRegion != null) {
			redraw(updateRegion.x, updateRegion.y, updateRegion.width + 1, updateRegion.height + 1, false);
		}
	}

	/* (non-Javadoc)
	 * @see org.micropsi.eclipse.worldconsole.ILocalWorldListener#OnObjListRefreshed(org.micropsi.eclipse.worldconsole.LocalWorld)
	 */
	public void onObjectListRefreshed(LocalWorld mrg) {
		if (isDisposed()) {
			return;
		}
		renderer.updateAllObjects();
		redraw();
	}

	/* (non-Javadoc)
	 * @see org.micropsi.eclipse.worldconsole.ILocalWorldListener#OnSelectionChanged(org.micropsi.eclipse.worldconsole.LocalWorld)
	 */
	public void onSelectionChanged(EditSession session, Collection changeList) {
		if (isDisposed()) {
			return;
		}
		Rectangle update = null;
		for (Iterator it = changeList.iterator(); it.hasNext(); ) {
			WorldObject obj = ((AbstractWorldObject) it.next()).getRootObject();
			Rectangle r = renderer.updateSelectionState(obj);
			if (r != null) {
				if (update == null) {
					update = r;
				} else {
					update = update.union(r);
				}
			}
		}
		if (update != null) {
			redraw(update.x, update.y, update.width, update.height, true);
		}
	}

	/* (non-Javadoc)
	 * @see org.micropsi.eclipse.console.controller.IViewControllerListener#setDataBase(java.lang.Object)
	 */
	public void setDataBase(Object o) {
	}

	/**
	 * @return Returns the localWorld.
	 */
	protected LocalWorld getLocalWorld() {
		return localWorld;
	}
	
	protected void prepareDraggingSelectedObjects() {
		for (Iterator it = renderer.getSelectedObjectPainters().iterator(); it.hasNext();) {
			WorldRenderer.ObjectPainter objPainter = (WorldRenderer.ObjectPainter) it.next();
			renderer.prepareDragging(objPainter);
		}
	}
	
	protected void startDraggingSelectedObjects() {
		Rectangle update = null;
		for (Iterator<ObjectPainter> it = renderer.getSelectedObjectPainters().iterator(); it.hasNext();) {
			WorldRenderer.ObjectPainter objPainter = it.next();
			lockedObjects.add(objPainter.originalObject);
			Rectangle r = renderer.startDragging(objPainter);
			if (r != null) {
				if (update == null) {
					update = r;
				} else {
					update = update.union(r);
				}
			}
		}
		if (update != null) {
			redraw(update.x, update.y, update.width, update.height, false);
		}
	}

	protected void confirmDraggingSelectedObjects(int x, int y) {
		for (Iterator it = renderer.getSelectedObjectPainters().iterator(); it.hasNext();) {
			WorldRenderer.ObjectPainter objPainter = (WorldRenderer.ObjectPainter) it.next();
			int requestId = localWorld.requestMoveObject(objPainter.originalObject, renderer.getDragPosition(objPainter, x, y), this);
			lockedObjects.add(objPainter.originalObject);
			lockedObjectInfo.put(new Long(objPainter.originalObject.getId()), new LockedObjectInfo(objPainter.originalObject, requestId));
						
			renderer.endDragging(objPainter);
		}
	}
	
	protected void dragSelectedObjectsBy(int x, int y) {
		Rectangle update = null;
		for (Iterator it = renderer.getSelectedObjectPainters().iterator(); it.hasNext();) {
			WorldRenderer.ObjectPainter objPainter = (WorldRenderer.ObjectPainter) it.next();
			Rectangle r = renderer.dragObjectPainterBy(objPainter, x, y);
			if (r != null) {
				if (update == null) {
					update = r;
				} else {
					update = update.union(r);
				}
			}
		}
		if (update != null) {
			redraw(update.x, update.y, update.width, update.height, true);
		}
	}
	
	
	/**
	 * @return Returns the editSession.
	 */
	public EditSession getEditSession() {
		return editSession;
	}

	/* @see org.micropsi.eclipse.worldconsole.IRequestResultHandler#handleRequestResult(int, org.micropsi.common.consoleservice.AnswerIF)*/
	public void handleRequestResult(int requestId, AnswerIF answer) {
		if (answer.getAnsweredQuestion().getQuestionName().equals("changeobjectproperties") && answer.getAnswerType() == AnswerTypesIF.ANSWER_TYPE_OK && answer.getAnsweredQuestion().getParameters().length >= 2) {
			Long objId = new Long(answer.getAnsweredQuestion().getParameters()[0]);
			LockedObjectInfo objInfo = lockedObjectInfo.get(objId);
			if (requestId == objInfo.requestId) {
				lockedObjects.remove(objInfo.object);
				lockedObjectInfo.remove(objId);
				Rectangle r = renderer.updateObject(objInfo.object, true);
				redraw(r.x, r.y, r.width, r.height, false);
			}
		}
	}
	
	/**
	 * @return Returns the editMode.
	 */
	public int getEditMode() {
		return editMode;
	}
	
	/**
	 * @param editMode The editMode to set.
	 */
	public void setEditMode(int editMode) {
		this.editMode = editMode;
		switch (editMode) {
		case MODE_SELECT :
			setCursor(selectCursor);
			break;
		case MODE_CREATE :
			setCursor(createCursor);
			break;
		case MODE_ZOOM :
			setCursor(zoomInCursor);
			break;
		}
	}
	/**
	 * @return Returns the createObjectType.
	 */
	public String getCreateObjectType() {
		return createObjectType;
	}
	/**
	 * @param createObjectType The createObjectType to set.
	 */
	public void setCreateObjectType(String createObjectType) {
		this.createObjectType = createObjectType;
	}
	
	/**
	 * @param worldScroller The worldScroller to set.
	 */
	public void setWorldScroller(IWorldWidgetScroller worldScroller) {
		this.worldScroller = worldScroller;
	}

	public void redraw(int x, int y, int width, int height) {
		redraw(x, y, width, height, true);
		
	}

	/**
	 * @return Returns the renderer.
	 */
	public WorldRenderer getRenderer() {
		return renderer;
	}

	/* @see org.micropsi.comp.console.worldconsole.WorldRenderer#isOverlayEnabled(java.lang.String)*/
	public boolean isOverlayEnabled(String name) {
		return renderer.isOverlayEnabled(name);
	}

	/* @see org.micropsi.comp.console.worldconsole.WorldRenderer#setOverlayEnabled(java.lang.String, boolean)*/
	public boolean setOverlayEnabled(String name, boolean enabled) {
		boolean res = renderer.setOverlayEnabled(name, enabled);
		if (res) {
			redraw();
		}
		return res;
	}
}