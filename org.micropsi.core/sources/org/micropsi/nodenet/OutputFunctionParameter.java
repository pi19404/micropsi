/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/OutputFunctionParameter.java,v 1.3 2005/07/11 22:13:20 vuine Exp $ 
 */
package org.micropsi.nodenet;

/**
 * OutputFunctionParameter. An instance of this class holds the constant parameter of a gate's output
 * function at runtime.
 * 
 * An output function has a fixed number of parameters, but every gate has a different value for its
 * function's constatn parameters, of course.
 */
public final class OutputFunctionParameter {

	private String onlyLettersAndDigits(String str) {
		
		StringBuffer newString = new StringBuffer(str.length());
		for(int i=0;i<str.length();i++) {
			if(!Character.isLetterOrDigit(str.charAt(i))) {
				newString.append('_');
			} else {
				newString.append(str.charAt(i));
			}
		}
		
		return newString.toString();
	}
	
	/**
	 * The parameter name
	 */
	private String name;
	
	/**
	 * The parameter value
	 */
	private double value = 0.0;
	
	/**
	 * Creates an output function parameter instance.
	 * This should ONLY be called in the constructEmptyParameters() method of implementations of
	 * OutputFunctionIF.
	 * The parameter will be default-initialized with 0.0
	 * 
	 * @param name the name of the output function
	 */
	public OutputFunctionParameter(String name) {
		this.name = onlyLettersAndDigits(name);
	}

	
	/**
	 * Creates an output function parameter instance.
	 * This should ONLY be called in the constructEmptyParameters() method of implementations of
	 * OutputFunctionIF.
	 * The parameter will be default-initialized with the given value
	 * 
	 * @param name the name of the output function
	 * @param defaultValue
	 */
	public OutputFunctionParameter(String name, double defaultValue) {
		this.name = name;
		this.value = defaultValue;
	}

	/**
	 * Sets the current value of the constant parameter represented by this object.
	 * 
	 * @param newValue the new value
	 */
	protected void setValue(double newValue) {
		this.value = newValue;
	}
	
	/**
	 * Returns the current value of the constant parameter represented by this object.
	 * 
	 * @return the value of the constant represented by this object.
	 */
	public double getValue() {
		return value;
	}
	
	/**
	 * The name of the parameter represented by this object.
	 * 
	 * @return the parameter name
	 */
	public String getName() {
		return name;
	}
	
}
