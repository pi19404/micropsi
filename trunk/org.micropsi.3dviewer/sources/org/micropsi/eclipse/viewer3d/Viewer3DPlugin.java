/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.3dviewer/sources/org/micropsi/eclipse/viewer3d/Viewer3DPlugin.java,v 1.1 2004/06/15 18:40:34 vuine Exp $ 
 */
package org.micropsi.eclipse.viewer3d;

import org.eclipse.ui.plugin.AbstractUIPlugin;


public class Viewer3DPlugin extends AbstractUIPlugin {

	private static Viewer3DPlugin instance;
	
	// for lazy initialization
	private static boolean initialized = false;

	public Viewer3DPlugin() {
		instance = this;
	}

	public static Viewer3DPlugin getDefault() {
		if(!initialized) {
			initialized = true;	
		}
		return instance;
	}
		
}
