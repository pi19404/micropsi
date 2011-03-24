package org.micropsi.eclipse.console.dialogs;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.micropsi.eclipse.console.IParameterMonitor;
import org.micropsi.eclipse.console.internal.ParameterMonitorRegistry;

/**
 * 
 * 
 * 
 */
public class AddMonitorDialog extends Dialog {
	
	private String initialselection;
	private Color selCol = new Color(null,255,0,0);
	private String selText = "";
	private IParameterMonitor selMon = null;
	private ParameterMonitorRegistry registry;
	
	public AddMonitorDialog(Shell shell, ParameterMonitorRegistry registry)  {
		super(shell);
		this.registry = registry;
	}
	
	protected Control createDialogArea(final Composite parent) {
		Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		topLevel.setFont(parent.getFont());
		
		Label label = new Label(topLevel,SWT.NONE);
		label.setText("Select the parameter monitor to add:");
		
		final ListViewer list = new ListViewer(topLevel);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 200;
		data.heightHint = 200;
		list.getList().setLayoutData(data);
		
		list.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return ((IParameterMonitor)element).getID(); 
			}
		});
		
		list.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				if(inputElement == null) return new Object[0];
				return ((ArrayList)inputElement).toArray();
			}

			public void dispose() {
			}
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
		});
		
		label = new Label(topLevel,SWT.NONE);
		label.setText("Display color will be:");
		
		final Composite colcomp = new Composite(topLevel,SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 16;
		colcomp.setLayoutData(data);
		colcomp.setBackground(selCol);
				
		Button changebut = new Button(topLevel,SWT.PUSH);
		changebut.setText("Change color");
		changebut.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ColorDialog cdl = new ColorDialog(parent.getShell());
				cdl.setText("Select display color");
				cdl.open();
				selCol.dispose();
				selCol = new Color(getShell().getDisplay(),cdl.getRGB());
				colcomp.setBackground(selCol);
			}
		});
		
		final Text display = new Text(topLevel,SWT.SINGLE | SWT.BORDER);
		display.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		display.setText("");
		
		display.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				selText = display.getText();	
			}
		});
		
		list.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if(list.getList().getSelectionIndex() < 0) {
					selMon = null;
					return;
				}
				selMon = (IParameterMonitor)list.getElementAt(list.getList().getSelectionIndex());
				display.setText(selMon.getID());
				selText = selMon.getID();
			}			
		});
		
		list.setInput(registry.getParameterMonitors());
		
		String[] items = list.getList().getItems();
		if(items == null) return topLevel;
		for(int i=0;i<items.length;i++) {
			if(items[i].equals(initialselection)) {
				list.getList().select(i);
				selMon = (IParameterMonitor)list.getElementAt(i);
				display.setText(selMon.getID());
				selText = selMon.getID();				
				break;
			}
		}
		
		return topLevel;
	}
	
	protected void okPressed() {
		super.okPressed();	
	}

	/**
	 * @return
	 */
	public Color getSelCol() {
		return selCol;
	}

	/**
	 * @return
	 */
	public IParameterMonitor getSelMon() {
		return selMon;
	}

	/**
	 * @return
	 */
	public String getSelDis() {
		return selText;
	}

	/**
	 * @param id
	 */
	public void setInitialSelection(String id) {
		this.initialselection = id;
	}

}
