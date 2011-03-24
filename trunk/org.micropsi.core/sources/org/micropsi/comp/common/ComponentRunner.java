/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/common/ComponentRunner.java,v 1.11 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.common;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.micropsi.common.communication.XMLTCPChannelServer;
import org.micropsi.common.config.ConfigurationReaderFactory;
import org.micropsi.common.config.ConfigurationReaderIF;
import org.micropsi.common.config.MicropsiConfigException;
import org.micropsi.common.exception.ExceptionProcessor;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.log.LoggerConfigurator;
import org.micropsi.common.progress.ProgressMonitorIF;
import org.micropsi.common.utils.ThreadPool;

/**
 * The ComponentRunner starts a number of aep components. It can be run by using it's "main" method or
 * by calling the getInstance method - either way will work, both ways will need to know a
 * configuration file's path. 
 * 
 * Please note that the ComponentRunner is meant to be used as a singleton. Calling the getInstance method
 * with different configfile urls within one VM lifecycle is strongly discouraged, as the class doesn't store
 * more than one instance of itself (This means, that after calling the method once, it will return the same
 * instance of itself regardless of the configfile parameter, what is probably not what you excpect it to do
 * when using it in this way, so just don't do it...)
 */

public class ComponentRunner {

	public static final String VERSION = "Micropsi Runtime System v0.85";
	private static ComponentRunner instance;
	protected static HashMap<String,String> variables = new HashMap<String,String>();
	
	protected ConfigurationReaderIF config;
	protected ExceptionProcessor exproc;
	protected HashMap<String,AbstractComponent> components;
	protected Logger logger;
	protected ClassLoader defaultLoader;
    boolean end = false;
    
    /**
     * Initializes the runner, creates an instance and returns it.
     * @param configfile the path to the configfile
     * @param loader the classLoader to use
     * @param monitor a progress monitor. May be null if you don't want to monitor the startup
     * @return The instance
     * @throws ComponentRunnerException if there is some failure when setting up the components
     */
    public static ComponentRunner getInstance(String configfile, ClassLoader loader, ProgressMonitorIF monitor) throws ComponentRunnerException {
    	if(configfile == null) throw new ComponentRunnerException("configfile was null");
		if(loader == null) throw new ComponentRunnerException("classloader was null");
    	
    	try {
			if(instance == null) instance = new ComponentRunner(configfile, loader, monitor);
    	} catch (Throwable t) {
    		throw new ComponentRunnerException("Could not instantiate: "+t.getMessage(),t);	
    	}
    	
    	return instance;
    }
    
    /**
     * Returns the instance.
     * @return The instance
     * @throws ComponentRunnerException if getInstance(String configfile) was not called previosly
     */
    public static ComponentRunner getInstance() throws ComponentRunnerException {
    	if(instance == null) throw new ComponentRunnerException("ComponentRunner not initialized - call getInstance(String configfile) before");
   		return instance;
    }
    
    /**
     * Retrieves a component from the ComponentRunner instance.
     * @param name The name of the component. Note that components are able to change their names.
     * @return the component.
     * @throws ComponentRunnerException if the component is not found.
     */
    public AbstractComponent getComponent(String name) throws ComponentRunnerException {
    	synchronized(components) {
    		if(!components.containsKey(name)) throw new ComponentRunnerException("Component '"+name+"' not found");
    		return components.get(name);
    	}
    }
    
    /**
     * Checks if a given component exists in this ComponentRunner
     * @return
     */
    public boolean componentExists(String name) {
    	return components.containsKey(name);
    }
    
    public String getLogFile() {
		try {
			return config.getConfigValue("config.runner.log.file");
		} catch (MicropsiConfigException e) {
			return null;
		}
    }
    
    private ComponentRunner() {}
    
