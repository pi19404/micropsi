package org.micropsi.eclipse.media.wizards;

import java.util.Iterator;

import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.micropsi.media.VideoServerRegistry;

public class CameraServerWizard extends Wizard implements IDescriptorProvider {

	class CameraPropertiesPage extends WizardSelectionPage {

		private Composite topLevel;
		private Combo deviceCombo = null;
		private Combo formatCombo = null;
		
		public CameraPropertiesPage() {
			super("camerawizward");
			this.setTitle("Camera / Frame grabber properties");
			this.setDescription("Select camera properties");
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
			label.setText("Capture device");
			
			deviceCombo = new Combo(topLevel,SWT.DROP_DOWN | SWT.READ_ONLY);	
			deviceCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			deviceCombo.setEnabled(true);
			
			Iterator<String> cd = VideoServerRegistry.getInstance().enumerateJMFVideoCaptureDevices();
			while(cd.hasNext()) {
				deviceCombo.add(cd.next());
			}
			deviceCombo.select(0);
			
			label = new Label(topLevel,SWT.NONE);
			label.setText("Format");
			
			formatCombo = new Combo(topLevel,SWT.DROP_DOWN | SWT.READ_ONLY);	
			formatCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			formatCombo.setEnabled(true);
			
			deviceCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					device = deviceCombo.getText();
					fillFormatList();
				}				
			});
			fillFormatList();
			
			formatCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					format = formatCombo.getText();
					validate();
				}				
			});
			
			// Show description on opening
			setErrorMessage(null);
			setMessage(null);
			setControl(topLevel);

		}
		
		private void fillFormatList() {
			formatCombo.removeAll();
			if(deviceCombo.getSelectionIndex() >= 0) {
				Iterator<String> f = VideoServerRegistry.getInstance().enumerateJMFVideoFormats(deviceCombo.getItem(deviceCombo.getSelectionIndex()));
				while(f.hasNext()) {
					formatCombo.add(f.next());
				}
				formatCombo.select(0);
			}
			validate();
		}

	}
	
	CameraPropertiesPage cameraPropertiesPage;
	String device;
	String format;
	
	public CameraServerWizard() {
		super();
		setHelpAvailable(false);
		setWindowTitle("Camera Properties");
		cameraPropertiesPage = new CameraPropertiesPage();
		addPage(cameraPropertiesPage);	
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
		if(	device != null &&
			format != null) {
			
			return true;
		}		
		return false;
	}
	
	protected void validate() {
		IWizardContainer container = this.getContainer();
		if(container != null) {
			container.updateMessage();
			container.updateButtons();
		}
	}

	public String provideDescriptor() {
		return "devicename="+device+","+format;
	}

}
