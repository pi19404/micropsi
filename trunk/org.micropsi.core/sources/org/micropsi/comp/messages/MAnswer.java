/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MAnswer.java,v 1.2 2004/08/10 14:38:16 fuessel Exp $
 */
package org.micropsi.comp.messages;

import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.QuestionIF;

public class MAnswer implements MessageIF,AnswerIF {
	
	private Object content;
	private int answerType;
	private String destination;
	private String origin;
	private MQuestion answeredQuestion;
	private long step;
//	public TreeNode node;	
		
	public MAnswer() {
	}
	
	public MAnswer(int type, MQuestion answQuestion, Object content, long step) {
		this.answerType = type;
		this.origin = answQuestion.getDestination();
		this.destination = answQuestion.getOrigin();
		this.content = content;
		this.step = step;
		this.answeredQuestion = answQuestion;
	}
		
	public int getMessageType() {
		return MessageTypesIF.MTYPE_CONSOLE_ANSWER;
	}

	public int getAnswerType() {
		return answerType;
	}

	public void setAnswerType(int answerType) {
		this.answerType = answerType;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public String getOrigin() {
		return origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public QuestionIF getAnsweredQuestion() {
		return answeredQuestion;
	}

	public void setAnsweredQuestion(MQuestion answeredquestion) {
		this.answeredQuestion = answeredquestion;
	}

	public long getStep() {
		return step;
	}

	public void setStep(long step) {
		this.step = step;
	}
	
/*	public MTreeNode getTreeNode() {
		return null;
	}*/
	
	public String toString() {
		if(answeredQuestion == null) return super.toString();
		return "answer to: "+answeredQuestion.getQuestionName()+" "+origin+" -> "+destination+" "+super.toString();
	}

}