	private ComponentRunner(String configfile, ClassLoader loader, ProgressMonitorIF monitor) throws ComponentRunnerException {
		
		defaultLoader = loader;
		components = new HashMap<String,AbstractComponent>(5);
		BasicConfigurator.configure();		
		exproc = new ExceptionProcessor(new ComponentExceptionInfo(Logger.getRootLogger()));
		Iterator<String> configElements;
		int numberOfComponents = 0;
		
		try {
		    if(monitor != null) monitor.beginTask("Starting runtime system");
		    
			config = ConfigurationReaderFactory.getConfigReader(
						configfile,
						variables,
						ConfigurationReaderFactory.CONFIG_XML
					 );
			
			LoggerConfigurator.configureLogger("config.runner.log",config); 
			logger = Logger.getRootLogger();
			System.out.println(VERSION+"\n");
			logger.info(VERSION);
			logger.info("Started: "+DateFormat.getDateTimeInstance().format(new Date()));
			logger.info("ClassLoader: "+loader.getClass().getName());
			
			List<String> configValues = config.getConfigurationValues("config.runner.components");
			numberOfComponents = configValues.size();
			configElements = configValues.iterator();
			
			ThreadPool.initDefaultInstance(
				config.getIntConfigValue("config.runner.threadpool.maxthreads"),
				config.getIntConfigValue("config.runner.threadpool.minthreads"),
				config.getIntConfigValue("config.runner.threadpool.maxidletime")
			);

		} catch (Exception e) {
			String error = exproc.handleException(e);
			throw new ComponentRunnerException("Setup crash. See logfile for details. Message: "+error,e);			
		}
			
		logger.debug("Creating instances");
		if(monitor != null) monitor.beginTask("Creating instances");
		
		int j=0;
		while(configElements.hasNext()) {
		    j++;
			String next = configElements.next();
			
			if(monitor != null) monitor.reportProgress(j,numberOfComponents,"Creating "+next);
			
			createComponent(next, config, loader,false);
		}
		
		if(monitor != null) monitor.endTask();
		if(monitor != null) monitor.beginTask("Starting active components");
		
		logger.debug("Starting ActiveComponents");
		Object[] keys = components.keySet().toArray();
		for(int i=0;i<keys.length;i++) {
			Object o = components.get(keys[i]);
			
			if(monitor != null) monitor.reportProgress(i,keys.length,"Starting "+((AbstractComponent)o).getComponentID());
			
			if(o instanceof ActiveComponentIF) {
				((ActiveComponentIF)o).start();
			}
		}
		
		if(monitor != null) monitor.endTask();
		if(monitor != null) monitor.endTask();
		
	}

    /**
     * Shuts down the ComponentRunner and all components.
     * @throws ComponentRunnerException
     */
	public void shutdown() throws ComponentRunnerException {
    	
    	ArrayList<String> names = new ArrayList<String>(components.keySet());
    	Iterator cn = names.iterator();
    	while(cn.hasNext()) {
    		String name = (String)cn.next();
    		logger.info("Shutting down: "+name);
    		destroyComponent(name);
    	}	
    	
    	instance = null;
    	System.gc();
    }
	
	/**
	 * Creates and adds a component. The component that will be added is the component with the name componentName in
	 * the given configuration. If there is no such component in the configuration, an exception will be thrown. If
	 * there is already a registered component with the same name, the component will be created according to the
	 * information in the configuration, but renamed. If the component implements ActiveComponentIF and the 
	 * startIfActive flag is true, the component will be started immediately after creation. 
	 * @param componentName The name of the component as it is in the configuration
	 * @param configuration The configuration to be used
	 * @param loader The classLoader to be used. - be careful with that. If you pass "null", the default classloader
	 * of the MRS will be used (unless you know exactly what you are doing, this is probably what you want)
	 * @param startIfActive if true, the component will be started after creation
	 * @return AbstractComponent the newly created component
	 * @throws ComponentRunnerException if there is no component entry with the given name in the configuration or
	 * something else went wrong.
	 */
	public AbstractComponent createComponent(String componentName, ConfigurationReaderIF configuration, ClassLoader loader, boolean startIfActive) throws ComponentRunnerException {
		
		if(loader == null) loader = defaultLoader;
		
		try {
		
			logger.info("Init: "+componentName);
					
			String classname = configuration.getConfigValue("config.component:id="+componentName+".class");
			AbstractComponent component;
			try {
				Class compclass = loader.loadClass(classname);
				component = (AbstractComponent)compclass.newInstance();
			} catch (ClassNotFoundException e) {
				throw new MicropsiException(10,e.getMessage());
			}
			component.initialize(configuration,"config.component:id="+componentName+".data",componentName,this);
			
			synchronized(components) {
				int suffix=0;
				String realkey = componentName;
				while(components.containsKey(realkey)) {
					realkey = componentName + suffix;
					suffix++;
				}
				if(!realkey.equals(componentName))
					component.overrideComponentID(realkey,false);
					
				components.put(realkey,component);
			}

			if(startIfActive) {
				if(component instanceof ActiveComponentIF) {
					((ActiveComponentIF)component).start();
					logger.debug("Started "+componentName);
				}
			}

			return component;

		} catch (Exception e) {
			String error = exproc.handleException(e);
			throw new ComponentRunnerException("Component creation failure. See logfile for details. Message: "+error,e);								
		}
		
	}
	
