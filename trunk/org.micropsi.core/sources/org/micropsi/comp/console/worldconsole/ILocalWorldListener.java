/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 06.10.2003
 *
 */
package org.micropsi.comp.console.worldconsole;

import java.util.Collection;

import org.micropsi.comp.console.worldconsole.model.AbstractWorldObject;
import org.micropsi.comp.console.worldconsole.model.WorldObject;

/**
 * @author Matthias
 *
 */
public interface ILocalWorldListener extends IViewControllerListener {
	public void onObjectChanged(LocalWorld localWorld, WorldObject changedObject);
	public void onMultipleObjectsChanged(LocalWorld localWorld, Collection<AbstractWorldObject> changedObjects);
	public void onObjectListRefreshed(LocalWorld localWorld);
	public void onGlobalsChanged();

}
