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

import org.eclipse.osgi.util.NLS;

public final class TomcatResources extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.tomcat.internal.TomcatResources";//$NON-NLS-1$

	private TomcatResources() {
		// Do not instantiate
	}

	public static String LocalConnectionTest_cannotGetLocalhostName;
	public static String TomcatAppServer_httpConnectorRemove;
	public static String TomcatAppServer_sslConnectorRemove;
	public static String TomcatAppServer_engineRemove;
	public static String TomcatAppServer_embeddedStop;
	public static String TomcatAppServer_addingWebapp;
	public static String TomcatAppServer_start;
	public static String TomcatAppServer_start_CannotObtainPort;
	public static String noDocument;
	public static String TomcatAppServer_missingFactoryElement;
	public static String TomcatAppServer_multipleFactoryElements;
	public static String TomcatAppServer_missingRealmExtension;
	public static String TomcatAppServer_multipleRealmExtensions;
	public static String TomcatAppServer_missingRealmExtensionPoint;
	public static String TomcatAppServer_getRealmFactoryFailed;

	static {
		NLS.initializeMessages(BUNDLE_NAME, TomcatResources.class);
	}
}