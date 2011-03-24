/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.worldconsole/sources/org/micropsi/eclipse/worldconsole/actions/ZoomPulldownAction.java,v 1.5 2005/06/01 19:42:22 fuessel Exp $ 
 */
package org.micropsi.eclipse.worldconsole.actions;

import java.net.URL;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.micropsi.eclipse.worldconsole.WorldMapView;
import org.micropsi.eclipse.worldconsole.WorldPlugin;


public class ZoomPulldownAction extends Action {
 
	private WorldMapView worldMapView = null;
	
	public ZoomPulldownAction(WorldMapView worldMapView) {
		super("Zoom", AS_DROP_DOWN_MENU);
		this.worldMapView = worldMapView;
				
		setToolTipText("Sets zoom factor");
		URL url = null;
		try {
			url = Platform.asLocalURL(WorldPlugin.getDefault().find(new Path("icons/zoom.gif")));
		} catch (Exception e) {
		}

		setImageDescriptor(ImageDescriptor.createFromURL(url));
		setMenuCreator(new IMenuCreator() {
			private Menu menu;
			
			public void dispose() {
				if (menu != null) {
					menu.dispose();
				}
			}

			public Menu getMenu(Menu parent) {
				if(menu == null) {
					menu = new Menu(parent); 
					fillMenu(menu);
				}
				selectCurrentZoomItem(menu);
		
				return menu;
			}

			public Menu getMenu(Control parent) {
				if(menu == null) {
					menu = new Menu(parent); 
					fillMenu(menu);
				}
				selectCurrentZoomItem(menu);
		
				return menu;
			}

			private void fillMenu(Menu menu) {
				MenuItem item = new MenuItem(menu,SWT.CASCADE | SWT.RADIO);
				item.setText("25%");
				item.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent arg0) {
						getWorldMapView().getWorldWidget().setZoom(25);						
					}
				});

				item = new MenuItem(menu,SWT.CASCADE | SWT.RADIO);
				item.setText("50%");
				item.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent arg0) {
						getWorldMapView().getWorldWidget().setZoom(50);
					}
				});
			
				item = new MenuItem(menu,SWT.CASCADE | SWT.RADIO);
				item.setText("75%");
				item.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent arg0) {
						getWorldMapView().getWorldWidget().setZoom(75);
					}
				});
			
				item = new MenuItem(menu,SWT.CASCADE | SWT.RADIO);
				item.setText("100%");
				item.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent arg0) {
						getWorldMapView().getWorldWidget().setZoom(100);
					}
				});
			
				item = new MenuItem(menu,SWT.CASCADE | SWT.RADIO);
				item.setText("125%");
				item.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent arg0) {
						getWorldMapView().getWorldWidget().setZoom(125);
					}
				});
			
				item = new MenuItem(menu,SWT.CASCADE | SWT.RADIO);
				item.setText("150%");
				item.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent arg0) {
						getWorldMapView().getWorldWidget().setZoom(150);
					}
				});
			
				item = new MenuItem(menu,SWT.CASCADE | SWT.RADIO);
				item.setText("200%");
				item.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent arg0) {
						getWorldMapView().getWorldWidget().setZoom(200);
					}
				});
			}
			
			private void selectCurrentZoomItem(Menu menu) {
				int zoomFactor = getWorldMapView().getWorldWidget().getZoom();
				MenuItem[] items = menu.getItems();
				for (int i = 0; i < items.length; i++) {
					items[i].setSelection(false);
				}
				
				switch (zoomFactor) {
					case 25: menu.getItem(0).setSelection(true); break;
					case 50: menu.getItem(1).setSelection(true); break;
					case 75: menu.getItem(2).setSelection(true); break;
					case 100: menu.getItem(3).setSelection(true); break;
					case 125: menu.getItem(4).setSelection(true); break;
					case 150: menu.getItem(5).setSelection(true); break;
					case 200: menu.getItem(6).setSelection(true); break;
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

}
