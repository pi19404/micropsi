/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/Gate.java,v 1.9 2006/10/15 15:39:35 rvuine Exp $
 */
package org.micropsi.nodenet;

import java.util.ArrayList;
import java.util.Iterator;

import org.micropsi.nodenet.outputfunctions.OFSemilinear;

/**
 * The links between NetEntitys originate from Gates. Gates have parameters that
 * affect the way activation is propagated, similar to neurons in connectionist
 * networks. The output of a gate is calculated by one of the functions defined
 * in the class GateOutputFunctions. These functions may make use of the
 * parameters activation, maximum, minimum and the "unbound" parameter
 * theta, the latter with different semantics for each output function. <br>
 * Whatever the output function may calculated, it's value is then multiplied by
 * the gate parameter "ampfactor", the result again checked to be between
 * minimum and maximum.
 * <br><br>
 * Additionaly, gates calculate the decay of the attached link's weights
 * whenever these links are used.<br/><br/>
 * Every gate has a <i> type </i> that MUST be unique within one entity.<br><br>
 */
public final class Gate {

	private static final int DEFAULT_GATELOAD_ASSUMPTION = 2;
	
	/**
	 * the type of the gate (must be unique within the gate's entity)
	 */
	private int type = -1;
	
	/**
	 * the activation
	 */
	private double activation = 0;
	
	/**
	 * confirmed activation. Activation is confirmed after all entities have
	 * calculated their gates. During gate calculation all entities access
	 * other entitie's old confirmed activation
	 */
	private double confirmedActivation = 0;
	
	
	/**
	 * the gate factor. This number is multiplied with the activation right
	 * before the treshold check and can be used to explicitly "turn on" or
	 * "turn off" the gate and all links attached.
	 */
	private double gateFactor = 1;
		
	/**
	 * the ampfactor. This number is multiplied to the activation after the
	 * threshold check.
	 */
	private double ampfactor = 1.0;
	
	/**
	 * the maximum output of the gate. The activation, even multiplied with the
	 * ampfactor, can never exceed this number.
	 */
	private double maximum = 1.0;
	
	/**
	 * the minimum output of the gate. The activation, if it exceeds the
	 * threshold, can never be smaller than this number. (That makes sense as
	 * the threshold is substracted from the activation at the threshold check)
	 */
	private double minimum = -1.0;
	
	/**
	 * the type of decay to be applied to the gate's links, defaults to "no decay"
	 */
	private int decayCalculatorType = -1;
	
	/**
	 * the output function to be used, defaults to the doernerian semilinear function
	 */	
	private OutputFunctionIF outputFunction = GateOutputFunctions.getOutputFunction(OFSemilinear.class);
	
	/**
	 * the output functions constant parameters
	 */
	private OutputFunctionParameter[] ofParams = outputFunction.constructEmptyParameters();
	
	/**
	 * the netstep when the last decay calculation was performed.
	 */
	private long lastDecayCalculation = 0;
	
	/**
	 * the entity that the gate is part of.
	 */
	private NetEntity center;

	/**
	 * the links from this gate
	 */
	private ArrayList<Link> links;

	/**
	 * Constructor for the gate, setting it's type, center-entity and the decay
	 * type of the links.
	 * @param type the gate's type
	 * @param center the NetEntity that the gate belongs to
	 * @param decayType the type of decay to be applied
	 */
	protected Gate(int type, NetEntity center, int decayType) {
		this.type = type;
		this.center = center;
		this.decayCalculatorType = decayType;
		this.links = new ArrayList<Link>(DEFAULT_GATELOAD_ASSUMPTION);		
	}
	
	/**
	 * Returns the gate's "center" entity, the entity that the gate belongs to.
	 * @return NetEntity the entity
	 */
	public NetEntity getNetEntity() {
		return center;
	} 	
	
	/**
	 * Adds a link to the gate. This method is low-level and does not
	 * care for the net's integrity in any way.
	 * @param link the link to be added to the gate.
	 */
	protected void addLink(Link link) {
		links.add(link);
	}
	
	/**
	 * Deletes a link from the gate. (Must be the instance of the link!) This
	 * method is low-level and does not care for integrity.
	 * @param link the link to be deleted
	 */
	protected void deleteLink(Link link) {
		links.remove(link);
	}
	
