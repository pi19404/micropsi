/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.console/sources/org/micropsi/eclipse/console/ConsoleRuntimeUser.java,v 1.4 2005/01/20 23:25:41 vuine Exp $ 
 */
package org.micropsi.eclipse.console;

import org.apache.log4j.Logger;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.eclipse.console.adminperspective.LogViewAppender;
import org.micropsi.eclipse.runtime.IRuntimeFacade;
import org.micropsi.eclipse.runtime.IRuntimeUser;


public class ConsoleRuntimeUser implements IRuntimeUser {

	private static ConsoleRuntimeUser instance;
	private IRuntimeFacade runtime;
	
	// for lazy initialisation, also as workaround for eclipse bug #5875
	private static boolean initialized = false;;

	public static ConsoleRuntimeUser getInstance() {
		if(Logger.getRootLogger().getAppender(LogViewAppender.LOGVIEWAPPENDER) == null)
			Logger.getRootLogger().addAppender(new LogViewAppender());
		if(!initialized) {
//			Logger.getRootLogger().addAppender(new LogViewAppender());
//			ConsoleServiceClient.create(instance.runtime.getSelf(), instance.runtime.getBasicServices().getServerID());
//			TimeClient.create(instance.runtime.getSelf());
			initialized = true;
		}
		return instance;
	}


	public Logger getLogger() {
		return runtime.getBasicServices().getLogger();
	}
	
	public String handleException(Throwable e) {
		return runtime.getBasicServices().handleException(e); 
	}

	public String getServerID() {
		return runtime.getBasicServices().getServerID();
	}
	
	public ConsoleFacadeIF getConsole() {
		return runtime.getConsole();
	}
	
	public void initialize(IRuntimeFacade runtime) {
		instance = this;	
		this.runtime = runtime;
	}

}
