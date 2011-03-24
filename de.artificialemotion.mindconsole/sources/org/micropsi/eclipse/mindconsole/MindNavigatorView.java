package org.micropsi.eclipse.mindconsole;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.micropsi.eclipse.common.model.AgentManager;
import org.micropsi.eclipse.console.controller.IViewControllerListener;
import org.micropsi.eclipse.mindconsole.groups.NetHierarchyGroup;
import org.micropsi.eclipse.mindconsole.jdt.ModuleJavaManager;
import org.micropsi.eclipse.model.net.AgentNetModelManager;
import org.micropsi.eclipse.model.net.NetModel;

/**
 * 
 * 
 * 
 */
public class MindNavigatorView extends ViewPart implements IViewControllerListener {

	private IStatusLineManager statusLineManager;
	private NetHierarchyGroup netHierarchy;
	private NetModel netmodel;
	private Thread uiThread;
 	
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
	
		uiThread = Thread.currentThread();
		
		if(memento != null) {
			IMemento m = memento.getChild("agentmanager");
			if(m != null)
				AgentManager.getInstance().loadState(m);
		}

		MindNavigatorController.getInstance().registerView(this);
							
		netmodel = AgentNetModelManager.getInstance().getNetModel();

	}


	public void createPartControl(Composite parent) {
				
		Composite topLevel = new Composite(parent,SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		topLevel.setLayout(gridLayout);
		topLevel.setFont(parent.getFont());

		// to ensure initialisation
		MindPlugin.getDefault();
		
		netHierarchy = new NetHierarchyGroup(
				topLevel,
				netmodel.getNet(),
				new MindNavigatorCallback(
					topLevel.getShell(),
					ModuleJavaManager.getInstance()	
				)
			);
		if(netmodel != null && netmodel.getNet() != null)
			netmodel.getNet().registerNetObserver(netHierarchy);

				
		statusLineManager = this.getViewSite().getActionBars().getStatusLineManager();
	}

	public void setFocus() {
	}

	public IStatusLineManager getStatusLineManager() {
		return statusLineManager;
	}


	public void refresh() {
		netHierarchy.refresh();
	}
	
	public void saveState(IMemento memento) {
		IMemento m = memento.createChild("agentmanager");
		AgentManager.getInstance().saveState(m);
		memento.putMemento(m);
	}


	public void setDataBase(Object o) {

		if(netmodel != null && netmodel.getNet() != null)
			netmodel.getNet().unregisterNetObserver(netHierarchy);
		
		if(o == null) {
			netmodel = null;			
			Display.findDisplay(uiThread).asyncExec(new Runnable() {
				public void run() {
					netHierarchy.setData(null);
				}
			});			
			return;		
		}
		
		netmodel = (NetModel)o;
		
		netmodel.getNet().registerNetObserver(netHierarchy);
		Display.findDisplay(uiThread).asyncExec(new Runnable() {
			public void run() {
				netHierarchy.setData(netmodel.getNet());
			}
		});
	}


	public void setData(Object o) {
	}
	
}
