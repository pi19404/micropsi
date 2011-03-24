/*
 * $ Header $
 * 
 * Author: Matthias
 * Created on 28.07.2003
 *
 */
package org.micropsi.comp.world.objects;

import org.apache.log4j.Logger;
import org.micropsi.common.exception.MicropsiException;
import org.micropsi.common.xml.XMLElementHelper;
import org.micropsi.common.xml.XMLElementNotFoundException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Matthias
 *
 */
public class GroundType {

	private String name;
	private int index;
	
	private double moveEfficiency = 1;
	private boolean agentAllowed = true;

	public GroundType(Element configData, Logger logger) throws MicropsiException {
		try {
			name = XMLElementHelper.getElementValueByTagName(configData, "name");
			index = Integer.parseInt(XMLElementHelper.getElementValueByTagName(configData, "index"));
		} catch (NumberFormatException e) {
			throw new MicropsiException(10, "No numerical index in GroundType " + name, e);
		} catch (XMLElementNotFoundException e) {
			throw new MicropsiException(10, "Error reading config: Name or index missing in GroundType", e);
		}
		
		try {
			moveEfficiency = Double.parseDouble(XMLElementHelper.getElementValueByTagName(configData, "moveefficiency"));
		} catch (NumberFormatException e1) {
			logger.warn("While reading config: moveEfficiency is no double in GroundType. Using default value.");
		} catch (XMLElementNotFoundException e1) {
			// parameters are optional
		}
		try {
			agentAllowed = Boolean.valueOf(XMLElementHelper.getElementValueByTagName(configData, "agentallowed")).booleanValue();
		} catch (XMLElementNotFoundException e1) {
			// parameters are optional
		}
	}


	/**
	 * @return
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 */
	public double getMoveEfficiency() {
		return moveEfficiency;
	}

	/**
	 * @param moveEfficiency
	 */
	public void setMoveEfficiency(double moveEfficiency) {
		this.moveEfficiency = moveEfficiency;
	}

	/**
	 * @return
	 */
	public boolean isAgentAllowed() {
		return agentAllowed;
	}

	/**
	 * @param agentAllowed
	 */
	public void setAgentAllowed(boolean agentAllowed) {
		this.agentAllowed = agentAllowed;
	}


	/**
	 * @param doc
	 * @return
	 */
	public Node toXMLElement(Document doc) {
		Element element = doc.createElement("groundtype");
		Element currentElement;
		
		currentElement = doc.createElement("name");
		currentElement.appendChild(doc.createTextNode(name));
		element.appendChild(currentElement);
		
		currentElement = doc.createElement("index");
		currentElement.appendChild(doc.createTextNode(Integer.toString(index)));
		element.appendChild(currentElement);
		
		currentElement = doc.createElement("moveefficiency");
		currentElement.appendChild(doc.createTextNode(Double.toString(moveEfficiency)));
		element.appendChild(currentElement);
		
		currentElement = doc.createElement("agentallowed");
		currentElement.appendChild(doc.createTextNode(Boolean.toString(agentAllowed)));
		element.appendChild(currentElement);
		
		return element;
	}

}
