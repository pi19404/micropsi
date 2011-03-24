/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.runtime/sources/org/micropsi/eclipse/runtime/dialogs/ExceptionDialog.java,v 1.3 2005/08/12 17:57:09 vuine Exp $ 
 */
package org.micropsi.eclipse.runtime.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ExceptionDialog extends Dialog {

	private static String BREAK = System.getProperty("line.separator");

	private String message;
	private Throwable e;

	public ExceptionDialog(Shell parentShell, String message) {
		super(parentShell);
		this.message = message;
		this.setBlockOnOpen(true);
	}
	
	public ExceptionDialog(Shell parentShell, String message, Throwable e) {
		super(parentShell);
		this.message = message;
		this.e = e;
		this.setBlockOnOpen(true);
	}

	protected Control createDialogArea(Composite parent) {
		Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		topLevel.setFont(parent.getFont());

		Label label = new Label(topLevel,SWT.NONE);
		Color red = new Color(parent.getDisplay(),255,0,0);
		label.setForeground(red);
		red.dispose();
		label.setText("Exception!");
				
		Text text = new Text(topLevel,SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 600;
		data.heightHint = 100;
		text.setLayoutData(data);
		text.setText(message);
		text.setEditable(false);
					
		if(e != null) {
			String m = e.getMessage();
			if(m == null) m = " "; else m = " "+m;
			String toReturn = e.toString()+": "+m+BREAK;
			StackTraceElement[] elements = e.getStackTrace();
			for(int i=0;i<elements.length;i++) {
				toReturn += "\t\tat "+
							elements[i].getClassName()+"."+
							elements[i].getMethodName()+" ("+
							elements[i].getFileName()+":"+
							elements[i].getLineNumber()+")"+BREAK;
			}
			
			while(e.getCause() != null && e.getCause() != e) {
				e = e.getCause();
				m = e.getMessage();
				if(m == null) m = " "; else m = " "+m;
				toReturn += BREAK+"Cause: "+BREAK;
				toReturn += e.toString()+": "+m+BREAK;
				elements = e.getStackTrace();
				for(int i=0;i<elements.length;i++) {
					toReturn += "\t\tat "+
								elements[i].getClassName()+"."+
								elements[i].getMethodName()+" ("+
								elements[i].getFileName()+":"+
								elements[i].getLineNumber()+")"+BREAK;
				}
				
			}
						
			text = new Text(topLevel,SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
			data = new GridData(GridData.FILL_HORIZONTAL);
			data.widthHint = 600;
			data.heightHint = 400;
			text.setLayoutData(data);
			text.setText(toReturn);
			text.setEditable(false);		
		}
		
		return topLevel;
	}
}
