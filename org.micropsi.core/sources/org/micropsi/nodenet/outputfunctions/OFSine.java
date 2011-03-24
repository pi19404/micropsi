/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/outputfunctions/OFSine.java,v 1.2 2006/06/27 19:37:54 rvuine Exp $ 
 */
package org.micropsi.nodenet.outputfunctions;

import org.micropsi.nodenet.OutputFunctionIF;
import org.micropsi.nodenet.OutputFunctionParameter;

/**
 * Sine function: f(x) = (sin(x-theta)+1)/2<br>
 * Warning: Sine calculations are expensive! If you don't have good reason to use SINE, use
 * SIGMOIDAL or LOGISTIC instead.
 */
public class OFSine implements OutputFunctionIF {

	/*(non-Javadoc)
	 * @see org.micropsi.nodenet.OutputFunctionIF#getDisplayName()
	 */
	public String getDisplayName() {
		return "Sine";
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
		
		if(value > (theta + (Math.PI /2))) {
			return 1;
		} else if(value < (theta - (Math.PI /2))) {
			return 0;
		} else {
			return (Math.sin(value - theta)+1) / 2;
		}
	}
}
