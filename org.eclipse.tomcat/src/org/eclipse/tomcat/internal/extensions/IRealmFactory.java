/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.tomcat.internal.extensions;

import org.apache.catalina.Realm;

/**
 * Factory used to generate an appropriate realm object for use during
 * authentication by the Tomcat server. Realm factories declared through the
 * org.eclipse.tomcat.realmfactory extention point must implement this
 * interface. <br>
 * Experimental: This extension point should be considered experimental. It may
 * change or be removed in the future releases.
 * 
 * @since 3.1
 */
public interface IRealmFactory {
	/**
	 * Creates a real for authentication by the Tomcat server.
	 * 
	 * @return an instance of a Realm.
	 */
	public Realm createRealm();
}
