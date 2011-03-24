/*
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/nodenet/LocalNetRegistry.java,v 1.3 2005/07/12 12:55:16 vuine Exp $ 
 */
package org.micropsi.nodenet;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.HashMap;

/**
 * A registry for local nets. Entries are created automatically. This class
 * can be used to access entities with only two string keys and absolutely
 * no instance available: First get the net with the net key, then the entity
 * with its key.
 * The underlying data structure is <i> weak </i>. This means that the
 * registry does not prevent nets from being garbage collected. Hence you
 * can also use this registry to check which nets are "alive".  
 */
public class LocalNetRegistry {

	private static LocalNetRegistry instance = new LocalNetRegistry();
	
	public static LocalNetRegistry getInstance() {
		return instance;
	}
	
	private int lastKey = -1;
	private HashMap<String,WeakReference<LocalNetFacade>> nets = new HashMap<String,WeakReference<LocalNetFacade>>();
	
	protected String registerNewNet(LocalNetFacade facade) {
		String key = newKey(); 
		nets.put(key,new WeakReference<LocalNetFacade>(facade));
		return key; 
	}
	
	private synchronized String newKey() {
		lastKey++;
		return "net-"+lastKey;
	}
	
	private void touchEntries() {
		Iterator iter = nets.values().iterator();
		while(iter.hasNext()) {
			WeakReference r = (WeakReference) iter.next();
			if(r.get() == null)
				iter.remove();
		}
	}

	/**
	 * Returns an iterator of Strings, containing the keys of all nets. Keys of
	 * nets that have been garbage collected will not be returned.
	 * @return an iterator, containig String keys
	 */
	public Iterator<String> getNetKeys() {
		touchEntries();
		return nets.keySet().iterator();
	}
	
	/**
	 * Returs a net by its key.
	 * @param key The net's key
	 * @return the net instance
	 */
	public LocalNetFacade getNet(String key) {
		touchEntries();
		WeakReference r = nets.get(key);
		return (LocalNetFacade)r.get();
	}
		
}
