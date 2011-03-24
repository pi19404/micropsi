/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.whiskerbotcontrol/sources/org/micropsi/eclipse/whiskerbotcontrol/ResetRobotAction.java,v 1.2 2006/04/26 18:22:47 dweiller Exp $ 
 */
package org.micropsi.eclipse.whiskerbotcontrol;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.micropsi.comp.console.ConsoleFacadeIF;


public class ResetRobotAction implements IWorkbenchWindowActionDelegate {

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		
		IPreferenceStore preferences = PlatformUI.getPreferenceStore();
		
		String componentName = preferences.getString(RobotPreferences.CFG_KEY_ROBOTNAME);
		
		RobotConsole.getInstance().getConsole().sendCommand(
			ConsoleFacadeIF.ZERO_TOLERANCE,
			componentName,
			"reset",
			"",
			null,
			false
		);
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
