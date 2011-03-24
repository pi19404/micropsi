package org.micropsi.eclipse.viewer3d;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import org.micropsi.eclipse.viewer3d.win32.NebulaSurface;

public class World3DView extends ViewPart {

	private IStatusLineManager statusLineManager;
	private IPreferenceStore prefs;
	
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		prefs = PlatformUI.getPreferenceStore();
	}
	
	public void createPartControl(Composite parent) {

		Composite topLevel = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		topLevel.setLayout(layout);
		topLevel.setFont(parent.getFont());
		
		String cmd = StartStringFactory.create3DStartString(prefs);						
		NebulaSurface nebula = new NebulaSurface(topLevel, cmd);
		nebula.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		statusLineManager = this.getViewSite().getActionBars().getStatusLineManager();
	}
	
	public IStatusLineManager getStatusLineManager() {
		return statusLineManager;
	}

	public void setFocus() {
	}
	
}
