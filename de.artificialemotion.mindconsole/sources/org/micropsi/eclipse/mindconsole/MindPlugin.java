/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/MindPlugin.java,v 1.9 2005/04/23 13:39:54 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.eclipse.mindconsole.jdt.ModuleJavaManager;
import org.micropsi.eclipse.mindconsole.jdt.ScriptJavaManager;
import org.micropsi.eclipse.mindconsole.library.LibraryManager;
import org.micropsi.eclipse.runtime.EclipseBundleClassLoader;
import org.micropsi.eclipse.runtime.RuntimePlugin;
import org.osgi.framework.BundleException;


public class MindPlugin extends AbstractUIPlugin {

	private static MindPlugin instance;
	
	// for lazy initialization
	private static boolean initialized = false;

	public static MindPlugin getDefault() {
		
		if(!initialized) {
			initialized = true;
			
			ModuleJavaManager.ensureInitialization(new EclipseBundleClassLoader(),RuntimePlugin.getDefault().getShell());
			ScriptJavaManager.ensureInitialization(new EclipseBundleClassLoader(),RuntimePlugin.getDefault().getShell());
						
			try {
				
				IPath libraryPath = Platform.getPluginStateLocation(instance);
				libraryPath = libraryPath.append("/library");
				libraryPath.toFile().mkdirs();
				
				instance.manager = new LibraryManager(instance.getLogger(),libraryPath.toFile().getAbsolutePath());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}
			
		return instance;
	}

	public MindPlugin() throws BundleException {
		super();
		instance = this;
	}
	
	LibraryManager manager;
	
	public ConsoleFacadeIF getConsole() {
		return MindConsole.getInstance().getConsole();
	}
	
	public Logger getLogger() {
		return MindConsole.getInstance().getBserv().getLogger();
	}
	
	public String handleException(Throwable e) {
		return MindConsole.getInstance().getBserv().handleException(e);
	}

	public String getServerID() {
		return MindConsole.getInstance().getBserv().getServerID();
	}

	public LibraryManager getLibraryManager() {
		return manager;
	}

}
