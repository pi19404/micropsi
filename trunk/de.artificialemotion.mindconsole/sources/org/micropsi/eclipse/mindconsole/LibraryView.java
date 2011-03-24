package org.micropsi.eclipse.mindconsole;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.eclipse.mindconsole.widgets.LibraryWidget;

/**
 *
 *
 *
 */
public class LibraryView extends ViewPart {
	
	private IStatusLineManager statusLineManager;
	
	public LibraryView() {
		super();
	}

	public void createPartControl(Composite parent) {
		
		final ScrolledComposite scroller = new ScrolledComposite(parent,SWT.V_SCROLL);
		scroller.setLayoutData(new GridData(GridData.FILL_BOTH));
		scroller.setAlwaysShowScrollBars(true);
		try {
			LibraryWidget lw = new LibraryWidget(scroller,SWT.NONE,MindPlugin.getDefault().getLibraryManager());
			lw.setLayoutData(new GridData(GridData.FILL));
			scroller.setContent(lw);
			scroller.setEnabled(true);
		} catch (MicropsiException e) {
			MindPlugin.getDefault().handleException(e);
		}
				
		statusLineManager = this.getViewSite().getActionBars().getStatusLineManager();
	}
	
	public void setFocus() {
	}

	public IStatusLineManager getStatusLineManager() {
		return statusLineManager;
	}
	
}
