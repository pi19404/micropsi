/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/common/AbstractComponent.java,v 1.4 2004/09/02 17:24:59 vuine Exp $
 */
package org.micropsi.comp.common;

import org.apache.log4j.Logger;

import org.micropsi.common.config.ConfigurationReaderIF;
import org.micropsi.common.consoleservice.ConsoleServiceProvider;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.exception.ExceptionProcessor;
import org.micropsi.comp.common.conserv.QTypeGetComponentDescriptor;
import org.micropsi.comp.common.conserv.QTypeGetRunnerLog;
import org.micropsi.comp.messages.MessageAnswerFactory;

/**
 * AbstractComponent is the abstract ancestor of all the application's 
 * components. The default AbstractComponent has no run method and hence is
 * purely reactive. Components that need a run() method must implement
 * ActiveComponentIF to be executed by the ComponentRunner.
 */

public abstract class AbstractComponent {
	
	/**
	 * Interface for reading configuration data. You can simply use this,
	 * as it is initialized by the ComponentRunner
	 */
	protected ConfigurationReaderIF config;

	/**
	 * The component's configuration prefix. Use this when accessing the
	 * configuration. The prefix points at the "data" section of the component's
	 * configuration. (...data.foo.bar can be read by reading the key 
	 * prefixKey + ".foo.bar"
	 */
	protected String prefixKey;
	
	/**
	 * The exception processor. Simply put in here any exception you catch
	 * anywhere within the component.
	 */
	protected ExceptionProcessor exproc;
		
	/**
	 * The component's id, as read from the configuration. 
	 */
	private String id;
	
	/**
	 * The ServerFacade. Register your RequestHandlers here.
	 */
	protected ComponentServerFacade servers;
	
	/**
	 * The ClientFacade. Place your requests here.
	 */
	protected ComponentClientFacade clients;
	
	/**
	 * The ConsoleServiceProvider. Register your QuestionTypes here
	 */
	protected ConsoleServiceProvider consoleService;
	
	
	/**
	 * The ComponenRunner that started the component
	 */
	protected ComponentRunner runner;	
	
	/**
	 * The logger. Feel free to use this flexible logging channel. This is a proxy
	 * instance that will route your logs to the correct channels.
	 */
	protected Logger logger;


	private Logger realLogger;
	
	/**
	 * Initializes the component. You don't want to call this, the ComponentRunner
	 * does.
	 */
	public void initialize(
					ConfigurationReaderIF config,
					String prefixKey,
					String id,
					ComponentRunner runner
				) throws MicropsiException {
		this.id = id;
		this.runner = runner;
					
		logger = new ComponentLogger(this);
		realLogger = Logger.getLogger("comp."+id);
		exproc = new ExceptionProcessor(new ComponentExceptionInfo(logger));
		
		this.config = config;
		this.prefixKey = prefixKey;
		
		servers = new ComponentServerFacade(config,prefixKey+".servers");
		clients = new ComponentClientFacade(config,prefixKey+".clients",servers);
		consoleService = new ConsoleServiceProvider(new MessageAnswerFactory(),logger);
		consoleService.registerQuestionType(new QTypeGetComponentDescriptor(this));	
		consoleService.registerQuestionType(new QTypeGetRunnerLog(this));
		
		performInitialisation();
					
	}
	
	/**
	 * Does the implementation-dependent initialisation. When implementing
	 * a component, you'll want to instatiate the RequesHandlers here and
	 * register them with the ServerFacade.
	 * 
	 */
	protected abstract void performInitialisation() throws MicropsiException;
	
	/**
	 * Return the component's id.
	 */
	public String getComponentID() {
		return id;
	}
	
	public void overrideComponentID(String newID, boolean notifyComponentRunner) {
		if(!id.equals(newID)) {
			if(notifyComponentRunner) runner.overrideComponentID(this.id, newID);
			this.id = newID;
			realLogger = Logger.getLogger("comp."+id);
			logger.debug("New component id: "+id);
		}
	}

	public ConsoleServiceProvider getConsoleService() {
		return consoleService;
	}

	public ExceptionProcessor getExproc() {
		return exproc;
	}

	public Logger getLogger() {
		return logger;
	}
	
	protected Logger getRealLogger() {
		return realLogger;
	}
	
	public ComponentDescriptor getComponentDescriptor() {
		return new ComponentDescriptor(this);
	}

	public ComponentRunner getComponentRunner() {
		return runner;
	}
	
	protected abstract int getInnerType();
	
	public abstract void shutdown();
	


}

