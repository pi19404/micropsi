package org.micropsi.eclipse.runtime;


import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.micropsi.common.config.ConfigurationReaderIF;
import org.micropsi.common.exception.ExceptionProcessor;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.common.ComponentRunner;
import org.micropsi.comp.common.ComponentRunnerException;
import org.micropsi.comp.console.ConsoleComponent;
import org.micropsi.comp.console.ConsoleFunctionalityIF;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.eclipse.common.ProgressDialog;
import org.micropsi.eclipse.runtime.internal.InteractiveClassLoader;

/**
 * 
 * 
 * 
 */
public class EclipseConsoleFunctionality implements ConsoleFunctionalityIF {
    
	private static EclipseConsoleFunctionality instance; 
	
	public static EclipseConsoleFunctionality getInstance(String config, ClassLoader parent, IPreferenceStore store, Shell shell) throws ComponentRunnerException,MicropsiException {

		if(instance != null) {
			return instance;
		}
		
		InteractiveClassLoader componentLoader = new InteractiveClassLoader(
						parent,
						shell,
						store
					);
		
		Platform.endSplash();
		ProgressDialog progress = new ProgressDialog("Starting MRS...",shell);
		
		try {
			ComponentRunner.getInstance(config,componentLoader,progress);
		} finally {
			progress.close();
		}
				
		return instance;	
	}

	private ConsoleFacadeIF facade;
	private ConsoleComponent component;
		
	public void initialize(ConsoleComponent component, ConfigurationReaderIF configReader, String prefix) {
		instance = this;
		this.facade = component;
		this.component = component;
	}
	
	public ConsoleFacadeIF getConsole() {
		return facade;
	}
	public Logger getLogger() {
		return component.getLogger();
	}

	public ExceptionProcessor getExproc() {
		return component.getExproc();
	}
	    
}
