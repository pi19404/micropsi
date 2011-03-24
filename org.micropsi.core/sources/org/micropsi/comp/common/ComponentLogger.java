/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/common/ComponentLogger.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $ 
 */
package org.micropsi.comp.common;

import java.util.Enumeration;
import java.util.ResourceBundle;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;

/**
 *
 * This is a proxy logger that allows components to rename without
 * explicitly updating all logger references that are around. This
 * class is a bit of a hack, but log4j has clearly a problem with
 * logger renaming, an I didn't find a factory or some other clean
 * solution to install proxy loggers.
 *  
 */
public class ComponentLogger extends Logger {

	private AbstractComponent component;

	protected ComponentLogger(AbstractComponent component) {
		super("dummy");
		this.component = component;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#getInstance(java.lang.Class)
	 */
	public static Category getInstance(Class arg0) {
		return Logger.getInstance(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#getInstance(java.lang.String)
	 */
	public static Category getInstance(String arg0) {
		return Logger.getInstance(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Logger#getLogger(java.lang.Class)
	 */
	public static Logger getLogger(Class arg0) {
		return Logger.getLogger(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Logger#getLogger(java.lang.String)
	 */
	public static Logger getLogger(String arg0) {
		return Logger.getLogger(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Logger#getLogger(java.lang.String, org.apache.log4j.spi.LoggerFactory)
	 */
	public static Logger getLogger(String arg0, LoggerFactory arg1) {
		return Logger.getLogger(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Logger#getRootLogger()
	 */
	public static Logger getRootLogger() {
		return Logger.getRootLogger();
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#addAppender(org.apache.log4j.Appender)
	 */
	public void addAppender(Appender arg0) {
		component.getRealLogger().addAppender(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#assertLog(boolean, java.lang.String)
	 */
	public void assertLog(boolean arg0, String arg1) {
		component.getRealLogger().assertLog(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#callAppenders(org.apache.log4j.spi.LoggingEvent)
	 */
	public void callAppenders(LoggingEvent arg0) {
		component.getRealLogger().callAppenders(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#debug(java.lang.Object)
	 */
	public void debug(Object arg0) {
		component.getRealLogger().debug(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#debug(java.lang.Object, java.lang.Throwable)
	 */
	public void debug(Object arg0, Throwable arg1) {
		component.getRealLogger().debug(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#error(java.lang.Object)
	 */
	public void error(Object arg0) {
		component.getRealLogger().error(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#error(java.lang.Object, java.lang.Throwable)
	 */
	public void error(Object arg0, Throwable arg1) {
		component.getRealLogger().error(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#fatal(java.lang.Object)
	 */
	public void fatal(Object arg0) {
		component.getRealLogger().fatal(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#fatal(java.lang.Object, java.lang.Throwable)
	 */
	public void fatal(Object arg0, Throwable arg1) {
		component.getRealLogger().fatal(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#getAdditivity()
	 */
	public boolean getAdditivity() {
		return component.getRealLogger().getAdditivity();
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#getAllAppenders()
	 */
	public Enumeration getAllAppenders() {
		return component.getRealLogger().getAllAppenders();
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#getAppender(java.lang.String)
	 */
	public Appender getAppender(String arg0) {
		return component.getRealLogger().getAppender(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#getEffectiveLevel()
	 */
	public Level getEffectiveLevel() {
		return component.getRealLogger().getEffectiveLevel();
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#getLoggerRepository()
	 */
	public LoggerRepository getLoggerRepository() {
		return component.getRealLogger().getLoggerRepository();
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#getResourceBundle()
	 */
	public ResourceBundle getResourceBundle() {
		return component.getRealLogger().getResourceBundle();
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#info(java.lang.Object)
	 */
	public void info(Object arg0) {
		component.getRealLogger().info(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#info(java.lang.Object, java.lang.Throwable)
	 */
	public void info(Object arg0, Throwable arg1) {
		component.getRealLogger().info(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#isAttached(org.apache.log4j.Appender)
	 */
	public boolean isAttached(Appender arg0) {
		return component.getRealLogger().isAttached(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#isDebugEnabled()
	 */
	public boolean isDebugEnabled() {
		return component.getRealLogger().isDebugEnabled();
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#isEnabledFor(org.apache.log4j.Priority)
	 */
	public boolean isEnabledFor(Priority arg0) {
		return component.getRealLogger().isEnabledFor(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#isInfoEnabled()
	 */
	public boolean isInfoEnabled() {
		return component.getRealLogger().isInfoEnabled();
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#l7dlog(org.apache.log4j.Priority, java.lang.String, java.lang.Object[], java.lang.Throwable)
	 */
	public void l7dlog(Priority arg0, String arg1, Object[] arg2, Throwable arg3) {
		component.getRealLogger().l7dlog(arg0, arg1, arg2, arg3);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#l7dlog(org.apache.log4j.Priority, java.lang.String, java.lang.Throwable)
	 */
	public void l7dlog(Priority arg0, String arg1, Throwable arg2) {
		component.getRealLogger().l7dlog(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#log(java.lang.String, org.apache.log4j.Priority, java.lang.Object, java.lang.Throwable)
	 */
	public void log(String arg0, Priority arg1, Object arg2, Throwable arg3) {
		component.getRealLogger().log(arg0, arg1, arg2, arg3);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#log(org.apache.log4j.Priority, java.lang.Object)
	 */
	public void log(Priority arg0, Object arg1) {
		component.getRealLogger().log(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#log(org.apache.log4j.Priority, java.lang.Object, java.lang.Throwable)
	 */
	public void log(Priority arg0, Object arg1, Throwable arg2) {
		component.getRealLogger().log(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#removeAllAppenders()
	 */
	public void removeAllAppenders() {
		component.getRealLogger().removeAllAppenders();
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#removeAppender(java.lang.String)
	 */
	public void removeAppender(String arg0) {
		component.getRealLogger().removeAppender(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#removeAppender(org.apache.log4j.Appender)
	 */
	public void removeAppender(Appender arg0) {
		component.getRealLogger().removeAppender(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#setAdditivity(boolean)
	 */
	public void setAdditivity(boolean arg0) {
		component.getRealLogger().setAdditivity(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#setLevel(org.apache.log4j.Level)
	 */
	public void setLevel(Level arg0) {
		component.getRealLogger().setLevel(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#setResourceBundle(java.util.ResourceBundle)
	 */
	public void setResourceBundle(ResourceBundle arg0) {
		component.getRealLogger().setResourceBundle(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#warn(java.lang.Object)
	 */
	public void warn(Object arg0) {
		component.getRealLogger().warn(arg0);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Category#warn(java.lang.Object, java.lang.Throwable)
	 */
	public void warn(Object arg0, Throwable arg1) {
		component.getRealLogger().warn(arg0, arg1);
	}

}
