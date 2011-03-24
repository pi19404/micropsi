/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/exception/ExceptionHandlerIF.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.exception;

public interface ExceptionHandlerIF {

  public String handleException(int id, Throwable e, AbstractExceptionInfo info);

}
