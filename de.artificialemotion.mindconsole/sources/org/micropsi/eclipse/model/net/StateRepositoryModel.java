/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/model/net/StateRepositoryModel.java,v 1.5 2005/07/12 12:53:54 vuine Exp $ 
 */
package org.micropsi.eclipse.model.net;

import java.util.ArrayList;

import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.comp.messages.MQuestion;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.eclipse.mindconsole.MindPlugin;


public class StateRepositoryModel {

	private String agentName;
	ArrayList<String> agentStates;

	public StateRepositoryModel(String agentName) {
		this.agentName = agentName;
		refreshStateList();	
	}
	
	private void refreshStateList() {
		MQuestion q = new MQuestion();
		q.setQuestionName("getstates");
		q.setDestination(agentName);
		try {
			AnswerIF answer = MindPlugin.getDefault().getConsole().askBlockingQuestion(q);
			MTreeNode resp = (MTreeNode)answer.getContent();
			agentStates = new ArrayList<String>();
			MTreeNode[] states = resp.getChildren();
			if(states == null) states = new MTreeNode[0];
			for(int i=0;i<states.length;i++)
				agentStates.add(states[i].getValue());
		} catch (MicropsiException e) {
			MindPlugin.getDefault().handleException(e);
		}
	}
	
	public void deleteState(String state) {
		MindPlugin.getDefault().getConsole().sendCommand(
			ConsoleFacadeIF.ZERO_TOLERANCE,
			agentName,
			"deletestate",
			state,
			false
		);
		refreshStateList();
	}
	
	public void renameState(String oldname, String newname) {
		MindPlugin.getDefault().getConsole().sendCommand(
			ConsoleFacadeIF.ZERO_TOLERANCE,
			agentName,
			"renamestate",
			oldname+" "+newname,
			false
		);
		refreshStateList();
	}
	
	public ArrayList getList() {
		return agentStates;
	}

}
