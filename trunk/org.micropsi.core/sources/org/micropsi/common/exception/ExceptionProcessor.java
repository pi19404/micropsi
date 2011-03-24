/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/exception/ExceptionProcessor.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.exception;

public final class ExceptionProcessor {

  private AbstractExceptionInfo info;
  
  public ExceptionProcessor(AbstractExceptionInfo info) {
  	this.info = info;
  }
  
  public String handleException(Throwable e) {
  	int id = 0;
	if(e instanceof MicropsiException) 
  		id = ((MicropsiException)e).id;

	return info.getHandlerForID(id).handleException(id,e,info);
  }

}
