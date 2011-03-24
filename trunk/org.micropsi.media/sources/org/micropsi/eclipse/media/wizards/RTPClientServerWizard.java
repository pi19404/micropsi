package org.micropsi.eclipse.media.wizards;

import java.net.InetAddress;

import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class RTPClientServerWizard extends Wizard implements IDescriptorProvider {

	class RTPClientPropertiesPage extends WizardSelectionPage {

		private Composite topLevel;
		private Text ip = null;
		private Text port = null;
		private Text size = null;
		
		public RTPClientPropertiesPage() {
			super("rtpwizward");
			this.setTitle("RTP client properties");
			this.setDescription("Specify the RTP session");
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
			label.setText("IP");
			
			ip = new Text(topLevel,SWT.SINGLE | SWT.BORDER);	
			ip.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			ip.setText("239.60.60.60");
			ip.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					ipStr = ip.getText();
					validate();
				}
			});
			
			label = new Label(topLevel,SWT.NONE);
			label.setText("Port");
			
			port = new Text(topLevel,SWT.SINGLE | SWT.BORDER);	
			port.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			port.setText("51372");
			port.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					portStr = port.getText();
					validate();
				}
			});

			
			label = new Label(topLevel,SWT.NONE);
			label.setText("Size");
			
			size = new Text(topLevel,SWT.SINGLE | SWT.BORDER);	
			size.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			size.setText("320x240");
			size.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					sizeStr = size.getText();
					validate();
				}
			});
			
			// Show description on opening
			setErrorMessage(null);
			setMessage(null);
			setControl(topLevel);
		}
	}
	
	RTPClientPropertiesPage rtpClientPropertiesPage;
	private String ipStr = "239.60.60.60";
	private String portStr = "51372";
	private String sizeStr = "320x240";
	
	public RTPClientServerWizard() {
		super();
		setHelpAvailable(false);
		setWindowTitle("RTP client Properties");
		rtpClientPropertiesPage = new RTPClientPropertiesPage();
		addPage(rtpClientPropertiesPage);	
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
		
		try {
			InetAddress.getByName(ipStr);
			Integer.parseInt(portStr);
			
			Integer.parseInt(sizeStr.substring(0,sizeStr.indexOf('x')));
			Integer.parseInt(sizeStr.substring(sizeStr.indexOf('x')+1));
		} catch (Exception e) {
			return false;
		}
				
		return true;
	}
	
	protected void validate() {
		IWizardContainer container = this.getContainer();
		if(container != null) {
			container.updateMessage();
			container.updateButtons();
		}
	}

	public String provideDescriptor() {
		return "devicename="+ipStr+":"+portStr+",size="+sizeStr;
	}

}
