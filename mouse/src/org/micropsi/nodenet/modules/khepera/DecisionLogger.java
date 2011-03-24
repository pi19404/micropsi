package org.micropsi.nodenet.modules.khepera;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

public class DecisionLogger {
   
    
    /**
     * In the debug modus the data is saved in decisionLog in the following order (of rows)
     * 0 - netstep
     * 1 - decision
     * 2 - x-coordinate
     * 3 - y-coordinate
     * 4 - orientation
     * 5 - last placefield (where a decision was drawn)
     * 6 - the current placefield
     * 7 - the (gauss) activation of the last netstep
     * 8 - the current (gauss) activation
     * 9 - 12 the current urgeinput[0] -urgeinput[3]
     * 13- Reflex value, if the robot touched the wall
     * 14- Rotate, when the robot rotate to get the orientation of the decision
     * 15- nodecision ask if it is possible to make a decision
     * 16- orientation of the robot
     * 17-20 proximity snesors
     */
    
	
	
	private DenseDoubleMatrix2D decisionLog;
	private double [] Activation;
	private int numcol;
	
	public DecisionLogger(int numr,int numc){
		
		this.decisionLog= new DenseDoubleMatrix2D(numr,numc);
//		this.Activation=new double [numc];
		this.numcol=numc;
	}
	
	public void logUpdate(int number,long netstep,int[] position,double orientation,int lastpla,double last_act,double[]urgeinput,double[] placeInfo,double[] sensor,double reflex,double rotate,int nodecision){	
		
		
		this.decisionLog.set(0,number,netstep);
		this.decisionLog.set(2,number,(int)position[0]);
		this.decisionLog.set(3,number,(int)position[1]);
		this.decisionLog.set(5,number,lastpla);
		this.decisionLog.set(6,number,placeInfo[0]);
		this.decisionLog.set(7,number,last_act);
		this.decisionLog.set(8,number,placeInfo[1]);
		this.decisionLog.set(9,number,urgeinput[0]);
		this.decisionLog.set(10,number,urgeinput[1]);
		this.decisionLog.set(11,number,urgeinput[2]);
		this.decisionLog.set(12,number,urgeinput[3]);
		this.decisionLog.set(13,number,reflex);
		this.decisionLog.set(14,number,rotate);
		this.decisionLog.set(15,number,nodecision);
		this.decisionLog.set(16,number,orientation);
		this.decisionLog.set(17,number,sensor[0]);
		this.decisionLog.set(18,number,sensor[1]);
		this.decisionLog.set(19,number,sensor[2]);
		this.decisionLog.set(20,number,sensor[3]);
//		this.Activation[number]=placeInfo[1];
	}
	
	public void logupdateDecision(int number,int decision,double orienation){
		this.decisionLog.set(1,number,decision);
		this.decisionLog.set(4,number,orienation);
	}
	
	
	public void setLinkColor(int number,int value1, double value2){
		this.decisionLog.set(21,number,value1);
		this.decisionLog.set(22,number,value2);
	}
	
	public boolean saveLogger(long netstep,String path){
		 double[] decisionArray = new double[decisionLog.size()];
			    
			 int count = 0;
				for (int k = 0; k < decisionLog.rows(); k++) {
					for (int i = 0; i < this.numcol; i++) {
						
							decisionArray[count] = decisionLog.get(k, i);
							count++;
					}
				}
				if(!saveArrayMatlabreadable(decisionArray, path+"DecisionLog"+ "_" + datum() + "_" + netstep)){
					return false;
				}
//				if(!saveArrayMatlabreadable(this.Activation, path+"ActivationLog"+ "_" + datum() + "_" + netstep)){
//					return false;
//				}
				return true;
				
				
		}
	 private boolean saveArrayMatlabreadable(double[] matrix,
				String filename) {

			// read out the data as column from the matrix into the text file
		try {
				PrintWriter out = new PrintWriter(new FileWriter(filename));
				// go through the slices first

				// go through the rows first
				//for (int i = 0; i < matrix.rows(); i++) {

					// go through the row and fill in the values into the text file
					for (int j = 0; j < matrix.length; j++) {
						out.println(matrix[j]);
					}
			//	}

				out.close();
			} catch (IOException e) {
				return false;
			}
			return true;

		}
	
	 private String datum(){
			Date now = new Date();
	 	 	SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyHHmm");
	 	 	return sdf.format(now);
		}

}
