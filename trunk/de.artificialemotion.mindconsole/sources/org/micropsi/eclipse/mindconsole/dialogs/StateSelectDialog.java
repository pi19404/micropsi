package org.micropsi.eclipse.mindconsole.dialogs;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.model.net.StateRepositoryModel;

/**
 * 
 * 
 * 
 */
public class StateSelectDialog extends Dialog {
	
	private String text;
	private String selection;
	private StateRepositoryModel model;
	
	List list;
	
	public StateSelectDialog(Shell shell, String text, StateRepositoryModel model) throws MicropsiException {
		super(shell);
		this.text = text;
		this.model = model;
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		topLevel.setFont(parent.getFont());
		
		Label label = new Label(topLevel,SWT.NONE);
		label.setText(text); 
		
		final Button newButton = new Button(topLevel,SWT.RADIO);
		newButton.setText("Create new");
		newButton.setSelection(false);
				
		final Button exButton = new Button(topLevel,SWT.RADIO);
		exButton.setText("Select existing");
		exButton.setSelection(true);
		
		final Group statesGroup = new Group(topLevel, SWT.NONE);
		statesGroup.setText("Existing states");
		statesGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = 2;
		statesGroup.setLayout(groupLayout);
		
		list = new List(statesGroup,SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		list.setLayoutData(new GridData(GridData.FILL_BOTH));
		refreshList();
		
		final Composite rightSide = new Composite(statesGroup,SWT.NONE);
		rightSide.setLayoutData(new GridData(GridData.FILL_BOTH));
		rightSide.setLayout(new GridLayout());
		
		Button renameButton = new Button(rightSide,SWT.NONE);
		renameButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		renameButton.setText("Rename");

		Button deleteButton = new Button(rightSide,SWT.NONE);
		deleteButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		deleteButton.setText("Delete");

		newButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				statesGroup.setEnabled(exButton.getSelection());
				selection = null;
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		exButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				statesGroup.setEnabled(exButton.getSelection());
				if(list.getItemCount() > 0) {
					list.select(0);
					selection = list.getItem(0);
				}
				else
					selection = null;
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		renameButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if(selection == null) return;
				StateInputDialog d = new StateInputDialog(
					list.getShell(),
					selection,
					model.getList()
				);
				d.open();
								
				model.renameState(selection,d.getValue());
				refreshList();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		deleteButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if(selection == null) return;
				model.deleteState(selection);
				refreshList();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		list.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				if(list.getSelectionIndex() < 0) selection = null;
				selection = list.getItem(list.getSelectionIndex());
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		list.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				okPressed();
			}			
		});
		 
		return topLevel;
	}
	
	private void refreshList() {
		list.removeAll();
		ArrayList l = model.getList();
		for(int i=0;i<l.size();i++)
			list.add((String)l.get(i));
	}
	
	protected void okPressed() {			
		super.okPressed();	
	}
	
	public String getSelectedState() {
		return selection;
	}

}
