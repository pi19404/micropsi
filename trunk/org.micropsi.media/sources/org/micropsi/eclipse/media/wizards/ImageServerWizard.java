package org.micropsi.eclipse.media.wizards;

import java.io.File;

import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardSelectionPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ImageServerWizard extends Wizard implements IDescriptorProvider {
	
	class ImagePropertiesPage extends WizardSelectionPage {

		private Composite topLevel;
		private Text image = null;
		
		public ImagePropertiesPage() {
			super("imagewizward");
			this.setTitle("Image properties");
			this.setDescription("Select the image");
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
			label.setText("Image");
			
			image = new Text(topLevel,SWT.SINGLE | SWT.BORDER);	
			image.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			image.setEnabled(false);
			
			Button button = new Button(topLevel, SWT.PUSH);
			button.setText("Select file");
			button.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					FileDialog fd = new FileDialog(e.display.getActiveShell());
					fd.setFilterExtensions(new String[] {"*.jpg"});
					fd.open();
					String fileName = fd.getFilterPath()+"/"+fd.getFileName();
					if(fileName != null) {
						image.setText(fileName);
						imageName = fileName;
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
	
	private String imageName;
	ImagePropertiesPage imagePropertiesPage;
	
	public ImageServerWizard() {
		super();
		setHelpAvailable(false);
		setWindowTitle("Image Properties");
		imagePropertiesPage = new ImagePropertiesPage();
		addPage(imagePropertiesPage);	
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
		String filename = imagePropertiesPage.image.getText();
		File f = new File(filename);
		if(!f.exists() || !f.canRead() || f.isDirectory()) return false;
		
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
		Image img = new Image(Display.getDefault(),imageName);
		String size = img.getBounds().width+"x"+img.getBounds().height;
		img.dispose();
		return "devicename="+imageName+",size="+size+",framerate=-1";
	}
	
	
}
