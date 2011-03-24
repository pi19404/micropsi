package org.micropsi.eclipse.mindconsole;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.part.ViewPart;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.eclipse.common.model.AgentManager;

/**
 *
 *
 *
 */
public class DebugSourceView extends ViewPart {
	
	private IStatusLineManager statusLineManager;
	
	public DebugSourceView() {
		super();
	}

	public void createPartControl(Composite parent) {
		
		Composite topLevel = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		topLevel.setLayout(layout);
		topLevel.setFont(parent.getFont());
				
		final Scale scale = new Scale(topLevel,SWT.HORIZONTAL);
		scale.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		scale.setIncrement(1);
		scale.setMaximum(200);
		scale.setMinimum(0);
		scale.setSelection(100);
		
		scale.addSelectionListener(new SelectionAdapter() {	
			public void widgetSelected(SelectionEvent e) {
				double val = ((double)(scale.getSelection() - 100) / (double)100);
				setPartName("DbgSource: "+val);
			}			
		});
		
		scale.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent e) {
				double val = ((double)(scale.getSelection() - 100) / (double)100);
				MindConsole.getInstance().getConsole().sendCommand(
					ConsoleFacadeIF.ZERO_TOLERANCE,
					AgentManager.getInstance().getCurrentAgent(),
					"changedebugsource",
					Double.toString(val),
					false
				);
			}
		});
				
		statusLineManager = this.getViewSite().getActionBars().getStatusLineManager();
	}
	
	public void setFocus() {
	}

	public IStatusLineManager getStatusLineManager() {
		return statusLineManager;
	}
	
}
