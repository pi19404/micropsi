/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.3demotion/sources/org/micropsi/eclipse/emotion3d/StartStringFactory.java,v 1.2 2005/11/15 16:40:06 vuine Exp $ 
 */
package org.micropsi.eclipse.emotion3d;

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
			path = Platform.asLocalURL((Emotion3DPlugin.getDefault().find(viewer))).getFile();
			path = path.substring(1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add(path + "3demotion.exe ");
		parameters.add("-httpport 8080");
		parameters.add("-worldserver localhost");
		parameters.add("-connect true");
		parameters.add("-connectionmethod http");
		parameters.add("-mode spectator");
		parameters.add("-windowsize " + prefs.getString(IEmotion3dPrefKeys.RESOLUTION));
			 
		parameters.add("-fullscreen false");	
		
		parameters.add("-alwaysontop false");
		
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
