/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.runtime/sources/org/micropsi/eclipse/common/ProgressDialog.java,v 1.7 2005/07/12 12:52:14 vuine Exp $ 
 */
package org.micropsi.eclipse.common;

import java.util.Stack;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.micropsi.common.progress.ProgressMonitorIF;


public class ProgressDialog extends Dialog implements ProgressMonitorIF {

	ProgressBar progressbar;
	Label task;
	protected Thread uiThread;
	protected boolean shouldclose = false;
	protected String text;
	
	protected Stack<String> taskStack = new Stack<String>();

	public ProgressDialog(String text, Shell parentShell) {
		super(parentShell);
		this.text = text;
		this.setBlockOnOpen(false);
		uiThread = parentShell.getDisplay().getThread();
	}
	
	protected Control createButtonBar(Composite parent) {
		Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayout(new GridLayout());
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.heightHint = 0;
		topLevel.setLayoutData(data);
		topLevel.setFont(parent.getFont());
		return topLevel;
	}

	protected Control createDialogArea(Composite parent) {
		Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayout(new GridLayout());
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.widthHint = 300;
		topLevel.setLayoutData(data);
		topLevel.setFont(parent.getFont());
		
		task = new Label(topLevel,SWT.NONE);
		task.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		task.setText("Preparing...");
		
		progressbar = new ProgressBar(topLevel,SWT.NONE);
		progressbar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		progressbar.setMinimum(0); 

		return topLevel;
	}
	
	public void reportProgress(final int done, final int of, final String info) {
		final Display d = Display.findDisplay(uiThread);
		d.syncExec(new Runnable() {
			public void run() {
				if(info != null) {
					task.setText(info);
					task.update();
				}
				progressbar.setMaximum(of);
				progressbar.setSelection(done);
			}
		});
	}
	
	public void beginTask(final String message) {
		shouldclose = false;
		if(taskStack.isEmpty()) {
			final Display d = Display.findDisplay(uiThread);
			d.syncExec(new Runnable() {
				public void run() {
					open();
				}
			});
		}
		taskStack.push(message);
	}

	public void endTask() {
		taskStack.pop();
		if(taskStack.isEmpty()) {
			final Display d = Display.findDisplay(uiThread);
			d.syncExec(new Runnable() {
				public void run() {
					shouldclose = true;
					close();
				}
			});
		}
	}
	
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(text);
	}
	
	protected ShellListener getShellListener() {	
		return new ShellAdapter() {
			public void shellClosed(ShellEvent event) {
				if (shouldclose) {
					handleShellCloseEvent();
				} else {
					event.doit= false;	// don't close now	
				}
			}
		};
	}
		
}
