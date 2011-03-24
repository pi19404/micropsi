/*
 * Created on 16.04.2005
 *
 */

package org.micropsi.comp.console.worldconsole;

import java.util.Collection;

/**
 * @author Matthias
 */
public interface IEditSessionListener extends IViewControllerListener {
	public void onSelectionChanged(EditSession session, Collection changeList);

}
