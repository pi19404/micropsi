/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.3demotion/sources/org/micropsi/eclipse/emotion3d/Emotion3DPlugin.java,v 1.2 2005/11/15 16:40:06 vuine Exp $ 
 */
package org.micropsi.eclipse.emotion3d;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


public class Emotion3DPlugin extends AbstractUIPlugin {

	private static Emotion3DPlugin instance;
	
	// for lazy initialization
	private static boolean initialized = false;

	public Emotion3DPlugin() {
		instance = this;
	}

	public static Emotion3DPlugin getDefault() {
		if(!initialized) {
			initialized = true;	
		}
		return instance;
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initializeFaceTranslations();
	}
	
	private static void initializeFaceTranslations() {
		
		IExtension[] extensions = 
			Platform.getExtensionRegistry().getExtensionPoint("org.micropsi.3demotion.facetranslations").getExtensions();
				
		if(extensions == null) return;
		
		for(int i=0;i<extensions.length;i++) {
			IConfigurationElement[] cfg = extensions[i].getConfigurationElements();
					
			for(int j=0;j<cfg.length;j++) {					
				try {
					IEmotionFaceTranslation translation = 
						(IEmotionFaceTranslation) cfg[j].createExecutableExtension("class");

					FaceTranslationRegistry.getInstance().registerEmotionFaceTranslation(translation);
				
				} catch (CoreException e) {
					throw new RuntimeException(e);
				}		
			}
		}
	}
}
