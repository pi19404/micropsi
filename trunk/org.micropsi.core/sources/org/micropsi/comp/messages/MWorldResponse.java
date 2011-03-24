package org.micropsi.comp.messages;

import java.util.ArrayList;

/**
 * @author Gregor
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class MWorldResponse extends RootMessage implements MessageIF {

	private ArrayList agentResponses = new ArrayList ();

	public MWorldResponse (ArrayList agentResponses){
		this.agentResponses = agentResponses;
	}

	/**
	 * @see org.micropsi.comp.messages.MessageIF#getMessageType()
	 */
	public int getMessageType() {
		return MessageTypesIF.MTYPE_WORLD_RESPONSE;
	}

	/**
	 * Returns the agentResponses.
	 * @return ArrayList
	 */
	public ArrayList getAgentResponses() {
		return agentResponses;
	}

}
