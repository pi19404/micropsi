/*
 * $ Header $
 * 
 * Author: matthias
 * Created on 16.04.2003
 *
 */
package org.micropsi.eclipse.worldconsole.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.micropsi.comp.console.worldconsole.EditSession;
import org.micropsi.comp.console.worldconsole.model.WorldObject;
import org.micropsi.eclipse.worldconsole.WorldConsole;

/**
 * @author matthias
 *
 */
public class RemoveObjectAction extends Action {
	
	/**
	 * 
	 */
	public RemoveObjectAction() {
		super("Remove...");
	}

	/**
	 * @param text
	 */
	public RemoveObjectAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public RemoveObjectAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public RemoveObjectAction(String text, int style) {
		super(text, style);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		EditSession editSession = WorldConsole.getInstance().getGlobalData().getEditSession();
		if (editSession.getSelectedObjects().size() == 1) {
			WorldObject obj = editSession.getSelectedObject();
			if (MessageDialog.openConfirm(
					null,
					"Remove Object",
					"Do you really want to remove object '"
						+ obj.getObjectName()
						+ " ("
						+ obj.getId()
						+ ")'?")) {
				WorldConsole.getInstance().getGlobalData().getRemoteWorld().removeObject(obj);
			}
		} else if (editSession.getSelectedObjects().size() > 1) {
			if (MessageDialog.openConfirm(
						null,
						"Remove Object",
						"Do you really want to remove "
							+ editSession.getSelectedObjects().size()
							+ " objects?")) {
					WorldConsole.getInstance().getGlobalData().getRemoteWorld().removeObjects(editSession.getSelectedObjects());
			}
		} else if (editSession.getSelectedObjectParts().size() > 0) {
			MessageDialog.openError(null, "Remove Object", "At the moment, you cannot remove subobjects.");
		}
	}
	
}