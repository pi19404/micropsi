/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/common/utils/Recycler.java,v 1.3 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.common.utils;

import java.util.ArrayList;

public class Recycler {
	
	public ArrayList<RecyclableIF> items = new ArrayList<RecyclableIF>();
	
	public boolean isEmpty() {
		return items.isEmpty();
	}
		
	public Object recycle() {
		Object toReturn = null;
		synchronized(items) {
			toReturn = items.get(items.size()-1);
			items.remove(items.size()-1);
		}
		((RecyclableIF)toReturn).reset();
		return toReturn;
	}
	
	public void trash(RecyclableIF o) {
		items.add(o);
	}

}
