/**
 * $Header: G:\DEV\Workspace\migration3.0\micropsi-cvs-repository\micropsi/org.micropsi.core/sources/org/micropsi/comp/world/objects/ObjectState.java,v 1.4 2005/07/12 12:55:16 vuine Exp $
 */
package org.micropsi.comp.world.objects;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.xml.XMLElementHelper;
import org.micropsi.common.xml.XMLElementNotFoundException;
import org.micropsi.comp.messages.MTreeNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *  $Header $
 *  @author matthias
 * 
 *  Encapsulates the state of a @see AbstractCommonObject.
 *
 */
public class ObjectState {
	private Map<String,String> states = null;
	
	/**
	 * Constructor ObjectState.
	 * @param element
	 */
	public ObjectState(Element element) throws MicropsiException {
		NodeList stateNodes = element.getElementsByTagName("state");
		for (int i = 0; i < stateNodes.getLength(); i++) {
			Element currentElement = (Element) stateNodes.item(i);
			String stateName;
			try {
				stateName =
					XMLElementHelper.getElementValueByTagName(
						currentElement,
						"name");
			} catch (XMLElementNotFoundException e) {
				throw new MicropsiException(10, "Error reading world setup file: object state with missing 'name' item.");
			}
			String stateValue;
			try {
				stateValue =
					XMLElementHelper.getElementValueByTagName(
						currentElement,
						"value");
			} catch (XMLElementNotFoundException e) {
				stateValue = null;
			}
			setState(stateName, stateValue);
		}
	}
	
	public ObjectState(){ 
		states = new HashMap<String,String>(3);
	}

	/**
	 * Method changeState.
	 * 
	 * Changes a state of the Object
	 * @param stateKey - The State.
	 * @param value - The value
	 * @throws MicropsiException if and only if you misspelled the attribute name and it
	 * doesn't exist.
	 */
	public void changeState(String stateKey, String value) throws MicropsiException{
		if(!states.containsKey(stateKey)) throw new 
			MicropsiException(23, "Tried to change nonexisting state in states HashMap");
		states.put(stateKey, value);
	}
	
	/**
	 * Method setState.
	 * 
	 * Adds a new state to the state map. If the state already exists,
	 * a new value will replace the old one. If the new value is null, the state will be removed.
	 * 
	 * @param stateKey -- The State
	 * @param value -- The new Value
	 */
	public void setState(String stateKey, String value){
		if (value != null) {
			if (states == null) {
				states = new HashMap<String,String>(3);
			}
			states.put(stateKey, value);
		} else {
			if (states != null) {
				states.remove(stateKey);
			}
		}
	}
	
	/**
	 * Method getState.
	 * 
	 * Gets the value for a specified state. Returns null if state doesn't exist.
	 * 
	 * @param stateKey -- The State
	 * @return String
	 * @throws MicropsiException
	 */
	public String getState(String stateKey) {
		if (states != null) {
			return states.get(stateKey);
		} else {
			return null;
		}
	}
	
	/**
	 * Method stateExists.
	 * 
	 * Returns true if the specified state exists.
	 * 
	 * @param stateKey  -- The State
	 * @return boolean
	 */
	public boolean stateExists(String stateKey) {
		return states != null && states.containsKey(stateKey);
	}

	public Element toXMLElement(Document doc) {
		Element element = doc.createElement("states");
		if (states != null) {
			Iterator it = states.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry state = (Entry) it.next();
				Element stateElement = doc.createElement("state");
				element.appendChild(stateElement);
				
				Element currentElement = doc.createElement("name");
				currentElement.appendChild(doc.createTextNode((String) state.getKey()));
				stateElement.appendChild(currentElement);
				
				currentElement = doc.createElement("value");
				currentElement.appendChild(doc.createTextNode((String) state.getValue()));
				stateElement.appendChild(currentElement);
			}
		}
		
		return element;
	}

	/**
	 * @return
	 */
	public MTreeNode toMTreeNode() {
		if (states == null || states.isEmpty()) {
			return null;
		}
		MTreeNode res = new MTreeNode("objectstates", "", null);
		Iterator it = states.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry state = (Entry) it.next();
			res.addChild((String) state.getKey(), (String) state.getValue());
		}
		return res;
	}

}