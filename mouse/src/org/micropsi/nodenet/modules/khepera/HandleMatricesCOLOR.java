package org.micropsi.nodenet.modules.khepera;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix3D;

public class HandleMatricesCOLOR {
	
//	setting the look up table for the placefields in a 1000*1000 Matirces. Nr pf placefields are
	//20*20 and the threshold is set to 0.
	//public static LookupGaussian placefields = new LookupGaussian(1000, 1000, nrPlacecells, 11);
	public static LookupGaussian placefieldsnr;
	public static LookupGaussian placefieldsact;
	public static SparseDoubleMatrix3D experience;
	public static DenseDoubleMatrix2D reflexLookUp;										
	public static SparseDoubleMatrix3D transition;
	public static DenseDoubleMatrix1D activationVector; 
	public static SparseDoubleMatrix2D decisionMatrix;
// COLOR MATRICES:	
	public static DenseDoubleMatrix1D colortempact;
	public static SparseDoubleMatrix3D colortrans;
	public static DenseDoubleMatrix2D colortargetsXdec;

	
	
	public HandleMatricesCOLOR(int sizer,int sizec,int nrcells,int dec){
		HandleMatricesCOLOR.placefieldsnr= new LookupGaussian(sizer, sizec,nrcells);
		HandleMatricesCOLOR.placefieldsact= new LookupGaussian(sizer, sizec,nrcells);
		HandleMatricesCOLOR.experience=new SparseDoubleMatrix3D(dec,nrcells,nrcells);
		HandleMatricesCOLOR.transition=new SparseDoubleMatrix3D(dec,nrcells,nrcells);
		HandleMatricesCOLOR.reflexLookUp=new DenseDoubleMatrix2D(nrcells,dec);
		HandleMatricesCOLOR.decisionMatrix=new SparseDoubleMatrix2D(nrcells,dec);
		HandleMatricesCOLOR.activationVector=new DenseDoubleMatrix1D(nrcells);
//		 COLOR MATRICES:
		HandleMatricesCOLOR.colortempact = new DenseDoubleMatrix1D(nrcells);
		HandleMatricesCOLOR.colortrans = new SparseDoubleMatrix3D(dec, nrcells,nrcells);
		HandleMatricesCOLOR.colortargetsXdec = new DenseDoubleMatrix2D(nrcells, dec);
	}
	
	
	
	public boolean loadAllMatrices(String timestamp, String path){
		if(loadReflexMatrix(timestamp,path)){
		}else{
			return false;
		}
		if(loadExperiencematrix(timestamp,path)){	
		}else{
			return false;
		}
		return true;
	}
	
    
	 public boolean saveMatrices(long netstep,String path,int nrplacearray){
			int tra=HandleMatricesCOLOR.transition.size();
			int ex= HandleMatricesCOLOR.experience.size();
			int act=HandleMatricesCOLOR.activationVector.size();
			
			if(!saveAll(netstep,path,nrplacearray,tra,ex,act)){
				return false;
			}
			
			if(!saveProductMatrix(HandleMatricesCOLOR.reflexLookUp,netstep,"ReflexLookUp",path)){  				
				return false;
			}
			
			if(!saveProductMatrix(HandleMatricesCOLOR.decisionMatrix,netstep,"DecisionMatrix",path)){  				
				return false;
			}
			
			return true;
				
	
	 }
	 
	 public boolean save2dMatrix(DoubleMatrix2D mat,long netstep,String name,String path){
		 
		 if(!saveProductMatrix(mat,netstep,name,path)){
			 return false;
		 }
		 return true;
		 
	 }
	 
	public boolean save1dMatrix(DoubleMatrix1D mat,long netstep,String name,String path){
			 
			 if(!saveProductVector(mat,netstep,name,path)){
				 return false;
			 }
			 return true;
			 
	}
		
	 
	 
	
	
