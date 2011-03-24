package org.micropsi.eclipse.mindconsole;

import java.util.List;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.progress.ProgressMonitorIF;
import org.micropsi.eclipse.common.ProgressDialog;
import org.micropsi.eclipse.mindconsole.dialogs.InspectorDialog;
import org.micropsi.eclipse.mindconsole.dialogs.LinkDialog;
import org.micropsi.eclipse.mindconsole.dialogs.LookupDialog;
import org.micropsi.eclipse.mindconsole.dialogs.SpaceDialog;
import org.micropsi.eclipse.mindconsole.jdt.ModuleJavaManager;
import org.micropsi.eclipse.mindconsole.widgets.EntityTransferData;
import org.micropsi.eclipse.mindconsole.widgets.IMindEditCallback;
import org.micropsi.eclipse.mindconsole.wizards.CreateLinkWizard;
import org.micropsi.eclipse.mindconsole.wizards.NewNetEntityWizard;
import org.micropsi.eclipse.mindconsole.wizards.SensActConnectWizard;
import org.micropsi.eclipse.mindconsole.wizards.SlotGateWizard;
import org.micropsi.eclipse.model.net.AgentNetModelManager;
import org.micropsi.eclipse.model.net.NetModel;
import org.micropsi.eclipse.runtime.RuntimePlugin;
import org.micropsi.nodenet.Link;
import org.micropsi.nodenet.NativeModule;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.NodeSpaceModule;


public abstract class AbstractMindCallback implements IMindEditCallback {

	protected Shell shell;
	protected ModuleJavaManager javaManager;
	protected NetModel netmodel;

	public AbstractMindCallback(Shell shell, ModuleJavaManager javaManager) {
		this.shell = shell;
		this.javaManager = javaManager;
		netmodel = AgentNetModelManager.getInstance().getNetModel();
	}

	public void handleException(Throwable e) {
		MindPlugin.getDefault().handleException(e);
	}

	public void createSlot(String spaceID) {
		try {
			NodeSpaceModule m = netmodel.getNet().getNodeSpaceModule(spaceID);
			SlotGateWizard wiz = new SlotGateWizard(netmodel.getNet(),m);
			WizardDialog wizDial = new WizardDialog(shell,wiz);
			wiz.setContainer(wizDial);
			wizDial.open();
		} catch (MicropsiException e) {
			handleException(e);
		}
	}

	public void createGate(String spaceID) {
		try {		
			NodeSpaceModule m = netmodel.getNet().getNodeSpaceModule(spaceID);
			SlotGateWizard wiz = new SlotGateWizard(netmodel.getNet(),m);
			WizardDialog wizDial = new WizardDialog(shell,wiz);
			wiz.setContainer(wizDial);
			wizDial.open();
		} catch (MicropsiException e) {
			handleException(e);
		}
	}

	public void createNativeModule(String spaceID) {
		try {
			NewNetEntityWizard wiz = new NewNetEntityWizard(netmodel,spaceID,NetEntityTypesIF.ET_MODULE_NATIVE);
			WizardDialog wizDial = new WizardDialog(shell,wiz);
			wiz.setContainer(wizDial);
			wizDial.open();
		} catch (MicropsiException e) {
			handleException(e);
		}					
	}

	public abstract void selectEntity(String entityID);
	
	public void selectLink(Link link) {
		EntityEditController.getInstance().setData(link.getLinkingEntity().getID());
		EntityEditController.getInstance().selectGate(link.getLinkingGate());
		LinkageEditController.getInstance().selectLink(link);
	}

	public abstract void openEntity(String entityID);

	public void connectSensAct(String nodeID) {
		Node n = null;
		try {
			n = (Node) netmodel.getNet().getEntity(nodeID);
		} catch (MicropsiException e) {
			handleException(e);
		}
		SensActConnectWizard wiz = new SensActConnectWizard(netmodel.getNet(),n);
		WizardDialog wizDial = new WizardDialog(shell,wiz);
		wiz.setContainer(wizDial);
		wizDial.open();		
	}

	public void setAutoUpdate(Shell shell, String entityID, boolean newState) {

		if(newState) {
			try {
				NativeModule mod = (NativeModule)netmodel.getNet().getEntity(entityID);
				javaManager.enableAutoReplacement(mod);
			} catch (MicropsiException e) {
				handleException(e);
			}						
		} else {
			javaManager.disableAutoReplacement(entityID);
		}
			
	}

	public void openInspector(Shell shell, String entityID) {
		
		try {
			InspectorDialog dlg = new InspectorDialog(shell, netmodel.getModel(entityID));
			dlg.open();
		} catch (MicropsiException e) {
			MindPlugin.getDefault().handleException(e);
		}		
		
	}
	public void openLinkDialog(Shell shell, Link link) {
		
		LinkDialog dlg = new LinkDialog(shell, netmodel, link);
		dlg.open();		
	}
	
	public void lookUp(Shell shell, String currentSpace) {
		LookupDialog d = new LookupDialog(shell,netmodel.getNet(),currentSpace);
		d.setBlockOnOpen(true);
		d.open();
		if(d.getReturnCode() != LookupDialog.OK) return;
		
		MindEditController.getInstance().lookUpNode(d.getSelectedID());
	}

	public void createLink(Shell shell, String entityID) {
		CreateLinkWizard linkWiz = new CreateLinkWizard(AgentNetModelManager.getInstance().getNetModel(),entityID);
		WizardDialog wizDial = new WizardDialog(shell,linkWiz);
		linkWiz.setContainer(wizDial);
		wizDial.open();			
	}

	public void editSpaceProperties(Shell shell, String entityID) {
		SpaceDialog dlg;
		try {
			dlg = new SpaceDialog(shell, netmodel, entityID);
			dlg.open();
		} catch (MicropsiException exc) {
			MindPlugin.getDefault().handleException(exc);					
		}
	}

	public abstract void dropEntities(String where, List<EntityTransferData> items);
	
	public ProgressMonitorIF createProgressMonitor(String text) {
		return new ProgressDialog(text,RuntimePlugin.getDefault().getShell());
	}

}
