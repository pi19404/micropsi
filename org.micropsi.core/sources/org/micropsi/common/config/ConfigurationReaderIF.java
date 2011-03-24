/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/config/ConfigurationReaderIF.java,v 1.3 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.common.config;

import java.util.List;

public interface ConfigurationReaderIF {

	public String getConfigValue(String key) throws MicropsiConfigException;
	
	public int getIntConfigValue(String key) throws MicropsiConfigException;
	
	public double getDoubleConfigValue(String key) throws MicropsiConfigException;
	
	public boolean getBoolConfigValue(String key) throws MicropsiConfigException;
	
	public List<String> getConfigurationValues(String key) throws MicropsiConfigException;

}
