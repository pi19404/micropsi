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

import org.apache.naming.resources.*;
import org.osgi.service.url.*;

public class JndiURLHandler extends DirContextURLStreamHandler
		implements
			URLStreamHandlerService {
	public URLConnection openConnection(URL u) throws java.io.IOException {
		return super.openConnection(u);
	}

	/**
	 * The <tt>URLStreamHandlerSetter</tt> object passed to the parseURL
	 * method.
	 */
	protected URLStreamHandlerSetter realHandler;

	/**
	 * Parse a URL using the <tt>URLStreamHandlerSetter</tt> object. This
	 * method sets the <tt>realHandler</tt> field with the specified
	 * <tt>URLStreamHandlerSetter</tt> object and then calls
	 * <tt>parseURL(URL,String,int,int)</tt>.
	 * 
	 * @param realHandler
	 *            The object on which the <tt>setURL</tt> method must be
	 *            invoked for the specified URL.
	 * @see "java.net.URLStreamHandler.parseURL"
	 */
	public void parseURL(URLStreamHandlerSetter realHandler, URL u,
			String spec, int start, int limit) {
		this.realHandler = realHandler;
		parseURL(u, spec, start, limit);
	}

	/**
	 * This method calls <tt>super.toExternalForm</tt>.
	 * 
	 * @see "java.net.URLStreamHandler.toExternalForm"
	 */
	public String toExternalForm(URL u) {
		return super.toExternalForm(u);
	}

	/**
	 * This method calls <tt>super.equals(URL,URL)</tt>.
	 * 
	 * @see "java.net.URLStreamHandler.equals(URL,URL)"
	 */
	public boolean equals(URL u1, URL u2) {
		return super.equals(u1, u2);
	}

	/**
	 * This method calls <tt>super.getDefaultPort</tt>.
	 * 
	 * @see "java.net.URLStreamHandler.getDefaultPort"
	 */
	public int getDefaultPort() {
		return super.getDefaultPort();
	}

	/**
	 * This method calls <tt>super.getHostAddress</tt>.
	 * 
	 * @see "java.net.URLStreamHandler.getHostAddress"
	 */
	public InetAddress getHostAddress(URL u) {
		return super.getHostAddress(u);
	}

	/**
	 * This method calls <tt>super.hashCode(URL)</tt>.
	 * 
	 * @see "java.net.URLStreamHandler.hashCode(URL)"
	 */
	public int hashCode(URL u) {
		return super.hashCode(u);
	}

	/**
	 * This method calls <tt>super.hostsEqual</tt>.
	 * 
	 * @see "java.net.URLStreamHandler.hostsEqual"
	 */
	public boolean hostsEqual(URL u1, URL u2) {
		return super.hostsEqual(u1, u2);
	}

	/**
	 * This method calls <tt>super.sameFile</tt>.
	 * 
	 * @see "java.net.URLStreamHandler.sameFile"
	 */
	public boolean sameFile(URL u1, URL u2) {
		return super.sameFile(u1, u2);
	}

	/**
	 * This method calls
	 * <tt>realHandler.setURL(URL,String,String,int,String,String)</tt>.
	 * 
	 * @see "java.net.URLStreamHandler.setURL(URL,String,String,int,String,String)"
	 * @deprecated This method is only for compatibility with handlers written
	 *             for JDK 1.1.
	 */
	protected void setURL(URL u, String proto, String host, int port,
			String file, String ref) {
		realHandler.setURL(u, proto, host, port, file, ref);
	}

	/**
	 * This method calls
	 * <tt>realHandler.setURL(URL,String,String,int,String,String,String,String)</tt>.
	 * 
	 * @see "java.net.URLStreamHandler.setURL(URL,String,String,int,String,String,String,String)"
	 */
	protected void setURL(URL u, String proto, String host, int port,
			String auth, String user, String path, String query, String ref) {
		realHandler.setURL(u, proto, host, port, auth, user, path, query, ref);
	}
}
