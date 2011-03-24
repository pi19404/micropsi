/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.agentmanager/sources/org/micropsi/eclipse/common/model/AgentManager.java,v 1.5 2005/07/12 12:55:42 vuine Exp $ 
 */
package org.micropsi.eclipse.common.model;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.ui.IMemento;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.console.AnswerHandlerIF;
import org.micropsi.comp.messages.MQuestion;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.eclipse.agentmanager.AgentManagerConsole;
import org.micropsi.eclipse.agentmanager.AgentManagerPlugin;
import org.micropsi.eclipse.console.command.AnswerQueue;


public class AgentManager implements AnswerHandlerIF {

	private static AgentManager instance;
	
	public static AgentManager getInstance() {
		if(instance == null) instance = new AgentManager();
		return instance; 
	}
		
	private AgentManager() {
		
		String serverID = AgentManagerConsole.getInstance().getBserv().getServerID();
		AnswerQueue aq = new AnswerQueue(this);
		AgentManagerPlugin.getDefault().getConsole().subscribe(5,serverID,"getagentlist","",aq);
	}
	
	private String currentAgent = null;
	private ArrayList<IAgentChangeListener> listeners = new ArrayList<IAgentChangeListener>();
	
	public boolean changeCurrentAgent(String ID) {
		if(ID == null) return false;
		
		String Q = "getagentlist";
		
		MQuestion q = new MQuestion(Q, QuestionIF.AM_ANSWER_ONCE);
		q.setDestination(AgentManagerPlugin.getDefault().getServerID());
		AnswerIF ret;
		try {
			ret = AgentManagerPlugin.getDefault().getConsole().askBlockingQuestion(q);
		} catch (MicropsiException e) {
			AgentManagerPlugin.getDefault().handleException(e);
			return false;
		}
		if(!doesAgentListContain(ID,ret)) return false;

		currentAgent = ID;
		
		for(int i=0;i<listeners.size();i++)
			listeners.get(i).agentSwitched(ID);
			
		return true;
	}
	
	private boolean doesAgentListContain(String id, AnswerIF ret) {
		MTreeNode root = (MTreeNode)ret.getContent();
		Iterator<MTreeNode> children = root.children();
		if(children == null) return false;
		
		boolean found = false;
		while(children.hasNext()) {
			String next = (children.next()).getValue(); 
			if(next.equals(id)) found = true;
		}
		
		return found;
	}
	
	public String getCurrentAgent() {
		return currentAgent;
	}
		
	public void reportBadCurrentAgent() {
		currentAgent = null;
	}
	
	public void addAgentChangeListener(IAgentChangeListener listener) {
		if(!listeners.contains(listener)) listeners.add(listener);
	}
	
	public void removeAgentChangeListener(IAgentChangeListener listener) {
		if(listeners.contains(listener)) listeners.remove(listener);
	}

	public void saveState(IMemento m) {
		m.putString("current",currentAgent);
	}

	public void loadState(IMemento m) {
		currentAgent = m.getString("current");
	}

	public void reportAgentDeletion(String res) {
		if(res == null) return; 
		if(res.equals(currentAgent)) {
			currentAgent = null;
			for(int i=0;i<listeners.size();i++)
				listeners.get(i).agentSwitched(null);
		}
		
		for(int i=0;i<listeners.size();i++)
			listeners.get(i).agentDeleted(res);			
	}

	public void handleAnswer(AnswerIF a) {
		if(getCurrentAgent() == null) return;
		if(!a.getAnsweredQuestion().getQuestionName().equals("getagentlist")) return;
		
		if(!doesAgentListContain(getCurrentAgent(),a)) {
			reportBadCurrentAgent();
			for(int i=0;i<listeners.size();i++)
				listeners.get(i).agentSwitched(null);
		}		
	}
	
}
