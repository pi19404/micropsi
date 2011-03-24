package org.micropsi.eclipse.console.adminperspective;

import java.util.Iterator;
import java.util.StringTokenizer;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.AnswerTypesIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.console.AnswerHandlerIF;
import org.micropsi.comp.console.AnswerQueueIF;
import org.micropsi.comp.console.ConsoleFacadeIF;
import org.micropsi.comp.messages.MQuestion;
import org.micropsi.comp.messages.MTreeNode;
import org.micropsi.eclipse.console.ConsolePlugin;
import org.micropsi.eclipse.console.ConsoleRuntimeUser;
import org.micropsi.eclipse.console.command.AnswerQueue;
import org.micropsi.eclipse.console.command.IConsoleWorkbenchPart;

/**
 *
 *
 *
 */
public class RawComView extends ViewPart implements IConsoleWorkbenchPart {

	private IStatusLineManager statusLineManager;
	
	private Combo target;
	private Combo question;
	private Text parameter;
	private Combo answerMode;
	private Button blocking;
	private Text retVal;
	private AnswerQueueIF callback;
    private Text tolerance;
    private Label toleranceLabel;

    private ConsoleFacadeIF console;
    
	public RawComView() {
		callback = new AnswerQueue(this);
		console = ConsolePlugin.getDefault().getConsole();
	}

