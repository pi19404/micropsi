package org.micropsi.nodenet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.micropsi.nodenet.outputfunctions.OFAbs;
import org.micropsi.nodenet.outputfunctions.OFBandpass;
import org.micropsi.nodenet.outputfunctions.OFGauss;
import org.micropsi.nodenet.outputfunctions.OFHighpass;
import org.micropsi.nodenet.outputfunctions.OFKBANN;
import org.micropsi.nodenet.outputfunctions.OFLinear;
import org.micropsi.nodenet.outputfunctions.OFLogistic;
import org.micropsi.nodenet.outputfunctions.OFLowpass;
import org.micropsi.nodenet.outputfunctions.OFSemilinear;
import org.micropsi.nodenet.outputfunctions.OFSigmoidal;
import org.micropsi.nodenet.outputfunctions.OFSine;
import org.micropsi.nodenet.outputfunctions.OFTanh;

/**
 * Contains the supported output function types, calculations and string constants.
 */
public class GateOutputFunctions {

	/**
	 * Simple threshold function (as in Doerner's theory): f(x) = x-theta if (|x| > theta), else 0
	 * @deprecated don't use this constants any more. Output functions are now represented by
	 * class names or objects. See the static methods in this class. 
	 */
	public static final int GOF_SEMILINEAR = 0;
	
	/**
	 * Linar function: f(x) = theta * x, min and max limiting. Note that theta is
	 * a factor here, not a threshold.
	 * @deprecated don't use this constants any more. Output functions are now represented by
	 * class names or objects. See the static methods in this class. 
	 */
	public static final int GOF_LINEAR = 1;
	
	/**
	 * Sine function: f(x) = (sin(x-theta)+1)/2<br>
	 * Warning: Sine calculations are expensive! If you don't have good reason to use SINE, use
	 * SIGMOIDAL or LOGISTIC instead.
	 * @deprecated don't use this constants any more. Output functions are now represented by
	 * class names or objects. See the static methods in this class. 
	 */
	public static final int GOF_SINE = 2;
	
	/**
	 * Sigmoidal function: f(x) = 1 / (1 + exp(theta * -value)). Note that this is very
	 * similar to the logistic function, but theta is used as a factor, not a threshold.
	 * @deprecated don't use this constants any more. Output functions are now represented by
	 * class names or objects. See the static methods in this class. 
	 */
	public static final int GOF_SIG_MULT = 3;
	
	/**
	 * Logistic function (also KBANN-style): f(x) = 1 / (1 + exp(-(value - theta)))
	 * @deprecated don't use this constants any more. Output functions are now represented by
	 * class names or objects. See the static methods in this class. 
	 */
	public static final int GOF_LOGISTIC = 4;
	
	/**
	 * Tangens hyperbolicus. f(x) = 2 / (1 + exp(-2*(x-theta)))
	 * @deprecated don't use this constants any more. Output functions are now represented by
	 * class names or objects. See the static methods in this class. 
	 */
	public static final int GOF_TANH = 5;
	
	/**
	 * Gaussian function. f(x) = exp(-(theta * x)²)
	 * @deprecated don't use this constants any more. Output functions are now represented by
	 * class names or objects. See the static methods in this class. 
	 */
	public static final int GOF_GAUSSIAN = 6;
	
	/** 
	 * Highpass threshold: f(x) = x, if (x >= theta), 0 else
	 * @deprecated don't use this constants any more. Output functions are now represented by
	 * class names or objects. See the static methods in this class.  
	 */
	public static final int GOF_HIGHPASS = 7;

	/** 
	 * Lowpass threshold: f(x) = x, if (x <= theta), 0 else
	 * @deprecated don't use this constants any more. Output functions are now represented by
	 * class names or objects. See the static methods in this class.  
	 */
	public static final int GOF_LOWPASS = 8;

	/** 
	 * Bandpass threshold: f(x) = x, if (x >= theta and x <= 1-theta), 0 else
	 * Note that normally, theta should be a value between 0 and 0.5
	 * @deprecated don't use this constants any more. Output functions are now represented by
	 * class names or objects. See the static methods in this class.  
	 */
	public static final int GOF_BANDPASS = 9;
    
    /**
     * KBANN-style steep activation function. (This is a logistic function with high gradient)
     * f(x) = 1 / (1 + exp(10 * -(value -theta2))
	 * @deprecated don't use this constants any more. Output functions are now represented by
	 * class names or objects. See the static methods in this class.  
     */
	public static final int GOF_KBANN = 10;

