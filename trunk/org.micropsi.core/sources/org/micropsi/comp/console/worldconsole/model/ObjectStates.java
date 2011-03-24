/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 30.03.2004
 *
 */
package org.micropsi.comp.console.worldconsole.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.xml.XMLElementHelper;
import org.micropsi.common.xml.XMLElementNotFoundException;
import org.micropsi.comp.messages.MTreeNode;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Matthias
 *
 */
public class ObjectStates {
	
	private Map<String,String> states = null;

	/**
	 * 
	 */
	public ObjectStates() {
		states = new HashMap<String,String>(3);
	}
	
	public ObjectStates(MTreeNode node) {
		this();
		if (node != null) {
			Iterator<MTreeNode> i = node.children();
			while (i.hasNext()) {
				MTreeNode state = i.next();
				addState(state.getName(), state.getValue());
			}
		}
	}
	
	public ObjectStates(Element config) throws MicropsiException {
		NodeList stateNodes = config.getElementsByTagName("state");
		states = new HashMap<String,String>(stateNodes.getLength());
		for (int i = 0; i < stateNodes.getLength(); i++) {
			Element stateElement = (Element) stateNodes.item(i);
			String stateName = null;
			try {
				stateName = XMLElementHelper.getElementValueByTagName(stateElement, "name");
			} catch (XMLElementNotFoundException e) {
				throw new MicropsiException(0, "Object state node must contain 'name' element", e);
			}
			String stateValue = null;
			try {
				stateValue = XMLElementHelper.getElementValueByTagName(stateElement, "value");
			} catch (XMLElementNotFoundException e1) {
				// state without value is ok.
			}
			addState(stateName, stateValue);
		}
	}
	
	public void addState(String key, String value) {
		states.put(key, value);
	}
	
	public String getState(String key) {
		return states.get(key);
	}
	
	public Map getStatesMap() {
		return states;
	}

	/**
	 * 
	 */
	public void clear() {
		states.clear();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof ObjectStates)) {
			return false;
		} else {
			return states.equals(((ObjectStates) obj).getStatesMap());
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return states.hashCode();
	}
	
	public boolean containsAll(ObjectStates otherStates) {
		if (otherStates == null) {
			return true;
		}
		if (states == null) {
			return false;
		}
		return states.entrySet().containsAll(otherStates.getStatesMap().entrySet());
	}

	/**
	 * @return
	 */
	public int getNumberOfStates() {
		return states.size();
	}
}
