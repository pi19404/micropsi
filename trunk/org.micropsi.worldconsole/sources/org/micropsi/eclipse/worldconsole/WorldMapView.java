package org.micropsi.eclipse.worldconsole;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.micropsi.comp.console.worldconsole.EditSession;
import org.micropsi.comp.console.worldconsole.IViewControllerListener;
import org.micropsi.comp.console.worldconsole.LocalWorld;
import org.micropsi.comp.console.worldconsole.OverlayRendererDescriptor;
import org.micropsi.comp.console.worldconsole.WorldMetaDataController;
import org.micropsi.eclipse.worldconsole.actions.EditModeChangeAction;
import org.micropsi.eclipse.worldconsole.actions.OverlayPulldownAction;
import org.micropsi.eclipse.worldconsole.actions.ZoomPulldownAction;

/**
 * @author David
 *
 *
 */
public class WorldMapView extends ViewPart implements IViewControllerListener, ModifyListener, IWorldWidgetScroller {
	
	private IStatusLineManager statusLineManager;
	private WorldMapWidget worldWidget;
	protected ScrolledComposite worldScroller = null;
	protected Label editModeIconLabel = null;
	protected Label editModeLabel = null;
	protected Combo objectTypesCombo = null;

	public WorldMapView() {
	}


	/**
	 * called by eclipse when controls must be created
	 */
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		parent.setLayout(layout);
		
		Composite editModeStatus = new Composite(parent, SWT.NONE);
		GridLayout editModeStatusLayout = new GridLayout(3, false);
		editModeStatusLayout.verticalSpacing = 0;
		editModeStatus.setLayout(editModeStatusLayout);
		editModeIconLabel = new Label(editModeStatus, SWT.NONE);
		editModeLabel = new Label(editModeStatus, SWT.NONE);
		objectTypesCombo = new Combo(editModeStatus, SWT.DROP_DOWN | SWT.READ_ONLY);
		objectTypesCombo.setVisibleItemCount(20);
		objectTypesCombo.setToolTipText("Select object type to be created on click");
		objectTypesCombo.addModifyListener(this);

		// set up scroller with the world widget

		worldScroller = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		worldScroller.setLayoutData(new GridData(GridData.FILL_BOTH));
		LocalWorld localWorld = WorldConsole.getInstance().getGlobalData().getLocalWorld();
		initializeOverlays(localWorld);
		EditSession editSession = WorldConsole.getInstance().getGlobalData().getEditSession();
		worldWidget = new WorldMapWidget(worldScroller, localWorld, editSession);
		worldWidget.setMenu(WorldConsole.getInstance().getGlobalData().getMenuManager().createContextMenu(this.getSite().getShell()));
		worldScroller.setContent(worldWidget);
		worldWidget.setWorldScroller(this);
		worldScroller.addControlListener(worldWidget);

