/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/outputfunctions/OFLogistic.java,v 1.2 2006/06/27 19:37:54 rvuine Exp $ 
 */
package org.micropsi.nodenet.outputfunctions;

import org.micropsi.nodenet.OutputFunctionIF;
import org.micropsi.nodenet.OutputFunctionParameter;

/**
 * Logistic function (also KBANN-style): f(x) = 1 / (1 + exp(-(value - theta)))
 */
public class OFLogistic implements OutputFunctionIF {

	/*(non-Javadoc)
	 * @see org.micropsi.nodenet.OutputFunctionIF#getDisplayName()
	 */
	public String getDisplayName() {
		return "Logistic";
	}

	/*(non-Javadoc)
	 * @see org.micropsi.nodenet.OutputFunctionIF#getParameters()
	 */
	public OutputFunctionParameter[] constructEmptyParameters() {
		return new OutputFunctionParameter[] {
			new OutputFunctionParameter("theta"),
		};
	}

	/*
	 * (non-Javadoc)
	 * @see org.micropsi.nodenet.OutputFunctionIF#calculate(double, double, org.micropsi.nodenet.OutputFunctionParameter[])
	 */
	public double calculate(double value, double lastCycleResult, OutputFunctionParameter[] params) {
		double theta = params[0].getValue();
		return 1 / (1+(Math.exp(-(value-theta))));
	}
}
