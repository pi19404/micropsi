package org.micropsi.nodenet.modules.khepera;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;



import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.DoubleMatrix3D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix2D;
import cern.colt.matrix.impl.SparseDoubleMatrix3D;

public class HandleMatrices {
	
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
// 	For the Forwardflooding	
	public static DenseDoubleMatrix1D colortempact;
	public static SparseDoubleMatrix3D colortrans;
	public static DenseDoubleMatrix2D colortargetsXdec;
	public static DenseDoubleMatrix2D frequdecsion;
	public static DenseDoubleMatrix2D frequvisits;
////////////////////////////////For Backwardflooding
	public static DoubleMatrix2D sourcesXdirections;
	public static DoubleMatrix1D oldActivationVector;
	public static DoubleMatrix1D matrixProduct;
	public static DoubleMatrix1D decayTerm;
	public static DenseDoubleMatrix1D testactivationVector;
	public static DenseDoubleMatrix1D rememberPlacefields;
/*
 * For the Logger Merging are the following methods:
 */
//	private DenseDoubleMatrix2D Decision;
//	private DenseDoubleMatrix2D Decisioncp1;
//	private DenseDoubleMatrix2D Decisioncp2;
//	
//	
//	public boolean MergeDecision(String timestamp1,String timestamp2,int length1,int length2,String path){
//		Decision=new DenseDoubleMatrix2D(21,length1+length2);
//		Decisioncp1=new DenseDoubleMatrix2D(21,length1);
//		Decisioncp2=new DenseDoubleMatrix2D(21,length2);
//		if(!loadDecisionLog(timestamp1,path,Decisioncp1)){
//			return false;
//		}
//		if(!loadDecisionLog(timestamp2,path,Decisioncp2)){
//			return false;
//		}
//		CopyDecision();
//		Loggersave(path);
//		return true;		
//	}
//	
////	Load DecisionLog
//	private boolean loadDecisionLog(String timestamp,String path,DoubleMatrix2D LoadData){
//		
//		String filename = path+"DecisionLog_" + timestamp;
//	   	   
//	    // read out the data column from the text file into the matrix
//	    
//	    try {
//	        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
//	        for (int i = 0; i < LoadData.rows(); i++) {
//				// go through the row and fill in the values into the text
//				// file
//				for (int j = 0; j < LoadData.columns(); j++) {
//					//experience.viewSlice(k).viewRow(i).set(j, Double.parseDouble(in.readLine()));
//					LoadData.set(i,j, Double.parseDouble(in.readLine()));
//						
//				}
//			}
//				//logger.debug("slice "+k +" loaded");
//
//	        in.close();
//	    } catch (IOException e) {return false;
//	    }
//	    return true;
//		
//	}
////	Copy DecisionLog
//	private void CopyDecision(){
//		for(int i=0; i <Decisioncp1.rows();i++){
//			for(int k=0; k<Decisioncp1.columns();k++){
//				Decision.set(i,k,Decisioncp1.get(i,k));
//			}
//		}
//		for(int i=0; i <Decisioncp2.rows();i++){
//			for(int k=0; k<Decisioncp2.columns();k++){
//				if(i==1){
//					Decision.set(i,k+Decisioncp1.columns(),Decisioncp2.get(i,k));
//				}else{
//					Decision.set(i,k+Decisioncp1.columns(),Decisioncp2.get(i,k));
//				}
//			}
//		}
//	}
//	
//	
////	Save the copied Decisionlogger
//	private boolean Loggersave(String path){
//		 double[] decisionArray = new double[Decision.size()];
//			    
//			 int count = 0;
//				for (int k = 0; k < Decision.rows(); k++) {
//					for (int i = 0; i < Decision.columns(); i++) {
//						
//							decisionArray[count] = Decision.get(k, i);
//							count++;
//					}
//				}
//				if(!saveArrayMatlabreadable(decisionArray, path+"DecisionLog")){
//					return false;
//				}
//				return true;
//	}
//	
/*
 * End of the Logger Merging
 */		
	
	
	public HandleMatrices(int sizer,int sizec,int nrcells,int dec,boolean color){
		HandleMatrices.placefieldsnr= new LookupGaussian(sizer, sizec,nrcells);
		HandleMatrices.placefieldsact= new LookupGaussian(sizer, sizec,nrcells);
		HandleMatrices.experience=new SparseDoubleMatrix3D(dec,nrcells,nrcells);
		HandleMatrices.transition=new SparseDoubleMatrix3D(dec,nrcells,nrcells);
		HandleMatrices.reflexLookUp=new DenseDoubleMatrix2D(nrcells,dec);
		HandleMatrices.decisionMatrix=new SparseDoubleMatrix2D(nrcells,dec);
		HandleMatrices.activationVector=new DenseDoubleMatrix1D(nrcells);
		HandleMatrices.frequdecsion=new DenseDoubleMatrix2D(nrcells,dec);
		HandleMatrices.frequvisits=new DenseDoubleMatrix2D(nrcells,dec);
		
		//////////////////For Mistakes
//		HandleMatrices.minus=new DenseDoubleMatrix1D(nrcells*nrcells*dec);
//		HandleMatrices.old=new DenseDoubleMatrix1D(nrcells*nrcells*dec);
//		HandleMatrices.newmat=new DenseDoubleMatrix1D(nrcells*nrcells*dec);
//		HandleMatrices.savemat=new DenseDoubleMatrix1D(nrcells*nrcells*dec);
//		HandleMatrices.reflminus=new DenseDoubleMatrix1D(nrcells*dec);
//		HandleMatrices.reflold=new DenseDoubleMatrix1D(nrcells*dec);
//		HandleMatrices.reflnew=new DenseDoubleMatrix1D(nrcells*dec);
//		HandleMatrices.reflsave=new DenseDoubleMatrix1D(nrcells*dec);
//		
		
		
		
		
		
		if(color){
			HandleMatrices.colortempact = new DenseDoubleMatrix1D(nrcells);
			HandleMatrices.colortrans = new SparseDoubleMatrix3D(dec, nrcells,nrcells);
			HandleMatrices.colortargetsXdec = new DenseDoubleMatrix2D(nrcells, dec);
		}
	}
	
	
	
