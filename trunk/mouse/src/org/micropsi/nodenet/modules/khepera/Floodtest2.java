/*
 * NOTE TO DANIEL:
 *  I made lots of little comments - they are either in "bookmarks" or to-dos - roll over the thingy on the left side of the window to see them!
 *  I changed the stuff you wanted in the color method, but didn't try running it because things got confused with other changes -
 *  maybe find me downstairs and we can talk about it together so you understand what my comments mean!
 *  hope you had a lovely weekend...
 */

/* OLD NOTE TO DANIEL:
 * changes made from Floodingtest:
 * 	- changed gate names & activation names with the following rough rule:
 * 			red -> tonic1
 * 			green -> tonic2
 * 			blue -> tonic3
 * 			yellow -> tonic4
 *  - changed the query return for gate types from "urgex" to "food x"
 *  - line 546 (approx!), changed a zero to threshold in the boolean flooding check.
 *  - added new sensors at slots:
 *  			ST_Red_Sensor = 70012;
 *			ST_Green_Sensor = 70013;
 *			ST_Blue_Sensor = 70014;
 *			ST_Yellow_Sensor = 70015;
 *	- wrote lots of stuff
 * 
 * 
 *  - NOTE: the command to stop him moving is commented out...  you need to fix it!
 *  				line 803ish
 * THINGS WE SHOULD MAYBE CHANGE:
 *  
 */



package org.micropsi.nodenet.modules.khepera;


import java.util.Iterator;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.NodeSpaceModule;
import org.micropsi.nodenet.Slot;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.SparseDoubleMatrix3D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.math.Functions;
import cern.jet.stat.Descriptive;

import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;

//import com.sun.org.apache.xerces.internal.impl.XMLEntityManager.Entity;


public class Floodtest2 extends AbstractNativeModuleImpl {

	
	//define Parameter for the flooding algorithm
	private static final int RedUrge=6;
	private static final int GreenUrge=149;
	private static final int BlueUrge=77;
	private static final int YellUrge=78;
	
	private static final int [] positionUrges = {RedUrge,GreenUrge,BlueUrge,YellUrge};
	private double decay=0.9;
	//private static int nrPlacecells=12;
	private static int nrPlacecellsrow=11;
	private static int nrPlacecellscol=15;
	private static int nrDecision=8;
	private static double threshold=0;
	
    private static boolean debugmodus=true; //save all matrices and data
    
    private static int foodposition=-1;
     
    //Store the last activation
    private static double [] lasturgeact = {0,0,0,0};
    
    private static int[] position = new int [2];
    
    //Flooding
    private boolean flooding=true;
    
    private static boolean loadmatrices = true; // load the matrices with the specified timestamp
    private static boolean loadGauss = true;  // flag to request that a pre-computed distribution of placefields is loaded
    private static boolean expmode = false; // flag if the robot should walk in the flooding mode or experience mod
    // convenience path variable for the in- and output of matrices
    private static String path = "/home/student/d/dweiller/NavigationProject/Matrices/12042006/";
    

    // timestamp for the matrices that should be loaded (up to now experience matrix, later also urgevector & transition matrix..)
    private static String timestamp = "0405061420_17746"; //e.g. "1901061828_20000"
    
    //private static int nrplacearray=(nrPlacecells*nrPlacecells);
    private static int nrplacearray=(nrPlacecellsrow*nrPlacecellscol);
    
    // after how many netsteps the matrices should be regularly saved?
    private static int saveEach=20000;
	
	
	//public static int totalNrPlaccells=nrPlacecells*nrPlacecells;
	
	public static int [] color_food = {0, 0, 0, 0}; //flag to indicate whether a color sensor has been linked to a food node
	
	private static DecisionLogger decisionlog= new DecisionLogger(23,saveEach);
	
	private static boolean updateColortrans=false;
	
	// definition of the slots and gates
	public static final int GT_Tonic_Food1 = 69996;
	
	public static final int GT_Tonic_Food2 = 69997;
	
	public static final int GT_Tonic_Food3 = 69998;
	
	public static final int GT_Tonic_Food4 = 69999;
	
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
	
	public static final int ST_Red_Sensor = 70012;
	
	public static final int ST_Green_Sensor = 70013;
	
	public static final int ST_Blue_Sensor = 70014;
	
	public static final int ST_Yellow_Sensor = 70015;
	
	//setting the value of the orientation which should be sent
	public static double sendorient = -1;
	
	// set the values of the tonic activations to be sent to each "food" that is then tagged to a place field
	private static double tonic1=1;
	private static double tonic2=1;
	private static double tonic3=1;
	private static double tonic4=1;
	private static double rotation;
	private static boolean nodecision=false;
	private static boolean runflood=false;
	private static double remorient=-5;
	
	private static long stopdecision=0;
	
//	Handle all matrices
	private static HandleMatrices matrix= new HandleMatrices(1000,1000,nrplacearray,nrDecision,true);
	
	//How many decision in one placefield can be done
	public static int[] NumberOfDecisions = new int [nrplacearray];
	
