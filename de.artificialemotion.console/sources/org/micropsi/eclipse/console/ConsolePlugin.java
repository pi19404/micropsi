package org.micropsi.eclipse.console;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.eclipse.console.internal.BasicServicesProxy;
import org.micropsi.eclipse.console.internal.ConsoleProxy;
import org.micropsi.eclipse.console.internal.MonitorProviderManager;
import org.micropsi.eclipse.console.internal.MonitorProviderWrapper;
import org.micropsi.eclipse.console.internal.MonitorRegistryProxy;
import org.osgi.framework.BundleContext;

public class ConsolePlugin extends AbstractUIPlugin {
	
	private static ConsolePlugin instance;
	
	public static ConsolePlugin getDefault() {
		return instance;
	}

	public ConsolePlugin() {
		super();
		
		instance = this;
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
		initializeConsoles();
		initializeMonitorProviders();
	}

	private void initializeMonitorProviders() {
		
		IExtension[] extensions = 
			Platform.getExtensionRegistry().getExtensionPoint("org.micropsi.console.monitorProviders").getExtensions();
				
		if(extensions == null) return;
		
		for(int i=0;i<extensions.length;i++) {
			IConfigurationElement[] cfg = extensions[i].getConfigurationElements();
					
			for(int j=0;j<cfg.length;j++) {					
				try {
					IMonitorProvider provider = 
						(IMonitorProvider) cfg[j].createExecutableExtension("class");

					MonitorProviderWrapper w = new MonitorProviderWrapper(provider,cfg[j].getAttribute("id"));
					MonitorProviderManager.getInstance().addProvider(w);
					w.initialize(new MonitorRegistryProxy(w.getID()));
				
				} catch (CoreException e) {
					throw new RuntimeException(e);
				}		
			}
		}			
	}
	
	private static void initializeConsoles() {
						
		IExtension[] extensions = 
			Platform.getExtensionRegistry().getExtensionPoint("org.micropsi.console.consoles").getExtensions();
				
		if(extensions == null) return;
		
		for(int i=0;i<extensions.length;i++) {
			IConfigurationElement[] cfg = extensions[i].getConfigurationElements();
					
			for(int j=0;j<cfg.length;j++) {					
				try {
					IConsolePart console = 
						(IConsolePart) cfg[j].createExecutableExtension("class");

					console.initialize(new ConsoleProxy(), new BasicServicesProxy());
				
				} catch (CoreException e) {
					throw new RuntimeException(e);
				}		
			}
		}
	}
	
	public ConsoleFacadeIF getConsole() {
		return ConsoleRuntimeUser.getInstance().getConsole();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		ConsoleRuntimeUser.getInstance().getConsole().unsubscribeAll();
		super.stop(context);
	}
}