	/**
	 * Unlinks the entity completely - for cleaning up or to prepare the entity
	 * for deletion. This is a high-level method and ensures that the link
	 * references are dropped correctly by the gate and the linked slots. 
	 * @throws NetIntegrityException if there are bad links at this gate.
	 */
	protected void unlinkCompletely() throws NetIntegrityException {
		for(int i=links.size()-1;i>=0;i--) {
			Link l = links.get(i);
			
			// remove the remote reference to the link object
			l.getLinkedSlot().detachIncomingLink(l);
			
			// remove our reference
			links.remove(i);
			
			l.destroy();
		}
	}
	
	/**
	 * Returns an iterator with instances of Link. Do not call remove() or the
	 * net's integrity will be violated badly.
	 * @return Iterator the links
	 */	
	public Iterator<Link> getLinks() {
		return links.iterator();
	}
	
	/**
	 * Returns the nth link attached to this gate
	 * @param n the index of the link
	 * @return Link the link or null if there is no nth link
	 */
	public Link getLinkAt(int n) {
		if(n>=links.size() || n < 0) return null;
		return links.get(n);
	}
	
	/**
	 * Returns a specific link
	 * @param entityID the ID of the linked entity 
	 * @param slot the slot where the link ends
	 * @return Link the link or null if there is no such link
	 * @throws NetIntegrityException if the requested link is bad
	 */	
	public Link getLinkTo(String entityID, int slot) throws NetIntegrityException {
		for(int i=0;i<links.size();i++) { 
			Link l = links.get(i); 
			if(l.getLinkedEntityID().equals(entityID) && l.getLinkedSlot().getType() == slot)
				return l;
		}
		return null;
	}
		
	/**
	 * Sets the gate factor.
	 * @param factor the gateFactor.
	 */
	protected void setGateFactor(double factor) {
		gateFactor = factor;
	}
	
	/**
	 * Returns the gateFactor.
	 * @return double the gateFactor
	 */
	public double getGateFactor() {
		return gateFactor;
	}
	
	/**
	 * Returns the gate's "raw" activation, without having it multiplied or
	 * checked it against anything. This should only be used for transporting or
	 * saving the net's state, not for any logical operation!
	 * @return double the raw activation of the gate.
	 */
	protected double getActivation() {
		return activation;
	}
	
	/**
	 * Returns the confirmed activation of this gate.
	 * @return double
	 */
	public double getConfirmedActivation() {
		return confirmedActivation;
	}
		
	/**
	 * Calculates the gate's activation.
	 * @return double the activation.
	 */
	private double calculateActivation() {
						
		double tmp = outputFunction.calculate(activation*gateFactor, confirmedActivation, ofParams);

		tmp *= ampfactor;

		tmp = (tmp > maximum) ? maximum : tmp;
		tmp = (tmp < minimum) ? minimum : tmp;

		return tmp;
	}
	
	/**
	 * Confirms the current activation as authoritative during the next cycle
	 */
	protected boolean confirmActivation() {
		confirmedActivation = calculateActivation();		
		return confirmedActivation != 0;
	}
	
	/**
	 * Checks if the gate is "active", that is: if the gate's activation 
	 * ist != 0<br><br>
	 * @return boolean true if the gate is active
	 */
	public boolean isActive() {
		return confirmedActivation != 0;
	}
		
	/**
	 * Calculates the decay for all attached links. This actually changes the
	 * weights of the links!
	 * @param netstep
	 */
	protected void calculateDecays(long netstep) throws NetIntegrityException {
		for(int i=links.size()-1;i>=0;i--) {
			Link l = links.get(i);
			l.setWeight(
				DecayCalculator.calculateWeigth(decayCalculatorType,l.getWeight(),netstep-lastDecayCalculation,center)
			);
			if(l.getWeight() == 0) {
				
				if(((NodeSpaceModule)center.entityManager.getEntity(center.getParentID())).isDecayAllowed()) {
					// remove the link:
					// remove the remote reference to the link object
					l.getLinkedSlot().detachIncomingLink(l);
				
					// remove our reference
					links.remove(i);
				
					l.destroy();
				}
			}
		}
		lastDecayCalculation = netstep;
	}
	
	/**
	 * Returns the type
	 * @return int the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns the ampfactor.
	 * @return double the ampfactor
	 */
	public double getAmpfactor() {
		return ampfactor;
	}

	/**
	 * Returns the decayCalculatorType.
	 * @return int
	 */
	public int getDecayCalculatorType() {
		return decayCalculatorType;
	}

	/**
	 * Retuns the type of the output function used in this gate
	 * @return int
	 */
	public OutputFunctionIF getOutputFunction() {
		return outputFunction;
	}

	/**
	 * Returns the maximum output of the gate.
	 * @return double
	 */
	public double getMaximum() {
		return maximum;
	}

