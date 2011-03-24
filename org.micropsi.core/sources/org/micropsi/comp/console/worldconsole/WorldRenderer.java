/*
 * Created on 11.04.2005
 *
 */

package org.micropsi.comp.console.worldconsole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.micropsi.common.coordinates.Area2D;
import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.console.worldconsole.model.AbstractWorldObject;
import org.micropsi.comp.console.worldconsole.model.ObjectVisualInfo;
import org.micropsi.comp.console.worldconsole.model.WorldObject;

/**This class represents all information needed to render a 2D view of the world at a sprecific
 * resolution. It caches resolution-dependent information and can update parts of it with given
 * world model data.
 * It's main service is to render a 2d view of the world to a given graphics context.
 * Objects of this class have to be disposed after usage using <code>dispose()</code>.
 * 
 * @author Matthias
 *
 */
public class WorldRenderer implements IRenderInfo {
	protected static final double baseScale = 15;
	private double scaleX, scaleY; // scale world -> graphics coordinates
	
	private Position worldLowestCoords;
	private Position worldHighestCoords;

	private Position groundMapLowestCoords;
	private Position groundMapHighestCoords;
	
	private Color bkIslandColor;
	private Color bkOceanColor;
	private Color selFrameColor;
	private Color objectTextColor = null;
	
	private Font objectTextFont = null;

	private Map<Long,ObjectPainter> objectPainters = null;
	private SortedSet<ObjectPainter> cachedSortedObjectPainters = null;
	private Set<ObjectPainter> selectedObjectPainters = new HashSet<ObjectPainter>();
	private Rectangle selectionRectangle = null;
	
	protected Map<String,Image> scaledImageCache = null;

	private LocalWorld localWorld = null;
	private EditSession editSession = null;
	
	private List<OverlayInfo> overlayInfoList = null;
	
