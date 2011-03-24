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
public class OFProxSensDist implements OutputFunctionIF {

	/* (non-Javadoc)
	 * @see org.micropsi.nodenet.OutputFunctionIF#getDisplayName()
	 */
	private static String DISPLAYNAME = "SensorSensitivity";
	
	public String getDisplayName() {
		return DISPLAYNAME;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.nodenet.OutputFunctionIF#constructEmptyParameters()
	 */
	public OutputFunctionParameter[] constructEmptyParameters() {
		OutputFunctionParameter[] parameters = new OutputFunctionParameter[1];
		parameters[0] = new OutputFunctionParameter("threshold", 0.0);
		
		return parameters;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.nodenet.OutputFunctionIF#calculate(double, org.micropsi.nodenet.OutputFunctionParameter[])
	 */
	public double calculate(double arg0, OutputFunctionParameter[] arg1) {
		// input arg0 is the proximity distance domain in sensor values [0,1]. 
		// (the function scales the domain to [-1,1] with arg1 as an element of [0,1] being the zero point or threshold:)
		
		double threshold = arg1[0].getValue();
		
		if ((arg0 - threshold) > threshold)
			return arg0;
		else 
			return -1;//(arg0 - threshold);
	}

}
