package org.micropsi.nodenet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * InnerStateContainers contain the published (and persistent) inner states of an native
 * module implementation.<br>
 * What "published" actually means is that the native module implementation uses its
 * innerstate object for storing variables. 
 */
public final class InnerStateContainer implements InnerStateInspectorIF {
	
	private HashMap<String,String> map = new HashMap<String,String>();
	
	protected InnerStateContainer() {
	}
	
	/**
	 * Returns the map of inner states for saving them
	 * @return HashMap the inner states
	 */
	protected HashMap<String,String> getMap() {
		return map;
	}

	/**
	 * Sets the map of inner states after loading them from somewhere
	 * @param map the inner states
	 */
	protected void setMap(Map<String,String> map) {
		this.map = new HashMap<String,String>(map);
	}
	
	/**
	 * Sets a state. If the state does not exist, it will be created
	 * @param key the state's key
	 * @param value the value
	 */
	public void setState(String key, String value) {
		map.put(key, value);
	}

	/**
	 * Sets a state. If the state does not exist, it will be created
	 * @param key the state's key
	 * @param value the value
	 */	
	public void setState(String key, long value) {
		map.put(key, Long.toString(value));
	}
	
	/**
	 * Sets a state. If the state does not exist, it will be created
	 * @param key the state's key
	 * @param value the value
	 */
	public void setState(String key, double value) {
		map.put(key, Double.toString(value));
	}
	
	/**
	 * Sets a state. If the state does not exist, it will be created
	 * @param key the state's key
	 * @param value the value
	 */
	public void setState(String key, int value) {
		map.put(key, Integer.toString(value));
	}
	
	/**
	 * Returns a state as a String. This works for all states.
	 * @param key the key of the state to be retrieved
	 * @return String the value
	 */
	public String getStateString(String key) {
		return map.get(key);
	}

	/**
	 * Returns a state as a long. If the value can't be interpreted as a long, zero will be returned.
	 * (Don't rely on this, it's more of a side effect than a feature and slow)
	 * @param key the key of the state to be retrieved
	 * @return long the value
	 */	
	public long getStateLong(String key) {
		if(!map.containsKey(key)) return 0;
		String tmp = map.get(key);
		try {
			return Long.parseLong(tmp);
		} catch (NumberFormatException e) {
			return 0; 
		}		
	}

	/**
	 * Returns a state as a double. If the value can't be interpreted as double, zero will be returned.
	 * (Don't rely on this, it's more of a side effect than a feature and slow)
	 * @param key the key of the state to be retrieved
	 * @return double the value
	 */	
	public double getStateDouble(String key) {
		if(!map.containsKey(key)) return 0;
		String tmp = map.get(key);
		try {
			return Double.parseDouble(tmp);
		} catch (NumberFormatException e) {
			return 0; 
		}		
	}

	/**
	 * Returns a state as an int. If the value can't be interpreted as int, zero will be returned.
	 * (Don't rely on this, it's more of a side effect than a feature and slow)
	 * @param key the key of the state to be retrieved
	 * @return int the value
	 */		
	public int getStateInt(String key) {
		if(!map.containsKey(key)) return 0;
		String tmp = map.get(key);
		try {
			return Integer.parseInt(tmp);
		} catch (NumberFormatException e) {
			return 0; 
		}		
	}

	/**
	 * @see org.micropsi.nodenet.InnerStateInspectorIF#getInnerStates()
	 */
	public String[] getInnerStates() {
		String[] s = new String[map.size()];
		Iterator iter = map.keySet().iterator();
		for(int i=0;i<s.length;i++)
			s[i] = (String)iter.next();
		return s;
	}

	/**
	 * @see org.micropsi.nodenet.InnerStateInspectorIF#getInspectionString()
	 */
	public String getInspectionString() {
		String toReturn = "InspectionString: \n\n";
		
		Iterator iter = map.keySet().iterator();
		while(iter.hasNext()) {
			String key = (String)iter.next();
			toReturn += "\n" + key + ": "+map.get(key);
		}
		return toReturn;
	}

	/**
	 * @see org.micropsi.nodenet.InnerStateInspectorIF#getInnerState(String)
	 */
	public String getInnerState(String key) {
		if(!map.containsKey(key)) return null;
		return map.get(key);
	}

	/**
	 * @see org.micropsi.nodenet.InnerStateInspectorIF#changeInnerState(String, String)
	 */
	public boolean changeInnerState(String key, String value) {
		if(!map.containsKey(key)) return false;
		
		map.put(key, value);
		
		return false;
	}
	
	/**
	 * This ensures that an inner state exists. If it does not exist, it will be created with the
	 * defaultValue parameter as it's value. If the state exists, nothing will happen.
	 * @param key the key of the value
	 * @param defaultValue the default value
	 */
	public void ensureStateExistence(String key, String defaultValue) {
		if(!map.containsKey(key)) map.put(key, defaultValue);
	}

}
