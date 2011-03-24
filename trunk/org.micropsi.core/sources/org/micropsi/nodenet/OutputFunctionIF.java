/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/OutputFunctionIF.java,v 1.2 2006/06/27 19:37:54 rvuine Exp $ 
 */
package org.micropsi.nodenet;

/**
 * OutputFunctionIF. An output function is a function f(x) = y. f can be
 * parametrized by any number of constant parameters. 
 *
 * @author rv
 */
public interface OutputFunctionIF {
	
	/**
	 * Returns the display name of this output function.
	 * The string returned will be used in the editor and
	 * has no technical meaning. 
	 * 
	 * @return a String to be used in the editor for identifying this output function ,must not be null
	 */
	public String getDisplayName();

	/**
	 * Defines the set of parameters for this output function. The default semilinear
	 * output function, for instance, will return one parameter, {theta}.
	 * You can define any number of parameters here. These parameters will be passed to
	 * the calculate method in the order you define here, so you can quickly access
	 * the parameters by index in calculate(...)  
	 * 
	 * @return the array of parameters this function uses, must not be null
	 */
	public OutputFunctionParameter[] constructEmptyParameters();
	
	/**
	 * When a gate uses this output function and its value is to be calculated, this
	 * method will be called. The given parameters are the objects returned on
	 * initialisation by getParameters(), but with the current values. The order defined
	 * in constructEmptyParameters() will be preserved, so implementations of this method can
	 * access the parameter objects by index.
	 * 
	 * @param value the gate value
	 * @param lastCycleResult the confirmed activation of this gate in the last cycle
	 * @param params the parameter array, never null
	 * @return the output function result
	 */
	public double calculate(double value, double lastCycleResult, OutputFunctionParameter[] params);
	
}
