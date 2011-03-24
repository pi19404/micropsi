package org.micropsi.eclipse.worldconsole;

import java.util.Collection;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.micropsi.comp.console.worldconsole.EditSession;
import org.micropsi.comp.console.worldconsole.IEditSessionListener;
import org.micropsi.comp.console.worldconsole.ILocalWorldListener;
import org.micropsi.comp.console.worldconsole.LocalWorld;
import org.micropsi.comp.console.worldconsole.model.AbstractWorldObject;
import org.micropsi.comp.console.worldconsole.model.WorldObject;

/**
 * @author David
 *
 *
 */
public class WorldObjectListView extends ViewPart implements ILocalWorldListener, IEditSessionListener {
	
	public class WorldObjectLabelProvider extends LabelProvider {
		
		public String getText(Object element) {
			AbstractWorldObject obj = (AbstractWorldObject) element;
			return obj.getLabelText();
		}
	}
	
	public class WorldObjectContentProvider implements ITreeContentProvider {

		public Object[] getElements(Object inputElement) {
			return ((Collection) inputElement).toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
		 */
		public Object[] getChildren(Object parentElement) {
			AbstractWorldObject obj = (AbstractWorldObject) parentElement;
			if (obj.getSubParts() == null) {
				return new Object[0];
			} else {
				return obj.getSubParts().toArray();
			}
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
		 */
		public Object getParent(Object element) {
			AbstractWorldObject obj = (AbstractWorldObject) element;
			return obj.getParent();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
		 */
		public boolean hasChildren(Object element) {
			AbstractWorldObject obj = (AbstractWorldObject) element;
			return (obj.getSubParts() != null && obj.getSubParts().size() > 0);
		}
	}
	
	private boolean disposed = false;
	
	private IStatusLineManager statusLineManager;
	private TreeViewer objTree;	
	private Composite toolbar;
	
//	private Group groupProps;
//	private Label objName, objClass, objPos, objID;

	protected LocalWorld localWorld;
	protected EditSession editSession;
	private Collection currentObjects = null;
	

	public WorldObjectListView() {
		localWorld = WorldConsole.getInstance().getGlobalData().getLocalWorld();
		editSession = WorldConsole.getInstance().getGlobalData().getEditSession();
	}


	/**
	 * called by eclipse when controls must be created
	 */
	public void createPartControl(Composite parent) {
		toolbar = new Composite(parent, SWT.NONE);
		GridData g = new GridData(GridData.FILL_VERTICAL);
		g.widthHint = 200;
		g.heightHint = 500;
		toolbar.setLayoutData(g);
		GridLayout toollayout = new GridLayout();
		toollayout.numColumns = 1;
		toolbar.setLayout(toollayout);

		
			// refesh button
			
		Button refreshButton = new Button(toolbar,SWT.NONE);
		refreshButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		refreshButton.setText("Refresh data");
		refreshButton.setToolTipText("Reget all object data from remote world");
		refreshButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				queryWorldObjects();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});


			// seperator

		Label sep = new Label(toolbar, SWT.HORIZONTAL | SWT.SHADOW_OUT | SWT.SEPARATOR);
		sep.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		
			// list of objects
		
		objTree = new TreeViewer(toolbar, SWT.MULTI);
		objTree.setLabelProvider(new WorldObjectLabelProvider());
		objTree.setContentProvider(new WorldObjectContentProvider());
		objTree.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		objTree.getTree().addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				editSession.selectObjects(((IStructuredSelection) objTree.getSelection()).toList(), false);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		objTree.getTree().setMenu(WorldConsole.getInstance().getGlobalData().getMenuManager().createContextMenu(this.getSite().getShell()));
		currentObjects = localWorld.getObjects();
		objTree.setInput(currentObjects);
		objTree.setSorter(new ViewerSorter());
		
		statusLineManager = this.getViewSite().getActionBars().getStatusLineManager();
		
		localWorld.registerView(this);
		editSession.registerView(this);
	}


	/**
	 * called by eclipse when view receives focus
	 */
	public void setFocus() {
//		objTree.setFocus();
	}

	public IStatusLineManager getStatusLineManager() {
		return statusLineManager;
	}



	public void queryWorldObjects() {
		WorldConsole.getInstance().getGlobalData().getLocalWorld().requestObjectList();
	}


	/**
	 * @see de.artificialemotion.comp.consoleapp.plugin.worldperspective.WorldSelectionChangeListenerIF#onSelectionChanged(de.artificialemotion.comp.consoleapp.plugin.worldperspective.LocalWorldController, Collection)
	 */
	public void onSelectionChanged(EditSession session, Collection changeList) {
		if (!disposed) {
			objTree.setSelection(new StructuredSelection(session.getSelectedObjectParts().toArray()), true);
		}
	}

	
	/**
	 * @see de.artificialemotion.comp.consoleapp.plugin.worldperspective.WorldObjChangeListenerIF#onObjectChanged(de.artificialemotion.comp.consoleapp.plugin.worldperspective.LocalWorldController, long)
	 */
	public void onObjectChanged(LocalWorld mgr, WorldObject object) {
		if (!disposed) {
			if (object.isRemoved()) {
				objTree.remove(object);
			} else if ((object.changeType & WorldObject.CT_CREATE) != 0) {
				objTree.refresh();
//				objTree.add(null, object);
			} else if ((object.changeType & WorldObject.CT_CHANGE_SUBPART) != 0) {
				objTree.refresh(object);
			} else {
				objTree.update(object, null);
			}
		}
	}
	

	/* (non-Javadoc)
	 * @see de.artificialemotion.comp.consoleapp.plugin.worldperspective.WorldObjChangeListenerIF#OnObjListRefreshed(de.artificialemotion.comp.consoleapp.plugin.worldperspective.WorldObjectMgr)
	 */
	public void onObjectListRefreshed(LocalWorld mgr) {
		if (!disposed) {
			Collection newObjects = mgr.getObjects();
			if (newObjects == currentObjects) {
				objTree.refresh();
			} else {
				currentObjects = newObjects;
				objTree.setInput(currentObjects);
			}
		}
	}


	/* (non-Javadoc)
	 * @see org.micropsi.eclipse.console.controller.IViewControllerListener#setDataBase(java.lang.Object)
	 */
	public void setDataBase(Object o) {
	}


	/* (non-Javadoc)
	 * @see org.micropsi.eclipse.console.controller.IViewControllerListener#setData(java.lang.Object)
	 */
	public void setData(Object o) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		disposed = true;
		super.dispose();
	}


	/* (non-Javadoc)
	 * @see org.micropsi.eclipse.worldconsole.ILocalWorldListener#OnMultipleObjectsChanged(org.micropsi.eclipse.worldconsole.LocalWorld, java.util.List)
	 */
	public void onMultipleObjectsChanged(LocalWorld mgr, Collection changedObjects) {
		onObjectListRefreshed(mgr);
	}


	/* @see org.micropsi.eclipse.worldconsole.ILocalWorldListener#onGlobalsChanged()*/
	public void onGlobalsChanged() {
		// TODO Auto-generated method stub
		
	}

}
