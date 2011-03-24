 package org.micropsi.nodenet.modules.khepera;


import java.util.*;

import org.micropsi.nodenet.AbstractNativeModuleImpl;
import org.micropsi.nodenet.Gate;
import org.micropsi.nodenet.GateManipulator;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.Slot;
import org.micropsi.nodenet.agent.TypeStrings;
import org.micropsi.nodenet.agent.TypeStringsExtensionIF;
import org.micropsi.nodenet.SlotTypesIF;
import org.micropsi.nodenet.GateTypesIF;

import com.sun.corba.se.impl.logging.POASystemException;


public class Memory extends AbstractNativeModuleImpl {
	
	//public final static int GT_Motorleft_Value=31000;
	public final static int GT_TriggerSignal_Value=31001;
	public final static int GT_Const1_Value=31002;
	public final static int GT_Memorize_Value=31003;
	public final static int GT_Memoryx_Value=31004;
	public final static int GT_Memoryy_Value=31005;
	
	public final static int ST_Positionx_Value=31006;
	public final static int ST_Positiony_Value=31007;
	public final static int ST_Urge_Value=31008;
	
//	parameters to set
// 	range of the mistake can be made from the tracker system
	private static double mistake=1; 
//  range for findig node 
	private static double range=2;
//	how many chain parts in a node
	private static int sizeofchain=20;
	
	
	private Slot posx,posy,urge;

	private Iterator<NetEntity> allentities;
	private Iterator<Link> memorylink;
	private Gate gatestep;
		
	private static int count=0;
	private int index,moveinMemory;
	private long switchofurge;
		
	private double posxact,posyact;
	private double geturge=0;
	private static double[] getposition=new double[2];
	
	private static String idNativeMod="0"; 	//id of the current Native Module
	private static boolean createNodes=true;		//Wait 2 netstep to get the activations to the xnodes and y nodes
	private boolean findSubPor=true;			//Look for ActPor and ActSub
	private boolean followline=true;		//Follow the line of in the long term Memory stored Nodes to get their ID`s
	private boolean stopaction=false;		
	private static boolean startMovingMemory=false;	//start Mocing Commands from the memory
	boolean firsttime=true;
	private static boolean tryonlyone=false; 
	
	
	
	private String[] lastnodes=new String [3];
	
	private String getadress;
	
	private static String idActPor=null;
	private static String idActSub=null;
	
	private static Vector idxnodes = new Vector(sizeofchain);
	private static Vector idynodes = new Vector(sizeofchain);
	private static Vector idstepnodes = new Vector(sizeofchain);
	private static Vector memorizex=new Vector(sizeofchain,sizeofchain);
	private static Vector memorizey=new Vector(sizeofchain,sizeofchain);
	private static Vector memorizestep=new Vector(sizeofchain,sizeofchain);
	
	
	
	private final int[] gateTypes = {
		//	GT_Motorleft_Value,
			GT_TriggerSignal_Value,
			GT_Const1_Value,
			GT_Memorize_Value,
			GT_Memoryx_Value,
			GT_Memoryy_Value
		};
	
	private final int[] slotTypes = {
			ST_Positionx_Value,
			ST_Positiony_Value,
			ST_Urge_Value
			
	};	
		
	

	@Override
	protected int[] getGateTypes() {
		// TODO Auto-generated method stub
		return gateTypes;
	}

	@Override
	protected int[] getSlotTypes() {
		// TODO Auto-generated method stub
		return slotTypes;
	}

	
	
	private void catchSlots(Slot[] slots) {
		for (int i = 0; i < slots.length; i++) {
			switch (slots[i].getType()) {
				case ST_Positionx_Value :
					posx = slots[i];
					break;
				case ST_Positiony_Value :
					posy = slots[i];
					break;
				case ST_Urge_Value :
					urge = slots[i];
					break;
					
			}
		}
	}

	
	public void initialize(){
		TypeStrings.activateExtension(new TypeStringsExtensionIF() {

			public String getExtensionID() {
				return "Memory";
			}

			public String gateType(int type) {
				switch(type) {
					//case GT_Motorleft_Value: return "Motorleft";
					case GT_TriggerSignal_Value: return "Triggersigtnal";
					case GT_Const1_Value: return "STM";
					case GT_Memorize_Value: return "LTM";
					case GT_Memoryy_Value: return "Memory y";
					case GT_Memoryx_Value: return "Memory x"; 
				}
				return null;
			}

			public String slotType(int type) {
				switch(type) {
				case ST_Positionx_Value: return "X-Position";
				case ST_Positiony_Value: return "Y-Position";
				case ST_Urge_Value: return "Urge saturated";
					}
				return null;
			}
			
		});
		firsttime = false;
	}
	
	
	
