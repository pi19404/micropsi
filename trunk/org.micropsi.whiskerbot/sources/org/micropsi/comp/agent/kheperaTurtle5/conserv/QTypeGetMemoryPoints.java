package org.micropsi.comp.agent.kheperaTurtle5.conserv;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.MemoryHandler;

import org.apache.log4j.Logger;
import org.micropsi.media.VideoServerRegistry;
import org.micropsi.nodenet.Link;
import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.comp.agent.kheperaTurtle5.KheperaWorldAdapter;
import org.micropsi.comp.agent.micropsi.MicroPsiAgent;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.comp.robot.khepera8.KheperaActionExecutor;
import org.micropsi.nodenet.Gate;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NativeModule;
import org.micropsi.nodenet.NetEntity;

public class QTypeGetMemoryPoints implements ConsoleQuestionTypeIF {

	private static final String QNAME = "getmemorypoints";
	
	private MicroPsiAgent micropsi;
	Logger logger;
	private Iterator<NetEntity> net;
	private Iterator<Link> links;
	private NetEntity entity;
	private Gate gate;
	
	
	private static Vector memorizex=new Vector(50,50);
	private static Vector memorizey=new Vector(50,50);
	private static Vector memorizestep=new Vector(50,50);
	
	
	private String id=null;
	private boolean go,followline;
	private double xvalue,yvalue;
	
	
	
	public QTypeGetMemoryPoints(MicroPsiAgent horst, Logger logger) {
		this.micropsi = horst;
		this.logger=logger;
	
	}
	
	public String getQuestionName() {
		return QNAME;
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
	
	
	
	
	
	private void findAllMemorylinks(Iterator<Link> memoryfind){
		
		String memory;
		String[] namememo=new String[3];
		NetEntity nodentity=null;
		int count;
		Gate gatestep=null;
		
		
		
		while(memoryfind.hasNext()){
			
			try{
				nodentity=memoryfind.next().getLinkedEntity();
			}catch (Exception e) {
				   logger.debug("[QTypeGetMemory] Exception at finding Memory nodes : "+e);
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
						
					}
					
					
					try{
						gatestep=micropsi.getNet().getEntity(namememo[2]).getGate(GateTypesIF.GT_GEN);
					}catch (Exception e) {
						logger.debug("[QTypeGetMemory] Exception at the memory search: "+e);
					}
					followline=gatestep.hasLinks();
					if(followline){
						if(gatestep.getLinks().hasNext())
							try{
								nodentity=gatestep.getLinks().next().getLinkedEntity();
							}catch (Exception e) {
								logger.debug("[QTypeGetMemory] Exception at get entity linked: "+e);
							}
						else followline=false;
					}
				}
			}
		}		
}
	
	
	
	public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
		MTreeNode memory = new MTreeNode("Memory","0",null);

		boolean onetime=true;
		double mistake=0;
		
		memorizestep.clear();
		memorizex.clear();
		memorizey.clear();
		
		
		net=micropsi.getNet().getAllEntities();
		go=net.hasNext();
		while(go){
			entity=net.next();
			if(entity.getEntityName().equals("MemoryAdmin")){
				id=entity.getID();
				go =false;
			}else{
				go=net.hasNext();
			}
			
		}
		//Start looking for the x and y positions
		if(id!=null){
			links=entity.getGate(31003).getLinks();
			findAllMemorylinks(links);			
		}
		
		if(memorizestep.size()>0){
			for(int i=0;i<memorizestep.size();i++){
				try{
				gate=micropsi.getNet().getEntity(memorizex.get(i).toString()).getGate(GateTypesIF.GT_GEN);
				}catch (Exception e) {
					logger.debug("[QTypeGetMemory] Exception at getting the x position : "+e);
				}
				
				xvalue=gate.getOutputFunctionParameter("threshold");
				if(onetime){
					mistake=gate.getOutputFunctionParameter("mistake");
					onetime=false;
				}
				
				MTreeNode parent=memory.addChild("step"+i,mistake);
				parent.addChild("xposition"+i,xvalue);
				try{
					gate=micropsi.getNet().getEntity(memorizey.get(i).toString()).getGate(GateTypesIF.GT_GEN);
					}catch (Exception e) {
						logger.debug("[QTypeGetMemory] Exception at getting the y position : "+e);
					}
					
				yvalue=gate.getOutputFunctionParameter("threshold");
				parent.addChild("yposition"+i,yvalue);
			}
			
			
		}
		
		
		
		
			
//		((NativeModule)micropsi.getNet().getEntity("sepp")).getImplementation().
		
		
		return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE,question,memory,step);
	}

}
