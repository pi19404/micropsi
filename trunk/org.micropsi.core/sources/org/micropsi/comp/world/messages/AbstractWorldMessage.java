/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 07.01.2004
 *
 */
package org.micropsi.comp.world.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.micropsi.comp.world.PostOffice;
import org.micropsi.comp.world.objects.AbstractObjectPart;

/**
 * @author Matthias
 *
 */
public abstract class AbstractWorldMessage {

	protected String messageClass;
	protected String messageContent;
	private List<Object> parameters = null;
	protected AbstractObjectPart sender;
	private boolean collectAnswers = false;
	private List<AbstractWorldMessage> answerList = null;

	public AbstractWorldMessage(String messageClass, String messageContent, AbstractObjectPart sender)
	{
		this.messageClass = messageClass;
		this.messageContent = messageContent;
		this.sender = sender;
	}
	
	public boolean isClass(String s) {
		return messageClass != null && messageClass.equals(s);
	}

	public boolean isContent(String s) {
		return messageContent != null && messageContent.equals(s);
	}

	/**
	 * Returns the messageClass.
	 * @return String
	 */
	public String getMessageClass() {
		return messageClass;
	}

	/**
	 * Returns parameter with index index; null, if parameter doesnot exist.
	 * 
	 * @param index
	 * @return Object
	 */
	public Object getParameter(int index) {
		if (parameters != null && parameters.size() > index) {
			return parameters.get(index);
		} else {
			return null;
		}
	}

	/**
	 * Returns the messageContent.
	 * @return String
	 */
	public String getMessageContent() {
		return messageContent;
	}

	/**
	 * Returns the sender of this message
	 * @return List
	 */
	public AbstractObjectPart getSender() {
		return sender;
	}

	/**
	 * Sets the messageClass.
	 * @param messageClass The messageClass to set
	 */
	public void setMessageClass(String messageClass) {
		this.messageClass = messageClass;
	}

	/**
	 * Sets the sender.
	 * @param sender The sender to set
	 */
	public void setSender(AbstractObjectPart sender) {
		this.sender = sender;
	}

	public String toString() {
		return "MessageClass: " + getMessageClass() + "\n" +
				"messageContent: " + getMessageContent() + "\n" + 
				"Parameters: " + getParameters() + "\n";
	}

	/**
	 * Returns the parameters.
	 * @return ArrayList
	 */
	public List getParameters() {
		return parameters;
	}

	/**
	 * Sets the parameters.
	 * @param parameters The parameters to set
	 */
	public void setParameters(List<Object> parameters) {
		this.parameters = new ArrayList<Object>(parameters);
	}

	public void addParameter(Object p) {
		if (parameters == null) {
			parameters = new ArrayList<Object>(3);
		}
		parameters.add(p);
	}

	/**
	 * @return
	 */
	public boolean isCollectAnswers() {
		return collectAnswers || getSender() == null;
	}

	/**
	 * If set true, answer will not send an answer message, but instead store it in "answerlist",
	 * so that it can be queried by the object that has sent the original message.
	 * 
	 * @param b
	 */
	public void setCollectAnswers(boolean b) {
		collectAnswers = b;
	}

	/**
	 * Sends Message m back to sender of this message.
	 * Depending on collectAnswers it either sends the message via PostOffice or stores it so that
	 * it can be queried by the sender later.
	 * 
	 * @param m - the message
	 */
	public void answer(AbstractWorldMessage m) {
		if (isCollectAnswers()) {
			if (answerList == null) {
				answerList = new ArrayList<AbstractWorldMessage>(3);
			}
			answerList.add(m);
		} else {
			PostOffice.sendMessage(m, getSender());
		}
	}

	public List<AbstractWorldMessage> getAnswers() {
		return answerList;
	}

	public abstract void delegateToParent(AbstractObjectPart obj, AbstractObjectPart parent);
	public abstract Set getCurrentRecipientOriginators();
		
}
