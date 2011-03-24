package org.micropsi.eclipse.mindconsole;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.micropsi.common.consoleservice.AnswerIF;
import org.micropsi.common.consoleservice.QuestionIF;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.comp.common.AbstractComponent;
import org.micropsi.comp.common.ComponentRunner;
import org.micropsi.comp.common.ComponentRunnerException;
import org.micropsi.comp.messages.MAnswer;
import org.micropsi.comp.messages.MQuestion;
import org.micropsi.eclipse.common.model.AgentManager;
import org.micropsi.eclipse.common.model.IAgentChangeListener;
import org.micropsi.eclipse.console.controller.IViewControllerListener;
import org.micropsi.eclipse.mindconsole.jdt.ScriptJavaManager;
import org.micropsi.eclipse.model.net.AgentNetModelManager;
import org.micropsi.nodenet.scripting.MindScript;
import org.micropsi.nodenet.scripting.MindScriptConfigurator;
import org.micropsi.nodenet.scripting.Script;
import org.micropsi.nodenet.scripting.ScriptStateListenerIF;
import org.micropsi.nodenet.scripting.ScriptingManagerIF;

/**
 *
 *
 *
 */
public class ScriptView extends ViewPart implements ScriptStateListenerIF,IAgentChangeListener,IViewControllerListener {
		
	private ScriptingManagerIF scriptingManager;
	
	private IStatusLineManager statusLineManager;
	
	private String currentScript;
	boolean terminateRequested = false;
	
	private Label label;
	private ListViewer scriptList;
	
	private Color normalColor;
	private Color hangColor;
	
	private String selected = "";
	private Object[] scripts = new Object[0];
	
	public ScriptView() {
		ScriptController.getInstance().registerView(this);
		AgentManager.getInstance().addAgentChangeListener(this);
	}
	
	Display display;
	
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
	
