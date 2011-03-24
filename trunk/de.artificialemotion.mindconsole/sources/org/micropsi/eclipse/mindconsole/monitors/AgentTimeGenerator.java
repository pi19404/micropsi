/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/monitors/AgentTimeGenerator.java,v 1.2 2004/08/10 14:35:49 fuessel Exp $ 
 */
package org.micropsi.eclipse.mindconsole.monitors;

import org.micropsi.eclipse.console.adminperspective.ParameterController;
import org.micropsi.nodenet.CycleObserverIF;


public class AgentTimeGenerator implements CycleObserverIF {

	public void startCycle(long arg0) {
	}

	public void endCycle(long cycle) {
		ParameterController.getInstance().tick(cycle);
	}
	
}
