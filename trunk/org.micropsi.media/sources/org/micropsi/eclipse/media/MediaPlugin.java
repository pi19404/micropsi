package org.micropsi.eclipse.media;

import java.io.ByteArrayInputStream;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.micropsi.media.MediaServerException;
import org.micropsi.media.VideoServerRegistry;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MediaPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static MediaPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public MediaPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
				
		String descriptor = PlatformUI.getPreferenceStore().getString(VideoServerPreferences.CFG_KEY_SERVERLIST);
		if(descriptor == null) {
			return;
		}
		
		String[] servers = descriptor.split("\n");
		for(int i=0;i<servers.length;i++) {

			String propString = servers[i].replace(",","\n");
			final Properties props = new Properties();
			props.load(new ByteArrayInputStream(propString.getBytes()));
			
			final String name = props.getProperty("name");
			if(name == null) {
				continue;
			}
			
			Runnable starter = new Runnable() {
				public void run() {
					try {
						VideoServerRegistry.getInstance().createVideoServer(name,props,null);
					} catch (MediaServerException e) {
						Status status = new Status(
							IStatus.WARNING,
							MediaPlugin.this.getBundle().getSymbolicName(),
							-1,
							"Video server not started",
							e
						);
						MediaPlugin.this.getLog().log(status);
					}
				}
			};
			new Thread(starter).start();
			
		}
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		VideoServerRegistry.getInstance().shutdown();
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static MediaPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.micropsi.media", path);
	}
}
