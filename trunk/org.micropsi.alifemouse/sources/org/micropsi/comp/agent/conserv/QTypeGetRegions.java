package org.micropsi.comp.agent.conserv;

import org.micropsi.common.consoleservice.AnswerFactoryIF;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.ConsoleQuestionTypeIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.ConstantValues;
import org.micropsi.comp.agent.MouseMicroPsiAgent;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.nodenet.ConceptNode;
import org.micropsi.nodenet.GateTypesIF;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetIntegrityException;

public class QTypeGetRegions implements ConsoleQuestionTypeIF {
    private static final String QNAME = "getregions";
	private MouseMicroPsiAgent agent;
	private boolean conceptsRegistered = false;
	private ConceptNode rootConcept;
	
	public QTypeGetRegions(MouseMicroPsiAgent agent) {
		this.agent = agent;
	}
	
    public String getQuestionName() {
        return QNAME;
    }

    public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
        MTreeNode root = new MTreeNode("rootNode",Long.toString(step),null);
        
        if(!conceptsRegistered) {
            try {
                rootConcept = (ConceptNode)agent.getNet().getEntity("1-051015/155837");
                //rootConcept = (ConceptNode)agent.getNet().getEntity("2-060309/140813");
                conceptsRegistered = true;
            } catch (MicropsiException e) {
                return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE, question, root, step);
            }
        }
        
        int i;
        for(i = 0; i < rootConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
            try {
                root.addChild(getTreeNodeFromNode(rootConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity()));
            } catch (NetIntegrityException e) {
                e.printStackTrace();
            }
        }

        return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE, question, root, step);
    }

    private MTreeNode getTreeNodeFromNode(NetEntity node){
        MTreeNode returnNode = new MTreeNode("region", "", null);
        try {
	        double x = node.getLink(GateTypesIF.GT_SUB, 0).getWeight() * ConstantValues.WORLDMAXX;
	        double y = node.getLink(GateTypesIF.GT_SUB, 1).getWeight() * ConstantValues.WORLDMAXY;
	        boolean explored = node.getLink(GateTypesIF.GT_SUB, 2).getWeight() > 0.5 ? true : false;
	        
	        returnNode.addChild("x", Double.toString(x));
	        returnNode.addChild("y", Double.toString(y));
	        returnNode.addChild("explored", Boolean.toString(explored));
	        
	        if(node.getGate(GateTypesIF.GT_SUB).getNumberOfLinks() >= 8) {
		        boolean food = node.getLink(GateTypesIF.GT_SUB, 3).getWeight() > 0.5 ? true : false;
		        boolean water = node.getLink(GateTypesIF.GT_SUB, 4).getWeight() > 0.5 ? true : false;
		        boolean healing = node.getLink(GateTypesIF.GT_SUB, 5).getWeight() > 0.5 ? true : false;
		        boolean damage = node.getLink(GateTypesIF.GT_SUB, 6).getWeight() > 0.5 ? true : false;
		        boolean impassable = node.getLink(GateTypesIF.GT_SUB, 7).getWeight() > 0.5 ? true : false;
		        
		        returnNode.addChild("food", Boolean.toString(food));
		        returnNode.addChild("water", Boolean.toString(water));
		        returnNode.addChild("healing", Boolean.toString(healing));
		        returnNode.addChild("damage", Boolean.toString(damage));
		        returnNode.addChild("impassable", Boolean.toString(impassable));
	        }
        } catch(Exception e) {
        	e.printStackTrace();
        }
        
        return returnNode;
    }
}



