/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/outputfunctions/OFKBANN.java,v 1.2 2006/06/27 19:37:54 rvuine Exp $ 
 */
package org.micropsi.nodenet.outputfunctions;

import org.micropsi.nodenet.OutputFunctionIF;
import org.micropsi.nodenet.OutputFunctionParameter;

/**
 * KBANN-style steep activation function. (This is a logistic function with high gradient)
 * f(x) = 1 / (1 + exp(rho * -(value -theta2))
 * 
 * rho defaults to 10 
 */
public class OFKBANN implements OutputFunctionIF {

	/*(non-Javadoc)
	 * @see org.micropsi.nodenet.OutputFunctionIF#getDisplayName()
	 */
	public String getDisplayName() {
		return "KBANN";
	}

	/*(non-Javadoc)
	 * @see org.micropsi.nodenet.OutputFunctionIF#getParameters()
	 */
	public OutputFunctionParameter[] constructEmptyParameters() {
		return new OutputFunctionParameter[] {
			new OutputFunctionParameter("theta"),
			new OutputFunctionParameter("rho",10)
		};
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.nodenet.OutputFunctionIF#calculate(double, double, org.micropsi.nodenet.OutputFunctionParameter[])
	 */
	public double calculate(double value, double lastCycleResult, OutputFunctionParameter[] params) {
		double theta = params[0].getValue();
		double rho = params[1].getValue();

        return (1 / (1 + Math.exp(rho * -(value-theta))));

	}
}
