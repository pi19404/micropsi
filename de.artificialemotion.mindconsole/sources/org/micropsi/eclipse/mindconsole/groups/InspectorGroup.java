package org.micropsi.eclipse.mindconsole.groups;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.eclipse.model.net.EntityModel;
import org.micropsi.nodenet.NetEntityTypesIF;

/**
 * 
 * 
 * 
 */
public class InspectorGroup {
	
	class StatesLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			MTreeNode node = (MTreeNode)element;
			switch(columnIndex) {
				case 0: return node.getName();
				case 1: return node.getValue();
				default: return null;
			}
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}

	}
	
		
	class StatesContentProvider implements IStructuredContentProvider {
		
		public void dispose() {
		}
		
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {						
		}
		
		public Object[] getElements(Object inputElement) {
			if(inputElement == null) return new Object[0];
			
			MTreeNode root = (MTreeNode)inputElement;
			MTreeNode[] children = root.getChildren();
			
			if(children == null) return new Object[0];
			
			Object[] toReturn = new Object[children.length];
			
			for(int i=0;i<children.length;i++)
				toReturn[i] = children[i];						
						
			return toReturn;			
		}
	}
		
	private TableViewer tableviewer;
	private Label status;
	private EntityModel entity;
	
	public InspectorGroup(Composite parent, GridData gridData) {
		createControls(parent, gridData);
	}
	
	public InspectorGroup(Composite parent, GridData gridData, EntityModel entity) {
		createControls(parent, gridData);
		setInput(entity);
	}
			
	private void createControls(Composite parent, GridData gridData) {
		final Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(gridData);
		topLevel.setFont(parent.getFont());
		
		status = new Label(topLevel,SWT.NONE);
		status.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		status.setText("No module selected");
		
		tableviewer = new TableViewer(topLevel);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 200;
		data.widthHint = 200;
		tableviewer.getTable().setLayoutData(data);
		
		tableviewer.getTable().setLinesVisible(true);
		
		new TableColumn(tableviewer.getTable(),SWT.NONE).setWidth(80);
		new TableColumn(tableviewer.getTable(),SWT.NONE).setWidth(120);
			
		tableviewer.setContentProvider(new StatesContentProvider());
		tableviewer.setLabelProvider(new StatesLabelProvider());
		
		String[] cprop = {"eins","zwei"};
		tableviewer.setColumnProperties(cprop);
		
		CellEditor[] cellEditors = {null,new TextCellEditor(tableviewer.getTable())};
		tableviewer.setCellEditors(cellEditors);
		
		tableviewer.setCellModifier(new ICellModifier() {
			
			public boolean canModify(Object element, String property) {				
				return property.equals("zwei");
			}
			
			public Object getValue(Object element, String property) {
				if(property.equals("zwei"))
					return ((MTreeNode)element).getValue();
				else
					return null;
			}
			
			public void modify(Object element, String property, Object value) {			
				if(property.equals("zwei")) {
					entity.changeInnerState(((TableItem)element).getText(),(String)value);
					((MTreeNode)((TableItem)element).getData()).setValue((String)value);
					
					((TableItem)element).setText(1, (String)value);	
				}
			}
			
		});	
		
/*		Menu menu = new Menu(topLevel.getShell(),SWT.POP_UP);
		tableviewer.getTable().setMenu(menu);
		
		MenuItem item = new MenuItem(menu,SWT.CASCADE);
		item.setText("Create monitor for this");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(tableviewer.getTable().getSelectionIndex() < 0) return;
				MTreeNode node = (MTreeNode)tableviewer.getElementAt(tableviewer.getTable().getSelectionIndex());
							
				try {
					NativeModuleMonitor m = new NativeModuleMonitor(
						NetModelManager.getNetModel(),
						entity.getEntity().getID(),
						node.getName()
					);
					
					ParameterMonitorRegistry.getInstance().registerParameterMonitor(m);
					
				} catch (MicropsiException exc) {
					EclipseConsole.getInstance().handleException(exc);
				}
							
			}			
		});*/
		
		tableviewer.getTable().setEnabled(false);		
	}
	
	public void setInput(EntityModel entity) {
		this.entity = entity;
		
		if(entity == null) {
			status.setText("No module selected");
			tableviewer.getTable().setEnabled(false);
			return;
		}
		
		if(entity.getEntity().getEntityType() != NetEntityTypesIF.ET_MODULE_NATIVE) {
			status.setText("Entity "+entity.getEntity().getID()+" is not a NativeModule");
			tableviewer.getTable().setEnabled(false);
			return;			
		}
		
		MTreeNode innerstates = entity.getInnerStates(); 
		
		if(innerstates == null) {
			status.setText("Module doesn't have inner states.");
			tableviewer.getTable().setEnabled(false);
		}
		
		status.setText("Inner states of module: "+entity.getEntity().getEntityName());
		
		tableviewer.getTable().setEnabled(true);	
		tableviewer.setInput(innerstates);	
	}
		
}
