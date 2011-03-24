/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.3demotion/sources/org/micropsi/eclipse/emotion3d/win32/LibLoaderClassLoader.java,v 1.1 2005/11/10 00:51:39 vuine Exp $ 
 */
package org.micropsi.eclipse.emotion3d.win32;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.micropsi.eclipse.emotion3d.Emotion3DPlugin;


public class LibLoaderClassLoader extends ClassLoader {

	String path;

	public LibLoaderClassLoader() throws IOException {
		Path viewer = new Path("viewer/bin");	
		path = Platform.asLocalURL(Emotion3DPlugin.getDefault().find(viewer)).getFile();
		
		String libLoaderFile = Platform.asLocalURL(Emotion3DPlugin.getDefault().find(new Path("LibLoader.class"))).getFile();
		File f = new File(libLoaderFile);
		byte[] content = new byte[(int)f.length()];
		FileInputStream finp = new FileInputStream(f);
		finp.read(content);
		
		Class libLoader = defineClass(null, content, 0, content.length);
		try {
			libLoader.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
					
	}
	
	public String findLibrary(String name)  {
		String full = path + File.separator + System.mapLibraryName(name);		
		if(!new File(full).exists()) return null; 		
		
		System.err.println(full);
		
		return full;
	}
	



}
