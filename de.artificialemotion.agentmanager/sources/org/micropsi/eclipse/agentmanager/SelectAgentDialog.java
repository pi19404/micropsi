package org.micropsi.eclipse.agentmanager;

import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.messages.MQuestion;
import org.micropsi.comp.messages.MTreeNode;

public class SelectAgentDialog extends Dialog {
	
	List list;
	String selected;
	String prompt;
	
	public SelectAgentDialog(Shell shell, String prompt) {
		super(shell);
		this.prompt = prompt;
	}
	
	protected Control createDialogArea(Composite parent) {		
		Composite topLevel = new Composite(parent,SWT.NONE);
		topLevel.setLayout(new GridLayout());
		topLevel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		topLevel.setFont(parent.getFont());
				
		Label label = new Label(topLevel,SWT.NONE);
		label.setText(prompt); 
		
		list = new List(parent,SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 80;
		list.setLayoutData(data);
				
		list.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selected = list.getItem(list.getSelectionIndex());
			}
		});
		
		list.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				okPressed();
			}			
		});
		
		fillList();
							
		return topLevel;
	}
			
	public String getSelected() {
		return selected;
	}
		
	private void fillList() {
			
		list.removeAll();
		String Q = "getagentlist";
		
		MQuestion q = new MQuestion(Q, QuestionIF.AM_ANSWER_ONCE);
		q.setDestination(AgentManagerPlugin.getDefault().getServerID());
		AnswerIF ret;
		try {
			ret = AgentManagerPlugin.getDefault().getConsole().askBlockingQuestion(q);
		} catch (MicropsiException e) {
			AgentManagerPlugin.getDefault().handleException(e);
			return;
		}
		MTreeNode root = (MTreeNode)ret.getContent();
		Iterator<MTreeNode> children = root.children();
		if(children == null) return;
		
		while(children.hasNext()) {
			MTreeNode tmp = children.next();						
			list.add(tmp.getValue());
		}
		
	}

}
