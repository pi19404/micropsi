/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/MindNavigatorController.java,v 1.3 2005/07/12 12:53:54 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole;

import java.lang.ref.WeakReference;

import org.micropsi.eclipse.console.controller.AbstractController;
import org.micropsi.eclipse.model.net.AgentNetModelManager;


public class MindNavigatorController extends AbstractController {

	private static MindNavigatorController instance;
	
	public static MindNavigatorController getInstance() {
		if(instance == null) {
			instance = new MindNavigatorController();
			instance.setDataBase(AgentNetModelManager.getInstance().getNetModel());
		} 
		return instance;
	}

	public void refresh() {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((MindNavigatorView)referenced).refresh();
			}
		}	
	}

}
