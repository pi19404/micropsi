package org.micropsi.nodenet;

/**
 * Agent implementations may provide "properties", String values that are available to
 * native modules.
 */
public interface NetPropertiesIF {

	/**
	 * Returns the value of the given property.
	 * @param propertyName the name of the property
	 * @return the value or null if there is no value
	 */
	public String getProperty(String propertyName);
	
}
