/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.media/sources/org/micropsi/eclipse/media/ServerPulldownAction.java,v 1.4 2005/09/30 15:09:10 vuine Exp $ 
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
import org.micropsi.media.VideoServer;
import org.micropsi.media.VideoServerRegistry;


public class ServerPulldownAction extends Action implements SelectionListener {
 
	private VideoView view;
	
	public ServerPulldownAction(VideoView view) {
		super("Servers", AS_DROP_DOWN_MENU);
		this.view = view;
				
		setToolTipText("Sets the video server to use");
		URL url = null;
		try {
			url = Platform.asLocalURL(MediaPlugin.getDefault().find(new Path("icons/servers.gif")));
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
					item.addSelectionListener(ServerPulldownAction.this);
					item.setText(server.getName());
				}
			}
		});

	} 
 
	public void widgetSelected(SelectionEvent e) {
		String name = ((MenuItem) (e.getSource())).getText();
		view.setServer(name);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
