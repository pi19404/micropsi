/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/utils/ListenerWeakVector.java,v 1.3 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.common.utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ListenerWeakVector {
	
	private ArrayList<WeakReference> listeners;
	
	public ListenerWeakVector(int size) {
		listeners = new ArrayList<WeakReference>(size);
	}
	
	public ListenerWeakVector() {
		listeners = new ArrayList<WeakReference>();
	}

	public void addListener(ListenerIF listener) {
		listeners.add(new WeakReference<ListenerIF>(listener));
	}
	
	public void removeListener(ListenerIF listener) {
		for(int i=0;i<listeners.size();i++) {
			Object tmp = listeners.get(i).get();
			if(tmp == listener) listeners.remove(i);
			return;
		}
	}
	
	public void fireNotifications(int eventType, Object parameters) {
		for(int i=listeners.size()-1;i>=0;i--) {
			Object tmp = listeners.get(i).get();
			if(tmp == null) {
				listeners.remove(i);
			} else {
				((ListenerIF)tmp).fireNotification(eventType, parameters);
			}
		}		
	}
}
