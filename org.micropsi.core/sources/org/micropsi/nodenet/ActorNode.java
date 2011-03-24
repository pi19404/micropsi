/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/ActorNode.java,v 1.4 2006/06/27 19:35:44 rvuine Exp $
 */
package org.micropsi.nodenet;

/**
 * Node that writes its activation to an ActorDataTarget.
 * @see org.micropsi.nodenet.ActorDataTargetIF
 */
public class ActorNode extends Node {

	protected SensActRegistry sensorReg;
	protected String dataTarget;

	/**
	 * Constructor.
	 * @param nodeID the ID of this node
	 * @param manager the entity manager
	 * @param sensorReg the sensor/actor registry to be used
	 */
	protected ActorNode(String nodeID, NetEntityManager manager, SensActRegistry sensorReg) {
		super(NodeFunctionalTypesIF.NT_ACTOR,nodeID,manager,DecayIF.NO_DECAY);
		this.sensorReg = sensorReg;
		manager.reportDefiantEntity(nodeID);
	}
	
	/**
	 * Connects the ActorNode to the given dataTarget. There is no check if the
	 * corresponding dataTarget exists. If there is no such dataTarget, the
	 * ActorNode will be connected anyway, but of course nothing is written
	 * nowhere as long as the dataTarget is not there.
	 * @param dataTarget the dataTarget to connect to.
	 */
	public void connectActor(String dataTarget) {
		this.dataTarget = dataTarget;
	} 
	
	/**
	 * Disconnects the ActorNode from it's current dataTarget.
	 */
	public void disconnectActor() {
		dataTarget = null;
	}
	
	/**
	 * Checks if the ActorNode is connected.
	 * @return boolean truf if the ActorNode is connected.
	 */
	public boolean isConnected() {
		return dataTarget != null;
	}
	
	/**
	 * Extended to write to the data target and set the GEN gate with the
	 * success of the execution
	 * @see org.micropsi.nodenet.NetEntity#calculateGates()
	 */
	protected void calculateGates() throws NetIntegrityException {
		super.calculateGates();
			
		sensorReg.addActuatorSignalStrength(dataTarget, getSlot(SlotTypesIF.ST_GEN).getIncomingActivation());
		
		Gate g = getGate(GateTypesIF.GT_GEN);
		g.setActivation(sensorReg.getActuatorSuccess(dataTarget));	
	}
	
	/**
	 * @see org.micropsi.nodenet.Node#use()
	 */
	protected void use() throws NetIntegrityException {	
	}
	
	/**
	 * Returns the dataTarget the ActorNode is connected to.
	 * @return String the dataType.
	 */
	public String getDataType() {
		return dataTarget;
	}

}
