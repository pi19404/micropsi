package org.micropsi.eclipse.mindconsole.wizards;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.micropsi.eclipse.mindconsole.MindPlugin;
import org.micropsi.eclipse.mindconsole.jdt.ModuleJavaManager;
import org.micropsi.eclipse.model.net.AgentNetModelManager;
import org.micropsi.nodenet.LocalNetFacade;
import org.micropsi.nodenet.NetFacadeIF;
import org.micropsi.nodenet.NetParametersIF;
import org.micropsi.nodenet.NodeSpaceModule;

/**
 *
 *
 *
 */
public class NewNativeModuleWizard extends Wizard {

	class ImplSelectPage extends WizardPage {

		private Composite topLevel;
		private ListViewer projectList;
		private ListViewer modulesList;
		private ProgressMonitorPart	monitor;
		private Button defiant;
		
		private IType selectedImpl = null;
	
		public ImplSelectPage() {
			super("implselect");
			this.setTitle("NativeModule");
			this.setDescription("Select the implementation for the NativeModule");
		}
		
		private void setSelectedProject(String project) {
//			selectedProject = project;
			modulesList.setInput(project);	
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
			label.setText("Select Project");
			
			projectList = new ListViewer(topLevel,SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
			projectList.getList().setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_VERTICAL));
			projectList.setContentProvider(new IStructuredContentProvider() {
				
				public Object[] getElements(Object inputElement) {
					return ModuleJavaManager.getInstance().getJavaProjects();
				}
				
				public void dispose() {
				}
				
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}
				
			});
						
			projectList.setLabelProvider(new LabelProvider() {
				public String getText(Object element) {
					return ((IProject)element).getName();
				}
			});
			
			projectList.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
/*					if(modulesList.getList().getSelectionCount() == 0) {
						setSelectedProject("");
						return;
					}*/ 
					setSelectedProject(projectList.getList().getSelection()[0]);
				}
				
			});
		
			projectList.setInput("");
			
			monitor = new ProgressMonitorPart(topLevel, new GridLayout());
			monitor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_VERTICAL));

			label = new Label(topLevel,SWT.NONE);
			label.setText("Select Module implementation");
			
			modulesList = new ListViewer(topLevel,SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
			modulesList.getList().setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_VERTICAL));
			modulesList.setContentProvider(new IStructuredContentProvider() {
				
				public Object[] getElements(Object inputElement) {
					List<IType> modules = ModuleJavaManager.getInstance().getModuleImplementationsInProject((String)inputElement,monitor);
					Object[] toReturn = new Object[modules.size()]; 
					for(int i=0;i<modules.size();i++)
						toReturn[i] = modules.get(i);
					return toReturn;
				}
				
				public void dispose() {
				}
				
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}
				
			});
						
/*			modulesList.setLabelProvider(new LabelProvider() {
				public String getText(Object element) {
					IJavaElement je = (IJavaElement)element;
					if(je.getElementType() == IJavaElement.COMPILATION_UNIT) 
						return je.getElementName()+" (editable)";
					if(je.getElementType() == IJavaElement.CLASS_FILE)
						return je.getElementName();
					return "-unknown-";
				}
			});*/
			modulesList.setLabelProvider(new JavaElementLabelProvider());
			
			
			modulesList.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					classIsGood = (modulesList.getList().getSelectionCount() > 0);
					if(classIsGood) {
						selectedImpl = (IType)modulesList.getElementAt(
							modulesList.getList().getSelectionIndex()
						);
					}						
					validate();
				}
			});
					
			modulesList.setInput("");
			
			defiant = new Button(topLevel,SWT.CHECK);
			defiant.setText("Defiant");
			defiant.setSelection(true);

			// Show description on opening
			setErrorMessage(null);
			setMessage(null);
			setControl(topLevel);

		}

		public IType getModuleImplementation() {
			return selectedImpl;
		}

	}

	private NetFacadeIF net;
	private NodeSpaceModule space;
	
	private boolean classIsGood;

	private ImplSelectPage implSelectPage;

	public NewNativeModuleWizard(NetFacadeIF net, NodeSpaceModule space) {
		super();
		this.net = net;
		this.space = space;
		setHelpAvailable(false);
		setWindowTitle("Create a new NativeModule in NodeSpace "+space.getEntityName());
		implSelectPage = new ImplSelectPage();
		addPage(implSelectPage);
	}

	/**
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		
		IType implementation = implSelectPage.getModuleImplementation(); 

		try {
			String entityID = net.createNativeModule(null, space.getID(), implSelectPage.defiant.getSelection()).getID();
		
			String entityName = ((NewNetEntityWizard.TypeSelectPage)implSelectPage.getPreviousPage())
				.entityName.getText();
			
			if(!entityName.equals(""))	
				net.changeParameter(NetParametersIF.PARM_ENTITY_NAME, entityID, 0, entityName);

			((LocalNetFacade)net).replaceNativeModuleImplementation(
				entityID, 
				ModuleJavaManager.getInstance().getNewJDTClassLoader(), 
				implementation.getFullyQualifiedName()
			);

			String comment = ((NewNetEntityWizard.TypeSelectPage)implSelectPage.getPreviousPage())
				.entityComment.getText();			
			AgentNetModelManager.getInstance().getNetModel().getModel(entityID).setComment(comment);

		} catch (Exception e) {
			MindPlugin.getDefault().handleException(e);
			return false;
		}
		return true;
	}

	public boolean canFinish() {
		return classIsGood;
	}

	protected void validate() {	
		IWizardContainer container = this.getContainer();
		if(container != null) {
			container.updateMessage();
			container.updateButtons();
		}
	}
	
}
