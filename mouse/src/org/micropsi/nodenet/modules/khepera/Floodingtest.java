
package org.micropsi.nodenet.modules.khepera;



import java.sql.PreparedStatement;
import java.util.Random;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Slot;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix3D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.math.Functions;
import cern.jet.stat.Descriptive;

import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

public class Floodingtest extends AbstractNativeModuleImpl {
	
	
	
	
	//define Parameter for the flooding algorithm
	private static final int RedUrge=88;
	private static final int GreenUrge=149;
	private static final int BlueUrge=88;
	private static final int YellUrge=78;
	
	private static final int [] positionUrges = {RedUrge,GreenUrge,BlueUrge,YellUrge};
	private double decay=0.9;
	private static int nrPlacecellsrow=11;
	private static int nrPlacecellscol=15;
	private static int nrDecision=8;
	private static double threshold=0;
	
	
    private static boolean debugmodus=true; //save all matrices and data
//    stop the robot
    private static boolean stoprobot=false;
//    send the deciion after stop the robot
    private static boolean sendnextdecision=false;

    //Store the last activation
    private static double [] lasturgeact = {0,0,0,0};
    
    private static int[] position = new int [2];
    

    private static boolean loadmatrices = true; // load the matrices with the specified timestamp
    private static boolean loadGauss = true;  // flag to request that a pre-computed distribution of placefields is loaded
    private static boolean expmode = true; // flag if the robot should walk in the flooding mode or experience mod
    private static boolean flooding=false;
    // convenience path variable for the in- and output of matrices
    private static String path = "/mnt/dweiller/NavigationProject/Matrices/250906/";
    

    // timestamp for the matrices that should be loaded (up to now experience matrix, later also urgevector & transition matrix..)
    private static String timestamp = "0210061338_30001"; //e.g. "1901061828_20000"
    
    private static int nrplacearray=(nrPlacecellsrow*nrPlacecellscol);
    private static int this_place_num;
    
    // after how many netsteps the matrices should be regularly saved?
    private static int saveEach=30000;
   
    private static DecisionLogger decisionlog= new DecisionLogger(21,saveEach);
	
	// definition of the slots and gates
	public static final int GT_UrgeBlue_Value = 69996;
	
	public static final int GT_UrgeYell_Value = 69997;
	
	public static final int GT_UrgeGreen_Value = 69998;
	
	public static final int GT_UrgeRed_Value = 69999;
	
	public static final int GT_Action_Value = 70000;
	
	public static final int ST_Orientation_Sensor = 70003;

	public static final int ST_Positionx_Sensor = 70004;

	public static final int ST_Positiony_Sensor = 70005;

	public static final int ST_Urge1_Sensor = 70006;
	
	public static final int ST_Urge2_Sensor = 70007;
	
	public static final int ST_Urge3_Sensor = 70008;
	
	public static final int ST_Urge4_Sensor = 70009;
	
	public static final int ST_Reflex_Value = 70010;
	
	public static final int ST_Rotate_Value = 70011;
	
	public static final int ST_Urge5_Sensor = 70012;
	
	public static final int ST_Urge6_Sensor = 70013;
	
	public static final int ST_Urge7_Sensor = 70014;
	
	public static final int ST_Urge8_Sensor = 70015;

	
	//setting the value of the orientation which should be sent
	public static double sendorient = -1;
	
	private static double redact=1;
	private static double greenact=1;
	private static double blueact=1;
	private static double yellact=1;
	private static double rotation;
	private static boolean nodecision=false;
	private static boolean runflood=false;
	private static double lookprob=0;
	private static Random gauss=new Random();
	private static boolean firstsort=true;
	
//	Calculating the Flooding
	private static boolean calculate=false;
//	take the best decision
	private static boolean nextdecision=false;
	
	//How many decision in one placefield can be done
	private static int[] NumberOfDecisions = new int [nrplacearray];
	
	Algebra algebra = new Algebra();
	
	
	//Store the Activation of the (number) last netsteps
	private static MakeLowPassFilter buffer = new MakeLowPassFilter(3);
    private static MakeLowPassFilter positionx = new MakeLowPassFilter(2);
	private static MakeLowPassFilter positiony = new MakeLowPassFilter(2);
	
	
	//Handle all matrices
	private static HandleMatrices matrix= new HandleMatrices(1600,1200,nrplacearray,nrDecision,false);
	
	private LoggerProxy translogger;
	
	/*
	 * Transfer the logger into other calsses
	 */
	
	public class LoggerProxy{
		
		private final NetStepLogger realLogger=logger;
		
		public void debug(String text){
			realLogger.debug(text);
		}
		
		public void error(String text){
			realLogger.error(text);
		}
		
	}
	
