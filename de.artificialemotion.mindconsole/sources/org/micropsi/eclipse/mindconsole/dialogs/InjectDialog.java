package org.micropsi.eclipse.mindconsole.dialogs;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import org.micropsi.eclipse.mindconsole.jdt.ModuleJavaManager;

/**
 * 
 * 
 * 
 */
public class InjectDialog extends Dialog {
		
		
	IProject[] projects;
	String selected = null;
	
	public InjectDialog(Shell shell)  {
		super(shell);	
		projects = ModuleJavaManager.getInstance().getJavaProjects();
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		topLevel.setFont(parent.getFont());
				
		Label label = new Label(topLevel,SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText(	"Select the project where the java sources\n"+
						"of your native modules are to be placed:");
						
		final Button newButton = new Button(topLevel,SWT.RADIO);
		newButton.setText("Create a new project and ask again");
		newButton.setSelection(projects.length == 0);
				
		final Button exButton = new Button(topLevel,SWT.RADIO);
		exButton.setText("Select an existing project");
		exButton.setSelection(projects.length > 0);
	
		final List list = new List(topLevel,SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		list.setLayoutData(new GridData(GridData.FILL_BOTH));
		for(int i=0;i<projects.length;i++) {
			list.add(projects[i].getName());
		}
		list.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selected = list.getItem(list.getSelectionIndex());
			}			
		});
		if(list.getItemCount() > 0) {
			list.select(0);
			selected = list.getItem(0);
		}

		newButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selected = null;
				list.setEnabled(false);
			}			
		});

		exButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				list.setEnabled(true);
				if(list.getItemCount() > 0)
					list.select(0);				
			}			
		});
				
		return topLevel;
	}
		
	protected void okPressed() {
		super.okPressed();	
	}
	
	public String getSelected() {
		return selected;
	}

}
