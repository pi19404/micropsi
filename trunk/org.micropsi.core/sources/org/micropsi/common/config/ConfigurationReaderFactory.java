/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/config/ConfigurationReaderFactory.java,v 1.2 2004/08/10 14:38:17 fuessel Exp $
 */
package org.micropsi.common.config;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import org.micropsi.common.exception.MicropsiException;

public class ConfigurationReaderFactory {
	
    public static final int CONFIG_XML = 0;

	public static ConfigurationReaderIF getConfigReader(String configRoot,HashMap variables,int configType) throws MicropsiException {
		return new XMLConfigurationReader(
			configRoot.replace('/', File.separatorChar).
			replace('\\', File.separatorChar),
			variables
		);
	}
	
	public static ConfigurationReaderIF getConfigReader(InputStream configInput,int configType) throws MicropsiException {
		return new XMLConfigurationReader(configInput);
	}

}
