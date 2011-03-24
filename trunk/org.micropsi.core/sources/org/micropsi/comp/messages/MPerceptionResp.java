/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/messages/MPerceptionResp.java,v 1.3 2005/07/12 12:55:17 vuine Exp $
 */
package org.micropsi.comp.messages;

import java.util.ArrayList;
import java.util.List;

public class MPerceptionResp extends RootMessage implements MessageIF {

	private ArrayList<MPercept> percepts;

	public MPerceptionResp() {
		percepts = new ArrayList<MPercept>();
	}	 

	public MPerceptionResp(List<MPercept> percepts){
		this.percepts = new ArrayList<MPercept>(percepts);
	}

	/**
	 * @see org.micropsi.comp.messages.MessageIF#getMessageType()
	 */
	public int getMessageType() {
		return MessageTypesIF.MTYPE_AGENT_PERCEPTIONRESP;
	}

	public List<MPercept> getPercepts() {
		return percepts;
	}
	
	public void addPercept(MPercept percept) {
		percepts.add(percept);
	}
	
	public void clear() {
		percepts.clear();
	}

}
