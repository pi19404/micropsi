/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/model/net/AgentNetModelManager.java,v 1.10 2005/04/23 12:03:16 vuine Exp $ 
 */
package org.micropsi.eclipse.model.net;

import java.io.FileNotFoundException;
import java.util.Iterator;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.common.ProgressDialog;
import org.micropsi.eclipse.common.model.AgentManager;
import org.micropsi.eclipse.common.model.IAgentChangeListener;
import org.micropsi.eclipse.mindconsole.EntityEditController;
import org.micropsi.eclipse.mindconsole.LinkageEditController;
import org.micropsi.eclipse.mindconsole.MindEditController;
import org.micropsi.eclipse.mindconsole.MindNavigatorController;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.mindconsole.NetUserInteractionFacility;
import org.micropsi.eclipse.mindconsole.jdt.ModuleJavaManager;
import org.micropsi.eclipse.mindconsole.monitors.AgentTimeGenerator;
import org.micropsi.eclipse.runtime.RuntimePlugin;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NativeModule;
import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.NetObserverIF;
import org.micropsi.nodenet.UserInteractionIF;

public class AgentNetModelManager implements IAgentChangeListener {
	
	private class AutoReplaceEnabler implements NetObserverIF {

		public void updateEntities(Iterator arg0, long arg1) {
		}

		public void createEntities(Iterator entities, long netstep) {
			while(entities.hasNext()) {
				String id = (String)entities.next();
				try {
					NetEntity e = model.getNet().getEntity(id);
					if(e.getEntityType() != NetEntityTypesIF.ET_MODULE_NATIVE) continue;
					
					ModuleJavaManager.getInstance().enableAutoReplacement((NativeModule)e);
				} catch (MicropsiException e) {
					MindPlugin.getDefault().handleException(e);
				}
			}					
		}

		public void deleteEntities(Iterator entities, long netstep) {
			while(entities.hasNext()) {
				String id = (String)entities.next();
				ModuleJavaManager.getInstance().disableAutoReplacement(id);
			}								
		}	
	}
	
	private static AgentNetModelManager instance;
	
	public static AgentNetModelManager getInstance() {
		if(instance == null) instance = new AgentNetModelManager();
		return instance;
	}

	private AgentNetModelManager() {
		AgentManager.getInstance().addAgentChangeListener(this);
	}
	
	private AgentNetModel model;    
    private LoadDetector detector = null;
    private AutoReplaceEnabler replaceEnabler = null;
    private AgentTimeGenerator atg = null;
    
	public void reinitialize() {
		if(model.getNet() != null) {
			((LocalNetFacade)model.getNet()).unregisterLocalNetLoadMonitor(detector);
			((LocalNetFacade)model.getNet()).unregisterNetObserver(replaceEnabler);
			model.getNet().getCycle().unregisterCycleObserver(atg);
		}		
		
		try {
			model.initializeFromAgent(new ProgressDialog("Positioning...",RuntimePlugin.getDefault().getShell()));
			
			ModuleJavaManager.getInstance().disableAutoReplacementForAll();
			Iterator all = model.getNet().getAllEntities();
			while(all.hasNext()) {
				NetEntity e = (NetEntity) all.next();
				if(e.getEntityType() != NetEntityTypesIF.ET_MODULE_NATIVE) continue;
				NativeModule m = (NativeModule)e;
				ModuleJavaManager.getInstance().enableAutoReplacement(m);
			}

			
		} catch (FileNotFoundException e) {
			MindPlugin.getDefault().handleException(e);
		}
		
		if(model.getNet() != null) {			
			((LocalNetFacade)model.getNet()).registerLocalNetLoadMonitor(detector);
			((LocalNetFacade)model.getNet()).registerNetObserver(replaceEnabler);
			model.getNet().getCycle().registerCycleObserver(atg);			
		}
			
	}
	
	public AgentNetModel getNetModel() {
		if(model == null) {
			detector = new LoadDetector(RuntimePlugin.getDefault().getShell());
			replaceEnabler = new AutoReplaceEnabler();
			atg = new AgentTimeGenerator();
			model = new AgentNetModel(); 

			try {
				model.initializeFromAgent(new ProgressDialog("Positioning...",RuntimePlugin.getDefault().getShell()));				
			} catch (FileNotFoundException e) {
				MindPlugin.getDefault().handleException(e);
			}

			if(model.getNet() != null) {
				((LocalNetFacade)model.getNet()).registerLocalNetLoadMonitor(detector);
				((LocalNetFacade)model.getNet()).registerNetObserver(replaceEnabler);
				model.getNet().getCycle().registerCycleObserver(atg);
				
				ModuleJavaManager.getInstance().disableAutoReplacementForAll();
				Iterator all = model.getNet().getAllEntities();
				while(all.hasNext()) {
					NetEntity e = (NetEntity) all.next();
					if(e.getEntityType() != NetEntityTypesIF.ET_MODULE_NATIVE) continue;
					NativeModule m = (NativeModule)e;
					ModuleJavaManager.getInstance().enableAutoReplacement(m);
				}
				
				if(model.getNet() instanceof LocalNetFacade) {
					UserInteractionIF interaction = new NetUserInteractionFacility(RuntimePlugin.getDefault().getShell(),model.getNet().getCycle()); 
					((LocalNetFacade)model.getNet()).setUserInteractionImplementation(interaction);
				}
			}
				
		}
		
		return model;
	}

	public void agentSwitched(String newAgentID) {
		MindEditController.getInstance().setDataBase(null);
		EntityEditController.getInstance().setDataBase(null);
		LinkageEditController.getInstance().setDataBase(null);							
		MindNavigatorController.getInstance().setDataBase(null);
		
		reinitialize();
		if(getNetModel().getNet() != null) {
			
			if(model.getNet() instanceof LocalNetFacade) {
				UserInteractionIF interaction = new NetUserInteractionFacility(RuntimePlugin.getDefault().getShell(),model.getNet().getCycle()); 
				((LocalNetFacade)model.getNet()).setUserInteractionImplementation(interaction);
			}
			
			MindEditController.getInstance().setDataBase(getNetModel());
			EntityEditController.getInstance().setDataBase(getNetModel());
			LinkageEditController.getInstance().setDataBase(getNetModel());
			MindNavigatorController.getInstance().setDataBase(getNetModel());
		}
	}

	public void agentDeleted(String deletedAgentID) {
	}    

}