	/**
	 * @deprecated don't use this constants any more. Output functions are now represented by
	 * class names or objects. See the static methods in this class.  
	 */
	public static final int GOF_KBANN_4 = 12;
	/**
	 * @deprecated don't use this constants any more. Output functions are now represented by
	 * class names or objects. See the static methods in this class.  
	 */
	public static final int GOF_KBANN_6 = 13;
	/**
	 * @deprecated don't use this constants any more. Output functions are now represented by
	 * class names or objects. See the static methods in this class.  
	 */
	public static final int GOF_KBANN_8 = 14;
	/**
	 * @deprecated don't use this constants any more. Output functions are now represented by
	 * class names or objects. See the static methods in this class.  
	 */
	public static final int GOF_KBANN_2 = 15;
	
	/**
	 * abs activation function. (theta has no effect)
	 * @deprecated don't use this constants any more. Output functions are now represented by
	 * class names or objects. See the static methods in this class.  
	 */
	public static final int GOF_ABS	= 11;
	
	//---------------------------------------------------------------------------
	
	static {
	
		registerOutputFunction(OFSemilinear.class);
		registerOutputFunction(OFLinear.class);
		registerOutputFunction(OFSine.class);			
		registerOutputFunction(OFSigmoidal.class);
		registerOutputFunction(OFLogistic.class);
		registerOutputFunction(OFTanh.class);
		registerOutputFunction(OFGauss.class);
		registerOutputFunction(OFLowpass.class);
		registerOutputFunction(OFHighpass.class);
		registerOutputFunction(OFBandpass.class);
		registerOutputFunction(OFKBANN.class);
		registerOutputFunction(OFAbs.class);
		
	}
	
	
	private static HashMap<String,OutputFunctionIF> outputFunctions;

	public static void registerOutputFunction(Class outputFunction) throws IllegalArgumentException {
		
		if(outputFunctions == null) {
			 outputFunctions = new HashMap<String,OutputFunctionIF>();
		}
		
		try {
			OutputFunctionIF function = (OutputFunctionIF)outputFunction.newInstance();
			outputFunctions.put(outputFunction.getName(),function);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Output function "+outputFunction+" is not valid. (No default constructor? Doesn't implement OutputFunctionIF?)");
		}
	}
	
	public static OutputFunctionIF getOutputFunction(Class outputFunction) throws IllegalArgumentException {
		if(outputFunctions == null) {
			 outputFunctions = new HashMap<String,OutputFunctionIF>();
		}

		
		if(!outputFunctions.containsKey(outputFunction.getName())) {
			registerOutputFunction(outputFunction);
		}
		
		return outputFunctions.get(outputFunction.getName());
	}

	public static OutputFunctionIF getOutputFunction(String outputFunction) throws IllegalArgumentException, ClassNotFoundException {
		if(outputFunctions == null) {
			 outputFunctions = new HashMap<String,OutputFunctionIF>();
		}
		
		if(!outputFunctions.containsKey(outputFunction)) {
			registerOutputFunction(Class.forName(outputFunction));
		}
		
		return outputFunctions.get(outputFunction);
	}
	
	public static List<OutputFunctionIF> getOutputFunctions() {
		if(outputFunctions == null) {
			 outputFunctions = new HashMap<String,OutputFunctionIF>();
		}
		
		return new ArrayList<OutputFunctionIF>(outputFunctions.values());
	}

	
	
	// for backwards compatibility
	// TODO: remove

	/**
	 * @deprecated provided only for the NetPersistencyManager for backward compatibility
	 * reasons
	 */
	protected static final String typeToClassName(int type) {
		switch(type) {
			case GOF_SEMILINEAR:		return OFSemilinear.class.getName();
			case GOF_LINEAR:			return OFLinear.class.getName();
			case GOF_SINE:				return OFSine.class.getName();
			case GOF_SIG_MULT:			return OFSigmoidal.class.getName();
			case GOF_LOGISTIC: 			return OFLogistic.class.getName();
			case GOF_TANH:				return OFTanh.class.getName();
			case GOF_GAUSSIAN:			return OFGauss.class.getName();
			case GOF_LOWPASS:			return OFLowpass.class.getName();
			case GOF_HIGHPASS:			return OFHighpass.class.getName();
			case GOF_BANDPASS:			return OFBandpass.class.getName();
			case GOF_KBANN:				return OFKBANN.class.getName();
			case GOF_KBANN_2:			return OFKBANN.class.getName();
			case GOF_KBANN_4:			return OFKBANN.class.getName();
			case GOF_KBANN_6:			return OFKBANN.class.getName();
			case GOF_KBANN_8:			return OFKBANN.class.getName();
			case GOF_ABS:				return OFAbs.class.getName();
			default:					return null;
		}
	}
	
	// ----------------------------------------------------------------------------

}