	public void prepareFlooding(int nrplacearray,int nrDecision){
		HandleMatrices.sourcesXdirections = DoubleFactory2D.dense.make(nrplacearray,nrDecision);
		HandleMatrices.oldActivationVector =  DoubleFactory1D.dense.make(nrplacearray);
		HandleMatrices.matrixProduct =  DoubleFactory1D.dense.make(nrplacearray);
		HandleMatrices.decayTerm =  DoubleFactory1D.dense.make(nrplacearray);
		HandleMatrices.testactivationVector =  new DenseDoubleMatrix1D(nrplacearray);
		HandleMatrices.rememberPlacefields =  new DenseDoubleMatrix1D(nrplacearray);
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
	
	public boolean loadAllMatrices(String timestamp, String path,org.micropsi.nodenet.modules.khepera.Floodingtest.LoggerProxy logger){
		if(loadReflexMatrix(timestamp,path)){
		}else{
			logger.error("Load of ReflexMatrix went wrong");
			return false;
		}
		if(loadExperiencematrix(timestamp,path)){	
		}else{
			logger.error("Load of ExperienceMatrix went wrong");
			return false;
		}
		return true;
	}
	
    
	 public boolean saveMatrices(long netstep,String path,int nrplacearray){
			int tra=HandleMatrices.transition.size();
			int ex= HandleMatrices.experience.size();
			int act=HandleMatrices.activationVector.size();
			
			if(!saveAll(netstep,path,nrplacearray,tra,ex,act)){
				return false;
			}
			
			if(!saveProductMatrix(HandleMatrices.reflexLookUp,netstep,"ReflexLookUp",path)){  				
				return false;
			}
			
			if(!saveProductMatrix(HandleMatrices.decisionMatrix,netstep,"DecisionMatrix",path)){  				
				return false;
			}
			
			return true;
				
	
	 }
	
	 public boolean save3dMatrix(DoubleMatrix3D mat,long netstep,String name,String path){
		 
		 int decnr=mat.slices();
		 int nrplacecell=mat.columns();
		 double[] transitionArray = new double[mat.size()];
		 int count = 0;
			for (int k = 0; k < decnr; k++) {
				for (int i = 0; i <nrplacecell; i++) {
					for (int j = 0; j < nrplacecell; j++) {
						transitionArray[count] = mat.get(k, i, j);
						count++;
					}
				}
			}
			if(!saveArrayMatlabreadable(transitionArray,
					path+name
							+ "_" + datum() + "_" + netstep)){
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
		
	//Sum over all decision
	public double Sum(){
		
		double look=0;
		double sum=0;
		
		for(int j=0;j<HandleMatrices.experience.slices();j++){
			for(int k=0;k<HandleMatrices.experience.rows();k++){
				look=HandleMatrices.experience.viewSlice(j).viewRow(k).zSum();
				sum+=look;
				frequdecsion.set(k,j,look);
			}
		}
		return sum;
		
	}
//	Has to be taken care that the array int is set to the nr of decisions!!!
	
 public void Sort(int nrdec,long netstep,String path){
		
		double value;
		int index=0;
		double min=0.0;
		int h;
		DenseDoubleMatrix1D remember=new DenseDoubleMatrix1D(nrdec);
		DenseDoubleMatrix1D test=new DenseDoubleMatrix1D(nrdec);
		
		
		
		for(int k=0;k<HandleMatrices.frequdecsion.rows();k++){
			remember=(DenseDoubleMatrix1D)HandleMatrices.frequdecsion.viewRow(k).copy();
			test=(DenseDoubleMatrix1D)remember.copy();
			value=remember.zSum()+1.0;
			if(remember.zSum()>0){
				for(int l=0;l<remember.size();l++){
					h=0;
					min=remember.get(h);
					index=h;
					while(remember.get(h)==value){
						min=remember.get(h);
						index=h;
						h++;
					}
					for(int g=0; g<remember.size();g++){
						if(min>remember.get(g) && remember.get(g)!=value){
							min=remember.get(g);
							index=g;
						}
					}
					remember.set(index,value);
					HandleMatrices.frequvisits.set(k,l,index+1);
				}
				if(test.get((int)(HandleMatrices.frequvisits.get(k,0)-1))==min){
					HandleMatrices.frequvisits.set(k,0,0.0);
				}
			}
		}	
	}

	 
	public boolean saveNumber(String name,String path,int number,long netstep){
		
		double[] sn=new double[1];
		
		sn[0]=(double)number;
		
		if(!saveArrayMatlabreadable(sn,
				path+name+"_"+datum()+"_"+netstep)){
			return false;
		}else{
			return true;
		}
		
		
	}
	
    private boolean loadReflexMatrix(String timestamp,String path) {
    	
    	String filename = path+"ReflexLookUp_" + timestamp;
	   	   
	    // read out the data column from the text file into the matrix
	    
	    try {
	        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
	        
	        	// go through the rows first
			for (int i = 0; i < HandleMatrices.reflexLookUp.rows(); i++) {
				// go through the row and fill in the values into the text
				// file
				for (int j = 0; j < HandleMatrices.reflexLookUp.columns(); j++) {
					//experience.viewSlice(k).viewRow(i).set(j, Double.parseDouble(in.readLine()));
					HandleMatrices.reflexLookUp.set(i,j, Double.parseDouble(in.readLine()));
						
				}
			}
				//logger.debug("slice "+k +" loaded");

	        in.close();
	    } catch (IOException e) {return false;
	    }
	    return true;
	}
    
 
    private boolean loadExperiencematrix(String timestamp,String path) {
		   	   
     		String filename = path+"Experiencematrix_" + timestamp;
		    // read out the data column from the text file into the matrix
		    
		    try {
		        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		        
		        for (int k = 0; k < HandleMatrices.experience.slices(); k++) {
		        	//logger.debug("load "+k +" te slice");
					
		        	// go through the rows first
					for (int i = 0; i < HandleMatrices.experience.rows(); i++) {
						// go through the row and fill in the values into the text
						// file
						for (int j = 0; j < HandleMatrices.experience.columns(); j++) {
							//experience.viewSlice(k).viewRow(i).set(j, Double.parseDouble(in.readLine()));
							HandleMatrices.experience.set(k,i,j, Double.parseDouble(in.readLine()));
							
						}
					}
					//logger.debug("slice "+k +" loaded");
				}
	
		        in.close();
		    } catch (IOException e) {return false;
		    }
		    return true;
		}
	
    
   
//     public void newexperience(String path,String timestamp1,String timestamp2,String timestamp3,long netstep){
////    	 			timestamp3-timestamp1+timestamp2
//    	 	String minusmat=path+"Experiencematrix_" + timestamp1;
//    	 	String oldmat=path+"Experiencematrix_" + timestamp2;
//    	 	String newmat=path+"Experiencematrix_" + timestamp3;
//       	 
//    	 	 try {
//    	 		  BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(minusmat))); 
//    	 		  for(int k=0;k< HandleMatrices.minus.size();k++){
//    	 			  HandleMatrices.minus.set(k,Double.parseDouble(in.readLine()));
//    	 		  }
// 			  in.close();
// 		    } catch (IOException e) {
// 		    }
// 		   try {
// 	 		  BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(oldmat))); 
// 	 		  for(int k=0;k< HandleMatrices.old.size();k++){
// 	 			  HandleMatrices.old.set(k,Double.parseDouble(in.readLine()));
// 	 		  }
//			  in.close();
//		    } catch (IOException e) {
//		    }    
// 		    
//		    try {
//	 	 		  BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(newmat))); 
//	 	 		  for(int k=0;k< HandleMatrices.newmat.size();k++){
//	 	 			  HandleMatrices.newmat.set(k,Double.parseDouble(in.readLine()));
//	 	 		  }
//				  in.close();
//			    } catch (IOException e) {
//			    }    
//			   
//			for(int k=0;k<HandleMatrices.savemat.size();k++){
//				HandleMatrices.savemat.set(k,(HandleMatrices.newmat.get(k)-HandleMatrices.minus.get(k))+HandleMatrices.old.get(k));
//			}
//			save1dMatrix(HandleMatrices.savemat,netstep,"Testmat",path);
//			
//			/////Reflexes
//			try {
//	 	 		  BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path+"ReflexLookUp_"+timestamp1))); 
//	 	 		  for(int k=0;k< HandleMatrices.reflminus.size();k++){
//	 	 			  HandleMatrices.reflminus.set(k,Double.parseDouble(in.readLine()));
//	 	 		  }
//				  in.close();
//			    } catch (IOException e) {
//			    }    
//			
//		    try {
//		 		  BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path+"ReflexLookUp_"+timestamp2))); 
//		 		  for(int k=0;k< HandleMatrices.reflold.size();k++){
//		 			  HandleMatrices.reflold.set(k,Double.parseDouble(in.readLine()));
//		 		  }
//			  in.close();
//		    } catch (IOException e) {
//		    }    
//				    
//		    try {
//		 		  BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path+"ReflexLookUp_"+timestamp3))); 
//		 		  for(int k=0;k< HandleMatrices.reflnew.size();k++){
//		 			  HandleMatrices.reflnew.set(k,Double.parseDouble(in.readLine()));
//		 		  }
//				  in.close();
//		    } catch (IOException e) {
//		    		}   
//		    
//		    
//		    for(int k=0;k<HandleMatrices.reflsave.size();k++){
//				HandleMatrices.reflsave.set(k,(HandleMatrices.reflnew.get(k)-HandleMatrices.reflminus.get(k))+HandleMatrices.reflold.get(k));
//			}
//		    save1dMatrix(HandleMatrices.reflsave,netstep,"Reflextest",path);
//		    
//			
//			
//     }
    
	 private boolean saveAll(long netstep,String path,int nrplacearray,int tra,int ex,int act){
		 	
	 	 	double[] transitionArray = new double[tra];
	 	 	double[] experienceArray = new double[ex];
	 	 	double[] activationArray = new double[act];
	 	 	
	 	 	int count = 0;
			for (int k = 0; k < 8; k++) {
				for (int i = 0; i < nrplacearray; i++) {
					for (int j = 0; j < nrplacearray; j++) {
						transitionArray[count] = HandleMatrices.transition.get(k, i, j);
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

						experienceArray[count] = HandleMatrices.experience.get(k, i, j);
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

				activationArray[j] = HandleMatrices.activationVector.get(j);

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
