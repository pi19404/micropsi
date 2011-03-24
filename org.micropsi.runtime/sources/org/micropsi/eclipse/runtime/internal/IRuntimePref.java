/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.runtime/sources/org/micropsi/eclipse/runtime/internal/IRuntimePref.java,v 1.2 2004/08/10 14:40:51 fuessel Exp $ 
 */
package org.micropsi.eclipse.runtime.internal;

import org.micropsi.eclipse.runtime.RuntimePlugin;


public interface IRuntimePref {

	public String CFG_KEY_SERVERPORT = "org.micropsi.runtime.serverport";

	public String CFG_KEY_EXPOSESERVER = "org.micropsi.runtime.exposeserver";

	public String CFG_KEY_CPEXTENSION = "org.micropsi.runtime.cpextension_"+RuntimePlugin.VERSION;

	public String CFG_KEY_USERNAME = "org.micropsi.runtime.username";

	public String CFG_KEY_SERVER = "org.micropsi.runtime.server";

	public String CFG_KEY_MICROPSICONFIG = "org.micropsi.runtime.micropsiconfig_"+RuntimePlugin.VERSION;

}
