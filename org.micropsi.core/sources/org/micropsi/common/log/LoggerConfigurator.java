/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/log/LoggerConfigurator.java,v 1.3 2004/10/28 17:04:40 vuine Exp $
 */
package org.micropsi.common.log;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.micropsi.common.config.ConfigurationReaderIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.utils.FileAppender;

public class LoggerConfigurator {

	public static void configureLogger(
		String prefix, 
		ConfigurationReaderIF config) 
		throws MicropsiException {
		
		String logfile = config.getConfigValue(prefix+".file");
		PatternLayout layout = new PatternLayout("%d{HH:mm:ss.SSS} %-5p\t%c{1} - %m [%t]%n");
		try {
			Logger.getRootLogger().removeAllAppenders();
			Logger.getRootLogger().addAppender(new FileAppender(layout,logfile,false));
		} catch (IOException e) {
			throw new MicropsiException(11,logfile,e);
		}
		
		Level l = Level.INFO;
		String level = config.getConfigValue(prefix+".level");
		if(level.equals("ALL")) l = Level.ALL; else
		if(level.equals("DEBUG")) l = Level.DEBUG; else
		if(level.equals("ERROR")) l = Level.ERROR; else
		if(level.equals("FATAL")) l = Level.FATAL; else
		if(level.equals("OFF")) l = Level.OFF;
		Logger.getRootLogger().setLevel(l);
	}

}
