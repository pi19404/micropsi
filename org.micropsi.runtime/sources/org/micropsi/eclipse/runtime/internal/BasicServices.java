/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.runtime/sources/org/micropsi/eclipse/runtime/internal/BasicServices.java,v 1.2 2004/08/10 14:40:51 fuessel Exp $ 
 */
package org.micropsi.eclipse.runtime.internal;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.micropsi.common.exception.ExceptionProcessor;
import org.micropsi.eclipse.runtime.IBasicServices;
import org.micropsi.eclipse.runtime.dialogs.ExceptionDialog;


public class BasicServices implements IBasicServices {

	private Logger logger;
	private ExceptionProcessor exproc;
	private String serverID;
	
	public BasicServices(Logger logger, ExceptionProcessor exproc, String serverID) {
		this.logger = logger;
		this.exproc = exproc;
		this.serverID = serverID;
	}
	
	public Logger getLogger() {
		return logger;
	}
	
	public String handleException(Throwable e) {

		String message = exproc.handleException(e);

		Display current = Display.getCurrent();
		if(current == null) {
			return message;
		}
		
		new ExceptionDialog(
			current.getActiveShell(),
			message,
			e
		).open();

		return message;
	}

	public String getServerID() {
		return serverID;
	}
	

}
