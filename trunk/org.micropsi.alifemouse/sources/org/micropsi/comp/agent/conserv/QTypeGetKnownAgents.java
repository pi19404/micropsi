package org.micropsi.comp.agent.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.Functions;
import org.micropsi.comp.agent.MouseMicroPsiAgent;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.nodenet.ConceptNode;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;

public class QTypeGetKnownAgents implements ConsoleQuestionTypeIF {
    
    private static final String QNAME = "getknownagents";
	private MouseMicroPsiAgent agent;
	private boolean conceptsRegistered = false;
	private ConceptNode agentsConcept;
	
	public QTypeGetKnownAgents(MouseMicroPsiAgent agent) {
		this.agent = agent;
	}
	
    public String getQuestionName() {
        return QNAME;
    }

    public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
        MTreeNode rootNode = new MTreeNode("rootNode",Long.toString(step),null);
        
        if(!conceptsRegistered) {
            try {
                agentsConcept = (ConceptNode)agent.getNet().getEntity("4-050920/133607");
            } catch (MicropsiException e) {
                return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE, question, rootNode, step);
            }
        }
        
        int i;
        for(i = 0; i < agentsConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
            try {
                rootNode.addChild(getTreeNodeFromNode(agentsConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity()));
            } catch (NetIntegrityException e) {
                e.printStackTrace();
            }
        }
        
        return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE, question, rootNode, step);
    }

    private MTreeNode getTreeNodeFromNode(NetEntity agent){
        MTreeNode returnNode = new MTreeNode("agent", "", null);
        
        if(agent.getGate(GateTypesIF.GT_SUB).getNumberOfLinks() != 4)
        	return null;
        int[] RGB = new int[3];
        RGB[0] = (int)(agent.getLink(GateTypesIF.GT_SUB, 0).getWeight() * 255);
        RGB[1] = (int)(agent.getLink(GateTypesIF.GT_SUB, 1).getWeight() * 255);
        RGB[2] = (int)(agent.getLink(GateTypesIF.GT_SUB, 2).getWeight() * 255);
        
        long ID = Functions.getID(RGB);
        returnNode.addChild("ID", Long.toString(ID));
        
        double experience = agent.getLink(GateTypesIF.GT_SUB, 3).getWeight();
        returnNode.addChild("experience", Double.toString(experience));

        return returnNode;
    }
}

