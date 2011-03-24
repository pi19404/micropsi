/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.whiskerbotcontrol/sources/org/micropsi/eclipse/whiskerbotcontrol/RegisterOverlayAction.java,v 1.4 2006/04/26 18:22:47 dweiller Exp $ 
 */
package org.micropsi.eclipse.whiskerbotcontrol;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.micropsi.eclipse.media.VideoView;


public class RegisterOverlayAction implements IWorkbenchWindowActionDelegate {

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		
//		LocalAgentInfo.getInstance().forceAgentSelection("agent");
		VideoView.getInstance().addUninitializedRenderer("memoryoverlay",new MemoryOverlayRenderer());

	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
