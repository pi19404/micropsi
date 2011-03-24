/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/console/worldconsole/AbstractController.java,v 1.2 2005/07/12 12:55:16 vuine Exp $ 
 */
package org.micropsi.comp.console.worldconsole;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


/**
 * Copied from org.micropsi.console project for now. Will probably be rewritten.
 */
public abstract class AbstractController {

	protected ArrayList<WeakReference> listeners = new ArrayList<WeakReference>();
	
	protected Object data;
	protected Object baseData;
	
	public void setData(Object o) {
		data = o;
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((IViewControllerListener)referenced).setData(data);
			}
		}
	}
	
	public void setDataBase(Object o) {
		baseData = o;
		for(int i=listeners.size()-1;i>=0;i--) {
			WeakReference ref = listeners.get(i);
			Object referenced = ref.get();
			if(referenced == null) {
				listeners.remove(i);
			} else {
				((IViewControllerListener)referenced).setDataBase(baseData);
			}
		}
	}
	
	public void registerView(IViewControllerListener listener) {
		WeakReference<IViewControllerListener> ref = new WeakReference<IViewControllerListener>(listener);
		listeners.add(ref);
	}


}
