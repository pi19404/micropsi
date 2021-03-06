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
 * @author Leonhard L�er
	 *
	 * This output function maps ([0,1],pivot element of [0,1])->{-1 if arg<pivot, 1 else).
	 * It's used e.g. to make all ingoing proximity values < pivot negative to make any connected motor
	 * run backwards. Here, the pivot stands for a distance from the wall the robot will keep. 
 */
public class OFSensorDistanceDiscrete implements OutputFunctionIF {

	/* (non-Javadoc)
	 * @see org.micropsi.nodenet.OutputFunctionIF#getDisplayName()
	 */
	private static String DISPLAYNAME = "SensDistDiscrete";
	
	public String getDisplayName() {
		return DISPLAYNAME;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.nodenet.OutputFunctionIF#constructEmptyParameters()
	 */
	public OutputFunctionParameter[] constructEmptyParameters() {
		OutputFunctionParameter[] parameters = new OutputFunctionParameter[1];
		parameters[0] = new OutputFunctionParameter("pivot", 0.0);
		
		return parameters;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.nodenet.OutputFunctionIF#calculate(double, org.micropsi.nodenet.OutputFunctionParameter[])
	 */
	public double calculate(double arg0, OutputFunctionParameter[] arg1) {
		// input arg0 is the proximity distance domain in sensor values [0,1]. 
		// (the function scales the domain to [-1,1] with arg1 as an element of [0,1] being the zero point or threshold:)
		
		double threshold = arg1[0].getValue();
		
//		if ((arg0 - threshold) > 0.0)
		if(arg0 < threshold)
			return -1;
		else 
			return 1;//(arg0 - threshold);
	}

}
