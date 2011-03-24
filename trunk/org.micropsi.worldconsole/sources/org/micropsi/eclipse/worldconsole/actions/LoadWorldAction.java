/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 16.04.2003
 *
 */
package org.micropsi.eclipse.worldconsole.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.micropsi.eclipse.worldconsole.dialogs.LoadWorldDialog;

/**
 * @author matthias
 *
 */
public class LoadWorldAction extends Action {
	
	/**
	 * 
	 */
	public LoadWorldAction() {
		super("Load world...");
	}

	/**
	 * @param text
	 */
	public LoadWorldAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public LoadWorldAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public LoadWorldAction(String text, int style) {
		super(text, style);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		LoadWorldDialog dialog = new LoadWorldDialog(null);
		dialog.open();
	}

}
