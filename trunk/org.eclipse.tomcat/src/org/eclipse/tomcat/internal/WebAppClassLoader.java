/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.tomcat.internal;

import java.net.*;

import org.eclipse.help.internal.appserver.*;

/**
 * A class loader that combines a plugin class loader with the tomcat class
 * loader
 */
public class WebAppClassLoader extends URLClassLoader {
	private ClassLoader pluginLoader;
	private PluginClassLoaderWrapper tomcatPluginLoader;

	public WebAppClassLoader(ClassLoader pluginLoader) {
		super(new URL[0]);
		this.pluginLoader = pluginLoader;
		this.tomcatPluginLoader = new PluginClassLoaderWrapper(
				TomcatPlugin.PLUGIN_ID);
	}

	public Class loadClass(String className) throws ClassNotFoundException {
		// First check tomcat plugin loader, then the webapp plugin loader
		Class c = null;
		try {
			c = tomcatPluginLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			c = pluginLoader.loadClass(className);
		} finally {
		}
		return c;
	}

	public URL getResource(String resName) {
		// First check the plugin loader, then current loader
		URL u = pluginLoader.getResource(resName);
		if (u == null)
			return tomcatPluginLoader.getResource(resName);
		return u;
	}

	/**
	 * This is a workaround for the jsp compiler that needs to know the
	 * classpath. NOTE: for now, assume that the web app plugin requires the
	 * tomcat plugin
	 */
	public URL[] getURLs() {
		URL[] pluginLoaderURLs;
		if (pluginLoader instanceof URLClassLoader)
			pluginLoaderURLs = ((URLClassLoader) pluginLoader).getURLs();
		else
			pluginLoaderURLs = new URL[0];

		URL[] tomcatPluginLoaderURLs = tomcatPluginLoader.getURLs();

		URL[] urls = new URL[pluginLoaderURLs.length
				+ tomcatPluginLoaderURLs.length];

		System.arraycopy(pluginLoaderURLs, 0, urls, 0, pluginLoaderURLs.length);
		System.arraycopy(tomcatPluginLoaderURLs, 0, urls,
				pluginLoaderURLs.length, tomcatPluginLoaderURLs.length);
		return urls;
	}
}
