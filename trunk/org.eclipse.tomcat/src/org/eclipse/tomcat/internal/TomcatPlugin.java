/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.tomcat.internal;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.osgi.framework.*;
import org.osgi.service.url.*;
/**
 */
public class TomcatPlugin extends Plugin implements BundleActivator {
	public final static String PLUGIN_ID = "org.eclipse.tomcat"; //$NON-NLS-1$

	// Preferences keys
	public final static String PREF_ACCEPT_COUNT = "acceptCount"; //$NON-NLS-1$
	public final static String PREF_MAX_PROCESSORS = "maxProcessors"; //$NON-NLS-1$
	public final static String PREF_MIN_PROCESSORS = "minProcessors"; //$NON-NLS-1$

	public final static String PREF_SSL_PORT = "sslPort"; //$NON-NLS-1$
	public final static String PREF_SSL_PROTOCOL = "sslProtocol"; //$NON-NLS-1$
	public final static String PREF_SSL_SCHEME = "sslScheme"; //$NON-NLS-1$
	public final static String PREF_SSL_ALGORITHM = "sslAlgorithm"; //$NON-NLS-1$
	public final static String PREF_KEY_STORE_FILE = "keyStoreFile"; //$NON-NLS-1$
	public final static String PREF_KEY_STORE_PASSWORD = "keyStorePassword"; //$NON-NLS-1$
	
	private static TomcatPlugin plugin;
//	private static BundleContext bundleContext;

	private ServiceRegistration jndiURLServiceRegistration;
	
	private TomcatAppServer appserver;

	void setAppserver(TomcatAppServer appserver) {
		this.appserver = appserver;
	}

	/**
	 */
	public TomcatPlugin() {
		super();
	}

	/**
	 * Logs an Error message with an exception. Note that the message should
	 * already be localized to proper locale. ie: TomcatResources.getString()
	 * should already have been called
	 */
	public static synchronized void logError(String message, Throwable ex) {
		if (message == null)
			message = ""; //$NON-NLS-1$
		Status errorStatus = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK,
				message, ex);
		TomcatPlugin.getDefault().getLog().log(errorStatus);
	}

	public static TomcatPlugin getDefault() {
		return plugin;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
//		bundleContext = context;

		registerJndiURL(context);
	}

	public void stop(BundleContext context) throws Exception {
		if(appserver!=null){
			try{
				appserver.stop();
			}catch(Exception e){
			}
		}
		unregisterJndiURL();

		plugin = null;
//		bundleContext = null;
		super.stop(context);
	}
	private void registerJndiURL(BundleContext context) {
		Hashtable properties = new Hashtable();
		properties.put(URLConstants.URL_HANDLER_PROTOCOL, new String[]{"jndi"}); //$NON-NLS-1$
		try {
			jndiURLServiceRegistration = context.registerService(
					URLStreamHandlerService.class.getName(),
					new JndiURLHandler(), properties);
		} catch (Error t) {
			logError(t.getMessage(), t);
			throw t;
		}
	}

	private void unregisterJndiURL() {
		if (jndiURLServiceRegistration != null) {
			jndiURLServiceRegistration.unregister();
		}
	}

}