	/**
	 * Removes a running component from the system.
	 * @param componentName The name of the component
	 * @throws ComponentRunnerException if the component can't be found
	 */
	public void destroyComponent(String componentName) throws ComponentRunnerException {
		
		if(!components.containsKey(componentName)) return;
		
		AbstractComponent component = getComponent(componentName);
		try {
			component.getConsoleService().shutdown();
			component.shutdown();
		} catch (Exception e) {
			logger.error("Component "+componentName+" was not shut down cleanly", e);
			throw new ComponentRunnerException("Component "+componentName+" was not shut down cleanly. See logfile for details. ("+e.getMessage()+")",e);
		}
		synchronized(components) {
			components.remove(componentName);
		}
		logger.info("Component "+componentName+" has been removed.");
	}
	
	/**
	 * Micropsi servlets, known as "ComponentService"s, need to have access to the mrs lowlevel communication
	 * structures (eg channelservers). As the servlets are created by a servlet container without the
	 * ComponentRunner knowing anything about them, they'll have to register with the ComponentRunner 
	 * to get access to the component and all the mrs stuff associated with it. 
	 * @param service The servlet that wants to register.
	 * @param component The component that the servlet wants to be the channelserver for
	 * @param server The name of the channelserver that the servlet wants to be  
	 * @throws MicropsiException if the component is not found or the channelserver does not exist
	 */
	public void registerServlet(ComponentService service, String component, String server) throws MicropsiException {
		
		if(!components.containsKey(component)) throw new MicropsiException(0,"Component not found: "+component);
		AbstractComponent c = components.get(component);
		try {
			XMLTCPChannelServer s = (XMLTCPChannelServer)c.servers.getServer(server);
			service.setServer(s);
		} catch (ClassCastException e) {
			throw new MicropsiException(18," should be: XMLTCPChannelServer, was: "+c.servers.getServer(server)+". Channelserver: "+server+". Component: "+component,e);
		}
		service.setExproc(c.exproc);
	}
	
	public static void setGlobalVariable(String key, String value) {
		variables.put(key, value);
	}
	
	public static String getGlobalVariable(String key) {
		if(variables.containsKey(key)) {
			return variables.get(key);
		} else return null;
	}
	
	public static String replaceVariables(String value) {
		if(variables == null) return value;
		if(value.indexOf("$") < 0) return value;

		String toReturn = value;
		Iterator iter = variables.keySet().iterator();
		while(iter.hasNext()) {
			String key = (String)iter.next();
			if(toReturn.indexOf("$"+key) >= 0) {
				String pre = toReturn.substring(0,toReturn.indexOf("$"+key));
				String post = toReturn.substring(toReturn.indexOf("$"+key)+key.length()+1);
				toReturn = pre + variables.get(key) + post;
			}
		}
		return toReturn;
	}

	
	public static void main(String[] args) {
		try {
			
			String micropsihome = System.getProperty("micropsi.home");
			String sep = System.getProperty("file.separator"); 
			if(micropsihome == null) {
				micropsihome = new File(".").getAbsolutePath();
			}
			if(micropsihome.endsWith(sep))	
				micropsihome = micropsihome.substring(0,micropsihome.length()-sep.length());
			
			String localname = Integer.toString(new Object().hashCode());			
			if(args.length > 0)
				localname = args[0];
			
			setGlobalVariable("MICROPSI_HOME", micropsihome);
			setGlobalVariable("USERNAME",localname);
			
			ComponentRunner.getInstance(
				args[0],
				ClassLoader.getSystemClassLoader(),
				null
			);		
		} catch (ComponentRunnerException e) {
			System.err.println(e.getMessage());
		}
	}

	protected void overrideComponentID(String oldID, String newID) {
		synchronized(components) {
			AbstractComponent o = components.get(oldID);
			components.put(newID, o);
			components.remove(oldID);
		}
	}
	
	public HashMap getGlobalVariables() {
		return variables;
	}
}
