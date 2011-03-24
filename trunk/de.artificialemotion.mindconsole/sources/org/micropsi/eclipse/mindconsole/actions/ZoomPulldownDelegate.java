/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/actions/ZoomPulldownDelegate.java,v 1.2 2004/08/10 14:35:49 fuessel Exp $ 
 */
package org.micropsi.eclipse.mindconsole.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate;

import org.micropsi.eclipse.mindconsole.MindEditController;


public class ZoomPulldownDelegate implements IWorkbenchWindowPulldownDelegate {
 
	Menu menu;
	int lastzoom = 100;
 
	public Menu getMenu(final Control parent) {
		
		if(menu == null) {
			menu = new Menu(parent); 

			MenuItem item = new MenuItem(menu,SWT.CASCADE);
			item.setText("25%");
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					lastzoom = 25;
					MindEditController.getInstance().setZoom(25);
				}
			});
	
			item = new MenuItem(menu,SWT.CASCADE);
			item.setText("50%");
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					lastzoom = 50;
					MindEditController.getInstance().setZoom(50);
				}
			});

			item = new MenuItem(menu,SWT.CASCADE);
			item.setText("75%");
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					lastzoom = 75;
					MindEditController.getInstance().setZoom(75);
				}
			});

			item = new MenuItem(menu,SWT.CASCADE);
			item.setText("100%");
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					lastzoom = 100;
					MindEditController.getInstance().setZoom(100);
				}
			});

			item = new MenuItem(menu,SWT.CASCADE);
			item.setText("125%");
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					lastzoom = 125;
					MindEditController.getInstance().setZoom(125);
				}
			});

			item = new MenuItem(menu,SWT.CASCADE);
			item.setText("150%");
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					lastzoom = 150;
					MindEditController.getInstance().setZoom(150);
				}
			});

			item = new MenuItem(menu,SWT.CASCADE);
			item.setText("200%");
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					lastzoom = 200;
					MindEditController.getInstance().setZoom(200);
				}
			});

			item = new MenuItem(menu,SWT.CASCADE);
			item.setText("500%");
			item.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					lastzoom = 500;
					MindEditController.getInstance().setZoom(500);
				}
			});
		}
		
		return menu;
	}

	public void dispose() {
		if(menu != null) menu.dispose();
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		MindEditController.getInstance().setZoom(lastzoom);
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
