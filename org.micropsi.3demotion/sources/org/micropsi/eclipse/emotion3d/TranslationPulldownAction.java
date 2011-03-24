/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.3demotion/sources/org/micropsi/eclipse/emotion3d/TranslationPulldownAction.java,v 1.1 2005/11/15 16:40:06 vuine Exp $ 
 */
package org.micropsi.eclipse.emotion3d;

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


public class TranslationPulldownAction extends Action implements SelectionListener {
 
	private Emotion3DView view;
	
	public TranslationPulldownAction(Emotion3DView view) {
		super("Translations", AS_DROP_DOWN_MENU);
		this.view = view;
				
		setToolTipText("Sets the face translation to use");
		URL url = null;
		try {
			url = Platform.asLocalURL(Emotion3DPlugin.getDefault().find(new Path("icons/translations.gif")));
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
				for (Iterator<IEmotionFaceTranslation> it = FaceTranslationRegistry.getInstance().getEmotionFaceTranslations().iterator();it.hasNext();) {
					IEmotionFaceTranslation translation = it.next();
					MenuItem item = new MenuItem(menu, SWT.CHECK);
					item.addSelectionListener(TranslationPulldownAction.this);
					item.setText(translation.getName());
				}
			}
		});

	} 
 
	public void widgetSelected(SelectionEvent e) {
		String name = ((MenuItem) (e.getSource())).getText();
		view.setTranslation(name);
	}

	public void widgetDefaultSelected(SelectionEvent e) {		
	}

}
