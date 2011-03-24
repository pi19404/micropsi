/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.console/sources/org/micropsi/eclipse/console/adminperspective/LogViewAppender.java,v 1.3 2005/10/05 22:37:01 vuine Exp $ 
 */
package org.micropsi.eclipse.console.adminperspective;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;


public class LogViewAppender extends AppenderSkeleton {
	
	public static final String LOGVIEWAPPENDER = "eclipse_logview_appender"; 
	
	public LogViewAppender() {
		name = LOGVIEWAPPENDER;
	}

	/**
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	protected void append(LoggingEvent data) {
		try {
			LogController.getInstance().setData(data);
		} catch (Throwable e) {
			e.printStackTrace();
			System.err.println("Couldn't append to log view: ");
			System.err.println(data.getRenderedMessage());
		}
	}

	/**
	 * @see org.apache.log4j.AppenderSkeleton#requiresLayout()
	 */
	public boolean requiresLayout() {
		return false;
	}

	/**
	 * @see org.apache.log4j.AppenderSkeleton#close()
	 */
	public void close() {
	}

}