		statusLineManager = this.getViewSite().getActionBars().getStatusLineManager();
		EditModeChangeAction editModeSelectAction = new EditModeChangeAction(this, "Select", "Select and modify objects", "icons/editmodeselect.gif");
		editModeSelectAction.addPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(EditModeChangeAction.CHECKED) && event.getNewValue().equals(Boolean.TRUE)) {
					setEditMode(((EditModeChangeAction) event.getSource()).getImageDescriptor(), WorldMapWidget.MODE_SELECT);
				}
			}
		});
		getViewSite().getActionBars().getToolBarManager().add(editModeSelectAction);
		
		EditModeChangeAction action = new EditModeChangeAction(this, "Create", "Create new objects", "icons/editmodecreate.gif");
		action.addPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(EditModeChangeAction.CHECKED) && event.getNewValue().equals(Boolean.TRUE)) {
					setEditMode(((EditModeChangeAction) event.getSource()).getImageDescriptor(), WorldMapWidget.MODE_CREATE);
				}
			}
		});
		getViewSite().getActionBars().getToolBarManager().add(action);
		
		action = new EditModeChangeAction(this, "Zoom", "Zoom in or out", "icons/editmodezoom.gif");
		action.addPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(EditModeChangeAction.CHECKED) && event.getNewValue().equals(Boolean.TRUE)) {
					setEditMode(((EditModeChangeAction) event.getSource()).getImageDescriptor(), WorldMapWidget.MODE_ZOOM);
				}
			}
		});
		getViewSite().getActionBars().getToolBarManager().add(action);
		
		editModeSelectAction.setChecked(true);
		getViewSite().getActionBars().getToolBarManager().add(new Separator());
		getViewSite().getActionBars().getToolBarManager().add(new OverlayPulldownAction(this));
		getViewSite().getActionBars().getToolBarManager().add(new ZoomPulldownAction(this));
		
		updateObjectList(WorldConsole.getInstance().getGlobalData().getWorldMetaData().getObjectTypes());
		WorldConsole.getInstance().getGlobalData().getWorldMetaData().registerView(this);
		
	}

	private void initializeOverlays(LocalWorld localWorld) {		
		
		List overlays = WorldPlugin.getDefault().getOverlayDescriptors();
		
		for(int i=0;i<overlays.size();i++) {
			OverlayRendererDescriptor descriptor = (OverlayRendererDescriptor) overlays.get(i);
			localWorld.getImageLibrary().registerOverlayRenderer(descriptor);			
		}
		
	}
	
	/**
	 * @param objectTypes
	 */
	protected void updateObjectList(Collection objectTypes) {
		if (objectTypesCombo != null && !objectTypesCombo.isDisposed()) {
			String oldSelected = objectTypesCombo.getText();
			objectTypesCombo.removeAll();
			for (Iterator it = objectTypes.iterator(); it.hasNext(); ) {
				String objectType = (String) it.next();
				objectTypesCombo.add(objectType);
			}
			String[] items = objectTypesCombo.getItems();
			for (int i = 0; i < items.length; i++) {
				if (items[i].equals(oldSelected)) {
					objectTypesCombo.select(i);
					break;
				}
			}
			if (objectTypesCombo.getSelectionIndex() < 0 && items.length > 0) {
				objectTypesCombo.select(0);
			}
		}
	}


	/**
	 * called by eclipse when view receives focus
	 */
	public void setFocus() {
	}

	public IStatusLineManager getStatusLineManager() {
		return statusLineManager;
	}



	/**
	 * @return
	 */
	public WorldMapWidget getWorldWidget() {
		return worldWidget;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		super.dispose();
	}

	/**
	 * @param editMode
	 */
	public void setEditMode(ImageDescriptor img, int editMode) {
		getWorldWidget().setEditMode(editMode);
		if (editModeIconLabel.getImage() != null) {
			editModeIconLabel.getImage().dispose();
		}
		if (img != null) {
			editModeIconLabel.setImage(img.createImage());
		}
		switch (editMode) {
		case WorldMapWidget.MODE_SELECT :
			editModeLabel.setText("Select objects (Ctrl for multiple)");
			objectTypesCombo.setVisible(false);
			break;
		case WorldMapWidget.MODE_CREATE :
			editModeLabel.setText("Create objects:");
			objectTypesCombo.setVisible(true);
			break;
		case WorldMapWidget.MODE_ZOOM :
			editModeLabel.setText("Zoom in or (Shift) out");
			objectTypesCombo.setVisible(false);
			break;
		}
	}


	/* @see org.micropsi.comp.console.worldconsole.IViewControllerListener#setDataBase(java.lang.Object)*/
	public void setDataBase(Object o) {
	}


	/**Called when world metadata changed. Refresh list of possible object types.
	 * @see org.micropsi.comp.console.worldconsole.IViewControllerListener#setData(java.lang.Object)
	 * */
	public void setData(Object data) {
		updateObjectList(((WorldMetaDataController) data).getObjectTypes());
	}


	/**Called when selected object type changes.
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 * */
	public void modifyText(ModifyEvent e) {
		getWorldWidget().setCreateObjectType(objectTypesCombo.getText());
	}


	/* @see org.micropsi.eclipse.worldconsole.IWorldWidgetScroller#setScrollOffset(int, int)*/
	public void setScrollOffset(int x, int y) {
		worldScroller.setOrigin(Math.max(0, x), Math.max(0, y));
	}


	/* @see org.micropsi.eclipse.worldconsole.IWorldWidgetScroller#getScrollOffset()*/
	public Point getScrollOffset() {
		return worldScroller.getOrigin();
	}
}
