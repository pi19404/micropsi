package org.micropsi.nodenet.agent;

import java.util.Properties;

import org.micropsi.nodenet.NetPropertiesIF;

public class MicroPSINetProperties implements NetPropertiesIF {

	private Properties properties = new Properties();
	
	private AgentInformationProviderIF informationProvider;
	
	public MicroPSINetProperties(AgentInformationProviderIF informationProvider) {
		this.informationProvider = informationProvider;
	}
	
	public String getProperty(String propertyName) {
		
		String property = getSpecialProperty(propertyName);
		if(property == null) {
			property = properties.getProperty(propertyName);
		}
		
		return property;
	}
	
	protected void putProperties(Properties props) {
		properties.putAll(props);
	}
	
	private String getSpecialProperty(String propertyName) {
		if("agentName".equals(propertyName)) {
			return informationProvider.getAgentName();
		}
		return null;
	}

}
