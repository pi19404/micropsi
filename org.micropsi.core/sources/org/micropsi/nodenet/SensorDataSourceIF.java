/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/SensorDataSourceIF.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.nodenet;

/**
 * Implementations of this interface provide a value to the net's sensors: After
 * registering the implementation with the net, one or more SensorNodes can
 * connect to it and read the value returned by getSignalStrength().
 */
public interface SensorDataSourceIF {
	
	/**
	 * Returns the unique name of the DataSource that will be used by the
	 * SensorNodes to identify their sources.
	 * @return String the unique name of the datasource
	 */
	public String getDataType();
	
	/**
	 * Whenever a SensorNode is connected to this DataSource, it reads it's
	 * activation from this method.
	 * @return double the value to be propageted into the net.
	 */
	public double getSignalStrength();

}
