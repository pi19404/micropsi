/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/common/ComponentRunnerException.java,v 1.4 2005/07/11 22:14:09 vuine Exp $
 */
package org.micropsi.comp.common;

public class ComponentRunnerException extends Exception {

	static final long serialVersionUID = 7876754746547645L;
	
	public ComponentRunnerException(String message) {
		super(message);
	}
	
	public ComponentRunnerException(String message, Throwable cause) {
		super(message,cause);
	}
	
	
}
