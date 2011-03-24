package org.micropsi.eclipse.mindconsole.wizards;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.model.net.AgentNetModelManager;
import org.micropsi.nodenet.NetFacadeIF;
import org.micropsi.nodenet.NetParametersIF;
import org.micropsi.nodenet.NodeFunctionalTypesIF;
import org.micropsi.nodenet.NodeSpaceModule;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * 
 * 
 */
public class NewNodeWizard extends Wizard {

	class NodeTypeSelectPage extends WizardSelectionPage {

		private Composite topLevel;
		private Combo combo = null;
		private IWizardNode connectNode = null;

		public NodeTypeSelectPage() {
			super("nodetypeselect");
			this.setTitle("Node");
			this.setDescription("Select the type of the new Node");
			
			connectNode = new IWizardNode() {

				SensActConnectWizard connectWiz;

				public void dispose() {
					if(connectWiz != null) connectWiz.dispose();
				}

				public Point getExtent() {
					return new Point(-1,-1);
				}
				
				public IWizard getWizard() {
					if(connectWiz == null || connectWiz.getNodeType() != nodeType) {
						connectWiz = new SensActConnectWizard(net, nodeType);
					}
					return connectWiz;
				}

				public boolean isContentCreated() {
				   return (connectWiz != null && connectWiz.getNodeType() == nodeType);
				}

			};
			
		}
		
		private void setCombo(boolean enabled) {
			combo.setEnabled(enabled);
		}

		/**
		 * @see org.eclipse.jface.dialogs.DialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent) {

			// top level group
			topLevel = new Composite(parent,SWT.NONE);
			topLevel.setLayout(new GridLayout());
			topLevel.setFont(parent.getFont());

			Label label = new Label(topLevel,SWT.NONE);
			label.setText("Type:");

			Button radio = new Button(topLevel,SWT.RADIO);
			radio.setSelection(true);
			radio.setText("Concept");
			radio.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setCombo(false);
					setSelectedNode(null);
					nodeType = NodeFunctionalTypesIF.NT_CONCEPT;
					validate();
				}
			});

			radio = new Button(topLevel,SWT.RADIO);
			radio.setText("Topo (exp.)");
			radio.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setCombo(false);
					setSelectedNode(null);
					nodeType = NodeFunctionalTypesIF.NT_TOPO;
					validate();
				}
			});
			
			radio = new Button(topLevel,SWT.RADIO);
			radio.setText("Register");
			radio.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setCombo(false);
					setSelectedNode(null);
					nodeType = NodeFunctionalTypesIF.NT_REGISTER;
					validate();
				}
			});

			radio = new Button(topLevel,SWT.RADIO);
			radio.setText("Sensor");
			radio.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setCombo(false);
					setSelectedNode(connectNode);
					nodeType = NodeFunctionalTypesIF.NT_SENSOR;
					validate();
				}
			});
			
			radio = new Button(topLevel,SWT.RADIO);
			radio.setText("Actor");
			radio.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setCombo(false);
					setSelectedNode(connectNode);
					nodeType = NodeFunctionalTypesIF.NT_ACTOR;
					validate();
				}
			});

			radio = new Button(topLevel,SWT.RADIO);
			radio.setText("Associator");
			radio.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setCombo(false);
					setSelectedNode(null);
					nodeType = NodeFunctionalTypesIF.NT_ASSOCIATOR;
					validate();
				}
			});

			radio = new Button(topLevel,SWT.RADIO);
			radio.setText("Dissociator");
			radio.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setCombo(false);
					setSelectedNode(null);
					nodeType = NodeFunctionalTypesIF.NT_DISSOCIATOR;
					validate();
				}
			});
			
			radio = new Button(topLevel,SWT.RADIO);
			radio.setText("Activator");
			radio.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setCombo(false);
					setSelectedNode(null);
					nodeType = NodeFunctionalTypesIF.NT_ACTIVATOR;
					validate();
				}
			});
			
			radio = new Button(topLevel,SWT.RADIO);
			radio.setText("Deactivator");
			radio.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setCombo(false);
					setSelectedNode(null);
					nodeType = NodeFunctionalTypesIF.NT_DEACTIVATOR;
					validate();
				}
			});

			radio = new Button(topLevel,SWT.RADIO);
			radio.setText("Directional activator");
			radio.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setCombo(true);
					setSelectedNode(null);
					validate();
				}
			});

			combo = new Combo(topLevel,SWT.DROP_DOWN | SWT.READ_ONLY);	
			combo.setEnabled(false);
			combo.add("POR");
			combo.add("RET");
			combo.add("SUR");
			combo.add("SUB");
			combo.add("CAT");
			combo.add("EXP");
			combo.select(0);
			combo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					String s = combo.getText();
					if(s.equals("POR")) {
						nodeType = NodeFunctionalTypesIF.NT_ACT_POR;
					} else if(s.equals("RET")) {
						nodeType = NodeFunctionalTypesIF.NT_ACT_RET;
					} else if(s.equals("SUR")) {
						nodeType = NodeFunctionalTypesIF.NT_ACT_SUR;
					} else if(s.equals("SUB")) {
						nodeType = NodeFunctionalTypesIF.NT_ACT_SUB;
					} else if(s.equals("CAT")) {
						nodeType = NodeFunctionalTypesIF.NT_ACT_CAT;
					} else if(s.equals("EXP")) {
						nodeType = NodeFunctionalTypesIF.NT_ACT_EXP;
					}
				}
			});

			// Show description on opening
			setErrorMessage(null);
			setMessage(null);
			setControl(topLevel);

		}

	}

	private NetFacadeIF net;
	private NodeSpaceModule space;
	private String newNodeID;
	boolean finished = false;

	private int nodeType = NodeFunctionalTypesIF.NT_CONCEPT;
	private NodeTypeSelectPage nodeTypeSelectPage;

	public NewNodeWizard(NetFacadeIF net, NodeSpaceModule space) {
		super();
		this.net = net;
		this.space = space;
		setHelpAvailable(false);
		setWindowTitle("Create a new Node in NodeSpace "+space.getEntityName());
		nodeTypeSelectPage = new NodeTypeSelectPage();
		addPage(nodeTypeSelectPage);	
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		if (!finished)
		try {
			finished = true;
			newNodeID = net.createNode(nodeType, space.getID()).getID();
			
			String entityName = ((NewNetEntityWizard.TypeSelectPage)nodeTypeSelectPage.getPreviousPage())
				.entityName.getText();
			if(!entityName.equals(""))
				net.changeParameter(NetParametersIF.PARM_ENTITY_NAME, newNodeID, 0, entityName);
	
			String comment = ((NewNetEntityWizard.TypeSelectPage)nodeTypeSelectPage.getPreviousPage())
				.entityComment.getText();
			AgentNetModelManager.getInstance().getNetModel().getModel(newNodeID).setComment(comment);
	
		} catch (MicropsiException e) {
			MindPlugin.getDefault().handleException(e);
			return false;
		}
				
		return true;
	}
	
	public String getNewNodeID() {
		return newNodeID;
	} 

	public boolean canCancel() {
		return !finished;
	}

	public boolean canFinish() {
		return true;
	}

	protected void validate() {
		IWizardContainer container = this.getContainer();
		if(container != null) {
			container.updateMessage();
			container.updateButtons();
		}
	}

}
