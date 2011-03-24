/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.console/sources/org/micropsi/eclipse/console/adminperspective/ParameterController.java,v 1.5 2005/07/12 12:52:34 vuine Exp $ 
 */
package org.micropsi.eclipse.console.adminperspective;

import java.lang.ref.WeakReference;

import org.eclipse.swt.widgets.Shell;

import org.micropsi.eclipse.console.controller.AbstractController;
import org.micropsi.eclipse.console.dialogs.AddMonitorDialog;
import org.micropsi.eclipse.console.internal.ParameterMonitorRegistry;

public class ParameterController extends AbstractController {

	private static ParameterController instance;
	
	public static ParameterController getInstance() {
		if(instance == null) {
			instance = new ParameterController();
		} 
		return instance;
	}
	
	public void tick(long step) {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((ParameterView)referenced).tick(step);
			}
		}
	}

	public void clear() {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((ParameterView)referenced).clear();
			}
		}
	}
	
	public void addParameterMonitor(String id, Shell shell) {
		
		AddMonitorDialog dlg = new AddMonitorDialog(shell, ParameterMonitorRegistry.getInstance());
		dlg.setInitialSelection(id);
		dlg.setBlockOnOpen(true);
		dlg.open();
		if(	dlg.getReturnCode() == AddMonitorDialog.OK &&
			dlg.getSelMon() != null) {

			for(int i=listeners.size()-1;i>=0;i--) {
				WeakReference ref = listeners.get(i);
				Object referenced = ref.get();
				if(referenced == null) {
					listeners.remove(i);
				} else {
					((ParameterView)referenced).addParameterMonitor(
						dlg.getSelMon(), 
						dlg.getSelCol(),
						dlg.getSelDis()
					);
				}
			}		
		}
	}
	
	public void saveState(String stateName) {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((ParameterView)referenced).saveState(stateName);
			}
		}				
	}

	public void loadState(String stateName) {
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((ParameterView)referenced).loadState(stateName);
			}
		}		
	}

}
