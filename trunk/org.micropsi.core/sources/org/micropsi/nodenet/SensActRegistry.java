/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/SensActRegistry.java,v 1.3 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.nodenet;

import java.util.HashMap;
import java.util.Iterator;

/**
 * The SenseActRegistry, unique in a net, decides where the net's input comes
 * from and where it's output goes to. The outside world can register
 * "DataSources" and "DataTargets", the net's sensors can connect to the
 * DataSources and the net's actors can connect to DataTargets. When a sensor
 * reads input, it reads it from the DataSource it has been connected to, when
 * an actor writes output, it writes it to the DataTarget it has been connected
 * to. 
 */
public class SensActRegistry {
	
	private HashMap<String,SensorDataSourceIF> dataProviders = new HashMap<String,SensorDataSourceIF>();
	private HashMap<String,ActorDataTargetIF> dataTargets = new HashMap<String,ActorDataTargetIF>();
		
	/**
	 * Register a DataSource.
	 * @param dp the DataSource to be registered.
	 */
	public void registerSensorDataProvider(SensorDataSourceIF dp) {
		dataProviders.put(dp.getDataType(),dp);
	}
	
	/**
	 * Unregister a DataSource. You can't break the net's integrity by doing so.
	 * Removing the DataSource will cause the connected sensors to read 0.0 all
	 * the time - so nothing really bad can happen.
	 * @param dp the DataSource to be unregistered
	 */
	public void unregisterSensorDataProvider(SensorDataSourceIF dp) {
		dataProviders.remove(dp.getDataType());
	}
	
	/**
	 * Register a DataTarget.
	 * @param dt the DataTarget to be registered.
	 */
	public void registerActuatorDataTarget(ActorDataTargetIF dt) {
		dataTargets.put(dt.getDataType(),dt);
	}
	
	/**
	 * Unregister a DataTarget. You can't break anything by doing so - actors
	 * connected to an inexistent DataTarget will simply write their output
	 * nowhere, just as if they were not connected at all.
	 * @param dt the DataTarget to unregister
	 */
	public void unregisterActuatorDataTarget(ActorDataTargetIF dt) {
		dataTargets.remove(dt.getDataType());
	}
	
	/**
	 * Returns the (String) IDs of the registered DataSources. Don't try
	 * anything with remove().
	 * @return Iterator the IDs
	 */
	public Iterator<String> getSensorDataProviderIDs() {
		return dataProviders.keySet().iterator();
	}
	
	/**
	 * Returns the (String) IDs of the registered DataTargets. Don't try
	 * anything with remove()
	 * @return Iterator the IDs
	 */
	public Iterator<String> getActuatorDataTargetIDs() {
		return dataTargets.keySet().iterator();
	}
	
	/**
	 * This method is called by sensor nodes to read the input. 
	 * @param dataProvider the ID of the DataSource the sensor is connected to
	 * @return double the value the sensor propagates into the net.
	 */
	protected double getSensorSignalStrength(String dataProvider) {
		if(!dataProviders.containsKey(dataProvider)) return 0;
		return dataProviders.get(dataProvider).getSignalStrength();
	}
	
	/**
	 * This method is called by the actor nodes to write the output. 
	 * @param dataTarget the ID of the DataTarget the actor is connected to.
	 * @param signalStrength the value the actor wants the outside world to know
	 */
	protected void addActuatorSignalStrength(String dataTarget, double signalStrength) {
		if(!dataTargets.containsKey(dataTarget)) return;
		dataTargets.get(dataTarget).addSignalStrength(signalStrength);
	}
	
	/**
	 * This method is called by the actor nodes to determine the success of
	 * some action.
	 * @param dataTarget the data target
	 * @return double the success of the previous execution
	 */
	protected double getActuatorSuccess(String dataTarget) {
		if(!dataTargets.containsKey(dataTarget)) return 0;
		return dataTargets.get(dataTarget).getExecutionSuccess();		
	}
	
	/**
	 * Checks if a DataSource is registered.
	 * @param dataProvider the ID of the DataSource.
	 * @return boolean true if there is a DataSource registered for this ID.
	 */
	public boolean knowsSensorDataProvider(String dataProvider) {
		return dataProviders.containsKey(dataProvider);
	}
	
	/**
	 * Checks if a DataTarget is registered.
	 * @param dataTarget the ID of the DataTarget.
	 * @return boolean true if there is a DataTagrte registered for this ID.
	 */
	public boolean knowsActuatorDataTarget(String dataTarget) {
		return dataTargets.containsKey(dataTarget);
	}

	/**
	 * Clears all dataTargets and dataTargets, resetting the registry
	 */
	protected void clear() {
		dataProviders.clear();
		dataTargets.clear();
	}

}
