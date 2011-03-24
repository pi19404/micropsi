/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.worldconsole/sources/org/micropsi/eclipse/worldconsole/actions/EditModeChangeAction.java,v 1.1 2005/06/01 19:42:22 fuessel Exp $ 
 */
package org.micropsi.eclipse.worldconsole.actions;

import java.net.URL;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.micropsi.eclipse.worldconsole.WorldMapView;
import org.micropsi.eclipse.worldconsole.WorldPlugin;

public class EditModeChangeAction extends Action {
 
	private WorldMapView worldMapView = null;
	
	public EditModeChangeAction(WorldMapView worldMapView, String actionName, String toolTip, String imagePath) {
		super(actionName, AS_RADIO_BUTTON);
		this.worldMapView = worldMapView;
		
		setToolTipText(toolTip);
		URL url = null;
		try {
			url = Platform.asLocalURL(WorldPlugin.getDefault().find(new Path(imagePath))); 
		} catch (Exception e) {
		}
 
		setImageDescriptor(ImageDescriptor.createFromURL(url));
	} 
 
	/**
	 * @return
	 */
	public WorldMapView getWorldMapView() {
		return worldMapView;
	}
}
