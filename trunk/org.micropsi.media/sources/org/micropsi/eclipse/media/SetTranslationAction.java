/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.media/sources/org/micropsi/eclipse/media/SetTranslationAction.java,v 1.2 2005/08/15 00:46:29 vuine Exp $ 
 */
package org.micropsi.eclipse.media;

import java.net.URL;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;


public class SetTranslationAction extends Action {
 
	private VideoView view;
	
	public SetTranslationAction(VideoView view) {
		super("Translation", AS_PUSH_BUTTON);
		this.view = view;
				
		setToolTipText("Sets the translation for overlays");
		URL url = null;
		try {
			url = Platform.asLocalURL(MediaPlugin.getDefault().find(new Path("icons/translation.gif")));
		} catch (Exception e) {
		}

		setImageDescriptor(ImageDescriptor.createFromURL(url));
		
	} 
	
	public void run() {
		view.beginCalibration();
	}
 
}