	private void floodingMatrice(double[] urgeinput,long netstep){
		
		logger.debug("Floooding the matrices!!");
		HandleMatrices.testactivationVector.assign(HandleMatrices.activationVector);	
		HandleMatrices.decayTerm.assign(decay);
		HandleMatrices.oldActivationVector.assign(0.0);
		//Calcualte the TransitionMatrice
		updateTransition();		
		double diff=1.0;
		// DoubleMatrix1D mistake = int x=0;
		//Do only multiplicate the Actiovation so often until a certain precition.	
		while(diff > 0.00000000001){
	
				//copy values of the activation of the last step.
				HandleMatrices.oldActivationVector.assign(HandleMatrices.activationVector);
				HandleMatrices.activationVector.assign(HandleMatrices.decayTerm,cern.jet.math.Functions.mult);
				
				//Multiplication of activation with the Transition Matrice 
				for(int i = 0; i < nrDecision; i++){
					HandleMatrices.matrixProduct = algebra.mult(HandleMatrices.transition.viewSlice(i),HandleMatrices.activationVector);
					HandleMatrices.sourcesXdirections.viewColumn(i).assign(HandleMatrices.matrixProduct);
				}
				
				//calculate the new activaion multiply the new activation with a decay factor
				for (int i = 0; i < HandleMatrices.sourcesXdirections.rows(); i++) {
					IntArrayList indices = new IntArrayList();
					DoubleArrayList values = new DoubleArrayList();
					HandleMatrices.sourcesXdirections.viewRow(i).getNonZeros(indices,values);
					
					if(values.size()>=1){
						HandleMatrices.activationVector.set(i,Descriptive.max(values));
					}
				}
				
				
				//Remebers the max Activation of each Source
				HandleMatrices.rememberPlacefields.assign(HandleMatrices.activationVector);
	//						set the activation at the positions of the urge to the input of this activation
				for(int i=0;i<positionUrges.length;i++){
					if(urgeinput[i]>0){
						HandleMatrices.activationVector.set(positionUrges[i]-1,urgeinput[i]);
					}
				}
				HandleMatrices.testactivationVector.assign(HandleMatrices.activationVector);		
				diff=Math.sqrt(HandleMatrices.testactivationVector.assign(HandleMatrices.oldActivationVector,cern.jet.math.Functions.minus).assign(cern.jet.math.Functions.square).zSum());
			} //end while of the Multiplication of the transition matrice by the activation
		
//					if(debugmodus){
//						if(!	matrix.save2dMatrix(sourcesXdirections,netstep,"Source",path)){
//							logger.error("Source not saved!!!");
//						}
//						if(!matrix.save1dMatrix(HandleMatrices.activationVector,netstep,"FlActivation",path)){
//							logger.error("FlActivation not saved!");
//						}
//						if(!matrix.saveNumber("Counts",path,testcount,netstep)){
//							logger.error("Number not saved!!!");
//						}
//					}
		
		// calculate best decision The decisiona are stored in a totalNrPlacecells times nrDecision matrices, where the sources 
		// The beste deciiosn is stored in the dirst of the columns, In case there ar more than one placecell, the others are stored in the other
		// columns. In the Numberofdecisions are the number stored how many good decision we have for each source.
		for (int i = 0; i < HandleMatrices.sourcesXdirections.rows(); i++) {
			NumberOfDecisions[i]=indexAtMax(HandleMatrices.sourcesXdirections.viewRow(i),i,HandleMatrices.rememberPlacefields.get(i));
		}
		
		if(debugmodus){
				if(!matrix.save2dMatrix(HandleMatrices.decisionMatrix,netstep,"Decision",path)){
					logger.error("Decision not saved!");
					}
				if(!matrix.save1dMatrix(HandleMatrices.activationVector,netstep,"activation",path)){
					logger.debug("Activation not saved");
				}
			}
		
	}
	
//	Find the best Decision of a ceratin Node;
	private int findBestDecision(int this_place_num){
		return (int)HandleMatrices.decisionMatrix.get(this_place_num-1,(int)Math.ceil(NumberOfDecisions[this_place_num-1]*Math.random())-1);
	}
	
	
	
	
	
	/**
	 * Gets the number and activation of the placecell we are currently on.
	 */
	private double[] getPlaceCellInfo(int number) {
		
		
		int xposition;
		int yposition;
		double[] placeInfo = new double[2];
		//get the indice of the current place cell from the placefield at x,y
		double xpo =  (posx.getIncomingActivation() * 16.0);
		double ypo =  (posy.getIncomingActivation() * 12.0);
		if(number<100){
			xposition=(int)xpo;
			yposition=(int)ypo;
			positionx.update(xpo);
			positiony.update(ypo);
		}else{
			positionx.update(xpo);
			positiony.update(ypo);
			if(Math.sqrt(positionx.euclidian()+positiony.euclidian())>100){
				logger.debug("X1: "+positionx.getEntry(0));
				logger.debug("X2: "+positionx.getEntry(1));
				logger.debug("X1: "+positionx.getEntry(0));
				logger.debug("X1: "+positionx.getEntry(0));
				logger.error("LOST Robot "+Math.sqrt(positionx.euclidian()+positiony.euclidian()));
				
				positionx.copyBuffer();
				positiony.copyBuffer();
				xposition=(int)positionx.getEntry(0);
				yposition=(int)positiony.getEntry(0);
				logger.error("LOST Robot "+Math.sqrt(positionx.euclidian()+positiony.euclidian()));
				
			}else{
				xposition=(int)xpo;
				yposition=(int)ypo;
			}
		}
						
		placeInfo[0] = HandleMatrices.placefieldsnr.getPlaceCellEntry(xposition, yposition);
		placeInfo[1] = HandleMatrices.placefieldsact.getPlaceCellEntry(xposition,yposition);
		position[0]=xposition;
		position[1]=yposition;
		return placeInfo;
		}	
	
	
	/**
	 * update the experience matrix (called frequency matrix by the old folks)
	 * 
	 * @param this_place
	 */
	
	private void updateMatrices(int this_place,double refl) {
		// logger.debug("Update of ExperienceMatrix at pos "+this_place+"with orientation "+innerstate.getStateInt("decision"));
		
		
		if(refl<0){
			HandleMatrices.reflexLookUp.set(innerstate.getStateInt("nrlastplace")-1,innerstate.getStateInt("decision")-1,HandleMatrices.reflexLookUp.get(innerstate
						.getStateInt("nrlastplace")-1,innerstate.getStateInt("decision")-1)+1);
		}
			
		
		HandleMatrices.experience.set(innerstate.getStateInt("decision")-1, innerstate
				.getStateInt("nrlastplace")-1, this_place-1, HandleMatrices.experience.get(
				innerstate.getStateInt("decision")-1, innerstate
						.getStateInt("nrlastplace")-1, this_place-1) + 1);		
	}

	
	/**
	 * Calculates the transition matrix from the experience matrix.
	 */
	
