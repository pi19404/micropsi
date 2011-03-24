package org.micropsi.eclipse.mindconsole.jdt;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.model.net.AgentNetModelManager;
import org.micropsi.eclipse.model.net.NetModel;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetCycleIF;

/**
 * 
 * 
 * 
 */
public class AutoUpdateListener implements IResourceChangeListener {

	class ObservedElement {

		public String id;
		public String type;
		public IPath path;
		
		public ObservedElement(String id, String type, IPath path) {
			this.id = id;
			this.path = path;
			this.type = type;
		}
		
	}

	private HashMap<String,ObservedElement> observed = new HashMap<String,ObservedElement>();
	private NetModel netmodel;
	private Shell shell;
	
	public AutoUpdateListener(Shell shell) {
		this.shell = shell;
		this.netmodel = AgentNetModelManager.getInstance().getNetModel();
	}

	/**
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		Iterator i = observed.values().iterator();
		while(i.hasNext()) {
			ObservedElement e = (ObservedElement)i.next();
			IResourceDelta ed = delta.findMember(e.path);
			try {
				if(ed != null) {
					NetCycleIF cycle = ((LocalNetFacade)netmodel.getNet()).getCycle();
					
					boolean shouldResume = false;
					if(!cycle.isSuspended()) {
						cycle.suspend();
						shouldResume = true;
					}
					
					((LocalNetFacade)netmodel.getNet()).replaceNativeModuleImplementation(
						e.id,
						ModuleJavaManager.getInstance().getNewJDTClassLoader(),
						e.type
					);
					
					if(shouldResume) {
						cycle.resume();
					}
				}
			} catch (MicropsiException exception) {
				String error = MindPlugin.getDefault().handleException(exception);
				failDialog(e.id,error);	
			}
		}
		
	}
	
	public void addObserved(String moduleID, String type, IPath path) {
		observed.put(moduleID, new ObservedElement(moduleID,type, path));
	}
	
	public void removeObserved(String moduleID) {
		observed.remove(moduleID);
	}

	public void removeAllObserved() {
		observed.clear();
	}
	
	public boolean isObserved(String moduleID) {
		return observed.containsKey(moduleID);
	}
	
	private void failDialog(String id, String error) {
		MessageDialog dlg = new MessageDialog(
			shell,
			"Autoreplace failed",
			null,
			"The implementation of module "+id+" could not be replaced. Reason: "+error,
			MessageDialog.ERROR,
			new String[] {"OK"},
			0
		);
		dlg.open();
	}


}
