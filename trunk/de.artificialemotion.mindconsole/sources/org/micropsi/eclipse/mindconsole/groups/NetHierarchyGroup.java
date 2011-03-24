package org.micropsi.eclipse.mindconsole.groups;

import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TreeItem;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.mindconsole.widgets.IMindEditCallback;
import org.micropsi.eclipse.mindconsole.widgets.NetLabelProvider;
import org.micropsi.nodenet.Module;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.NetFacadeIF;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.NetObserverIF;
import org.micropsi.nodenet.NodeSpaceModule;

/**
 * 
 * 
 * 
 */
public class NetHierarchyGroup implements NetObserverIF {
	
	private NetFacadeIF net;
	
	private TreeViewer hierarchyTree;
	private IMindEditCallback callback;
	
	private Thread uiThread;
	
	public NetHierarchyGroup(Composite parent, NetFacadeIF net, IMindEditCallback callback) {
		this.net = net;
		this.callback = callback;
		this.uiThread = Thread.currentThread();
		createControls(parent);
	}
	
	protected void createControls(Composite parent) {
		Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		topLevel.setFont(parent.getFont());
		
		hierarchyTree = new TreeViewer(topLevel);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.heightHint = 200;
		hierarchyTree.getTree().setLayoutData(data);
		hierarchyTree.setContentProvider(new ITreeContentProvider() {
			
			public Object[] getChildren(Object parentElement) {
				NetEntity entity = (NetEntity)parentElement;
				if(entity.getEntityType() != NetEntityTypesIF.ET_MODULE_NODESPACE)
					return new Object[] {};
				
				NodeSpaceModule space = (NodeSpaceModule)entity;
				
				Vector<NetEntity> toReturn = new Vector<NetEntity>();
				Iterator<NetEntity> iter = space.getAllLevelOneEntities();		
				while(iter.hasNext()) toReturn.add(iter.next());				
				return toReturn.toArray();
			}
			
			public Object getParent(Object element) {
				NetEntity entity = (NetEntity)element;
				if(entity.getEntityType() == NetEntityTypesIF.ET_NODE)
					return null;
				
				Module m = (Module) entity;
				try {
					return m.getParent();
				} catch (NetIntegrityException e) {
					return null;
				}
				
			}
			
			public boolean hasChildren(Object element) {
				NetEntity entity = (NetEntity)element;
				if(entity.getEntityType() != NetEntityTypesIF.ET_MODULE_NODESPACE)
					return false;
				
				return true;
			}
			
			public Object[] getElements(Object inputElement) {
				if(net == null) return new Object[] {};
				try {
					return new Object[] {net.getRootNodeSpaceModule()};
				} catch (MicropsiException e) {
					return null;
				}
			}
			
			public void dispose() {
			}
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		});

		
		hierarchyTree.setLabelProvider(new NetLabelProvider());
		
		hierarchyTree.setInput(new Object());
		
		// interaction capabilities only make sense when there is a callback
		if(callback == null) return;
		
		hierarchyTree.addOpenListener(new IOpenListener() {

			public void open(OpenEvent event) {
				TreeItem[] selection = hierarchyTree.getTree().getSelection();
				if(selection != null && selection.length > 0) {
					NetEntity selected = (NetEntity)selection[0].getData();
					callback.openEntity(selected.getID());
				}
			}
			
		});
		
		hierarchyTree.getTree().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] selection = hierarchyTree.getTree().getSelection();
				if(selection != null && selection.length > 0) {
					NetEntity selected = (NetEntity)selection[0].getData();
					callback.selectEntity(selected.getID());
				}
			}			
		});
		
		final Menu popUpMenu = new Menu(hierarchyTree.getTree());
		
		popUpMenu.addMenuListener(new MenuListener() {

			public void menuHidden(MenuEvent e) {
			}

			public void menuShown(MenuEvent e) {
				TreeItem[] selection = hierarchyTree.getTree().getSelection();
				if(selection != null && selection.length > 0) {
					NetEntity selected = (NetEntity)selection[0].getData();
					try {
						popUpMenu.getItem(2).setEnabled(net.getRootNodeSpaceModule() != selected);
					} catch (MicropsiException exc) {
						callback.handleException(exc);
					}
				}
			}
		});
		
		hierarchyTree.getTree().setMenu(popUpMenu);
		MenuItem item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Refresh");
		item.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				refresh();
			}
			
		});
		
		new MenuItem(popUpMenu,SWT.SEPARATOR);
		
		item = new MenuItem(popUpMenu, SWT.CASCADE);
		item.setText("Delete");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TreeItem[] selection = hierarchyTree.getTree().getSelection();
				triggerEntityDeletion(selection);
			}
		});

		
		hierarchyTree.getTree().addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				switch(e.keyCode) {
					case SWT.DEL:
						TreeItem[] selection = hierarchyTree.getTree().getSelection();
						triggerEntityDeletion(selection);
						break;
				}
				
			}

			public void keyReleased(KeyEvent e) {			
			}
			
		});
	}
	
	public NetEntity getSelected() {
		TreeItem[] sel = hierarchyTree.getTree().getSelection();
		
		if(sel != null && sel.length > 0)
			return (NetEntity)sel[0].getData();
		
		return null;
	}
	
	public void addSelectionListener(ISelectionChangedListener listener) {
		hierarchyTree.addSelectionChangedListener(listener);
	}

	public void select(String preSelectedEntity) {				
		try {
			NetEntity e = net.getEntity(preSelectedEntity);		
			hierarchyTree.expandAll();
			hierarchyTree.setSelection(new StructuredSelection(e),true);	
		} catch (MicropsiException exc) {
			MindPlugin.getDefault().handleException(exc);
		}		
	}
	
	public void refresh() {
		Display.findDisplay(uiThread).asyncExec(new Runnable() {
			public void run() {
				hierarchyTree.refresh();				
			}
		});
	}
	
	public void setData(NetFacadeIF net) {
		this.net = net;
		refresh();
	}

	public void updateEntities(Iterator entities, long step) {
		// let's see - perhaps we mark them by color when visible
	}

	public void createEntities(Iterator entities, long step) {
		if(entities.hasNext()) refresh();
	}

	public void deleteEntities(Iterator entities, long step) {
		if(entities.hasNext()) refresh();
	}
	
	public void triggerEntityDeletion(TreeItem[] selection) {
		if(selection != null) {
			for(int i=0;i<selection.length;i++) {
				try {
					NetEntity selected = (NetEntity)selection[i].getData();
					
					// if one of the selected entities was a nodespace
					// whose children were also selected, they might be
					// deleted already, so only delete those entities that
					// are still there...
					if(net.entityExists(selected.getID()))
						net.deleteEntity(selected.getID());
				} catch (MicropsiException exc) {
					MindPlugin.getDefault().handleException(exc);
				}			
			}
		}
	}
	
}
