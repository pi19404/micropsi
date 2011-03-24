/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/GateManipulator.java,v 1.5 2005/10/04 17:40:48 vuine Exp $
 */
package org.micropsi.nodenet;


/**
 * This wrapper class is designed to let classes outside the micropsi.net
 * package change the inner state of some entity's gates. Normally, this is
 * prohibited for good reasons, but some implementations of native modules need
 * full access to the gates.
 */
public final class GateManipulator {
	
	private NetEntity entity;
	
	private GateManipulator() {
	}
	
	protected GateManipulator(NetEntity entity) {
		this.entity = entity;
	}
		
	/**
	 * Sets the activation of the gate.
	 * @param type The gate to be changed
	 * @param activation the new activation
	 * @throws NullPointerException if there is no such gate
	 */
	public void setGateActivation(int type, double activation) {
		entity.getGate(type).setActivation(activation);
	}
	
	/**
	 * Sets the gatefactor of the gate
	 * @param type The gate to be changed
	 * @param gatefactor
	 * @throws NullPointerException if there is no such gate 
	 */
	public void setGateFactor(int type, double gatefactor) {
		entity.getGate(type).setGateFactor(gatefactor);
	}
	
	/**
	 * Sets the ampfactor of the gate
	 * @param type The gate to be changed
	 * @param ampfactor
	 * @throws NullPointerException if there is no such gate 
	 */
	public void setGateAmpFactor(int type, double ampfactor) {
		entity.getGate(type).setAmpfactor(ampfactor);
	}
	
	/**
	 * Sets one of the parameters of the output function.
	 * The available parameters depend on the output funtion used by the gate.
	 * If you set a parameter that does not exist, an IllegalArgumentException
	 * will be thrown. You can check the current output function by calling
	 * Gate.getOutputFunction(). Have a look at the documentation of the 
	 * respective output functions to learn what parameters exist.
	 * 
	 * @param type The gate to be modified
	 * @param parameterName the name of the output function parameter
	 * @param value the new value of the output function
	 */
	public void setOutputFunctionParameter(int type, String parameterName, double value) {
		entity.getGate(type).setOutputFunctionParameter(parameterName,value);
	}
	
	/**
	 * Sets the maximum activation of the gate
	 * @param type The gate to be changed
	 * @param maximum
	 * @throws NullPointerException if there is no such gate 
	 */
	public void setGateMaximum(int type, double maximum) {
		entity.getGate(type).setMaximum(maximum);
	}
	
	/**
	 * Sets the minimum activation of the gate
	 * @param type The gate to be changed
	 * @param minimum
	 * @throws NullPointerException if there is no such gate 
	 */
	public void setGateMinimum(int type, double minimum) {
		entity.getGate(type).setMinimum(minimum);
	}
	
	/**
	 * Sets the type of decay that will be applied to attached links
	 * @param type The gate to be changed
	 * @param decayType
	 * @throws NullPointerException if there is no such gate 
	 */
	public void setGateDecayType(int type, int decayType) {
		entity.getGate(type).setDecayCalculatorType(decayType);
	}
	
	/**
	 * Returns the entity's gate of the given type or null if there is no such
	 * gate.
	 * @param type the type of the requested Gate
	 * @return Gate the gate or null
	 */
	public Gate getGate(int type) {
		return entity.getGate(type);
	}
	
	/**
	 * Deletes all links originating from this gate. Know what you're doing when
	 * calling this or face the consequences.
	 * @param type the type of the gate to be "cleaned"
	 * @throws NetIntegrityException if the gate has bogus links.
	 */
	public void unlinkGate(int type) throws NetIntegrityException {
		Gate g = entity.getGate(type);
		if(g == null) return;
		g.unlinkCompletely();
	}
	
	/**
	 * Create a new link from the gate with the type gate to some other entity.
	 * @param gate the type of the gate the new link will originate from 
	 * @param to the ID of the newly to be linked entity
	 * @param slot the linked entity's slot
	 * @param weight the initial weight of the link
	 * @param confidence the initial confidence of the link
	 * @throws NetIntegrityException if the linked entity does not exist or has
	 * no slot of the given type or if the requested gate cannot be found.
	 */
	public void createLink(int gate, String to, int slot, double weight, double confidence) throws NetIntegrityException {
		entity.createLinkTo(to, slot, entity.getGate(gate), weight, confidence);
	}
	
	/**
	 * Changes the output function of the gate. 
	 * @param gate the type of the gate that is to be changed
	 * @param outputFunction full qualifying class name of the OutputFunctionIF implementation you want to use 
	 * @throws IllegalArgumentException
	 * @throws ClassNotFoundException
	 * @see GateOutputFunctions
	 */
	public void setOutputFunction(int gate, String outputFunction) throws IllegalArgumentException, ClassNotFoundException {
		entity.getGate(gate).setOutputFunction(GateOutputFunctions.getOutputFunction(outputFunction));
	}
}
