package org.micropsi.comp.agent.kheperaTurtle5.OutputFunctions;


	/*
	 * Created on Jun 3, 2005
	 *
	 * TODO To change the template for this generated file go to
	 * Window - Preferences - Java - Code Style - Code Templates
	 */

	import org.micropsi.nodenet.OutputFunctionIF;
	import org.micropsi.nodenet.OutputFunctionParameter;

	/**
	 * @author Leonhard Laeer
	 *
	 * This output function maps ([0,1],pivot element of [0,1])->(-1,...,pivot=0,...,1).
	 * It's used e.g. to make all ingoing proximity values < pivot negative to make any connected motor
	 * run backwards. Here, the pivot stands e.g. for a distance from the wall the robot will keep. 
	 */
	
	public class OFSensorDistanceContinuous implements OutputFunctionIF{
		/* (non-Javadoc)
		 * @see org.micropsi.nodenet.OutputFunctionIF#getDisplayName()
		 */
		private static String DISPLAYNAME = "SensDistContinuous";
		
		public String getDisplayName() {
			return DISPLAYNAME;
		}

		/* (non-Javadoc)
		 * @see org.micropsi.nodenet.OutputFunctionIF#constructEmptyParameters()
		 */
		public OutputFunctionParameter[] constructEmptyParameters() {
			OutputFunctionParameter[] parameters = new OutputFunctionParameter[4];
			
			//parameter 1: pivot point i.e. zero point for scaling.
			parameters[0] = new OutputFunctionParameter("pivot", 0.0);
			//parameter 2: value range: [0,1]. the minimal absolute value the 
			//multiplicative factor given by this output function can obtain. 
			//(e.g. threshold=0.5 means, that in an area
			//around the pivot value the output is 0.5 or -0.5 respectively. outside 
			//this area the output is an alemat of {[-1,-0.5[, ]0.5,1]}).
			parameters[1] = new OutputFunctionParameter("threshold", 0.0);
			
//		Parameter to change the output of the function at certain positions to another slope of the output:
			
			parameters[2]= new OutputFunctionParameter("discontinous", 0.0);
			parameters[3]= new OutputFunctionParameter("slope", 0.0);
			
			
			return parameters;
		}

		/* (non-Javadoc)
		 * @see org.micropsi.nodenet.OutputFunctionIF#calculate(double, org.micropsi.nodenet.OutputFunctionParameter[])
		 */
		public double calculate(double arg0, OutputFunctionParameter[] arg1) {
			// input arg0 is the proximity distance domain in sensor values [0,1]. 
			// (the function scales the domain to [-1,1] with arg1(=pivot) as an element of [0,1] being the zero point or threshold:)
			
			double pivot = arg1[0].getValue();
			double threshold = arg1[1].getValue();
			double discontinous=arg1[2].getValue();
			double slope=arg1[3].getValue();
			
			double out = 0;
			
			if(arg0 > pivot)
				out = (arg0 - pivot)/(1 - pivot);
			else
				out = -((pivot - arg0)/pivot);
			
			if(out<threshold)
				out -= 2*threshold;
			
			if(discontinous!=0.0){
				if(out<discontinous)
					out=slope*out;
			}
			
			return out;		
		}

	}

