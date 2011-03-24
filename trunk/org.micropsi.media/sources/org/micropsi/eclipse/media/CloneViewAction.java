/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.media/sources/org/micropsi/eclipse/media/CloneViewAction.java,v 1.1 2005/09/30 15:09:10 vuine Exp $ 
 */
package org.micropsi.eclipse.media;

import java.net.URL;
import java.util.Iterator;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.micropsi.media.VideoServer;
import org.micropsi.media.VideoServerRegistry;


public class CloneViewAction extends Action implements SelectionListener {
 
	private static int clonedID = 0;
	
	public static String getNextCloneID(){
		clonedID++;
		return "VideoViewClone"+clonedID;
	}
	
	private VideoView view;
	
	public CloneViewAction(final VideoView view) {
		super("Clone", AS_DROP_DOWN_MENU);
		this.view = view;
				
		setToolTipText("Clones the view");
		
		URL url = null;
		try {
			url = Platform.asLocalURL(MediaPlugin.getDefault().find(new Path("icons/clone.gif")));
		} catch (Exception e) {
		}

		setImageDescriptor(ImageDescriptor.createFromURL(url));		
		setMenuCreator(new IMenuCreator() {
			private Menu menu = null;
			
			public void dispose() {
				if (menu != null) {
					menu.dispose();
				}
			}

			public Menu getMenu(Menu parent) {
				if(menu != null) {
					menu.dispose();
				}
				menu = new Menu(parent); 
				fillMenu(menu);
		
				return menu;
			}

			public Menu getMenu(Control parent) {
				if(menu != null) {
					menu.dispose();
				}
				menu = new Menu(parent); 
				fillMenu(menu);
		
				return menu;
			}

			private void fillMenu(Menu menu) {
				for (Iterator<VideoServer> it = VideoServerRegistry.getInstance().getServers(); it.hasNext(); ) {
					VideoServer server = it.next();
					MenuItem item = new MenuItem(menu, SWT.CHECK);
					item.addSelectionListener(CloneViewAction.this);
					item.setText("Clone width "+server.getName());
					item.setData(server.getName());
				}
			}
		});
		
	} 
 
	public void widgetSelected(SelectionEvent e) {
		
		try {
			String viewID = getNextCloneID();
			view.getViewSite().getPage().showView("org.micropsi.eclipse.media.videoview",viewID,IWorkbenchPage.VIEW_CREATE);
			VideoView v = (VideoView)view.getViewSite().getPage().findViewReference("org.micropsi.eclipse.media.videoview",viewID).getView(false); 
			v.setServer((String)((MenuItem)e.getSource()).getData());
		} catch (PartInitException e1) {
			e1.printStackTrace();
		}
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
		
	}

}
