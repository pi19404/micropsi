/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/console/ConsoleFunctionalityIF.java,v 1.2 2005/04/28 17:07:25 vuine Exp $ 
 */
package org.micropsi.comp.console;

import org.micropsi.common.config.ConfigurationReaderIF;
import org.micropsi.common.exception.MicropsiException;


public interface ConsoleFunctionalityIF {

	public void initialize(ConsoleComponent component, ConfigurationReaderIF config, String configPrefix) throws MicropsiException;
	
	// hier was getten, was das mit den Threads hinkriegt
	
}
