/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.console/sources/org/micropsi/eclipse/console/controller/AbstractController.java,v 1.3 2005/07/12 12:52:34 vuine Exp $ 
 */
package org.micropsi.eclipse.console.controller;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


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