	public class OverlayInfo {
		private String name;
		private IOverlayRenderer renderer;
		private boolean enabled;
		private int zOrder;
		public OverlayInfo(OverlayRendererDescriptor rendererDescriptor) {
			name = rendererDescriptor.getName();
			renderer = rendererDescriptor.getRenderObject();
			enabled = rendererDescriptor.isEnabled();
			zOrder = rendererDescriptor.getZOrder();
		}
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
			getRenderer().setEnabled(enabled);
		}
		public boolean isEnabled() {
			return enabled;
		}
		public String getName() {
			return name;
		}
		public IOverlayRenderer getRenderer() {
			return renderer;
		}
		public int getZOrder() {
			return zOrder;
		}
	}
	
	private class DragOrigin {
		public Position worldPosition; 
		public Point iconRectPos;
		public Point displayRectPos;
		public Point painterPos;
		
		public DragOrigin(ObjectPainter painter) {
			worldPosition = painter.originalPosition;
			iconRectPos = new Point(painter.getImageRectangle().x, painter.getImageRectangle().y);
			displayRectPos = new Point(painter.getDisplayRectangle().x, painter.getDisplayRectangle().y);
			painterPos = new Point(painter.painterPos.x, painter.painterPos.y);
		}
	}

	public class ObjectPainter implements Comparable {
		public WorldObject originalObject = null;
		
		protected Position originalPosition;
		protected Point painterPos = new Point(0, 0);
		
		protected DragOrigin dragOrigin = null;

		public boolean selected = false;
		public boolean subobjectSelected = false;
		
		private Image image = null;
		protected boolean imageModified = false;
		protected String stateDescription = null;
		protected Point stateDescriptionExtent = null;
		private Rectangle imageRectangle = null;
		private Rectangle displayRectangle = null;
		
		public ObjectPainter(WorldObject obj) {
			setData(obj, true);
		}
		
		public void update(WorldObject obj, boolean forced) {
			setData(obj, !forced);
		}
		
		protected void prepareDragging() {
			dragOrigin = new DragOrigin(this);
		}
		
		protected Rectangle startDragging() {
			ImageData imageData = getImage().getImageData();
			imageData.alpha = 80;
			if (imageModified) {
				getImage().dispose();
			}
			image = new Image(Display.getDefault(), imageData);
			imageModified = true;
			return getDisplayRectangle();
		}
		
		protected void endDragging() {
			dragOrigin = null;
		}
		
		protected Rectangle dragBy(int x, int y) {
			Rectangle r = getDisplayRectangle();
			Rectangle modified = new Rectangle(r.x, r.y, r.width, r.height);
			displayRectangle.x = dragOrigin.displayRectPos.x + x;
			displayRectangle.y = dragOrigin.displayRectPos.y + y;
			imageRectangle.x = dragOrigin.iconRectPos.x + x;
			imageRectangle.y = dragOrigin.iconRectPos.y + y;
			painterPos.x = dragOrigin.painterPos.x + x;
			painterPos.y = dragOrigin.painterPos.y + y;
			modified.add(getDisplayRectangle());
			return modified;
		}
		
		private void setData(WorldObject obj, boolean ignoreChangeType) {
			originalObject = obj;
			originalPosition = obj.getPosition();
			updateSelectionState();
			if (ignoreChangeType || (obj.changeType & WorldObject.CT_CHANGE_STATE) != 0) {
				updateStateDescription();
				updateImage();
				updateImageRectangle();
				updateDisplayRectangle();
			}
			if (ignoreChangeType
					|| ((obj.changeType & WorldObject.CT_CHANGE_POSITION) != 0)
					|| (obj.changeType & WorldObject.CT_CHANGE_STATE) != 0) {
				updatePainterPos();
				updateImageRectangle();
				updateDisplayRectangle();
			}
			updateSelectionState();
		}

		public Rectangle updateSelectionState() {
			if (getEditSession() != null) {
				selected = getEditSession().isSelected(originalObject);
				subobjectSelected = getEditSession().hasSelectedPart(originalObject);
			} else {
				selected = false;
				subobjectSelected = false;
			}
			return getDisplayRectangle();
		}
		
		public Rectangle getDisplayRectangle() {
			return displayRectangle;
		}
		
		private void updateDisplayRectangle() {
			Rectangle r = getImageRectangle();
			r = new Rectangle(r.x, r.y, r.width, r.height);
			r.x--;
			r.y -= stateDescriptionExtent.y + 1;
			r.height += stateDescriptionExtent.y + 3;
			r.width += 3;
			if (r.width < stateDescriptionExtent.x) {
				r.x -= (stateDescriptionExtent.x - r.width) / 2;
				r.width = stateDescriptionExtent.x;
			}
			if (painterPos.x - r.x < 5) {
				int padding = 5 - (painterPos.x - r.x); 
				r.x -= padding;
				r.width += padding;
			}
			if (painterPos.y - r.y < 5) {
				int padding = 5- (painterPos.x - r.y);
				r.y -= padding;
				r.height += padding;
			}
			if (r.x + r.width - painterPos.x < 5) {
				r.width += 5 - (r.x + r.width - painterPos.x);
			}
			if (r.y + r.height - painterPos.y < 5) {
				r.height += 5 - (r.y + r.height - painterPos.y);
			}
			displayRectangle = r;
		};

		private void updateImageRectangle() {
			Rectangle r = originalObject.getVisualInfo().getImageBounds();
			double fakX = getScaleX() / baseScale;
			double fakY = getScaleY() / baseScale;

			r.x = (int) Math.round(painterPos.x - originalObject.getVisualInfo().imageCenter.x*fakX);
			r.y = (int) Math.round(painterPos.y - originalObject.getVisualInfo().imageCenter.y*fakY);
			r.width = (int) Math.round(r.width*fakX);
			r.height = (int) Math.round(r.height*fakY);

			imageRectangle = r;
		}
		
		private void updatePainterPos() {
			painterPos.x = getScreenX(originalObject.getPosition());
			painterPos.y = getScreenY(originalObject.getPosition());
		}
		
		private void updateImage() {
			if (imageModified) {
				image.dispose();
			}
			image = null;
		}
		
		public Rectangle getImageRectangle() {
			return imageRectangle;
		}

		private void updateStateDescription() {
			stateDescription = originalObject.getStateDescription();
			if (stateDescription != null && stateDescription != "") {
				Image img = new Image(Display.getDefault(), 1, 1);
				GC gc = new GC(img);
				ensureObjectTextFontIsThere();
				gc.setFont(objectTextFont);
				stateDescriptionExtent = gc.textExtent(stateDescription);
				gc.dispose();
				img.dispose();
			} else {
				stateDescriptionExtent = new Point(0, 0);
			}
		}

		private void paintObject(GC gc, int offsetX, int offsetY) {
			if (getImage() != null) {
				Rectangle r = getImageRectangle();
				gc.drawImage(getImage(), r.x - offsetX, r.y - offsetY);
			}
			if (selected) {
				drawSelectionFrame(gc, offsetX, offsetY);
			} else if (subobjectSelected) {
				drawPartSelectionFrame(gc, offsetX, offsetY);
			}
			
			Rectangle r = getDisplayRectangle();
			gc.setForeground(objectTextColor);
			ensureObjectTextFontIsThere();
			gc.setFont(objectTextFont);
			gc.drawText(originalObject.getStateDescription(), r.x - offsetX, r.y - offsetY, true);
		}

		/**
		 * Draws a frame around an selected object
		 */
		private void drawSelectionFrame(GC gc, int offsetX, int offsetY) {
			gc.setForeground(selFrameColor);
			Rectangle r = getImageRectangle();
			gc.setLineWidth(2);
			gc.setLineStyle(SWT.LINE_DASH);
			gc.drawRectangle(r.x - offsetX, r.y - offsetY, r.width, r.height);
			gc.setForeground(objectTextColor);
			gc.setLineStyle(SWT.LINE_SOLID);
			int x = painterPos.x - offsetX;
			int y = painterPos.y - offsetY;
			gc.drawLine(x - 5, y - 5, x + 5, y + 5);
			gc.drawLine(x - 5, y + 5, x + 5, y - 5);
		}

		/**
		 * Draws a frame around an object that has selected object parts
		 */
		private void drawPartSelectionFrame(GC gc, int offsetX, int offsetY) {
			gc.setForeground(selFrameColor);
			Rectangle r = getImageRectangle();
			gc.setLineWidth(2);
			gc.setLineStyle(SWT.LINE_DOT);
			gc.drawRectangle(r.x - offsetX, r.y - offsetY, r.width, r.height);
		}

		/* @see java.lang.Comparable#compareTo(java.lang.Object)*/
		public int compareTo(Object arg0) {
			if (arg0 instanceof ObjectPainter) {
				ObjectPainter objPainter = (ObjectPainter) arg0;
				if (selected != objPainter.selected) {
					return objPainter.selected? -1 : 1;
				}
				return compareToIgnoreSelection(objPainter);
			}
			return arg0.hashCode() - hashCode();
		}
		
		public int compareToIgnoreSelection(ObjectPainter objPainter) {
			if (originalPosition.getZ() != objPainter.originalPosition.getZ()) {
				return originalPosition.getZ() < objPainter.originalPosition.getZ() ? -1 : 1;
			} else if (originalPosition.getY() != objPainter.originalPosition.getY()) {
				return originalPosition.getY() > objPainter.originalPosition.getY() ? -1 : 1;
			}
			return objPainter.hashCode() - hashCode();
		}
		
		public void dispose() {
			if (image != null && imageModified) {
				image.dispose();
			}
		}

		protected Image getImage() {
			if (image == null) {
				image = retrieveImage();
			}
			return image;
		}
		
		private Image retrieveImage() {
			imageModified = false;
			ObjectVisualInfo visualInfo = originalObject.getVisualInfo();
			Image img = scaledImageCache.get(visualInfo.imageKey);
			if (img != null) {
				return img;
			}
			Rectangle r = visualInfo.getImageBounds();
			if (r.width == getImageRectangle().width && r.height == getImageRectangle().height) {
				return visualInfo.image;
			}
			img = new Image(Display.getDefault(), visualInfo.image.getImageData().scaledTo(getImageRectangle().width, getImageRectangle().height));
			scaledImageCache.put(visualInfo.imageKey, img);
			return img;
		}
		
	}
	
	
	
	/**
	 * Creates a renderer that uses a given world model and - if not null - the
	 * given edit session.
	 * <b>This object creates SWT resources and therefore has to be disposed after usage
	 * using <code>dispose()</code></b>. 
	 * @param worldController - the world model to use.
	 * @param editSession - an edit session that will be used. May be null.
	 * @see LocalWorld, @see EditSession
	 */
	public WorldRenderer(LocalWorld worldController, EditSession editSession) {
		this(worldController, editSession, null);
	}
	
	/**
	 * Creates a renderer that uses a given world model and - if not null - the
	 * given edit session and an object to request redraw. Redraw is only requested by overlays, otherwise
	 * it has to be managed by the render target itself.
	 * <b>This object creates SWT resources and therefore has to be disposed after usage
	 * using <code>dispose()</code></b>. 
	 * @param worldController - the world model to use.
	 * @param editSession - an edit session that will be used. May be null.
	 * @param changeNotifier - instance for overlays to request redraw of the render target. May be null.
	 * @see LocalWorld, @see EditSession
	 */
	public WorldRenderer(LocalWorld worldController, EditSession editSession, IOverlayChangeNotifier changeNotifier) {
		localWorld = worldController;
		this.editSession = editSession;
		worldLowestCoords = new Position(0, 0, 0);
		worldHighestCoords = new Position(100, 100, 0);
		groundMapLowestCoords = null;
		groundMapHighestCoords = null;
		scaleX = 15; scaleY = 15;

		bkIslandColor = new Color(Display.getDefault(), 50, 160, 20);
		bkOceanColor = new Color(Display.getDefault(), 0, 20, 200);
		selFrameColor = new Color(Display.getDefault(), 255, 0, 0);
		objectTextColor = new Color(Display.getDefault(), 0, 0, 0);
		
		scaledImageCache = new HashMap<String,Image>();
				
		initOverlayRenderer(worldController, editSession, changeNotifier);
	}
	
	private void ensureObjectTextFontIsThere() {
		if(objectTextFont == null) {
			
			//TODO: I'm still getting invalid thread access exceptions from this
			// It's of no use anyway as only getting the default font is causing the
			// problems and the default font is, well, default. You don't have to set it.
			
//			FontData fd = Display.getDefault().getSystemFont().getFontData()[0];
//			objectTextFont = new Font(Display.getDefault(), fd);
		}	
	}
	
	protected void initOverlayRenderer(LocalWorld world, EditSession editSession, IOverlayChangeNotifier changeNotifier) {
		List renderDescriptors = world.getImageLibrary().getOverlayRenderDescriptors();
		overlayInfoList = new ArrayList<OverlayInfo>(renderDescriptors.size());
		for (Iterator it = renderDescriptors.iterator(); it.hasNext(); ) {
			OverlayRendererDescriptor renderer = (OverlayRendererDescriptor) it.next();
			OverlayInfo renderInfo = new OverlayInfo(renderer);
			if (renderInfo.getRenderer() != null) {
				renderInfo.getRenderer().init(world, editSession, changeNotifier, this);
				renderInfo.getRenderer().setEnabled(renderInfo.isEnabled());
				overlayInfoList.add(renderInfo);
			}
		}
	}

	/**
	 * 
	 */
	public void updateAllObjects() {
		objectPainters = new HashMap<Long,ObjectPainter>((int) (getLocalWorld().getObjects().size()*1.2));
		selectedObjectPainters.clear();
		Iterator it = getLocalWorld().getObjects().iterator();
		while (it.hasNext()) {
			WorldObject obj = (WorldObject) it.next();
			ObjectPainter objectPainter = new ObjectPainter(obj);
			objectPainters.put(new Long(obj.getId()), objectPainter);
			if (objectPainter.selected) {
				selectedObjectPainters.add(objectPainter);
			}
		}
		resetObjectPaintersSort();
	}
	
	/**Creates or updates resolution specific data for a given world object. Calculates the image area
	 * that needs to be updated to reflect this change.
	 * 
	 * @param changedObject - the world object whose data should be updated
	 * @return a rectangle describing the image area that needs to be updated. Null if no update is necessary.
	 */
	public Rectangle updateObject(WorldObject changedObject) {
		return updateObject(changedObject, false);
	}
	
	public Rectangle updateObject(WorldObject changedObject, boolean forceRefresh) {
		if (objectPainters == null) {
			return null;
		}
		Long id = new Long(changedObject.getId());
		ObjectPainter objPainter = getObjectPainters().get(id);
		Rectangle updateRegion = null;
		if (objPainter != null) {
			updateRegion = objPainter.getDisplayRectangle();
			if (cachedSortedObjectPainters != null) {
				cachedSortedObjectPainters.remove(objPainter); // reinserted later - needs possibly to be reordered
			}
		}
		if (objPainter != null && objPainter.selected) {
			getSelectedObjectPainters().remove(objPainter);
		}
		if (changedObject.isRemoved()) {
			getObjectPainters().remove(id);
		} else {
			if (objPainter == null) {
				objPainter = new ObjectPainter(changedObject);
				getObjectPainters().put(id, objPainter);
			} else {
				objPainter.update(changedObject, forceRefresh);
			}
			if (updateRegion == null) {
				updateRegion = objPainter.getDisplayRectangle();
			} else {
				updateRegion = updateRegion.union(objPainter.getDisplayRectangle());
			}
			if (cachedSortedObjectPainters != null) {
				cachedSortedObjectPainters.add(objPainter);
			}
			if (objPainter.selected) {
				getSelectedObjectPainters().add(objPainter);
			}
		}
		return updateRegion;
	}
	
	public Rectangle updateSelectionState(WorldObject obj) {
		if (objectPainters == null) {
			return null;
		}
		ObjectPainter objPainter = getObjectPainters().get(new Long(obj.getId()));
		if (objPainter == null) {
			return null;
		}
		if (cachedSortedObjectPainters != null) {
			cachedSortedObjectPainters.remove(objPainter);
		}
		if (objPainter.selected) {
			getSelectedObjectPainters().remove(objPainter);
		}
		Rectangle res = objPainter.updateSelectionState();
		if (objPainter.selected) {
			getSelectedObjectPainters().add(objPainter);
		}
		if (cachedSortedObjectPainters != null) {
			cachedSortedObjectPainters.add(objPainter);
		}
		return res;
	}
	
	/**Creates or updates resolution specific data for a collection of given world objects.
	 * Calculates the image area that needs to be updated to reflect this change.
	 * 
	 * @param changedObjects - the world objects whose data should be updated
	 * @return a rectangle describing the image area that needs to be updated. Null if no update is necessary.
	 */
	public Rectangle updateMultipleObjects(Collection changedObjects) {
		if (objectPainters == null || changedObjects.size() == 0) {
			return null;
		}
		Iterator it = changedObjects.iterator();
		Rectangle updateRegion = null;
		while (it.hasNext()) {
			AbstractWorldObject obj = (AbstractWorldObject) it.next();
			if (!(obj instanceof WorldObject)) {
				continue; // ignore subobject changes
			}
			WorldObject changedObject = (WorldObject) obj;
			Long id = new Long(changedObject.getId());
			ObjectPainter objPainter = getObjectPainters().get(id);
			if (objPainter != null) {
				if (updateRegion == null) {
					updateRegion = objPainter.getDisplayRectangle();
				} else {
					updateRegion = updateRegion.union(objPainter.getDisplayRectangle());
				}
				if (cachedSortedObjectPainters != null) {
					cachedSortedObjectPainters.remove(objPainter); // reinserted later - needs possibly be reordered
				}
			}
			if (objPainter != null && objPainter.selected) {
				getSelectedObjectPainters().remove(objPainter);
			}
			if (changedObject.isRemoved()) {
				objectPainters.remove(id);
			} else {
				if (objPainter == null) {
					objPainter = new ObjectPainter(changedObject);
					objectPainters.put(id, objPainter);
				} else {
					objPainter.update(changedObject, false);
				}
				if (updateRegion == null) {
					updateRegion = objPainter.getDisplayRectangle();
				} else {
					updateRegion = updateRegion.union(objPainter.getDisplayRectangle());
				}
				if (cachedSortedObjectPainters != null) {
					cachedSortedObjectPainters.add(objPainter);
				}
				if (objPainter.selected) {
					getSelectedObjectPainters().add(objPainter);
				}
			}
		}
		return updateRegion;
	}
	
	/**Convenience method.
	 * Paints current world on given GC, placing Position pos to the center and using
	 * gc.getClipping() to determin the requested image size.
	 * 
	 * @param gc - target GC
	 * @param pos - Position to be placed in the center of the image
	 */
	public void paintWorld(GC gc, Position pos) {
		int offsetX = getScreenX(pos) - gc.getClipping().width / 2 + gc.getClipping().x;
		int offsetY = getScreenY(pos) - gc.getClipping().height / 2 + gc.getClipping().y;
		paintWorld(gc, offsetX, offsetY);
	}

	/**Convenience method.
	 * Paints current world on given GC, placing object with id objectId to the center and using
	 * gc.getClipping() to determin the requested image size. If no object with the given id exists,
	 * it will put the world center to the image center
	 * 
	 * @param gc - target GC
	 * @param objectId - id of object to be placed in the center
	 */
	public void paintWorld(GC gc, long objectId) {
		ObjectPainter objPainter = getObjectPainters().get(new Long(objectId));
		int posX, posY;
		if (objPainter != null) {
			posX = objPainter.getDisplayRectangle().x + objPainter.getDisplayRectangle().width / 2;
			posY = objPainter.getDisplayRectangle().y + objPainter.getDisplayRectangle().height / 2;
		} else {
			Position pos = new Position(
					(getWorldHighestCoords().getX() + getWorldLowestCoords().getX()) / 2,
					(getWorldHighestCoords().getY() + getWorldLowestCoords().getY()) / 2);
			posX = getScreenX(pos);
			posY = getScreenY(pos);
		}
		paintWorld(gc, posX - gc.getClipping().width / 2 + gc.getClipping().x,
				posY - gc.getClipping().height / 2 + gc.getClipping().y);
	}

	/**Paints part of the current world on the given GC. The 'screen' coordinates (offsetX, offsetY)
	 * will be placed in the upper left corner of the GC's clipping, the size of the painted part is
	 * determined by the size of the GC's clipping.
	 * 
	 * @param gc - GC to paint on
	 * @param offsetX - x 'screen' coordinate to be placed on the left border of the painted part
	 * @param offsetY - y 'screen' coordinate to be placed on the top border of the painted part
	 */
	public void paintWorld(GC gc, int offsetX, int offsetY) {
		paintBackground(gc, offsetX, offsetY);
		Iterator overlayIt = getOverlayInfoList().iterator();
		OverlayInfo renderInfo = null;
		while (overlayIt.hasNext()) {
			renderInfo = (OverlayInfo) overlayIt.next();
			if (renderInfo.getZOrder() >= 100) {
				break;
				// z-index >= 100 will be painted *over* the objects
			}
			paintOverlay(gc, offsetX, offsetY, renderInfo);
		}
		if (getSelectionRectangle() != null) {
			paintSelectionRectangle(gc, offsetX, offsetY);
		}
		paintObjects(gc, offsetX, offsetY);
		if (renderInfo != null && renderInfo.getZOrder() >= 100) {
			paintOverlay(gc, offsetX, offsetY, renderInfo);
			while (overlayIt.hasNext()) {
				renderInfo = (OverlayInfo) overlayIt.next();
				paintOverlay(gc, offsetX, offsetY, renderInfo);
			}
		}
	}

	protected void paintOverlay(GC gc, int offsetX, int offsetY, OverlayInfo renderInfo) {
		if (renderInfo.enabled) {
			renderInfo.getRenderer().paintOverlay(gc, offsetX, offsetY, this);
		}
	}

	protected void paintObjects(GC gc, int offsetX, int offsetY) {
		gc.setForeground(objectTextColor);
		Collection painters = getObjectPaintersSorted();
		Iterator it = painters.iterator();
		while (it.hasNext()) {
			ObjectPainter objPainter = (ObjectPainter) it.next();
			if (objPainter.getDisplayRectangle().intersects(
					gc.getClipping().x + offsetX, gc.getClipping().y + offsetY,
					gc.getClipping().width, gc.getClipping().height)) {
				objPainter.paintObject(gc, offsetX, offsetY);
			}
		}
	}

	/**
	 * @param gc
	 */
	protected void paintBackground(GC gc, int offsetX, int offsetY) {
		Image groundImage = localWorld.getGroundImage();
		Rectangle virtualTarget = null;
		if (groundImage != null) {
			virtualTarget = new Rectangle(
					getScreenX(groundMapLowestCoords) - offsetX,
					getScreenY(groundMapHighestCoords) - offsetY,
					getScreenX(groundMapHighestCoords) - offsetX + 1,
					getScreenY(groundMapLowestCoords) - offsetY + 1);
			virtualTarget.width -= virtualTarget.x;
			virtualTarget.height -= virtualTarget.y;
		}
		Rectangle toFill = gc.getClipping();
		if (groundImage == null
				|| toFill.x < virtualTarget.x
				|| toFill.y < virtualTarget.y
				|| toFill.x + toFill.width > virtualTarget.x + virtualTarget.y
				|| toFill.y + toFill.height > virtualTarget.y
						+ virtualTarget.height) {
			gc.setBackground(bkOceanColor);
			gc.fillRectangle(toFill);
		}
		if (groundImage != null) {
			Rectangle realTarget = gc.getClipping().intersection(virtualTarget);
			if (!realTarget.isEmpty()) {
				float scaleX = virtualTarget.width / (float) groundImage.getBounds().width;
				float scaleY = virtualTarget.height / (float) groundImage.getBounds().height;
				Rectangle source = new Rectangle(
						(int) Math.floor((realTarget.x - virtualTarget.x)/scaleX),
						(int) Math.floor((realTarget.y - virtualTarget.y)/scaleY),
						groundImage.getBounds().width + (int) Math.ceil((realTarget.width - virtualTarget.width)/scaleX) + 1,
						groundImage.getBounds().height + (int) Math.ceil((realTarget.height - virtualTarget.height)/scaleY) + 1);
				source = source.intersection(groundImage.getBounds());
				realTarget.x = virtualTarget.x + Math.round(source.x*scaleX);
				realTarget.y = virtualTarget.y + Math.round(source.y*scaleY);
				realTarget.width = Math.round(source.width*scaleX);
				realTarget.height = Math.round(source.height*scaleY);
				gc.drawImage(groundImage, source.x, source.y, source.width, source.height,
						realTarget.x, realTarget.y, realTarget.width, realTarget.height);
			}
		} else {
			gc.setForeground(objectTextColor);
			ensureObjectTextFontIsThere();
			gc.setFont(objectTextFont);
			if (getLocalWorld().getGroundImageFileName() != null) {
				gc.drawText("Groundmap image '" + getLocalWorld().getGroundImageFileName() + "' not found.", -offsetX, -offsetY);
			} else {
				gc.drawText("No groundmap image file known.", -offsetX, -offsetY);
			}
		}
	}
	
	protected void paintSelectionRectangle(GC gc, int offsetX, int offsetY) {
		Rectangle r = getSelectionRectangle();
		if (r != null) {
			gc.drawRectangle(r.x - offsetX, r.y - offsetY, r.width, r.height);
		}
	}

	public void setScale(double sX, double sY) {
		scaleX = sX;
		scaleY = sY;
		for (Iterator it = getOverlayInfoList().iterator(); it.hasNext(); ) {
			OverlayInfo renderInfo = (OverlayInfo) it.next();
			renderInfo.getRenderer().setScale(sX, sY);
		}
		clearImageCache();
		if (objectPainters != null) {
			updateAllObjects();
		}
	}

	public WorldObject getClickedObject(int x, int y) {
		WorldObject obj = null;
		
		Iterator it = getObjectPaintersSorted().iterator();
		while (it.hasNext()) {
			ObjectPainter objPainter = (ObjectPainter) it.next();
			if (objPainter.getImageRectangle().contains(x, y)) {
				if (objPainter.selected) {
					return objPainter.originalObject; // prefer selected object
				}
				obj = objPainter.originalObject;
			}
		}
		return obj;
	}

	public WorldObject getClickedObjectUnderSelected(int x, int y) {
		ObjectPainter selObject = null;

		// find clicked object as usual, prefer selected
		Iterator it = getObjectPaintersSorted().iterator();
		while (it.hasNext()) {
			ObjectPainter objPainter = (ObjectPainter) it.next();
			if (objPainter.getImageRectangle().contains(x, y)) {
				selObject = objPainter;
				if (objPainter.selected) {
					break; // take *first* selected
				}
			}
		}
		if (selObject == null) {
			return null;
		}
		if (!selObject.selected) {
			return selObject.originalObject;
		}
		
		// if selected, find clicked object that is directly under selObject
		ObjectPainter res = null;
		it = getObjectPaintersSorted().iterator();
		while (it.hasNext()) {
			ObjectPainter objPainter = (ObjectPainter) it.next();
			if (objPainter.getImageRectangle().contains(x, y)) {
				if (res != null && ((res.compareToIgnoreSelection(selObject) < 0 && objPainter.compareToIgnoreSelection(selObject) > 0) || objPainter.selected)) {
					break; // take this: object UNDER currently selected object
				}
				res = objPainter;
			}
		}
		if (res == null) {
			return selObject.originalObject;
		} else {
			return res.originalObject;
		}
	}
	
	public void dispose() {
		bkIslandColor.dispose();
		selFrameColor.dispose();
		objectTextColor.dispose();

		if (objectTextFont != null) {
			objectTextFont.dispose();
		}
		
		for (Iterator it = getOverlayInfoList().iterator(); it.hasNext();) {
			OverlayInfo renderInfo = (OverlayInfo) it.next();
			renderInfo.getRenderer().dispose();
		}
		
		if (objectPainters != null) {
			for (Iterator it = objectPainters.values().iterator(); it.hasNext(); ) {
				ObjectPainter objPainter = (ObjectPainter) it.next();
				objPainter.dispose();
			}
		}
		clearImageCache();
	}

	protected void clearImageCache() {
		for (Iterator it = scaledImageCache.values().iterator(); it.hasNext(); ) {
			Image image = (Image) it.next();
			image.dispose();
		}
		scaledImageCache.clear();
	}

	/* @see org.micropsi.comp.console.worldconsole.IRenderInfo#getScreenX(org.micropsi.common.coordinates.Position)*/
	public int getScreenX(Position pos) {
		return (int) Math.round((pos.getX() - worldLowestCoords.getX())
				* scaleX);
	}

	/* @see org.micropsi.comp.console.worldconsole.IRenderInfo#getScreenX(double)*/
	public int getScreenX(double x) {
		return (int) Math.round((x - worldLowestCoords.getX()) * scaleX);
	}

	/* @see org.micropsi.comp.console.worldconsole.IRenderInfo#getScreenY(org.micropsi.common.coordinates.Position)*/
	public int getScreenY(Position pos) {
		return (int) Math.round((worldHighestCoords.getY() - pos.getY())
				* scaleY);
	}

	/* @see org.micropsi.comp.console.worldconsole.IRenderInfo#getScreenY(double)*/
	public int getScreenY(double y) {
		return (int) Math.round((worldHighestCoords.getY() - y) * scaleY);
	}

	/* @see org.micropsi.comp.console.worldconsole.IRenderInfo#getWorldPosition(int, int)*/
	public Position getWorldPosition(int screenX, int screenY) {
		return new Position(screenX / scaleX + worldLowestCoords.getX(),
				worldHighestCoords.getY() - screenY / scaleY, 0);
	}
	
	/* @see org.micropsi.comp.console.worldconsole.IRenderInfo#getObjectBounds(org.micropsi.comp.console.worldconsole.model.WorldObject)*/
	public Rectangle getObjectBounds(WorldObject obj) {
		if (obj == null) {
			throw new IllegalArgumentException("obj may not be null");
		}
		ObjectPainter objPainter = getObjectPainters().get(new Long(obj.getId()));
		if (objPainter != null) {
			return objPainter.getImageRectangle();
		} else {
			return null;
		}
	}

	protected Collection getObjectPaintersSorted() {
		if (cachedSortedObjectPainters == null) {
			cachedSortedObjectPainters = new TreeSet<ObjectPainter>(getObjectPainters().values());
		}
		return cachedSortedObjectPainters;
	}
	
	protected void resetObjectPaintersSort() {
		cachedSortedObjectPainters = null;
	}

	/**
	 * @return Returns the localWorld.
	 */
	protected LocalWorld getLocalWorld() {
		return localWorld;
	}
	/**
	 * @return Returns the objectPainters.
	 */
	protected Map<Long,ObjectPainter> getObjectPainters() {
		if (objectPainters == null) {
			updateAllObjects();
		}
		return objectPainters;
	}
	
	/* @see org.micropsi.comp.console.worldconsole.IRenderInfo#getSizeRenderedWorld()*/
	public Point getSizeRenderedWorld() {
		return new Point(getScreenX(getWorldHighestCoords()) - getScreenX(getWorldLowestCoords()), getScreenY(getWorldLowestCoords()) - getScreenY(getWorldHighestCoords()));
	}
	/* @see org.micropsi.comp.console.worldconsole.IRenderInfo#getWorldHighestCoords()*/
	public Position getWorldHighestCoords() {
		return worldHighestCoords;
	}
	/* @see org.micropsi.comp.console.worldconsole.IRenderInfo#getWorldLowestCoords()*/
	public Position getWorldLowestCoords() {
		return worldLowestCoords;
	}
	/* @see org.micropsi.comp.console.worldconsole.IRenderInfo#getScaleX()*/
	public double getScaleX() {
		return scaleX;
	}
	/* @see org.micropsi.comp.console.worldconsole.IRenderInfo#getScaleY()*/
	public double getScaleY() {
		return scaleY;
	}
	
	public void setWorldArea(Area2D area) {
		worldLowestCoords = area.getLowestCoords();
		worldHighestCoords = area.getHighestCoords();
		updateAllObjects();
	}

	public void setGroundmapArea(Area2D area) {
		groundMapLowestCoords = area.getLowestCoords();
		groundMapHighestCoords = area.getHighestCoords();
	}
	
	/**
	 * @return Returns the editSession.
	 */
	protected EditSession getEditSession() {
		return editSession;
	}

	public Position getDragPosition(ObjectPainter objPainter, int x, int y) {
		Position res = new Position(objPainter.dragOrigin.worldPosition);
		res.setX(res.getX() + x / getScaleX());
		res.setY(res.getY() - y / getScaleY());
		return res;
	}
	
	public void prepareDragging(ObjectPainter objPainter) {
		objPainter.prepareDragging();
	}

	public Rectangle startDragging(ObjectPainter objPainter) {
		return objPainter.startDragging();
	}

	public void endDragging(ObjectPainter objPainter) {
		objPainter.endDragging();
	}
	
	public Rectangle dragObjectPainterBy(ObjectPainter objPainter, int x, int y) {
		return objPainter.dragBy(x, y);
	}

	/**
	 * @return Returns the selectedObjectPainters.
	 */
	public Set<ObjectPainter> getSelectedObjectPainters() {
		return selectedObjectPainters;
	}
	
	/**Gets the rectangle shown while selecting areas. Returns null when no selection rectangle is shown.
	 * @return Returns the selectionRectangle. May be null
	 */
	public Rectangle getSelectionRectangle() {
		return selectionRectangle;
	}
	
	/**Sets the rectangle shown while selecting areas. Set null when no selection rectangle should be shown.
	 * Returns the image area that needs to be updated to reflect this change.
	 * 
	 * @param selectionRectangle The selectionRectangle to set. May be null.
	 * @return a rectangle describing the image area that needs to be updated. Null if no update is necessary.
	 */
	public Rectangle updateSelectionRectangle(Rectangle selectionRectangle) {
		Rectangle updateRegion = getSelectionRectangle();
		this.selectionRectangle = selectionRectangle;
		if (updateRegion == null) {
			return selectionRectangle;
		} else {
			if (selectionRectangle == null) {
				return updateRegion;
			} else {
				return updateRegion.union(selectionRectangle);
			}
		}
	}
	
	public Collection<WorldObject> getObjectsInSelectionRectangle() {
		Collection<WorldObject> res = new ArrayList<WorldObject>();
		Rectangle sel = getSelectionRectangle(); 
		if (sel == null) {
			return res;
		}
		for (Iterator it = getObjectPainters().values().iterator(); it.hasNext(); ) {
			ObjectPainter objPainter = (ObjectPainter) it.next();
			Rectangle r = objPainter.getImageRectangle();
			if (sel.contains(r.x, r.y) && sel.contains(r.x + r.width, r.y + r.height)) {
				res.add(objPainter.originalObject);
			}
		}
		return res;
	}

	/**Returns a List of <code>WorldRenderer.OverlayInfo</code> objects. Instances can be modified. Modification
	 * does not trigger redraw.
	 * 
	 * @return Returns the overlayInfoList.
	 */
	public List getOverlayInfoList() {
		return overlayInfoList;
	}

	/**Returns the <code>WorldRenderer.OverlayInfo</code> objects with the given name, null if none exists. 
	 * Instance can be modified. Modification does not trigger redraw.
	 * 
	 * @return the overlayInfo.
	 */
	public OverlayInfo getOverlayInfo(String name) {
		for (Iterator it = getOverlayInfoList().iterator(); it.hasNext(); ) {
			OverlayInfo renderInfo = (OverlayInfo) it.next();
			if (renderInfo.getName().equals(name)) {
				return renderInfo;
			}
		}
		return null;
	}
	
	/**Returns true if the overlay with the given name is enabled, false if it is not or if there is no
	 * such overlay.
	 * 
	 * @param name - the name of the overlay
	 * @return true if overlay is enabled
	 */
	public boolean isOverlayEnabled(String name) {
		OverlayInfo renderInfo = getOverlayInfo(name);
		if (renderInfo != null) {
			return renderInfo.isEnabled();
		} else {
			return false;
		}
	}
	
	/**Sets the enabled state of the overlay with the given name. Does not trigger redraw. Returns true
	 * if something has changed.
	 * 
	 * @param name - the name of the overlay
	 * @param enabled - the new state
	 * @return - true, if something has changed
	 */
	public boolean setOverlayEnabled(String name, boolean enabled) {
		OverlayInfo renderInfo = getOverlayInfo(name);
		if (renderInfo != null && renderInfo.isEnabled() != enabled) {
			renderInfo.setEnabled(enabled);
			return true;
		}
		return false;
	}
}
