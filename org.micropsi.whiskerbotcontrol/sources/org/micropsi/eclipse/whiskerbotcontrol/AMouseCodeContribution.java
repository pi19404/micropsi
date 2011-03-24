/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.whiskerbotcontrol/sources/org/micropsi/eclipse/whiskerbotcontrol/AMouseCodeContribution.java,v 1.2 2006/04/26 18:22:47 dweiller Exp $ 
 */
package org.micropsi.eclipse.whiskerbotcontrol;

import org.eclipse.core.runtime.Platform;
import org.micropsi.eclipse.runtime.IRuntimeCodeContribution;
import org.osgi.framework.Bundle;




public class AMouseCodeContribution implements IRuntimeCodeContribution {

	public Bundle getBundle() {
		return Platform.getBundle("org.micropsi.whiskerbotcontrol");
	}

}
