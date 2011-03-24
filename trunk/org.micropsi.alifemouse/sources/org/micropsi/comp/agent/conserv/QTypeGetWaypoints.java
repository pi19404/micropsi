/*
 * Created on 09.06.2005
 *
 */
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

/**
 * @author Markus
 *
 */
public class QTypeGetWaypoints implements ConsoleQuestionTypeIF {

    private static final int wayType = 0;
    private static final int locationType = 1;
    
    private static final String QNAME = "getwaypoints";
	private MouseMicroPsiAgent agent;
	private boolean conceptsRegistered = false;
	private ConceptNode foodConcept;
	private ConceptNode waterConcept;
	private ConceptNode healingConcept;
	private ConceptNode obstacleConcept;
	private ConceptNode damageConcept;
	
	public QTypeGetWaypoints(MouseMicroPsiAgent agent) {
		this.agent = agent;
	}
	
    public String getQuestionName() {
        return QNAME;
    }

    public AnswerIF answerQuestion(AnswerFactoryIF factory, QuestionIF question, long step) {
        MTreeNode root = new MTreeNode("rootNode",Long.toString(step),null);
        MTreeNode foodNode = root.addChild("food", "");
        MTreeNode waterNode = root.addChild("water", "");
        MTreeNode healingNode = root.addChild("healing", "");
        MTreeNode obstacleNode = root.addChild("obstacle", "");
        MTreeNode damageNode = root.addChild("damage", "");
        
        if(!conceptsRegistered) {
            try {
                foodConcept = (ConceptNode)agent.getNet().getEntity("15-050920/133607");
                waterConcept = (ConceptNode)agent.getNet().getEntity("22-050920/133607");
                healingConcept = (ConceptNode)agent.getNet().getEntity("3-050920/133607");
                damageConcept = (ConceptNode)agent.getNet().getEntity("11-050920/133607");
                obstacleConcept = (ConceptNode)agent.getNet().getEntity("20-050920/133607");
                conceptsRegistered = true;
            } catch (MicropsiException e) {
                return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE, question, root, step);
            }
        }
        
        int i;
        for(i = 0; i < foodConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
            try {
                foodNode.addChild(getTreeNodeFromNode(foodConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity(), wayType));
            } catch (NetIntegrityException e) {
                e.printStackTrace();
            }
        }
        
        for(i = 0; i < waterConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
            try {
                waterNode.addChild(getTreeNodeFromNode(waterConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity(), wayType));
            } catch (NetIntegrityException e) {
                e.printStackTrace();
            }
        }
        
        for(i = 0; i < healingConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
            try {
                healingNode.addChild(getTreeNodeFromNode(healingConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity(), wayType));
            } catch (NetIntegrityException e) {
                e.printStackTrace();
            }
        }
        
        for(i = 0; i < obstacleConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
            try {
                obstacleNode.addChild(getTreeNodeFromNode(obstacleConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity(), locationType));
            } catch (NetIntegrityException e) {
                e.printStackTrace();
            }
        }
        
        for(i = 0; i < damageConcept.getGate(GateTypesIF.GT_CAT).getNumberOfLinks(); i++) {
            try {
                damageNode.addChild(getTreeNodeFromNode(damageConcept.getLink(GateTypesIF.GT_CAT, i).getLinkedEntity(), locationType));
            } catch (NetIntegrityException e) {
                e.printStackTrace();
            }
        }

        return factory.createAnswer(AnswerTypesIF.ANSWER_TYPE_COMPLEX_MESSAGE, question, root, step);
    }

    private MTreeNode getTreeNodeFromNode(NetEntity position, int type){
        MTreeNode returnNode = new MTreeNode("position", "", null);
        double x = position.getLink(GateTypesIF.GT_SUB, 0).getWeight() * ConstantValues.WORLDMAXX;
        double y = position.getLink(GateTypesIF.GT_SUB, 1).getWeight() * ConstantValues.WORLDMAXY;
        returnNode.addChild("x", Double.toString(x));
        returnNode.addChild("y", Double.toString(y));
        if(type == wayType) {
            if(position.getFirstLinkAt(GateTypesIF.GT_POR) != null) {
                try {
                    NetEntity next = position.getFirstLinkAt(GateTypesIF.GT_POR).getLinkedEntity();
                    double nextX = next.getLink(GateTypesIF.GT_SUB, 0).getWeight() * ConstantValues.WORLDMAXX;
                    double nextY = next.getLink(GateTypesIF.GT_SUB, 1).getWeight() * ConstantValues.WORLDMAXY;
                    MTreeNode pointer = returnNode.addChild("leadsTo","");
                    pointer.addChild("x", Double.toString(nextX));
                    pointer.addChild("y", Double.toString(nextY));
                } catch (NetIntegrityException e) {
                    e.printStackTrace();
                }
            }
            return returnNode;
        } else if(type == locationType) {          
            return returnNode;
        } else
            return null;
    }
}
