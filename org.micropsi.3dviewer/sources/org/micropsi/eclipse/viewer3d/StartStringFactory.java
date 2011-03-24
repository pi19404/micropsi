/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.3dviewer/sources/org/micropsi/eclipse/viewer3d/StartStringFactory.java,v 1.8 2005/07/12 13:43:13 vuine Exp $ 
 */
package org.micropsi.eclipse.viewer3d;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;


public class StartStringFactory {

	public static String[] create3DStartCMD(IPreferenceStore prefs) {
		
		Path viewer = new Path("viewer\\bin");	
		String path;
		
		try {
			path = Platform.asLocalURL((Viewer3DPlugin.getDefault().find(viewer))).getFile();
			path = path.substring(1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add(path + "3dview2.exe ");
		parameters.add("-httpport 8080");
		parameters.add("-worldserver localhost");
		parameters.add("-connect true");
		parameters.add("-connectionmethod http");
		parameters.add("-mode spectator");
		parameters.add("-windowsize " + prefs.getString(IViewer3dPrefKeys.RESOLUTION));
			 
		if(prefs.getBoolean(IViewer3dPrefKeys.FULLSCREEN)) {
			parameters.add("-fullscreen true");	
		} else {
			parameters.add("-fullscreen false");	
		}
		
		if(prefs.getBoolean(IViewer3dPrefKeys.ALWAYSONTOP)) {
			parameters.add("-alwaysontop true");
		} else {
			parameters.add("-alwaysontop false");
		}
		
		String[] cmd = new String[parameters.size()];
		for(int i=0;i<parameters.size();i++) cmd[i] = parameters.get(i);
		return cmd;
	}
	
	public static String create3DStartString(IPreferenceStore prefs) {
		String[] cmd = create3DStartCMD(prefs);
		String toReturn = "";
		for(int i=0;i<cmd.length;i++) {
			toReturn += cmd[i] + " ";
		}
		return toReturn;
	}

}
