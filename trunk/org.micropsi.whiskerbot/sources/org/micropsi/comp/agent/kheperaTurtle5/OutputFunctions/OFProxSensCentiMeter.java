/*
 * Created on Jun 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.micropsi.comp.agent.kheperaTurtle5.OutputFunctions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.micropsi.nodenet.OutputFunctionIF;
import org.micropsi.nodenet.OutputFunctionParameter;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OFProxSensCentiMeter implements OutputFunctionIF {
  
	private static String DISPLAYNAME = "Sensor2CentiMeter";
  private double[][] matrix = new double[1024][8];
  //private Logger logger = null;
  
	public OFProxSensCentiMeter() {
    
	  for(int i = 0;i<1023;i++)
      for(int j = 0;i<=8;j++)
        matrix[i][j]=1024 - i;
    
    
    /*
    BufferedReader in_cm = null;
    //logger = Logger.getRootLogger;
    //logger.debug("constructor OutputFunction OFProxSensCentiMeter()...");
	  
    for(int i=1;i<=8;i++){
	    
	    try {
	      //in_cm = new BufferedReader(new FileReader("matlab/sensor_prox"+i+"_extrapol_distance.txt"));
        in_cm = new BufferedReader(new FileReader("C:\\sensor_prox"+i+"_extrapol_distance.txt"));
	    } catch (FileNotFoundException e1) {
       // logger.error("(OFProxSensCentiMeter) error opening distance data file for sensor #"+i+"!",e1);
        e1.printStackTrace();
	    }
      
      String cm;
      int j = 1024;
	    try {
	      while( (cm=in_cm.readLine()) != null){
	        //logger.debug("ProxSens"+i+": value "+(j-1)+"="+cm+"cm");
	        matrix[--j][i] = Double.valueOf(cm);	        
	      }
	      
        ///fill rest of array with last value
        while(j != 0){
          matrix[--j][i] = Double.valueOf(cm); 
        }
       
      } catch (IOException e) {
        //logger.error("(OFProxSensCentiMeter) error filling calibration matrix for sensor #"+i+"!",e);
	      e.printStackTrace();
	    }
      
	    try {
	      in_cm.close();
	    } catch (IOException e2) {
        //logger.error("(OFProxSensCentiMeter) error closing file for sensor #"+i+"!",e2);
        e2.printStackTrace();
	    }
	    
	  }
    */
	}


  /* (non-Javadoc)
   * @see org.micropsi.nodenet.OutputFunctionIF#getDisplayName()
   */
	public String getDisplayName() {
		return DISPLAYNAME;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.nodenet.OutputFunctionIF#constructEmptyParameters()
	 */
	public OutputFunctionParameter[] constructEmptyParameters() {

    OutputFunctionParameter[] parameters = new OutputFunctionParameter[1];
	  parameters[0] = new OutputFunctionParameter("ProxSens#", 0.0);
	  
	  return parameters;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.nodenet.OutputFunctionIF#calculate(double, org.micropsi.nodenet.OutputFunctionParameter[])
	 */
	public double calculate(double arg0, OutputFunctionParameter[] arg1) {
		// input arg0 is the proximity distance domain in sensor values [0,1024]. 
		// the function maps the values to cm (using experimentally prepaired calibration data):
		
		int value = (int)arg0;
    int proxSensNumber = (int)(arg1[0].getValue());
		  
    return matrix[value][proxSensNumber-1];
	}

  
}
