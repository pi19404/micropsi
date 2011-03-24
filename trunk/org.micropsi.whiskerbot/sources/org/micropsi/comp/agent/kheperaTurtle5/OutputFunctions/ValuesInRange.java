package org.micropsi.comp.agent.kheperaTurtle5.OutputFunctions;

import org.micropsi.nodenet.OutputFunctionIF;
import org.micropsi.nodenet.OutputFunctionParameter;

public class ValuesInRange implements OutputFunctionIF {

	
	public static String Displayname="ValuesInRange";
	
	
	public String getDisplayName() {
		
		return Displayname;
	}

	public OutputFunctionParameter[] constructEmptyParameters() {
		
		OutputFunctionParameter[] parameters = new OutputFunctionParameter[2];
		parameters[0] = new OutputFunctionParameter("threshold", 0);
		parameters[1]= new  OutputFunctionParameter("mistake",0);
		return parameters;
	}

	public double calculate(double activation, OutputFunctionParameter[] arg1) {
		
		double thres=arg1[0].getValue();
		double mistake=arg1[1].getValue();
		
		if(activation>(thres+mistake) || activation<(thres-mistake))
			activation=0;
		
		return activation;
	}

}
