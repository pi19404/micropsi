/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/LinkageEditController.java,v 1.3 2005/07/12 12:53:54 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole;

import java.lang.ref.WeakReference;

import org.micropsi.eclipse.console.controller.AbstractController;
import org.micropsi.nodenet.Link;


public class LinkageEditController extends AbstractController {

	private static LinkageEditController instance;
	
	public static LinkageEditController getInstance() {
		if(instance == null) instance = new LinkageEditController();
		return instance;
	}

	public void selectLink(Link link) {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((LinkageEditView)referenced).selectLink(link);
			}
		}		
	}

}
