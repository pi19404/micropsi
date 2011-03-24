/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/model/net/LoadDetector.java,v 1.6 2005/05/10 18:37:04 vuine Exp $ 
 */
package org.micropsi.eclipse.model.net;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.micropsi.eclipse.common.ProgressDialog;
import org.micropsi.eclipse.console.adminperspective.ParameterController;
import org.micropsi.eclipse.mindconsole.EntityEditController;
import org.micropsi.eclipse.mindconsole.LinkageEditController;
import org.micropsi.eclipse.mindconsole.MindEditController;
import org.micropsi.eclipse.mindconsole.MindNavigatorController;
import org.micropsi.eclipse.mindconsole.jdt.ModuleJavaManager;

public class LoadDetector extends ProgressDialog {

	public LoadDetector(Shell parentShell) {
		super("Loading net...",parentShell);
	}

	public void beginTask(String message) {
		if(taskStack.isEmpty()) {
			Display.findDisplay(uiThread).syncExec(new Runnable() {
				public void run() {
					MindEditController.getInstance().setDataBase(null);
					EntityEditController.getInstance().setDataBase(null);
					LinkageEditController.getInstance().setDataBase(null);
					ModuleJavaManager.getInstance().reinitialize();
				}
			});
		}
		super.beginTask(message);
	}

	public void endTask() {
		super.endTask();
		
		if(taskStack.isEmpty()) {
			Display.findDisplay(uiThread).asyncExec(new Runnable() {
				public void run() {
					AgentNetModelManager.getInstance().reinitialize();
					MindEditController.getInstance().setDataBase(AgentNetModelManager.getInstance().getNetModel());
					MindNavigatorController.getInstance().setDataBase(AgentNetModelManager.getInstance().getNetModel());
					EntityEditController.getInstance().setDataBase(AgentNetModelManager.getInstance().getNetModel());
					LinkageEditController.getInstance().setDataBase(AgentNetModelManager.getInstance().getNetModel());
					
					ParameterController.getInstance().loadState(AgentNetModelManager.getInstance().getNetModel().getLastLoadedState());
				}
			});
		}
	}

}