	private void updateTransition(){
		// logger.debug("Update of Transitionmatrix");
		double norm=0;
		double reflexfactor=0;
		
		//If the wall is touched a reflexfactor is introduced which discaunts th connection
		HandleMatrices.transition = (SparseDoubleMatrix3D) HandleMatrices.experience.copy();
			
		for(int dec=0;dec<nrDecision;dec++){	
		//	transition.viewSlice(dec).getNonZeros(rowl,columnl,value); Could be done only by this rows which are not zero. But what is the order if the column!!
			for(int row=0;row<nrplacearray;row++){
				norm=HandleMatrices.transition.viewSlice(dec).viewRow(row).zSum();
				if(norm>0){
					reflexfactor=Math.abs(1-(5.0/6.0)*(HandleMatrices.reflexLookUp.get(row,dec)/norm));
					HandleMatrices.transition.viewSlice(dec).viewRow(row).assign(Functions.div(norm));
					if(reflexfactor<1){
						HandleMatrices.transition.viewSlice(dec).viewRow(row).assign(Functions.mult(reflexfactor));
					}
				}
					
			}
		}		
	}
	
	
	
	//
	// TODO what if we flood without any memory of urges? is this possible
	
	/**
	 *  Extracts the indices of the maximal activation
	 */
	private static int indexAtMax(DoubleMatrix1D elements,int row,double maxact) {
		int size = elements.size();
		if (size==0) throw new IllegalArgumentException();
		int numberMax=0;
		
		if(maxact>0){
			for(int i=0;i<size;i++){
				if(elements.get(i)==maxact){
					numberMax+=1;
					HandleMatrices.decisionMatrix.set(row,numberMax-1,i+1);
				}
			}
			
		}
		else {
			numberMax=nrDecision;
			for(int i=1;i<9;i++){
				HandleMatrices.decisionMatrix.set(row,i-1,i);
			}
		}
		
		return numberMax;
	}

	
	
	
	
	
	
	private final int[] gateTypes = { GT_Action_Value,GT_UrgeBlue_Value,GT_UrgeGreen_Value,GT_UrgeRed_Value,GT_UrgeYell_Value };

	private final int[] slotTypes = { ST_Orientation_Sensor,
			ST_Positionx_Sensor, ST_Positiony_Sensor, ST_Urge1_Sensor,ST_Urge2_Sensor,ST_Urge3_Sensor,ST_Urge4_Sensor,ST_Reflex_Value,ST_Rotate_Value,
			ST_Urge5_Sensor,ST_Urge6_Sensor,ST_Urge7_Sensor,ST_Urge8_Sensor};

	@Override
	protected int[] getGateTypes() {
		return gateTypes;
	}

	@Override
	protected int[] getSlotTypes() {
		// TODO Auto-generated method stub
		return slotTypes;
	}

	Slot orient, posx, posy,reflex,rotate,sensor1,sensor2,sensor3,sensor4;
	Slot [] urge = new Slot[(positionUrges.length)];

