package org.micropsi.eclipse.mindconsole.wizards;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.model.net.NetModel;
import org.micropsi.nodenet.NetEntityTypesIF;
import org.micropsi.nodenet.NetFacadeIF;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * 
 * 
 */
public class NewNetEntityWizard extends Wizard {

	class TypeSelectPage extends WizardSelectionPage {
	
		private Composite topLevel;
//		private int neType = NetEntityTypesIF.ET_NODE;
		private int initialSelection = -1;

		private Button nodeRadio;
		private	Button spaceRadio;
		private Button nativeRadio;

		protected Text entityName;
		protected Text entityComment;

		private final NetFacadeIF fnet;
		private final NodeSpaceModule fspace; 		
		
		private IWizardNode nodeWizardNode;
		private IWizardNode nativeModuleWizardNode;
		private IWizardNode	nodeSpaceWizardNode;
	
		public TypeSelectPage(NetFacadeIF net, NodeSpaceModule space, int initialSelection) {
			super("typeselect");
			this.setTitle("NetEntity");
			this.setDescription("Select type, name and comment for the new NetEntity");
			
			this.initialSelection = initialSelection;
			this.fnet = net;
			this.fspace = space;
			
			nodeWizardNode = new IWizardNode() {
				
				NewNodeWizard newNodeWiz;

				public void dispose() {
					if(newNodeWiz != null) newNodeWiz.dispose();
			   	}

			   	public Point getExtent() {
					return new Point(-1,-1);
			   	}

			   	public IWizard getWizard() {
				   if(newNodeWiz == null) newNodeWiz = new NewNodeWizard(fnet, fspace);
				   return newNodeWiz;
			   	}

			   	public boolean isContentCreated() {
				   return (newNodeWiz != null);
			   	}
			};

			nativeModuleWizardNode = new IWizardNode() {

				NewNativeModuleWizard newNativeModuleWiz;

				public void dispose() {
					if(newNativeModuleWiz != null) newNativeModuleWiz.dispose();
				}

				public Point getExtent() {
					return new Point(-1,-1);
				}

				public IWizard getWizard() {
				   if(newNativeModuleWiz == null) newNativeModuleWiz = new NewNativeModuleWizard(fnet, fspace);
				   return newNativeModuleWiz;
				}

				public boolean isContentCreated() {
				   return (newNativeModuleWiz != null);
				}
			};
			
			nodeSpaceWizardNode = new IWizardNode() {

				SlotGateWizard slotGateWiz;

				public void dispose() {
					if(slotGateWiz != null) slotGateWiz.dispose();
				}

				public Point getExtent() {
					return new Point(-1,-1);
				}

				public IWizard getWizard() {
				   if(slotGateWiz == null) slotGateWiz = new SlotGateWizard(fnet, fspace.getID());
				   return slotGateWiz;
				}

				public boolean isContentCreated() {
				   return (slotGateWiz != null);
				}
			};			
	
			setSelectedNode(nodeWizardNode);
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
			
			nodeRadio = new Button(topLevel,SWT.RADIO);
			nodeRadio.setText("Node");
			nodeRadio.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setSelectedNode(nodeWizardNode);
//					neType = NetEntityTypesIF.ET_NODE;
					validate();
				}
			});			
			
			spaceRadio = new Button(topLevel,SWT.RADIO);
			spaceRadio.setText("NodeSpaceModule");
			spaceRadio.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setSelectedNode(nodeSpaceWizardNode);
//					neType = NetEntityTypesIF.ET_MODULE_NODESPACE;
					validate();
				}
			});

			
			nativeRadio = new Button(topLevel,SWT.RADIO);
			nativeRadio.setText("NativeModule");
			nativeRadio.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					setSelectedNode(nativeModuleWizardNode);
//					neType = NetEntityTypesIF.ET_MODULE_NATIVE;
					validate();
				}
			});			

			//new Label(topLevel,SWT.SEPARATOR | SWT.HORIZONTAL);
			
			label = new Label(topLevel,SWT.NONE);
			label.setText("Name:");
			
			entityName = new Text(topLevel,SWT.SINGLE | SWT.BORDER);
			entityName.setTextLimit(30);
			entityName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			//new Label(topLevel,SWT.SEPARATOR | SWT.HORIZONTAL);
			
			label = new Label(topLevel,SWT.NONE);
			label.setText("Comment:");

			entityComment = new Text(topLevel,SWT.MULTI | SWT.BORDER | SWT.WRAP);
			entityComment.setText("");
			entityComment.setTextLimit(300);
			entityComment.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
			
			// Show description on opening
			setErrorMessage(null);
			setMessage(null);
			setControl(topLevel);
			
			switch(initialSelection) {
				case NetEntityTypesIF.ET_NODE:
					setSelectedNode(nodeWizardNode);
//					neType = NetEntityTypesIF.ET_NODE;
					nodeRadio.setSelection(true);
					break;
				case NetEntityTypesIF.ET_MODULE_NODESPACE:
					setSelectedNode(null);
//					neType = NetEntityTypesIF.ET_MODULE_NODESPACE;
					spaceRadio.setSelection(true);
					break;
				case NetEntityTypesIF.ET_MODULE_NATIVE:
					setSelectedNode(nativeModuleWizardNode);
//					neType = NetEntityTypesIF.ET_MODULE_NATIVE;
					nativeRadio.setSelection(true);
					break;
			}

		}
		
	}

	private NetFacadeIF net;
	private TypeSelectPage typeSelector;
		
	public NewNetEntityWizard(NetModel netmodel, String spaceID, int initialSelection) throws MicropsiException {
		super();
		this.net = netmodel.getNet();
		NodeSpaceModule space = netmodel.getNet().getNodeSpaceModule(spaceID);
		setHelpAvailable(false);
		setWindowTitle("Create a new NetEntity in NodeSpace "+space.getEntityName());
		setForcePreviousAndNextButtons(true);
		
		typeSelector = new TypeSelectPage(net,space,initialSelection); 
		addPage(typeSelector);		
	}
	
	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		return false;
	}
	
	protected void validate() {
		IWizardContainer container = this.getContainer();
		if(container != null) {
			container.updateMessage();
			container.updateButtons();
		}
	}
}
