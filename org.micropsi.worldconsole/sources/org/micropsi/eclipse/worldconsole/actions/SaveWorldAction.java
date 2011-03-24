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
import org.micropsi.eclipse.worldconsole.dialogs.SaveWorldDialog;

/**
 * @author matthias
 *
 */
public class SaveWorldAction extends Action {
	
	/**
	 * 
	 */
	public SaveWorldAction() {
		super("Save world...");
	}

	/**
	 * @param text
	 */
	public SaveWorldAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public SaveWorldAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public SaveWorldAction(String text, int style) {
		super(text, style);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
//		if (MessageDialog
//			.openConfirm(
//				null,
//				"Save World",
//				"Do you really want to overwrite the startup world setup with the current world setup?")) {
//			WorldPlugin.getDefault().getConsole().getInformation(
//				0,
//				100,
//				"world",
//				"saveworld",
//				"",
//				QuestionErrorHandler.getInstance().getAnswerQueue(),
//				null,
//				true);
//		}
		SaveWorldDialog dialog = new SaveWorldDialog(null);
		dialog.open();
	}

}
