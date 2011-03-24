/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/IncomingLinksController.java,v 1.2 2004/08/10 14:35:49 fuessel Exp $ 
 */
package org.micropsi.eclipse.mindconsole;

import org.micropsi.eclipse.console.controller.AbstractController;

public class IncomingLinksController extends AbstractController {

	private static IncomingLinksController instance;
	
	public static IncomingLinksController getInstance() {
		if(instance == null) instance = new IncomingLinksController();
		return instance;
	}

}
