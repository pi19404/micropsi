/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.worldconsole/sources/org/micropsi/eclipse/worldconsole/actions/OverlayPulldownAction.java,v 1.1 2005/06/22 18:07:28 fuessel Exp $ 
 */
package org.micropsi.eclipse.worldconsole.actions;

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
import org.micropsi.comp.console.worldconsole.WorldRenderer;
import org.micropsi.comp.console.worldconsole.WorldRenderer.OverlayInfo;
import org.micropsi.eclipse.worldconsole.WorldMapView;
import org.micropsi.eclipse.worldconsole.WorldPlugin;


public class OverlayPulldownAction extends Action implements SelectionListener {
 
	private WorldMapView worldMapView = null;
	
	public OverlayPulldownAction(WorldMapView worldMapView) {
		super("Overlays", AS_DROP_DOWN_MENU);
		this.worldMapView = worldMapView;
				
		setToolTipText("Enables or disables overlays");
		URL url = null;
		try {
			url = Platform.asLocalURL(WorldPlugin.getDefault().find(new Path("icons/overlay.gif")));
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
				for (Iterator it = getWorldMapView().getWorldWidget().getRenderer().getOverlayInfoList().iterator(); it.hasNext(); ) {
					WorldRenderer.OverlayInfo overlayInfo = (OverlayInfo) it.next();
					MenuItem item = new MenuItem(menu, SWT.CHECK);
					item.setText(overlayInfo.getName());
					item.setSelection(overlayInfo.isEnabled());
					item.addSelectionListener(OverlayPulldownAction.this);
				}
			}
		});

	} 
 
	/**
	 * @return
	 */
	public WorldMapView getWorldMapView() {
		return worldMapView;
	}

	public void widgetSelected(SelectionEvent e) {
		String name = ((MenuItem) (e.getSource())).getText();
		boolean enabled = ((MenuItem) (e.getSource())).getSelection();
		getWorldMapView().getWorldWidget().setOverlayEnabled(name, enabled);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
