/**
 * $Header:
 * /var/cvs/org.micropsi.core/sources/org/micropsi/comp/messages/MQuestion.java,v
 * 1.1 2004/08/06 13:51:41 cvsuser Exp $
 */
package org.micropsi.comp.messages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.micropsi.common.consoleservice.QuestionIF;

public class MQuestion implements MessageIF, QuestionIF {

	private ArrayList<String> parameters;
	private Object additionalData;
	private String questionname;
	private String destination;
	protected String origin;
	private int answerMode;
	protected long step = 0;

	public MQuestion() {
		setAnswerMode(QuestionIF.AM_ANSWER_ONCE);
		parameters = new ArrayList<String>(5);
	}

	public Object clone() {
		MQuestion q = new MQuestion(this.questionname, this.answerMode);
		q.setAdditionalData(this.additionalData);
		q.setParameters(this.getParameters());
		q.setDestination(this.destination);
		q.setOrigin(this.origin);
		q.setStep(this.step);
		return q;
	}

	protected MQuestion(MQuestion q) {
		this.questionname = q.getQuestionName();
		this.answerMode = q.getAnswerMode();
		this.additionalData = q.getAdditionalData();
		this.parameters = new ArrayList<String>(q.getParameterList());
		this.destination = q.getDestination();
		this.origin = q.getOrigin();
		this.step = q.getStep();
	}

	public MQuestion(String name, int answerMode) {
		parameters = new ArrayList<String>(5);
		this.questionname = name;
		this.answerMode = answerMode;
	}

	public String getQuestionName() {
		return questionname;
	}

	public void setQuestionName(String questionname) {
		this.questionname = questionname;
	}

	public void addParameter(String nextParameter) {
		if (nextParameter == null)
			return;
		parameters.add(nextParameter);
	}

	protected List<String> getParameterList() {
		return parameters;
	}

	public int getMessageType() {
		return MessageTypesIF.MTYPE_CONSOLE_QUESTION;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String[] getParameters() {
		String[] toReturn = new String[parameters.size()];
		for (int i = 0; i < parameters.size(); i++)
			toReturn[i] = parameters.get(i);
		return toReturn;
	}

	public void setParameters(String[] parameters) {
		this.parameters.clear();
		if (parameters != null) {
			for (int i = 0; i < parameters.length; i++) {
				if (parameters[i] != null)
					this.parameters.add(parameters[i]);
			}
		}
	}

	public int getAnswerMode() {
		return answerMode;
	}

	public void setAnswerMode(int answerMode) {
		this.answerMode = answerMode;
	}

	public long getStep() {
		return step;
	}

	public void setStep(long step) {
		this.step = step;
	}

	public Object getAdditionalData() {
		return additionalData;
	}

	public MTreeNode getAdditionalDataAsTree() {
		return (MTreeNode) additionalData;
	}

	public void setAdditionalData(Object additionalData) {
		this.additionalData = additionalData;
	}

	public String retrieveParameter(int at) {
		if (at >= parameters.size())
			return null;
		return parameters.get(at);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s;
		s = super.toString() + " question: " + questionname + " parameters: ";
		Iterator i = parameters.iterator();
		while (i.hasNext())
			s += (String) i.next();
		s += " origin: " + origin + " destination: " + destination
				+ " answermode: " + answerMode + " step: " + step
				+ (additionalData == null ? "" : additionalData.toString());
		return s;
	}

	private boolean equalParameters(MQuestion q) {
		boolean ret = true;

		String[] param = q.getParameters();
		int l = parameters.size();

		if (param.length == l) {
			for (int i = 0; (i < l) && ret; i++) {
				ret &= (param[i].equals(this.parameters.get(i)));
			}
		} else
			ret = false;

		return ret;
	}

	private boolean eq(Object o1, Object o2) {
		return (o1 == null && o2 == null) || o1.equals(o2);
	}

	private int key(Object o) {
		return (o == null ? 0 : o.hashCode());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {

		boolean ret = false;

		if (obj instanceof MQuestion) {
			MQuestion q = (MQuestion) obj;
			ret = eq(q.getQuestionName(), this.getQuestionName())
					&& this.equalParameters(q)
					&& eq(q.getOrigin(), this.origin)
					&& eq(q.getDestination(), this.destination)
					&& q.getAnswerMode() == this.answerMode;

			//@todo This is not really smart -- we have to check the type,
			// as MTreeNode.equals() is unusable here, it uses Object.equals()
			if (this.additionalData != null) {
				if (q.additionalData == null) {
					ret = false;
				} else {
					//check type:
					if ((this.additionalData instanceof MTreeNode)
							&& (q.getAdditionalData() instanceof MTreeNode)) {
						//both MTreeNodes --> compare content
						ret &= ((MTreeNode) this.additionalData)
								.contentEquals(q.getAdditionalData());
					} else if ((this.additionalData instanceof MTreeNode)
							&& (q.getAdditionalData() instanceof MTreeNode)) {
						//one is MTreeNode, one not --> return false
						ret = false;
					} else {
						//both are not MTreeNodes --> return Object.equals()
						ret &= this.additionalData
								.equals(q.getAdditionalData());
					}
				}
			} else {
				//this.additionalData == null
				ret &= (q.additionalData == null);
			}
		} else {
			//obj is no MQuestion
			ret = false;
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int addHash = 0;

		if (additionalData != null)
			addHash = (additionalData instanceof MTreeNode) ? ((MTreeNode) additionalData)
					.getHashKey()
					: additionalData.hashCode();

		int key = addHash + answerMode + key(destination) + key(origin)
				+ key(questionname);
		int l = parameters.size();
		for (int i = 0; i < l; i++) {
			key += key(parameters.get(i));
		}
		return key;
	}

	public String getStorageKey() {
		String key = additionalData + "," + destination + "," + origin + ","
				+ questionname;
		int l = parameters.size();
		for (int i = 0; i < l; i++) {
			key += "," + parameters.get(i);
		}
		return key;
	}

}