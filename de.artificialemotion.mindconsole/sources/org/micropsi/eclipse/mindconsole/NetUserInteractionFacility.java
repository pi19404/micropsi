package org.micropsi.eclipse.mindconsole;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.micropsi.eclipse.mindconsole.dialogs.SuspendMessageDialog;
import org.micropsi.nodenet.NetCycleIF;
import org.micropsi.nodenet.UserInteractionIF;

/**
 * @author rvuine
 */
public class NetUserInteractionFacility implements UserInteractionIF {

	private class SuspendInputDialog extends InputDialog {
		
		private boolean suspend = false;

		public SuspendInputDialog(Shell arg0, String arg1, String arg2, String arg3, IInputValidator arg4) {
			super(arg0, arg1, arg2, arg3, arg4);
		}

		public Control createButtonBar(Composite parent) {
			Composite topLevel = new Composite(parent,SWT.NONE);
			topLevel.setLayout(new GridLayout());
			topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
			topLevel.setFont(parent.getFont());
			final Button s = new Button(topLevel,SWT.CHECK);
			s.setText("Suspend net after this dialog has been closed");
			s.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					suspend = s.getSelection();					
				}				
			});
			super.createButtonBar(topLevel);
			return topLevel;
		}
		
		public boolean isSuspend() {
			return suspend;
		}
		
	}

	private class SuspendListSelectionDialog extends ListSelectionDialog {

		private boolean suspend = false;

		public SuspendListSelectionDialog(Shell shell, Object alternatives, IStructuredContentProvider content, ILabelProvider label, String text) {
			super(shell, alternatives, content, label, text);		
		}
		
		public Control createButtonBar(Composite parent) {
			Composite topLevel = new Composite(parent,SWT.NONE);
			topLevel.setLayout(new GridLayout());
			topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
			topLevel.setFont(parent.getFont());
			final Button s = new Button(topLevel,SWT.CHECK);
			s.setText("Suspend net after this dialog has been closed");
			s.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent arg0) {
					suspend = s.getSelection();					
				}				
			});
			super.createButtonBar(topLevel);
			return topLevel;
		}
		
		public boolean isSuspend() {
			return suspend;
		}
	}

	private class AlternativesContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object alternatives) {		
			String[] s = (String[])alternatives;
			Object[] toReturn = new Object[s.length];
			for(int i=0;i<s.length;i++) toReturn[i] = s[i];
			return toReturn;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}		
	}

	private Shell shell;
	private Thread uiThread;
	private NetCycleIF cycle;

	public NetUserInteractionFacility(Shell shell, NetCycleIF cycle) {
		this.shell = shell;
		uiThread = Thread.currentThread();
		this.cycle = cycle;
	}

	public String[] selectFromAlternatives(String[] alternatives) {

		final SuspendListSelectionDialog sdl = new SuspendListSelectionDialog(
			shell,
			alternatives,
			new AlternativesContentProvider(),
			new LabelProvider(),
			"Choose alternative:"
		);
		sdl.setBlockOnOpen(true);
		sdl.setTitle("Node net debug");


		Display.findDisplay(uiThread).syncExec(new Runnable() {
			public void run() {
				sdl.open();				
			}
		});					
		
		if(sdl.isSuspend())
			cycle.suspend();	
		
		Object[] res = sdl.getResult();
		
		if(res == null) return new String[0];
		if(res.length == 0) return new String[0];
		
		String[] toReturn = new String[res.length];
		for(int i=0;i<res.length;i++)
			toReturn[i] = (String)res[i];
			
		return toReturn;
	}

	public String askUser(String prompt) {
		final SuspendInputDialog idl = new SuspendInputDialog(
			shell,
			"Node net debug input request",
			prompt,
			"",
			null
		);
		idl.setBlockOnOpen(true);
		Display.findDisplay(uiThread).syncExec(new Runnable() {
			public void run() {
				idl.open();				
			}
		});
		
		if(idl.isSuspend())
			cycle.suspend();	
						
		return idl.getValue();
	}

	public void displayInformation(String information) {
		final SuspendMessageDialog mdl = new SuspendMessageDialog(
			shell,
			"Node net debug information",
			null,
			information,
			MessageDialog.INFORMATION,
			new String[] {"OK"},
			0);
			
		mdl.setBlockOnOpen(true);
		Display.findDisplay(uiThread).syncExec(new Runnable() {
			public void run() {
				mdl.open();				
			}
		});
		
		if(mdl.isSuspend())
			cycle.suspend();					
	}
}
