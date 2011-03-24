package org.micropsi.nodenet;

/**
 *	The InnerStateInspectorIF can be used to take a look at the published (and persistent) inner
 * states of a native module.
 */
public interface InnerStateInspectorIF {

	/**
	 * Returns a String that describes the inner state of the module, inculding the classname
	 * and all persistent states (key/value)
	 * @return String the description
	 */
	public String getInspectionString();
	
	/**
	 * Returns an array with the inner state's keys. You can safely use that array, it won't be null,
	 * but may of course have the size 0 if there are no published inner states,
	 * @return String[] the keys of the published inner states
	 */
	public String[] getInnerStates();
	
	/**
	 * Returns the value of an inner state by it's key. The returned String can be null if there is no
	 * inner state with that key
	 * @param key the key
	 * @return String the value
	 */
	public String getInnerState(String key);
	
	/**
	 * Changes an inner state if, and only if, the module already created the state. You can not
	 * create a state with this method. If you call it with a key of an inexisting state, it will
	 * return false and do nothing
	 * @param key the state's key
	 * @param value the new value
	 * @return boolean true if there was a state that could be modified
	 */
	public boolean changeInnerState(String key, String value);
	
}