    private boolean loadReflexMatrix(String timestamp,String path) {
    	
    	String filename = path+"ReflexMatrix_" + timestamp;
	   	   
	    // read out the data column from the text file into the matrix
	    
	    try {
	        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
	        
	        	// go through the rows first
			for (int i = 0; i < HandleMatricesCOLOR.reflexLookUp.rows(); i++) {
				// go through the row and fill in the values into the text
				// file
				for (int j = 0; j < HandleMatricesCOLOR.reflexLookUp.columns(); j++) {
					//experience.viewSlice(k).viewRow(i).set(j, Double.parseDouble(in.readLine()));
					HandleMatricesCOLOR.reflexLookUp.set(i,j, Double.parseDouble(in.readLine()));
						
				}
			}
				//logger.debug("slice "+k +" loaded");

	        in.close();
	    } catch (IOException e) {return false;
	    }
	    return true;
	}
    
 
    private boolean loadExperiencematrix(String timestamp,String path) {
		   	   
     		String filename = path+"ReflexMatrix_" + timestamp;
		    // read out the data column from the text file into the matrix
		    
		    try {
		        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		        
		        for (int k = 0; k < HandleMatricesCOLOR.experience.slices(); k++) {
		        	//logger.debug("load "+k +" te slice");
					
		        	// go through the rows first
					for (int i = 0; i < HandleMatricesCOLOR.experience.rows(); i++) {
						// go through the row and fill in the values into the text
						// file
						for (int j = 0; j < HandleMatricesCOLOR.experience.columns(); j++) {
							//experience.viewSlice(k).viewRow(i).set(j, Double.parseDouble(in.readLine()));
							HandleMatricesCOLOR.experience.set(k,i,j, Double.parseDouble(in.readLine()));
							
						}
					}
					//logger.debug("slice "+k +" loaded");
				}
	
		        in.close();
		    } catch (IOException e) {return false;
		    }
		    return true;
		}
	
    
   
    
    
	 private boolean saveAll(long netstep,String path,int nrplacearray,int tra,int ex,int act){
		 	
	 	 	double[] transitionArray = new double[tra];
	 	 	double[] experienceArray = new double[ex];
	 	 	double[] activationArray = new double[act];
	 	 	
	 	 	int count = 0;
			for (int k = 0; k < 8; k++) {
				for (int i = 0; i < nrplacearray; i++) {
					for (int j = 0; j < nrplacearray; j++) {
						transitionArray[count] = HandleMatricesCOLOR.transition.get(k, i, j);
						count++;
					}
				}
			}
			if(!saveArrayMatlabreadable(transitionArray,
					path+"Transitionmatrix"
							+ "_" + datum() + "_" + netstep)){
				return false;
			}
				

			count = 0;
			for (int k = 0; k < 8; k++) {
				for (int i = 0; i < nrplacearray; i++) {
					for (int j = 0; j < nrplacearray; j++) {

						experienceArray[count] = HandleMatricesCOLOR.experience.get(k, i, j);
						count++;
					}
				}
			}
			if(!saveArrayMatlabreadable(experienceArray,
					path+"Experiencematrix"
							+ "_" + datum() + "_" + netstep)){
				return false;
			}

			for (int j = 0; j < nrplacearray; j++) {

				activationArray[j] = HandleMatricesCOLOR.activationVector.get(j);

			}
			
			
			if(!saveArrayMatlabreadable(activationArray, path+"activationMatrix"+"_"+datum()+"_"+netstep)){
				return false;
			}
			
			return true;
	 }
	
	 
	 private boolean saveProductMatrix(DoubleMatrix2D matrix,long netstep,String name,String path){
			double[] Produkt = new double[matrix.size()];
			int count = 0;
				for (int i = 0; i < matrix.rows(); i++) {
					for (int j = 0; j < matrix.columns(); j++) {
						 Produkt[count] = matrix.get(i, j);
						count++;
					}
				}
			if(!saveArrayMatlabreadable(Produkt,
					path+name
							+ "_" + datum() + "_" + netstep)){
				return false;
			}
			
			return true;
		}
	 
	 private boolean saveProductVector(DoubleMatrix1D matrix,long netstep,String name,String path){
			double[] Produkt = new double[matrix.size()];
			int count = 0;
				for (int i = 0; i < matrix.size(); i++) {
					Produkt[count] = matrix.get(i);
					count++;
				}
			if(!saveArrayMatlabreadable(Produkt,
					path+name
							+ "_" + datum() + "_" + netstep)){
				return false;
			}
			return true;
		}
	 
	
	 private String datum(){
			Date now = new Date();
	 	 	SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyHHmm");
	 	 	return sdf.format(now);
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

	
	
	

}
