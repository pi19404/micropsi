package org.micropsi.eclipse.mindconsole.dialogs;

import java.util.ArrayList;
import java.util.Iterator;

import org.micropsi.nodenet.NetEntity;
import org.micropsi.nodenet.NetFacadeIF;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * 
 * 
 */
public class LookupDialog extends Dialog {
	
	private NetFacadeIF net;
	private String currentSpace;
	private Object[] elements;
	String returnid = null;
	String returnparent = null;
		
	public LookupDialog(Shell shell, NetFacadeIF net, String currentSpace)  {
		super(shell);
		this.net = net;
		this.currentSpace = currentSpace;
				
		Iterator<NetEntity> iter = net.getAllEntities();
		ArrayList<NetEntity> elist = new ArrayList<NetEntity>();
		while(iter.hasNext()) elist.add(iter.next());
		elements = elist.toArray();
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		topLevel.setFont(parent.getFont());
				
		final Text searchField = new Text(topLevel,SWT.SINGLE | SWT.BORDER);
		searchField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchField.setFocus();
		
		Label label = new Label(topLevel,SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText("You are in nodespace: "+currentSpace);
		
		final Label info = new Label(topLevel,SWT.NONE);
		info.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		info.setText("Select an entity or enter your search");
		
		final ListViewer resList = new ListViewer(topLevel,SWT.BORDER | SWT.V_SCROLL);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 200;
		resList.getList().setLayoutData(data);
		resList.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				return elements;
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}			
		});
		
		resList.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				NetEntity e = (NetEntity)element;
				return e.getID() + (e.hasName() ? " ("+e.getEntityName()+")" : ""); 
			}
		});
		
		resList.setInput(net);
		
		resList.addFilter(new ViewerFilter() {
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				NetEntity e = (NetEntity)element;
				if(e.getID().startsWith(searchField.getText())) return true;
				if(e.hasName() && e.getEntityName().startsWith(searchField.getText())) return true;
				return false;
			}
			
		});	
		
		searchField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				getButton(OK).setEnabled(false);
				resList.refresh();
				if(resList.getList().getItemCount() == 1) {
					resList.getList().select(0);
					NetEntity entity = (NetEntity)resList.getElementAt(resList.getList().getSelectionIndex());
					returnid = entity.getID();
					returnparent = entity.getParentID();
					String infotext = (	currentSpace.equals(entity.getParentID()) ?
										"Selected item is in the same space" :
										"Selected item is in space: "+entity.getParentID());
					info.setText(infotext);
					getButton(OK).setEnabled(true);
				}
			}
		});
		
		resList.getList().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				NetEntity entity = (NetEntity)resList.getElementAt(resList.getList().getSelectionIndex());
				searchField.setText(entity.getID());
				returnid = entity.getID();
				returnparent = entity.getParentID();
				String infotext = (	currentSpace.equals(entity.getParentID()) ?
									"Selected item is in the same space" :
									"Selected item is in space: "+entity.getParentID());
				info.setText(infotext);
				getButton(OK).setEnabled(true);
			}
		});
				
		return topLevel;
	}
	
	public String getSelectedID() {
		return returnid;
	}
	
	public String getSelectedParent() {
		return returnparent;
	}	
	
	protected void okPressed() {
		super.okPressed();	
	}

}
