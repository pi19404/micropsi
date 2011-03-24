/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/SensorNode.java,v 1.4 2006/06/27 19:37:02 rvuine Exp $
 */
package org.micropsi.nodenet;

/**
 * Node that reads it's activation from a SensorDataSource.
 * @see org.micropsi.nodenet.SensorDataSourceIF
 */
public class SensorNode extends Node {

	protected SensActRegistry sensorReg;
	protected String dataType;

	/**
	 * Constructs a sensor. May only be called by the NetEntityFactory.
	 * @param nodeID the id of the sensor/entity
	 * @param manager the entity manager
	 * @param sensorReg the SensActRegistry
	 */
	protected SensorNode(String nodeID, NetEntityManager manager, SensActRegistry sensorReg) {
		super(NodeFunctionalTypesIF.NT_SENSOR,nodeID,manager,DecayIF.NO_DECAY);
		this.sensorReg = sensorReg;
		manager.reportDefiantEntity(nodeID);
	}
	
	/**
	 * Connects the sensor to the given DataSource. There is no check if the
	 * corresponding dataSource exists. If there is no such dataSource, the
	 * SensorNode will be connected anyway, but of course nothing is read as
	 * long as the DataSource does not exist, so the activation of the sensor
	 * node will be 0.0
	 * @param dataType the dataType to connect to.
	 */
	public void connectSensor(String dataType) {
		this.dataType = dataType;
	} 
	
	/**
	 * Disconnects the sensor from it's current DataSource.
	 */
	public void disconnectSensor() {
		dataType = null;
	}
	
	/**
	 * Checks if the sensor is currently connected.
	 * @return boolean if the sensor is connected.
	 */
	public boolean isConnected() {
		return dataType != null;
	}
	
	/**
	 * Overrides the default NetEntity method to read it's activation from the
	 * DataSource.
	 * @see org.micropsi.nodenet.NetEntity#calculateGates()
	 */
	protected void calculateGates() throws NetIntegrityException {
		super.calculateGates();
		
		Gate g = getGate(GateTypesIF.GT_GEN);
		g.setActivation(sensorReg.getSensorSignalStrength(dataType));					
	}

	protected void use() throws NetIntegrityException {
	}
	
	/**
	 * Returns the dataType.
	 * @return String
	 */
	public String getDataType() {
		return dataType;
	}

}