	private void resetToZero(Vector x,Vector y){
		
		for(int i=0;i<x.size();i++){
			try{
				structure.getGateManipulator(x.get(i).toString()).setGateActivation(GateTypesIF.GT_GEN,0);
				structure.getGateManipulator(y.get(i).toString()).setGateActivation(GateTypesIF.GT_GEN,0);
			}catch (Exception e) {
				logger.debug("Error at resetToZero : "+e);
			}
			
		}
		
		
		
	}
	
	
	
	
	
	private void createNewNodes(){
		if(idstepnodes.size()<sizeofchain){       
			if(idstepnodes.size()==0){
				
				try{
					lastnodes=memorize(posxact,posyact,mistake,idNativeMod,true);//get Connection to the native module
				}catch (Exception e) {
					logger.debug("Exception while creating connection to native module : "+e);
				}
				
			}
			else{
				lastnodes=memorize(posxact,posyact,mistake,(String)idstepnodes.get(idstepnodes.size()-1),false);//get Connection to the NOdes for the first sizeofchain steps
				}
		}else{
			try{
				//create the Connecttion tio the id Native module if the remove of nodes hat to be started
				structure.createLink(idNativeMod,GT_Const1_Value,idstepnodes.get(1).toString(),SlotTypesIF.ST_GEN,1.0,1.0);
			}catch (Exception e) {
				logger.debug("Error at building Connection from Native Module to next link :"+e);
			}
		}
		
		
		
		//Remove the old nodes which are longer than the sizeofchange	
		if(idstepnodes.size()==sizeofchain){
			remove((String)idxnodes.get(0));
			remove((String)idynodes.get(0));
			remove((String)idstepnodes.get(0));
			idxnodes.remove(0);
			idynodes.remove(0);
			idstepnodes.removeElementAt(0);
			lastnodes=memorize(posxact,posyact,mistake,(String)idstepnodes.get(sizeofchain-2),false);
		}
		

		//save created iod in the vector idstepnodes etc.
		
		idxnodes.add(idstepnodes.size(),lastnodes[0]);
		idynodes.add(idstepnodes.size(),lastnodes[1]);
		idstepnodes.add(idstepnodes.size(),lastnodes[2]);
	}
	
	
	
	
	
	
	private void remove(String idremove){
		
		try{
			structure.deleteEntity(idremove);
		}catch (Exception e) {
			logger.debug("Error while remove "+idremove+" :"+e);
		}
	}
	
	
	
