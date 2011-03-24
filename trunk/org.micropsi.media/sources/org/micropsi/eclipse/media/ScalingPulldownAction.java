/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.media/sources/org/micropsi/eclipse/media/ScalingPulldownAction.java,v 1.3 2005/10/06 19:27:56 vuine Exp $ 
 */
package org.micropsi.eclipse.media;

import java.net.URL;

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


public class ScalingPulldownAction extends Action implements SelectionListener {
 
	private VideoView view;
	
	public ScalingPulldownAction(VideoView view) {
		super("Scaling", AS_DROP_DOWN_MENU);
		this.view = view;
				
		setToolTipText("Sets video data scaling");
		URL url = null;
		try {
			url = Platform.asLocalURL(MediaPlugin.getDefault().find(new Path("icons/zoom.gif")));
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

				MenuItem item = new MenuItem(menu, SWT.CASCADE);
				item.addSelectionListener(ScalingPulldownAction.this);
				item.setText("0.5");

				item = new MenuItem(menu, SWT.CASCADE);
				item.addSelectionListener(ScalingPulldownAction.this);
				item.setText("0.75");
				
				item = new MenuItem(menu, SWT.CASCADE);
				item.addSelectionListener(ScalingPulldownAction.this);
				item.setText("1.0");
				
				item = new MenuItem(menu, SWT.CASCADE);
				item.addSelectionListener(ScalingPulldownAction.this);
				item.setText("1.5");

				item = new MenuItem(menu, SWT.CASCADE);
				item.addSelectionListener(ScalingPulldownAction.this);
				item.setText("2.0");

				item = new MenuItem(menu, SWT.CASCADE);
				item.addSelectionListener(ScalingPulldownAction.this);
				item.setText("2.5");
				
				item = new MenuItem(menu, SWT.CASCADE);
				item.addSelectionListener(ScalingPulldownAction.this);
				item.setText("3.0");
			}
		});

	} 
 
	public void widgetSelected(SelectionEvent e) {
		String newScale = ((MenuItem) (e.getSource())).getText();
		view.setScaling(Double.parseDouble(newScale));
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
