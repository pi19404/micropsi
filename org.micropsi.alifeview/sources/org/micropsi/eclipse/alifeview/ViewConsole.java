/*
 * Created on 30.06.2005
 *
 */
package org.micropsi.eclipse.alifeview;

import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.eclipse.console.IConsolePart;
import org.micropsi.eclipse.runtime.IBasicServices;

/**
 * @author Markus
 *
 */
public class ViewConsole implements IConsolePart {

    private static ViewConsole instance = null;
    
    private ConsoleFacadeIF console;
    private IBasicServices bserv;
    
    public static ViewConsole getInstance() {
        return instance;
    }
    
    public void initialize(ConsoleFacadeIF console, IBasicServices bserv) {
        this.console = console;
        this.bserv = bserv;
        instance = this;
    }

    /**
	 * @return
	 */
	public IBasicServices getBserv() {
		return bserv;
	}

	/**
	 * @return
	 */
	public ConsoleFacadeIF getConsole() {
		return console;
	}
}
