/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.worldconsole/sources/org/micropsi/eclipse/worldconsole/WorldPlugin.java,v 1.8 2005/11/19 12:04:34 dietzsch Exp $ 
 */
package org.micropsi.eclipse.worldconsole;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.comp.console.worldconsole.IOverlayRenderer;
import org.micropsi.comp.console.worldconsole.OverlayRendererDescriptor;


public class WorldPlugin extends AbstractUIPlugin {

	private static WorldPlugin instance;
	
	// for lazy initialization
	private static boolean initialized = false;

	public static WorldPlugin getDefault() {
		if(!initialized) {
			
			IExtension[] extensions = 
				Platform.getExtensionRegistry().getExtensionPoint("org.micropsi.worldconsole.overlays").getExtensions();
				
			if(extensions != null) {
				for(int i=0;i<extensions.length;i++) {
					IConfigurationElement[] cfg = extensions[i].getConfigurationElements();
					
					int zOrder = -1;
					try {
						zOrder = Integer.parseInt(cfg[0].getAttribute("zOrder"));
					} catch (Exception e) {
						// ok.
					}
					
					try {
						OverlayRendererDescriptor descriptor = new OverlayRendererDescriptor(
							(IOverlayRenderer)cfg[0].createExecutableExtension("class"),
							cfg[0].getAttribute("name"),
							zOrder,
							true,
							instance.getConsole()
						);
												
						instance.overlayDescriptors.add(descriptor);
					} catch (CoreException e) {
						instance.getLogger().error("Unable to load overlay extension "+cfg[0].getAttribute("name"),e);
					}
				}
			}		
			
			initialized = true;	
		}
		
		return instance;
	}

	public WorldPlugin() {
		instance = this;
	}
	
	private ArrayList<OverlayRendererDescriptor> overlayDescriptors = new ArrayList<OverlayRendererDescriptor>();
		
	public ConsoleFacadeIF getConsole() {
		return WorldConsole.getInstance().getConsole();
	}
	
	public Logger getLogger() {
		return WorldConsole.getInstance().getBserv().getLogger();
	}
	
	public String handleException(Throwable e) {
		return WorldConsole.getInstance().getBserv().handleException(e);
	}
	
	public List getOverlayDescriptors() {
		return overlayDescriptors;
	}


}
