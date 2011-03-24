package org.micropsi.eclipse.runtime;

import java.net.URL;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.micropsi.comp.common.ComponentRunner;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.eclipse.runtime.dialogs.ExceptionDialog;
import org.micropsi.eclipse.runtime.dialogs.FatalDialog;
import org.micropsi.eclipse.runtime.internal.BasicServices;
import org.micropsi.eclipse.runtime.internal.IRuntimePref;
import org.micropsi.eclipse.runtime.internal.MicropsiEclipseAppServer;
import org.micropsi.eclipse.runtime.internal.RuntimeCodeContributionManager;
import org.micropsi.eclipse.runtime.internal.RuntimeFacade;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class RuntimePlugin extends AbstractUIPlugin {
	
	public static final String VERSION = "0_6_13";
	
	// The shared instance.
	private static RuntimePlugin plugin;
	
	// Preferences
	private IPreferenceStore preferences;
	
	// Resource bundle.
	private ResourceBundle resourceBundle;
	
	// The console wrapper
	private EclipseConsoleFunctionality runtimeSystemWrapper;
	
	// boolean value signaling if the runtime system cannot be reached / started
	private boolean giveup = false;
	
	// the location of the runtime system's configuration file
	private String config;
	
	// the id of the server the system is using
	private String serverID;
	
	// the central facility for Micropsi logging and exception handling, passed to
	// all plugins depending on the runtime plugin
	private IBasicServices basicservices;
	
	// all users of the runtime system (extensions at the runtimeUser extension point)
	private ArrayList<IRuntimeUser> runtimeUsers;
	
	private static boolean initialized = false;
		
	public void start(BundleContext context) throws Exception {
		plugin = this;		
	
		super.start(context);
						
		RuntimeCodeContributionManager.getInstance().registerRuntimeCodeContribution(
				new IRuntimeCodeContribution() {
					public Bundle getBundle() {
						return RuntimePlugin.this.getBundle();
					}
				});
		
		try {
			resourceBundle= ResourceBundle.getBundle("org.micropsi.runtime_"+VERSION);
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}

		preferences = PlatformUI.getPreferenceStore();;

		// read configuration and set defaults if no values are present
		config = preferences.getString(IRuntimePref.CFG_KEY_MICROPSICONFIG);
		if(config == null) config = "";

		serverID = preferences.getString(IRuntimePref.CFG_KEY_SERVER);
		if(serverID == null || serverID.equals("")) {
			serverID = "localserver";
			preferences.setDefault(IRuntimePref.CFG_KEY_SERVER,"localserver");
			preferences.setToDefault(IRuntimePref.CFG_KEY_SERVER);
		}
		
		// the default paths
		Path pConfig = new Path("config/config-full.xml");
		Path worldJar = new Path("lib/components/org.micropsi.world.jar");
		Path serverJar = new Path("lib/components/org.micropsi.server.jar");
		Path timerJar = new Path("lib/components/org.micropsi.timer.jar");
		Path agentJar = new Path("lib/components/org.micropsi.agentframework.jar");
		Path robotJar = new Path("lib/components/org.micropsi.robot.jar");
		
		// set default values if no value is present
		if(config.length() < 3) {
			preferences.setDefault(IRuntimePref.CFG_KEY_EXPOSESERVER,true);
			try {
				config = Platform.asLocalURL(find(pConfig)).getFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
			preferences.setDefault(IRuntimePref.CFG_KEY_MICROPSICONFIG, config);
			preferences.setToDefault(IRuntimePref.CFG_KEY_MICROPSICONFIG);
		}
		
		if(preferences.getString(IRuntimePref.CFG_KEY_USERNAME).length() < 2) {
			preferences.setDefault(IRuntimePref.CFG_KEY_USERNAME,new Object().hashCode());
			preferences.setToDefault(IRuntimePref.CFG_KEY_USERNAME);
		}
		
		String ext = preferences.getString(IRuntimePref.CFG_KEY_CPEXTENSION);
		if(ext == null) ext = "";
		String komma = "";
		if(ext.length() < 3) {
							
			ext = "";
			URL f = find(worldJar);
			if(f != null) {
				f = Platform.asLocalURL(f);
				ext += komma+f.getFile();
				komma = ",";
			}

			f = find(serverJar);
			if(f != null) {
				f = Platform.asLocalURL(f);
				ext += komma+f.getFile();
				komma = ",";
			}
						
			f = find(timerJar);
			if(f != null) {
				f = Platform.asLocalURL(f);
				ext += komma+f.getFile();
				komma = ",";
			}
			
			f = find(robotJar);
			if(f != null) {
				f = Platform.asLocalURL(f);
				ext += komma+f.getFile();
				komma = ",";
			}
			
			f = find(agentJar);
			if(f != null) {
				f = Platform.asLocalURL(f);
				ext += komma+f.getFile();
				komma = ",";
			}

			preferences.setDefault(IRuntimePref.CFG_KEY_CPEXTENSION, ext);
			preferences.setToDefault(IRuntimePref.CFG_KEY_CPEXTENSION);			
		}
				
		// now tell the extensions where to go
		this.runtimeUsers = new ArrayList<IRuntimeUser>();
	
		IExtension[] extensions = 
			Platform.getExtensionRegistry().getExtensionPoint("org.micropsi.runtime.runtimeUsers").getExtensions();
			
		if(extensions != null) {
			for(int i=0;i<extensions.length;i++) {
				IConfigurationElement[] cfg = extensions[i].getConfigurationElements();
											
				IRuntimeUser user = 
					(IRuntimeUser) cfg[i].createExecutableExtension("class");
	
				user.initialize(new RuntimeFacade());
					
				runtimeUsers.add(user);
			}
		}

		extensions = 
			Platform.getExtensionRegistry().getExtensionPoint("org.micropsi.runtime.runtimeCodeContributions").getExtensions();
			
		if(extensions != null) {
			for(int i=0;i<extensions.length;i++) {
				IConfigurationElement[] cfg = extensions[i].getConfigurationElements();
											
				IRuntimeCodeContribution contribution = 
					(IRuntimeCodeContribution) cfg[0].createExecutableExtension("class");

				RuntimeCodeContributionManager.getInstance().registerRuntimeCodeContribution(contribution);					
			}
		}
		
		
	}
	
	private void startRuntimeSystem() {

		if((runtimeSystemWrapper == null) && (!giveup)) {
			try {
				String home = Platform.asLocalURL(find(new Path("."))).getFile(); 
								
				home = home.substring(0,home.length()-1);
				
				String workspace = ResourcesPlugin.getWorkspace().getRoot().getRawLocation().toOSString();
								
				ComponentRunner.setGlobalVariable("MICROPSI_HOME", home);
				ComponentRunner.setGlobalVariable("WORKSPACE", workspace);
				ComponentRunner.setGlobalVariable("USERNAME",preferences.getString(IRuntimePref.CFG_KEY_USERNAME));
				
				EclipseBundleClassLoader ecbcl = new EclipseBundleClassLoader(); 
				
				runtimeSystemWrapper = EclipseConsoleFunctionality.getInstance(
					config,
					ecbcl,
					preferences,
					getShell()
				);
				
				basicservices = new BasicServices(
					runtimeSystemWrapper.getLogger(),
					runtimeSystemWrapper.getExproc(),
					serverID
				);
				
				MicropsiEclipseAppServer.stop(basicservices);
				Path webapp = new Path("webapp");
				String fullpath = Platform.asLocalURL(find(webapp)).getFile();
				
				int port = preferences.getInt(IRuntimePref.CFG_KEY_SERVERPORT);
				if(port <= 0) {
					port = 8080;
					preferences.setValue(IRuntimePref.CFG_KEY_SERVERPORT, port);
				}
				boolean startAppServer = preferences.getBoolean(IRuntimePref.CFG_KEY_EXPOSESERVER);
				if(startAppServer) {
					basicservices.getLogger().info("Starting web application server at port "+port);
					MicropsiEclipseAppServer.start(fullpath,port,ecbcl,basicservices);
				}
				
			} catch (Exception e) {
				
				giveup = true;
				
				new ExceptionDialog(
					getShell(),
					"Exception during Micropsi system startup: "+e.getMessage(),
					e
				).open();
				
				FatalDialog dlg = new FatalDialog(
					getShell(), 
					"Exception during Micropsi system startup: "+e.getMessage(),
					getDefault().getWorkbench()
				);
				dlg.open();
			}

		}       
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {	
		if(runtimeSystemWrapper != null) {
			ComponentRunner.getInstance().shutdown();
		}
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static synchronized RuntimePlugin getDefault() {
		if(!initialized) {
			initialized = true;
			plugin.startRuntimeSystem();
		}
		return plugin;
	}
	
	/**
	 * This returns a shell. Always. 
	 * 
	 * @return
	 */
	public Shell getShell() {
		Display display = getWorkbench().getDisplay();
		Shell toReturn = display.getActiveShell();
		if(toReturn == null) {
			toReturn = new Shell(display);
			toReturn.forceActive();
		}
		return toReturn;
	}
	
	public Display getDisplay() {
		return getWorkbench().getDisplay();
	}
	
	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle= RuntimePlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public IBasicServices getBasicServices() {
		return basicservices;
	}

	public EclipseConsoleFunctionality getComponent() {
		return runtimeSystemWrapper;
	}
	
	public ConsoleFacadeIF getConsole() {
		return runtimeSystemWrapper.getConsole(); 
	}

	public String getServerID() {
		return preferences.getString(IRuntimePref.CFG_KEY_MICROPSICONFIG);
	}
}
