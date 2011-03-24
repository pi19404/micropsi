/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.agentmanager/sources/org/micropsi/eclipse/common/model/IAgentChangeListener.java,v 1.2 2004/08/10 14:35:15 fuessel Exp $ 
 */
package org.micropsi.eclipse.common.model;


public interface IAgentChangeListener {

	public void agentSwitched(String newAgentID);
	
	public void agentDeleted(String deletedAgentID);
	
}
