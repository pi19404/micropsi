/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/model/net/AgentNetModel.java,v 1.6 2005/07/12 12:53:54 vuine Exp $ 
 */
package org.micropsi.eclipse.model.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.progress.ProgressMonitorIF;
import org.micropsi.comp.common.AbstractComponent;
import org.micropsi.comp.common.ComponentRunner;
import org.micropsi.comp.common.ComponentRunnerException;
import org.micropsi.comp.messages.MAnswer;
import org.micropsi.comp.messages.MQuestion;
import org.micropsi.eclipse.common.model.AgentManager;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetFacadeIF;
import org.micropsi.nodenet.NetObserverIF;


public class AgentNetModel extends NetModel {
	
	protected NetObserverIF modelDeleteObserver = new NetObserverIF() {

		public void updateEntities(Iterator changedKeys, long netstep) {
		}

		public void createEntities(Iterator newKeys, long netstep) {
		}

		public void deleteEntities(Iterator deletedKeys, long netstep) {
			while(deletedKeys.hasNext())
				models.remove(deletedKeys.next());
		}
		
	};
	
	/**
	 * the last state reported by initializeFromAgent
	 */
	private String lastLoadedState = null;
	
	public AgentNetModel() {
	
	}
	
	public void loadModels(String statename, ProgressMonitorIF progress) throws FileNotFoundException {		
		String positionfile = getMetadataFilename(statename);
		if(statename != null) {
			loadModels(new FileInputStream(positionfile),progress);
		} else {
			super.loadModels(null,progress);
		}
	}
	
	public void saveModels(String statename) throws MicropsiException,IOException {
		String positionfile = getMetadataFilename(statename);
		
		File f = new File(positionfile);
		if(f.exists()) f.delete();
		f.createNewFile();
		saveModels(new FileOutputStream(f));
	}

	public void initializeFromAgent(ProgressMonitorIF progress) throws FileNotFoundException {
	
		NetFacadeIF previousnet = super.getNet();
		if(previousnet != null) {
			((LocalNetFacade)previousnet).unregisterNetObserver(modelDeleteObserver);
		}
				
		MQuestion q = new MQuestion();
		q.setQuestionName("getcurrentstate");
		String agent = AgentManager.getInstance().getCurrentAgent();
		q.setDestination(agent);
		
		if(agent == null) {
			lastLoadedState = null;
			net = null;
			return;
		}
	
		try {		
			
			lastLoadedState = (String)MindPlugin.getDefault().getConsole().askBlockingQuestion(q,20).getContent();
		
			q = new MQuestion();
			q.setQuestionName("getlocalnet");
			q.setDestination(AgentManager.getInstance().getCurrentAgent());
			
			AnswerIF answer = MindPlugin.getDefault().getConsole().askBlockingQuestion(q,20);

			if(answer != null) {			
				net = (NetFacadeIF)answer.getContent();
			} else {
				net = null;
			}
				
		} catch (MicropsiException e) {
			AgentManager.getInstance().reportBadCurrentAgent();
			MindPlugin.getDefault().getLogger().error("Can't reach agent "+agent,e);			
			lastLoadedState = null;
			net = null;
			return;
		}
		
		// the net may be null if the server is remote
		if(net == null) {
			try {
				// if the server is remote, but the agent is local, we can
				// bypass the server and get the net directly
				if(ComponentRunner.getInstance().componentExists(agent)) {
					ArrayList<QuestionIF> questionlist = new ArrayList<QuestionIF>();
					questionlist.add(q);
					
					AbstractComponent comp = ComponentRunner.getInstance().getComponent(agent);
					List<AnswerIF> answerlist = comp.getConsoleService().answerQuestions(questionlist, 0);
					net = (NetFacadeIF)((MAnswer)answerlist.get(0)).getContent();					
				}
			} catch (ComponentRunnerException e) {
				
			}
		}
		
		// if the net is still null, we really can't retrieve it.
		if(net == null)	return;
		
		loadModels(lastLoadedState,progress);

		((LocalNetFacade)net).registerNetObserver(modelDeleteObserver);

	}
	
	public String getLastLoadedState() {
		return lastLoadedState;
	}

	private String getMetadataFilename(String statename) {
		if(statename == null) return null;
		
		MQuestion q = new MQuestion();
		q.setQuestionName("getstatemetadatapath");
		q.setDestination(AgentManager.getInstance().getCurrentAgent());
		q.setParameters(new String[]{statename});
		
		try {
			return (String)MindPlugin.getDefault().getConsole().askBlockingQuestion(q).getContent();
		} catch (MicropsiException e) {
			return null;
		}
	}

}
