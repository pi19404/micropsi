/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.mindconsole/sources/org/micropsi/eclipse/mindconsole/MindEditController.java,v 1.4 2005/07/12 12:53:54 vuine Exp $ 
 */
package org.micropsi.eclipse.mindconsole;

import java.lang.ref.WeakReference;
import java.util.List;

import org.micropsi.eclipse.console.controller.AbstractController;
import org.micropsi.eclipse.mindconsole.widgets.EntityTransferData;
import org.micropsi.eclipse.model.net.AgentNetModelManager;
import org.micropsi.nodenet.CycleObserverIF;


public class MindEditController extends AbstractController implements CycleObserverIF {

	private static MindEditController instance;
	
	
	/**
	 * Warning: Do not call ths method before the AgentManager and
	 * AgentNetModelManagers have succeeded!
	 * @return
	 */
	public static MindEditController getInstance() {
		if(instance == null) {
			instance = new MindEditController();
			instance.setDataBase(AgentNetModelManager.getInstance().getNetModel());
		} 
		return instance;
	}
		
	public void setDataBase(Object o) {
		super.setDataBase(o);
		
		if(o == null) return;
		if(AgentNetModelManager.getInstance().getNetModel().getNet() == null) return;		
		AgentNetModelManager.getInstance().getNetModel().getNet().getCycle().registerCycleObserver(this);

	}
	
	public void lookUpNode(String id) {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((MindEditView)referenced).lookUpNode(id);
			}
		}
	}
	
	public void bringToFront(String id) {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((MindEditView)referenced).bringToFront(id);
			}
		}		
	}		
	
	public void setNetStep(long netstep) {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((MindEditView)referenced).setNetStep(netstep);
			}
		}
	}

	public void startCycle(long netStep) {
		setNetStep(netStep);
	}

	public void endCycle(long netStep) {
	}

	public void triggerRedraw() {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((MindEditView)referenced).triggerRedraw();
			}
		}
	}
	
	public void openParentSpace() {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((MindEditView)referenced).openParentSpace();
			}
		}
	}

	public boolean toggleWatch() {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				return ((MindEditView)referenced).toggleUpdate();
			}
		}
		return false;
	}

	public void setZoom(int zoom) {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((MindEditView)referenced).setZoom(zoom);
			}
		}		
	}

	public void drop(String where, List<EntityTransferData> items) {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((MindEditView)referenced).dropItems(where, items);
				// drop only once!s
				return;
			}
		}		
	}
}
