package org.micropsi.eclipse.mindconsole;

import java.util.Iterator;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.part.ViewPart;

import org.micropsi.eclipse.console.controller.IViewControllerListener;
import org.micropsi.eclipse.mindconsole.dialogs.LinkDialog;
import org.micropsi.eclipse.model.net.AgentNetModelManager;
import org.micropsi.eclipse.model.net.NetModel;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.Slot;

/**
 *
 *
 *
 */
public class IncomingLinksView extends ViewPart implements IViewControllerListener {
	
	class LinkContentProvider implements IStructuredContentProvider {

		Slot slot;

		public Object[] getElements(Object inputElement) {
			if(slot == null) return new Object[0];
			
			Object[] toReturn = new Object[slot.getNumberOfIncomingLinks()];
			Iterator iter = slot.getIncomingLinks();
			int i = 0;
			while(iter.hasNext()) {
				toReturn[i] = iter.next();
				i++;
			}
			
			return toReturn;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			this.slot = (Slot)newInput;
		}
	}

	private NetModel netmodel;
	private Slot currentSlot;
	
	private Thread uiThread;
	private ListViewer links;
	private IStatusLineManager statusLineManager;
	
	public IncomingLinksView() {
		super();
		netmodel = AgentNetModelManager.getInstance().getNetModel();
		IncomingLinksController.getInstance().registerView(this);
		uiThread = Thread.currentThread();
	}

	public void createPartControl(Composite parent) {
		
		Composite topLevel = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		topLevel.setLayout(layout);
		topLevel.setFont(parent.getFont());
		
		links = new ListViewer(topLevel);
		links.getList().setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		links.setContentProvider(new LinkContentProvider());
						
		links.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				Link l = (Link)element;
				String d = l.getLinkingEntity().getID();				
				if(l.getLinkingEntity().hasName()) d += " ("+l.getLinkingEntity().getEntityName()+")";
				return d;
			}
		});
						
		links.getList().addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
			}
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == SWT.DEL) {
					triggerLinkDelete(links.getList().getSelectionIndex());
				}
			}
		});
		
		links.getList().addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				Link l = (Link)links.getElementAt(links.getList().getSelectionIndex());
				LinkDialog dlg = new LinkDialog(getSite().getShell(),netmodel,l);
				dlg.open();
			}
		});
		
		Menu popUp = new Menu(links.getList());
		
		MenuItem item = new MenuItem(popUp,SWT.CASCADE);
		item.setText("Edit link paramters");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(links.getList().getSelectionIndex() < 0) return;
				Link l = (Link)links.getElementAt(links.getList().getSelectionIndex());
				LinkDialog dlg = new LinkDialog(getSite().getShell(),netmodel,l);
				dlg.open();				
			}
		});
		
		item = new MenuItem(popUp,SWT.CASCADE);
		item.setText("Delete link");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(links.getList().getSelectionIndex() < 0) return;
				triggerLinkDelete(links.getList().getSelectionIndex());
			}
		});
		
		item = new MenuItem(popUp,SWT.CASCADE);
		item.setText("Look up linking node");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(links.getList().getSelectionIndex() < 0) return;
				Link l = (Link)links.getElementAt(links.getList().getSelectionIndex());
				triggerFollowLink(l.getLinkingEntity());
			}
		});
		
		item = new MenuItem(popUp,SWT.CASCADE);
		item.setText("Linking node to front");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(links.getList().getSelectionIndex() < 0) return;
				Link l = (Link)links.getElementAt(links.getList().getSelectionIndex());
				triggerToFront(l.getLinkingEntity().getID());
			}
		});
		
	
		links.getList().setMenu(popUp);				
		
		statusLineManager = this.getViewSite().getActionBars().getStatusLineManager();
	}
	
	public void setFocus() {
	}

	public IStatusLineManager getStatusLineManager() {
		return statusLineManager;
	}
	
	public void triggerLinkDelete(int i) {
		Link l = currentSlot.getIncomingLinkAt(i);
		
		try {
			netmodel.getNet().deleteLink(
				l.getLinkingEntity().getID(),
				l.getLinkingGate().getType(),
				currentSlot.getNetEntity().getID(), 
				currentSlot.getType());
		} catch (Exception e) {
			MindPlugin.getDefault().handleException(e);
		}
		
		links.refresh();
		
		LinkageEditController.getInstance().setData(null);
		
	}
	
	public void triggerFollowLink(NetEntity linked) {
		MindEditController.getInstance().lookUpNode(linked.getID());
	}

	public void triggerToFront(String id) {
		MindEditController.getInstance().bringToFront(id);
	}

	/**
	 * @see org.micropsi.comp.console.controller.ViewControllerListenerIF#setDataBase(java.lang.Object)
	 */
	public void setDataBase(Object o) {
		this.netmodel = (NetModel)o;
		setData(null);
	}

	/**
	 * @see org.micropsi.comp.console.controller.ViewControllerListenerIF#setData(java.lang.Object)
	 */
	public void setData(Object o) {
		if(o == null) {
			Display.findDisplay(uiThread).asyncExec(new Runnable() {
				public void run() {
					currentSlot = null;
					links.setInput(null);
				}
			});
		} else {
			currentSlot = (Slot)o;
			links.setInput(currentSlot);
		}

	}

}
