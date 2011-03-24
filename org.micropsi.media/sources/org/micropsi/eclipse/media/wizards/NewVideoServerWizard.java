package org.micropsi.eclipse.media.wizards;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardNode;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * 
 * 
 */
public class NewVideoServerWizard extends Wizard {

	class ServerTypeSelectPage extends WizardSelectionPage {

		private Composite topLevel;
		private Combo combo = null;
		private Text text = null;
		private IWizardNode cameraNode = null;
		private IWizardNode imageNode = null;
		private IWizardNode rtpNode = null;
		
		public ServerTypeSelectPage() {
			super("nodetypeselect");
			this.setTitle("Server type");
			this.setDescription("Select name and type of the new server");
			
			cameraNode = new IWizardNode() {

				CameraServerWizard wiz = new CameraServerWizard();
				
				public void dispose() {
					wiz.dispose();
				}

				public Point getExtent() {
					return new Point(-1,-1);
				}

				public IWizard getWizard() {
					return wiz;
				}

				public boolean isContentCreated() {
					return false;
				}
			};
			
			imageNode = new IWizardNode() {

				ImageServerWizard wiz = new ImageServerWizard();
				
				public void dispose() {
					wiz.dispose();
				}

				public Point getExtent() {
					return new Point(-1,-1);
				}

				public IWizard getWizard() {
					return wiz;
				}

				public boolean isContentCreated() {
					return false;
				}
			};
			
			rtpNode = new IWizardNode() {

				RTPClientServerWizard wiz = new RTPClientServerWizard();
				
				public void dispose() {
					wiz.dispose();
				}

				public Point getExtent() {
					return new Point(-1,-1);
				}

				public IWizard getWizard() {
					return wiz;
				}

				public boolean isContentCreated() {
					return false;
				}
			};

		}
		
		/**
		 * @see org.eclipse.jface.dialogs.DialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent) {

			// top level group
			topLevel = new Composite(parent,SWT.NONE);
			topLevel.setLayout(new GridLayout(2,false));
			topLevel.setFont(parent.getFont());

			Label label = new Label(topLevel,SWT.NONE);
			label.setText("Name");
			
			text = new Text(topLevel,SWT.SINGLE | SWT.BORDER);
			text.setText("view");
			text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					name = text.getText();
				}
			});
			
			label = new Label(topLevel,SWT.NONE);
			label.setText("Type");

			combo = new Combo(topLevel,SWT.DROP_DOWN | SWT.READ_ONLY);	
			combo.setEnabled(true);
			combo.add("Image");
			combo.add("Camera / Framegrabber");
			combo.add("RTP Client");
			//combo.add("iSight (Quicktime)");
			combo.select(0);
			
			setSelectedNode(imageNode);
			desciptorProvider = (IDescriptorProvider)imageNode.getWizard();
			
			combo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					selected = combo.getItem(combo.getSelectionIndex());
					if("Camera / Framegrabber".equals(selected)) {
						setSelectedNode(cameraNode);
						desciptorProvider = (IDescriptorProvider)cameraNode.getWizard();
					} else if("Image".equals(selected)) {
						setSelectedNode(imageNode);
						desciptorProvider = (IDescriptorProvider)imageNode.getWizard();
					} else if("RTP Client".equals(selected)) {
						setSelectedNode(rtpNode);
						desciptorProvider = (IDescriptorProvider)rtpNode.getWizard();
					} else {
						setSelectedNode(null);
						desciptorProvider = null;
					}
					validate();
				}	
			});
			

			// Show description on opening
			setErrorMessage(null);
			setMessage(null);
			setControl(topLevel);

		}

	}

	private IDescriptorProvider desciptorProvider = null;
	
	String name = "view";
	String selected = "Image";
	ServerTypeSelectPage serverTypeSelectPage;
	
	public NewVideoServerWizard() {
		super();
		setForcePreviousAndNextButtons(true);
		setHelpAvailable(false);
		setWindowTitle("Create a new video server");
		serverTypeSelectPage = new ServerTypeSelectPage();
		addPage(serverTypeSelectPage);	
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		return true;
	}
	
	public boolean canCancel() {
		return true;
	}

	public boolean canFinish() {
		return false;
	}
	
	protected void validate() {
		IWizardContainer container = this.getContainer();
		if(container != null) {
			container.updateMessage();
			container.updateButtons();
		}
	}

	public String getVideoServerDescriptor() {
		
		String type = "";
		if("Camera / Framegrabber".equals(selected)) {
			type = "camera";
		} else if("Image".equals(selected)) {
			type = "image";
		} else if("RTP Client".equals(selected)) {
			type = "net-rtp";
		} else {
			type = "unknown";
		}
		
		String descriptor = "";
		if(desciptorProvider != null) {
			descriptor = ","+desciptorProvider.provideDescriptor();
		}
		
		return "name="+name+",type="+type+descriptor;
	}

}
