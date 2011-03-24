/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.console/sources/org/micropsi/eclipse/console/internal/MonitorProviderManager.java,v 1.4 2005/07/12 12:52:34 vuine Exp $ 
 */
package org.micropsi.eclipse.console.internal;

import java.util.ArrayList;
import java.util.Collection;

import org.micropsi.eclipse.console.IMonitorProvider;



public class MonitorProviderManager {

	private static MonitorProviderManager instance;
	
	public static MonitorProviderManager getInstance() {
		if(instance == null) instance = new MonitorProviderManager();
		return instance;
	}
	
	ArrayList<MonitorProviderWrapper> providers = new ArrayList<MonitorProviderWrapper>();
	
	public void addProvider(MonitorProviderWrapper p) {
		providers.add(p);
	}

	public Collection getProviders() {
		return providers;
	}

	public IMonitorProvider getProvider(String prov) {
		for(int i=0;i<providers.size();i++) {
			if(providers.get(i).getID().equals(prov))
				return providers.get(i);
		}
		return null;
	}

	/**
	 * 
	 */
	public void removeAllProviders() {
		providers.clear();
	}

}
