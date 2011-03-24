package org.micropsi.eclipse.mindconsole.wizards;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.mindconsole.groups.SensActGroup;
import org.micropsi.nodenet.NetFacadeIF;
import org.micropsi.nodenet.Node;
import org.micropsi.nodenet.NodeFunctionalTypesIF;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * 
 * 
 */
public class SensActConnectWizard extends Wizard {

	class SelectDataPage extends WizardPage {		
		protected SensActGroup sag;
		
		private NetFacadeIF net;
		private int type;
		private Node node;
		
		public SelectDataPage(NetFacadeIF net,int type) {
			super("selectdata");
			this.net = net;
			this.type = type;
			setTitle(isSensor ? "Sensor" : "Actor");
			setDescription(isSensor ? "Connect to DataSource" : "Connect to DataTarget");
		}
		
		public SelectDataPage(NetFacadeIF net,Node node) {
			super("selectdata");
			this.net = net;
			this.node = node;
			setTitle(isSensor ? "Sensor" : "Actor");
			setDescription(isSensor ? "Connect to DataSource" : "Connect to DataTarget");
		}

		public void createControl(Composite parent) {
			
			// top level group
			Composite topLevel = new Composite(parent,SWT.NONE);
			topLevel.setLayout(new GridLayout());
			topLevel.setFont(parent.getFont());

			if(node == null)	
				sag = new SensActGroup(topLevel,net,type);
			else
				sag = new SensActGroup(topLevel,net,node);

			setErrorMessage(null);
			setMessage(null);
			setControl(topLevel);
		}
	}
	
	private NetFacadeIF net;
	private String nodeID;
	private Node node;
	private int type;
	
	private SelectDataPage selectDataPage;
	private boolean isSensor = true;
	
	public SensActConnectWizard(NetFacadeIF net, int type) {
		super();
		this.net = net;
		this.type = type;
		isSensor = (type == NodeFunctionalTypesIF.NT_SENSOR);
		setHelpAvailable(false);
		setWindowTitle("Connect");
		selectDataPage = new SelectDataPage(net,type);
		addPage(selectDataPage);
	}
	
	public SensActConnectWizard(NetFacadeIF net, Node node) {
		super();
		this.net = net;
		this.node = node;
		this.nodeID = node.getID();
		this.type = node.getType();
		isSensor = (node.getType() == NodeFunctionalTypesIF.NT_SENSOR);
		setHelpAvailable(false);
		setWindowTitle("Connect");
		selectDataPage = new SelectDataPage(net,node);
		addPage(selectDataPage);
	}
	
	public int getNodeType() {
		return (node == null) ? type : node.getType();
	}
	
	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}	

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		
		// if the wizard is used inside an entity creation wizard: get the node id
		IWizardPage prevpage = selectDataPage.getPreviousPage();
		if(prevpage != null) {
			prevpage.getWizard().performFinish();
			setNodeID(((NewNodeWizard)prevpage.getWizard()).getNewNodeID());
		}
		
		try {
			if(isSensor) {
				if(selectDataPage.sag.isConnected())
					net.connectSensor(nodeID, selectDataPage.sag.getConnection());
				else 
					net.disconnectSensor(nodeID);
			} else {
				if(selectDataPage.sag.isConnected())
					net.connectActor(nodeID, selectDataPage.sag.getConnection());		
				else
					net.disconnectActor(nodeID);
			}
		} catch (MicropsiException e) {
			MindPlugin.getDefault().handleException(e);
			return false;
		}
		return true;
	}

}
