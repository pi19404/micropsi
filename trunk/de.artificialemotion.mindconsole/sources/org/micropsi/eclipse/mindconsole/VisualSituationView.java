package org.micropsi.eclipse.mindconsole;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.micropsi.eclipse.common.model.AgentManager;
import org.micropsi.eclipse.mindconsole.widgets.VisualSituationWidget;

/**
 *
 *
 *
 */
public class VisualSituationView extends ViewPart {
	
	private IStatusLineManager statusLineManager;
	
	public VisualSituationView() {
		super();
	}

	public void createPartControl(Composite parent) {
		
		String currentAgent = AgentManager.getInstance().getCurrentAgent();

		VisualSituationWidget vsw = new VisualSituationWidget(parent,SWT.NONE,currentAgent);
		vsw.setLayoutData(new GridData(GridData.FILL_BOTH));
				
		statusLineManager = this.getViewSite().getActionBars().getStatusLineManager();
	}
	
	public void setFocus() {
	}

	public IStatusLineManager getStatusLineManager() {
		return statusLineManager;
	}
	
}