	private void catchSlots(Slot[] slots) {
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
			case ST_Orientation_Sensor:
				orient = slots[i];
				break;
			case ST_Positionx_Sensor:
				posx = slots[i];
				break;
			case ST_Positiony_Sensor:
				posy = slots[i];
				break;
			case ST_Urge1_Sensor:
				urge[0] = slots[i];
				break;
			case ST_Urge2_Sensor:
				urge[1] = slots[i];
				break;
			case ST_Urge3_Sensor:
				urge[2] = slots[i];
				break;
			case ST_Urge4_Sensor:
				urge[3] = slots[i];
				break;
			case ST_Reflex_Value:
				reflex = slots[i];
				break;
			case ST_Rotate_Value:
				rotate = slots[i];
				break;
			case ST_Urge5_Sensor:
				sensor1 = slots[i];
				break;
			case ST_Urge6_Sensor:
				sensor2 = slots[i];
				break;
			case ST_Urge7_Sensor:
				sensor3 = slots[i];
				break;
			case ST_Urge8_Sensor:
				sensor4 = slots[i];
				break;
			}
		}
	}

	boolean firsttime = true;

	public void initialize() {
		
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			public String getExtensionID() {
				return "Flooding Algorithm";
			}

			public String gateType(int type) {
				switch (type) {
				case GT_Action_Value:
					return "Action";
				case GT_UrgeRed_Value:
					return "Urge1";
				case GT_UrgeGreen_Value:
					return "Urge2";
				case GT_UrgeBlue_Value:
					return "Urge3";
				case GT_UrgeYell_Value:
					return "Urge4";
					

				}
				return null;
			}

			public String slotType(int type) {
				switch (type) {
				case ST_Orientation_Sensor:
					return "Orientation";
				case ST_Positionx_Sensor:
					return "x Position";
				case ST_Positiony_Sensor:
					return "Y Position";
				case ST_Urge1_Sensor:
					return "Urge 1";
				case ST_Urge2_Sensor:
					return "Urge 2";
				case ST_Urge3_Sensor:
					return "Urge 3";
				case ST_Urge4_Sensor:
					return "Urge 4";
				case ST_Reflex_Value:
					return "Reflex";
				case ST_Rotate_Value:
					return "Rotate";
				case ST_Urge5_Sensor:
					return "Sensor 1";
				case ST_Urge6_Sensor:
					return "Sensor 2";
				case ST_Urge7_Sensor:
					return "Sensor 3";
				case ST_Urge8_Sensor:
					return "Sensor 4";
				}
				return null;
			}

		});
		firsttime = false;
	}

	/**
	 * generell structure:
	 * 1. look if urgees are over threshold
	 *  yes > 
	 * 2a. flood if there was no recent flooding
	 * 2b. move by the decisionVector advice if there was a recent flooding
	 */
	
	public void calculate(Slot[] slots, GateManipulator manipulator,
			long netstep) throws NetIntegrityException {
		
		//If the robot rotates then falg is one.
		innerstate.ensureStateExistence("Rotate","0");
		innerstate.ensureStateExistence("Filternetsteps","0");
		//memroizes if there was a reflexsignal in a Run
		innerstate.ensureStateExistence("Reflex","0");
		// memorizes the last Nr of the placefield
		innerstate.ensureStateExistence("nrlastplace", "-1");
		// memorizes the last activation (placefield number is egal)
		innerstate.ensureStateExistence("actlastplace","-1");
		//memorizes the last placecell in the array
		innerstate.ensureStateExistence("LastCycle","-1");
		// memorizes the last Orientation which was token 
		innerstate.ensureStateExistence("Orient", "-1");
		// memorizes the last decision
		innerstate.ensureStateExistence("decision", "-1");
		// take care that the flooding Algorithm was run only once and not every netstep
		innerstate.ensureStateExistence("flood", "1");
		innerstate.ensureStateExistence("debug","-1");
		//Remeber nestep for the stop roobot
		innerstate.ensureStateExistence("stoprobot","0.0");
		//Store the placecell where the decision was made
		innerstate.ensureStateExistence("CurrentPlacecell","0");
		// save the first netstep
		if (debugmodus)
		innerstate.ensureStateExistence("firstnetstep","netstep");
				
		
		//////////////////////////////////Initialize the Components and the innerstates
		if(firsttime){ 
			initialize();
			catchSlots(slots);
			
			if(debugmodus)
			innerstate.setState("firstnetstep",netstep);
			
			if(loadmatrices){
				if(matrix.loadAllMatrices(timestamp,path,translogger)){
					
				}else{
					logger.error("Loading of the data matirces went wrong");
				}
			}
////			To Merge two DecisionLogger
//			if(matrix.MergeDecision("2408061601_24746","2408061604_36746",12000,12000,path)){
//				logger.debug("Merged");
//			}else{
//				logger.debug("Not Merged");
//			}
////			End of Merge
			
			
			//load the Gauss File generated by matlab
			if (loadGauss){ 
				logger.debug("Load Gauss");
				if(!HandleMatrices.placefieldsnr.loadMatrixMatlabreadable(path + "indRandLookup.dat")){
					logger.error("Gauss index not loaded");
				}
				HandleMatrices.placefieldsnr.writeGaussMatlabreadable(path+"LookupGaussian");
				
				if(!HandleMatrices.placefieldsact.loadMatrixMatlabreadable(path + "actRandLookup.dat")){
					logger.error("Gauss activity not loaded");
				}
				HandleMatrices.placefieldsact.writeGaussMatlabreadable(path+"ActGaussian");
			}
			//end Load Guass
			
//			HandleMatrices.experience.set(4,80,26,0);
//			HandleMatrices.experience.set(6,80,106,0);
//			HandleMatrices.experience.set(5,94,87,0);
//			HandleMatrices.experience.set(6,16,13,0);
//			HandleMatrices.reflexLookUp.set(92,3,0);
//			if(HandleMatrices.reflexLookUp.get(80,4)>=1)
//				HandleMatrices.reflexLookUp.set(80,4,HandleMatrices.reflexLookUp.get(80,4)-1);
//			if(HandleMatrices.reflexLookUp.get(80,6)>=1)
//				HandleMatrices.reflexLookUp.set(80,6,HandleMatrices.reflexLookUp.get(80,6)-1);
//			if(HandleMatrices.reflexLookUp.get(94,5)>=1)
//				HandleMatrices.reflexLookUp.set(94,5,HandleMatrices.reflexLookUp.get(94,5)-1);
//			if(HandleMatrices.reflexLookUp.get(16,6)>=1)
//				HandleMatrices.reflexLookUp.set(16,6,HandleMatrices.reflexLookUp.get(16,6)-1);
			
			
			
			
//			HandleMatrices.reflexLookUp.set(38,0,HandleMatrices.reflexLookUp.get(38,0)-1);
//			HandleMatrices.reflexLookUp.set(87,1,HandleMatrices.reflexLookUp.get(87,1)-1);
//			HandleMatrices.reflexLookUp.set(148,6,HandleMatrices.reflexLookUp.get(148,6)-1);
//			HandleMatrices.reflexLookUp.set(87,2,HandleMatrices.reflexLookUp.get(87,2)-1);
//			HandleMatrices.experience.set(0,38,87,0);
//			HandleMatrices.experience.set(1,87,38,0);
//			HandleMatrices.experience.set(6,148,68,0);
//			HandleMatrices.experience.set(5,68,148,0);
//			HandleMatrices.experience.set(2,62,86,0);
//			HandleMatrices.experience.set(3,98,62,0);
//			HandleMatrices.experience.set(5,93,76,0);
//			HandleMatrices.experience.set(2,93,79,0);
//			HandleMatrices.experience.set(0,16,12,0);
//			HandleMatrices.experience.set(2,87,57,0);
			
			
			
//
//			HandleMatrices.experience.set(2,35,80,0);
//			HandleMatrices.experience.set(0,69,35,0);
//			HandleMatrices.experience.set(2,87,35,0);
//			
//			HandleMatrices.reflexLookUp.set(69,0,HandleMatrices.reflexLookUp.get(69,0)-1);
//			HandleMatrices.reflexLookUp.set(87,2,HandleMatrices.reflexLookUp.get(87,2)-1);
//			HandleMatrices.experience.set(3,25,73,0);
//			HandleMatrices.experience.set(4,73,25,0);
//			HandleMatrices.experience.set(6,36,87,0);
//			HandleMatrices.experience.set(5,87,36,0);
//			HandleMatrices.experience.set(5,47,87,0);
//			HandleMatrices.experience.set(1,98,47,0);
//			HandleMatrices.experience.set(1,57,75,0);
//			HandleMatrices.experience.set(4,57,56,0);
//			HandleMatrices.experience.set(7,57,94,0);
//			HandleMatrices.experience.set(5,56,57,0);
//			HandleMatrices.experience.set(2,86,57,0);
//			HandleMatrices.experience.set(2,87,56,0);
//			HandleMatrices.experience.set(1,46,24,0);
//			HandleMatrices.experience.set(1,46,28,0);
//			HandleMatrices.experience.set(6,28,46,0);
//			HandleMatrices.experience.set(7,12,46,0);
//			HandleMatrices.experience.set(0,35,56,0);
//			HandleMatrices.experience.set(5,35,23,0);
//			HandleMatrices.experience.set(7,35,76,0);
//			HandleMatrices.experience.set(2,98,35,0);
//			HandleMatrices.experience.set(4,23,35,0);
//			HandleMatrices.experience.set(6,34,35,0);
//			HandleMatrices.experience.set(0,24,16,0);
//			HandleMatrices.experience.set(2,24,39,0);
//			HandleMatrices.experience.set(6,24,12,0);
//			HandleMatrices.experience.set(1,46,24,0);
//			HandleMatrices.experience.set(1,33,24,0);
//			HandleMatrices.experience.set(5,16,24,0);
//			HandleMatrices.experience.set(6,13,16,0);
//			HandleMatrices.experience.set(3,16,13,0);
//			HandleMatrices.experience.set(0,12,34,0);
//			HandleMatrices.experience.set(1,12,26,0);
//			HandleMatrices.experience.set(2,12,23,0);
//			HandleMatrices.experience.set(4,12,27,0);
//			HandleMatrices.experience.set(5,12,27,0);
//			HandleMatrices.experience.set(5,12,23,0);
//			HandleMatrices.experience.set(6,12,26,0);
//			HandleMatrices.experience.set(7,12,46,0);
//			HandleMatrices.experience.set(7,12,69,0);
//			HandleMatrices.experience.set(1,23,12,0);
//			HandleMatrices.experience.set(3,26,12,0);
//			HandleMatrices.experience.set(3,49,12,0);
//			HandleMatrices.experience.set(4,23,12,0);
//			HandleMatrices.experience.set(6,10,12,0);
//			HandleMatrices.experience.set(6,24,12,0);
//			HandleMatrices.experience.set(6,27,12,0);
//			HandleMatrices.experience.set(7,23,12,0);
//			HandleMatrices.experience.set(0,23,15,0);
//			HandleMatrices.experience.set(2,23,12,0);
//			HandleMatrices.experience.set(2,23,59,0);
//			HandleMatrices.experience.set(3,23,27,0);
//			HandleMatrices.experience.set(4,23,12,0);
//			HandleMatrices.experience.set(4,23,36,0);
//			HandleMatrices.experience.set(7,23,12,0);
//			HandleMatrices.experience.set(1,49,23,0);
//			HandleMatrices.experience.set(2,12,23,0);
//			HandleMatrices.experience.set(3,26,23,0);
//			HandleMatrices.experience.set(5,12,23,0);
//			HandleMatrices.experience.set(5,26,23,0);
//			HandleMatrices.experience.set(5,35,23,0);
//			HandleMatrices.experience.set(6,15,23,0);
//			HandleMatrices.experience.set(6,34,23,0);
//			HandleMatrices.experience.set(1,34,26,0);
//			HandleMatrices.experience.set(3,34,77,0);
//			HandleMatrices.experience.set(5,34,50,0);
//			HandleMatrices.experience.set(6,34,80,0);
//			HandleMatrices.experience.set(6,34,35,0);
//			HandleMatrices.experience.set(6,34,23,0);
//			HandleMatrices.experience.set(0,12,34,0);
//			HandleMatrices.experience.set(0,69,34,0);
//			HandleMatrices.experience.set(4,26,34,0);
//			HandleMatrices.experience.set(4,38,34,0);
//			HandleMatrices.experience.set(5,77,34,0);
//			HandleMatrices.experience.set(6,56,34,0);
//			HandleMatrices.experience.set(1,33,24,0);
//			HandleMatrices.experience.set(2,27,33,0);
//			HandleMatrices.experience.set(4,45,92,0);
//			HandleMatrices.experience.set(3,92,45,0);
//			HandleMatrices.experience.set(5,56,57,0);
//			HandleMatrices.experience.set(6,56,34,0);
//			HandleMatrices.experience.set(0,35,56,0);
//			HandleMatrices.experience.set(4,57,56,0);
//			HandleMatrices.experience.set(6,55,89,0);
//			HandleMatrices.experience.set(3,88,55,0);
//			
//	
//			HandleMatrices.reflexLookUp.set(25,3,0);
//			HandleMatrices.reflexLookUp.set(73,4,HandleMatrices.reflexLookUp.get(73,4)-1);
//			HandleMatrices.reflexLookUp.set(36,6,0);
//			HandleMatrices.reflexLookUp.set(87,5,HandleMatrices.reflexLookUp.get(87,5)-1);
//			HandleMatrices.reflexLookUp.set(47,5,0);
//			HandleMatrices.reflexLookUp.set(98,1,HandleMatrices.reflexLookUp.get(98,1)-1);
//			HandleMatrices.reflexLookUp.set(57,1,0);
//			HandleMatrices.reflexLookUp.set(57,4,0);
//			HandleMatrices.reflexLookUp.set(57,7,0);
//			HandleMatrices.reflexLookUp.set(56,5,0);
//			HandleMatrices.reflexLookUp.set(86,2,HandleMatrices.reflexLookUp.get(86,2)-1);
//			HandleMatrices.reflexLookUp.set(87,2,HandleMatrices.reflexLookUp.get(87,2)-1);
//			HandleMatrices.reflexLookUp.set(46,1,0);
//			HandleMatrices.reflexLookUp.set(46,1,0);
//			HandleMatrices.reflexLookUp.set(28,6,HandleMatrices.reflexLookUp.get(28,6)-1);
//			HandleMatrices.reflexLookUp.set(12,7,0);
//			HandleMatrices.reflexLookUp.set(35,0,0);
//			HandleMatrices.reflexLookUp.set(35,5,0);
//			HandleMatrices.reflexLookUp.set(35,7,0);
//			HandleMatrices.reflexLookUp.set(87,2,HandleMatrices.reflexLookUp.get(87,2)-1);
//			HandleMatrices.reflexLookUp.set(23,4,0);
//			HandleMatrices.reflexLookUp.set(34,6,0);
//			HandleMatrices.reflexLookUp.set(24,0,0);
//			HandleMatrices.reflexLookUp.set(24,2,0);
//			HandleMatrices.reflexLookUp.set(24,6,0);
//			HandleMatrices.reflexLookUp.set(46,1,0);
//			HandleMatrices.reflexLookUp.set(33,1,0);
//			HandleMatrices.reflexLookUp.set(16,5,0);
//			HandleMatrices.reflexLookUp.set(13,6,0);
//			HandleMatrices.reflexLookUp.set(16,3,0);
//			HandleMatrices.reflexLookUp.set(12,0,0);
//			HandleMatrices.reflexLookUp.set(12,1,0);
//			HandleMatrices.reflexLookUp.set(12,2,0);
//			HandleMatrices.reflexLookUp.set(12,4,0);
//			HandleMatrices.reflexLookUp.set(12,5,0);
//			HandleMatrices.reflexLookUp.set(12,5,0);
//			HandleMatrices.reflexLookUp.set(12,7,0);
//			HandleMatrices.reflexLookUp.set(12,7,0);
//			HandleMatrices.reflexLookUp.set(12,7,0);
//			HandleMatrices.reflexLookUp.set(23,1,0);
//			HandleMatrices.reflexLookUp.set(26,3,0);
//			HandleMatrices.reflexLookUp.set(49,3,HandleMatrices.reflexLookUp.get(49,3)-1);
//			HandleMatrices.reflexLookUp.set(23,4,0);
//			HandleMatrices.reflexLookUp.set(10,6,0);
//			HandleMatrices.reflexLookUp.set(24,6,0);
//			HandleMatrices.reflexLookUp.set(27,6,HandleMatrices.reflexLookUp.get(27,6)-1);
//			HandleMatrices.reflexLookUp.set(23,7,0);
//			HandleMatrices.reflexLookUp.set(23,0,0);
//			HandleMatrices.reflexLookUp.set(23,2,0);
//			HandleMatrices.reflexLookUp.set(23,2,0);
//			HandleMatrices.reflexLookUp.set(23,3,0);
//			HandleMatrices.reflexLookUp.set(23,4,0);
//			HandleMatrices.reflexLookUp.set(23,4,0);
//			HandleMatrices.reflexLookUp.set(23,7,0);
//			HandleMatrices.reflexLookUp.set(49,1,HandleMatrices.reflexLookUp.get(49,1)-1);
//			HandleMatrices.reflexLookUp.set(12,2,0);
//			HandleMatrices.reflexLookUp.set(26,3,HandleMatrices.reflexLookUp.get(26,3)-1);
//			HandleMatrices.reflexLookUp.set(12,5,0);
//			HandleMatrices.reflexLookUp.set(26,5,HandleMatrices.reflexLookUp.get(26,5)-1);
//			HandleMatrices.reflexLookUp.set(35,5,0);
//			HandleMatrices.reflexLookUp.set(15,6,0);
//			HandleMatrices.reflexLookUp.set(34,6,0);
//			HandleMatrices.reflexLookUp.set(34,1,0);
//			HandleMatrices.reflexLookUp.set(34,3,0);
//			HandleMatrices.reflexLookUp.set(34,5,0);
//			HandleMatrices.reflexLookUp.set(34,6,0);
//			HandleMatrices.reflexLookUp.set(34,6,0);
//			HandleMatrices.reflexLookUp.set(34,6,0);
//			HandleMatrices.reflexLookUp.set(12,0,0);
//			HandleMatrices.reflexLookUp.set(69,0,HandleMatrices.reflexLookUp.get(69,0)-1);
//			HandleMatrices.reflexLookUp.set(26,0,0);
//			HandleMatrices.reflexLookUp.set(38,4,0);
//			HandleMatrices.reflexLookUp.set(77,5,0);
//			HandleMatrices.reflexLookUp.set(56,6,0);
//			HandleMatrices.reflexLookUp.set(33,1,0);
//			HandleMatrices.reflexLookUp.set(27,2,0);
//			HandleMatrices.reflexLookUp.set(45,4,0);
//			HandleMatrices.reflexLookUp.set(92,3,HandleMatrices.reflexLookUp.get(92,3)-1);
//			HandleMatrices.reflexLookUp.set(56,5,0);
//			HandleMatrices.reflexLookUp.set(56,6,0);
//			HandleMatrices.reflexLookUp.set(35,0,0);
//			HandleMatrices.reflexLookUp.set(57,4,0);
//			HandleMatrices.reflexLookUp.set(55,6,0);
//			HandleMatrices.reflexLookUp.set(88,3,HandleMatrices.reflexLookUp.get(88,3)-1);
					
			// we have to reset the innerstates as they are loaded with the net
			innerstate.setState("actlastplace", -1);
			innerstate.setState("nrlastplace", -1);
			innerstate.setState("Orient", 1);
			innerstate.setState("decision", 1);
//			Initialize the Matrices for Flooding
			if(flooding){
				matrix.prepareFlooding(nrplacearray,nrDecision);
			}
		}
//////////////////////////////////////////////////////////////////////////////End Of Initialize
		
		
		//If the robot bumps a second time in the wall the Reflex is set on
		rotation=rotate.getIncomingActivation();
		if(rotation==-1){
			innerstate.setState("Reflex","-1");
			rotation=0;
		}
		
//		logger.debug("Rotation  " +rotation);
		//Make shure that no decision is taken into account if the Robot is rotating.
		
		if((netstep-innerstate.getStateLong("Filternetsteps"))==20){
			nodecision=false;
			buffer.clean();
			logger.debug("Decision posiible");
		}
		
		
		if((innerstate.getStateDouble("Rotate")-rotation)<0){
			innerstate.setState("Rotate",rotation);
			nodecision=true;
			innerstate.setState("Filternetsteps",0);
			logger.debug("ROTATE STATE ON");
		}
		if((innerstate.getStateDouble("Rotate")-rotation)>0){
			nodecision=true;
			innerstate.setState("Filternetsteps",netstep);
			innerstate.setState("Rotate",rotation);
			logger.debug("ROTATE STATE OFF");
		}
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
						
		// saving the numerous matrices after a certain (mod) amount of netsteps
		
		if (debugmodus){
			if ((netstep-innerstate.getStateLong("firstnetstep"))%saveEach == 0 && (netstep-innerstate.getStateLong("firstnetstep"))!=0) {
				logger.info("All files saved in netstep "+netstep);
				if(!matrix.saveMatrices(netstep,path,nrplacearray)){
					logger.error("Matrices are not stored");
				}
				if(!decisionlog.saveLogger(netstep,path)){
					logger.error("Logger are not stored");
				}
			}
		}
		// test if we run in the random mode or in the flooding mode
		// floodingmode: urge is over threshold, thus we have to run the
		// flooding (if flood = 1) or to follow the path (decisionVector)
		// which a recent flooding gave to us (if flood = 0)
			
			
		double[] urgeinput=new double[urge.length];
				
		//number of which decision should be taken
		int decisionnumber = -1;
		//number of placefield
		double act;
		double reflexinput=reflex.getIncomingActivation();
				
				
		// Read Out Urges
		for (int j=0;j<urge.length;j++){
		//			urgeinput[j]=urge[j].getIncomingActivation();
			if(flooding){
				urgeinput[0]=10;
				urgeinput[1]=0;
				urgeinput[2]=0;
				urgeinput[3]=0;
			}else{
				urgeinput[0]=urge[0].getIncomingActivation();
				urgeinput[1]=urge[1].getIncomingActivation();
				urgeinput[2]=urge[2].getIncomingActivation();
				urgeinput[3]=urge[3].getIncomingActivation();
			}
			}
//		Only go there if robot has already stoped and do it only once the calculation
		if(calculate && !stoprobot){
			logger.debug("Flooding the Matrices");
			floodingMatrice(urgeinput,netstep);
//			wait another four timesteps;
			stoprobot=true;
			innerstate.setState("stoprobot",netstep);
//			after this time the robot has to take the best decision
			nextdecision=true;
//			calculate already done
			calculate=false;
		}
//		if robot stoped for 4 netsteps after calculating the flooding part of the matrices start the best decision
		if(!stoprobot && nextdecision){
			logger.debug("Given next decision");
			decisionnumber=findBestDecision(innerstate.getStateInt("CurrentPlacecell"));
			innerstate.setState("Orient",decisionnumber*Math.PI/4);
			innerstate.setState("decision",decisionnumber);
			innerstate.setState("nrlastplace",innerstate.getStateInt("CurrentPlacecell"));
			logger.debug("Orientation send "+decisionnumber*45);
//			done the next decision
			nextdecision=false;
//			send it directly to the AllocentricEgocentric and start in the next timestep
			stoprobot=true;
			sendnextdecision=true;
		}
			
		int number= (int)((netstep-innerstate.getStateLong("firstnetstep"))%saveEach);
//		update of decision
		if(debugmodus){
			if(nodecision)
				decisionlog.logUpdate(number,netstep,position,orient.getIncomingActivation(),innerstate.getStateInt("nrlastplace"),innerstate.getStateDouble("actlastplace"),urgeinput,getPlaceCellInfo(number),new double[]{sensor1.getIncomingActivation(),sensor2.getIncomingActivation(),sensor3.getIncomingActivation(),sensor4.getIncomingActivation()},innerstate.getStateDouble("Reflex"),innerstate.getStateDouble("Rotate"),0);
			else
				decisionlog.logUpdate(number,netstep,position,orient.getIncomingActivation(),innerstate.getStateInt("nrlastplace"),innerstate.getStateDouble("actlastplace"),urgeinput,getPlaceCellInfo(number),new double[]{sensor1.getIncomingActivation(),sensor2.getIncomingActivation(),sensor3.getIncomingActivation(),sensor4.getIncomingActivation()},innerstate.getStateDouble("Reflex"),innerstate.getStateDouble("Rotate"),1);
		}
		
		//In case the robot has to stop because of any calculation
		if(!stoprobot){
				if(!nodecision){
					this_place_num = (int)getPlaceCellInfo(number)[0];
				}else
					this_place_num = innerstate.getStateInt("nrlastplace");
				
				//Make Low Pass Filter of the activities of the netsteps amd strore act
				act=getPlaceCellInfo(number)[1];
				if(act != buffer.getEntry(0))
					buffer.update(act);
				
				//set the buffer to zeros, everytime when a new placecell is entered
				if(this_place_num != innerstate.getStateInt("LastCycle"))
					buffer.clean();
				
				//only while standing at a placefield a decision can be drawn, so do nothing if:
				//    this place is not a valid place OR i'm in the same place as where i drew the last decision OR the gradient of the place field is positively increasing
				
				if ((this_place_num == 0 || this_place_num == innerstate.getStateInt("nrlastplace") || !buffer.difference() || nodecision)) {
					// do nothing except to set the value to -1 so that the Allocentric-Egocentric Module do nothing except moving straight
					// note: if our Gaussians were not all equally parametered, then there could be a case where two overlap in such
					// a way that a decrease in the gradient is not found until the agent has entered the second place...
					innerstate.setState("Orient",-1);
					if (reflexinput<0){
						innerstate.setState("Reflex",reflexinput); // Give a discaunting factor onto the connection of the placefields if the robot bumps iinto the wall
					}
					
				} 
				else { 			
					logger.debug("------------------------------- decision ----------------------------- ");
					if (innerstate.getStateInt("nrlastplace") != -1 & expmode){
						//calculate the experience Matrice
						updateMatrices(this_place_num,innerstate.getStateDouble("Reflex"));
						
						if(innerstate.getStateDouble("Reflex")<0){
							logger.debug("RRRRRRRRRRRRRRRRRRRREEEEEEEEEEEEEEEEEEEEEEEEEEFLEX!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
							innerstate.setState("Reflex",0);
						}
					}
					
//					In case of stopping the Motor the placecell number has to be stored because near boundaire and the Noise
					if(expmode && flooding)
						innerstate.setState("CurrentPlacecell",this_place_num);
					
					
//					Online Learning
					if(expmode && flooding){
						innerstate.setState("flood",1);
//						stop the robot
						stoprobot=true;
						innerstate.setState("stoprobot",netstep);
//						after waiting 4 timesteps the robot has to calculate the flooding and the best decision
						calculate=true;
					}else{			
						for (int j=0;j<urge.length;j++){
							if(Math.abs(urgeinput[j]-lasturgeact[j])>threshold && flooding){
								runflood=true;
							}
						}
						if(runflood && flooding){
							logger.debug("Start the Flooding Part");
							runflood=false;
							innerstate.setState("flood",1);
//							stop the robot
							stoprobot=true;
							innerstate.setState("stoprobot",netstep);
//							after waiting 4 timesteps the robot has to calculate the flooding and the best decision
							calculate=true;
//							Remeber the placecell where the decision was taken
							innerstate.setState("CurrentPlacecell",this_place_num);
							for(int i=0;i<urgeinput.length;i++){
								lasturgeact[i]=urgeinput[i];
							}							
						}else{
							if(flooding){
//    				            	set BestDecision
								logger.debug("Loook for the Best decision");
								decisionnumber = findBestDecision(this_place_num);
								innerstate.setState("Orient",decisionnumber*Math.PI/4);
								innerstate.setState("decision",decisionnumber);
								innerstate.setState("nrlastplace", this_place_num);
								logger.debug("Number of Decision"+decisionnumber);
								}
						}	
					}
					
//					Random Decision if in exmode
					if(!flooding){
						if(!stoprobot){
						
						// move random in the arena so that each Number of decision on each Placecell should be chosen equal likely
		//				if(((innerstate.getStateLong("firstnetstep")-netstep)%40000)==0 || firstsort){
		//					firstsort=false;
		//					double trigger=matrix.Sum();
		//					if((trigger-lookprob)>200){
		//						matrix.Sort(nrDecision,netstep,path);
		//						lookprob=trigger;
		//						
		//					}
		//				}
		//				if(lookprob>0 && HandleMatrices.frequvisits.get(this_place_num-1,0)>0){
		//					double choose=(Math.ceil(Math.abs(gauss.nextGaussian()*nrDecision)))%8;
		//					if(choose==0){
		//						choose=8.0;
		//					}
		//					
		//					if(choose>nrDecision){
		//						if(Math.random()>0.5){
		//							choose=8;
		//						}else{
		//							choose=7;
		//						}
		//					}
		//					int take=(int)choose;
		//					decisionnumber=(int)HandleMatrices.frequvisits.get(this_place_num-1,take-1);
		//				}else{
//							
//						switch (this_place_num) {
//						case 104:
//							decisionnumber=5;
//							break;
//						case 105:
//							decisionnumber=4;
//							break;
//						case 106:
//							decisionnumber=4;
//							break;
//						case 107:
//							decisionnumber=3;
//							break;
//						case 96:
//							decisionnumber=2;
//							break;
//						case 85:
//							decisionnumber=2;
//							break;
//						case 51:
//							decisionnumber=8;
//							break;
//						case 50:
//							decisionnumber=7;
//							break;
//						case 49:
//							decisionnumber=7;
//							break;
//						case 70:
//							decisionnumber=6;
//							break;
//						case 81:
//							decisionnumber=6;
//							break;
//						case 92:
//							decisionnumber=6;
//							break;
//						case 72:
//							decisionnumber=4;
//							break;
//						default:
							decisionnumber = (int) Math.ceil(Math.random() * nrDecision);
//							break;
//						}	
							
							
							
		//				}
						/////////////////////////////////End of Move Random equaly
					    logger.debug("Chosen random Decision");
						sendorient = decisionnumber * Math.PI / 4;
						innerstate.setState("nrlastplace", this_place_num);
						innerstate.setState("Orient", sendorient);
						innerstate.setState("decision", decisionnumber);
						logger.debug("Orienation Flooding "+innerstate.getStateDouble("decision")*45);
						// logger.debug("RANDOM --------- WALK _____ "+decisionnumber+"  ("+Math.toDegrees(sendorient)+")_____"+this_place_num);
						}else{
//							Stop the robot
							innerstate.setState("Orient",-.5);
						}
					}
		
				}// end else make a decision
		
				//Remember the last placecell of the cycle to reset the buffer
				innerstate.setState("LastCycle",this_place_num);
				//In case robot is looking for Food
//				if(flooding){
		//			if(redact==0 && greenact==0 && yellact==0 && blueact==0){//this_place_num==positionUrges[0]){
		//				//set the speed to zeros if the robot reached the food	
		//				innerstate.setState("Orient",-.5);
		//			}
		//			//Setting of the Gates continously at 1; The Tobic Activation and setting to 0 if the urge is reached
		//			switch (this_place_num) {
		//			case RedUrge:redact=0;  //red
		//			break;
		//			case GreenUrge:greenact=0; //green
		//			break;
		//			case BlueUrge:blueact=0; //blue
		//			break;
		//			case YellUrge:yellact=0; //yellow
		//			break;
		//			}
		//	
		//			if((urgeinput[0]-lasturgeact[0])<0)
		//				redact=0;
		//			if((urgeinput[1]-lasturgeact[1])<0)
		//				greenact=0;
		//			if((urgeinput[2]-lasturgeact[2])<0)
		//				blueact=0;
		//			if((urgeinput[3]-lasturgeact[3])<0)
		//				yellact=0;
		//			
		//			manipulator.setGateActivation(GT_UrgeBlue_Value,blueact);
		//			manipulator.setGateActivation(GT_UrgeGreen_Value,greenact);
		//			manipulator.setGateActivation(GT_UrgeRed_Value,redact);
		//			manipulator.setGateActivation(GT_UrgeYell_Value,yellact);
//				}
			}//End of Stoprobot
			else {
//				logger.debug("Stop the Robot");
				if(!sendnextdecision){
	//			Stop the robot
				innerstate.setState("Orient",-.5);
				}else{
					sendnextdecision=false;
				}
				if((netstep-innerstate.getStateLong("stoprobot"))>4){
					stoprobot=false;
				}
		}
		if(flooding){
			if(this_place_num==RedUrge){
				innerstate.setState("Orient",-.5);
//				logger.debug("END  reached "+RedUrge);
			}
		}
		if (debugmodus){
			decisionlog.logupdateDecision((int)((netstep-innerstate.getStateLong("firstnetstep"))%saveEach),decisionnumber,innerstate.getStateDouble("Orient"));
		}
		//setting the activation of the gate to the Orient value
		manipulator.setGateActivation(GT_Action_Value,innerstate.getStateDouble("Orient"));
//		if(innerstate.getStateDouble("Orient")>0)
//		logger.debug("Orient sended "+innerstate.getStateDouble("Orient"));
	}
	
}

