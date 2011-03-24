/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/exception/AbstractExceptionInfo.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.exception;

public abstract class AbstractExceptionInfo {
		
	public abstract String getTextForID(int id);
	
	public abstract int getSeverityForID(int id);
	
	public abstract ExceptionHandlerIF getHandlerForID(int id);
	
	public abstract Object getParameterForID(int id);

}