	Algebra algebra = new Algebra();
	
	
	//Store the Activation of the (number) last netsteps
	public static MakeLowPassFilter buffer = new MakeLowPassFilter(3);
	public static MakeLowPassFilter redcolorbuffer = new MakeLowPassFilter(10);
	public static MakeLowPassFilter greencolorbuffer = new MakeLowPassFilter(10);
	public static MakeLowPassFilter bluecolorbuffer = new MakeLowPassFilter(10);
	public static MakeLowPassFilter yellcolorbuffer = new MakeLowPassFilter(10);
	
	
	
	// for GetEntities method!
	//private Iterator<NetEntity> allentities;
	
	/**
	 * Gets the number and activation of the placecell we are currently on.
	 */
//	private double[] getPlaceCellInfo(long netstep) {  // replaced with:
	private double[] getPlaceCellInfo() {
		
		double[] placeInfo = new double[2];
		//get the indice of the current place cell from the placefield at x,y
		int xposition = (int) (posx.getIncomingActivation() * 10.0);
		int yposition = (int) (posy.getIncomingActivation() * 10.0);
		placeInfo[0] = HandleMatrices.placefieldsnr.getPlaceCellEntry(xposition, yposition);
		placeInfo[1] = HandleMatrices.placefieldsact.getPlaceCellEntry(xposition,yposition);
		position[0]=xposition;
		position[1]=yposition;
		return placeInfo;
	}	
	
	
	private boolean stopRobot(long step){
		if(stopdecision==0){
			stopdecision=step;
			return false;
		}
		else{
			if(step-stopdecision<4){
				return false;
			}else{
				stopdecision=0;
				return true;
			}
		}	
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
					reflexfactor=1.0-(5.0/6.0)*(HandleMatrices.reflexLookUp.get(row,dec)/norm);
					HandleMatrices.transition.viewSlice(dec).viewRow(row).assign(Functions.div(norm));
					if(reflexfactor<1){
						HandleMatrices.transition.viewSlice(dec).viewRow(row).assign(Functions.mult(reflexfactor));
					}
				}
					
			}
		}		
	}
	
	private boolean updateTransitionColor(long netstep){
		// logger.debug("Update of Transitionmatrix");
		double norm=0;
		double reflexfactor=0;
		DoubleMatrix2D refl= DoubleFactory2D.dense.make(nrplacearray,nrDecision);
		
		//Color transition matrix is the transpose of the normal one, so transpose the exp matrix, and then normalise as usual...
		//transposeEx = (SparseDoubleMatrix3D)HandleMatrices.experience.copy();//.viewDice(0,2,1);	
		HandleMatrices.colortrans = (SparseDoubleMatrix3D)HandleMatrices.experience.copy().viewDice(0,2,1);
		
		for(int dec=0;dec<nrDecision;dec++){	
		//	transition.viewSlice(dec).getNonZeros(rowl,columnl,value); Could be done only by this rows which are not zero. But what is the order if the column!!
			for(int col=0;col<nrplacearray;col++){
//		The normalization has to be done over the Targets and in this manner over the columns!!!
				norm=HandleMatrices.colortrans.viewSlice(dec).viewColumn(col).zSum();
				if(norm>0){
					reflexfactor=1.0-((5.0/6.0)*(HandleMatrices.reflexLookUp.get(col,dec)/norm));
					refl.set(col,dec,reflexfactor);
					HandleMatrices.colortrans.viewSlice(dec).viewColumn(col).assign(Functions.div(norm));
					if(reflexfactor<1.0){
//						logger.debug("ReflexFaktor    :"+reflexfactor);
						HandleMatrices.colortrans.viewSlice(dec).viewColumn(col).assign(Functions.mult(reflexfactor));
					}
				}
					
			}
		}
//		if(! matrix.save3dMatrix(HandleMatrices.colortrans,netstep,"CorlorTrans",path)){
//			logger.debug("Did not save ColorTrans");
//		}
//		
		
		return true;
	}
	
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

	
	
	
	
	
	
	private final int[] gateTypes = { GT_Action_Value,GT_Tonic_Food1,GT_Tonic_Food2,GT_Tonic_Food3,GT_Tonic_Food4};

	private final int[] slotTypes = { ST_Orientation_Sensor,
			ST_Positionx_Sensor, ST_Positiony_Sensor, ST_Urge1_Sensor,ST_Urge2_Sensor,ST_Urge3_Sensor,ST_Urge4_Sensor,ST_Reflex_Value,ST_Rotate_Value,ST_Red_Sensor,ST_Green_Sensor,ST_Blue_Sensor,ST_Yellow_Sensor};

	@Override
	protected int[] getGateTypes() {
		return gateTypes;
	}

	@Override
	protected int[] getSlotTypes() {
		return slotTypes;
	}

	Slot orient, posx, posy,reflex,rotate;
	Slot [] urge = new Slot[(positionUrges.length)];
	Slot [] color = new Slot[4];  // [red, green, blue, yellow]
	
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
			case ST_Red_Sensor:
				color[0] = slots[i];
				break;
			case ST_Green_Sensor:
				color[1] = slots[i];
				break;
			case ST_Blue_Sensor:
				color[2] = slots[i];
				break;
			case ST_Yellow_Sensor:
				color[3] = slots[i];
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
				case GT_Tonic_Food1:
					return "Food1";
				case GT_Tonic_Food2:
					return "Food2";
				case GT_Tonic_Food3:
					return "Food3";
				case GT_Tonic_Food4:
					return "Food4";
				
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
				case ST_Red_Sensor:
					return "Color 1 Red";
				case ST_Green_Sensor:
					return "Color 2 Green";
				case ST_Blue_Sensor:
					return "Color 3 Blue";
				case ST_Yellow_Sensor:	
					return "Color 4 Yellow";
				}
				return null;
			}

		});
		firsttime = false;
	}

	
	private int colorCheck(int this_place_num, double [] activity,long netstep,double orient) {		
			int color_num = -1;  // color index (follows this convention: {R,G,B,Y})
			
			// check the color sensors and LowPassFilter it 
			if(lowPassColor(activity[0],activity[1],activity[2],activity[3])){
				for (int i = 0; i<activity.length; i++) 
					if (activity[i] > 0.0)
						color_num = i;
			}
			
			if (color_num>-1 && color_food[color_num]==0) { // if you can see a color stimulus which has not been linked to a food source...
				logger.debug("Now aware of color stimulus and beginning processing!");
				
				if(!stopRobot(netstep)){
					logger.debug("Stop the robot");
					if(orient!=-0.5){
						remorient=orient;
					}
					return -5;
				}				
				if(orient!=-0.5){
					remorient=orient;
				}else
					remorient=-5;
				
				// link it...					
				if (colorLink(this_place_num-1, color_num, netstep)) { //if the modulation was successful...
					return(color_num);	
				} // inherent "else" condition is to try again next netstep... but in theory modulation should only fail if there is no transition matrix
			
			} 
			
			else if (color_num>-1 && color_food[color_num]==1) { // if you can see a color stimulus which has been linked to a food source...
				//colorRelink(this_place_num, color_num);  // to be added later...
				
			}
			return(-1);
	}
	

	private boolean lowPassColor(double red,double green,double blue,double yell){
		
		if(red>0)
			redcolorbuffer.update(red);
		else
			redcolorbuffer.clean();
		if(blue>0)
			bluecolorbuffer.update(blue);
		else
			bluecolorbuffer.clean();
		if(green>0)
			greencolorbuffer.update(green);
		else
			greencolorbuffer.clean();
		if(yell>0)
			yellcolorbuffer.update(yell);
		else
			yellcolorbuffer.clean();
		
		if(redcolorbuffer.looksame() || greencolorbuffer.looksame() || bluecolorbuffer.looksame() || yellcolorbuffer.looksame()){
			return true;
		}else{
			return false;
		}		
	}
	
	/**
	 * This method creates a new link between the color sensor indexed by 'color' and the
	 * closest food source that can be reached from the place field indexed by 'place'
	 * 
	 * The food sources are currently "hard-wired" at locations given in the (global) vector positionUrges.
	 * 
	 * @param placeindex - the index of the place field at which the robot is now located
	 * @return boolean - indicates success (true) or failure (false)
	 */
	
	private boolean colorLink(int place, int color_index, long netstep) {
		logger.debug("Now in the colorModulation method...");
		int food_link = -1;  // index of food node to be linked to color sensor
		boolean flood_it = true;  // flag to start / stop forward flooding...
		double act_thresh = 0; // holds a threshold activation value - used to check for max act at food sources
		
		// update the color transition matrix
		if(!updateColortrans){
			updateColortrans=updateTransitionColor(netstep);
		}
				
		DenseDoubleMatrix1D coloract = new DenseDoubleMatrix1D(nrplacearray);  	//I've put this here because it should be
																				//initialised every time colorLink is called...
				
		IntArrayList indices = new IntArrayList();
		DoubleArrayList values = new DoubleArrayList();
		// FOR DEBUGGING (in case the do while loop runs infinitely...)
		int counter = 0;
		
		do { // 	forward-flooding while flood_it is true
			logger.debug("Now in the flooding part of the colorModulation method !");	
		
			coloract.set(place,1); // set place=1, i.e. flood from current location!
			
			// multiply color transition matrix by current activation.
			for(int i = 0; i < nrDecision; i++){
				HandleMatrices.colortempact = (DenseDoubleMatrix1D) algebra.mult(HandleMatrices.colortrans.viewSlice(i),coloract);
				HandleMatrices.colortargetsXdec.viewColumn(i).assign(HandleMatrices.colortempact);
			}
			
			// set the activation for next iteration of forward-flooding...
			// take the maximum of colortargetsXdec, and multiply by decay factor
			for (int i = 0; i < HandleMatrices.colortargetsXdec.rows(); i++) {
				HandleMatrices.colortargetsXdec.viewRow(i).getNonZeros(indices,values);			
				if(values.size()>=1){
					coloract.set(i,Descriptive.max(values)*decay);
				}	
				else{
					coloract.set(i,0); // this is needed in order to overwrite previous values...
				}
			}
			
			// check activations at food sources and take the maximum activation
			for (int i=0; i < positionUrges.length; i++) {
				if (coloract.get(positionUrges[i]-1) > act_thresh) { // if act is greater than threshold
					food_link=i; // link this food source
					act_thresh=coloract.get(positionUrges[i]-1); //update the lower threshold of activation
					flood_it = false; // break out of the do while loop after this iteration
				}
			}
						
			// FOR DEBUGGING (in case the do while loop runs infinitely...)
			counter++;
			if (counter==100) {
				logger.debug("ColorModulation while loop got stuck - broke out at 100 iterations");
				matrix.save1dMatrix(coloract,netstep,"ColorFloodedActivation",path);
				return(false);
			}	
		} while (flood_it);	
		matrix.save1dMatrix(coloract,netstep,"ColorFloodedActivation",path);
		
		///for Debugging
		foodposition=food_link;
		logger.debug("FooodNode "+foodposition);
		logger.debug("ColorNode "+color_index);
		
		// GET IDs OF THE ENTITIES TO BE LINKED
		// arrays of the names of the possible entities to be linked (one food will be linked to one color)
		String [] food_names = {"Food1", "Food2", "Food3", "Food4"};
		String [] color_names = {"Red", "Green", "Blue", "Yellow"};
		
		// initialise ID strings for the entities to be linked...
		String foodID = null;
		String colorID = null;
		
		// food_link is the food node index to be modulated by color activation
		
		NodeSpaceModule testnodesp = null;
		try {
			testnodesp = structure.getSpace();
		} catch (NetIntegrityException e) {
			e.printStackTrace();
			logger.error("colorModulation: something went wrong with getting the node space");
			return false;
		}
		Iterator entity_iterator = testnodesp.getAllEntities();
		
		
		//looking for the id of the nodes which should be connected
		while(entity_iterator.hasNext()) {
			NetEntity currentent = (NetEntity) entity_iterator.next();
			if (currentent.getEntityName().equals(food_names[food_link])) {
				foodID = currentent.getID();
				logger.debug("foodID"+foodID);
			}
			if (currentent.getEntityName().equals(color_names[color_index])) {
				colorID = currentent.getID();
				logger.debug("colorID"+colorID);
			}
		}
		
		// DUMMY ARRAY FOR WEIGHT VALUES - LATER THESE SHOULD BE LEARNED
		// (they represent the valence of the color stimulus...
		double [] color_weights = {10, -1, -1, -1}; // [R,G,B,Y]
		
		double confidence = 1;
		// THIS IS A TEMPORARY WAY TO POINT THE NEW LINK INTO THE CORRECT SLOT
		int food_colorslot_num = 82001;
		
		// MAKE THE NEW LINK
		try {
			structure.createLink(colorID,GateTypesIF.GT_GEN,foodID,food_colorslot_num,color_weights[color_index],confidence);
		} catch (NetIntegrityException e) {
			e.printStackTrace();
			logger.error("colorModulation: something went wrong with creating the link");
			return false;
		}
		return true;
	}//end of colorModulation method

	/**
	 * This method checks if the link between the color sensor indexed by 'color' and a food node is
	 * the closest food source that can be reached from the place field indexed by 'place'.  If it is not
	 * the closest, then the link is destroyed, and a new link between the sensor and closer food node is
	 * created.
	 * 
	 * The food sources are currently "hard-wired" at locations given in the (global) vector positionUrges.
	 * 
	 * @param placeindex - the index of the place field at which the robot is now located
	 * @return boolean - indicates success (true) or failure (false)
	 */
	// TODO Cliona... fix this method so it makes sense!!
	/*private boolean colorRelink(int place, int color_index) {
		logger.debug("Now in the colorRelink method...");
		int food_link = -1;  // index of food node to be linked to color sensor
		boolean flood_it = true;  // flag to start / stop forward flooding...
		
		// make a new dummy activation vector for all place cells
		DenseDoubleMatrix1D activation =  new DenseDoubleMatrix1D(totalNrPlaccells);
		DenseDoubleMatrix1D tempAct = new DenseDoubleMatrix1D(totalNrPlaccells);
		
		// make a new "transition matrix" in order to use as "weights" for forward flooding.
		SparseDoubleMatrix3D colortrans = new SparseDoubleMatrix3D(nrDecision, totalNrPlaccells,totalNrPlaccells);
		// this holds the current activation for each cell, partioned by decision link that carried the activation in
		DenseDoubleMatrix2D targetsXdecisions = new DenseDoubleMatrix2D(totalNrPlaccells, nrDecision);
		
		colortrans = (SparseDoubleMatrix3D) transition.copy();	// make a copy of hte transition matrix
		// the transition matrix for "normal" flooding shows target to source connections - we need source to target 
		// so transpose it using viewDice - swap rows and columns:
		colortrans = (SparseDoubleMatrix3D) colortrans.viewDice(0,2,1);

		// FOR NOW: strengths of weights are also immaterial - reflexes are no longer penalised, so set all weights to 1
		int cardinality = colortrans.cardinality();				// how many non-zero cells?
		if (cardinality == 0) {
			return(false);
		}
		
		logger.debug("cardinality:"+cardinality);
		IntArrayList sliceList = new IntArrayList(cardinality); 
		IntArrayList rowList = new IntArrayList(cardinality);
		IntArrayList columnList = new IntArrayList(cardinality); 
		DoubleArrayList valueList = new DoubleArrayList(cardinality);
		colortrans.getNonZeros(sliceList, rowList, columnList, valueList); // get the indices and values of non-zero cells
		for (int i = 0; i < cardinality; i++) {
			colortrans.setQuick(sliceList.getQuick(i), rowList.getQuick(i), columnList.getQuick(i), 1.0); // set non-zero cells to 1
		}
		
		activation.set(place,1); // set place=1, i.e. flood from current location!
		
		// FOR DEBUGGING (in case the do while loop runs infinitely...)
		int counter = 0;
		
		do { // 	forward-flooding while flood_it is true
			logger.debug("Now in the flooding part of the colorRelink method !");	
			
			// multiply color transition matrix by current activation.
			for(int i = 0; i < nrDecision; i++){
				tempAct = (DenseDoubleMatrix1D) algebra.mult(colortrans.viewSlice(i),activation);
				targetsXdecisions.viewColumn(i).assign(tempAct);
			}
			
			// set the activation for next iteration of forward-flooding...
			// (no need to "re-inject" activation, because you can only be in one place at one time...)
			// (and no decay is needed because we don't remember the stuff from step to step...)
			DenseDoubleMatrix1D nextAct = new DenseDoubleMatrix1D(totalNrPlaccells);
			
			for (int i = 0; i < targetsXdecisions.rows(); i++) {
				if(targetsXdecisions.viewRow(i).cardinality()>0){
					nextAct.set(i,1);
				}
			}
		 
			activation = (DenseDoubleMatrix1D) nextAct.copy();
			
			// check if any of the food sources have been reached by activation...
			for (int i=0; i < positionUrges.length; i++) {
				if (activation.get(positionUrges[i]) > 0) { // if yes,
					flood_it = false;						// break out of this do while loop
					food_link = i;							// remember which food source to link
				}
			}
			
			// FOR DEBUGGING (in case the do while loop runs infinitely...)
			counter++;
			if (counter==100) {
				logger.debug("ColorRelink while loop got stuck - broke out at 100 iterations");
				saveColorActVector(activation,(long)place);
				return(false);
			}	
		} while (flood_it);	
		
		// GET IDs OF THE ENTITIES TO BE LINKED
		// arrays of the names of the possible entities to be linked (one food will be linked to one color)
		String [] food_names = {"Food1", "Food2", "Food3", "Food4"};
		String [] color_names = {"Red", "Green", "Blue", "Yellow"};
		
		// initialise ID strings for the entities to be linked...
		String foodID = null;
		String colorID = null;
		
		// food_link is the food node index to be modulated by color activation
		
		NodeSpaceModule testnodesp = null;
		try {
			testnodesp = structure.getSpace();
		} catch (NetIntegrityException e) {
			e.printStackTrace();
			logger.error("colorRelink: something went wrong with getting the node space");
			return false;
		}
		Iterator entity_iterator = testnodesp.getAllEntities();
		
		while(entity_iterator.hasNext()) {
			NetEntity currentent = (NetEntity) entity_iterator.next();
			if (currentent.getEntityName().equals(food_names[food_link])) {
				foodID = currentent.getID();
				logger.debug("foodID"+foodID);
			}
			if (currentent.getEntityName().equals(color_names[color_index])) {
				colorID = currentent.getID();
				logger.debug("colorID"+colorID);
			}
		}
		
		// DUMMY ARRAY FOR WEIGHT VALUES - LATER THESE SHOULD BE LEARNED
		// (the represent the valence of the color stimulus...
		double [] color_weights = {10, -1, -1, -1};
		// I can't remember what this is for...
		double confidence = 1;
		// THIS IS TEMPORARY WAY TO POINT THE NEW LINK INTO THE CORRECT SLOT
		int food_colorslot_num = 82001;
		
		// MAKE THE NEW LINK
		try {
			
			structure.createLink(colorID,GateTypesIF.GT_GEN,foodID,food_colorslot_num,color_weights[color_index],confidence);
		} catch (NetIntegrityException e) {
			e.printStackTrace();
			logger.error("colorRelink: something went wrong with creating the link");
			return false;
		}
		return true;
	}//end of colorRelink method
*/
	
	
	/**
	 * generell structure:
	 * 1. look if urgees are over threshold
	 *  yes > 
	 * 2a. flood if there was no recent flooding
	 * 2b. move by the decisionVector advice if there was a recent flooding
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
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
		// save the first netstep
		if (debugmodus)
		innerstate.ensureStateExistence("firstnetstep","netstep");
				
		
		int linked_color=-1;
		
		
		if(firsttime){ 
			
			initialize();
			catchSlots(slots);
			if(debugmodus)
			innerstate.setState("firstnetstep",netstep);
			
			if(loadmatrices){
				if(!matrix.loadAllMatrices(timestamp,path)){		
					logger.error("Loading of the data matirces went wrong");
				}
			}
			
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
			
			// we have to reset the innerstates as they are loaded with the net
			innerstate.setState("actlastplace", -1);
			innerstate.setState("nrlastplace", -1);
			innerstate.setState("Orient", 1);
			innerstate.setState("decision", 1);
//			loadDifferentExp("0803061456_162746" ,"0803061248_222746","0603061800_162746",netstep);
			
		}
		
		
		//If the robot bumps a second time in the wall the Reflex is set on
		rotation=rotate.getIncomingActivation();
		if(rotation==-1){
			innerstate.setState("Reflex","-1");
			rotation=0;
		}
//		logger.debug("Rotation  " +rotation);
		//Make shure that no decision is taken into account if the Robot is rotating.
		if((netstep-innerstate.getStateLong("Filternetsteps"))==40){
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
			if ((netstep-innerstate.getStateLong("firstnetstep"))%saveEach == 0 && (netstep-innerstate.getStateLong("firstnetstep"))!=0 ){
				/*saveDecisionMatlabreadable(netstep);
				saveProductMatrix(decisionMatrix,netstep,"decisionMatrix");
			}
			
			if ((netstep-innerstate.getStateLong("firstnetstep"))%saveEach == 0 && (netstep-innerstate.getStateLong("firstnetstep"))!=0) {
				logger.info("All files saved in netstep "+netstep);
				saveAll(netstep);
				saveProductMatrix(reflexLookUp,netstep,"ReflexMatrix");
			}
			 REPLACED ABOVE WITH:
			*/
				
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
		
		//boolean runflood=false;
		double[] urgeinput=new double[urge.length];
		
		//number of which decision should be taken
		int decisionnumber = -1;
		//number of placefield
		int this_place_num;
		double act;
		double reflexinput=reflex.getIncomingActivation();
		
		// Read Out Urges
		for (int j=0;j<urge.length;j++){
			urgeinput[j]=urge[j].getIncomingActivation();
			if(urgeinput[j]>0)
				flooding=true;
			}
		//this_place_num = (int)getPlaceCellInfo(netstep)[0];
		if(!nodecision){
			this_place_num = (int)getPlaceCellInfo()[0];
		}else
			this_place_num = innerstate.getStateInt("nrlastplace");
		
		
		//Make Low Pass Filter of the activities of the netsteps amd strore act
		/*act=getPlaceCellInfo(netstep)[1];
		if(act != buffer.getEntry(0))
			buffer.update(getPlaceCellInfo(netstep)[1]);
			REPLACD WITH:
			*/
		act=getPlaceCellInfo()[1];
		if(act != buffer.getEntry(0))
			buffer.update(act);
		
		
		// grab color activities
		double [] colorActs = new double [] {color[0].getIncomingActivation(),color[1].getIncomingActivation(),color[2].getIncomingActivation(),color[3].getIncomingActivation()};
		
		//		update of decision
		if(debugmodus){
			int number= (int)((netstep-innerstate.getStateLong("firstnetstep"))%saveEach);
			if(nodecision)
				decisionlog.logUpdate(number,netstep,position,orient.getIncomingActivation(),innerstate.getStateInt("nrlastplace"),innerstate.getStateDouble("actlastplace"),urgeinput,getPlaceCellInfo(),colorActs,innerstate.getStateDouble("Reflex"),innerstate.getStateDouble("Rotate"),0);
			else
				decisionlog.logUpdate(number,netstep,position,orient.getIncomingActivation(),innerstate.getStateInt("nrlastplace"),innerstate.getStateDouble("actlastplace"),urgeinput,getPlaceCellInfo(),colorActs,innerstate.getStateDouble("Reflex"),innerstate.getStateDouble("Rotate"),1);
		}
		
		
		
		//set the buffer to zeros, everytime when a new placecell is entered
		if(this_place_num != innerstate.getStateInt("LastCycle"))
			buffer.clean();
	
		// check if we want to link a color sensor to a food node...
		if(this_place_num!=-1){
			linked_color = colorCheck(this_place_num,colorActs,netstep,innerstate.getStateDouble("Orient"));
		}
		
		if(debugmodus){
			decisionlog.setLinkColor((int)((netstep-innerstate.getStateLong("firstnetstep"))%saveEach),linked_color,foodposition);
			}
		
		
		if (linked_color > -1) {
				color_food[linked_color] = 1;  // this sets the 'linked' flag of the color node to 
				foodposition=-1;
		}else{
				/*decisionLog.set(18,(int)number,-1);
				decisionLog.set(19,(int)number,-1);*/
				//Debug
		}
		/*
		 * linked color = -5 thebn the robot shoul stop
		 */
		if(linked_color!=-5){
			//remoreint stores the roeintattion befor it is set to -.5 to stop the robot and make the colorlink
			if(remorient!=-5)
				innerstate.setState("Orient",remorient);
		//only while standing at a placefield a decision can be drawn, so do nothing if:
		//    this place is not a valid place OR i'm in the same place as where i drew the last decision OR the gradient of the place field is positively increasing
			
			if (this_place_num == 0 || this_place_num == innerstate.getStateInt("nrlastplace") || !buffer.difference() || nodecision) {
				// do nothing except to set the value to -1 so that the Allocentric-Egocentric Module do nothing except moving straight
				// note: if our Gaussians were not all equally parametered, then there could be a case where two overlap in such
				// a way that a decrease in the gradient is not found until the agent has entered the second place...
				innerstate.setState("Orient",-1);
				if (reflexinput<0){
					innerstate.setState("Reflex",reflexinput); // Give a discaunting factor onto the connection of the placefields if the robot bumps iinto the wall
				}
				//logger.debug("this place act:  "+this_place_act +" & last: " + innerstate.getStateDouble("actlastplace"));
				
			} 
			else { 			
				logger.debug("------------------------------- decision ----------------------------- " + nodecision );
				if (innerstate.getStateInt("nrlastplace") != -1 && !flooding) {
					//calculate the experience Matrice
					updateMatrices(this_place_num,innerstate.getStateDouble("Reflex"));
	//				logger.debug("Orienation Flooding "+innerstate.getStateDouble("decision")*45);
					if(innerstate.getStateDouble("Reflex")<0)
						logger.debug("RRRRRRRRRRRRRRRRRRRREEEEEEEEEEEEEEEEEEEEEEEEEEFLEX!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				}
				innerstate.setState("Reflex",0);
					
				for (int j=0;j<urge.length;j++){
					if(Math.abs(urgeinput[j]-lasturgeact[j])>threshold && !expmode){
						runflood=true;
						innerstate.setState("flood",1);
					}
	//				logger.debug("input "+(urgeinput[j]));
	//				logger.debug("LastInput "+(lasturgeact[j]));
				}
	//			logger.debug("LastUrgeActivation : "+runflood);
				
				if(runflood){
					logger.debug("Now in the flooding plan following in the calculate method!");
					// follow aim and do the max decision
					if (innerstate.getStateInt("flood") == 1) {
						//Starts the flooding but before tha robot has to be stopped
						if(!stopRobot(netstep)){
							innerstate.setState("Orient",-.5);
							logger.debug("Stop the Robot");
						}else{
							logger.debug("Now in the flooding part of the calculate method !");	
							
							IntArrayList indices = new IntArrayList();
							DoubleArrayList values = new DoubleArrayList();
							
							//memorize the last acivation
							for(int i=0;i<urgeinput.length;i++){
								lasturgeact[i]=urgeinput[i];
							}
							
							
							//Multiply urgeactivity by decay
							for(int i=0;i<positionUrges.length;i++){
								HandleMatrices.activationVector.set(positionUrges[i]-1,urgeinput[i]);
							}
						
			    				DoubleMatrix2D sourcesXdirections = DoubleFactory2D.dense.make(nrplacearray,nrDecision);
			    				DoubleMatrix1D oldActivationVector =  DoubleFactory1D.dense.make(nrplacearray);
			    				DoubleMatrix1D matrixProduct =  DoubleFactory1D.dense.make(nrplacearray);
			    				DoubleMatrix1D decayTerm =  DoubleFactory1D.dense.make(nrplacearray);
			    				DenseDoubleMatrix1D testactivationVector =  new DenseDoubleMatrix1D(nrplacearray);
			    				DenseDoubleMatrix1D rememberPlacefields =  new DenseDoubleMatrix1D(nrplacearray);
			    				testactivationVector.assign(HandleMatrices.activationVector);	
			    				decayTerm.assign(decay);
		    				
		    				
		    				
		      				//Calcualte the TransitionMatrice
		     				updateTransition();
		     				
						
							// DoubleMatrix1D mistake = int x=0;
							//Do only multiplicate the Actiovation so often until a certain precition.	
		     				logger.debug("In the FLOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOD");
		     				while(testactivationVector.assign(oldActivationVector,cern.jet.math.Functions.minus).assign(cern.jet.math.Functions.square).zSum() > 0.000000001){
		    				
								
								//copy values of the activation of the last step.
								oldActivationVector.assign(HandleMatrices.activationVector);
								HandleMatrices.activationVector.assign(decayTerm,cern.jet.math.Functions.mult);
								
								//Multiplication of activation with the Transition Matrice 
								for(int i = 0; i < nrDecision; i++){
									matrixProduct = algebra.mult(HandleMatrices.transition.viewSlice(i),HandleMatrices.activationVector);
									sourcesXdirections.viewColumn(i).assign(matrixProduct);
								}
								//saveProductMatrix(sourcesXdirections,netstep);
								
								//calculate the new activaion multiply the new activation with a decay factor
								for (int i = 0; i < sourcesXdirections.rows(); i++) {
									sourcesXdirections.viewRow(i).getNonZeros(indices,values);
									
									if(values.size()>=1){
										HandleMatrices.activationVector.set(i,Descriptive.max(values));
									}
								}
								
								//Remebers the max Activation of each Source
								rememberPlacefields.assign(HandleMatrices.activationVector);
	//							set the activation at the positions of the urge to the input of this activation
								for(int i=0;i<positionUrges.length;i++){
									HandleMatrices.activationVector.set(positionUrges[i]-1,urgeinput[i]);
								}
								testactivationVector.assign(HandleMatrices.activationVector);						
							} //end while of the Multiplication of the transition matrice by the activation
							
							//saveProductMatrix(sourcesXdirections,netstep,"Source");
//							saveProductVector(activationVector,netstep);
							
		     				if(debugmodus){
								if(!	matrix.save2dMatrix(sourcesXdirections,netstep,"Source",path)){
									logger.error("Source not saved!!!");
								}
								if(!matrix.save1dMatrix(HandleMatrices.activationVector,netstep,"FlActivation",path)){
									logger.error("FlActivation not saved!");
								}
							}
							
							// calculate best decision The decisiona are stored in a totalNrPlacecells times nrDecision matrices, where the sources 
							// The beste deciiosn is stored in the dirst of the columns, In case there ar more than one placecell, the others are stored in the other
							// columns. In the Numberofdecisions are the number stored how many good decision we have for each source.
							for (int i = 0; i < sourcesXdirections.rows(); i++) {
								NumberOfDecisions[i]=indexAtMax(sourcesXdirections.viewRow(i),i,rememberPlacefields.get(i));
							}
							
							if(debugmodus){
								if(!matrix.save2dMatrix(HandleMatrices.decisionMatrix,netstep,"Decision",path)){
									logger.error("Decision not saved!");
								}
							}
							
							
							
							//set the decision and look if there are more than one decision given by indexAtMax
							/*
							 * Rand because of if we have more than one good decision at the placecell
							 */
							decisionnumber=(int)HandleMatrices.decisionMatrix.get(this_place_num-1,(int)Math.ceil(NumberOfDecisions[this_place_num-1]*Math.random())-1);
							innerstate.setState("Orient",decisionnumber*Math.PI/4);
							innerstate.setState("decision",decisionnumber);
							innerstate.setState("nrlastplace", this_place_num);
							// set "flood" flag to false do that the multiplication is not run in the next netstep
							innerstate.setState("flood", 0);
							} // end of else if(stopRobot(netstep)
						
						
					} else { // if (innerstate.getStateInt("flood") != 1)
							
	//						set decision, Orienation 
							decisionnumber = (int)HandleMatrices.decisionMatrix.get(this_place_num-1,(int)Math.ceil(NumberOfDecisions[this_place_num-1]*Math.random())-1);
							innerstate.setState("Orient",decisionnumber*Math.PI/4);
							innerstate.setState("decision",decisionnumber);
							innerstate.setState("nrlastplace", this_place_num);
							logger.debug("Number of Decision"+decisionnumber);	
					}
					
					
			}else { // if(!runflood)
					// move random in the arena
					decisionnumber = (int) Math.ceil(Math.random() * nrDecision);
					sendorient = decisionnumber * Math.PI / 4;
					innerstate.setState("nrlastplace", this_place_num);
					innerstate.setState("Orient", sendorient);
					innerstate.setState("decision", decisionnumber);
					logger.debug("Orienation Flooding "+innerstate.getStateDouble("decision")*45);
					// logger.debug("RANDOM --------- WALK _____ "+decisionnumber+"  ("+Math.toDegrees(sendorient)+")_____"+this_place_num);
					
				}
	
			}// end else make a decision
			
		}else{ // if(linked_color==-5)
			innerstate.setState("Orient",-.5);
		}
			

		if (debugmodus)
			//updateDecisionLog(netstep, decisionnumber);
			decisionlog.logupdateDecision((int)((netstep-innerstate.getStateLong("firstnetstep"))%saveEach),decisionnumber,innerstate.getStateDouble("Orient"));
		
			
		//Remember the last placecell of the cycle to reset the buffer
		innerstate.setState("LastCycle",this_place_num);
		
		
		
		//In case robot is looking for Food
		if(flooding){
		
			if(tonic1==0 && tonic2==0 && tonic4==0 && tonic3==0){//this_place_num==positionUrges[0]){
				//set the speed to zeros if the robot reached the food	
				innerstate.setState("Orient",-.5);
			}
			//Setting of the Gates continously at 1; The Tobic Activation and setting to 0 if the urge is reached
			switch (this_place_num) {
			case RedUrge:tonic1=0;  //red
			break;
			case GreenUrge:tonic2=0; //green
			break;
			case BlueUrge:tonic3=0; //blue
			break;
			case YellUrge:tonic4=0; //yellow
			break;
			}
	
			if((urgeinput[0]-lasturgeact[0])<0)
				tonic1=0;
			if((urgeinput[1]-lasturgeact[1])<0)
				tonic2=0;
			if((urgeinput[2]-lasturgeact[2])<0)
				tonic3=0;
			if((urgeinput[3]-lasturgeact[3])<0)
				tonic4=0;
			
			manipulator.setGateActivation(GT_Tonic_Food1,tonic1);
			manipulator.setGateActivation(GT_Tonic_Food2,tonic2);
			manipulator.setGateActivation(GT_Tonic_Food3,tonic3);
			manipulator.setGateActivation(GT_Tonic_Food4,tonic4);
		}
		
		
		//setting the activation of the gate to the Orient value
		manipulator.setGateActivation(GT_Action_Value,innerstate.getStateDouble("Orient"));
	}
} // END OF CLASS!