		if(memento != null) { 
			IMemento m = memento.getChild("agentmanager");
			if(m != null)
				AgentManager.getInstance().loadState(m);
		}					
	}

	public void createPartControl(Composite parent) {
	    
		// ensure initialisation
		MindPlugin.getDefault();
		
		display = getSite().getWorkbenchWindow().getWorkbench().getDisplay();
	    
		Composite topLevel = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		topLevel.setLayout(layout);
		topLevel.setFont(parent.getFont());
		
		label = new Label(topLevel,SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText("No active script");
		normalColor = label.getForeground();
		hangColor = new Color(null,200,0,0);
		
		scriptList = new ListViewer(topLevel);
		scriptList.getList().setLayoutData(new GridData(GridData.FILL_BOTH));
		scriptList.setContentProvider(new IStructuredContentProvider() {
			
			public Object[] getElements(Object inputElement) {
				return scripts;
			}
			
			public void dispose() {
			}
			
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			
		});
		
		scriptList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				selected = scriptList.getList().getItem(scriptList.getList().getSelectionIndex());
			}
			
		});
		
		scriptList.getList().addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(MouseEvent e) {
				runScript();
			}
		});
		
		scriptList.getList().setMenu(new Menu(scriptList.getList()));
		
		MenuItem item = new MenuItem(scriptList.getList().getMenu(),SWT.CASCADE);
		item.setText("Start");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				runScript();
			}
		});
		item = new MenuItem(scriptList.getList().getMenu(),SWT.CASCADE);
		item.setText("Edit");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(scriptList.getList().getSelectionCount() != 1) return;
				ScriptJavaManager.getInstance().openInEditor(selected);
			}
		});
		item = new MenuItem(scriptList.getList().getMenu(),SWT.CASCADE);
		item.setText("Delete");
		item.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(scriptList.getList().getSelectionCount() != 1) return;
				ScriptJavaManager.getInstance().delete(selected);
			}
		});

		
		statusLineManager = this.getViewSite().getActionBars().getStatusLineManager();

		agentSwitched(AgentManager.getInstance().getCurrentAgent());
		
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				new IResourceChangeListener() {
					public void resourceChanged(IResourceChangeEvent event) {
						updateInput();
					}
				},
				IResourceChangeEvent.POST_BUILD				
			);
						
		updateInput();
	}
	
	public void setFocus() {
	}

	public IStatusLineManager getStatusLineManager() {
		return statusLineManager;
	}

	/* (non-Javadoc)
	 * @see org.micropsi.nodenet.scripting.ScriptStateListenerIF#scriptStarted()
	 */
	public void scriptStarted() {
	    display.asyncExec(new Runnable() {
            public void run() {
                update();                
            }
	    });
	}

	/* (non-Javadoc)
	 * @see org.micropsi.nodenet.scripting.ScriptStateListenerIF#scriptTerminated()
	 */
	public void scriptTerminated() {
	    display.asyncExec(new Runnable() {
            public void run() {
        		currentScript = null;
        		terminateRequested = false;
        		update();
            }
	    });
	 }	
	
	private void updateInput() {
		
		if(scriptingManager == null) {
			return;
		}
		
		final Runnable updater = new Runnable() {
            public void run() {
        	    
            	// script list can be disposed during shutdown
            	if(scriptList.getList().isDisposed()) {
            		return;
            	}				
            	
            	scriptList.setInput(new Object());
        	    
				String[] items = scriptList.getList().getItems();
				if(items == null) {
					return;
				}
				for(int i=0;i<items.length;i++) {
					if(items[i].equals(selected)) {
						scriptList.getList().select(i);
						return;
					}
				}
				scriptList.getList().select(0);
            }
	    };
		
		Thread getScriptsThread = new Thread(new Runnable() {
			public void run() {
				List<IType> scriptList = ScriptJavaManager.getInstance().getScriptImplementations();
				
				scripts = new Object[scriptList.size()];
				for(int i=0;i<scripts.length;i++) {
					scripts[i] = scriptList.get(i).getFullyQualifiedName();
				}
				
			    display.asyncExec(updater);	
			}
		});
		
		getScriptsThread.start();
	}
	
	public void update() {
		
		// label can be disposed if the script terminates during shutdown
		if(label.isDisposed()) {
			return;
		}
		
		if(terminateRequested) {
			label.setForeground(hangColor);
		} else {
			label.setForeground(normalColor);
		}
		
		if(currentScript == null) {
			label.setText("No active script");
		} else {
			label.setText("Script: "+currentScript+(terminateRequested ? " (stopping)":""));
		}
	}
	
	public void runScript() {
		if(currentScript != null) {
			return;
		}
		
		if(scriptingManager == null) {
			return;
		}

		if(scriptList.getList().getSelectionCount() != 1) return;
		
		try {
			String className = scriptList.getList().getItem(scriptList.getList().getSelectionIndex());
			Class clazz = ScriptJavaManager.getInstance().getNewJDTClassLoader().loadClass(className);
				
			Script script = (Script)clazz.newInstance();
			if(script instanceof MindScript) {
				MindScriptConfigurator.configureMindScript(
					(MindScript)script, 
					AgentNetModelManager.getInstance().getNetModel().getNet()
				);
			}
			
			currentScript = script.getClass().getName();
			scriptingManager.executeScript(script);
					
		} catch (Throwable e) {
		    e.printStackTrace();
			MindPlugin.getDefault().handleException(e);
		}
	}
		
	public void terminateScript(boolean force) {
		if(currentScript == null || scriptingManager == null) {
			return;
		}

		terminateRequested = true;
		
		if(!force) {
			scriptingManager.terminateScript();
		} else {
			scriptingManager.forceTerminateScript();
		}
		
		update();

	}
	

	/*(non-Javadoc)
	 * @see org.micropsi.eclipse.common.model.IAgentChangeListener#agentSwitched(java.lang.String)
	 */
	public void agentSwitched(String newAgentID) {
		
		if(currentScript != null) {
			terminateScript(false);
		}
		
		if(scriptingManager != null) {
			scriptingManager.removeScriptStateListener(this);
		}
		
		if(newAgentID == null) {
			scriptingManager = null;
		}
				
		MQuestion q = new MQuestion();
		q.setQuestionName("getscriptingmanager");
		q.setDestination(newAgentID);
		
		try {
			AnswerIF answer = MindPlugin.getDefault().getConsole().askBlockingQuestion(q,20);
			if(scriptingManager != null) {
				scriptingManager.removeScriptStateListener(this);
			}
			
			this.scriptingManager = (ScriptingManagerIF)answer.getContent();
			
			if(scriptingManager == null) {
				try {
					// if the server is remote, but the agent is local, we can
					// bypass the server and get the net directly
					if(ComponentRunner.getInstance().componentExists(newAgentID)) {
						ArrayList<QuestionIF> questionlist = new ArrayList<QuestionIF>();
						questionlist.add(q);
						
						AbstractComponent comp = ComponentRunner.getInstance().getComponent(newAgentID);
						List<AnswerIF> answerlist = comp.getConsoleService().answerQuestions(questionlist, 0);
						scriptingManager = (ScriptingManagerIF)((MAnswer)answerlist.get(0)).getContent();					
					}
				} catch (ComponentRunnerException e) {
					
				}
			}
			
			if(scriptingManager != null) {
			
				scriptingManager.addScriptStateListener(this);
			
				updateInput();
			
			}
		} catch (MicropsiException e) {
			MindPlugin.getDefault().handleException(e);
		}		
	}

	/*(non-Javadoc)
	 * @see org.micropsi.eclipse.common.model.IAgentChangeListener#agentDeleted(java.lang.String)
	 */
	public void agentDeleted(String deletedAgentID) {
	}

	/*(non-Javadoc)
	 * @see org.micropsi.eclipse.console.controller.IViewControllerListener#setDataBase(java.lang.Object)
	 */
	public void setDataBase(Object o) {	
	}

	/*(non-Javadoc)
	 * @see org.micropsi.eclipse.console.controller.IViewControllerListener#setData(java.lang.Object)
	 */
	public void setData(Object o) {
	}
	
	public void dispose() {
		hangColor.dispose();
	}
	
	public void saveState(IMemento memento) {
		IMemento m = memento.createChild("agentmanager");
		AgentManager.getInstance().saveState(m);
		memento.putMemento(m);
	}

	
}
