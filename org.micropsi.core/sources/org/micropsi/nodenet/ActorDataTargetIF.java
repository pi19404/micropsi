/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/ActorDataTargetIF.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.nodenet;

/**
 * Implementations of this interface can receive information from a MicroPSI
 * net: After registering the implementation with the net, one ore more
 * ActorNodes can be connected to the datatarget.
 */

public interface ActorDataTargetIF {

	/**
	 * Returns the unique name of the DataTarget that will be used by the
	 * ActorNodes to identify their targets.
	 * @return String the unique name of the datatarget
	 */
	public String getDataType();
	
	/**
	 * Whenever a ActorNode that is connected to the implementation of this
	 * interface fires, it's outgoing activation will be passed to this method
	 * @param value the activation of the ActorNode 
	 */
	public void addSignalStrength(double value);
	
	/**
	 * Implementations should return values > 0 here if the execution of the
	 * corresponding action was successfull. Some actors may be fire-and-
	 * forget, and of course it's not relevant what's returned then.
	 * @return double the success of the previous execution
	 */
	public double getExecutionSuccess();
	

}
