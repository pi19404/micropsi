/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MUpdateWorld.java,v 1.4 2005/07/12 12:55:17 vuine Exp $
 */
package org.micropsi.comp.messages;

import java.util.ArrayList;
import java.util.List;

public class MUpdateWorld extends RootMessage implements MessageIF {

	private ArrayList<MAction> actions = new ArrayList<MAction>();
	
	private ArrayList<String> newAgents = new ArrayList<String>();
	private ArrayList<String> deletedAgents = new ArrayList<String>();

	/**
	 * @see org.micropsi.comp.messages.MessageIF#getMessageType()
	 */
	public int getMessageType() {
		return MessageTypesIF.MTYPE_SERVER_UPDATEWORLD;
	}

	/**
	 * Returns the actions.
	 * @return ArrayList
	 */
	public List<MAction> getActions() {
		return actions;
	}

	/**
	 * Sets the actions.
	 * @param actions The actions to set
	 */
	public void setActions(List<MAction> actions) {
		this.actions = new ArrayList<MAction>(actions);
	}
	
	public void addAction(MAction action) {
		actions.add(action);
	}	

	/**
	 * Returns the deletedAgents.
	 * @return ArrayList
	 */
	public List<String> getDeletedAgents() {
		return deletedAgents;
	}

	/**
	 * Returns the newAgents.
	 * @return ArrayList
	 */
	public List<String> getNewAgents() {
		return newAgents;
	}

	/**
	 * Sets the deletedAgents.
	 * @param deletedAgents The deletedAgents to set
	 */
	public void setDeletedAgents(List<String> deletedAgents) {
		this.deletedAgents = new ArrayList<String>(deletedAgents);
	}

	/**
	 * Sets the newAgents.
	 * @param newAgents The newAgents to set
	 */
	public void setNewAgents(List<String> newAgents) {
		this.newAgents = new ArrayList<String>(newAgents);
	}

}
