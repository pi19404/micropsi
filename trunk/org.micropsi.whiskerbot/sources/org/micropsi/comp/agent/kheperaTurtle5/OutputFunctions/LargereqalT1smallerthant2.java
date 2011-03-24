package org.micropsi.comp.agent.kheperaTurtle5.OutputFunctions;


import org.micropsi.nodenet.OutputFunctionIF;
import org.micropsi.nodenet.OutputFunctionParameter;

public class LargereqalT1smallerthant2 implements OutputFunctionIF {

	
	
	private static String DISPLAYNAME = "LargereuqalT1smallerthanT2";
	
	
	
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return DISPLAYNAME;
	}

	public OutputFunctionParameter[] constructEmptyParameters() {
		
		OutputFunctionParameter[] parameters = new OutputFunctionParameter[2];
		parameters[0] = new OutputFunctionParameter("lowerbound", 0.0);
		parameters[1] = new OutputFunctionParameter("upperbound", 0.0);
		
		return parameters;

	}

	public double calculate(double arg0, OutputFunctionParameter[] arg1) {
		double thres1=arg1[0].getValue();
		double thres2=arg1[1].getValue();
		
		if((arg0>=thres2)|(arg0<thres1))
			arg0=0;
		else arg0=1;
		
		return arg0;
	}

}
