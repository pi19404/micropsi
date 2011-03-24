/*
 * Created on 08.08.2005
 *
 */
package org.micropsi.eclipse.console;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.micropsi.common.coordinates.Position;
import org.micropsi.comp.agent.MouseAgentManager;
import org.micropsi.comp.agent.genetics.Genom;
import org.micropsi.comp.common.ComponentRunner;
import org.micropsi.comp.common.ComponentRunnerException;
import org.micropsi.comp.console.ExperimentSupervisor;
import org.micropsi.eclipse.runtime.RuntimePlugin;

/**
 * @author Markus
 *
 */
public class AlifeLoaderView extends ViewPart {

    private Button loadAgents;
    private Button resumeExperiment;
    private Button start;
    
    ExperimentSupervisor sup = null;
    
    public static String filename = "AgentGenom.xml";
    
    public AlifeLoaderView() {
        super();
		RuntimePlugin.getDefault();
        MouseAgentManager.getInstance();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl(Composite parent) {
        GridData data = new GridData();
        data.horizontalSpan = 1;
        loadAgents = new Button(parent, SWT.PUSH);
        loadAgents.setLayoutData(data);
        loadAgents.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                createAgents();
            }
        });
        loadAgents.setText("load agent");
        
        data = new GridData();
        data.horizontalSpan = 1;
        start = new Button(parent, SWT.PUSH);
        start.setLayoutData(data);
        start.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                startExperiment();
            }           
        });
        start.setText("start experiment");
        
        /*
        data = new GridData();
        data.horizontalSpan = 1;
        resumeExperiment = new Button(parent, SWT.PUSH);
        resumeExperiment.setLayoutData(data);
        resumeExperiment.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                resumeExperiment();
            }
        });
        resumeExperiment.setText("resume experiment");    
        */   
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPart#setFocus()
     */
    public void setFocus() {
        loadAgents.setFocus();
    }
    
    /*
    private void createNets() {
        System.out.println("widget");
        MouseAgentManager.getInstance().createMinds();
    }
    */
    
    private void createAgents() {
        Genom genom = null;
        boolean isLoaded = true;
        
        try {
            ComponentRunner.getInstance();
        } catch (ComponentRunnerException e2) {
            e2.printStackTrace();
        }

        if (isLoaded)
            MouseAgentManager.getInstance().createAgent(new Position(15.0, 15.0));
        else
            System.out.println("View: could not create");
           
    }
    
    private void startExperiment() {
        
        if (sup == null) {
            sup = new ExperimentSupervisor();
            sup.start();
        }
    }
    
    /*
    private void resumeExperiment() {      
        if (sup == null) {
            MouseAgentManager.getInstance().parseBestAgentFile();
            startExperiment();
        }
    }
    */
    
    private void stopExperiment() {
        if (sup != null)
            sup.setStayAlive(false);
    }
}
