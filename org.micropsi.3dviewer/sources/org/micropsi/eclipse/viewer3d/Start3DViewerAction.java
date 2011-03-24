package org.micropsi.eclipse.viewer3d;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * 
 * 
 */
public class Start3DViewerAction implements IWorkbenchWindowActionDelegate {

	private IPreferenceStore prefs;
	private String path;
	
	public void init(IWorkbenchWindow window) {
		prefs = PlatformUI.getPreferenceStore();
		
		Path viewer = new Path("viewer\\bin");
		try {
			path = Platform.asLocalURL((Viewer3DPlugin.getDefault().find(viewer))).getFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		String[] cmd = StartStringFactory.create3DStartCMD(prefs);
		try {
			Runtime.getRuntime().exec(cmd,null,new File(path));			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}


	public void dispose() {
	}

}