/*
 * Created on Jun 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.micropsi.comp.agent.kheperaTurtle5.OutputFunctions;

import org.micropsi.nodenet.OutputFunctionIF;
import org.micropsi.nodenet.OutputFunctionParameter;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OFRandomSignal implements OutputFunctionIF {

	/* (non-Javadoc)
	 * @see org.micropsi.nodenet.OutputFunctionIF#getDisplayName()
	 */
	private static String DISPLAYNAME = "RandomSignal";
	
	public String getDisplayName() {
		return DISPLAYNAME;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.nodenet.OutputFunctionIF#constructEmptyParameters()
	 */
	public OutputFunctionParameter[] constructEmptyParameters() {
		OutputFunctionParameter[] parameters = new OutputFunctionParameter[1];
		parameters[0] = new OutputFunctionParameter("factor", 1.0);
//		parameters[0] = new OutputFunctionParameter("range [zero mean]", 1.0);
		
		return parameters;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.nodenet.OutputFunctionIF#calculate(double, org.micropsi.nodenet.OutputFunctionParameter[])
	 */
	public double calculate(double arg0, OutputFunctionParameter[] arg1) {
		
		double value = arg0;
		double factor = arg1[0].getValue();
		
		/* the function adds a randomly ([-1,1], equally distributed) 
		 * factorized version of the input to itself. the factor parameter
		 * scales the effect:
	     */
//		return value + (((Math.random() * 2) - 1.0) * value * factor);
		if(value > 7)
			return value;
		else
			if(Math.random() < 0.9)
				return value;
			else
				return value - factor;
	}

}
