/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/NetCodeContribution.java,v 1.2 2004/09/02 15:59:24 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole;

import org.eclipse.core.runtime.Platform;
import org.micropsi.eclipse.runtime.IRuntimeCodeContribution;
import org.osgi.framework.Bundle;



public class NetCodeContribution implements IRuntimeCodeContribution {

	public Bundle getBundle() {
		return Platform.getBundle("org.micropsi.mindconsole");
	}

}
