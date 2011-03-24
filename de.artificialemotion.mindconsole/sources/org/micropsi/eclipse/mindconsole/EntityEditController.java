/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/EntityEditController.java,v 1.3 2005/07/12 12:53:54 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole;

import java.lang.ref.WeakReference;

import org.micropsi.eclipse.console.controller.AbstractController;
import org.micropsi.nodenet.Gate;


public class EntityEditController extends AbstractController {

	private static EntityEditController instance;
	
	public static EntityEditController getInstance() {
		if(instance == null) instance = new EntityEditController();
		return instance;
	}

	public void selectGate(Gate gate) {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((EntityEditView)referenced).selectGate(gate);
			}
		}	
	}

}
