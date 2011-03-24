package org.micropsi.comp.agent.micropsi;

import java.util.HashMap;
import java.util.Iterator;

/**
 * The actor value cache can be used to keep actuator values.
 * 
 * ActorDataTargetIFs "push" the activation from the net somewhere. There is no
 * possiblilty to "pull" values from them, and hence there is no possibility to
 * get to know the values, if the ActorDataTargets do not support caching of
 * their values via an ActorDataCache.
 * 
 * ActorDataTargetIF implementations can put values into an ActorValueCache,
 * other parts of the agent implementation that are not updated by the ActoDataTargets
 * themselves can read the values from this cache. Typically, only QuestionTypes will
 * want to use the values from the ActorValueCache, as there is no guarantee that these
 * values will be present or up-to-date. 
 */
public class ActorValueCache {

	private HashMap<String,Double> cache = new HashMap<String,Double>(); 
	
	/**
	 * Constructs an ActorValueCache. Each agent should only have one.
	 */
	protected ActorValueCache() {
	}
	
	/**
	 * Reports a value to the cache. Typically, this should be called by ActorDataTargetIF
	 * implementations that want to "publish" their values for QuestionTypeIF implementations.
	 * @param key the key
	 * @param value the value
	 */
	public void reportValue(String key, double value) {
		cache.put(key,new Double(value));
	}
	
	/**
	 * Returns a value from the cache. Typically, this should be called by a QuestionTypeIF implementation
	 * that wants to report an actor value to some console.
	 * @param key the key
	 * @return the value stored with the given key or Double.NaN if there was no value for that key
	 */
	public double queryValue(String key) {
		if(!cache.containsKey(key)) {
			return Double.NaN;
		}
		return cache.get(key).doubleValue();
	}
	
	/**
	 * Returns an iterator with the keys for which values are present.
	 * @return an iterator
	 */
	public Iterator<String> getKeys() {
		return cache.keySet().iterator();
	}
	

}