	private void findAllMemory(Iterator<Link> memoryfind){
		
		String memory;
		String[] namememo=new String[3];
		NetEntity nodentity=null;
		
		
		while(memoryfind.hasNext()){
			
			try{
				nodentity=memoryfind.next().getLinkedEntity();
			}catch (Exception e) {
				   logger.debug("Exception at finding Memory nodes : "+e);
			}
			//logger.debug("Nodeentitiy in Memory : "+nodentity.getID());
			followline=true;
			while(followline){
				memory=nodentity.getEntityName();
				if(memory.equals("step")){
					followline=!(memorizestep.contains(nodentity.getID()));
					namememo=findStepand(nodentity);
					if(followline){
						count=memorizex.size();
						memorizex.add(count,namememo[0]);
						memorizey.add(count,namememo[1]);
						memorizestep.add(count,namememo[2]);
						logger.debug("NemeMemo : "+namememo[2]);
					}
					
					try{
						gatestep=structure.getGateManipulator(namememo[2]).getGate(GateTypesIF.GT_GEN);
					}catch (Exception e) {
						logger.debug("Exception at the memory search: "+e);
					}
					followline=gatestep.hasLinks();
					if(followline){
						if(gatestep.getLinks().hasNext())
							try{
								nodentity=gatestep.getLinks().next().getLinkedEntity();
							}catch (Exception e) {
								logger.debug("Exception at get entity linked: "+e);
							}
						else followline=false;
					}
				}
			}
		}
}
		
		
	private String getNextAdress(String adress){
		
		String nextAdress=null;
		Iterator<Link> statenow=null;
		
		//getlinked entity
		try{
			statenow=structure.getGateManipulator(adress).getGate(GateTypesIF.GT_GEN).getLinks();
		}catch (Exception e) {
			logger.debug("Did not find next Node to be run at : "+e);
		}
		
		
		//get the adress of the linked entitiy
		while(statenow.hasNext()){
			nextAdress=statenow.next().getLinkedEntityID();
		}
	
		return nextAdress;
		
	}
	
	
	private double[] getXandY(String getAdress){
		
		double[] backg=new double[2];
		double xlocation=0;
		double ylocation=0;
		int number=0;
		
		
		if(getAdress!=null){
			number=memorizestep.indexOf(getAdress);
			try{
				xlocation=structure.getGateManipulator((String)memorizex.get(number)).getGate(GateTypesIF.GT_GEN).getOutputFunctionParameter("threshold");
				ylocation=structure.getGateManipulator((String)memorizey.get(number)).getGate(GateTypesIF.GT_GEN).getOutputFunctionParameter("threshold");
			} catch (Exception e) {
				logger.debug("getting the Gates to extract the position x and y failed : "+e);
			}
		}
		
		backg[0]=xlocation;
		backg[1]=ylocation;
		return backg;
		
	}
	
	
	
	
	
	
	
	private int testmemory(GateManipulator nativemod){
		Iterator<Link> linknative;
		NetEntity stepentity,next;
		Link link;
		int k=0;
		linknative=nativemod.getGate(GT_Const1_Value).getLinks();
		while(linknative.hasNext()){
			idstepnodes.add(k,linknative.next().getLinkedEntityID());
			stepentity=structure.findEntity(idstepnodes.get(k).toString());
			
			try{				
				next=stepentity.getFirstLinkAt(GateTypesIF.GT_SUB).getLinkedEntity();
				idxnodes.add(k,next.getID());
			}catch (Exception e) {
				logger.debug("Error while searching for the first Nodes in short term memory : "+e);
			}
			
			try{				
				next=stepentity.getFirstLinkAt(GateTypesIF.GT_POR).getLinkedEntity();
				idynodes.add(k,next.getID());
			}catch (Exception e) {
				logger.debug("Error while searching for the first Nodes in short term memory : "+e);
			}
			linknative=stepentity.getGate(GateTypesIF.GT_GEN).getLinks();
			k+=1;		
		}
		return k;
		
	}
	
	
	
	
	
	
	
	
	
	private String[] findStepand(NetEntity step){
		
		String[] adresses =new String[3];
		String name;
		Link link;
		NetEntity next;
		
		
		name=step.getEntityName();
		if(name.equals("step"))
			adresses[2]=step.getID();
		else
			logger.debug("No Step is found");
		link=step.getFirstLinkAt(GateTypesIF.GT_SUB);
		try{
			next=link.getLinkedEntity();
			adresses[0]=next.getID();
		}catch (Exception e) {
			logger.debug("No connection found ");
		}
		link=step.getFirstLinkAt(GateTypesIF.GT_POR);
		try{
			next=link.getLinkedEntity();
			adresses[1]=next.getID();
		}catch (Exception e) {
			logger.debug("No connection found ");
		}
			
		return adresses;
	}
	
	
	
	
	
	
	
	
   private int findBothActiveEntity(Vector lookforx,Vector lookfory){
	   
	   	   
	   int active= -1;
	   Gate gate;
	   boolean act=false;
	   
	   logger.debug("Size of Memory "+lookforx.size());
	   
	   
	   if(!act && !lookforx.isEmpty()){
		   for (int i=0;i<lookforx.size();i++){
			    gate=structure.findEntity((String)lookforx.get(i)).getGate(GateTypesIF.GT_GEN);
				   if(gate.isActive()){
					   gate=structure.findEntity((String)lookfory.get(i)).getGate(GateTypesIF.GT_GEN);
					   if(gate.isActive()){
						   act=true;
						   active=i;
					   }
				   }
			 }
	   }
	   
	   
	   
	   return active;
   }

