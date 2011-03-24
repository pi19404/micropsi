package org.micropsi.eclipse.emotion3d;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.comp.console.AnswerQueueIF;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.comp.console.worldconsole.SWTAwareAnswerQueue;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.eclipse.common.model.AgentManager;
import org.micropsi.eclipse.common.model.IAgentChangeListener;
import org.micropsi.eclipse.console.command.IConsoleWorkbenchPart;
import org.micropsi.eclipse.emotion3d.win32.FaceSurface;


public class Emotion3DView extends ViewPart implements IConsoleWorkbenchPart, IAgentChangeListener {

	private IStatusLineManager statusLineManager;
	private IPreferenceStore prefs;
	
	private Logger logger;
	private ConsoleFacadeIF console;
	private AnswerQueueIF callback;
	
	private FaceSurface face;
	
	private IEmotionFaceTranslation translation = null;
	
	private String currentAgent;
	private boolean isSubscribed = false;
	
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		prefs = PlatformUI.getPreferenceStore();
		logger = Emotion3DConsole.getInstance().getBserv().getLogger();
		console = Emotion3DConsole.getInstance().getConsole();
		callback = new SWTAwareAnswerQueue(this);
		
		AgentManager.getInstance().addAgentChangeListener(this);
		
		if(AgentManager.getInstance().getCurrentAgent() != null) {
			setupForCurrentAgent();
		}
		
		getViewSite().getActionBars().getToolBarManager().add(new TranslationPulldownAction(this));
		
		setTranslation(FaceTranslationRegistry.getInstance().getEmotionFaceTranslations().get(0).getName());
		
	}
	
	public void stop() {
		if(isSubscribed) {
			console.unsubscribe(currentAgent,"getactorvalues","",callback);
			isSubscribed = false;
		}
	}
	
	public void setupForCurrentAgent() {
		if(isSubscribed) {
			stop();
		}
		
		currentAgent = AgentManager.getInstance().getCurrentAgent();
		
		console.subscribe(
			10,
			currentAgent,
			"getactorvalues",
			"",
			callback,
			null	
		);
		isSubscribed = true;

		updatePartName();
	}
	
	public void createPartControl(Composite parent) {
		
		Composite topLevel = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		topLevel.setLayout(layout);
		topLevel.setFont(parent.getFont());
		
		String cmd = StartStringFactory.create3DStartString(prefs);						
		face = new FaceSurface(topLevel, cmd);
		face.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		statusLineManager = this.getViewSite().getActionBars().getStatusLineManager();
	}
	
	public IStatusLineManager getStatusLineManager() {
		return statusLineManager;
	}

	public void setFocus() {
	}

	public void handleAnswer(AnswerIF answer) {
		MTreeNode data = (MTreeNode)answer.getContent();
		
		if(translation != null) {
			double[] values = translation.calculateFaceParameters(data);
			List<String> names = translation.getFaceParameterNames();
			try {
				if(names.size() != values.length) {
					logger.warn("Bad face translation: value and name list lengths do not match");
				}
				
				StringBuffer namesString = new StringBuffer();
				StringBuffer valuesString = new StringBuffer();
				
				for(int i=0;i<values.length;i++) {
					if(i>0) {
						namesString.append(',');
						valuesString.append(',');
					}
					namesString.append(names.get(i));
					valuesString.append(values[i]);
				}
				
				face.updatebones(namesString.toString().toCharArray(),valuesString.toString().toCharArray());
			} catch(Exception e) {
				Emotion3DConsole.getInstance().getBserv().handleException(e);
			}
		}
	}

	public void agentSwitched(String newAgentID) {
		if(newAgentID == null) {
			stop();
			return;
		}
		
		if(newAgentID.equals(currentAgent)) {
			return;
		}
		
		stop();
		currentAgent = newAgentID;
		setupForCurrentAgent();
	}

	public void agentDeleted(String deletedAgentID) {
		if(deletedAgentID != null && deletedAgentID.equals(currentAgent)) {
			stop();
			currentAgent = null;
			updatePartName();
		}
	}

	public void setTranslation(String name) {
		Iterator<IEmotionFaceTranslation> translations = FaceTranslationRegistry.getInstance().getEmotionFaceTranslations().iterator();
		IEmotionFaceTranslation t = translations.next();
		if(t.getName().equals(name)) {
			this.translation = t;
			updatePartName();
			return;
		}
		t = null;
		updatePartName();
	}

	private void updatePartName() {
		String agent = currentAgent;
		if(agent == null) {
			agent = "[nobody]";
		}
		
		String tname = "no translation";
		if(translation != null) {
			tname = translation.getName();
		}
		
		String name = "Emotions of "+agent+" ("+tname+")";
		
		setPartName(name);		
	}
}
