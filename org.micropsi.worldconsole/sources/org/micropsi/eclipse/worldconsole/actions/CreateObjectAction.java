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
import org.micropsi.eclipse.worldconsole.dialogs.CreateObjectDialog;

/**
 * @author matthias
 *
 */
public class CreateObjectAction extends Action {
	
	/**
	 * 
	 */
	public CreateObjectAction() {
		super("Create...");
	}

	/**
	 * @param text
	 */
	public CreateObjectAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public CreateObjectAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public CreateObjectAction(String text, int style) {
		super(text, style);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		CreateObjectDialog dialog = new CreateObjectDialog(null);
		dialog.open();
	}

}
