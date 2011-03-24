/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/log/LoggingExceptionHandler.java,v 1.2 2004/08/10 14:38:18 fuessel Exp $
 */
package org.micropsi.common.log;

import org.apache.log4j.Logger;

import org.micropsi.common.exception.AbstractExceptionInfo;
import org.micropsi.common.exception.ExceptionAnalyzer;
import org.micropsi.common.exception.ExceptionHandlerIF;

public class LoggingExceptionHandler implements ExceptionHandlerIF {

	private Logger logger;
	
	public LoggingExceptionHandler(Logger logger) {
		this.logger = logger;
	}

	public String handleException(int id, Throwable e, AbstractExceptionInfo info) {
		logger.error(ExceptionAnalyzer.getFullExceptionAnalysis(e,info));
		return ExceptionAnalyzer.getShortExceptionAnalysis(e,info);
	}

}