	/**
	 * Returns the minimum output of the (active) gate
	 * @return double
	 */
	public double getMinimum() {
		return minimum;
	}
	
	/**
	 * Sets the activation.
	 * @param activation The activation to set
	 */
	protected void setActivation(double activation) {		
		this.activation = activation;
	}

	/**
	 * Sets the confirmed activation. This should only be called for
	 * persistency reasons!
	 * @param activation The activation to set
	 */
	protected void setConfirmedActivation(double activation) {
		this.confirmedActivation = activation;
	}

	/**
	 * Sets the ampfactor.
	 * @param ampfactor The ampfactor to set
	 */
	protected void setAmpfactor(double ampfactor) {
		this.ampfactor = ampfactor;
	}

	/**
	 * Sets the decayCalculatorType.
	 * @param decayCalculatorType The decayCalculatorType to set
	 */
	protected void setDecayCalculatorType(int decayCalculatorType) {
		if(this.decayCalculatorType != decayCalculatorType)
			lastDecayCalculation = center.entityManager.getNetstep();
	
		this.decayCalculatorType = decayCalculatorType;
	}
	
	/**
	 * Sets the output function.
	 * @param function The function to be used.
	 */
	protected void setOutputFunction(OutputFunctionIF function) {
		outputFunction = function;
		ofParams = outputFunction.constructEmptyParameters();
	}
	
	/**
	 * Sets the maximum.
	 * @param maximum The maximum to set
	 */
	protected void setMaximum(double maximum) {
		this.maximum = maximum;
	}

	/**
	 * Returns the current parameters of the output function.
	 * @return an array with parameters, filled with current values. Never null.
	 */
	public OutputFunctionParameter[] getCurrentOutputFunctionParameters() {
		return ofParams;
	}

	/**
	 * Returns the current value of a given output function parameter 
	 * @param parameterName the name of the parameter
	 * @return the current value of the parameter
	 * @throws IllegalArgumentException if the output function of this gate does not support the given parameter
	 */
	public double getOutputFunctionParameter(String parameterName) {
		OutputFunctionParameter[] params = getCurrentOutputFunctionParameters();
		for(int i=0;i<params.length;i++) {
			if(params[i].getName().equals(parameterName)) {
				return 	params[i].getValue();
			}
		}
		
		throw new IllegalArgumentException("parameter "+parameterName+" not found for output function "+getOutputFunction());
	}

	
	/**
	 * Sets one of tge output function's constant parameters. 
	 * @param parameterName the name of the parameter
	 * @param value the new value
	 * @throws IllegalArgumentException if the output function does not have this parameter
	 */
	protected void setOutputFunctionParameter(String parameterName, double value) {
		OutputFunctionParameter[] params = getCurrentOutputFunctionParameters();
		for(int i=0;i<params.length;i++) {
			if(params[i].getName().equals(parameterName)) {
				params[i].setValue(value);
				return;
			}
		}
		
		throw new IllegalArgumentException("parameter "+parameterName+" not found for output function "+getOutputFunction());
	}
	
	/**
	 * Sets all output function parameters at once.
	 * This method does not check wheter the gate's output function supports the given parameters.
	 * @param ofParams the parameters.
	 */
	protected void setOutputFunctionParameters(OutputFunctionParameter[] ofParams) {
		this.ofParams = ofParams;		
	}
	
	/**
	 * Method setMinimum.
	 * @param minimum
	 */
	protected void setMinimum(double minimum) {
		this.minimum = minimum;
	}
	
	/**
	 * Returns the number of links
	 * @return int
	 */
	public int getNumberOfLinks() {
		return this.links.size();
	}
	
	/**
	 * Returns true if there are links at this gate
	 * @return boolean
	 */
	public boolean hasLinks() {
		return this.links.size() > 0;
	}

	/**
	 * @return long
	 */
	protected long getLastDecayCalculation() {
		return lastDecayCalculation;
	}

	/**
	 * Sets the lastDecayCalculation.
	 * @param lastDecayCalculation The lastDecayCalculation to set
	 */
	protected void setLastDecayCalculation(long lastDecayCalculation) {
		this.lastDecayCalculation = lastDecayCalculation;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return super.toString()+" Type: "+type+" Act: "+confirmedActivation;
	}
	
	/**
	 * Frees as many references as possible to avoid too much gc activity
	 */
	protected void destroy() {
		Iterator iter = getLinks();
		while(iter.hasNext()) {
			Link l = (Link)iter.next();
			l.destroy();
			iter.remove();
		}
		links = null;
		center = null;
	}

}
