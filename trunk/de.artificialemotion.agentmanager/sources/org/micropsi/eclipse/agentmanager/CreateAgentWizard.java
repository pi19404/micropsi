package org.micropsi.eclipse.agentmanager;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.micropsi.common.config.ConfigurationReaderFactory;
import org.micropsi.common.config.ConfigurationReaderIF;
import org.micropsi.comp.common.ComponentRunner;
import org.micropsi.comp.common.ComponentRunnerException;
import org.micropsi.eclipse.common.ProgressDialog;
import org.micropsi.eclipse.runtime.RuntimePlugin;

/**
 * 
 * 
 * 
 */
public class CreateAgentWizard extends Wizard {

	class CreatorPage extends WizardPage {
	
		private Composite topLevel;
		private List agentlist; 
		
		public CreatorPage() {
			super("creator");
			setTitle("Settings");
			setDescription("Select one of the agent configurations found in the file");
			
			
		}
	
		/**
		 * @see org.eclipse.jface.dialogs.DialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent) {
			
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			
			// top level group
			topLevel = new Composite(parent,SWT.NONE);
			topLevel.setLayout(layout);
			topLevel.setFont(parent.getFont());
			
			agentlist = new List(topLevel, SWT.BORDER | SWT.V_SCROLL);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.heightHint = 200;
			data.horizontalSpan = 2;
			agentlist.setLayoutData(data);
			updateAgentList();
			agentlist.addSelectionListener(new SelectionAdapter () {
				public void widgetSelected(SelectionEvent e) {
					selected = agents.get(agentlist.getSelectionIndex());
					validate();
				}
			});
			
			Label label = new Label(topLevel,SWT.NONE);
			label.setText("Number of new agents:");
			
			final Text number = new Text(topLevel,SWT.BORDER | SWT.SINGLE);
			number.setText("1");
			data = new GridData();
			data.widthHint = 50;
			number.setLayoutData(data);
			number.addModifyListener(new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					try {
						howMany = Integer.parseInt(number.getText());
					} catch (Exception exc) {
						howMany = 1;
					}
				}
			});
			
			setErrorMessage(null);
			setMessage(null);
			setControl(topLevel);						
					
		}
		
		public void updateAgentList() {
			agentlist.removeAll();
			for(int i=0;i<agents.size();i++)
				agentlist.add(agents.get(i));
		}
		
	}

	class ConfigSelectorPage extends WizardPage {
	
		private Composite topLevel;
		
		public ConfigSelectorPage() {
			super("configselect");
			setTitle("Configuration file");
			setDescription("Select the config file that contains the new agent's entry");					
		}
	
		/**
		 * @see org.eclipse.jface.dialogs.DialogPage#createControl(org.eclipse.swt.widgets.Composite)
		 */
		public void createControl(Composite parent) {
			
			GridLayout layout = new GridLayout();
			layout.numColumns = 3;
			layout.makeColumnsEqualWidth = false;
			
			// top level group
			topLevel = new Composite(parent,SWT.NONE);
			topLevel.setLayout(layout);
			topLevel.setFont(parent.getFont());
			topLevel.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			final Text file = new Text(topLevel,SWT.BORDER | SWT.SINGLE);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;
			file.setLayoutData(data);
			file.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					config = null;
					selected = null;
					agents.clear();
					setPageComplete(false);
					setErrorMessage("You must open and check the configfile");
					validate();
				}
			});
			
			Button select = new Button(topLevel,SWT.NONE);
			select.setText("Select File");
			select.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					
					String sep = System.getProperty("file.separator");
					
					FileDialog dlg = new FileDialog(getShell());
					dlg.setFilterExtensions(new String[] {"*.xml"});
					dlg.open();
					file.setText(dlg.getFilterPath()+sep+dlg.getFileName());
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			
			Button check = new Button(topLevel,SWT.NONE);
			check.setText("Open and check");
			check.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					openConfigFile(file.getText());
					if(config != null) {
						creator.updateAgentList();
						setErrorMessage(null);
						setPageComplete(true);
						validate();
					}
				}

				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
									
			setErrorMessage(null);
			setMessage(null);
			setControl(topLevel);
		}
		
		
		private void openConfigFile(String file) {
			
			try {
				ConfigurationReaderIF cfg = ConfigurationReaderFactory.getConfigReader(
					file,
					ComponentRunner.getInstance().getGlobalVariables(),
					ConfigurationReaderFactory.CONFIG_XML);
					
					
				Iterator<String> cfgValues = cfg.getConfigurationValues("config.runner.components").iterator();
				while(cfgValues.hasNext()) {
					String componentName = cfgValues.next();
					String c = cfg.getConfigValue("config.component:id="+componentName+".class");
					if(c.lastIndexOf("AgentFrameworkComponent") > 0) {
						agents.add(componentName);
					}
				}	
				config = cfg;							
			} catch (Exception e) {
				setErrorMessage(AgentManagerPlugin.getDefault().handleException(e));
				config = null;
				selected = null;
				agents.clear();
				setPageComplete(false);
				validate();
			}
			
		}
	}

	ConfigurationReaderIF config;
	String selected = null;
	int howMany = 1;
	ArrayList<String> agents = new ArrayList<String>();

	private ConfigSelectorPage configSelector;
	private CreatorPage creator;
		
	public CreateAgentWizard() {
		super();

		RuntimePlugin.getDefault();
		
		setHelpAvailable(false);
		setWindowTitle("Create Agent");

		configSelector = new ConfigSelectorPage();
		addPage(configSelector);
		creator = new CreatorPage(); 
		addPage(creator);	
	}
	
	public boolean canFinish() {
		
		if(config != null && selected != null) return true;
				
		return false;
	}
	
	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		
		final ProgressDialog prg = new ProgressDialog("Creating agents...",getShell());
		prg.beginTask("Creating "+howMany+" agents");
		
		new Thread() {
			
			public void run() {

				try {
					
					for(int i=0;i<howMany;i++) {
			
						prg.reportProgress(i+1, howMany, "Creating agent "+(i+1)+" of "+howMany);
						ComponentRunner.getInstance().createComponent(
							selected, 
							config, 
							null, 
							true);
			
					}
			
					prg.endTask();
							
				} catch (ComponentRunnerException e) {
					AgentManagerPlugin.getDefault().handleException(e);
				}
			
			}
			
		}.start();
				
		prg.setBlockOnOpen(true);
		prg.open();			
				
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
