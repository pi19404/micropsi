/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.console/sources/org/micropsi/eclipse/console/adminperspective/LogController.java,v 1.3 2005/07/12 12:52:34 vuine Exp $ 
 */
package org.micropsi.eclipse.console.adminperspective;

import java.lang.ref.WeakReference;

import org.micropsi.eclipse.console.controller.AbstractController;

public class LogController extends AbstractController {

	private static LogController instance;
	
	public static LogController getInstance() {
		if(instance == null) {
			instance = new LogController();
		} 
		return instance;
	}
	
	public boolean toggleWatch() {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				return ((LogView)referenced).toggleWatch();
			}
		}
		return false;
	}

}
