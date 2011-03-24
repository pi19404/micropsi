/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/nodenet/scripting/MindScriptConfigurator.java,v 1.1 2004/11/23 01:37:00 vuine Exp $ 
 */
package org.micropsi.nodenet.scripting;

import org.micropsi.nodenet.NetFacadeIF;


public final class MindScriptConfigurator {

	public static void configureMindScript(MindScript script, NetFacadeIF net) {
		script.setup(net);
	}
	
}