	public void createPartControl(Composite parent) {

		Composite topLevel = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		topLevel.setLayout(layout);
		topLevel.setFont(parent.getFont());
		
		Label label = new Label(topLevel,SWT.NONE);
		GridData data = new GridData();
		data.horizontalSpan = 1;
		label.setLayoutData(data);
		label.setText("Micropsi component");
		
		target = new Combo(topLevel,SWT.DROP_DOWN | SWT.READ_ONLY);
		data = new GridData();
		data.horizontalSpan = 2;
		target.setLayoutData(data);
		target.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				populateQuestionsList();
				question.select(0);								
			}
		});
		populateComponentsList();
		target.select(0);
		
		Button refresh = new Button(topLevel,SWT.NONE);
		data = new GridData();
		data.horizontalSpan = 1;
		refresh.setLayoutData(data);
		refresh.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				populateComponentsList();
				target.select(0);				
			}
		});
		refresh.setText("Refresh list");
				
		label = new Label(topLevel,SWT.NONE);
		data = new GridData();
		data.horizontalSpan = 1;
		label.setLayoutData(data);
		label.setText("Question");
		
		question = new Combo(topLevel,SWT.DROP_DOWN | SWT.READ_ONLY);
		data = new GridData();
		data.horizontalSpan = 3;
		data.widthHint = 300;
		question.setLayoutData(data);
		populateQuestionsList();
		question.select(0);

		label = new Label(topLevel,SWT.NONE);
		data = new GridData();
		data.horizontalSpan = 1;
		label.setLayoutData(data);	
		label.setText("Parameters");
		
		parameter = new Text(topLevel, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		parameter.setLayoutData(data);
		parameter.setText("");
		
		label = new Label(topLevel,SWT.NONE);
		data = new GridData();
		data.horizontalSpan = 1;
		label.setLayoutData(data);	
		label.setText("Answer Mode");

		answerMode = new Combo(topLevel,SWT.DROP_DOWN | SWT.READ_ONLY);
		data = new GridData();
		data.horizontalSpan = 3;
		answerMode.setLayoutData(data);
		answerMode.add("ANSWER_ONCE");
		answerMode.add("DONT_ANSWER");
		answerMode.add("ANSWER_CONTINUOUSLY");
		answerMode.add("ANSWER_EVERY_5_STEPS");
		answerMode.add("ANSWER_EVERY_10_STEPS");
		answerMode.add("ANSWER_EVERY_50_STEPS");
		answerMode.add("ANSWER_EVERY_100_STEPS");
		answerMode.add("STOP_ANSWERING");
		answerMode.select(0);

        toleranceLabel = new Label(topLevel, SWT.NONE);
        data = new GridData();
        data.horizontalSpan = 1;
        toleranceLabel.setLayoutData(data);
        toleranceLabel.setText("Tolerance");
        
        tolerance = new Text(topLevel, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.horizontalSpan = 3;
        tolerance.setLayoutData(data);
        tolerance.setText("0");
        

        blocking = new Button(topLevel,SWT.CHECK);
        data = new GridData();
        data.horizontalSpan = 2;
        blocking.setLayoutData(data);
        blocking.setText("Ask questions blocking");
        blocking.setSelection(false);
        blocking.setVisible(true);



        answerMode.addSelectionListener(new SelectionAdapter(){
            public void widgetSelected(SelectionEvent e){
                String m = answerMode.getText();
                boolean blockingVisible = m.equals("ANSWER_ONCE");
                blocking.setVisible(blockingVisible);
             }
        });              

		
		Button send = new Button(topLevel, SWT.NONE);
		data = new GridData();
		data.horizontalSpan = 4;
		send.setLayoutData(data);
		send.setText("Send question");
		send.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				askQuestion();
			}
		});
				
		retVal = new Text(topLevel,SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 4;
		retVal.setLayoutData(data);
		retVal.setText("");

		statusLineManager = this.getViewSite().getActionBars().getStatusLineManager();
	}
	
	private void askQuestion() {
		
		int mode = -1;
		String m = answerMode.getText();
		if(m.equals("ANSWER_ONCE")) mode = QuestionIF.AM_ANSWER_ONCE;
		else if(m.equals("DONT_ANSWER")) mode = QuestionIF.AM_ANSWER_ONCE;
		else if(m.equals("ANSWER_CONTINUOUSLY")) mode = QuestionIF.AM_ANSWER_CONTINUOUSLY;
		else if(m.equals("ANSWER_EVERY_5_STEPS")) mode = QuestionIF.AM_ANSWER_EVERY_5_STEPS;
		else if(m.equals("ANSWER_EVERY_10_STEPS")) mode = QuestionIF.AM_ANSWER_EVERY_10_STEPS;
		else if(m.equals("ANSWER_EVERY_50_STEPS")) mode = QuestionIF.AM_ANSWER_EVERY_50_STEPS;
		else if(m.equals("ANSWER_EVERY_100_STEPS")) mode = QuestionIF.AM_ANSWER_EVERY_100_STEPS;
		else if(m.equals("STOP_ANSWERING")) mode = QuestionIF.AM_STOP_ANSWERING;
				
		retVal.append("----- Question: -----\n\n");
		
		String t = target.getText();
		String q = question.getText();
		String p = parameter.getText();
        int tol = Integer.parseInt(tolerance.getText());
        int freq = mode==QuestionIF.AM_ANSWER_CONTINUOUSLY?1:
                   mode==QuestionIF.AM_ANSWER_EVERY_5_STEPS?5:
                   mode==QuestionIF.AM_ANSWER_EVERY_10_STEPS?10:
                   mode==QuestionIF.AM_ANSWER_EVERY_50_STEPS?50:100;

		retVal.append(q+"\n");
		
        switch(mode){
                case QuestionIF.AM_ANSWER_ONCE:
                    if(blocking.getSelection()){
                        MQuestion question = new MQuestion(q, mode);
                        StringTokenizer tokenizer = new StringTokenizer(p);
                        while(tokenizer.hasMoreTokens()){
                            question.addParameter(tokenizer.nextToken());                        
                        }
                        question.setDestination(t);
                        try {
							callback.dispatchAnswer(console.askBlockingQuestion(question));
						} catch (MicropsiException e) {
                            retVal.append(e.getMessage());
						}
                        callback.handleAnswers();
                    } else {
            			console.getInformation(tol, t, q, p, callback);
                    }
                    break;
                case QuestionIF.AM_DONT_ANSWER:
                    console.sendCommand(tol, t, q, p, false);
                    break;
                case QuestionIF.AM_STOP_ANSWERING:
                	console.unsubscribe(t, q, p, callback);
                    break;
                default:
                	console.subscribe(freq, t, q, p, callback);
            }
                
	}
	
	private void populateComponentsList() {
		
		target.removeAll();
		
		String Q = "getenvironment";
		
        MQuestion q = new MQuestion(Q, QuestionIF.AM_ANSWER_ONCE);
        q.setDestination(ConsoleRuntimeUser.getInstance().getServerID());
		AnswerIF ret;
		try {
			ret = console.askBlockingQuestion(q);
		} catch (MicropsiException e) {
            ConsoleRuntimeUser.getInstance().handleException(e);
            return;
		}
		MTreeNode root = (MTreeNode)ret.getContent();
		Iterator<MTreeNode> children = root.children();
		
		while(children.hasNext()) {
			target.add(children.next().getValue());
		}
	}
	
	private void populateQuestionsList() {
		AnswerQueue answers = new AnswerQueue(new AnswerHandlerIF() {
			public void handleAnswer(AnswerIF a) {	
				MTreeNode root = (MTreeNode)a.getContent();
				Iterator<MTreeNode> children = root.children();
				question.removeAll();
				while(children.hasNext()) {
					MTreeNode next = children.next();
					if(next.getName().equals("qtype")) {
						question.add(next.getValue());
					}
				}
				question.select(0);
			}
		});
		
		question.removeAll();
		console.getInformation(
			ConsoleFacadeIF.ZERO_TOLERANCE,
			50,
			target.getText(), 
			"getcomponentdescriptor",
			"",
			answers,
			null,
			true);		
	}

	public IStatusLineManager getStatusLineManager() {
		return statusLineManager;
	}
	/**
	 * @see org.micropsi.comp.console.plugin.eclipseconsole.ConsoleWorkbenchPartIF#handleAnswer(org.micropsi.common.consoleservice.AnswerIF)
	 */
	public void handleAnswer(AnswerIF answer) {
		retVal.append("----- Answer: -----\n\n");
        if(answer.getAnswerType()==AnswerTypesIF.ANSWER_TYPE_ERROR){
            retVal.append("----- ERROR: -----\n\n");
        }
		retVal.append((answer.getContent() != null? answer.getContent().toString()+"\n\n":"Empty answer."));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
	}


}