   private String findEntity(int searchfor){
	  
	   String ID=null;
	   String testid=null;
	   NetEntity netentity;
	   Node depp;
	   
	   logger.debug("searchfor : "+searchfor);
	   	try{
		   allentities=structure.getSpace().getAllLevelOneEntities();
	   	}catch (Exception e) {
		   logger.debug("Error by finding : "+e);
	   	}
	   
	   	while(allentities.hasNext()){
	   		testid=allentities.next().getID();
	   		netentity=structure.findEntity(testid);
	   		if(netentity.getEntityType()==0){
	   			depp=(Node)netentity;
	   			if(netentity.getEntityType()==searchfor)
					ID=testid;
	   		}else{
			if(netentity.getEntityType()==searchfor)
				ID=testid;
	   			}
	   		}
	   	return ID;
       }	
	
	private String[] memorize(double posxa,double posya,double cutarea,String fromold,boolean nat){
		
		GateManipulator gateman=null;
		String [] newnodes =new String[3];
		String idconceptx="0";
		String idconcepty="0";
		String idconceptstep="0";
		
						
		   try{
			   idconceptx=structure.createConceptNode("x "+(int)posxa);
		   }catch (Exception e) {
			   logger.debug("Error in creating x Node :"+e);
		   }
			
			try{
				idconcepty=structure.createConceptNode("y "+(int)posya);
			}catch (Exception e) {
				logger.debug("Error in creating y Node :"+e);
			}
			
			try{
				idconceptstep=structure.createConceptNode("step");
			}catch (Exception e) {
				logger.debug("Error in creating step Node :"+e);
			}
			
			//set maximum values of the step
			
			
			try{
				structure.getGateManipulator(idconceptstep).setGateMaximum(GateTypesIF.GT_SUB,200);
				structure.getGateManipulator(idconceptstep).setGateMaximum(GateTypesIF.GT_POR,200);				
			}catch (Exception e) {
				    logger.debug("Exception while setting the maximum value : "+e);
			}
			
			
			
			//String idconceptstepy=structure.createConceptNode("step");
			try{
				structure.createLink(idconceptstep,GateTypesIF.GT_SUB,idconceptx,SlotTypesIF.ST_GEN,1.0,1.0);
			
			}catch (Exception e) {
				logger.debug("Error in Creating link from step to x: "+e);
			}
			try{
				structure.createLink(idconceptstep,GateTypesIF.GT_POR,idconcepty,SlotTypesIF.ST_GEN,1.0,1.0);
			}catch (Exception e) {
				logger.debug("Error in Creating Link from step to y: "+e);
			}
			
			if(nat){
				try{
					structure.createLink(fromold,GT_Const1_Value,idconceptstep,SlotTypesIF.ST_GEN,1,1.0);
				}catch (Exception e) {
					logger.debug("Error in Creating link Native Module to step Node: "+e);
				}
			}
			else{
				try{
					structure.createLink(fromold,GateTypesIF.GT_GEN,idconceptstep,SlotTypesIF.ST_GEN,1,1.0);
				}catch (Exception e) {
				    logger.debug("Error in Creating link from step before to step now Node: "+e);
				}
			}
			
			//structure.createLink(fromoldy,GateTypesIF.GT_GEN,idconceptstepy,SlotTypesIF.ST_GEN,(1.0/posx),1.0);
			
			newnodes[0]=idconceptx;
			newnodes[1]=idconcepty;
			newnodes[2]=idconceptstep;
			
			
			//setting Parameters
			try{
				gateman=structure.getGateManipulator(idconceptx);
				gateman.setGateMaximum(GateTypesIF.GT_GEN,200);
				gateman.setOutputFunction(GateTypesIF.GT_GEN,"org.micropsi.comp.agent.kheperaTurtle5.OutputFunctions.ValuesInRange");
			}catch (Exception e) {
				logger.debug("Error while setting outputfunction of x Node: "+e);
			}	
			try{
				gateman.setOutputFunctionParameter(GateTypesIF.GT_GEN,"threshold",posxa);
				gateman.setOutputFunctionParameter(GateTypesIF.GT_GEN,"mistake",cutarea);
			}catch (Exception e) {
				logger.debug("Error in Setting Parameters of function :"+e);
			}		
			
			try{
				gateman=structure.getGateManipulator(idconcepty);
				gateman.setGateMaximum(GateTypesIF.GT_GEN,200);
				gateman.setOutputFunction(GateTypesIF.GT_GEN,"org.micropsi.comp.agent.kheperaTurtle5.OutputFunctions.ValuesInRange");
			}catch (Exception e) {
				logger.debug("Error while setting outputfunction of Node y: "+e);
			}	
			
			try{
				gateman.setOutputFunctionParameter(GateTypesIF.GT_GEN,"threshold",posya);
				gateman.setOutputFunctionParameter(GateTypesIF.GT_GEN,"mistake",cutarea);
			}catch (Exception e) {
				logger.debug("Error in Setting Parameters of function :"+e);
			}		
			
			
		
			return newnodes;
				
	}
	
	
	
	public void calculate(Slot[] Slots, GateManipulator manipulator, long netstep)
			throws NetIntegrityException {
		
		
		innerstate.ensureStateExistence("netstep","0");
		innerstate.ensureStateExistence("lastpositionx","0");
		innerstate.ensureStateExistence("lastpositiony","0");		
		
		if(firsttime){
			initialize();
			catchSlots(Slots);
			innerstate.setState("position",testmemory(manipulator));
			innerstate.setState("lastpositionx",0.0);
			innerstate.setState("lastpositiony",0.0);
			innerstate.setState("test",0);
			findSubPor=true;
			switchofurge=netstep;
			}
		
		posxact=posx.getIncomingActivation();
		posyact=posy.getIncomingActivation();
		
	
		
		
		// get incomming activity
		
		
		
		
//		find actpor and actsub + look if there is any memory already stored and read it in the storage;
		idNativeMod=manipulator.getGate(GT_Const1_Value).getNetEntity().getID();
			
		if (findSubPor){
			logger.debug("Find ActPor and ActSub");
			idActPor=structure.getSpace().getActPor().getID();
			idActSub=structure.getSpace().getActSub().getID();
			structure.getGateManipulator(idActPor).setGateMaximum(GateTypesIF.GT_GEN,200);
			structure.getGateManipulator(idActSub).setGateMaximum(GateTypesIF.GT_GEN,200);
			memorylink=manipulator.getGate(GT_Memorize_Value).getLinks();
			findAllMemory(memorylink);
			findSubPor=false;
		}
// 		End of find	Nodes////////
		
		
		if(netstep<switchofurge+4){
			geturge=0;
			posxact=0;
			posyact=0;
			if(memorizestep.size()>0)
				resetToZero(memorizex,memorizey);
		}
		else geturge=urge.getIncomingActivation();
		logger.debug("Get incomming Urge: "+ geturge);
		
		
		
		
		
		
//Activate the Nodes to get the position
		manipulator.setGateActivation(GT_Const1_Value,1.0);
		manipulator.setGateActivation(GT_Memorize_Value,1.0);
		manipulator.setGateActivation(GT_TriggerSignal_Value,0.0);
		if(idActPor!=null && idActSub!=null){
			structure.activateEntity(idActPor,GateTypesIF.GT_GEN,posyact);
			structure.activateEntity(idActSub,GateTypesIF.GT_GEN,posxact);
		}	
//End of Activate Nodes		
				
		
//stops if urge is sufficient and connect to the Long Term Memory if Urge is sufficeint
		if(geturge>=2 && !startMovingMemory && !tryonlyone){
			createNewNodes();
			manipulator.setGateActivation(GT_TriggerSignal_Value,1);
			manipulator.unlinkGate(GT_Const1_Value);
			structure.createLink(idNativeMod,GT_Memorize_Value,(String)idstepnodes.get(0),GateTypesIF.GT_GEN,1.0,1.0);
			stopaction=true;
			tryonlyone=true;
			//Move STM in LTM Memory
			moveinMemory=memorizestep.size();
			for(int k=moveinMemory;k<(moveinMemory+idstepnodes.size());k++){
				memorizestep.add(k,(String)idstepnodes.get(k-moveinMemory));
				memorizex.add(k,(String)idxnodes.get(k-moveinMemory));
				memorizey.add(k,(String)idynodes.get(k-moveinMemory));
			}
			idstepnodes.clear();
			idxnodes.clear();
			idynodes.clear();
		}
// End if Move into LTM and reset the STM;
		
		logger.debug("size of LTM : "+memorizestep.size());
		
		//stops if earch is reached or finding a memory node all other processes is stoped
		if(tryonlyone||startMovingMemory)
			manipulator.setGateActivation(GT_TriggerSignal_Value,1);
		
		
//look for activated nodes and connect the Memory in STM to the LTM chain	
		if(memorizestep.size()>0 && !startMovingMemory&&!stopaction&&netstep>switchofurge+4){
			index=findBothActiveEntity(memorizex,memorizey);
			if(index>-1){				
				logger.debug("index **********************************************************************: "+index);
				//Unlink the STM and Connect to Memorize
				try{
					manipulator.unlinkGate(GT_Const1_Value);
					structure.createLink((String)idstepnodes.get(idstepnodes.size()-1),GateTypesIF.GT_GEN,(String)memorizestep.get(index),GateTypesIF.GT_GEN,1.0,1.0);
					structure.createLink(idNativeMod,GT_Memorize_Value,(String)idstepnodes.get(0),GateTypesIF.GT_GEN,1.0,1.0);
				}catch (Exception e) {
					logger.debug("Exception at creating Link to memory : "+e);
				}
				
				startMovingMemory=true;
				moveinMemory=memorizestep.size();
				getadress=(String)memorizestep.get(index);
				//Add STM to LTM
				for(int k=moveinMemory;k<(moveinMemory+idstepnodes.size());k++){
					memorizestep.add(k,(String)idstepnodes.get(k-moveinMemory));
					memorizex.add(k,(String)idxnodes.get(k-moveinMemory));
					memorizey.add(k,(String)idynodes.get(k-moveinMemory));
				}
				//Reset STM
				idstepnodes.clear();
				idxnodes.clear();
				idynodes.clear();
			}
		}
		
		
		
		
		
//TriggerSignal to stop all other guiding Processes of the robot
		if(stopaction||startMovingMemory)
			manipulator.setGateActivation(GT_TriggerSignal_Value,1.0);
				
			
//If Movement is governed by the M;emory than look for the next nodes 
		if(!stopaction&&startMovingMemory){
			getposition=getXandY(getadress);
			if(Math.abs(posxact-getposition[0])<range && Math.abs(posyact-getposition[1])<range){
				getadress=getNextAdress(getadress);
				//logger.debug("GetAdress : "+getadress);
				if(getadress!=null)
					getposition=getXandY(getadress);
				else stopaction=true;
			}
			manipulator.setGateActivation(GT_Memoryx_Value,getposition[0]);
			manipulator.setGateActivation(GT_Memoryy_Value,getposition[1]);
		    logger.debug("getnextentity :"+getadress+" "+getposition[0]+" "+getposition[1]);
		}else{
			manipulator.setGateActivation(GT_Memoryx_Value,-1);
			manipulator.setGateActivation(GT_Memoryy_Value,-1);
			}
			
			
		
		
				
		// wait two netsteps so that the activity of the Sub and Por is propagated to the x and y nodes of the Meory
		
		if((memorizestep.size()>0) && !startMovingMemory){
			if (createNodes ){
				innerstate.setState("netstep",netstep);
				createNodes=false;
			}else
				if(innerstate.getStateLong("netstep")+2<netstep && !stopaction)
					createNodes=true;
		}else{
			if (startMovingMemory || stopaction) {
				createNodes=false;	
			}else
				createNodes=true;
		}
		
		
		logger.debug("Size of Memory STM "+idstepnodes.size());
		
		
		
		//Create Nodes for the Short Term Memory
				
		if(createNodes){
			
			
			if((Math.abs(posxact-Math.round(posxact))<0.2 || Math.abs(posyact-Math.round(posyact))<0.2) && ((innerstate.getStateDouble("lastpositionx")!=(posxact-posxact%1)) || (innerstate.getStateDouble("lastpositiony")!=(posyact-posyact%1)))){ //create Node only if you are in a certain range of a integer
				createNewNodes();
				}//End of if for create new Nodes and store the position
			
			innerstate.setState("lastpositionx",posxact-(posxact%1));
			innerstate.setState("lastpositiony",posyact-(posyact%1));
			
		}//End of creation of node in a certain range
		
		
	
	}//End of Calculate	
	
}//End of Native module
